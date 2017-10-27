package tt.discrete.vis;

import cz.agents.alite.vis.element.StyledLine;
import cz.agents.alite.vis.element.StyledPoint;
import cz.agents.alite.vis.element.aggregation.StyledLineElements;
import cz.agents.alite.vis.element.aggregation.StyledPointElements;
import cz.agents.alite.vis.element.implemetation.StyledPointImpl;
import cz.agents.alite.vis.layer.GroupLayer;
import cz.agents.alite.vis.layer.VisLayer;
import cz.agents.alite.vis.layer.common.CommonLayer;
import cz.agents.alite.vis.layer.terminal.StyledLineLayer;
import cz.agents.alite.vis.layer.terminal.StyledPointLayer;
import cz.agents.alite.vis.layer.toggle.KeyToggleLayer;
import org.apache.log4j.Logger;
import tt.discrete.Trajectory;
import tt.vis.ProjectionTo2d;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.awt.*;
import java.util.ArrayList;

public class TrajectoryLayer extends CommonLayer {

    static Logger LOGGER = Logger.getLogger(TrajectoryLayer.class);

    public static interface TrajectoryProvider<PP> {
        Trajectory<PP> getTrajectory();
    }

    public static <X> VisLayer create(final TrajectoryProvider<X> trajectoryProvider, final ProjectionTo2d<X> projection,
                                      final Color color, final int samplingInterval, final int maxTimeArg, final char toggleKey) {
        return create(trajectoryProvider, projection, color, samplingInterval, maxTimeArg, 4, toggleKey);
    }

    public static <X> VisLayer create(final TrajectoryProvider<X> trajectoryProvider, final ProjectionTo2d<X> projection,
                                      final Color color, final int samplingInterval, final int maxTimeArg, final int pointSize, final char toggleKey) {
        GroupLayer group = GroupLayer.create();

        group.addSubLayer(StyledPointLayer.create(new StyledPointElements() {

            private Point2d previous;

            @Override
            public Iterable<? extends StyledPoint> getPoints() {
                ArrayList<StyledPoint> points = new ArrayList<StyledPoint>();
                Trajectory<X> traj = trajectoryProvider.getTrajectory();

                if (traj != null) {
                    int maxTime = Math.min(traj.getMaxTime(), maxTimeArg);

                    Point2d start = projection.project(traj.get(traj.getMinTime()));
                    Point2d target = projection.project(traj.get(maxTime));

                    if (start != null) {
                        points.add(new StyledPointImpl(new Point3d(start.x, start.y, 0), color, pointSize));
                    }

                    if (target != null) {
                        points.add(new StyledPointImpl(new Point3d(target.x, target.y, 0), color, 8));
                    }

                    for (int time = traj.getMinTime(); time < maxTime; time += samplingInterval) {
                        X pos = traj.get(time);
                        if (pos != null) {
                            Point2d point = projection.project(pos);

                            if (previous != null && point != null && point.distance(previous) == 0)
                                continue;

                            previous = point;

                            if (point != null) {
                                points.add(new StyledPointImpl(new Point3d(point.x, point.y, 0), color, pointSize));
                            }
                        } else {
                            throw new RuntimeException("Position for time " + time + "s is null in trajectory " + traj);
                        }
                    }

                }

                return points;
            }

        }));

        group.addSubLayer(StyledLineLayer.create(new StyledLineElements() {

            @Override
            public Iterable<? extends StyledLine> getLines() {
                ArrayList<StyledLine> lines = new ArrayList<StyledLine>();
                return lines;
            }

        }));


        KeyToggleLayer toggle = KeyToggleLayer.create(toggleKey);
        toggle.addSubLayer(group);
        toggle.setEnabled(true);

        return toggle;

    }
}
