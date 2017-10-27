package tt.euclidyaw3i.vis;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.AbstractLayer;
import cz.agents.alite.vis.layer.VisLayer;
import tt.euclid2i.region.Polygon;
import tt.euclidyaw3i.Point;

import java.awt.*;
import java.util.Collection;

public class AxisLayer extends AbstractLayer {

	public static VisLayer create(final int tickInterval) {

		return new AbstractLayer() {

			@Override
			public void paint(Graphics2D canvas) {

                canvas.setColor(new Color(230,230,230));
				canvas.drawLine(Vis.transX(0), Vis.transY(-100000), Vis.transX(0), Vis.transY(+100000));
                canvas.drawLine(Vis.transX(-100000), Vis.transY(0), Vis.transX(+100000), Vis.transY(0));

                for (int x=-10; x <=10; x++) {
                    canvas.drawLine(Vis.transX(x*tickInterval), Vis.transY(-tickInterval/10),
                            Vis.transX(x*tickInterval), Vis.transY(+tickInterval/10));
                }

                for (int y=-10; y <=10; y++) {
                    canvas.drawLine(Vis.transX(-tickInterval/10), Vis.transY(y*tickInterval),
                            Vis.transX(+tickInterval/10), Vis.transY(y*tickInterval));
                }

			}

		};

	}

}
