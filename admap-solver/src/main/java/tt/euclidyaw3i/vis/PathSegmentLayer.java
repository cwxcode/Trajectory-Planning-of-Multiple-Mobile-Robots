package tt.euclidyaw3i.vis;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.layer.AbstractLayer;
import cz.agents.alite.vis.layer.VisLayer;
import tt.euclidyaw3i.Point;
import tt.euclidyaw3i.discretization.PathSegment;

import java.awt.*;
import java.util.Collection;

public class PathSegmentLayer extends AbstractLayer {
	
	public interface PathSegmentsProvider {
		Collection<PathSegment> getPathSegements();
	}

	public static VisLayer create(final PathSegmentsProvider psProvider, final Color color) {
		return  create(psProvider, color, 1);
	}

	public static VisLayer create(final PathSegmentsProvider psProvider, final Color color, final int strokeWidth) {

		return new AbstractLayer() {

			@Override
			public void paint(Graphics2D canvas) {
				canvas.setColor(color);
				Stroke oldStroke = canvas.getStroke();
				canvas.setStroke(new BasicStroke(strokeWidth));
                Collection<PathSegment> segments = psProvider.getPathSegements();
                if (segments != null) {
                    for (PathSegment segment : segments) {
                        if (segment != null) {
                            Point[] waypoints = segment.getWaypoints();
                            for (int i = 0; i < waypoints.length - 1; i++) {
                                canvas.drawLine(
                                        Vis.transX(waypoints[i].getPos().x),
                                        Vis.transY(waypoints[i].getPos().y),
                                        Vis.transX(waypoints[i + 1].getPos().x),
                                        Vis.transY(waypoints[i + 1].getPos().y));
                            }
                        }
                    }
                }
				canvas.setStroke(oldStroke);

			}

		};

	}

}
