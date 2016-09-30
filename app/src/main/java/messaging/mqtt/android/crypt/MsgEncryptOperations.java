package messaging.mqtt.android.crypt;

import android.content.Context;
import android.content.res.Resources;
import android.util.Base64;
import android.util.Log;

import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.SecretKeySpec;

import messaging.mqtt.android.database.DbConstants;
import messaging.mqtt.android.database.DbEntryService;
import messaging.mqtt.android.mqtt.MqttConstants;
import messaging.mqtt.android.service.AsimService;
import messaging.mqtt.android.tasks.MqttSendMsgTask;

/**
 * Created by eercan on 30.12.2015.
 */
public class MsgEncryptOperations {

    public static Integer ALARM_CODE = -25;
    private static String TAG = MsgEncryptOperations.class.getSimpleName();
    private static String ALIES = "MsgDFKey";
    private static String KEY_STORE = "AndroidKeyStore";
    private static KeyPairGenerator kpg;
    private static Context context;


    public static byte[] createSelfKeySpec(Context context, String topic) throws Exception {
        MsgEncryptOperations.context = context;

        PrivateKey privateKey;
        PublicKey publicKey;

        createKeyPairGenerator();

        KeyPair kp1 = genKeyPair();
        publicKey = kp1.getPublic();
        privateKey = kp1.getPrivate();


        byte[] pbEnc = DbEncryptOperations.encrypt(publicKey.getEncoded());
        byte[] prEnc = DbEncryptOperations.encrypt(privateKey.getEncoded());

        String pbStr = Base64.encodeToString(pbEnc, Base64.DEFAULT);
        String prStr = Base64.encodeToString(prEnc, Base64.DEFAULT);

        DbEntryService.updateChatPbSpec(topic, pbStr, prStr);
        return publicKey.getEncoded();
    }

    public static void createMsgKeySpec(Context context, String topic, String opbk, int isSent)
            throws Exception {
        MsgEncryptOperations.context = context;

        if (opbk == null) {
            throw new Exception("OPBK is empty");
        }

        HashMap<String, String> chatByTopic = DbEntryService.getChatByTopic(topic);
        if (chatByTopic.get(DbConstants.CHAT_PBK) == null || chatByTopic.get(DbConstants
                .CHAT_PRK) == null) {
            createSelfKeySpec(context, topic);
        }

        chatByTopic = DbEntryService.getChatByTopic(topic);
        byte[] pbDeco = Base64.decode(chatByTopic.get(DbConstants.CHAT_PBK).getBytes(), Base64
                .DEFAULT);
        byte[] prDeco = Base64.decode(chatByTopic.get(DbConstants.CHAT_PRK).getBytes(), Base64
                .DEFAULT);

        byte[] pbDcr = DbEncryptOperations.decrypt(pbDeco);
        byte[] prDcr = DbEncryptOperations.decrypt(prDeco);
        byte[] otherEnc = Base64.decode(opbk.getBytes(), Base64.DEFAULT);

        createKeyPairGenerator();

        KeyFactory keyFactory = KeyFactory.getInstance("ECDH", "SC");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(prDcr);
        EncodedKeySpec otherPublicKeySpec = new X509EncodedKeySpec(otherEnc);

        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        PublicKey otherKey = keyFactory.generatePublic(otherPublicKeySpec);

        byte[] secretKey = MsgEncryptOperations.agreeSecretKey(privateKey, otherKey, true);


        byte[] opbEnc = DbEncryptOperations.encrypt(otherKey.getEncoded());
        byte[] msgEnc = DbEncryptOperations.encrypt(secretKey);

        String opbStr = Base64.encodeToString(opbEnc, Base64.DEFAULT);
        String msgStr = Base64.encodeToString(msgEnc, Base64.DEFAULT);

        DbEntryService.updateChatMsgSpec(topic, opbStr, msgStr);

        if (isSent == 0) {
            String pb = new String(pbDcr, "utf-8");
            String sent = (MqttConstants.MQTT_PB_SELF + pb);
            MqttSendMsgTask sendPb = new MqttSendMsgTask(topic, Base64.encode(sent.getBytes(),
                    Base64.DEFAULT));
            AsimService.getSubSendExecutor().submit(sendPb);
        }
        MqttSendMsgTask task = new MqttSendMsgTask(topic, (MqttConstants.MQTT_PB_TAKEN)
                .getBytes());
        AsimService.getSubSendExecutor().submit(task);

        Log.e(TAG, "New key taken. " + topic);
    }

