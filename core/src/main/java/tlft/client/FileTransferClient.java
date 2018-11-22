package tlft.client;

import java.io.File;
import java.util.Collection;
import java.util.function.Consumer;
import tlft.model.Participant;
import tlft.model.TransferContract;

public interface FileTransferClient {

    void start(String accountFileName, String password, String rootContractAddress) throws Exception;

    void start(String accountFile, String password) throws Exception;

    String getRootContractAddress() throws Exception;

    String getMyParticipantAddress() throws Exception;

    void registerParticipant(String name) throws Exception;

    Collection<Participant> getAllParticipants() throws Exception;

    /**
     * @param address Participant address.
     */
    Participant getParticipant(String address) throws Exception;

    /**
     * Initiated by file sender.
     * @param file Transfered file.
     * @param receiver Receiver ethereum address.
     * @return Contracts address.
     */
    String initFileTransfer(File file, String receiver) throws Exception;

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

    Collection<TransferContract> getFileTransferContracts(String sender, String receiver) throws Exception;

    Collection<TransferContract> getMyFileTransferContracts() throws Exception;

    TransferContract getFileTransferContract(String address) throws Exception;

    void stop() throws Exception;
}
