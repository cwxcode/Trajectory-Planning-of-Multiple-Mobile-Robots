package tt.jointtraj.separableflow;

import java.awt.Color;

import javax.vecmath.Point2d;

import tt.euclid2d.trajectory.DesiredTrajectoryDifferenceObjective;
import tt.euclid2d.trajectory.SoftGoalArrivalObjective;
import tt.euclid2d.trajectory.TrajectoryObjectiveFunctionAtPoint;
import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Point;
import tt.euclid2i.Trajectory;
import tt.euclid2i.trajectory.LinearTrajectory;
import tt.euclidtime3i.discretization.softconstraints.BumpSeparationPenaltyFunction;
import tt.euclidtime3i.discretization.softconstraints.ConstantSeparationPenaltyFunction;
import tt.euclidtime3i.discretization.softconstraints.PenaltyFunction;
import tt.euclidtime3i.util.TrajectoryAsPathWrapper;
import tt.euclidtime3i.vis.TimeParameter;
import tt.util.AgentColors;
import tt.vis.FastAgentsLayer;
import tt.vis.FastTrajectoriesLayer;
import tt.vis.FastTrajectoriesLayer.ColorProvider;
import tt.vis.FastTrajectoriesLayer.TrajectoriesProvider;
import tt.vis.ParameterControlLayer;
import cz.agents.alite.creator.Creator;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.VisManager.SceneParams;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;

public class GradientOptimizerDemo implements Creator {

	TimeParameter time;

    @Override
    public void init(String[] args) {}

    @Override
    public void create() {


    	final Trajectory obstacleTraj = new LinearTrajectory(0,
    			new Point(500, 0),
    			new Point(500, 1000), 1, 1000, 0);

    	final int radiusObstacle = 100;

		Point start = new Point(499, 1000);
		Point target = new Point(500, 0);

		final int radiusAgent = 100;
		final int MAXTIME = 3000;
		final double COST_EPS = 0.001;
		final double STEP = 0.5;
		final int SAMPLING_INTERVAL = 50;
		final int MAXITER = 5000;

		final Trajectory desiredTrajectory = new LinearTrajectory(0, start, target, 1, MAXTIME, 0);


		final int MAXSPEED = 2;

		//TrajectoryObjectiveFunctionAtPoint objective = new SoftGoalArrivalObjective(target, SAMPLING_INTERVAL/2, 0);

		TrajectoryObjectiveFunctionAtPoint objective = new DesiredTrajectoryDifferenceObjective(desiredTrajectory, 0.001*1.0/SAMPLING_INTERVAL );

		// continue here with desired traj objective


		final GradientOptimizer optimizer = new GradientOptimizer(start, target, objective,
				MAXSPEED, MAXTIME, SAMPLING_INTERVAL, STEP, COST_EPS, MAXITER, true);

//		EvaluatedTrajectory traj = optimizer.getOptimalTrajectoryUnconstrained(
//				Double.MAX_VALUE, Long.MAX_VALUE);

		final double pmax = 100;
		PenaltyFunction penaltyFunction
			= new BumpSeparationPenaltyFunction(pmax, radiusAgent + radiusObstacle, 1.0);
        	//= new ConstantSeparationPenaltyFunction(pmax, radiusAgent + radiusObstacle);

		initVisualization();

		VisManager.registerLayer(ColorLayer.create(Color.WHITE));
		VisManager.registerLayer(FastTrajectoriesLayer.create(
				new TrajectoriesProvider() {

					@Override
					public Trajectory[] getTrajectories() {
						return new Trajectory[] { obstacleTraj,
								optimizer.getCurrentTraj(), desiredTrajectory};
					}
				}, new ColorProvider() {

					@Override
					public Color getColor(int i) {
						return AgentColors.getColorForAgent(i);
					}
				}, 8, SAMPLING_INTERVAL));

		TimeParameter time = new TimeParameter(SAMPLING_INTERVAL);

		VisManager.registerLayer(FastAgentsLayer.create(
				new FastAgentsLayer.TrajectoriesProvider() {

					@Override
					public Trajectory[] getTrajectories() {
						return new Trajectory[] { obstacleTraj,
								optimizer.getCurrentTraj(), desiredTrajectory };
					}

					@Override
					public int[] getBodyRadiuses() {
						return new int[] { radiusObstacle, radiusAgent, radiusAgent};
					}
				}, new FastAgentsLayer.ColorProvider() {

					@Override
					public Color getColor(int i) {
						return AgentColors.getColorForAgent(i);
					}
				}, time));

		VisManager.registerLayer(ParameterControlLayer.create(time));
        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());

		final EvaluatedTrajectory bestResponse = optimizer
				.getOptimalTrajectoryConstrained(
						new PenaltyFunction[] { penaltyFunction },
						new Trajectory[] {obstacleTraj}, desiredTrajectory,
						Double.MAX_VALUE, Long.MAX_VALUE);
    }

    private void initVisualization() {
        VisManager.setInitParam("Trajectory Tools Vis", 1024, 768, 1000, 1000);
        VisManager.setSceneParam(new SceneParams(){

            @Override
            public Point2d getDefaultLookAt() {
                return new Point2d(500,500);
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
        new GradientOptimizerDemo().create();
    }

}
