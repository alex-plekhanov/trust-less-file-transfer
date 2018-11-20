package client;

import dao.ParticipantDAO;
import dao.ParticipantDAOImpl;
import dao.TransferContractDAO;
import dao.TransferContractDAOImpl;
import java.io.File;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.Security;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import model.ContractState;
import model.Participant;
import model.TransferContract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import solidity.FileTransfer;
import solidity.FileTransferRegistry;
import ssl.SSLClientSocketFileReceiver;
import ssl.SSLServerSocketFileSender;
import util.CryptoUtil;

public class FileTransferClientImpl implements FileTransferClient {
    private static final Logger log = LoggerFactory.getLogger(FileTransferClientImpl.class);

    public static final ContractGasProvider GAS_PROVIDER = new StaticGasProvider(BigInteger.valueOf(1000),
        new BigInteger("3000000"));

    //private String rootContractAddress = null;
    private String rootContractAddress = "0xc2cea8865384353d21f3e9b21cc26e202968d3bd";

    private static AtomicInteger discoveryPortCounter = new AtomicInteger(8443);
    private int discoveryPort;

    private Web3j web3j;
    private Credentials credentials;
    private KeyPair keyPair;
    private FileTransferRegistry rootContract;
    private ParticipantDAO participantDAO;
    private TransferContractDAO transferContractDAO;
    private Thread notifyThread;

    private File inDir;
    private File outDir;
    private File uploadDir;
    private File downloadDir;
    private File tmpDir;

    private final Collection<Consumer<TransferContract>> incomingFileListeners = new CopyOnWriteArrayList<>();
    private final Collection<Consumer<TransferContract>> acceptedFileListeners = new CopyOnWriteArrayList<>();
    private final Collection<Consumer<TransferContract>> secretKeyListeners = new CopyOnWriteArrayList<>();

    @Override public void start(String accountFileName, String password, String rootContractAddress) throws Exception {
        this.rootContractAddress = rootContractAddress;
        start(accountFileName, password);
    }

    @Override public void start(String accountFileName, String password) throws Exception {
        Security.setProperty("crypto.policy", "unlimited");

        web3j = Web3j.build(new HttpService());

        log.info("Connected to Ethereum client version: " + web3j.web3ClientVersion().send().getWeb3ClientVersion());

        credentials = WalletUtils.loadCredentials(password, accountFileName);

        keyPair = CryptoUtil.decodeKeyPair(credentials.getEcKeyPair());

        log.info("Credentials loaded");

        initRootContract(rootContractAddress);

        participantDAO = new ParticipantDAOImpl(web3j, rootContract);
        transferContractDAO = new TransferContractDAOImpl(web3j, credentials, rootContract);

        File accountDir = new File("work", credentials.getAddress());
        ensureDir(accountDir);

        inDir = new File(accountDir, "in");
        ensureDir(inDir);

        outDir = new File(accountDir, "out");
        ensureDir(outDir);

        uploadDir = new File(accountDir, "upload");
        ensureDir(uploadDir);

        downloadDir = new File(accountDir, "download");
        ensureDir(downloadDir);

        tmpDir = new File(accountDir, "tmp");
        ensureDir(tmpDir);

        discoveryPort = discoveryPortCounter.getAndIncrement();

        SSLServerSocketFileSender senderServer = new SSLServerSocketFileSender(participantDAO.getAllParticipantsMap(),
            transferContractDAO.getMyTransferContractsMap(), keyPair, credentials.getAddress(), discoveryPort, uploadDir);

        senderServer.start();

        notifyThread = new Thread(() -> {
           try {
               for (Iterator<TransferContract> it = transferContractDAO.getPendingContractChages().iterator(); it.hasNext();) {
                   TransferContract contract = it.next();

                   if (contract.getState() == ContractState.INITIATED && credentials.getAddress().equals(contract.getReceiver()))
                       onFileContractInitiated(contract);
                   else if (contract.getState() == ContractState.CONFIRMED && credentials.getAddress().equals(contract.getSender()))
                       onFileContractComfirmed(contract);
                   else if (contract.getState() == ContractState.DECRYPTED && credentials.getAddress().equals(contract.getReceiver()))
                       onFileContractDecrypted(contract);

                   it.remove();
               }
           }
           catch (Exception ex) {
               if (ex instanceof InterruptedException)
                   throw ex;

               log.error("Failed to process pending changes", ex);
           }
        });

        notifyThread.start();
    }

    private void onFileContractInitiated(TransferContract contract) {
        // Notify listeners.
        incomingFileListeners.forEach(l -> l.accept(contract));
    }

    private void onFileContractComfirmed(TransferContract contract) {
        // Send secret key
        try {
            publishSecretKey(contract);
        }
        catch (Exception ex) {
            log.error("Failed to publish secret key", ex);
        }

        // Notify listeners.
        acceptedFileListeners.forEach(l -> l.accept(contract));
    }

