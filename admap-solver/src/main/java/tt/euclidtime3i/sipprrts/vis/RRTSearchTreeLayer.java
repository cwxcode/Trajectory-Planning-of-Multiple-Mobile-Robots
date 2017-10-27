package tt.euclidtime3i.sipprrts.vis;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.layer.VisLayer;
import tt.vis.ProjectionTo2d;

import javax.vecmath.Point2d;
import java.awt.*;

public class RRTSearchTreeLayer<V> extends SearchTreeLayer<V> {

    static public interface RRTSearchTreeProvider<V> extends SearchTreeProvider<V> {
        double getBallRadius();
    }

    private RRTSearchTreeProvider<V> searchTreeProvider;

    public static <V> VisLayer create(final RRTSearchTreeProvider<V> searchTreeProvider, final ProjectionTo2d<V> projection, int vertexDotSize) {
        return new RRTSearchTreeLayer<V>(searchTreeProvider, projection, vertexDotSize);
    }

    protected RRTSearchTreeLayer(RRTSearchTreeProvider<V> searchTreeProvider, ProjectionTo2d<V> projection, int vertexDotSize) {
        super(searchTreeProvider, projection, vertexDotSize);
        this.searchTreeProvider = searchTreeProvider;
    }


    @Override
    public void paint(Graphics2D canvas) {
        super.paint(canvas);

        // current
        V current = searchTreeProvider.getCurrent();
        if (current != null) {
            double radius = searchTreeProvider.getBallRadius();
            paintCircle(canvas, current, radius, Color.BLACK);
        }
    }

    protected void paintCircle(Graphics2D canvas, V point, double radius, Color color) {
        Point2d p = projection.project(point);
        int r = Vis.transH(radius);

        canvas.setColor(color);
        canvas.drawOval(Vis.transX(p.x) - r / 2, Vis.transY(p.y) - r / 2, r, r);
    }

}