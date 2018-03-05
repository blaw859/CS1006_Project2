import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;

public class ImageCarver {
    public static Image currentImage;
    public static void main(String[] args){

        EventQueue.invokeLater(() -> {
            ProjectGUI ex = new ProjectGUI();
            ex.setVisible(true);
        });

        /*
        String filePath = args[0];
        filePath = "\\images\\TestImage2.jpg";

        File imageFile = new File(filePath);
        System.out.println(imageFile.canRead());
        System.out.println(imageFile.canWrite());
        System.out.println(imageFile.canExecute());
        try {
            currentImage = new Image(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(currentImage);
        SeamCarver.initializeWeights(currentImage);
        currentImage.outputEnergyMatrix(currentImage.getEnergyMatrix());
        */

    }

}
