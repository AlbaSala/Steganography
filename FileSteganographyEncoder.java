import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class FileSteganographyEncoder {
    public static void encode(String inputImagePath, String outputImagePath, File fileToHide, String password) throws Exception {
        BufferedImage image = ImageIO.read(new File(inputImagePath));

        byte[] fileBytes = readAllBytes(fileToHide);
        byte[] encrypted = CryptoUtils.encrypt(fileBytes, password);

        byte[] len = intToBytes(encrypted.length);

        int msgIndex = 0;
        int bitIndex = 0;
        byte[] fullMessage = new byte[len.length + encrypted.length];
        System.arraycopy(len, 0, fullMessage, 0, len.length);
        System.arraycopy(encrypted, 0, fullMessage, len.length, encrypted.length);

        outer: for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (msgIndex >= fullMessage.length) break outer;

                int pixel = image.getRGB(x, y);
                int blue = pixel & 0xFF;
                int bit = (fullMessage[msgIndex] >> (7 - bitIndex)) & 1;

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

        ImageIO.write(image, "png", new File(outputImagePath));
        System.out.println("Encrypted file embedded in image: " + outputImagePath);
    }

    private static byte[] intToBytes(int val) {
        return new byte[] {
            (byte) ((val >> 24) & 0xFF),
            (byte) ((val >> 16) & 0xFF),
            (byte) ((val >> 8) & 0xFF),
            (byte) (val & 0xFF)
        };
    }

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
