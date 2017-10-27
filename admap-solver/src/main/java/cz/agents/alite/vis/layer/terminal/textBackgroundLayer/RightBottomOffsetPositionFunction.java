package cz.agents.alite.vis.layer.terminal.textBackgroundLayer;

import java.awt.Dimension;
import java.awt.Point;

/**
 * This implementation will keep drawn rectangle by given distance from bottom and righ border of canvas. The
 * distance  is between right edge of drawn rectangle and right border of canvas and between bottom edge of drawn
 * rectangle and bottom border of canvas.
 * @author Ondrej Hrstka (ondrej.hrstka at agents.fel.cvut.cz)
 */
public class RightBottomOffsetPositionFunction implements PositionFunction {

    private int rightOffset;
    private int bottomOffset;

    /**
     * @param rightOffset distance between right edge of the drawn rectangle and right border of canvas
     * @param bottomOffset distance between bottom edge of the drawn rectangle and bottom border of canvas
     */
    public RightBottomOffsetPositionFunction(int rightOffset, int bottomOffset) {
        this.rightOffset = rightOffset;
        this.bottomOffset = bottomOffset;
    }

    @Override
    public Point getTopLeftPoint(int rectangeWidth, int rectangleHeight, Dimension drawingDimension) {
        return new Point((int) (drawingDimension.getWidth() - rightOffset - rectangeWidth),
                (int) (drawingDimension.getHeight() - bottomOffset - rectangleHeight));
    }

    @Override
    public void moveLocation(int deltaX, int deltaY) {
        rightOffset -= deltaX;
        bottomOffset -= deltaY;
    }
}
