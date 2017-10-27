package tt.euclidyaw3i.vis;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.layer.AbstractLayer;
import cz.agents.alite.vis.layer.VisLayer;
import tt.euclidyaw3i.Point;

import java.awt.*;
import java.util.Collection;

public class PointsLayer extends AbstractLayer {
	
	public interface PointProvider {
		Collection<Point> getPoints();
	}

	public static VisLayer create(final PointProvider pointProvider, final Color color) {

		return new AbstractLayer() {

			@Override
			public void paint(Graphics2D canvas) {

				Collection<Point> points = pointProvider.getPoints();
				for(Point point : points) {
					if (point != null) {
						canvas.setColor(color);
						canvas.setStroke(new BasicStroke(1));
						canvas.fillOval(Vis.transX(point.getPos().x)-2, Vis.transY(point.getPos().y)-2, 4, 4);

						float tipLength = 100;
						canvas.drawLine(
								Vis.transX(point.getPos().x),
								Vis.transY(point.getPos().y),
								Vis.transX(point.getPos().x + tipLength*Math.cos(point.getYawInRads())),
								Vis.transY(point.getPos().y + tipLength*Math.sin(point.getYawInRads())));
					}
				}

						
			}

		};

	}

}
