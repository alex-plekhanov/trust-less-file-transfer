package tlft.dao;

import tlft.client.FileTransferClientImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import tlft.model.ContractState;
import tlft.model.TransferContract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import rx.Subscription;
import solidity.FileTransfer;
import solidity.FileTransferRegistry;
import tlft.util.CryptoUtil;

public class TransferContractDAOImpl implements TransferContractDAO {
    private final Logger log = LoggerFactory.getLogger(TransferContractDAOImpl.class);

    private final Web3j web3j;
    private final FileTransferRegistry rootContract;
    private final Credentials credentials;

    private final Map<String, TransferContract> myContracts = new ConcurrentHashMap<>();
    private final Map<String, Collection<Subscription>> contractsSubscriptions = new ConcurrentHashMap<>();

    private final Map<String, TransferContract> pendingContractChanges = new ConcurrentHashMap<>();

    private String encodeAddress(String address) {
        return address == null ? address : "0x" + TypeEncoder.encode(new Address(address));
    }

    public TransferContractDAOImpl(Web3j web3j, Credentials credentials, FileTransferRegistry rootContract) {
        this.web3j = web3j;
        this.rootContract = rootContract;
        this.credentials = credentials;

        EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST,
            rootContract.getContractAddress())
            .addSingleTopic(EventEncoder.encode(FileTransferRegistry.FILETRANSFERINITIATED_EVENT))
            .addOptionalTopics(encodeAddress(credentials.getAddress()), null)
            .addOptionalTopics(null, encodeAddress(credentials.getAddress()))
            ;

