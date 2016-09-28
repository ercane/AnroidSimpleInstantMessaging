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
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.SecretKeySpec;

import messaging.mqtt.android.service.AsimService;

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

    public static void createMsgKey(Context context) throws Exception {
        MsgEncryptOperations.context = context;
        PrivateKey privateKey;
        PublicKey publicKey;
        byte[] serverEncode = new byte[0];
        createKeyPairGenerator();
        String publicStr;
/*        if ("".equals(McysDeviceService.getPreferencesService().getPublicKey()) ||
                "".equals(McysDeviceService.getPreferencesService().getPrivateKey())) {*/
        KeyPair kp1 = genKeyPair();
        publicKey = kp1.getPublic();
        privateKey = kp1.getPrivate();

/*        } else {
            publicStr = McysDeviceService.getPreferencesService().getPublicKey();
            String privateStr = McysDeviceService.getPreferencesService().getPrivateKey();

            KeyFactory keyFactory = KeyFactory.getInstance("DH");
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.decode(privateStr.getBytes(), Base64.DEFAULT));
            privateKey = keyFactory.generatePrivate(privateKeySpec);

            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decode(publicStr.getBytes(), Base64.DEFAULT));
            publicKey = keyFactory.generatePublic(publicKeySpec);
        }*/

        //TODO create new key
        // serverEncode = responseEntity.getBody();

/*        KeyFactory keyFactory = KeyFactory.getInstance("DH");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(serverEncode);
        PublicKey serverKey = keyFactory.generatePublic(publicKeySpec);

        byte[] secretKey = MsgEncryptOperations.agreeSecretKey(privateKey, serverKey, true);

        String keyStr = Base64.encodeToString(secretKey, Base64.DEFAULT);
        String encrypt = DbEncryptOperations.encrypt(keyStr);
        McysDeviceService.getPreferencesService().setMsgKey(encrypt);*/

        KeyFactory keyFactory = KeyFactory.getInstance("ECDH", "SC");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(serverEncode);
        PublicKey serverKey = keyFactory.generatePublic(publicKeySpec);

        byte[] secretKey = MsgEncryptOperations.agreeSecretKey(privateKey, serverKey, true);

        byte[] encrypt = DbEncryptOperations.encrypt(secretKey);
        String keyStr = Base64.encodeToString(encrypt, Base64.DEFAULT);
        AsimService.getPreferencesService().setMsgKey(keyStr);
        AsimService.getPreferencesService().setMsgKeyExpiredTime(System.currentTimeMillis() +
                1000 * 60 * 55);//55 minute

        Log.e(TAG, "New key taken.");

        /*String cron="0 0 0/1 1/1 * ? *";
        Intent targetIntent = new Intent();
        targetIntent.putExtra(CronSchedulerReceiver.EXTRA_FIELD_CRON_EXPRESSION, cron);
        targetIntent.putExtra(CronSchedulerReceiver.EXTRA_FIELD_DM_ID, ALARM_CODE);
        targetIntent.putExtra(CronSchedulerReceiver.MONITOR_TYPE, ALARM_CODE);

        CronSchedulerReceiver.scheduleJob(MsgEncryptOperations.context, targetIntent, cron, ALARM_CODE);*/
    }

 /*   private static void createKeyPairGenerator() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidParameterSpecException {

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

    private static void createKeyPairGenerator() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidParameterSpecException {

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

    public static byte[] encryptMsg(byte[] msg) throws Exception {
        byte[] encrypt = encrypt(getKey(), msg);
        return encrypt;
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    public static byte[] decryptMsg(byte[] msg) throws Exception {
        byte[] decrypt = decrypt(getKey(), msg);
        return decrypt;
    }

    private static byte[] decrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(clear);
        return decrypted;
    }

    private static byte[] getKey() throws Exception {
        String msgKey = AsimService.getPreferencesService().getMsgKey();
        byte[] msg = Base64.decode(msgKey.getBytes(), Base64.DEFAULT);
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
