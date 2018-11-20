package ssl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import model.Participant;
import model.TransferContract;
import org.junit.Assert;
import org.junit.Test;

public class SslTest {
    void serverSocket(SSLContext context, int port) {
        SSLServerSocket s;

        try {
            s = (SSLServerSocket)context.getServerSocketFactory().createServerSocket(port);;
            s.setNeedClientAuth(true);

            SSLSocket c = (SSLSocket)s.accept();

            OutputStream out = c.getOutputStream();
            InputStream in = c.getInputStream();

            int val = in.read();
            out.write(val ^ 0xFF);

            System.out.println("Server received " + val);

            Assert.assertEquals(val, 10);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    void clientSocket(SSLContext context, int port) {
        try {
            SSLSocket s = (SSLSocket)context.getSocketFactory().createSocket("localhost", port);

            OutputStream out = s.getOutputStream();
            InputStream in = s.getInputStream();

            out.write(10);
            int val = in.read();

            System.out.println("Client received " + val);

            Assert.assertEquals(val, 10 ^ 0xFF);
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSslConnection() throws Exception{
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

        keyGen.initialize(256, random);

        KeyPair keyPair1 = keyGen.generateKeyPair();
        KeyPair keyPair2 = keyGen.generateKeyPair();

        String dn1 = "CN=test1";
        String dn2 = "CN=test2";

        X509Certificate cert1 = X509CertUtils.generateCertificate(dn1, keyPair1);
        X509Certificate cert2 = X509CertUtils.generateCertificate(dn2, keyPair2);

        SSLContext sslContext1 = SSLContext.getInstance("TLS");
        sslContext1.init(
            new KeyManager[] { new X509SingleKeyManager(cert1, keyPair1.getPrivate()) },
            new TrustManager[] { new X509SingleKeyTrustManager(dn2.substring(3), keyPair2.getPublic()) },
            null);

        SSLContext sslContext2 = SSLContext.getInstance("TLS");
        sslContext2.init(
            new KeyManager[] { new X509SingleKeyManager(cert2, keyPair2.getPrivate()) },
            new TrustManager[] { new X509SingleKeyTrustManager(dn1.substring(3), keyPair1.getPublic()) },
            null);


        Future<Void> serverFuture = CompletableFuture.runAsync(() -> serverSocket(sslContext1, 12345));

        Thread.sleep(1_000L);

        Future<Void> clientFuture = CompletableFuture.runAsync(() -> clientSocket(sslContext2, 12345));

        serverFuture.get();
        clientFuture.get();
    }

    private TransferContract contract(String addr, String sender, String receiver, String fileName) {
        TransferContract contract = new TransferContract(addr, sender, receiver);
        contract.setFileName(fileName);

        return contract;
    }

    @Test
    public void testSslSocketFileReceiver() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

        keyGen.initialize(256, random);

        KeyPair keyPair1 = keyGen.generateKeyPair();
        KeyPair keyPair2 = keyGen.generateKeyPair();
        KeyPair keyPair3 = keyGen.generateKeyPair();

        Map<String, Participant> participants = new HashMap<>();
        participants.put("test1", new Participant("test1", "Test participant 1", keyPair1.getPublic()));
        participants.put("test2", new Participant("test2", "Test participant 2", keyPair2.getPublic()));
        participants.put("test3", new Participant("test3", "Test participant 3", keyPair3.getPublic()));

        Map<String, TransferContract> transferContracts = new HashMap<>();
        transferContracts.put("contract1", contract("contract1", "test1", "test2", "testfile1.txt"));
        transferContracts.put("contract2", contract("contract2", "test1", "test3", "testfile2.txt"));
        transferContracts.put("contract3", contract("contract3", "test1", "test3", "testfile3.txt"));

        File uploadDir = new File("upload");
        uploadDir.mkdirs();
        uploadDir.deleteOnExit();

        File uploadFile = new File(uploadDir, "contract1");

        try (OutputStream out = new FileOutputStream(uploadFile)) {
            for (int i = 0; i < 10_000; i++)
                out.write(i ^ (i >> 8) ^ (i >> 16));
        }

        File downloadDir = new File("download");
        downloadDir.mkdirs();
        downloadDir.deleteOnExit();

        SSLServerSocketFileSender senderServer = new SSLServerSocketFileSender(participants,
            transferContracts, keyPair1, "test1", 12345, uploadDir);

        senderServer.start();

        SSLClientSocketFileReceiver clientReceiver = new SSLClientSocketFileReceiver(keyPair2, "test2",
            "test1", keyPair1.getPublic(), "localhost", 12345);

        File downloadFile = clientReceiver.downloadFile("contract1", downloadDir);

        byte[] fileContent1 = Files.readAllBytes(uploadFile.toPath());
        byte[] fileContent2 = Files.readAllBytes(downloadFile.toPath());

        Assert.assertTrue(Arrays.equals(fileContent1, fileContent2));
    }
}
