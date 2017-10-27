package tt.euclidyaw3i.vis;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.layer.AbstractLayer;
import cz.agents.alite.vis.layer.VisLayer;
import tt.euclid2i.region.Polygon;
import tt.euclidyaw3i.Point;

import java.awt.*;
import java.util.Collection;

public class FootPrintLayer extends AbstractLayer {

	public interface PointProvider {
		Collection<Point> getPoints();
	}

	public static VisLayer create(final Polygon footprint, final PointProvider pointsProvider,
                                  final Color edgeColor, final Color fillColor) {

		return new AbstractLayer() {

			@Override
			public void paint(Graphics2D canvas) {
				for (Point point : pointsProvider.getPoints()) {
                    if (point != null) {
                        // draw polygon
                        Polygon transformedFootprint = footprint.getRotated(new tt.euclid2i.Point(0,0), point.getYawInRads())
                                .getTranslated(point.getPos());
                        tt.euclid2i.vis.RegionsLayer.paintPolygon(transformedFootprint, canvas, fillColor, edgeColor, false);

                        // draw point and orientation
                        canvas.setColor(edgeColor);
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
						
			}

		};

	}

}
