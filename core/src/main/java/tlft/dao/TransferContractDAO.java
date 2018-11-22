package tlft.dao;

import java.util.Collection;
import java.util.Map;
import tlft.model.TransferContract;

public interface TransferContractDAO {
    /**
     * @param sender {@code null} or sender address.
     * @param receiver {@code null} or receiver address.
     */
    Collection<TransferContract> getAllTransferContracts(String sender, String receiver);

    Map<String, TransferContract> getMyTransferContractsMap();

    Collection<TransferContract> getMyTransferContracts();

    /**
     * @param address Transfer contract address.
     */
    TransferContract getTransferContract(String address);

    Collection<TransferContract> getPendingContractChanges();
}
