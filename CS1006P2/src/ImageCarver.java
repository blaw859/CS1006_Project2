import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageCarver {
    public static Image currentImage;
    public static void main(String[] args){

        /*EventQueue.invokeLater(() -> {
            ProjectGUI ex = new ProjectGUI();
            ex.setVisible(true);
        });*/

        //File imageFile = new File(args[0]);
        /*System.out.println(imageFile.canRead());
        System.out.println(imageFile.canWrite());
        System.out.println(imageFile.canExecute());*/
        try {
            currentImage = new Image("/images/TestImage.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        int verticalSeams = 3;
        SeamCarver.setEnergyMatrices(currentImage);
        BufferedImage outputImage = currentImage.removeSeams(SeamCarver.findSeams(SeamCarver.verticalWeights,verticalSeams));
        //System.out.println(currentImage);
        /*SeamCarver.initializeWeights(currentImage.getEnergyMatrix());
        SeamCarver.*/
        //currentImage.outputEnergyMatrix(currentImage.getEnergyMatrix());

    }

}
