package solidity;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
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
public class FileTransferRegistry extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b5061141f806100206000396000f30060806040526004361061004b5763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416630a03b7a88114610050578063f4cb91b51461013b575b600080fd5b34801561005c57600080fd5b5060408051602060046024803582810135601f8101859004850286018501909652858552610139958335600160a060020a031695369560449491939091019190819084018382808284375050604080516020601f89358b018035918201839004830284018301909452808352979a99988101979196509182019450925082915084018382808284375050604080516020601f89358b018035918201839004830284018301909452808352979a9998810197919650918201945092508291508401838280828437509497505050923561ffff1693506101d292505050565b005b34801561014757600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261013994369492936024939284019190819084018382808284375050604080516020601f89358b018035918201839004830284018301909452808352979a9998810197919650918201945092508291508401838280828437509497506105189650505050505050565b33600090815260208190526040812060019081015460029181161561010002600019011604151561028a57604080517f08c379a000000000000000000000000000000000000000000000000000000000815260206004820152602960248201527f46696c65207472616e7366657220696e69746961746f72206973206e6f74207260448201527f6567697374657265640000000000000000000000000000000000000000000000606482015290519081900360840190fd5b600160a060020a038616600090815260208190526040902060019081015460029181161561010002600019011604151561032557604080517f08c379a000000000000000000000000000000000000000000000000000000000815260206004820152601f60248201527f46696c65207265636569766572206973206e6f74207265676973746572656400604482015290519081900360640190fd5b338686868686610333610721565b8087600160a060020a0316600160a060020a0316815260200186600160a060020a0316600160a060020a031681526020018060200180602001806020018561ffff1661ffff168152602001848103845288818151815260200191508051906020019080838360005b838110156103b357818101518382015260200161039b565b50505050905090810190601f1680156103e05780820380516001836020036101000a031916815260200191505b50848103835287518152875160209182019189019080838360005b838110156104135781810151838201526020016103fb565b50505050905090810190601f1680156104405780820380516001836020036101000a031916815260200191505b50848103825286518152865160209182019188019080838360005b8381101561047357818101518382015260200161045b565b50505050905090810190601f1680156104a05780820380516001836020036101000a031916815260200191505b509950505050505050505050604051809103906000f0801580156104c8573d6000803e3d6000fd5b5060408051600160a060020a03808416825291519293509088169133917fb3e2d86065705596cb9a296ef9c830f6c9005f6396cfe948fe797cc14d50b574919081900360200190a3505050505050565b33600090815260208190526040902060019081015460029181161561010002600019011604156105cf57604080517f08c379a000000000000000000000000000000000000000000000000000000000815260206004820152602160248201527f5061727469636970616e7420697320616c72656164792072656769737465726560448201527f6400000000000000000000000000000000000000000000000000000000000000606482015290519081900360840190fd5b3360009081526020818152604090912083516105f392600190920191850190610731565b5033600090815260208181526040909120825161061292840190610731565b5033600160a060020a03167f09f6343983a89cd9ef42910b46255d2371f6d6a76f4f526c92ffac9dbc17ba118383604051808060200180602001838103835285818151815260200191508051906020019080838360005b83811015610681578181015183820152602001610669565b50505050905090810190601f1680156106ae5780820380516001836020036101000a031916815260200191505b50838103825284518152845160209182019186019080838360005b838110156106e15781810151838201526020016106c9565b50505050905090810190601f16801561070e5780820380516001836020036101000a031916815260200191505b5094505050505060405180910390a25050565b604051610c27806107cd83390190565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061077257805160ff191683800117855561079f565b8280016001018555821561079f579182015b8281111561079f578251825591602001919060010190610784565b506107ab9291506107af565b5090565b6107c991905b808211156107ab57600081556001016107b5565b905600608060405234801561001057600080fd5b50604051610c27380380610c278339810160409081528151602080840151928401516060850151608086015160a08701516000805433600160a060020a031991821617909155600180548216600160a060020a03808a16919091179091556002805490921690891617905591870180519597938401959094919093019261009d9160039190860190610268565b50600680546000919060ff191660018302179055507f0781b5da7b6aab5340f7321c153a7c81f327521d53b14c160177366b2497573d8686868686866040518087600160a060020a0316600160a060020a0316815260200186600160a060020a0316600160a060020a031681526020018060200180602001806020018561ffff1661ffff168152602001848103845288818151815260200191508051906020019080838360005b8381101561015c578181015183820152602001610144565b50505050905090810190601f1680156101895780820380516001836020036101000a031916815260200191505b50848103835287518152875160209182019189019080838360005b838110156101bc5781810151838201526020016101a4565b50505050905090810190601f1680156101e95780820380516001836020036101000a031916815260200191505b50848103825286518152865160209182019188019080838360005b8381101561021c578181015183820152602001610204565b50505050905090810190601f1680156102495780820380516001836020036101000a031916815260200191505b50995050505050505050505060405180910390a1505050505050610303565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106102a957805160ff19168380011785556102d6565b828001600101855582156102d6579182015b828111156102d65782518255916020019190600101906102bb565b506102e29291506102e6565b5090565b61030091905b808211156102e257600081556001016102ec565b90565b610915806103126000396000f3006080604052600436106100615763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416630b4f3f3d811461006657806323475ce6146100c15780638ffaa3d914610121578063b27d9b931461017a575b600080fd5b34801561007257600080fd5b506040805160206004803580820135601f81018490048402850184019095528484526100bf9436949293602493928401919081908401838280828437509497506101d39650505050505050565b005b3480156100cd57600080fd5b5060408051602060046024803582810135601f81018590048502860185019096528585526100bf95833515159536956044949193909101919081908401838280828437509497506103a79650505050505050565b34801561012d57600080fd5b506040805160206004803580820135601f81018490048402850184019095528484526100bf94369492936024939284019190819084018382808284375094975061055d9650505050505050565b34801561018657600080fd5b506040805160206004803580820135601f81018490048402850184019095528484526100bf9436949293602493928401919081908401838280828437509497506106c59650505050505050565b60015473ffffffffffffffffffffffffffffffffffffffff16331480610210575060025473ffffffffffffffffffffffffffffffffffffffff1633145b151561028c576040805160e560020a62461bcd02815260206004820152602e60248201527f4f6e6c792073656e646572206f722072656365697665722063616e2063616c6c60448201527f20746869732066756e6374696f6e000000000000000000000000000000000000606482015290519081900360840190fd5b600060065460ff16600481111561029f57fe5b14806102bb5750600160065460ff1660048111156102b957fe5b145b15156102ff576040805160e560020a62461bcd02815260206004820152601460248201526000805160206108ca833981519152604482015290519081900360640190fd5b6006805460ff1916600417905560408051602080825283518183015283517f7b5db6ce4197cbe33070c05dc81626ae3252d763b359e99d60ea1a5e34540a03938593928392918301919085019080838360005b8381101561036a578181015183820152602001610352565b50505050905090810190601f1680156103975780820380516001836020036101000a031916815260200191505b509250505060405180910390a150565b60025473ffffffffffffffffffffffffffffffffffffffff16331461043b576040805160e560020a62461bcd028152602060048201526024808201527f4f6e6c792072656365697665722063616e2063616c6c20746869732066756e6360448201527f74696f6e00000000000000000000000000000000000000000000000000000000606482015290519081900360840190fd5b600260065460ff16600481111561044e57fe5b14610491576040805160e560020a62461bcd02815260206004820152601460248201526000805160206108ca833981519152604482015290519081900360640190fd5b60068054600360ff199091161761ff0019166101008415159081029190911790915560408051918252602080830182815284519284019290925283517f7981b972a336958617d227a0bcae18a0132b9e3103e0045fd66a57c2aa49691a93869386939192909160608401919085019080838360005b8381101561051e578181015183820152602001610506565b50505050905090810190601f16801561054b5780820380516001836020036101000a031916815260200191505b50935050505060405180910390a15050565b60025473ffffffffffffffffffffffffffffffffffffffff1633146105f1576040805160e560020a62461bcd028152602060048201526024808201527f4f6e6c792072656365697665722063616e2063616c6c20746869732066756e6360448201527f74696f6e00000000000000000000000000000000000000000000000000000000606482015290519081900360840190fd5b600060065460ff16600481111561060457fe5b14610647576040805160e560020a62461bcd02815260206004820152601460248201526000805160206108ca833981519152604482015290519081900360640190fd5b805161065a90600490602084019061082e565b506006805460ff1916600117905560408051602080825283518183015283517f6ab01f1d42a4c582d9365a35b24db683b6f0e690f698edb9f68fbc2c1562c388938593928392918301919085019080838360008381101561036a578181015183820152602001610352565b60015473ffffffffffffffffffffffffffffffffffffffff16331461075a576040805160e560020a62461bcd02815260206004820152602260248201527f4f6e6c792073656e6465722063616e2063616c6c20746869732066756e63746960448201527f6f6e000000000000000000000000000000000000000000000000000000000000606482015290519081900360840190fd5b600160065460ff16600481111561076d57fe5b146107b0576040805160e560020a62461bcd02815260206004820152601460248201526000805160206108ca833981519152604482015290519081900360640190fd5b80516107c390600590602084019061082e565b506006805460ff1916600217905560408051602080825283518183015283517f6f755c00d90307ec0350a17e9a9d9fbdc58a866f4fcb565c92baa8489766925c938593928392918301919085019080838360008381101561036a578181015183820152602001610352565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061086f57805160ff191683800117855561089c565b8280016001018555821561089c579182015b8281111561089c578251825591602001919060010190610881565b506108a89291506108ac565b5090565b6108c691905b808211156108a857600081556001016108b2565b90560057726f6e6720636f6e7472616374207374617465000000000000000000000000a165627a7a72305820cd5a16252bdeec55c2f5655f397b1bd451b8e85a32dc9accb39849ec275c34530029a165627a7a7230582096b90a445a0e13382b70f2006ff4649b7eb55e1bc0a867f7ecf78818842d3d140029";

    public static final String FUNC_INITIATEFILETRANSFER = "initiateFileTransfer";

    public static final String FUNC_REGISTERME = "registerMe";

    public static final Event PARTICIPANTREGISTERED_EVENT = new Event("ParticipantRegistered", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event FILETRANSFERINITIATED_EVENT = new Event("FileTransferInitiated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Address>() {}));
    ;

    @Deprecated
    protected FileTransferRegistry(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected FileTransferRegistry(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected FileTransferRegistry(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected FileTransferRegistry(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> initiateFileTransfer(String receiver, String fileName, String cryptedFileSign, String discoveryHost, BigInteger discoveryPort) {
        final Function function = new Function(
                FUNC_INITIATEFILETRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(receiver), 
                new org.web3j.abi.datatypes.Utf8String(fileName), 
                new org.web3j.abi.datatypes.Utf8String(cryptedFileSign), 
                new org.web3j.abi.datatypes.Utf8String(discoveryHost), 
                new org.web3j.abi.datatypes.generated.Uint16(discoveryPort)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> registerMe(String pubKey, String name) {
        final Function function = new Function(
                FUNC_REGISTERME, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(pubKey), 
                new org.web3j.abi.datatypes.Utf8String(name)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public List<ParticipantRegisteredEventResponse> getParticipantRegisteredEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(PARTICIPANTREGISTERED_EVENT, transactionReceipt);
        ArrayList<ParticipantRegisteredEventResponse> responses = new ArrayList<ParticipantRegisteredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ParticipantRegisteredEventResponse typedResponse = new ParticipantRegisteredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.addr = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.pubKey = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.name = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ParticipantRegisteredEventResponse> participantRegisteredEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, ParticipantRegisteredEventResponse>() {
            @Override
            public ParticipantRegisteredEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(PARTICIPANTREGISTERED_EVENT, log);
                ParticipantRegisteredEventResponse typedResponse = new ParticipantRegisteredEventResponse();
                typedResponse.log = log;
                typedResponse.addr = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.pubKey = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.name = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<ParticipantRegisteredEventResponse> participantRegisteredEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PARTICIPANTREGISTERED_EVENT));
        return participantRegisteredEventObservable(filter);
    }

    public List<FileTransferInitiatedEventResponse> getFileTransferInitiatedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(FILETRANSFERINITIATED_EVENT, transactionReceipt);
        ArrayList<FileTransferInitiatedEventResponse> responses = new ArrayList<FileTransferInitiatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            FileTransferInitiatedEventResponse typedResponse = new FileTransferInitiatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sender = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.receiver = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.fileTransferContract = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<FileTransferInitiatedEventResponse> fileTransferInitiatedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, FileTransferInitiatedEventResponse>() {
            @Override
            public FileTransferInitiatedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(FILETRANSFERINITIATED_EVENT, log);
                FileTransferInitiatedEventResponse typedResponse = new FileTransferInitiatedEventResponse();
                typedResponse.log = log;
                typedResponse.sender = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.receiver = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.fileTransferContract = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<FileTransferInitiatedEventResponse> fileTransferInitiatedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(FILETRANSFERINITIATED_EVENT));
        return fileTransferInitiatedEventObservable(filter);
    }

    public static RemoteCall<FileTransferRegistry> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(FileTransferRegistry.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<FileTransferRegistry> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(FileTransferRegistry.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<FileTransferRegistry> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(FileTransferRegistry.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<FileTransferRegistry> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(FileTransferRegistry.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static FileTransferRegistry load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new FileTransferRegistry(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static FileTransferRegistry load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new FileTransferRegistry(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static FileTransferRegistry load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new FileTransferRegistry(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static FileTransferRegistry load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new FileTransferRegistry(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class ParticipantRegisteredEventResponse {
        public Log log;

        public String addr;

        public String pubKey;

        public String name;
    }

    public static class FileTransferInitiatedEventResponse {
        public Log log;

        public String sender;

        public String receiver;

        public String fileTransferContract;
    }
}
