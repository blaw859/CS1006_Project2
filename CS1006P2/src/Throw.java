import java.util.ArrayList;
import java.util.List;

public class Throw {
    List<ProjectGUI.ThrowListener> listeners = new ArrayList<ProjectGUI.ThrowListener>();
    public void addThrowListener(ProjectGUI.ThrowListener toAdd){
        listeners.add(toAdd);
    }
    public void Throw(){
        for (ProjectGUI.ThrowListener hl : listeners) hl.Catch();
        ProjectGUI.incrementProgress();
        ProjectGUI.progress.repaint();
    }
}