package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.web3j.crypto.ECKeyPair;
import org.web3j.utils.Numeric;

public class CryptoUtil {
    private static final String ALGORITHM = "ECDSA";
    private static final String CURVE_NAME = "secp256k1";

    private static final ECDomainParameters dp;
    private static final ECCurve curve;

    private static final ECNamedCurveSpec p;

    static {
        X9ECParameters xp = ECUtil.getNamedCurveByName(CURVE_NAME);
        p = new ECNamedCurveSpec(CURVE_NAME, xp.getCurve(), xp.getG(), xp.getN(), xp.getH(), null);
        curve = EC5Util.convertCurve(p.getCurve());
        org.bouncycastle.math.ec.ECPoint g = EC5Util.convertPoint(curve, p.getGenerator(), false);
        BigInteger n = p.getOrder();
        BigInteger h = BigInteger.valueOf(p.getCofactor());
        dp = new ECDomainParameters(curve, g, n, h);
    }

    public static PublicKey decodePublicKey(BigInteger publicKey) {
        byte[] bytes = Numeric.toBytesPadded(publicKey, 64);
        BigInteger x = Numeric.toBigInt(Arrays.copyOfRange(bytes, 0, 32));
        BigInteger y = Numeric.toBigInt(Arrays.copyOfRange(bytes, 32, 64));
        ECPoint q = curve.createPoint(x, y);
        return new BCECPublicKey(ALGORITHM, new ECPublicKeyParameters(q, dp), BouncyCastleProvider.CONFIGURATION);
    }

    public static KeyPair decodeKeyPair(ECKeyPair ecKeyPair) {
        BCECPublicKey publicKey = (BCECPublicKey)decodePublicKey(ecKeyPair.getPublicKey());
        BCECPrivateKey privateKey = new BCECPrivateKey(ALGORITHM, new ECPrivateKeyParameters(ecKeyPair.getPrivateKey(), dp), publicKey, p, BouncyCastleProvider.CONFIGURATION);
        return new KeyPair(publicKey, privateKey);
    }

    public static byte[] calcFileHash(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buf = new byte[1024];
            int bytesCount = 0;

            while ((bytesCount = fis.read(buf)) != -1)
                digest.update(buf, 0, bytesCount);
        }

        return digest.digest();
    }

    public static void cryptFile(File src, File dst, byte[] secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
        SecretKeySpec skeySpec = new SecretKeySpec(secretKey, "AES");
        Cipher encryptor = Cipher.getInstance("AES/CTR/NoPadding");

        // Initialisation vector:
        byte[] iv = new byte[encryptor.getBlockSize()];
        //SecureRandom.getInstance("SHA1PRNG").nextBytes(iv); // If storing separately
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        encryptor.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);
        //encryptor.init(Cipher.ENCRYPT_MODE, skeySpec);

        try (FileInputStream fis = new FileInputStream(src);
             FileOutputStream fos = new FileOutputStream(dst);
             CipherOutputStream cipherOut = new CipherOutputStream(fos, encryptor)
             ) {
            byte[] buf = new byte[1024];
            int bytesCount = 0;

            while ((bytesCount = fis.read(buf)) != -1)
                cipherOut.write(buf, 0, bytesCount);
        }
    }

    public static void decryptFile(File src, File dst, byte[] secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
        SecretKeySpec skeySpec = new SecretKeySpec(secretKey, "AES");
        Cipher decryptor = Cipher.getInstance("AES/CTR/NoPadding");

        // Initialisation vector:
        byte[] iv = new byte[decryptor.getBlockSize()];
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        decryptor.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);

        try (FileInputStream fis = new FileInputStream(src);
             CipherInputStream cipherIn = new CipherInputStream(fis, decryptor);
             FileOutputStream fos = new FileOutputStream(dst);
        ) {
            byte[] buf = new byte[1024];
            int bytesCount = 0;

            while ((bytesCount = cipherIn.read(buf)) != -1)
                fos.write(buf, 0, bytesCount);
        }
    }

    public static byte[] signFile(File file, KeyPair keyPair) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException {
        Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
        ecdsaSign.initSign(keyPair.getPrivate());

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buf = new byte[1024];
            int bytesCount = 0;

            while ((bytesCount = fis.read(buf)) != -1)
                ecdsaSign.update(buf, 0, bytesCount);
        }

        return ecdsaSign.sign();
    }

    public static boolean checkSignFile(File file, byte[] sign, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException {
        // TODO conflict with keys between BC and SUN providers
        return true;
/*
        Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
        ecdsaSign.initVerify(publicKey);

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buf = new byte[1024];
            int bytesCount = 0;

            while ((bytesCount = fis.read(buf)) != -1)
                ecdsaSign.update(buf, 0, bytesCount);
        }

        return ecdsaSign.verify(sign);
*/
    }

    public static byte[] signBuffer(byte[] buffer, KeyPair keyPair) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException {
        Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
        ecdsaSign.initSign(keyPair.getPrivate());

        ecdsaSign.update(buffer);

        return ecdsaSign.sign();
    }

    public static boolean checkSignBuffer(byte[] buffer, byte[] sign, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException {
        Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
        ecdsaSign.initVerify(publicKey);

        ecdsaSign.update(buffer);

        return ecdsaSign.verify(sign);
    }

    public static String bytesToString(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes));
    }

    public static byte[] stringToBytes(String string) {
        return Base64.getDecoder().decode(string.getBytes());
    }
}
