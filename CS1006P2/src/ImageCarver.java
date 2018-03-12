import java.awt.EventQueue;

public class ImageCarver {
    public static void main(String[] args){

        EventQueue.invokeLater(() -> {
            ProjectGUI ex = new ProjectGUI();
            ex.setVisible(true);
        });

    }
}
