package tt.euclidyaw3i.vis;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.layer.AbstractLayer;
import cz.agents.alite.vis.layer.VisLayer;
import tt.euclidyaw3i.Point;

import java.awt.*;

public class PointLayer extends AbstractLayer {
	
	public interface PointProvider {
		Point getPoint();
	}

	public static VisLayer create(final PointProvider pointProvider, final Color color) {

		return new AbstractLayer() {

			@Override
			public void paint(Graphics2D canvas) {
				
				Point point = pointProvider.getPoint();
				if (point != null) {
					canvas.setColor(color);
					canvas.setStroke(new BasicStroke(1));
					canvas.drawOval(Vis.transX(point.getPos().x)-4, Vis.transY(point.getPos().y)-4, 8, 8);
					
					float tipLength = 500;
					canvas.drawLine(
							Vis.transX(point.getPos().x),
							Vis.transY(point.getPos().y),
							Vis.transX(point.getPos().x + tipLength*Math.cos(point.getYawInRads())),
							Vis.transY(point.getPos().y + tipLength*Math.sin(point.getYawInRads())));
					
				}
						
			}

		};

	}

}
