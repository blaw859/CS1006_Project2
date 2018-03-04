import java.awt.EventQueue;

public class Project2 {
    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            ProjectGUI ex = new ProjectGUI();
            ex.setVisible(true);
        });


    }
}