    private void onFileContractDecrypted(TransferContract contract) {
        // Decrypt file
        try {
            decryptAndCompleteFileTransfer(contract);
        }
        catch (Exception ex) {
            log.error("Failed to decrypt file", ex);
        }

        // Notify listeners.
        secretKeyListeners.forEach(l -> l.accept(contract));
    }

    private void ensureDir(File dir) {
        if (!dir.exists())
            dir.mkdirs();
    }

    private void initRootContract(String contractAddress) throws Exception {
        if (contractAddress == null) {
            log.info("Deploying smart contract");

            rootContract = FileTransferRegistry.deploy(
                web3j, credentials, GAS_PROVIDER).send();

            log.info("Smart contract deployed to address " + rootContract.getContractAddress());
        }
        else {
            rootContract = FileTransferRegistry.load(contractAddress, web3j, credentials,
                GAS_PROVIDER);

            log.info("Smart contract loaded " + contractAddress);
        }
    }

    @Override public void registerParticipant(String name) throws Exception {
        //PublicKey publicKey = CryptoUtil.decodeKeyPair(credentials.getEcKeyPair()).getPublic();
        TransactionReceipt receipt = rootContract.registerMe(credentials.getEcKeyPair().getPublicKey().toString(), name)
            .send();

        log.info("Participant registered " + credentials.getAddress());

        log.debug("Receipt " + receipt);
    }

    @Override public Collection<Participant> getAllParticipants() {
        return participantDAO.getAllParticipants();
    }

    @Override public Participant getParticipant(String address) {
        return participantDAO.getParticipant(address);
    }

    @Override public void initFileTransfer(File file, String receiver) throws Exception {
        // Copy file to outbound dir
        File fileOutDir = new File(outDir, (!receiver.startsWith("0x") ? "0x" : "") + receiver);
        ensureDir(fileOutDir);
        File outFile = new File(fileOutDir, file.getName());
        Files.copy(file.toPath(), outFile.toPath());

        // Crypt file to temp dir
        File cryptedFile = new File(tmpDir, file.getName());
        byte[] secretKey = CryptoUtil.calcFileHash(outFile);
        CryptoUtil.cryptFile(outFile, cryptedFile, secretKey);

        // Sign file
        byte[] signature = CryptoUtil.signFile(cryptedFile, keyPair);

        // Register file in ethereum
        TransactionReceipt receipt = rootContract.initiateFileTransfer(
            receiver,
            file.getName(),
            CryptoUtil.bytesToString(signature),
            InetAddress.getLocalHost().getHostAddress(),
            BigInteger.valueOf(discoveryPort)
        ).send();

        // Move file to work/outbound/{hash}
        List<FileTransferRegistry.FileTransferInitiatedEventResponse> events = rootContract.getFileTransferInitiatedEvents(receipt);

        if (events.isEmpty())
            throw new Exception("Events not generated");

        String fileTransferContract = events.get(0).fileTransferContract;

        File uploadFile = new File(uploadDir, fileTransferContract);
        Files.move(cryptedFile.toPath(), uploadFile.toPath());

        log.info("New file transfer contract created " + fileTransferContract + " file stored to " + uploadFile.getAbsolutePath());

        log.debug("Receipt " + receipt);
    }

    @Override public void acceptFile(TransferContract contract) throws Exception {
        // TODO: check host and port in discovery service too
        Participant participant = participantDAO.getParticipant(contract.getSender());

        SSLClientSocketFileReceiver clientReceiver = new SSLClientSocketFileReceiver(keyPair, credentials.getAddress(),
            contract.getSender(), participant.getPublicKey(), contract.getDiscoveryHost(), contract.getDiscoveryPort());

        File cryptedFileReceived = clientReceiver.downloadFile(contract.getContractAddress(), downloadDir);

        // Check sender sign
        Participant sender = participantDAO.getParticipant(contract.getSender());
        if (!CryptoUtil.checkSignFile(cryptedFileReceived, contract.getSenderSign(), sender.getPublicKey())) {
            cancelFileTransfer(contract, "Wrong sender sign");

            throw new Exception("Wrong sender sign");
        }

        // Sign file
        byte[] signature = CryptoUtil.signFile(cryptedFileReceived, keyPair);

        // Accept file contract in ethereum
        FileTransfer transferContract = FileTransfer.load(contract.getContractAddress(), web3j, credentials, GAS_PROVIDER);
        TransactionReceipt receipt = transferContract.confirmCryptedFileReceived(CryptoUtil.bytesToString(signature)).send();

        log.info("Confirmed contract " + contract.getContractAddress());

        log.debug("Receipt " + receipt);
    }

