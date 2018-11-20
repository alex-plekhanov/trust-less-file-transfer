package ssl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.KeyPair;
import java.security.PublicKey;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSLClientSocketFileReceiver {
    private final Logger log = LoggerFactory.getLogger(SSLClientSocketFileReceiver.class);
    private final SSLContext sslContext;
    private final String host;
    private final int port;
    private String sender;

    public SSLClientSocketFileReceiver(KeyPair keyPair, String address,
        String sender, PublicKey senderKey, String host, int port) throws Exception {
        java.security.cert.X509Certificate cert = X509CertUtils.generateCertificate("CN=" + address, keyPair);

        SSLContext context = SSLContext.getInstance("TLS");

        context.init(
            new KeyManager[] { new X509SingleKeyManager(cert, keyPair.getPrivate()) },
            new TrustManager[] { new X509SingleKeyTrustManager(sender, senderKey) },
            null);

        sslContext = context;

        this.host = host;
        this.port = port;
        this.sender = sender;
    }

    public static final int byteArrayToInt(byte[] val) {
        return (val[0] & 0xFF) << 24 | (val[1] & 0xFF) << 16 | (val[2] & 0xFF) << 8 | (val[3] & 0xFF);
    }

    public File downloadFile(String fileName, File downloadDir) throws Exception {
        SSLSocket socket = (SSLSocket)sslContext.getSocketFactory().createSocket(host, port);

        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();

        PrintWriter pw = new PrintWriter(out);
        pw.println(fileName);
        pw.flush();

        byte[] bufLen = new byte[4];
        in.read(bufLen, 0, 4);

        int size = byteArrayToInt(bufLen);

        File outputFile = new File(downloadDir, sender);

        if (!outputFile.exists())
            outputFile.mkdirs();

        outputFile = new File(outputFile, fileName);

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buf = new byte[1024];
            int total = 0;
            int bytesCount;

            while ((bytesCount = in.read(buf)) != -1 && total < size) {
                fos.write(buf, 0, bytesCount);

                total += bytesCount;
            }
        }

        return outputFile;
    }
}
