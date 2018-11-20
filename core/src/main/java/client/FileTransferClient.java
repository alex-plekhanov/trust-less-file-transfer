package client;

import java.io.File;
import java.util.Collection;
import java.util.function.Consumer;
import model.Participant;
import model.TransferContract;

public interface FileTransferClient {

    void start(String accountFileName, String password, String rootContractAddress) throws Exception;

    void start(String accountFile, String password) throws Exception;

    void registerParticipant(String name) throws Exception;

    Collection<Participant> getAllParticipants();

    /**
     * @param address Participant address.
     */
    Participant getParticipant(String address);

    /**
     * Initiated by file sender.
     *
     * @param file Transfered file.
     * @param receiver Receiver ethereum address.
     */
    void initFileTransfer(File file, String receiver) throws Exception;

    /**
     * Initiated by file receiver.
     *
     * @param contract File transfer contract.
     */
    void acceptFile(TransferContract contract) throws Exception;

    /**
     * Initiated by sender or receiver.
     *
     * @param contract File transfer contract.
     */
    void cancelFileTransfer(TransferContract contract, String reason) throws Exception;

    /**
     * Initiated by arbiter.
     *
     * @param contract Contract.
     * @param file File.
     */
    void validateContract(TransferContract contract, File file) throws Exception;

    void addIncomingFileTransferListener(Consumer<TransferContract> listener);

    void addAcceptedFileListener(Consumer<TransferContract> listener);

    void addSecretKeyPublishedListener(Consumer<TransferContract> listener);

    Collection<TransferContract> getFileTransferContracts(String sender, String receiver);

    Collection<TransferContract> getMyFileTransferContracts();

    TransferContract getFileTransferContract(String address);

    void stop();
}
