package tt.vis;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;

import tt.euclid2i.Trajectory;
import tt.euclidtime3i.Region;
import tt.euclidtime3i.region.MovingCircle;
import tt.euclidtime3i.vis.RegionsLayer;
import tt.euclidtime3i.vis.RegionsLayer.RegionsProvider;
import tt.util.AgentColors;
import cz.agents.alite.vis.layer.GroupLayer;
import cz.agents.alite.vis.layer.VisLayer;
import cz.agents.alite.vis.layer.common.CommonLayer;

public class CylindricAgentsLayer extends CommonLayer {

    static Logger LOGGER = Logger.getLogger(CylindricAgentsLayer.class);

    public static interface TrajectoriesProvider {
        Trajectory[] getTrajectories();
    }

    public static VisLayer create(
            final TrajectoriesProvider trajectoriesProvider,
            final ProjectionTo2d<tt.euclidtime3i.Point> projection, final int[] bodyRadiuses, final int samplingInterval) {
        GroupLayer group = GroupLayer.create();

        Trajectory[] trajectories = trajectoriesProvider.getTrajectories();

        for (int i = 0; i < trajectories.length; i++) {

            final int finalI = i;
            final Color color = AgentColors.getColorForAgent(finalI);

            group.addSubLayer(RegionsLayer.create(new RegionsProvider() {

                @Override
                public Collection<Region> getRegions() {
                    Trajectory trajectory = trajectoriesProvider.getTrajectories()[finalI];
                    if (trajectory != null) {
                        Region region = new MovingCircle(trajectory, bodyRadiuses[finalI], samplingInterval);
                        return Collections.singleton(region);
                    } else {
                        return new ArrayList<Region>();
                    }
                }
            }, projection, color, color));
        }


        return group;
    }
}
