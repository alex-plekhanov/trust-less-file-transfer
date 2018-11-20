package ssl;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509ExtendedKeyManager;

public class X509SingleKeyManager extends X509ExtendedKeyManager {
    private static final String MAIN_ALIAS = "main";
    private final X509Certificate certificate;
    private final PrivateKey privateKey;

    public X509SingleKeyManager(X509Certificate certificate, PrivateKey privateKey) {
        this.certificate = certificate;
        this.privateKey = privateKey;
    }

    @Override public String[] getClientAliases(String keyType, Principal[] issuers) {
        return new String[] { MAIN_ALIAS };
    }

    @Override public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        return MAIN_ALIAS;
    }

    @Override public String[] getServerAliases(String keyType, Principal[] issuers) {
        return new String[] { MAIN_ALIAS };
    }

    @Override public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        return MAIN_ALIAS;
    }

    @Override public X509Certificate[] getCertificateChain(String alias) {
        if (MAIN_ALIAS.equals(alias))
            return new X509Certificate[] { certificate };
        else
            return new X509Certificate[0];
    }

    @Override public PrivateKey getPrivateKey(String alias) {
        if (MAIN_ALIAS.equals(alias))
            return privateKey;
        else
            return null;
    }
}
