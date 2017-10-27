package cz.agents.alite.vis.layer.terminal;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.element.Circle;
import cz.agents.alite.vis.element.aggregation.FilledStyledCircleElements;

public class FilledStyledCircleLayer extends TerminalLayer {

    private final FilledStyledCircleElements circleElements;

    protected FilledStyledCircleLayer(FilledStyledCircleElements circleElements) {
        this.circleElements = circleElements;
    }

    @Override
    public void paint(Graphics2D canvas) {
        canvas.setStroke(new BasicStroke(circleElements.getStrokeWidth()));

        for (Circle circle: circleElements.getCircles()) {
            int x1 = Vis.transX(circle.getPosition().x - circle.getRadius());
            int y1 = Vis.transY(circle.getPosition().y - circle.getRadius());
            int x2 = Vis.transX(circle.getPosition().x + circle.getRadius());
            int y2 = Vis.transY(circle.getPosition().y + circle.getRadius());
            int diameterW = Vis.transW(circle.getRadius() * 2.0);
            int diameterH = Vis.transH(circle.getRadius() * 2.0);
            if (x2 > 0 && x1 < Vis.getDrawingDimension().width  && y2 > 0 && y1 < Vis.getDrawingDimension().height) {
                canvas.setColor(circleElements.getFillColor());
            	canvas.fillOval(x1, y1, diameterW, diameterH);
                canvas.setColor(circleElements.getColor());
                canvas.drawOval(x1, y1, diameterW, diameterH);
            }
        }
    }

    public static FilledStyledCircleLayer create(FilledStyledCircleElements circleElements) {
        return new FilledStyledCircleLayer(circleElements);
    }
}
