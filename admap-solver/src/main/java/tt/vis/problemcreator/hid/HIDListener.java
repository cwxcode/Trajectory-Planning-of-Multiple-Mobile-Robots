package tt.vis.problemcreator.hid;

import tt.euclid2i.Point;

public interface HIDListener {

    public void constructAndSaveProblem();

    public void save();

    public void clear();

    public void createNewObject();

    public void createNewAgent();
    
    public void createNewDock();
    
    public void keyTyped(int key);

    public void mouseClicked(Point point, int button);
}
