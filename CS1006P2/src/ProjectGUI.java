import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JSlider;
import javax.swing.JFileChooser;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Dimension;

public class ProjectGUI extends JFrame {

    private Image image = null;
    private int resolution2 = 0;
    private File file;
    private String filePath;
    private int resolution = 0;
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

        //The dimensions of the user's monitor is used to specify JFrame position
        Dimension monitor = Toolkit.getDefaultToolkit().getScreenSize();

        //This window displays the energy matrix and the selected image
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

        //The button initialises the seam carving or addition
        JButton button = new JButton("Confirm selection");
        button.setVisible(false);

        //This label explains the current horizontal resolution
        JLabel text2 = new JLabel("No resolution selected");
        text2.setAlignmentX(JLabel.LEFT);

        //This label explains the current selected file
        JLabel text1 = new JLabel("No file selected");
        text1.setAlignmentX(JLabel.LEFT);

        //This label explains the vertical resolution
        JLabel text3 = new JLabel();
        text3.setAlignmentX(JLabel.LEFT);

        topPanel.add(text1);
        topPanel.add(text2);
        topPanel.add(text3);
        topPanel.add(button);
        topPanel.setVisible(true);
        topPanel.setBounds(5,12,200,52);
        topPanel.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        bottomPanel.setAlignmentX(JLabel.RIGHT_ALIGNMENT);

        //Adds the slider which is used to select the new resolution
        //The max resolution is at 0, until an image is selected
        JSlider slider = new JSlider(0,0,0);
        slider.setVisible(false);
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setBackground(Color.getHSBColor(150,93,91));
        slider.addChangeListener((ChangeEvent event) -> {
            resolution = slider.getValue();
            text2.setText("Horizontal Resolution: " + Integer.toString(resolution));
        });

        //Textbox for inputting vertical resolution
        JTextArea textV = new JTextArea();
        textV.setVisible(false);
        textV.setEditable(true);
        textV.setLineWrap(true);
        textV.setWrapStyleWord(true);
        textV.setBounds(0,90,180,100);
        textV.setAlignmentX(JLabel.LEFT);

