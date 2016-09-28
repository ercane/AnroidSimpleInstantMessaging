package messaging.mqtt.android.crypt;

import android.util.Log;

import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by eercan on 31.12.2015.
 */
public class ECDHModule {

    private static final int AES_KEY_SIZE = 128;
    private static String TAG = ECDHModule.class.getSimpleName();
    private static KeyPairGenerator kpg;

    static {
        try {
            // === Generates and inits a KeyPairGenerator ===

            // changed this to use default parameters, generating your
            // own takes a lot of time and should be avoided
            // use ECDH or a newer Java (8) to support key generation with
            // higher strength
            Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);

            kpg = KeyPairGenerator.getInstance("ECDH", "SC");
            // ECDHGenParameterSpec spec = new ECGenParameterSpec("secp256r1");
            // keyPairGenerator.initialize(spec, new SecureRandom());
            ECNamedCurveParameterSpec parameterSpec = ECNamedCurveTable.getParameterSpec("secp256r1");
            kpg.initialize(parameterSpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testECDH() {
        // Generates keyPairs for Alice and Bob
        KeyPair kp1 = ECDHModule.genECDHKeyPair();
        KeyPair kp2 = ECDHModule.genECDHKeyPair();
        // Gets the public key of Alice(g^X mod p) and Bob (g^Y mod p)
        PublicKey pbk1 = kp1.getPublic();
        PublicKey pbk2 = kp2.getPublic();
        // Gets the private key of Alice X and Bob Y
        PrivateKey prk1 = kp1.getPrivate();
        PrivateKey prk2 = kp2.getPrivate();
        try {
            // Computes secret keys for Alice (g^Y mod p)^X mod p == Bob (g^X
            // mod p)^Y mod p
            SecretKey key1 = ECDHModule.agreeSecretKey(prk1, pbk2, true);
            SecretKey key2 = ECDHModule.agreeSecretKey(prk2, pbk1, true);
            // Instantiate the Cipher of algorithm "DES"
            Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
            // Init the cipher with Alice's key1
            c.init(Cipher.ENCRYPT_MODE, key1);
            // Compute the cipher text = E(key,plainText)
            byte[] ciphertext = c.doFinal("Stand and unfold yourself".getBytes());
            // prints ciphertext
            Log.e(TAG, "Encrypted: " + new String(ciphertext, "utf-8"));
            // inits the encryptionMode
            c.init(Cipher.DECRYPT_MODE, key2);
            // Decrypts and print
            Log.e(TAG, "Decrypted: " + new String(c.doFinal(ciphertext), "utf-8"));
            Log.e(TAG, "Done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SecretKey agreeSecretKey(PrivateKey prk_self, PublicKey pbk_peer, boolean lastPhase) throws Exception {
        // instantiates and inits a KeyAgreement
        KeyAgreement ka = KeyAgreement.getInstance("ECDH", "SC");
        ka.init(prk_self);
        // Computes the KeyAgreement
        ka.doPhase(pbk_peer, lastPhase);
        // Generates the shared secret
        byte[] secret = ka.generateSecret();

        // === Generates an AES key ===

        // you should really use a Key Derivation Function instead, but this is
        // rather safe

        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] bkey = sha256.digest(secret);

        SecretKey desSpec = new SecretKeySpec(bkey, "AES");
        return desSpec;
    }

    public static KeyPair genECDHKeyPair() {
        return kpg.generateKeyPair();
    }
}

