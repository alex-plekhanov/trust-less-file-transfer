package ssl;

import java.net.Socket;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import model.Participant;

public class X509MapBasedTrustManager extends X509ExtendedTrustManager {
    private final Map<String, Participant> acceptedParticipants;

    public X509MapBasedTrustManager(Map<String, Participant> participants) {
        acceptedParticipants = participants;
    }

    @Override public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
        checkClientTrusted(chain, authType);
    }

    @Override public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
        checkServerTrusted(chain, authType);
    }

    @Override public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
        checkClientTrusted(chain, authType);
    }

    @Override public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
        checkServerTrusted(chain, authType);
    }

    @Override public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        checkCertificateChain(chain);
    }

    @Override public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        checkCertificateChain(chain);
    }

    private void checkCertificateChain(X509Certificate[] chain) throws CertificateException {
        if (chain.length != 1)
            throw new CertificateException("Chain must consist of single self signed certificate");

        X509Certificate certificate = chain[0];

        if (!certificate.getSubjectDN().equals(certificate.getIssuerDN()))
            throw new CertificateException("Certificate must be self issued");

        String address = certificate.getSubjectDN().getName().substring(3);

        Participant participant = acceptedParticipants.get(address);

        if (participant == null)
            throw new CertificateException("Subject not found: " + address);

        PublicKey publicKey = participant.getPublicKey();

        if (publicKey == null)
            throw new CertificateException("Public key is not registered: " + address);

        if (!publicKey.equals(certificate.getPublicKey()))
            throw new CertificateException("Public key does not match: " + address);

        try {
            certificate.verify(certificate.getPublicKey());
        }
        catch (Exception e) {
            throw new CertificateException(e.getMessage(), e);
        }
    }

    @Override public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
