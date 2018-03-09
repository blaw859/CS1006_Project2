import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Dimension;

public class ProjectGUI extends JFrame {

    private static Image image = null;
    private int resolution2;
    private File file;
    private String filePath;
    private static int resolution = 0;
    public static JProgressBar progress;
    private static JLabel text3;

    public ProjectGUI() {
        initUI();
    }

    private void initUI() {

        Dimension monitor = Toolkit.getDefaultToolkit().getScreenSize();

        JFrame frame2 = new JFrame("New Image");
        frame2.setLocation((((monitor.width)/3)*2-(getSize().width)/2), 0);
        frame2.setVisible(false);
        frame2.setResizable(false);

        JPanel imagePanel = new JPanel();

        JPanel energyPanel = new JPanel();
        energyPanel.setBackground(Color.getHSBColor(194,194,163));
        energyPanel.setOpaque(true);

        setLocation((monitor.width/2)-(getSize().width/2) - 450, (monitor.height/2)-(getSize().height/2) - 200);
        setTitle("Image Carver");
        setResizable(false);
        setVisible(true);
        setSize(970,400);

        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.getHSBColor(150,93,90));
        topPanel.setOpaque(true);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.getHSBColor(200,94,105));

        JButton button = new JButton("Confirm selection");
        button.setVisible(false);

        JLabel text2 = new JLabel("No resolution selected");
        text2.setAlignmentX(JLabel.LEFT);

        JLabel text1 = new JLabel("No file selected");
        text1.setAlignmentX(JLabel.LEFT);

        text3 = new JLabel();
        text3.setAlignmentX(JLabel.LEFT);

        topPanel.add(text1);
        topPanel.add(text2);
        topPanel.add(text3);
        topPanel.add(button);
        topPanel.setVisible(true);
        topPanel.setBounds(5,12,200,100);
        topPanel.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        //bottomPanel.setBounds(0,300,200,200);
        bottomPanel.setAlignmentX(JLabel.RIGHT_ALIGNMENT);

        //Adds the slider which is used to select the new resolution
        //The max resolution is at 0, until an image is selected
        JSlider slider = new JSlider(0,0,0); //The slider is 0 size before an image is selected
        slider.setVisible(false);
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setBackground(Color.getHSBColor(150,93,90));
        slider.addChangeListener((ChangeEvent event) -> {
            resolution = slider.getValue();
            text2.setText("Horizontal Resolution: " + Integer.toString(resolution));
        });

        //This class will allow the user to select an image easily
        JFileChooser fileChooser = new JFileChooser();
        File currentDir = new File(System.getProperty("user.dir") + "/CS1006P2/src/images");
        fileChooser.setCurrentDirectory(currentDir);
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

                        JLabel energyLabel = new JLabel(new ImageIcon("CS1006P2/out/outputImage.png"));
                        energyLabel.setAlignmentY(LEFT_ALIGNMENT);

                        energyPanel.add(energyLabel);
                        energyPanel.setAlignmentY(JPanel.LEFT_ALIGNMENT);
                        energyPanel.setBounds(5, 5, image.getWidth(), image.getHeight());
                        energyPanel.setVisible(true);

                        imagePanel.add(imageLabel);
                        imagePanel.setAlignmentY(JPanel.RIGHT_ALIGNMENT);
                        imagePanel.setBounds(3,image.getHeight() + 5,image.getWidth(), image.getHeight());
                        imagePanel.setVisible(true);

                        frame2.add(imagePanel);
                        frame2.add(energyPanel);
                        frame2.setSize(image.getWidth() + 20, image.getHeight()*2 + 50);

                        frame2.setVisible(true);
                    }
                }
            }
        });

        topPanel.add(slider);
        bottomPanel.setBounds(0,200,200,200);

        add(topPanel);
        add(bottomPanel);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (file != null) {
                    int verticalSeams = image.getWidth() - resolution;

                    progress.setValue(0);
                    progress.setMaximum(verticalSeams - 1);
                    progress.setVisible(true);
                    progress.setMinimum(0);
                    topPanel.setBounds(5,12,200,200);
                    SeamCarver.setEnergyMatrices(image);
                    double[][] verticalEnergyMatrix = image.getEnergyMatrix();
                    //BufferedImage outputImage = image.removeSeams(SeamCarver.findSeams(SeamCarver.verticalWeights, verticalSeams,verticalEnergyMatrix));
                    SeamCarver.findSeams(SeamCarver.verticalWeights,verticalSeams,verticalEnergyMatrix);
                    BufferedImage outputImage = image.RGBArrayToImage();
                    JFrame frame3 = new JFrame("Carved Image");

                    JPanel newImagePanel = new JPanel();

                    JLabel newImageLabel = new JLabel(new ImageIcon(outputImage));
                    newImageLabel.setSize(outputImage.getWidth(), outputImage.getHeight());

                    newImagePanel.add(newImageLabel);

                    frame3.setSize(outputImage.getWidth(), outputImage.getHeight());
                    frame3.add(newImageLabel);
                    frame3.setVisible(true);

                    try {
                        File carvedImage = new File("CS1006P2/out/carvedImage.png");
                        ImageIO.write(outputImage, "png", carvedImage);
                    } catch (IOException exception) {
                        System.out.println("Error: " + exception);
                    }
                }
            }
        });

        progress = new JProgressBar();
        progress.setStringPainted(true);
        progress.setValue(0);
        progress.setVisible(false);
        progress.setMinimum(0);
        progress.setName("Carving Progress");

        topPanel.add(progress);
        //topPanel.setBounds(5,12,200,200);

        JSlider slider2 = new JSlider(0,0,0); //The slider is 0 size before an image is selected
        slider2.setVisible(false);
        slider2.setOrientation(JSlider.VERTICAL);
        slider2.setMinorTickSpacing(1);
        slider2.setMajorTickSpacing(10);
        slider2.setPaintTicks(true);
        slider2.setBackground(Color.getHSBColor(150,93,90));
        slider2.addChangeListener((ChangeEvent event) -> {
            resolution2 = slider.getValue();
            text3.setText("Vertical Resolution: " + Integer.toString(resolution));
        });


        //for (int i = 0; i < 5; i++) {
        //    incrementProgress();
            //progress.setValue(progress.getValue() + 1);
        //}


        //JPanel rightPanel = new JPanel();

        //rightPanel.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
        //rightPanel.add(text3);
        //rightPanel.add(progress);
        //add(rightPanel);
    }

    public static void incrementProgress() {
        int val = progress.getValue();
        val += 1;
        if (val >= image.getWidth() - resolution) {
            text3.setText("Seam Carving complete!");
            return;
        }
        progress.setValue(val);
        progress.repaint();
    }

    public class UpdateProgress implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int val = progress.getValue();
            if (val >= resolution) {
                text3.setText("Seam Carving complete!");
                return;
            }
            progress.setValue(val++);
            progress.repaint();
        }
    }


}
