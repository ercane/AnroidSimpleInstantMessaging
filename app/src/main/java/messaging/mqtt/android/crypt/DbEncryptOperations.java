package messaging.mqtt.android.crypt;

import android.os.Build;
import android.util.Base64;


import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import messaging.mqtt.android.service.AsimService;

/**
 * Created by eercan on 28.12.2015.
 */
public class DbEncryptOperations {

    private final static String HEX = "0123456789ABCDEF";

    public static byte[] encrypt(byte[] s) throws Exception {
        byte[] salt = readKey();
        SecretKey secretKey = generateKey(Build.ID.toCharArray(), salt);
        byte[] encrypted = encrypt(secretKey.getEncoded(), s);
        return encrypted;
    }

    public static byte[] decrypt(byte[] s) throws Exception {
        byte[] salt = readKey();
        SecretKey secretKey = generateKey(Build.ID.toCharArray(), salt);
        byte[] decrypt = decrypt(secretKey.getEncoded(), s);
        return decrypt;
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seed);
        kgen.init(256, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted)
            throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }

    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                    16).byteValue();
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        // Generate a 256-bit key
        final int outputKeyLength = 256;
        SecureRandom secureRandom = new SecureRandom();
        // Do *not* seed secureRandom! Automatically seeded from system entropy.
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(outputKeyLength, secureRandom);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }

    public static SecretKey generateKey(char[] passphraseOrPin, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Number of PBKDF2 hardening rounds to use. Larger values increase
        // computation time. You should select a value that causes computation
        // to take >100ms.
        final int iterations = 1000;
// Generate a 256-bit key
        final int outputKeyLength = 256;
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        KeySpec keySpec = new PBEKeySpec(passphraseOrPin, salt, iterations, outputKeyLength);
        SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        return secretKey;
    }


    public static byte[] readKey() throws Exception {
        if ("".equals(AsimService.getPreferencesService().getPassword())) {
            Long time=System.currentTimeMillis();
            AsimService.getPreferencesService().setEnrollmentDate(time);
            return time.toString().getBytes();
        }

        String dbKeyEncoded = AsimService.getPreferencesService().getPassword();
        byte[] key = Base64.decode(dbKeyEncoded.getBytes(), Base64.DEFAULT);
        return key;
    }

    public static byte[] readPass() throws Exception {
        if ("".equals(AsimService.getPreferencesService().getPassword())) {
            throw new Exception("There is no pass");
        }

        String dbKeyEncoded = AsimService.getPreferencesService().getPassword();
        byte[] key = Base64.decode(dbKeyEncoded.getBytes(), Base64.DEFAULT);

        SecretKey secretKey = generateKey(Build.ID.toCharArray(), AsimService.getPreferencesService().getEnrollmentDate().toString().getBytes());
        byte[] decrypt = decrypt(secretKey.getEncoded(), key);
        return decrypt;
    }

    public static void writeKey(byte[] key) throws Exception {
        String s = Base64.encodeToString(key, Base64.DEFAULT);
        AsimService.getPreferencesService().setPassword(s);
    }
}
