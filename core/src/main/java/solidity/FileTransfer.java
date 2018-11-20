package solidity;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint16;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.6.0.
 */
public class FileTransfer extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50604051610c27380380610c278339810160409081528151602080840151928401516060850151608086015160a08701516000805433600160a060020a031991821617909155600180548216600160a060020a03808a16919091179091556002805490921690891617905591870180519597938401959094919093019261009d9160039190860190610268565b50600680546000919060ff191660018302179055507f0781b5da7b6aab5340f7321c153a7c81f327521d53b14c160177366b2497573d8686868686866040518087600160a060020a0316600160a060020a0316815260200186600160a060020a0316600160a060020a031681526020018060200180602001806020018561ffff1661ffff168152602001848103845288818151815260200191508051906020019080838360005b8381101561015c578181015183820152602001610144565b50505050905090810190601f1680156101895780820380516001836020036101000a031916815260200191505b50848103835287518152875160209182019189019080838360005b838110156101bc5781810151838201526020016101a4565b50505050905090810190601f1680156101e95780820380516001836020036101000a031916815260200191505b50848103825286518152865160209182019188019080838360005b8381101561021c578181015183820152602001610204565b50505050905090810190601f1680156102495780820380516001836020036101000a031916815260200191505b50995050505050505050505060405180910390a1505050505050610303565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106102a957805160ff19168380011785556102d6565b828001600101855582156102d6579182015b828111156102d65782518255916020019190600101906102bb565b506102e29291506102e6565b5090565b61030091905b808211156102e257600081556001016102ec565b90565b610915806103126000396000f3006080604052600436106100615763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416630b4f3f3d811461006657806323475ce6146100c15780638ffaa3d914610121578063b27d9b931461017a575b600080fd5b34801561007257600080fd5b506040805160206004803580820135601f81018490048402850184019095528484526100bf9436949293602493928401919081908401838280828437509497506101d39650505050505050565b005b3480156100cd57600080fd5b5060408051602060046024803582810135601f81018590048502860185019096528585526100bf95833515159536956044949193909101919081908401838280828437509497506103a79650505050505050565b34801561012d57600080fd5b506040805160206004803580820135601f81018490048402850184019095528484526100bf94369492936024939284019190819084018382808284375094975061055d9650505050505050565b34801561018657600080fd5b506040805160206004803580820135601f81018490048402850184019095528484526100bf9436949293602493928401919081908401838280828437509497506106c59650505050505050565b60015473ffffffffffffffffffffffffffffffffffffffff16331480610210575060025473ffffffffffffffffffffffffffffffffffffffff1633145b151561028c576040805160e560020a62461bcd02815260206004820152602e60248201527f4f6e6c792073656e646572206f722072656365697665722063616e2063616c6c60448201527f20746869732066756e6374696f6e000000000000000000000000000000000000606482015290519081900360840190fd5b600060065460ff16600481111561029f57fe5b14806102bb5750600160065460ff1660048111156102b957fe5b145b15156102ff576040805160e560020a62461bcd02815260206004820152601460248201526000805160206108ca833981519152604482015290519081900360640190fd5b6006805460ff1916600417905560408051602080825283518183015283517f7b5db6ce4197cbe33070c05dc81626ae3252d763b359e99d60ea1a5e34540a03938593928392918301919085019080838360005b8381101561036a578181015183820152602001610352565b50505050905090810190601f1680156103975780820380516001836020036101000a031916815260200191505b509250505060405180910390a150565b60025473ffffffffffffffffffffffffffffffffffffffff16331461043b576040805160e560020a62461bcd028152602060048201526024808201527f4f6e6c792072656365697665722063616e2063616c6c20746869732066756e6360448201527f74696f6e00000000000000000000000000000000000000000000000000000000606482015290519081900360840190fd5b600260065460ff16600481111561044e57fe5b14610491576040805160e560020a62461bcd02815260206004820152601460248201526000805160206108ca833981519152604482015290519081900360640190fd5b60068054600360ff199091161761ff0019166101008415159081029190911790915560408051918252602080830182815284519284019290925283517f7981b972a336958617d227a0bcae18a0132b9e3103e0045fd66a57c2aa49691a93869386939192909160608401919085019080838360005b8381101561051e578181015183820152602001610506565b50505050905090810190601f16801561054b5780820380516001836020036101000a031916815260200191505b50935050505060405180910390a15050565b60025473ffffffffffffffffffffffffffffffffffffffff1633146105f1576040805160e560020a62461bcd028152602060048201526024808201527f4f6e6c792072656365697665722063616e2063616c6c20746869732066756e6360448201527f74696f6e00000000000000000000000000000000000000000000000000000000606482015290519081900360840190fd5b600060065460ff16600481111561060457fe5b14610647576040805160e560020a62461bcd02815260206004820152601460248201526000805160206108ca833981519152604482015290519081900360640190fd5b805161065a90600490602084019061082e565b506006805460ff1916600117905560408051602080825283518183015283517f6ab01f1d42a4c582d9365a35b24db683b6f0e690f698edb9f68fbc2c1562c388938593928392918301919085019080838360008381101561036a578181015183820152602001610352565b60015473ffffffffffffffffffffffffffffffffffffffff16331461075a576040805160e560020a62461bcd02815260206004820152602260248201527f4f6e6c792073656e6465722063616e2063616c6c20746869732066756e63746960448201527f6f6e000000000000000000000000000000000000000000000000000000000000606482015290519081900360840190fd5b600160065460ff16600481111561076d57fe5b146107b0576040805160e560020a62461bcd02815260206004820152601460248201526000805160206108ca833981519152604482015290519081900360640190fd5b80516107c390600590602084019061082e565b506006805460ff1916600217905560408051602080825283518183015283517f6f755c00d90307ec0350a17e9a9d9fbdc58a866f4fcb565c92baa8489766925c938593928392918301919085019080838360008381101561036a578181015183820152602001610352565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061086f57805160ff191683800117855561089c565b8280016001018555821561089c579182015b8281111561089c578251825591602001919060010190610881565b506108a89291506108ac565b5090565b6108c691905b808211156108a857600081556001016108b2565b90560057726f6e6720636f6e7472616374207374617465000000000000000000000000a165627a7a72305820cd5a16252bdeec55c2f5655f397b1bd451b8e85a32dc9accb39849ec275c34530029";

    public static final String FUNC_CANCEL = "cancel";

    public static final String FUNC_COMPLETE = "complete";

    public static final String FUNC_CONFIRMCRYPTEDFILERECEIVED = "confirmCryptedFileReceived";

    public static final String FUNC_PUBLISHSECRETKEY = "publishSecretKey";

    public static final Event TRANSFERINITIATED_EVENT = new Event("TransferInitiated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint16>() {}));
    ;

    public static final Event TRANSFERCONFIRMED_EVENT = new Event("TransferConfirmed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
    ;

    public static final Event TRANSFERDECRYPTED_EVENT = new Event("TransferDecrypted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
    ;

    public static final Event TRANSFERCANCELED_EVENT = new Event("TransferCanceled", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
    ;

    public static final Event TRANSFERCOMPLETED_EVENT = new Event("TransferCompleted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}, new TypeReference<Utf8String>() {}));
    ;

    @Deprecated
    protected FileTransfer(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected FileTransfer(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected FileTransfer(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected FileTransfer(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> cancel(String _reason) {
        final Function function = new Function(
                FUNC_CANCEL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_reason)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> complete(Boolean _result, String _reason) {
        final Function function = new Function(
                FUNC_COMPLETE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Bool(_result), 
                new org.web3j.abi.datatypes.Utf8String(_reason)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> confirmCryptedFileReceived(String _cryptedFileReceiverSign) {
        final Function function = new Function(
                FUNC_CONFIRMCRYPTEDFILERECEIVED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_cryptedFileReceiverSign)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> publishSecretKey(String _secretKey) {
        final Function function = new Function(
                FUNC_PUBLISHSECRETKEY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_secretKey)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<FileTransfer> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String _fileSender, String _fileReceiver, String _fileName, String _cryptedFileSenderSign, String _discoveryHost, BigInteger _discoveryPort) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_fileSender), 
                new org.web3j.abi.datatypes.Address(_fileReceiver), 
                new org.web3j.abi.datatypes.Utf8String(_fileName), 
                new org.web3j.abi.datatypes.Utf8String(_cryptedFileSenderSign), 
                new org.web3j.abi.datatypes.Utf8String(_discoveryHost), 
                new org.web3j.abi.datatypes.generated.Uint16(_discoveryPort)));
        return deployRemoteCall(FileTransfer.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<FileTransfer> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String _fileSender, String _fileReceiver, String _fileName, String _cryptedFileSenderSign, String _discoveryHost, BigInteger _discoveryPort) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_fileSender), 
                new org.web3j.abi.datatypes.Address(_fileReceiver), 
                new org.web3j.abi.datatypes.Utf8String(_fileName), 
                new org.web3j.abi.datatypes.Utf8String(_cryptedFileSenderSign), 
                new org.web3j.abi.datatypes.Utf8String(_discoveryHost), 
                new org.web3j.abi.datatypes.generated.Uint16(_discoveryPort)));
        return deployRemoteCall(FileTransfer.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<FileTransfer> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _fileSender, String _fileReceiver, String _fileName, String _cryptedFileSenderSign, String _discoveryHost, BigInteger _discoveryPort) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_fileSender), 
                new org.web3j.abi.datatypes.Address(_fileReceiver), 
                new org.web3j.abi.datatypes.Utf8String(_fileName), 
                new org.web3j.abi.datatypes.Utf8String(_cryptedFileSenderSign), 
                new org.web3j.abi.datatypes.Utf8String(_discoveryHost), 
                new org.web3j.abi.datatypes.generated.Uint16(_discoveryPort)));
        return deployRemoteCall(FileTransfer.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<FileTransfer> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _fileSender, String _fileReceiver, String _fileName, String _cryptedFileSenderSign, String _discoveryHost, BigInteger _discoveryPort) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_fileSender), 
                new org.web3j.abi.datatypes.Address(_fileReceiver), 
                new org.web3j.abi.datatypes.Utf8String(_fileName), 
                new org.web3j.abi.datatypes.Utf8String(_cryptedFileSenderSign), 
                new org.web3j.abi.datatypes.Utf8String(_discoveryHost), 
                new org.web3j.abi.datatypes.generated.Uint16(_discoveryPort)));
        return deployRemoteCall(FileTransfer.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public List<TransferInitiatedEventResponse> getTransferInitiatedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFERINITIATED_EVENT, transactionReceipt);
        ArrayList<TransferInitiatedEventResponse> responses = new ArrayList<TransferInitiatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferInitiatedEventResponse typedResponse = new TransferInitiatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.fileSender = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.fileReceiver = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.fileName = (String) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.cryptedFileSenderSign = (String) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.discoveryHost = (String) eventValues.getNonIndexedValues().get(4).getValue();
            typedResponse.discoveryPort = (BigInteger) eventValues.getNonIndexedValues().get(5).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TransferInitiatedEventResponse> transferInitiatedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferInitiatedEventResponse>() {
            @Override
            public TransferInitiatedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFERINITIATED_EVENT, log);
                TransferInitiatedEventResponse typedResponse = new TransferInitiatedEventResponse();
                typedResponse.log = log;
                typedResponse.fileSender = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.fileReceiver = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.fileName = (String) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.cryptedFileSenderSign = (String) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.discoveryHost = (String) eventValues.getNonIndexedValues().get(4).getValue();
                typedResponse.discoveryPort = (BigInteger) eventValues.getNonIndexedValues().get(5).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<TransferInitiatedEventResponse> transferInitiatedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFERINITIATED_EVENT));
        return transferInitiatedEventObservable(filter);
    }

    public List<TransferConfirmedEventResponse> getTransferConfirmedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFERCONFIRMED_EVENT, transactionReceipt);
        ArrayList<TransferConfirmedEventResponse> responses = new ArrayList<TransferConfirmedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferConfirmedEventResponse typedResponse = new TransferConfirmedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.cryptedFileReceiverSign = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TransferConfirmedEventResponse> transferConfirmedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferConfirmedEventResponse>() {
            @Override
            public TransferConfirmedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFERCONFIRMED_EVENT, log);
                TransferConfirmedEventResponse typedResponse = new TransferConfirmedEventResponse();
                typedResponse.log = log;
                typedResponse.cryptedFileReceiverSign = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<TransferConfirmedEventResponse> transferConfirmedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFERCONFIRMED_EVENT));
        return transferConfirmedEventObservable(filter);
    }

    public List<TransferDecryptedEventResponse> getTransferDecryptedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFERDECRYPTED_EVENT, transactionReceipt);
        ArrayList<TransferDecryptedEventResponse> responses = new ArrayList<TransferDecryptedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferDecryptedEventResponse typedResponse = new TransferDecryptedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.secretKey = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TransferDecryptedEventResponse> transferDecryptedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferDecryptedEventResponse>() {
            @Override
            public TransferDecryptedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFERDECRYPTED_EVENT, log);
                TransferDecryptedEventResponse typedResponse = new TransferDecryptedEventResponse();
                typedResponse.log = log;
                typedResponse.secretKey = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<TransferDecryptedEventResponse> transferDecryptedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFERDECRYPTED_EVENT));
        return transferDecryptedEventObservable(filter);
    }

    public List<TransferCanceledEventResponse> getTransferCanceledEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFERCANCELED_EVENT, transactionReceipt);
        ArrayList<TransferCanceledEventResponse> responses = new ArrayList<TransferCanceledEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferCanceledEventResponse typedResponse = new TransferCanceledEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.reason = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TransferCanceledEventResponse> transferCanceledEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferCanceledEventResponse>() {
            @Override
            public TransferCanceledEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFERCANCELED_EVENT, log);
                TransferCanceledEventResponse typedResponse = new TransferCanceledEventResponse();
                typedResponse.log = log;
                typedResponse.reason = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<TransferCanceledEventResponse> transferCanceledEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFERCANCELED_EVENT));
        return transferCanceledEventObservable(filter);
    }

    public List<TransferCompletedEventResponse> getTransferCompletedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFERCOMPLETED_EVENT, transactionReceipt);
        ArrayList<TransferCompletedEventResponse> responses = new ArrayList<TransferCompletedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferCompletedEventResponse typedResponse = new TransferCompletedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.result = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.reason = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TransferCompletedEventResponse> transferCompletedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferCompletedEventResponse>() {
            @Override
            public TransferCompletedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFERCOMPLETED_EVENT, log);
                TransferCompletedEventResponse typedResponse = new TransferCompletedEventResponse();
                typedResponse.log = log;
                typedResponse.result = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.reason = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<TransferCompletedEventResponse> transferCompletedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFERCOMPLETED_EVENT));
        return transferCompletedEventObservable(filter);
    }

    @Deprecated
    public static FileTransfer load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new FileTransfer(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static FileTransfer load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new FileTransfer(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static FileTransfer load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new FileTransfer(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static FileTransfer load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new FileTransfer(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class TransferInitiatedEventResponse {
        public Log log;

        public String fileSender;

        public String fileReceiver;

        public String fileName;

        public String cryptedFileSenderSign;

        public String discoveryHost;

        public BigInteger discoveryPort;
    }

    public static class TransferConfirmedEventResponse {
        public Log log;

        public String cryptedFileReceiverSign;
    }

    public static class TransferDecryptedEventResponse {
        public Log log;

        public String secretKey;
    }

    public static class TransferCanceledEventResponse {
        public Log log;

        public String reason;
    }

    public static class TransferCompletedEventResponse {
        public Log log;

        public Boolean result;

        public String reason;
    }
}
