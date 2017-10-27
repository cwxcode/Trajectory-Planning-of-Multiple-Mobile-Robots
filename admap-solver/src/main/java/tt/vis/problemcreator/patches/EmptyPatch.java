package tt.vis.problemcreator.patches;

import tt.vis.problemcreator.main.ExtensibleProblem;

import java.awt.*;

public class EmptyPatch implements ProblemPatch {

    private static EmptyPatch instance = new EmptyPatch();

    private EmptyPatch() {
    }

    public static EmptyPatch getInstance() {
        return instance;
    }

    @Override
    public void apply(ExtensibleProblem problem) {
    }

    @Override
    public void paint(Graphics2D g) {
    }
}
