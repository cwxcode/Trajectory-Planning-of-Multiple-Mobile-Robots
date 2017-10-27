package tt.vis.problemcreator.states;

import tt.euclid2i.Point;
import tt.vis.problemcreator.patches.ProblemPatch;

import java.awt.*;

public interface CreatorState {

    public void paint(Graphics2D g);

    public void mouseClicked(Point point, int button);

    public void keyTyped(int key);

    public boolean hasChanged();

    public ProblemPatch getPatch();

}
