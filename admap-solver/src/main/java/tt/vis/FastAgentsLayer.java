package tt.vis;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.layer.AbstractLayer;
import cz.agents.alite.vis.layer.VisLayer;

import org.apache.log4j.Logger;

import tt.discrete.Trajectory;
import tt.euclid2i.Point;
import tt.euclidtime3i.vis.TimeParameter;

import java.awt.*;

public class FastAgentsLayer extends AbstractLayer {

    static Logger LOGGER = Logger.getLogger(FastAgentsLayer.class);

    public static interface TrajectoriesProvider {
        Trajectory<Point>[] getTrajectories();

        int[] getBodyRadiuses();
    }

    public static interface ColorProvider {
        Color getColor(int i);
    }

    private TrajectoriesProvider trajectoryProvider;
    private ColorProvider colorProvider;
    private boolean filled;
    private TimeParameter time;

    public FastAgentsLayer(TrajectoriesProvider trajectoryProvider, ColorProvider colorProvider, TimeParameter time) {
    	this(trajectoryProvider, colorProvider, false, time);
    }
    
    public FastAgentsLayer(TrajectoriesProvider trajectoryProvider, ColorProvider colorProvider, boolean filled, TimeParameter time) {
        this.trajectoryProvider = trajectoryProvider;
        this.colorProvider = colorProvider;
        this.filled = filled;
        this.time = time;
    }

    public static VisLayer create(TrajectoriesProvider trajectoriesProvider, ColorProvider colorProvider, TimeParameter time) {
        return new FastAgentsLayer(trajectoriesProvider, colorProvider, time);
    }
    
    public static VisLayer create(TrajectoriesProvider trajectoriesProvider, ColorProvider colorProvider, boolean filled, TimeParameter time) {
    		return new FastAgentsLayer(trajectoriesProvider, colorProvider, filled, time);
    }

    @Override
    public void paint(Graphics2D canvas) {
        Trajectory<Point>[] trajectories = trajectoryProvider.getTrajectories();
        int[] bodyRadiuses = trajectoryProvider.getBodyRadiuses();

        if (trajectories == null)
            return;

        for (int i = 0; i < trajectories.length; i++) {
            if (trajectories[i] == null)
                continue;

            Point projectedPoint = trajectories[i].get(time.getTime());
            int radius = bodyRadiuses[i];

            if (projectedPoint == null)
                continue;

            canvas.setColor(colorProvider.getColor(i));
            if (filled) {
	            canvas.fillOval(
	                    Vis.transX(projectedPoint.x - radius),
	                    Vis.transY(projectedPoint.y - radius),
	                    Vis.transH(radius * 2), Vis.transW(radius * 2));
            } else {
            	
            	final int RADIUS = 9; 
	            canvas.fillOval(
	                    Vis.transX(projectedPoint.x) - RADIUS,
	                    Vis.transY(projectedPoint.y) - RADIUS,
	                    RADIUS*2, RADIUS*2);
            	
            	Stroke originalStroke = canvas.getStroke();
            	canvas.setStroke(new BasicStroke(2));
            	
	            canvas.drawOval(
	                    Vis.transX(projectedPoint.x - radius),
	                    Vis.transY(projectedPoint.y - radius),
	                    Vis.transH(radius * 2), Vis.transW(radius * 2));
	            canvas.setStroke(originalStroke);
            }
            canvas.setColor(Color.WHITE);
            
            
            String label = Integer.toString(i);
            int labelWidth = (int)  canvas.getFontMetrics().getStringBounds(label, canvas).getWidth();  
            int labelHeight = (int)  canvas.getFontMetrics().getStringBounds(label, canvas).getHeight();  
            
            canvas.drawString(label, Vis.transX(projectedPoint.x) - (labelWidth / 2), Vis.transY(projectedPoint.y) + (labelHeight / 2));
        }
    }
}
