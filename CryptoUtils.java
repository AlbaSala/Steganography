import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @brief
 */
public class CryptoUtils {
    /**
     * @brief
     * 
     * @param password
     * @throws Exception
     * 
     * @pre
     * @post
     */
    public static SecretKey getAESKey(String password) throws Exception {
        byte[] key = password.getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        key = sha.digest(key); // hash to 256-bit key
        return new SecretKeySpec(Arrays.copyOf(key, 16), "AES"); // AES-128
    }
    
    /**
     * @brief
     * 
     * @param data
     * @param password
     * 
     * @pre
     * @post
     */
    public static byte[] encrypt(byte[] data, String password) throws Exception {
        SecretKey key = getAESKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }
    
    /**
     * @brief
     * 
     * @param encrypted
     * @param password
     * 
     * @throws Exception
     * 
     * @pre 
     * @post
     */
    public static byte[] decrypt(byte[] encrypted, String password) throws Exception {
        SecretKey key = getAESKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encrypted);
    }
}