        rootContract
            .fileTransferInitiatedEventObservable(filter)
            .subscribe(
                event -> {
                    myContracts.put(event.fileTransferContract, getTransferContract(
                        event.fileTransferContract, event.sender, event.receiver, true));

                    log.debug("File transfer contract added " + event.fileTransferContract);
                }
            );
    }

    /** {@inheritDoc} */
    @Override public Collection<TransferContract> getAllTransferContracts(String sender, String receiver) {
        Collection<TransferContract> contracts = new ArrayList<>();

        EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST,
            rootContract.getContractAddress())
            .addSingleTopic(EventEncoder.encode(FileTransferRegistry.FILETRANSFERINITIATED_EVENT))
            .addOptionalTopics(encodeAddress(sender), encodeAddress(receiver));

        Subscription subscription = rootContract
            .fileTransferInitiatedEventObservable(filter)
            .subscribe(
                event -> {
                    contracts.add(getTransferContract(event.fileTransferContract));

                    log.debug("File transfer contract received " + event.fileTransferContract);
                }
            );

        subscription.unsubscribe();

        return contracts;
    }

    /** {@inheritDoc} */
    @Override public Map<String, TransferContract> getMyTransferContractsMap() {
        return myContracts;
    }

    /** {@inheritDoc} */
    @Override public Collection<TransferContract> getMyTransferContracts() {
        return Collections.unmodifiableCollection(myContracts.values());
    }

    private TransferContract getTransferContract(String address, String sender, String receiver, boolean subscribeToUpdates) {
        TransferContract contract = subscribeToUpdates ?
            myContracts.computeIfAbsent(address, val -> new TransferContract(address, sender, receiver))
            : new TransferContract(address);

        // TODO Store completed contracts to local file storage and don't subscribe to updates for them.

        FileTransfer transfer = FileTransfer.load(address, web3j, credentials, FileTransferClientImpl.GAS_PROVIDER);

        Collection<Subscription> subscriptions = new LinkedList<>();

        if (subscribeToUpdates)
            contractsSubscriptions.put(address, subscriptions);

        EthFilter filter;

        filter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, address)
            .addSingleTopic(EventEncoder.encode(FileTransfer.TRANSFERINITIATED_EVENT));

        subscriptions.add(transfer
            .transferInitiatedEventObservable(filter)
            .subscribe(
                event -> {
                    contract.setSender(event.fileSender);
                    contract.setReceiver(event.fileReceiver);
                    contract.setSenderSign(CryptoUtil.stringToBytes(event.cryptedFileSenderSign));
                    contract.setDiscoveryHost(event.discoveryHost);
                    contract.setDiscoveryPort(event.discoveryPort.intValue());
                    contract.setFileName(event.fileName);
                    contract.setState(ContractState.INITIATED);

                    if (subscribeToUpdates)
                        onContractInitiated(contract);
                }
            )
        );

        filter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, address)
            .addSingleTopic(EventEncoder.encode(FileTransfer.TRANSFERCONFIRMED_EVENT));

        subscriptions.add(transfer
            .transferConfirmedEventObservable(filter)
            .subscribe(
                event -> {
                    contract.setReceiverSign(CryptoUtil.stringToBytes(event.cryptedFileReceiverSign));
                    contract.setState(ContractState.CONFIRMED);

                    if (subscribeToUpdates)
                        onContractConfirmed(contract);
                }
            )
        );

        filter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, address)
            .addSingleTopic(EventEncoder.encode(FileTransfer.TRANSFERCANCELED_EVENT));

        subscriptions.add(transfer
            .transferCanceledEventObservable(filter)
            .subscribe(
                event -> {
                    contract.setReason(event.reason);
                    contract.setState(ContractState.CANCELED);

                    if (subscribeToUpdates)
                        onContractCanceled(contract);
                }
            )
        );

        filter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, address)
            .addSingleTopic(EventEncoder.encode(FileTransfer.TRANSFERDECRYPTED_EVENT));

        subscriptions.add(transfer
            .transferDecryptedEventObservable(filter)
            .subscribe(
                event -> {
                    contract.setSecretKey(CryptoUtil.stringToBytes(event.secretKey));
                    contract.setState(ContractState.DECRYPTED);

                    if (subscribeToUpdates)
                        onContractDecrypted(contract);
                }
            )
        );

        filter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, address)
            .addSingleTopic(EventEncoder.encode(FileTransfer.TRANSFERCOMPLETED_EVENT));

        subscriptions.add(transfer
            .transferCompletedEventObservable(filter)
            .subscribe(
                event -> {
                    contract.setReason(event.reason);
                    contract.setState(event.result ? ContractState.COMPLETED : ContractState.DISPUTE);

                    if (subscribeToUpdates)
                        onContractCompleted(contract);
                }
            )
        );

        if (!subscribeToUpdates)
            unsubscribeFromSubscriptions(subscriptions);

        return contract;
    }

    private void onContractInitiated(TransferContract contract) {
        if (credentials.getAddress().equals(contract.getReceiver()))
            pendingContractChanges.put(contract.getContractAddress(), contract);

        log.debug("File transfer inited for contract " + contract.getContractAddress());
    }

    private void onContractConfirmed(TransferContract contract) {
        if (credentials.getAddress().equals(contract.getSender()))
            pendingContractChanges.put(contract.getContractAddress(), contract);
        else
            pendingContractChanges.remove(contract.getContractAddress());

        log.debug("File transfer confirmed for contract " + contract.getContractAddress());
    }

    private void onContractDecrypted(TransferContract contract) {
        if (credentials.getAddress().equals(contract.getReceiver()))
            pendingContractChanges.put(contract.getContractAddress(), contract);
        else
            pendingContractChanges.remove(contract.getContractAddress());

        unsubscribeContract(contract.getContractAddress());

        log.debug("File transfer decrypted for contract " + contract.getContractAddress());
    }

    private void onContractCompleted(TransferContract contract) {
        pendingContractChanges.remove(contract.getContractAddress());

        unsubscribeContract(contract.getContractAddress());

        log.debug("File transfer completed for contract " + contract.getContractAddress() + " state: "
            + contract.getState() + " reason: " + contract.getReason());
    }

    private void onContractCanceled(TransferContract contract) {
        pendingContractChanges.remove(contract.getContractAddress());

        unsubscribeContract(contract.getContractAddress());

        log.debug("File transfer canceled for contract " + contract.getContractAddress() + " reason: " + contract.getReason());
    }

    private void unsubscribeContract(String address) {
        Collection<Subscription> subscriptions = contractsSubscriptions.remove(address);

        unsubscribeFromSubscriptions(subscriptions);
    }

    private void unsubscribeFromSubscriptions(Collection<Subscription> subscriptions) {
        if (subscriptions != null)
            for (Subscription subscription : subscriptions)
                subscription.unsubscribe();
    }

    /** {@inheritDoc} */
    @Override public TransferContract getTransferContract(String address) {
        TransferContract contract = myContracts.get(address);

        if (contract != null)
            return contract;

        return getTransferContract(address, null, null, false);
    }

    /** {@inheritDoc} */
    @Override public Collection<TransferContract> getPendingContractChanges() {
        // Modifiable
        return pendingContractChanges.values();
    }
}
