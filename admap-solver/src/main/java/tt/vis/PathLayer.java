package tt.vis;

import java.awt.Color;
import java.util.ArrayList;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.apache.log4j.Logger;

import tt.continous.Path;
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

public class PathLayer extends CommonLayer {

    static Logger LOGGER = Logger.getLogger(PathLayer.class);

    public static interface PathProvider<PP> {
        Path<PP> getPath();
    }

    public static <X> VisLayer create(final PathProvider<X> pathProvider, final ProjectionTo2d<X> projection, final Color color, final double samplingInterval, final char toggleKey) {
        GroupLayer group = GroupLayer.create();

        group.addSubLayer(StyledPointLayer.create(new StyledPointElements() {

            @Override
            public Iterable<? extends StyledPoint> getPoints() {
                ArrayList<StyledPoint> points = new ArrayList<StyledPoint>();
                Path<X> path = pathProvider.getPath();

                if (path != null) {
                    Point2d start = projection.project(path.get(0));
                    Point2d end = projection.project(path.get(1));

                    points.add(new StyledPointImpl( new Point3d(start.x, start.y, 0), color, 6));
                    points.add(new StyledPointImpl( new Point3d(end.x, end.y, 0), color, 6));


                    for (double alpha = 0; alpha <= 1.0; alpha += samplingInterval) {
                        X pos = path.get(alpha);
                        if (pos != null) {
                            Point2d point = projection.project(pos);
                            points.add(new StyledPointImpl(new Point3d(point.x, point.y, 0), color, 4));
                        } else {
                            throw new RuntimeException("Position for arg " + alpha + " is null in path " + path);
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
