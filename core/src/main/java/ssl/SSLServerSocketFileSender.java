package ssl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.KeyPair;
import java.util.Map;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.security.cert.X509Certificate;
import model.Participant;
import model.TransferContract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSLServerSocketFileSender {
    private final Logger log = LoggerFactory.getLogger(SSLServerSocketFileSender.class);
    private final SSLContext sslContext;
    private final int port;
    private Thread thread;
    private SSLServerSocket serverSocket;
    private File uploadDir;
    private final Map<String, Participant> participants;
    private final Map<String, TransferContract> myContracts;

    public SSLServerSocketFileSender(Map<String, Participant> participants, Map<String, TransferContract> myContracts,
        KeyPair keyPair, String address, int port, File uploadDir) throws Exception {
        this.participants = participants;
        this.myContracts = myContracts;

        java.security.cert.X509Certificate cert = X509CertUtils.generateCertificate("CN=" + address, keyPair);

        SSLContext context = SSLContext.getInstance("TLS");

        context.init(
            new KeyManager[] { new X509SingleKeyManager(cert, keyPair.getPrivate()) },
            new TrustManager[] { new X509MapBasedTrustManager(participants) },
            null);

        sslContext = context;
        this.port = port;
        this.uploadDir = uploadDir;
    }


    public void start() throws IOException {
        serverSocket = (SSLServerSocket)sslContext.getServerSocketFactory().createServerSocket(port);
        serverSocket.setNeedClientAuth(true);

        thread = new Thread(() -> body());
        thread.start();
    }

    public static final byte[] intToByteArray(int value) {
        return new byte[] {
            (byte)(value >>> 24),
            (byte)(value >>> 16),
            (byte)(value >>> 8),
            (byte)value};
    }

    public void body() {
        while (true) {
            try (SSLSocket socket = (SSLSocket)serverSocket.accept()){
                X509Certificate[] chain = socket.getSession().getPeerCertificateChain();

                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();

                BufferedReader inBuffered = new BufferedReader(new InputStreamReader(in));
                String fileName = inBuffered.readLine();

                if (chain == null || chain.length == 0) {
                    log.warn("Empty certificate");

                    continue;
                }

                if (!myContracts.keySet().contains(fileName)) {
                    log.warn("File " + fileName + " not sent");

                    continue;
                }

                String address = chain[0].getSubjectDN().getName().substring(3);

                if (!myContracts.get(fileName).getReceiver().equals(address)) {
                    log.warn("Wrong receiver" + address);

                    continue;
                }

                if (fileName != null) {
                    File inputFile = new File(uploadDir, fileName);

                    if (!inputFile.exists()) {
                        log.warn("File " + inputFile.getAbsolutePath() + " not found");

                        continue;
                    }

                    out.write(intToByteArray((int)inputFile.length()));

                    try (FileInputStream fis = new FileInputStream(inputFile)) {
                        byte[] buf = new byte[1024];
                        int bytesCount = 0;

                        while ((bytesCount = fis.read(buf)) != -1)
                            out.write(buf, 0, bytesCount);
                    }
                }

            }
            catch (Exception ex) {
                log.error("Failed to process client request", ex);
            }

            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                return;
            }
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
        thread.interrupt();
    }
}
