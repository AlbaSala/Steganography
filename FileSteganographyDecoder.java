
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;

/**
 * @brief Provides functionality to extract and decrypt hidden files from an image using LSB steganography.
 */
public class FileSteganographyDecoder {
    /**
     * @brief Decodes a hidden file embedded in the least significant bits (LSBs) of an image's pixel data,
     *        decrypts it using a password, and writes the result to an output file.
     * 
     * @param imagePath Path to the image containing the hidden, encrypted file.
     * @param outputFilePath Path where the decrypted output file should be saved.
     * @param password Password used to decrypt the hidden content.
     * 
     * @throws Exception If there are issues reading the image, extracting the data, decrypting, or writing the output file.
     * 
     * @pre 
     * - imagePath points to a valid image file with embedded, encrypted data in LSB format.
     * - The hidden data must start with a 32-bit (4-byte) header indicating the length of the encrypted content.
     * - password is the correct key used during encryption.
     * 
     * @post 
     * - Extracts encrypted bytes from the image, decrypts them using the given password,
     *   and saves the original file content to the specified output path.
     * - If successful, the file is fully recovered and stored on disk.
     */
    public static void decode(String imagePath, String outputFilePath, String password) throws Exception {
        BufferedImage image = ImageIO.read(new File(imagePath));
        
        // Read 4 bytes (32 bits) from LSBs to get the length of the encrypted data
        byte[] lenBytes = new byte[4];
        int bitIndex = 0, byteIndex = 0;

        int i = 0;
        for (int y = 0; y < image.getHeight() && byteIndex < 4; y++) {
            for (int x = 0; x < image.getWidth() && byteIndex < 4; x++) {
                int blue = image.getRGB(x, y) & 0xFF;
                int bit = blue & 1;
                lenBytes[byteIndex] = (byte)((lenBytes[byteIndex] << 1) | bit);
                bitIndex++;
                if (bitIndex == 8) {
                    bitIndex = 0;
                    byteIndex++;
                }
                i++;
            }
        }

        // Combine bytes to get length of hidden data
        int length = ((lenBytes[0] & 0xFF) << 24) |
                     ((lenBytes[1] & 0xFF) << 16) |
                     ((lenBytes[2] & 0xFF) << 8) |
                     (lenBytes[3] & 0xFF);

        // Extract the encrypted data bits
        byte[] encrypted = new byte[length];
        bitIndex = 0;
        byteIndex = 0;

        outer: for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (i-- > 0) continue; // skip the pixels used for the length header

                int blue = image.getRGB(x, y) & 0xFF;
                int bit = blue & 1;
                encrypted[byteIndex] = (byte)((encrypted[byteIndex] << 1) | bit);
                bitIndex++;
                if (bitIndex == 8) {
                    bitIndex = 0;
                    byteIndex++;
                    if (byteIndex == length) {
                        break outer;
                    }
                }
            }
        }

        // Decrypt and save the extracted data
        byte[] decrypted = CryptoUtils.decrypt(encrypted, password);
        try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            fos.write(decrypted);
        }
        System.out.println("File extracted and decrypted: " + outputFilePath);
    }
}
