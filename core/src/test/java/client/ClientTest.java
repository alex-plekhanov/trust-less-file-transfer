package client;

import java.io.File;
import java.util.Collection;
import model.Participant;
import model.TransferContract;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientTest {
    //String rootConctractAddress = null;
    String rootConctractAddress = "0xd29ee902116d0dc704bfd24c0c398e5e18e64162";

    Logger log = LoggerFactory.getLogger(ClientTest.class);

    @Test
    public void testClientStartStop() throws Exception {
        FileTransferClient client = new FileTransferClientImpl();

        client.start("src/test/resources/keystore/test_key", "password", rootConctractAddress);

        Collection<Participant> participants = client.getAllParticipants();

        log.info("Participants: " + participants.size());

        client.stop();
    };

    @Test
    public void testNewParticipantsRegistration() throws Exception {
        FileTransferClient client1 = new FileTransferClientImpl();
        FileTransferClient client2 = new FileTransferClientImpl();
        FileTransferClient client3 = new FileTransferClientImpl();

        client1.start("src/test/resources/keystore/test_key", "password", rootConctractAddress);
        client2.start("src/test/resources/keystore/test_key2", "password", rootConctractAddress);
        client3.start("src/test/resources/keystore/test_key3", "password", rootConctractAddress);

        client1.registerParticipant("Name1");
        client2.registerParticipant("Name2");
        client3.registerParticipant("Name3");

        Collection<Participant> participants1 = client1.getAllParticipants();
        Collection<Participant> participants2 = client2.getAllParticipants();
        Collection<Participant> participants3 = client3.getAllParticipants();

        log.info("Participants1: " + participants1.size());
        log.info("Participants2: " + participants2.size());
        log.info("Participants3: " + participants3.size());

        client1.stop();
        client2.stop();
        client3.stop();
    }

    @Test
    public void testInitiateFileTransfer() throws Exception {
        FileTransferClient client1 = new FileTransferClientImpl();
        FileTransferClient client2 = new FileTransferClientImpl();
        FileTransferClient client3 = new FileTransferClientImpl();

        client1.start("src/test/resources/keystore/test_key", "password", rootConctractAddress);
        client2.start("src/test/resources/keystore/test_key2", "password", rootConctractAddress);
        client3.start("src/test/resources/keystore/test_key3", "password", rootConctractAddress);

        // 1 -> 2
        client1.initFileTransfer(new File("src/test/resources/test_file1.txt"),
            "db95b7d87bfb5414caff0593eeb18d936fc4dee2");

        // 2 -> 3
        client2.initFileTransfer(new File("src/test/resources/test_file2.txt"),
            "bb14a6f137c74902e65ca20ddaf0e1fc2bae677d");

        // 3 -> 1
        client3.initFileTransfer(new File("src/test/resources/test_file3.txt"),
            "c876e9c1094d15ead4a966a7b662221e4fad2232");

        log.info("My contracts1: " + client1.getMyFileTransferContracts().size());
        log.info("My contracts2: " + client2.getMyFileTransferContracts().size());
        log.info("My contracts3: " + client3.getMyFileTransferContracts().size());

        client1.stop();
        client2.stop();
        client3.stop();
    }

    @Test
    public void testFileAccept() throws Exception {
        FileTransferClient client2 = new FileTransferClientImpl();

        client2.start("src/test/resources/keystore/test_key2", "password", rootConctractAddress);

        TransferContract contract = client2.getFileTransferContract("0x5dff6369fcf7ffc26b2c04078eac608e3b7c870d");

        client2.acceptFile(contract);

        client2.stop();
    }

    @Test
    public void testFilePublishSecretKey() throws Exception {
        FileTransferClient client1 = new FileTransferClientImpl();

        client1.start("src/test/resources/keystore/test_key", "password", rootConctractAddress);

        TransferContract contract = client1.getFileTransferContract("0x5dff6369fcf7ffc26b2c04078eac608e3b7c870d");

        client1.stop();
    }
}
