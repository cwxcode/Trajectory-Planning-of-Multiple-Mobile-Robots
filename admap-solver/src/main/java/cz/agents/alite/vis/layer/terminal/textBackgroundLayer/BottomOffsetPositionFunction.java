package cz.agents.alite.vis.layer.terminal.textBackgroundLayer;

import java.awt.Dimension;
import java.awt.Point;

/**
 * This implementation will keep drawn rectangle by given distance from bottom border of canvas. The distance is
 * between bottom edge of drawn rectangle and bottom border of canvas.
 * @author Ondrej Hrstka (ondrej.hrstka at agents.fel.cvut.cz)
 */
public class BottomOffsetPositionFunction implements PositionFunction {

    private int x;
    private int bottomOffset;

    /**
     * @param x x-coordinate of the left edge of the drawn rectange
     * @param bottomOffset distance between bottom edge of the drawn rectangle and bottom border of canvas
     */
    public BottomOffsetPositionFunction(int x, int bottomOffset) {
        this.x = x;
        this.bottomOffset = bottomOffset;
    }

    @Override
    public Point getTopLeftPoint(int rectangeWidth, int rectangleHeight, Dimension drawingDimension) {
        return new Point(x, (int) (drawingDimension.getHeight() - bottomOffset - rectangleHeight));
    }

    @Override
    public void moveLocation(int deltaX, int deltaY) {
        x += deltaX;
        bottomOffset -= deltaY;
    }

}
