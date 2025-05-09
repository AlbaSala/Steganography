import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @brief Utility class for AES encryption and decryption using a password-derived key.
 */
public class CryptoUtils {
    /**
     * @brief Derives a 128-bit AES key from the given password using SHA-256 hashing.
     * 
     * @param password The password from which to derive the AES key.
     * @throws Exception If the SHA-256 algorithm or UTF-8 encoding is not supported.
     * 
     * @pre password is not null and not empty.
     * @post Returns a valid SecretKey suitable for AES encryption/decryption.
     */
    public static SecretKey getAESKey(String password) throws Exception {
        byte[] key = password.getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        key = sha.digest(key); // hash to 256-bit key
        return new SecretKeySpec(Arrays.copyOf(key, 16), "AES"); // AES-128
    }
    
    /**
     * @brief Encrypts the given data using AES encryption with a key derived from the provided password.
     * 
     * @param data The plaintext data to encrypt.
     * @param password The password used to derive the encryption key.
     * 
     * @pre data is not null; password is not null and not empty.
     * @post Returns a byte array that represents the AES-encrypted form of the input data.
     */
    public static byte[] encrypt(byte[] data, String password) throws Exception {
        SecretKey key = getAESKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }
    
    /**
     * @brief Decrypts AES-encrypted data using a key derived from the provided password.
     * 
     * @param encrypted The AES-encrypted data to decrypt.
     * @param password The password used to derive the decryption key.
     * 
     * @throws Exception If decryption fails or the password is incorrect.
     * 
     * @pre encrypted is a valid AES-encrypted byte array created using the same password.
     * @post Returns the original plaintext data if the correct password is used.
     */
    public static byte[] decrypt(byte[] encrypted, String password) throws Exception {
        SecretKey key = getAESKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encrypted);
    }
}
