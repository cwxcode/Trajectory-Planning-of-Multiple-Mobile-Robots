package tt.vis.problemcreator.states;

import tt.euclid2i.Point;
import tt.vis.problemcreator.patches.EmptyPatch;
import tt.vis.problemcreator.patches.ProblemPatch;

import java.awt.*;

public class EmptyState implements CreatorState {

    private static final EmptyState instance = new EmptyState();

    private EmptyState() {
    }

    public static EmptyState getInstance() {
       return instance;
    }

    @Override
    public void paint(Graphics2D g) {
    }

    @Override
    public void mouseClicked(Point point, int button) {
    }

    @Override
    public void keyTyped(int key) {
    }

    @Override
    public boolean hasChanged() {
        return false;
    }

    @Override
    public ProblemPatch getPatch() {
        return EmptyPatch.getInstance();
    }
}