 /*   private static void createKeyPairGenerator() throws NoSuchProviderException,
 NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidParameterSpecException {

        // === Generates and inits a KeyPairGenerator ===

        // changed this to use default parameters, generating your
        // own takes a lot of time and should be avoided
        // use ECDH or a newer Java (8) to support key generation with
        // higher strength
        if (Build.VERSION.SDK_INT > 18) {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 1);

            KeyPairGeneratorSpec spec21 = new KeyPairGeneratorSpec.Builder(context)
                    .setAlias(ALIES)
                    .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                    .setSerialNumber(BigInteger.ONE)
                    .setStartDate(start.getTime())
                    .setEndDate(end.getTime())
                    .setKeySize(1024)
                    .build();

            AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
            paramGen.init(spec21);
            //paramGen.init(1024);
            AlgorithmParameters params = paramGen.generateParameters();
            DHParameterSpec spec = (DHParameterSpec) params.getParameterSpec(DHParameterSpec.class);
            kpg = KeyPairGenerator.getInstance("DH");
            kpg.initialize(spec);
        } else {
            kpg = KeyPairGenerator.getInstance("DH");
            kpg.initialize(1024);

        }
        //kpg = KeyPairGenerator.getInstance("DH");


    }   */

    private static void createKeyPairGenerator() throws NoSuchProviderException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidParameterSpecException {

        // === Generates and inits a KeyPairGenerator ===

        // changed this to use default parameters, generating your
        // own takes a lot of time and should be avoided
        // use ECDH or a newer Java (8) to support key generation with
        // higher strength
        if (kpg == null) {
            Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);

            kpg = KeyPairGenerator.getInstance("ECDH", "SC");
            // ECDHGenParameterSpec spec = new ECGenParameterSpec("secp256r1");
            // keyPairGenerator.initialize(spec, new SecureRandom());
            ECNamedCurveParameterSpec parameterSpec = ECNamedCurveTable.getParameterSpec
                    ("secp256r1");
            kpg.initialize(parameterSpec);
        }


    }

    private static byte[] agreeSecretKey(PrivateKey prk_self,
                                         PublicKey pbk_peer, boolean lastPhase) throws Exception {
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
        return bkey;
    }

    private static KeyPair genKeyPair() {
        return kpg.generateKeyPair();
    }

    public static byte[] encryptMsg(String topic, byte[] msg) throws Exception {
        byte[] encrypt = encrypt(getKey(topic), msg);
        return encrypt;
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    public static byte[] decryptMsg(String topic, byte[] msg) throws Exception {
        byte[] decrypt = decrypt(getKey(topic), msg);
        return decrypt;
    }

    private static byte[] decrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(clear);
        return decrypted;
    }

    private static byte[] getKey(String topic) throws Exception {
        HashMap<String, String> chatByTopic = DbEntryService.getChatByTopic(topic);

        String sgkStr = chatByTopic.get(DbConstants.CHAT_MSGK);

        if (sgkStr == null) {
            throw new Exception("");
        }

        byte[] msg = Base64.decode(sgkStr.getBytes(), Base64.DEFAULT);
        byte[] key = DbEncryptOperations.decrypt(msg);
        return key;
    }

    private static byte[] readFromFile(int resourceId) throws IOException {
        Resources res = MsgEncryptOperations.context.getResources();
        InputStream prv = res.openRawResource(resourceId);

        byte[] b = new byte[prv.available()];
        prv.read(b);
        return b;
    }

}
