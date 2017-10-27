package cz.agents.alite.vis.layer.terminal;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.element.Point;
import cz.agents.alite.vis.element.aggregation.PointElements;

public class PointLayer extends TerminalLayer {

    private final PointElements pointElements;

    protected PointLayer(PointElements pointElements) {
        this.pointElements = pointElements;
    }

    @Override
    public void paint(Graphics2D canvas) {
        int radius = (int) (pointElements.getStrokeWidth() / 2.0);
        canvas.setColor(pointElements.getColor());
        canvas.setStroke(new BasicStroke(1));
        Dimension dim = Vis.getDrawingDimension();

        for (Point point : pointElements.getPoints()) {

            int x1 = Vis.transX(point.getPosition().x) - radius;
            int y1 = Vis.transY(point.getPosition().y) - radius;
            int x2 = Vis.transX(point.getPosition().x) + radius;
            int y2 = Vis.transY(point.getPosition().y) + radius;
            if (x2 > 0 && x1 < dim.width && y2 > 0 && y1 < dim.height) {
                canvas.fillOval(x1, y1, radius * 2, radius * 2);
            }
        }
    }

    @Override
    public String getLayerDescription() {
        String description = "Layer shows points.";
        return buildLayersDescription(description);
    }

    public static PointLayer create(PointElements pointElements) {
        return new PointLayer(pointElements);
    }

}
