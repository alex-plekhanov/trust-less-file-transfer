package tlft.client;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import junit.framework.TestCase;
import tlft.model.ContractState;
import tlft.model.Participant;
import tlft.model.TransferContract;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientTest extends TestCase {
    Logger log = LoggerFactory.getLogger(ClientTest.class);

    @Test
    public void testClientStartStop() throws Exception {
        FileTransferClient client = new FileTransferClientImpl();

        client.start("src/test/resources/keystore/test_key", "password");

        client.stop();
    };

    @Test
    public void testNewParticipantsRegistration() throws Exception {
        FileTransferClient client1 = new FileTransferClientImpl();
        FileTransferClient client2 = new FileTransferClientImpl();
        FileTransferClient client3 = new FileTransferClientImpl();

        client1.start("src/test/resources/keystore/test_key", "password");
        client2.start("src/test/resources/keystore/test_key2", "password", client1.getRootContractAddress());
        client3.start("src/test/resources/keystore/test_key3", "password", client1.getRootContractAddress());

        client1.registerParticipant("Name1");
        client2.registerParticipant("Name2");
        client3.registerParticipant("Name3");

        Collection<Participant> participants1 = client1.getAllParticipants();
        Collection<Participant> participants2 = client2.getAllParticipants();
        Collection<Participant> participants3 = client3.getAllParticipants();

        assertEquals(3, participants1.size());
        assertEquals(3, participants2.size());
        assertEquals(3, participants3.size());

        assertEquals(new ArrayList<>(participants1), new ArrayList<>(participants2));
        assertEquals(new ArrayList<>(participants2), new ArrayList<>(participants3));

        client1.stop();
        client2.stop();
        client3.stop();
    }

    @Test
    public void testInitiateFileTransfer() throws Exception {
        FileTransferClient client1 = new FileTransferClientImpl();
        FileTransferClient client2 = new FileTransferClientImpl();
        FileTransferClient client3 = new FileTransferClientImpl();

        client1.start("src/test/resources/keystore/test_key", "password");
        client2.start("src/test/resources/keystore/test_key2", "password", client1.getRootContractAddress());
        client3.start("src/test/resources/keystore/test_key3", "password", client1.getRootContractAddress());

        client1.registerParticipant("Name1");
        client2.registerParticipant("Name2");
        client3.registerParticipant("Name3");

        // 1 -> 2
        client1.initFileTransfer(new File("src/test/resources/test_file1.txt"), client2.getMyParticipantAddress());

        // 2 -> 3
        client2.initFileTransfer(new File("src/test/resources/test_file2.txt"), client3.getMyParticipantAddress());

        // 3 -> 1
        client3.initFileTransfer(new File("src/test/resources/test_file3.txt"), client1.getMyParticipantAddress());

        assertEquals(2, client1.getMyFileTransferContracts().size());
        assertEquals(2, client2.getMyFileTransferContracts().size());
        assertEquals(2, client3.getMyFileTransferContracts().size());

        client1.stop();
        client2.stop();
        client3.stop();
    }

    @Test
    public void testFileAccept() throws Exception {
        FileTransferClient client1 = new FileTransferClientImpl();
        FileTransferClient client2 = new FileTransferClientImpl();

        client1.start("src/test/resources/keystore/test_key", "password");
        client2.start("src/test/resources/keystore/test_key2", "password", client1.getRootContractAddress());

        client1.registerParticipant("Name1");
        client2.registerParticipant("Name2");

        String contractAddress = client1.initFileTransfer(new File("src/test/resources/test_file1.txt"),
            client2.getMyParticipantAddress());

        TransferContract contract2 = client2.getFileTransferContract(contractAddress);

        Thread.sleep(3_000L);

        client2.acceptFile(contract2);

        Thread.sleep(3_000L);

        TransferContract contract1 = client1.getFileTransferContract(contractAddress);

        assertEquals(ContractState.COMPLETED, contract1.getState());

        client1.stop();
        client2.stop();
    }
}
