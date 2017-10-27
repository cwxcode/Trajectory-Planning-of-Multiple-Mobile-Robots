package tt.vis.problemcreator;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;

import javax.vecmath.Point2d;

import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;
import tt.jointeuclid2ni.probleminstance.TrajectoryCoordinationProblem;
import tt.jointeuclid2ni.probleminstance.TrajectoryCoordinationProblemXMLSerializer;
import tt.vis.PictureLayer;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;

public class ProblemDesignerDemo {

    public static void main(String[] args) {
        // ---- initialize vizualization itself

        VisManager.setInitParam("Problem creator", 1024, 768, 200, 200);
        VisManager.setSceneParam(new VisManager.SceneParams() {

            @Override
            public Point2d getDefaultLookAt() {
                return new Point2d(500, 500);
            }

            @Override
            public double getDefaultZoomFactor() {
                return 0.5;
            }
        });

        // ---- initialize problem creator layer

        ProblemDesignerLayer problemCreatorLayer = ProblemDesignerLayer.create();
        problemCreatorLayer.addListener(new ProblemCreatedListener() {
            @Override
            public void handleProblem(TrajectoryCoordinationProblem problem) {
                TrajectoryCoordinationProblemXMLSerializer.serialize(problem, System.out);
            }
        });

        // ---- add all the layers
        VisManager.registerLayer(ColorLayer.create(Color.WHITE));
        VisManager.registerLayer(PictureLayer.create(new File("src/main/resources/pictures/urbanmc2.png"), new Rectangle(1000, 1000), "i"));
        VisManager.registerLayer(problemCreatorLayer);
        VisManager.registerLayer(VisInfoLayer.create());
        VisManager.init();
    }
}
