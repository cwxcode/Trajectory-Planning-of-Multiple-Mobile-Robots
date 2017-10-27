package tt.vis.problemcreator.patches;

import tt.vis.problemcreator.main.ExtensibleProblem;

import java.awt.*;

public interface ProblemPatch {

    public void apply(ExtensibleProblem problem);

    public void paint(Graphics2D g);
}
