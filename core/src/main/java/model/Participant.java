package model;

import java.math.BigInteger;
import java.security.PublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.CryptoUtil;

public class Participant {
    private Logger log = LoggerFactory.getLogger(Participant.class);
    private String address;
    private String name;
    private PublicKey publicKey;

    public Participant() {
    }

    public Participant(String address, String name, PublicKey publicKey) {
        this.address = address;
        this.name = name;
        this.publicKey = publicKey;
    }

    public Participant(String address, String name, String publicKey) {
        this.address = address;
        this.name = name;

        try {
            BigInteger publicAsInt = new BigInteger(publicKey);
            this.publicKey = CryptoUtil.decodePublicKey(publicAsInt);
        }
        catch (Exception ex) {
            log.error("Error converting public key", ex);
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }
}
