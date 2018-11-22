package tlft.ssl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import tlft.model.Participant;
import tlft.model.TransferContract;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SslFileTransfer {
    private Map<String, KeyPair> keyPairs;
    private Map<String, Participant> participants;
    private Map<String, TransferContract> transferContracts;
    private File uploadDir;
    private File downloadDir;
    private SSLServerSocketFileSender senderServer;

    @Before
    public void setUp() throws Exception {
        keyPairs = new HashMap<>();
        participants = new HashMap<>();
        transferContracts = new HashMap<>();

        File workDir = new File("work/test");
        workDir.mkdirs();
        workDir.deleteOnExit();

        uploadDir = new File(workDir, "upload");
        uploadDir.mkdirs();

        downloadDir = new File(workDir, "download");
        downloadDir.mkdirs();

        newParticipant("test1");
        newParticipant("test2");
        newParticipant("test3");

        newContract("contract1", "test1", "test2", "testfile1.txt");
        newContract("contract2", "test1", "test3", "testfile2.txt");
        newContract("contract3", "test1", "test3", "testfile3.txt");

        senderServer = new SSLServerSocketFileSender(participants, transferContracts, keyPairs.get("test1"), "test1",
            12345, uploadDir);
        senderServer.start();
    }

    @After
    public void tearDown() throws IOException, InterruptedException {
        senderServer.stop();
    }

    private void newContract(String addr, String sender, String receiver, String fileName) throws IOException {
        TransferContract contract = new TransferContract(addr, sender, receiver);
        contract.setFileName(fileName);

        transferContracts.put(addr, contract);

        // Generate file
        File uploadFile = new File(uploadDir, addr);

        try (OutputStream out = new FileOutputStream(uploadFile)) {
            for (int i = 0; i < Math.abs(addr.hashCode()) % 10_000; i++)
                out.write(i ^ (i >> 8) ^ (i >> 16));
        }
    }

    private void newParticipant(String addr) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

        keyGen.initialize(256, random);

        KeyPair keyPair = keyGen.generateKeyPair();

        keyPairs.put(addr, keyPair);

        participants.put(addr, new Participant(addr, "Participant " + addr, keyPair.getPublic()));
    }

    private void assertFilesEqual(File file1, File file2) throws IOException {
        byte[] fileContent1 = Files.readAllBytes(file1.toPath());
        byte[] fileContent2 = Files.readAllBytes(file2.toPath());

        Assert.assertTrue(Arrays.equals(fileContent1, fileContent2));
    }

    @Test
    public void testSslSocketSuccessfulFileReceiver() throws Exception {
        SSLClientSocketFileReceiver clientReceiver2 = new SSLClientSocketFileReceiver(keyPairs.get("test2"), "test2",
            "test1", keyPairs.get("test1").getPublic(), "localhost", 12345);

        File downloadFile = clientReceiver2.downloadFile("contract1", downloadDir);

        assertFilesEqual(new File(uploadDir, "contract1"), downloadFile);

        SSLClientSocketFileReceiver clientReceiver3 = new SSLClientSocketFileReceiver(keyPairs.get("test3"), "test3",
            "test1", keyPairs.get("test1").getPublic(), "localhost", 12345);

        downloadFile = clientReceiver3.downloadFile("contract2", downloadDir);

        assertFilesEqual(new File(uploadDir, "contract2"), downloadFile);

        downloadFile = clientReceiver3.downloadFile("contract3", downloadDir);

        assertFilesEqual(new File(uploadDir, "contract3"), downloadFile);

        senderServer.stop();
    }

    @Test(expected = IOException.class)
    public void testSslSocketWrongFile() throws Exception {
        SSLClientSocketFileReceiver clientReceiver = new SSLClientSocketFileReceiver(keyPairs.get("test2"), "test2",
            "test1", keyPairs.get("test1").getPublic(), "localhost", 12345);

        clientReceiver.downloadFile("contract2", downloadDir);
    }

    @Test(expected = IOException.class)
    public void testSslSocketNotExistFile() throws Exception {
        SSLClientSocketFileReceiver clientReceiver = new SSLClientSocketFileReceiver(keyPairs.get("test2"), "test2",
            "test1", keyPairs.get("test1").getPublic(), "localhost", 12345);

        clientReceiver.downloadFile("contract0", downloadDir);
    }

    @Test(expected = IOException.class)
    public void testSslSocketUnknownClient() throws Exception {
        SSLClientSocketFileReceiver clientReceiver = new SSLClientSocketFileReceiver(keyPairs.get("test2"), "test0",
            "test1", keyPairs.get("test1").getPublic(), "localhost", 12345);

        clientReceiver.downloadFile("contract1", downloadDir);
    }

    @Test(expected = IOException.class)
    public void testSslSocketWrongClientKey() throws Exception {
        SSLClientSocketFileReceiver clientReceiver = new SSLClientSocketFileReceiver(keyPairs.get("test3"), "test2",
            "test1", keyPairs.get("test1").getPublic(), "localhost", 12345);

        clientReceiver.downloadFile("contract1", downloadDir);
    }

    @Test(expected = IOException.class)
    public void testSslSocketWrongServerAddr() throws Exception {
        SSLClientSocketFileReceiver clientReceiver = new SSLClientSocketFileReceiver(keyPairs.get("test2"), "test2",
            "test3", keyPairs.get("test1").getPublic(), "localhost", 12345);

        clientReceiver.downloadFile("contract1", downloadDir);
    }

    @Test(expected = IOException.class)
    public void testSslSocketWrongServerKey() throws Exception {
        SSLClientSocketFileReceiver clientReceiver = new SSLClientSocketFileReceiver(keyPairs.get("test2"), "test2",
            "test1", keyPairs.get("test3").getPublic(), "localhost", 12345);

        clientReceiver.downloadFile("contract1", downloadDir);
    }

    @Test(expected = IOException.class)
    public void testSslSocketWrongPort() throws Exception {
        SSLClientSocketFileReceiver clientReceiver = new SSLClientSocketFileReceiver(keyPairs.get("test2"), "test2",
            "test1", keyPairs.get("test1").getPublic(), "localhost", 12346);

        clientReceiver.downloadFile("contract1", downloadDir);
    }
}
