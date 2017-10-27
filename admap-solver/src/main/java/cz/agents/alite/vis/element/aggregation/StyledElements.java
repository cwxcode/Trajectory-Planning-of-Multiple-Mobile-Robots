package cz.agents.alite.vis.element.aggregation;

import java.awt.Color;

/**
 * The StyledElements have also a color and a width of the drawing stroke defined.
 *
 *
 * @author Antonin Komenda
 */
public interface StyledElements extends Elements {

    public Color getColor();

    public int getStrokeWidth();

}
