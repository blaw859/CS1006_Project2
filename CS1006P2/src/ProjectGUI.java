import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileFilter;
import javax.imageio.ImageIO;
import java.awt.Color;

public class ProjectGUI extends JFrame {

    private Image image = null;
    private File file;
    private String filePath;
    private int resolution;
    private int minResolution = 10;

    public ProjectGUI() {
        initUI();
    }

    private void initUI() {

        JFrame frame2 = new JFrame("New Image");
        frame2.setVisible(false);
        frame2.setBackground(Color.GREEN);
        frame2.getContentPane().setBackground(Color.GREEN);

        JPanel imagePanel = new JPanel();

        JPanel energyPanel = new JPanel();
        energyPanel.setBackground(Color.GREEN);
        energyPanel.setOpaque(true);

        setTitle("Image Carver");
        setLocationRelativeTo(null);
        setVisible(true);
        setSize(1000,400);

        JPanel topPanel = new JPanel();

        JPanel bottomPanel = new JPanel();

        JButton button = new JButton("Confirm selection");
        button.setVisible(false);

        JLabel text2 = new JLabel("No resolution selected");
        text2.setAlignmentX(JLabel.LEFT);

        JLabel text1 = new JLabel("No file selected");
        text1.setAlignmentX(JLabel.LEFT);

        topPanel.add(text1);
        topPanel.add(text2);
        topPanel.add(button);
        topPanel.setVisible(true);
        topPanel.setBounds(0,0,200,100);
        topPanel.setAlignmentY(JLabel.TOP_ALIGNMENT);

        bottomPanel.setBounds(0,300,200,200);
        bottomPanel.setAlignmentY(JLabel.RIGHT_ALIGNMENT);


        //Adds the slider which is used to select the new resolution
        //The max resolution is at 0, until an image is selected
        JSlider slider = new JSlider(0,0,0); //The slider is 0 size before an image is selected
        slider.setVisible(false);
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.addChangeListener((ChangeEvent event) -> {
            resolution = slider.getValue();
            text2.setText("Selected Resolution: " + Integer.toString(resolution));
        });

        //This class will allow the user to select an image easily
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setVisible(true);
        bottomPanel.add(fileChooser);
        fileChooser.setBounds(100,100,200,200);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        fileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(ProjectGUI.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile();
                    filePath = file.getAbsolutePath();
                    if (filePath.contains("src")) {
                        filePath = filePath.substring(filePath.indexOf("src") + 3, filePath.length());
                        //filePath = filePath.toLowerCase();
                    }
                    if (file.exists() && (filePath.endsWith(".png")
                            || filePath.endsWith(".jpg")
                            || filePath.endsWith(".jpeg")
                            || filePath.endsWith(".JPG"))) {

                        text1.setText(file.getName() + " is selected");
                        try {
                            image = new Image(filePath);
                        } catch (IOException exception) {
                            System.out.println("Error " + exception);
                        }
                        slider.setMaximum(image.getWidth());
                        slider.setVisible(true);
                        button.setVisible(true);
                        JLabel imageLabel = new JLabel(new ImageIcon(image.getBufferedImage()));
                        BufferedImage energyImage = null;
                        try {
                            energyImage = ImageIO.read(image.getEnergyMatrixImage());
                        } catch (IOException exception) {
                            System.out.println("Error: " + exception);
                        }
                        //JLabel energyLabel = new JLabel(new ImageIcon(image.getEnergyMatrixImage()));
                        JLabel energyLabel = new JLabel(new ImageIcon("CS1006P2/out/outputImage.png"));
                        energyPanel.add(energyLabel);
                        energyPanel.setAlignmentY(JPanel.BOTTOM_ALIGNMENT);
                        energyPanel.setBounds(0, image.getHeight(), image.getWidth(), image.getHeight());
                        energyPanel.setVisible(true);
                        imagePanel.add(imageLabel);
                        imagePanel.setAlignmentY(JPanel.TOP_ALIGNMENT);
                        frame2.add(imagePanel);
                        frame2.setSize(image.getWidth() + 20, image.getHeight()*2 + 20);

                        frame2.setVisible(true);
                    }
                }
            }
        });

        topPanel.add(slider);
        bottomPanel.setBounds(0,200,200,200);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (file != null && resolution > minResolution) { //Minimum resolution is currently at 10px
                    try {
                        image = new Image(filePath);
                    } catch (IOException exception) {
                        System.out.println("Error " + exception); //Needs to be changed
                    }
                    SeamCarver.initializeWeights(image);
                    image.carve(image.getWidth()-resolution); //Method not yet implemented
                }
            }
        });

        add(topPanel);
        add(bottomPanel);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

}
