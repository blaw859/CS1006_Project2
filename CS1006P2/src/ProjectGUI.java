import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.event.ChangeEvent;
import javax.swing.JFileChooser;
import javax.swing.JButton;

public class ProjectGUI extends JFrame{

    private Image image;

    private String file;

    private int resolution;

    private int minResolution = 10;

    public ProjectGUI() {
        initUI();
    }

    private void initUI() {

        JFrame frame = new JFrame("Image compressor"); //Temporary title
        frame.setLocationRelativeTo(null);

        JPanel topPanel = new JPanel();

        JPanel bottomPanel = new JPanel();

        JButton button = new JButton("Confirm selection");

        JLabel text2 = new JLabel("No resolution selected");

        JLabel text1 = new JLabel("No file selected");

        topPanel.add(text1);
        topPanel.add(text2);

        //Adds the slider which is used to select the new resolution
        //The max resolution is at 0, until an image is selected
        JSlider slider = new JSlider(0,0,0); //The slider is 0 size before an image is selected
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.addChangeListener((ChangeEvent event) -> {
            resolution = slider.getValue();
            text2.setText("Selected Resolution: " + Integer.toString(resolution));
        });

        //This class will allow the user to select an image easily
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                file = fileChooser.getName();
                text1.setText("You have currently selected " + file + " as the image you want to use.\n " +
                        "Click the button to confirm your selection");
                try {
                    image = new Image(file);
                } catch (IOException exception) {
                    System.out.println("Error " + exception);
                }
                slider.setMaximum(image.getWidth());
            }
        });

        bottomPanel.add(slider);
        bottomPanel.add(button);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (file != null && resolution > minResolution) { //Minimum resolution is currently at 10px
                    try {
                        image = new Image(file);
                    } catch (IOException exception) {
                        System.out.println("Error " + exception); //Needs to be changed
                    }
                    image.createEnergyMatrix(); //The energy matrix is calculated
                    image.compress(image.getWidth()-resolution); //Method not yet implemented
                }
            }
        });

        frame.add(topPanel);
        frame.add(bottomPanel);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

}
