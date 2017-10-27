package cz.agents.alite.vis.layer.terminal.textBackgroundLayer;

import java.awt.Dimension;
import java.awt.Point;

/**
 * This implementation will keep drawn rectangle by given distance from right border of canvas. The distance is
 * between right border of drawn rectangle and right border of canvas.
 * @author Ondrej Hrstka (ondrej.hrstka at agents.fel.cvut.cz)
 */
public class RightOffsetPositionFunction implements PositionFunction {

    private int rightOffset;
    private int y;

    /**
     * @param rightOffset distance between right edge of the drawn rectangle and right border of canvas
     * @param y y-coordinate of the top edge of the drawn rectangle
     */
    public RightOffsetPositionFunction(int rightOffset, int y) {
        this.rightOffset = rightOffset;
        this.y = y;
    }

    @Override
    public Point getTopLeftPoint(int rectangeWidth, int rectangleHeight, Dimension drawingDimension) {
        return new Point((int) (drawingDimension.getWidth() - rightOffset - rectangeWidth), y);
    }

    @Override
    public void moveLocation(int deltaX, int deltaY) {
        rightOffset -= deltaX;
        y += deltaY;
    }
}
