package cz.agents.alite.vis.layer.terminal.textBackgroundLayer;

import cz.agents.alite.vis.element.aggregation.ColoredTextLineElements;

import java.awt.Color;

/**
 * Builder for {@link TextBackgroundLayer} class.
 *
 * @author Ondrej Hrstka (ondrej.hrstka at agents.fel.cvut.cz)
 * @see TextBackgroundLayer
 * @see PositionFunction
 */
public class TextBackgroundLayerBuilder {

    public static final Color DEFAULT_COLOR = new Color(0, 0, 0, 100);

    public static final int DEFAULT_LEFT_PADDING = 15;
    public static final int DEFAULT_RIGHT_PADDING = 15;
    public static final int DEFAULT_BOTTOM_PADDING = 15;
    public static final int DEFAULT_TOP_PADDING = 15;

    private ColoredTextLineElements coloredTextLineElements;
    private PositionFunction positionFunction;
    private Color backgroundColor = DEFAULT_COLOR;
    private int leftPadding = DEFAULT_LEFT_PADDING;
    private int rightPadding = DEFAULT_RIGHT_PADDING;
    private int bottomPadding = DEFAULT_BOTTOM_PADDING;
    private int topPadding = DEFAULT_TOP_PADDING;

    /**
     * Create builder with {@link TopLeftPositionFunction}.
     * @param x x-coordinate of top left corner
     * @param y y-coordinate of top left corner
     * @param coloredTextLineElements elements to display
     * @return the builder
     */
    public static TextBackgroundLayerBuilder createTopLeft(int x, int y,
                                                ColoredTextLineElements coloredTextLineElements) {
        return new TextBackgroundLayerBuilder(new TopLeftPositionFunction(x, y),
                coloredTextLineElements);
    }

    /**
     * Create builder with {@link RightOffsetPositionFunction}.
     * @param rightOffset distance between right edge of the drawn rectangle and right border of canvas
     * @param y y-coordinate of the top edge of the drawn rectangle
     * @param coloredTextLineElements elements to display
     * @return the builder
     */
    public static TextBackgroundLayerBuilder createRightOffset(int rightOffset, int y,
                                                 ColoredTextLineElements coloredTextLineElements) {
        return new TextBackgroundLayerBuilder(new RightOffsetPositionFunction(rightOffset, y),
                coloredTextLineElements);
    }

    /**
     * Create builder with {@link BottomOffsetPositionFunction}.
     * @param x x-coordinate of the left edge of the drawn rectange
     * @param bottomOffset distance between bottom edge of the drawn rectangle and bottom border of canvas
     * @param coloredTextLineElements elements to display
     * @return the builder
     */
    public static TextBackgroundLayerBuilder createBottomOffset(int x, int bottomOffset,
                                                 ColoredTextLineElements coloredTextLineElements) {
        return new TextBackgroundLayerBuilder(new BottomOffsetPositionFunction(x, bottomOffset),
                coloredTextLineElements);
    }

    /**
     * Create builder with {@link RightBottomOffsetPositionFunction}.
     * @param rightOffset distance between right edge of the drawn rectangle and right border of canvas
     * @param bottomOffset distance between bottom edge of the drawn rectangle and bottom border of canvas
     * @param coloredTextLineElements elements to display
     * @return the builder
     */
    public static TextBackgroundLayerBuilder createRightBottomOffset(int rightOffset,
                                                                     int bottomOffset,
                                                ColoredTextLineElements coloredTextLineElements) {
        return new TextBackgroundLayerBuilder(new RightBottomOffsetPositionFunction(rightOffset,
                bottomOffset), coloredTextLineElements);
    }

    /**
     * Create builder with given positionFunction and text
     * @param positionFunction position function
     * @param coloredTextLineElements text elements to display
     */
    public TextBackgroundLayerBuilder(PositionFunction positionFunction,
                                      ColoredTextLineElements coloredTextLineElements) {
        this.positionFunction = positionFunction;
        this.coloredTextLineElements = coloredTextLineElements;
    }

    public TextBackgroundLayerBuilder setColoredTextLineElements(ColoredTextLineElements
                                                                          coloredTextLineElements) {
        this.coloredTextLineElements = coloredTextLineElements;
        return this;
    }

    public TextBackgroundLayerBuilder setPositionFunction(PositionFunction positionFunction) {
        this.positionFunction = positionFunction;
        return this;
    }

    public TextBackgroundLayerBuilder setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public TextBackgroundLayerBuilder setLeftPadding(int leftPadding) {
        this.leftPadding = leftPadding;
        return this;
    }

    public TextBackgroundLayerBuilder setRightPadding(int rightPadding) {
        this.rightPadding = rightPadding;
        return this;
    }

    public TextBackgroundLayerBuilder setBottomPadding(int bottomPadding) {
        this.bottomPadding = bottomPadding;
        return this;
    }

    public TextBackgroundLayerBuilder setTopPadding(int topPadding) {
        this.topPadding = topPadding;
        return this;
    }

    public TextBackgroundLayer build() {
        return TextBackgroundLayer.create(coloredTextLineElements, positionFunction,
                backgroundColor, leftPadding, rightPadding, bottomPadding, topPadding);
    }
}