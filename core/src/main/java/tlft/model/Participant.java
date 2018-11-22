package tlft.model;

import java.math.BigInteger;
import java.security.PublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tlft.util.CryptoUtil;

public class Participant {
    private static final Logger log = LoggerFactory.getLogger(Participant.class);
    private final String address;
    private final String name;
    private final PublicKey publicKey;

    public Participant(String address, String name, PublicKey publicKey) {
        this.address = address;
        this.name = name;
        this.publicKey = publicKey;
    }

    public Participant(String address, String name, String publicKey) {
        PublicKey key = null;

        try {
            BigInteger publicAsInt = new BigInteger(publicKey);
            key = CryptoUtil.decodePublicKey(publicAsInt);
        }
        catch (Exception ex) {
            log.error("Error converting public key", ex);
        }

        this.address = address;
        this.name = name;
        this.publicKey = key;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Participant that = (Participant)o;

        return address.equals(that.address);
    }

    @Override public int hashCode() {
        return address.hashCode();
    }

    @Override public String toString() {
        return "Participant{" +
            "address='" + address + '\'' +
            ", name='" + name + '\'' +
            ", publicKey=" + publicKey +
            '}';
    }
}
