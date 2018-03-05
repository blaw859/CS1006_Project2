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
        File imageFile = new File(args[0]);
        System.out.println(imageFile.canRead());
        System.out.println(imageFile.canWrite());
        System.out.println(imageFile.canExecute());
        try {
            currentImage = new Image(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        currentImage.outputEnergyMatrix(currentImage.getEnergymatrix());
        */
    }

}
