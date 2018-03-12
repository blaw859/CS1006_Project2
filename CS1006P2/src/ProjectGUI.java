import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
    private int firstResolution;
    private int width2;
    private int width;
    private int n;
    private int firstResolution2;
    private int height2;
    private int height;
    private boolean add = true;
    private BufferedImage carvedBufferedImage = null;

    public ProjectGUI() {
        initUI();
    }

    private void initUI() {

        Dimension monitor = Toolkit.getDefaultToolkit().getScreenSize();

        //JFrame frame3 = new JFrame("Carved Image");

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
        setBackground(Color.getHSBColor(200,94,105));
        setResizable(false);
        setVisible(true);
        setSize(970,400);

        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.getHSBColor(150,93,91));
        topPanel.setOpaque(true);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.getHSBColor(200,90,105));

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
        topPanel.setBounds(5,12,200,52);
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
        slider.setBackground(Color.getHSBColor(150,93,91));
        slider.addChangeListener((ChangeEvent event) -> {
            resolution = slider.getValue();
            text2.setText("Horizontal Resolution: " + Integer.toString(resolution));
        });

        JTextArea textV = new JTextArea();
        textV.setVisible(false);
        textV.setEditable(true);
        textV.setLineWrap(true);
        textV.setWrapStyleWord(true);
        textV.setBounds(0,90,180,100);
        textV.setAlignmentX(JLabel.LEFT);
        textV.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!textV.getText().equals("")) {
                    try {
                    resolution2 = Integer.parseInt(textV.getText());
                    } catch (NumberFormatException exception) {
                        return;
                    }
                    text3.setText("Vertical Resolution: " + Integer.toString(resolution2));
                } else {
                    resolution2 = image.getWidth();
                }
                text3.setText("Vertical Resolution: " + Integer.toString(resolution2));
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!textV.getText().equals("")) {
                    try {
                    resolution2 = Integer.parseInt(textV.getText());
                    } catch (NumberFormatException exception) {
                        return;
                    }
                    text3.setText("Vertical Resolution: " + Integer.toString(resolution2));
                } else {
                    resolution2 = image.getWidth();
                }
                text3.setText("Vertical Resolution: " + Integer.toString(resolution2));
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        textV.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                textV.setText("");
            }
        });

        JTextArea textH = new JTextArea();
        textH.setVisible(false);
        textH.setEditable(true);
        textH.setLineWrap(true);
        textH.setWrapStyleWord(true);
        textH.setBounds(0,50,180,100);
        textH.setAlignmentX(JLabel.LEFT);
        //textH.setBounds(5,100,30, 10);
        textH.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!textH.getText().equals("")) {
                    try {
                        resolution = Integer.parseInt(textH.getText());
                    } catch (NumberFormatException exception) {
                        return;
                    }
                } else {
                    resolution = image.getWidth();
                }
                text2.setText("Horizontal Resolution: " + Integer.toString(resolution));
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!textH.getText().equals("")) {
                    try {
                    resolution = Integer.parseInt(textH.getText());
                    } catch (NumberFormatException exception) {
                        return;
                    }
                } else {
                    resolution = image.getWidth();
                }
                text2.setText("Horizontal Resolution: " + Integer.toString(resolution));
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        textH.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                textH.setText("");
            }
        });

        //bottomPanel.add(textH);
        //bottomPanel.add(textV);

        JSlider slider2 = new JSlider(0,0,0); //The slider is 0 size before an image is selected
        slider2.setVisible(false);
        slider2.setOrientation(JSlider.VERTICAL);
        slider2.setMinorTickSpacing(1);
        slider2.setMajorTickSpacing(10);
        slider2.setPaintTicks(true);
        slider2.setBackground(Color.getHSBColor(150,93,91));
        slider2.addChangeListener((ChangeEvent event) -> {
            resolution2 = slider2.getValue();
            text3.setText("Vertical Resolution: " + Integer.toString(resolution2));
        });

        JButton mode = new JButton("Addition mode");
        mode.setVisible(false);
        mode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (add) {
                    add = false;
                    mode.setText("Removal mode");
                    textH.setText("Horizontal");
                    textV.setText("Vertical");
                    textV.setVisible(true);
                    textH.setVisible(true);
                    slider.setVisible(false);
                    slider2.setVisible(false);
                } else {
                    add = true;
                    mode.setText("Addition mode");
                    textV.setVisible(false);
                    textH.setVisible(false);
                    slider.setVisible(true);
                    slider2.setVisible(true);
                }

            }
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
                    if (file.exists() &&
                            (filePath.endsWith(".png")
                            || filePath.endsWith(".jpg")
                            || filePath.endsWith(".jpeg")
                            || filePath.endsWith(".JPG"))) {

                        text1.setText(file.getName() + " is selected");
                        try {
                            image = new Image(filePath);
                        } catch (IOException exception) {
                            System.out.println("Error " + exception);
                        }
                        width = image.getWidth();
                        height = image.getHeight();
                        topPanel.setBounds(5,12,200,300);
                        slider.setMaximum(image.getWidth());
                        slider.setValue(image.getWidth());
                        slider.setVisible(true);
                        slider2.setVisible(true);
                        slider2.setMaximum(image.getHeight());
                        slider2.setValue(image.getHeight());
                        button.setVisible(true);
                        mode.setVisible(true);
                        bottomPanel.remove(fileChooser);
                        fileChooser.setVisible(false);
                        bottomPanel.add(slider2);
                        bottomPanel.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
                        bottomPanel.revalidate();
                        setSize(480,250);
                        repaint();

                        JLabel imageLabel = new JLabel(new ImageIcon(image.getBufferedImage()));

                        JLabel energyLabel = new JLabel(new ImageIcon("CS1006P2/out/outputImage.png"));
                        energyLabel.setAlignmentX(LEFT_ALIGNMENT);

                        energyPanel.add(energyLabel);
                        energyPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
                        energyPanel.setBounds(5, 5, image.getWidth(), image.getHeight());
                        energyPanel.setVisible(true);

                        imagePanel.add(imageLabel);
                        imagePanel.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
                        imagePanel.setBounds(3,image.getHeight() + 5,image.getWidth(), image.getHeight());
                        imagePanel.setVisible(true);

                        frame2.add(imagePanel);
                        frame2.add(energyPanel);
                        frame2.setSize(image.getWidth() + 20, image.getHeight()*2 + 50);

                        frame2.setVisible(true);
                    } else {
                        text1.setText("Not a valid filetype");
                    }
                }
            }
        });

        topPanel.add(slider);
        topPanel.add(textH);
        topPanel.add(textV);
        topPanel.add(mode);
        bottomPanel.setBounds(0,200,200,200);

        add(topPanel);
        add(bottomPanel);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        button.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 if (file != null) {
                     int verticalSeams;
                     int horizontalSeams;
                     mode.setVisible(false);
                     if (n > 0) {
                         horizontalSeams = firstResolution2 - resolution2;
                         verticalSeams = firstResolution - resolution;
                         firstResolution = resolution;
                     } else {
                         width2 = width - resolution;
                         firstResolution = resolution;
                         firstResolution2 = resolution2;
                         height2 = height - resolution2;
                         horizontalSeams = height2;
                         verticalSeams = width2;
                         n++;
                         slider.setMaximum(firstResolution);
                         slider2.setMaximum(firstResolution2);
                     }
                     //if (!(add && n > 1 && (firstResolution < resolution || firstResolution2 < resolution2))) {

                         //topPanel.setBounds(5, 12, 200, 200);
                         //SeamCarver.setEnergyMatrices(image);
                     double[][] verticalEnergyMatrix = image.getEnergyMatrix();
                     if (horizontalSeams >= 0 && verticalSeams >= 0) {
                         carvedBufferedImage = image.carveImage(horizontalSeams, verticalSeams);
                     } else if(horizontalSeams < 0 && verticalSeams > 0) {
                         image = new Image(image.carveImage(0, verticalSeams));
                         horizontalSeams *= -1;
                         carvedBufferedImage = image.addToImageHorizontal(horizontalSeams);
                     } else if (horizontalSeams > 0 && verticalSeams < 0) {
                         image = new Image(image.carveImage(horizontalSeams,0));
                         verticalSeams *= -1;
                         carvedBufferedImage = image.addToImageVertical(verticalSeams);
                     } else {
                         verticalSeams *= -1;
                         horizontalSeams *= -1;
                         image = new Image(image.addToImageHorizontal(horizontalSeams));
                         //carvedBufferedImage = image.RGBArrayToImage(image.addSeams(verticalSeams, image.getEnergyMatrix(), image.getCurrentRGBArray()));
                         carvedBufferedImage = image.addToImageVertical(verticalSeams);
                     }

                     //BufferedImage outputImage = image.RGBArrayToImage();
                     slider.setMaximum(width);
                     slider2.setMaximum(height);
                     mode.setVisible(true);
                     JFrame frame3 = new JFrame("Carved Image");

                     JPanel newImagePanel = new JPanel();
                     JLabel newImageLabel = new JLabel(new ImageIcon(carvedBufferedImage));

                     newImageLabel.setSize(carvedBufferedImage.getWidth(), carvedBufferedImage.getHeight());
                     frame3.add(newImageLabel);
                     //frame3.setVisible(true);
                     frame3.setSize(image.getWidth() + 20, image.getHeight() * 3 + 50);
                     frame3.pack();
                     newImagePanel.add(newImageLabel);

                     frame3.setSize(carvedBufferedImage.getWidth(), carvedBufferedImage.getHeight());
                     frame3.add(newImageLabel);
                     frame3.setVisible(true);

                     try {
                         File carvedImage = new File("CS1006P2/out/carvedImage.png");
                         ImageIO.write(carvedBufferedImage, "png", carvedImage);
                     } catch (IOException exception) {
                         System.out.println("Error: " + exception);
                     }

                     image = new Image(carvedBufferedImage);

                 //} else {
                     /*
                     text2.setText("Resolution is too small");
                     //verticalSeams = image.getWidth() - resolution;
                     //horizontalSeams = image.getHeight() - resolution2;
                     //progress.setValue(0);
                     //progress.setMaximum(verticalSeams - 1);
                     //progress.setVisible(true);
                     //progress.setMinimum(0);
                     topPanel.setBounds(5, 12, 200, 200);
                     //SeamCarver.setEnergyMatrices(image);
                     double[][] verticalEnergyMatrix = image.getEnergyMatrix();
                     //BufferedImage outputImage = image.removeSeams(SeamCarver.findSeams(SeamCarver.verticalWeights, verticalSeams,verticalEnergyMatrix));
                     //SeamCarver.findSeams(SeamCarver.verticalWeights,verticalSeams,verticalEnergyMatrix);
                     //BufferedImage outputImage = image.carveImage(horizontalSeams, verticalSeams);
                     BufferedImage outputImage = image.addToImage(500);
                     JFrame frame3 = new JFrame("Carved Image");

                     JPanel newImagePanel = new JPanel();
                     JLabel newImageLabel = new JLabel(new ImageIcon(outputImage));
                     newImageLabel.setSize(outputImage.getWidth(), outputImage.getHeight());
                     frame3.add(newImageLabel);
                     frame3.setSize(image.getWidth() + 20, image.getHeight() * 3 + 50);
                     frame3.pack();
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
                     */
                 //}
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
    }

    /*
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
    }*/

}
