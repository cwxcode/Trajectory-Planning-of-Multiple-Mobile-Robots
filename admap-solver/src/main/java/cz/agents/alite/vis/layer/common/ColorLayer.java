package cz.agents.alite.vis.layer.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.layer.VisLayer;

public class ColorLayer extends CommonLayer {
// TODO: the color should be requested

    private Color color;

    protected ColorLayer(final Color color) {
        this.color = color;
    }

    @Override
    public void paint(Graphics2D canvas) {
        canvas.setColor(color);
        Dimension dim = Vis.getDrawingDimension();
        canvas.fillRect(0, 0, dim.width, dim.height);
    }

    @Override
    public String getLayerDescription() {
        String description = "Layer fills the view with color.";
        return buildLayersDescription(description);
    }

    public static VisLayer create(Color color) {
        return new ColorLayer(color);
    }

}
