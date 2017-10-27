package cz.agents.alite.vis.layer.terminal.textBackgroundLayer;

import java.awt.Dimension;
import java.awt.Point;

/**
* This implementation will keep drawn rectangle's top left corner in given point.
* @author Ondrej Hrstka (ondrej.hrstka at agents.fel.cvut.cz)
*/
public class TopLeftPositionFunction implements PositionFunction {

    private Point point;

    /**
     *
     * @param x x-coordinate of top left corner
     * @param y y-coordinate of top left corner
     */
    public TopLeftPositionFunction(int x, int y) {
        point = new Point(x, y);
    }

    @Override
    public Point getTopLeftPoint(int rectangeWidth, int rectangleHeight, Dimension drawingDimension) {
        return point;
    }

    @Override
    public void moveLocation(int deltaX, int deltaY) {
        point = new Point(point.x + deltaX, point.y + deltaY);
    }
}