    private void publishSecretKey(TransferContract contract) throws Exception {
        // Calc file hash from outbound dir
        File fileOutDir = new File(outDir, contract.getReceiver());
        File outFile = new File(fileOutDir, contract.getFileName());

        if (!outFile.exists())
            throw new Exception("File not found " + outFile.getAbsolutePath());

        // Check crypted file sign
        File cryptedFile = new File(uploadDir, contract.getContractAddress());
        Participant receiver = participantDAO.getParticipant(contract.getReceiver());
        if (!CryptoUtil.checkSignFile(cryptedFile, contract.getReceiverSign(), receiver.getPublicKey())) {
            cancelFileTransfer(contract, "Wrong receiver sign");

            throw new Exception("Wrong receiver sign");
        }

        // Publish secret key to ethereum
        byte[] secretKey = CryptoUtil.calcFileHash(outFile);

        // Send secret key to ethereum
        FileTransfer transferContract = FileTransfer.load(contract.getContractAddress(), web3j, credentials, GAS_PROVIDER);
        TransactionReceipt receipt = transferContract.publishSecretKey(CryptoUtil.bytesToString(secretKey)).send();

        log.info("Secret key for contract " + contract.getContractAddress() + " published");

        log.debug("Receipt " + receipt);
    }

    private void decryptAndCompleteFileTransfer(TransferContract contract) throws Exception {
        // Decrypt file
        File cryptedFile = new File(downloadDir, contract.getContractAddress());
        if (!cryptedFile.exists())
            throw new Exception("File not found " + cryptedFile.getAbsolutePath());

        File fileInDir = new File(inDir, contract.getSender());
        File inFile = new File(fileInDir, contract.getFileName());

        // Check file hash
        CryptoUtil.decryptFile(cryptedFile, inFile, contract.getSecretKey());
        byte[] hash = CryptoUtil.calcFileHash(inFile);
        boolean success = Arrays.equals(hash, contract.getSecretKey());

        // Store state to ethereum
        FileTransfer transferContract = FileTransfer.load(contract.getContractAddress(), web3j, credentials, GAS_PROVIDER);
        TransactionReceipt receipt = transferContract.complete(success, success ? "" : "Wrong file hash").send();

        if (!success)
            inFile.delete();

        log.info("Contract completed " + contract.getContractAddress() + (success ? " succesfully" : " unsuccessfully"));

        log.debug("Receipt " + receipt);
    }

    @Override public void validateContract(TransferContract contract, File file) throws Exception {
        // Encrypt file with secret key
        File cryptedFile = new File(tmpDir, file.getName());
        CryptoUtil.cryptFile(file, cryptedFile, contract.getSecretKey());

        try {
            // Check sender sign
            Participant sender = participantDAO.getParticipant(contract.getSender());
            if (!CryptoUtil.checkSignFile(cryptedFile, contract.getSenderSign(), sender.getPublicKey()))
                throw new Exception("Wrong sender sign");

            // Check receiver sign
            Participant receiver = participantDAO.getParticipant(contract.getReceiver());
            if (!CryptoUtil.checkSignFile(cryptedFile, contract.getReceiverSign(), receiver.getPublicKey()))
                throw new Exception("Wrong receiver sign");
        }
        finally {
            cryptedFile.delete();
        }
    }

    @Override public void cancelFileTransfer(TransferContract contract, String reason) throws Exception {
        FileTransfer transferContract = FileTransfer.load(contract.getContractAddress(), web3j, credentials, GAS_PROVIDER);
        TransactionReceipt receipt = transferContract.cancel(reason).send();

        log.info("Contract " + contract.getContractAddress() + " canceled, reason: " + reason);

        log.debug("Receipt " + receipt);
    }

    @Override public void addIncomingFileTransferListener(Consumer<TransferContract> listener) {
        incomingFileListeners.add(listener);
    }

    @Override public void addAcceptedFileListener(Consumer<TransferContract> listener) {
        acceptedFileListeners.add(listener);
    }

    @Override public void addSecretKeyPublishedListener(Consumer<TransferContract> listener) {
        secretKeyListeners.add(listener);
    }

    @Override public void stop() {
        web3j.shutdown();
    }

    public ParticipantDAO getParticipantDAO() {
        return participantDAO;
    }

    public TransferContractDAO getTransferContractDAO() {
        return transferContractDAO;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    @Override public Collection<TransferContract> getFileTransferContracts(String sender, String receiver) {
        return transferContractDAO.getAllTransferContracts(sender, receiver);
    }

    @Override public Collection<TransferContract> getMyFileTransferContracts() {
        return transferContractDAO.getMyTransferContracts();
    }

    @Override public TransferContract getFileTransferContract(String address) {
        return transferContractDAO.getTransferContract(address);
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }
}
