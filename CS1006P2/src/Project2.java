import java.awt.EventQueue;

public class Project2 {
    public static void main(String[] args) {
        int[] nums = {0,1,2,3,4,5,6,7,8,9};
        int offset=10;
        for(int i = 0; i < 20; i++) {
            int pointer = (i+offset) % nums.length;
            System.out.println(nums[pointer]);
        }
        EventQueue.invokeLater(() -> {
            ProjectGUI ex = new ProjectGUI();
            ex.setVisible(true);
        });


    }
}
