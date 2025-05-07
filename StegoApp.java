import java.awt.*;
import java.io.File;
import javax.swing.*;

/**
 * @brief Simple Stenography GUI, work in progress ;)
 */
public class StegoApp extends JFrame {
    private JTextField imagePathField, filePathField; 
    private final JTextField passwordField; 
    private final JButton encodeBtn, decodeBtn; 
    
    /**
     * @brief
     * 
     * @pre
     * @post
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
     * @brief
     * 
     * @param field
     * @param extensions
     * 
     * @pre
     * @post
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
     * @brief
     * 
     * @pre
     * @post
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
     * @brief
     * 
     * @pre
     * @post
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
    
    /**
     * @brief
     * 
     * @param args
     * 
     * @pre
     * @post
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StegoApp().setVisible(true));
    }
}