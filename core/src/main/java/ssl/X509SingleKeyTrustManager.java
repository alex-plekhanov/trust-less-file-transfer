package ssl;

import java.security.PublicKey;
import java.util.Collections;
import model.Participant;

public class X509SingleKeyTrustManager extends X509MapBasedTrustManager {
    public X509SingleKeyTrustManager(String address, PublicKey key) {
        super(Collections.singletonMap(address, new Participant(address, address, key)));
    }
}
