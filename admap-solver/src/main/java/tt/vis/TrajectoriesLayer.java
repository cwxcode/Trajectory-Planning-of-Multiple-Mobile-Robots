package tt.vis;

import cz.agents.alite.vis.layer.GroupLayer;
import cz.agents.alite.vis.layer.VisLayer;
import cz.agents.alite.vis.layer.common.CommonLayer;
import org.apache.log4j.Logger;
import tt.discrete.Trajectory;
import tt.discrete.vis.TrajectoryLayer;
import tt.discrete.vis.TrajectoryLayer.TrajectoryProvider;
import tt.util.AgentColors;

public class TrajectoriesLayer extends CommonLayer {

    static Logger LOGGER = Logger.getLogger(TrajectoriesLayer.class);

    public static interface TrajectoriesProvider<XX> {
        Trajectory[] getTrajectories();
    }

    public static <X> VisLayer create(
            final TrajectoriesProvider<X> trajectoriesProvider,
            final ProjectionTo2d<X> projection, final int samplingInterval,
            final int maxTimeArg, final int pointSize, final char toggleKey) {
        GroupLayer group = GroupLayer.create();

        Trajectory[] trajectories = trajectoriesProvider.getTrajectories();

        for (int i = 0; i < trajectories.length; i++) {
            final int agentNo = i;
            group.addSubLayer(TrajectoryLayer.create(
                    new TrajectoryProvider<X>() {

                        @Override
                        public Trajectory getTrajectory() {
                            return trajectoriesProvider.getTrajectories()[agentNo];
                        }
                    }, projection, AgentColors.getColorForAgent(agentNo),
                    samplingInterval, maxTimeArg, pointSize, toggleKey));
        }

        return group;
    }
}
