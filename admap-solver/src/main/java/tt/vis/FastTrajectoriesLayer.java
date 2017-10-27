package tt.vis;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.layer.AbstractLayer;
import cz.agents.alite.vis.layer.VisLayer;
import org.apache.log4j.Logger;
import tt.euclid2i.Point;
import tt.euclid2i.Trajectory;

import java.awt.*;

public class FastTrajectoriesLayer extends AbstractLayer {

    static Logger LOGGER = Logger.getLogger(FastTrajectoriesLayer.class);

    public static interface TrajectoriesProvider {

        Trajectory[] getTrajectories();
    }

    public static interface ColorProvider {

        Color getColor(int i);
    }

    private TrajectoriesProvider trajectoryProvider;
    private ColorProvider colorProvider;
    private int radius;
    private int step;

    private Point previous;

    public FastTrajectoriesLayer(TrajectoriesProvider trajectoryProvider, ColorProvider colorProvider, int radius, int step) {
        this.trajectoryProvider = trajectoryProvider;
        this.colorProvider = colorProvider;
        this.radius = radius;
        this.step = step;
    }

    public static VisLayer create(TrajectoriesProvider trajectoriesProvider, ColorProvider colorProvider, int radius, int step) {
        return new FastTrajectoriesLayer(trajectoriesProvider, colorProvider, radius, step);
    }

    @Override
    public void paint(Graphics2D canvas) {
        Trajectory[] trajectories = trajectoryProvider.getTrajectories();

        if (trajectories == null)
        	return;

        for (int i = 0; i < trajectories.length; i++) {
            if (trajectories[i] == null)
                continue;

            int min = trajectories[i].getMinTime();
            int max = trajectories[i].getMaxTime();

            for (int t = min; t < max; t += step) {
                Point point = trajectories[i].get(t);

                if (previous != null && point.distance(previous) == 0)
                    continue;

                previous = point;

                canvas.setColor(colorProvider.getColor(i));
                canvas.fillOval(
                        Vis.transX(point.x - radius),
                        Vis.transY(point.y - radius),
                        Vis.transH(radius * 2), Vis.transW(radius * 2));
            }

        }
    }
}
