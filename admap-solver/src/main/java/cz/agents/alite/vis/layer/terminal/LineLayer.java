package cz.agents.alite.vis.layer.terminal;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.element.Line;
import cz.agents.alite.vis.element.aggregation.LineElements;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class LineLayer extends TerminalLayer {

    private final LineElements lineElements;

    protected LineLayer(LineElements lineElements) {
        this.lineElements = lineElements;
    }

    @Override
    public void paint(Graphics2D canvas) {
        canvas.setColor(lineElements.getColor());
        canvas.setStroke(new BasicStroke(lineElements.getStrokeWidth()));

        Dimension dim = Vis.getDrawingDimension();
        Rectangle2D drawingRectangle = new Rectangle(dim);

        for (Line line : lineElements.getLines()) {
            int x = Vis.transX(line.getFrom().x);
            int y = Vis.transY(line.getFrom().y);
            int xTo = Vis.transX(line.getTo().x);
            int yTo = Vis.transY(line.getTo().y);

            Line2D line2d = new Line2D.Double(x, y, xTo, yTo);

            if (line2d.intersects(drawingRectangle)) {
                canvas.draw(line2d);
                onEachLine(canvas, line);
            }
        }
    }

    protected void onEachLine(Graphics2D canvas, Line line) {
    }

    @Override
    public String getLayerDescription() {
        String description = "Layer shows lines.";
        return buildLayersDescription(description);
    }

    public static LineLayer create(LineElements lineElements) {
        return new LineLayer(lineElements);
    }

}
