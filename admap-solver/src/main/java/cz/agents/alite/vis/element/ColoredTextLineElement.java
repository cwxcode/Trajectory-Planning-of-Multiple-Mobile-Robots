package cz.agents.alite.vis.element;

import java.awt.Color;

/**
 * Element representing line with given color.
 * @author Ondrej Hrstka (ondrej.hrstka at agents.fel.cvut.cz)
 *
 * @see cz.agents.alite.vis.layer.terminal.textBackgroundLayer.TextBackgroundLayer
 */
public interface ColoredTextLineElement extends Element {

    /**
     * @return color of the line
     */
    public Color getColor();

    /**
     * @return text of the line
     */
    public String getTextLine();

}
