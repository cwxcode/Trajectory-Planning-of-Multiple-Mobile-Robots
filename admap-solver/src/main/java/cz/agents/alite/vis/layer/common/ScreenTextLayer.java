package cz.agents.alite.vis.layer.common;

import java.awt.Color;
import java.awt.Graphics2D;

import cz.agents.alite.vis.layer.VisLayer;

/**
 * The layer prints one line of text to the given screen coordinates.
 */
public class ScreenTextLayer extends CommonLayer {

    public static interface TextProvider {
        String getText();
    }

    private final Color color;
    private final int x;
    private final int y;
    private final TextProvider textProvider;

    protected ScreenTextLayer(int x, int y, Color color, TextProvider textProvider) {
        this.textProvider = textProvider;
        this.color = color;
        this.x = x;
        this.y = y;
    }

    @Override
    public void paint(Graphics2D canvas) {
        canvas.setColor(color);
        canvas.drawString(textProvider.getText(),x,y);
    }

    public static VisLayer create(int x, int y, Color color, TextProvider textProvider) {
        return new ScreenTextLayer(x, y, color, textProvider);
    }
}