        //A documentlistener is used to specify what resolution to expand to based
        //on the text in the editable textboxes
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
                    resolution2 = image.getHeight();
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
                    resolution2 = image.getHeight();
                }
                text3.setText("Vertical Resolution: " + Integer.toString(resolution2));
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        //The text in the textboxes are removed on the box is selected
        textV.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                textV.setText("");
            }
        });

        //Textbox for inputting horizontal resolution
        JTextArea textH = new JTextArea();
        textH.setVisible(false);
        textH.setEditable(true);
        textH.setLineWrap(true);
        textH.setWrapStyleWord(true);
        textH.setBounds(0,50,180,100);
        textH.setAlignmentX(JLabel.LEFT);

        //A documentlistener is used to specify what resolution to expand to based
        //on the text in the editable textboxes
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

        //The text in the textboxes are removed on the box is selected
        textH.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                textH.setText("");
            }
        });

        //The slider is used to select the vertical resolution
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

        //The mode button alternates between the slider and text input modes
        JButton mode = new JButton("Addition mode");
        mode.setVisible(false);
        mode.addActionListener(e -> {
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

        });

        //This class will allow the user to select an image easily
        JFileChooser fileChooser = new JFileChooser();

        //Only image files are allowed
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "png", "jpg", "JPG", "jpeg");

        fileChooser.setFileFilter(filter);
        fileChooser.setVisible(true);
        bottomPanel.add(fileChooser);
        fileChooser.setBounds(100,100,200,200);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        //This process is run once a file has been selected
        fileChooser.addActionListener(e -> {
            int returnVal = fileChooser.showOpenDialog(ProjectGUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                file = new File(fileChooser.getCurrentDirectory() + "/" + fileChooser.getName(fileChooser.getSelectedFile()));
                filePath = file.getAbsolutePath();
                filePath = filePath.replaceAll(" ", "");
                text1.setText(file.getName() + " is selected");

                try {
                    image = new Image(file); //An image object is created based on the selected file
                } catch (IOException exception) {
                    System.out.println("Error " + exception);
                }

                //The filechooser is removed and the mode button sliders are added
                width = image.getWidth();
                height = image.getHeight();
                topPanel.setBounds(5,12,200,170);
                slider.setMaximum(image.getWidth());
                slider.setValue(image.getWidth());
                slider.setVisible(true);

                slider2.setVisible(true);
                slider2.setMaximum(image.getHeight());
                slider2.setValue(image.getHeight());

                button.setVisible(true);
                mode.setVisible(true);
                fileChooser.setVisible(false);

                bottomPanel.remove(fileChooser);
                bottomPanel.add(slider2);
                bottomPanel.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
                bottomPanel.revalidate();
                setSize(480,250);
                repaint();

                JLabel imageLabel = new JLabel(new ImageIcon(image.getBufferedImage()));

                //The generated energy matrix image is put in an imageicon and displayed
                String outPath = (System.getProperty("user.dir") + "/CS1006P2/out/outputImage.png");
                outPath = outPath.replaceAll("CS1006P2/src/", "");
                JLabel energyLabel = new JLabel(new ImageIcon(outPath));
                energyLabel.setAlignmentX(LEFT_ALIGNMENT);

                energyPanel.add(energyLabel);
                energyPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
                energyPanel.setBounds(5, 5, image.getWidth(), image.getHeight());
                energyPanel.setVisible(true);

                imagePanel.add(imageLabel);
                imagePanel.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
                imagePanel.setBounds(3,image.getHeight() + 5,image.getWidth(), image.getHeight());
                imagePanel.setVisible(true);

                //A new window is created to display the energy matrix and selected image
                frame2.add(imagePanel);
                frame2.add(energyPanel);
                frame2.setSize(image.getWidth() + 20, image.getHeight()*2 + 50);

                frame2.setVisible(true);
            } else {
                text1.setText("Not a valid filetype");
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

        //The seam carving and/or addition is started once the button is pressed
        button.addActionListener(e -> {
            if (file != null) {
                int verticalSeams;
                int horizontalSeams;
                mode.setVisible(false);

                if(resolution < 0) {
                    resolution *= -1;
                }
                if (resolution2 < 0) {
                    resolution2 *= -1;
                }

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

                //These if statements account for when different operations are done on the
                //vertical and horizontal components of the image
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
                    carvedBufferedImage = image.addToImageVertical(verticalSeams);
                }

                slider.setMaximum(width);
                slider2.setMaximum(height);
                mode.setVisible(true);

                //This window displays the carved and/or expanded image
                JFrame frame3 = new JFrame("Carved Image");

                JPanel newImagePanel = new JPanel();
                JLabel newImageLabel = new JLabel(new ImageIcon(carvedBufferedImage));

                newImageLabel.setSize(carvedBufferedImage.getWidth(), carvedBufferedImage.getHeight());
                frame3.add(newImageLabel);
                frame3.setSize(image.getWidth() + 20, image.getHeight() * 3 + 50);
                frame3.pack();
                newImagePanel.add(newImageLabel);

                frame3.setSize(carvedBufferedImage.getWidth(), carvedBufferedImage.getHeight());
                frame3.add(newImageLabel);
                frame3.setVisible(true);

                //The edited image is made into a file in the "out" directory"
                try {
                    File carvedImage;
                    String outPath = (System.getProperty("user.dir") + "/CS1006P2/out/carvedImage.png");
                    outPath = outPath.replaceAll("CS1006P2/src/", "");
                    carvedImage = new File(outPath);
                    ImageIO.write(carvedBufferedImage, "png", carvedImage);
                } catch (IOException exception) {
                    System.out.println("Error: " + exception);
                }

                //A new image object is created based on the carved or expanded image
                image = new Image(carvedBufferedImage);
            }
        });
    }
}
