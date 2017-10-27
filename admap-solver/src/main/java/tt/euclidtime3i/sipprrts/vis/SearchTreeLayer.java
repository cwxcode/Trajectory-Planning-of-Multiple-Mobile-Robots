package tt.euclidtime3i.sipprrts.vis;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.layer.AbstractLayer;
import cz.agents.alite.vis.layer.VisLayer;
import tt.vis.ProjectionTo2d;

import javax.vecmath.Point2d;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;

public class SearchTreeLayer<V> extends AbstractLayer {

    static public interface SearchTreeProvider<VV> {

        Collection<VV> getOpen();

        Collection<VV> getClosed();

        VV getParent(VV vertex);
        /* the returned value must be in (0,1)*/

        double getValue(VV vertex);

        VV getCurrent();
    }

    private SearchTreeProvider<V> searchTreeProvider;
    protected ProjectionTo2d<V> projection;
    protected int vertexDotSize;
    protected ColorMap colorMap;

    protected SearchTreeLayer(SearchTreeProvider<V> searchTreeProvider, ProjectionTo2d<V> projection, int vertexDotSize) {
        super();
        this.searchTreeProvider = searchTreeProvider;
        this.projection = projection;
        this.vertexDotSize = vertexDotSize;
        this.colorMap = new ColorMap(0, 1.0, ColorMap.JET);
    }

    public static <VV> VisLayer create(
            final SearchTreeProvider<VV> searchTreeProvider,
            final ProjectionTo2d<VV> projection,
            int vertexDotSize) {
        return new SearchTreeLayer<VV>(searchTreeProvider, projection, vertexDotSize);
    }

    @Override
    public void init(Vis vis) {
        super.init(vis);

        vis.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                String text = KeyEvent.getKeyText(e.getKeyCode());
            }
        });
    }

    @Override
    public void paint(Graphics2D canvas) {

        super.paint(canvas);

        // open
        for (V vertex : searchTreeProvider.getOpen()) {

            double value = searchTreeProvider.getValue(vertex);
            paintVertex(canvas, vertex, value, Color.BLACK);

            V parent = searchTreeProvider.getParent(vertex);
            if (parent != null) {
                double parentValue = searchTreeProvider.getValue(parent);
                paintEdge(canvas, parent, vertex, parentValue, value);
            }
        }

        // closed
        for (V vertex : searchTreeProvider.getClosed()) {
            double value = searchTreeProvider.getValue(vertex);
            paintVertex(canvas, vertex, value, Color.WHITE);

            V parent = searchTreeProvider.getParent(vertex);
            if (parent != null) {
                double parentValue = searchTreeProvider.getValue(parent);
                paintEdge(canvas, parent, vertex, parentValue, value);
            }
        }

        // current
        V current = searchTreeProvider.getCurrent();
        if (current != null) {
            double value = searchTreeProvider.getValue(current);
            paintVertex(canvas, current, value, Color.RED);
        }

    }

    protected void paintVertex(Graphics2D canvas, V point, double cost, Color strokeColor) {
        Point2d point2d = projection.project(point);
        if (point2d != null) {
	        canvas.setColor(colorMap.getColor(cost));
	        canvas.fillOval(Vis.transX(point2d.x) - vertexDotSize, Vis.transY(point2d.y) - vertexDotSize, vertexDotSize * 2, vertexDotSize * 2);
	        canvas.setColor(strokeColor);
	        canvas.drawOval(Vis.transX(point2d.x) - (vertexDotSize + 1), Vis.transY(point2d.y) - (vertexDotSize + 1), (vertexDotSize + 1) * 2, (vertexDotSize + 1) * 2);
        }
    }

    protected void paintEdge(Graphics2D canvas, V start, V target, double startCost, double endCost) {
        Point2d source = projection.project(start);
        Point2d dest = projection.project(target);
        if (source != null && dest != null) {
	        canvas.setColor(colorMap.getColor(startCost));
	        canvas.drawLine(Vis.transX(source.x), Vis.transY(source.y), Vis.transX(dest.x), Vis.transY(dest.y));
        }
    }

    protected void paintText(Graphics2D canvas, V point, String text, Color color) {
        Point2d p = projection.project(point);
        canvas.setColor(color);
        canvas.drawString(text, (Vis.transX(p.x) + 8), (Vis.transY(p.y) + 5));
    }
}
