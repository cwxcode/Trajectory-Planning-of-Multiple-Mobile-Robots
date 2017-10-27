package tt.euclidyaw3d.dubins;

import java.awt.Color;

import javax.vecmath.Point2d;

import tt.discrete.vis.TrajectoryLayer;
import tt.discrete.vis.TrajectoryLayer.TrajectoryProvider;
import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Point;
import tt.euclid2i.vis.ProjectionTo2d;
import tt.euclidtime3i.vis.TimeParameter;
import tt.vis.ParameterControlLayer;
import cz.agents.alite.creator.Creator;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.VisManager.SceneParams;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;

public class DubinsDemo implements Creator {

    @Override
    public void init(String[] args) {}

    @Override
    public void create() {

        initVisualization();

        // create time parameter (for visualization)
        final TimeParameter time = new TimeParameter();

        VisManager.registerLayer(ParameterControlLayer.create(time));

        tt.euclidyaw3d.Point start = new tt.euclidyaw3d.Point(0, 0, 0);
        tt.euclidyaw3d.Point end = new tt.euclidyaw3d.Point(-400, -300, 0.2);

        DubinsCurve dc = new DubinsCurve(start, end, 150, true);

        final EvaluatedTrajectory traj = dc.getTrajectory(1, 10);

        VisManager.registerLayer(TrajectoryLayer.create(new TrajectoryProvider<Point>() {

            @Override
            public tt.discrete.Trajectory<Point> getTrajectory() {
                return traj;
            }
        }, new ProjectionTo2d(), Color.BLUE, 1, 5000, 's'));
    }


    private void initVisualization() {
        VisManager.setInitParam("Trajectory Tools Vis", 1024, 768, 200, 200);
        VisManager.setSceneParam(new SceneParams(){

            @Override
            public Point2d getDefaultLookAt() {
                return new Point2d(0,0);
            }

            @Override
            public double getDefaultZoomFactor() {
                return 0.5;
            } } );

        VisManager.init();

        // background
        VisManager.registerLayer(ColorLayer.create(Color.WHITE));

        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());
    }


    public static void main(String[] args) {
        new DubinsDemo().create();
    }

}
