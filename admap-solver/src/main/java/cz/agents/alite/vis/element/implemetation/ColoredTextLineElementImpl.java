package cz.agents.alite.vis.element.implemetation;

import cz.agents.alite.vis.element.ColoredTextLineElement;

import java.awt.Color;

/**
 * Default implementation of {@link ColoredTextLineElement}
 * @author Ondrej Hrstka (ondrej.hrstka at agents.fel.cvut.cz)
 */
public class ColoredTextLineElementImpl implements ColoredTextLineElement {

    private final Color color;
    private final String textLine;

    /**
     * @param textLine text of the line
     * @param color color of the line
     */
    public ColoredTextLineElementImpl(String textLine, Color color) {
        this.textLine = textLine;
        this.color = color;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public String getTextLine() {
        return textLine;
    }
}
