package tt.euclid2i.vis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Collection;

import tt.euclid2i.Point;
import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.layer.terminal.TerminalLayer;

public class PointLayer extends TerminalLayer {
	
		public interface PointProvider {
			Collection<Point> getPoints();
		}
		
	    private final PointProvider pointProvider;
		private Color color;
		private int size;

	    protected PointLayer(PointProvider pointProvider, Color color, int size) {
	        this.pointProvider = pointProvider;
	        this.color = color;
	        this.size = size;
	    }

	    @Override
	    public void paint(Graphics2D canvas) {
	        canvas.setColor(color);
	        canvas.setStroke(new BasicStroke(1));
	        Dimension dim = Vis.getDrawingDimension();

	        for (Point point : pointProvider.getPoints()) {	        	
	        	if (point != null) {
		            int x1 = Vis.transX(point.x) - size;
		            int y1 = Vis.transY(point.y) - size;
		            int x2 = Vis.transX(point.x) + size;
		            int y2 = Vis.transY(point.y) + size;
		            if (x2 > 0 && x1 < dim.width && y2 > 0 && y1 < dim.height) {
		                canvas.fillOval(x1, y1, size * 2, size * 2);
		            }
	        	}
	        }
	    }

	    @Override
	    public String getLayerDescription() {
	        String description = "Layer shows points.";
	        return buildLayersDescription(description);
	    }

	    public static PointLayer create(PointProvider pointProvider, Color color, int size) {
	        return new PointLayer(pointProvider, color, size);
	    }


}
