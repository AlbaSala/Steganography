import java.awt.*;
import java.io.File;
import javax.swing.*;

/**
 * @brief A GUI application for encoding and decoding files using steganography. 
 *        The application allows users to hide a file within an image and extract it later using a password.
 */
public class StegoApp extends JFrame {
    private JTextField imagePathField, filePathField; 
    private final JTextField passwordField; 
    private final JButton encodeBtn, decodeBtn; 
    
   /**
     * @brief Initializes the GUI components and sets up the layout for encoding and decoding actions.
     * 
     * @pre 
     * - The application is instantiated and the GUI components are initialized with proper layout.
     * 
     * @post 
     * - A fully functional GUI is displayed, allowing the user to choose an image, a file to hide, and provide a password for encoding and decoding actions.
     */
    public StegoApp() {
        setTitle("Java Steganography Tool");
        setSize(500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 1));

        imagePathField = new JTextField();
        filePathField = new JTextField();
        passwordField = new JTextField();

        JButton browseImage = new JButton("Select Image");
        browseImage.addActionListener(e -> chooseFile(imagePathField, "png", "jpg", "jpeg"));

        JButton browseFile = new JButton("Select File to Hide");
        browseFile.addActionListener(e -> chooseFile(filePathField, "*"));

        encodeBtn = new JButton("Encode");
        encodeBtn.addActionListener(e -> encodeAction());

        decodeBtn = new JButton("Decode");
        decodeBtn.addActionListener(e -> decodeAction());

        add(browseImage); add(imagePathField);
        add(browseFile); add(filePathField);
        add(new JLabel("Password:")); add(passwordField);
        add(encodeBtn); add(decodeBtn);
    }
    
    /**
     * @brief Opens a file chooser dialog to select a file and sets the selected file's path in the provided text field.
     * 
     * @param field The text field where the selected file path will be displayed.
     * @param extensions The file extensions that are allowed for selection (e.g., "png", "jpg").
     * 
     * @pre 
     * - A valid file chooser dialog is created.
     * 
     * @post 
     * - The user selects a file, and the path is set in the provided text field.
     */
    private void chooseFile(JTextField field, String... extensions) {
        JFileChooser chooser = new JFileChooser();
        if (extensions.length > 0) {
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Supported files", extensions));
        }
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            field.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    /**
     * @brief Handles the encoding process. It reads the user input, encrypts the file, embeds it into the image, 
     *        and saves the output image.
     * 
     * @pre 
     * - The user has selected a valid image and file to hide, and provided a password.
     * 
     * @post 
     * - The input image has been modified to contain the encrypted file and saved as a new image.
     * - A success message is shown to the user.
     */
    private void encodeAction() {
        try {
            //Paths
            String imgPath = imagePathField.getText();
            String filePath = filePathField.getText();
            String password = passwordField.getText();

            File file = new File(filePath);
            String output = "stego_output.png"; //Default name of output image

            FileSteganographyEncoder.encode(imgPath, output, file, password);
            JOptionPane.showMessageDialog(this, "File hidden successfully!\nSaved as: " + output);
        } catch (Exception ex) {
            System.err.println("Error during decoding: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Encoding failed: " + ex.getMessage());
        }
    }
    
    /**
     * @brief Handles the decoding process. It extracts the hidden file from the image using the provided password 
     *        and saves the extracted file to the disk.
     * 
     * @pre 
     * - The user has selected a valid image containing the hidden file and provided the correct password.
     * 
     * @post 
     * - The hidden file has been successfully extracted and saved to disk. A success message is shown to the user.
     */
    private void decodeAction() {
        try {
            String imgPath = imagePathField.getText();
            String password = passwordField.getText();
            String output = "recovered_file"; //Default name of output file

            FileSteganographyDecoder.decode(imgPath, output, password);
            JOptionPane.showMessageDialog(this, "File extracted successfully!\nSaved as: " + output);
        } catch (Exception ex) {
            System.err.println("Error during decoding: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Decoding failed: " + ex.getMessage());
        }
    }
    
    **
     * @brief The main method that starts the application by initializing the StegoApp GUI.
     * 
     * @param args Command-line arguments (not used in this application).
     * 
     * @pre 
     * - The application is ready to run in a graphical environment.
     * 
     * @post 
     * - The GUI is displayed, allowing the user to encode and decode files with steganography.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StegoApp().setVisible(true));
    }
}
