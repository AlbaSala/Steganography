
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;

public class FileSteganographyDecoder {
    public static void decode(String imagePath, String outputFilePath, String password) throws Exception {
        BufferedImage image = ImageIO.read(new File(imagePath));

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

        int length = ((lenBytes[0] & 0xFF) << 24) |
                     ((lenBytes[1] & 0xFF) << 16) |
                     ((lenBytes[2] & 0xFF) << 8) |
                     (lenBytes[3] & 0xFF);

        byte[] encrypted = new byte[length];
        bitIndex = 0;
        byteIndex = 0;

        outer: for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (i-- > 0) continue; // skip header pixels

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

        byte[] decrypted = CryptoUtils.decrypt(encrypted, password);
        try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            fos.write(decrypted);
        }
        System.out.println("File extracted and decrypted: " + outputFilePath);
    }
}
