package tt.vis;

import cz.agents.alite.vis.layer.GroupLayer;
import cz.agents.alite.vis.layer.VisLayer;
import cz.agents.alite.vis.layer.common.CommonLayer;
import org.apache.log4j.Logger;
import tt.discrete.Trajectory;
import tt.discrete.vis.TrajectoryLayer;
import tt.discrete.vis.TrajectoryLayer.TrajectoryProvider;

import java.awt.*;

public class ColoredTrajectoriesLayer extends CommonLayer {

    static Logger LOGGER = Logger.getLogger(ColoredTrajectoriesLayer.class);

    public static interface TrajectoriesProvider<XX> {
        Trajectory[] getTrajectories();
    }

    public static interface ColorProvider {
        Color getColor(int i);
    }

    public static <X> VisLayer create(
            final TrajectoriesProvider<X> trajectoriesProvider,
            final ColorProvider colorProvider,
            final ProjectionTo2d<X> projection, final int samplingInterval,
            final int maxTimeArg, final int pointSize, final char toggleKey) {
        GroupLayer group = GroupLayer.create();

        Trajectory[] trajectories = trajectoriesProvider.getTrajectories();

        if (trajectories != null)
            for (int i = 0; i < trajectories.length; i++) {
                final int agentNo = i;
                group.addSubLayer(TrajectoryLayer.create(
                        new TrajectoryProvider<X>() {

                            @Override
                            public Trajectory getTrajectory() {
                                return trajectoriesProvider.getTrajectories()[agentNo];
                            }
                        }, projection, colorProvider.getColor(agentNo),
                        samplingInterval, maxTimeArg, pointSize, toggleKey));
            }

        return group;
    }
}
