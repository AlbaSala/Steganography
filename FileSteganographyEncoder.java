import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

/**
 * @brief Provides functionality to encode a file into an image using LSB (Least Significant Bit) steganography.
 *        The file is encrypted before being embedded into the image.
 */
public class FileSteganographyEncoder {

    /**
     * @brief Embeds an encrypted file into an image using LSB steganography.
     *        The file is first encrypted using the provided password and then the encrypted data
     *        is embedded into the least significant bits of the image's pixels.
     * 
     * @param inputImagePath Path to the input image where the file will be embedded.
     * @param outputImagePath Path to the output image that will contain the embedded file.
     * @param fileToHide The file that will be encrypted and embedded into the image.
     * @param password The password used to encrypt the file before embedding.
     * 
     * @throws Exception If there are issues reading the image, encrypting the file, or embedding the data.
     * 
     * @pre 
     * - inputImagePath points to a valid image file.
     * - outputImagePath is a valid file path where the new image will be saved.
     * - fileToHide exists and can be read.
     * - password is not null or empty.
     * 
     * @post 
     * - The input image has been modified to contain the encrypted file, and the new image is saved to the output path.
     * - The file is successfully encrypted and embedded into the least significant bits of the image's pixels.
     */
    public static void encode(String inputImagePath, String outputImagePath, File fileToHide, String password) throws Exception {
        BufferedImage image = ImageIO.read(new File(inputImagePath));

        // Read the file and encrypt it using the password
        byte[] fileBytes = readAllBytes(fileToHide);
        byte[] encrypted = CryptoUtils.encrypt(fileBytes, password);

        // Convert the length of the encrypted data into a 4-byte array
        byte[] len = intToBytes(encrypted.length);

        // Prepare the full message to embed (length + encrypted content)
        int msgIndex = 0;
        int bitIndex = 0;
        byte[] fullMessage = new byte[len.length + encrypted.length];
        System.arraycopy(len, 0, fullMessage, 0, len.length);
        System.arraycopy(encrypted, 0, fullMessage, len.length, encrypted.length);

        // Embed the fullMessage into the image using LSB steganography
        outer: for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (msgIndex >= fullMessage.length) break outer;

                int pixel = image.getRGB(x, y);
                int blue = pixel & 0xFF;
                int bit = (fullMessage[msgIndex] >> (7 - bitIndex)) & 1;

                // Modify the blue component of the pixel to embed the bit
                blue = (blue & 0xFE) | bit;
                pixel = (pixel & 0xFFFF00) | blue;
                image.setRGB(x, y, pixel);

                bitIndex++;
                if (bitIndex == 8) {
                    bitIndex = 0;
                    msgIndex++;
                }
            }
        }

        // Save the image with the embedded file
        ImageIO.write(image, "png", new File(outputImagePath));
        System.out.println("Encrypted file embedded in image: " + outputImagePath);
    }

    /**
     * @brief Converts an integer into a 4-byte array (big-endian format).
     * 
     * @param val The integer value to convert.
     * @return A byte array representing the integer.
     */
    private static byte[] intToBytes(int val) {
        return new byte[] {
            (byte) ((val >> 24) & 0xFF),
            (byte) ((val >> 16) & 0xFF),
            (byte) ((val >> 8) & 0xFF),
            (byte) (val & 0xFF)
        };
    }

    /**
     * @brief Reads all bytes from a file and returns them as a byte array.
     * 
     * @param file The file to read.
     * @return A byte array containing the file's content.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    private static byte[] readAllBytes(File file) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             InputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int n;
            while ((n = is.read(buffer)) != -1) {
                baos.write(buffer, 0, n);
            }
            return baos.toByteArray();
        }
    }
}
