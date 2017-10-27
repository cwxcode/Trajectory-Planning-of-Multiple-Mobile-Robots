package tt.vis;

import cz.agents.alite.vis.layer.AbstractLayer;
import cz.agents.alite.vis.layer.VisLayer;

import java.awt.*;

public class LineHUDLayer extends AbstractLayer {

    public static interface TextProvider {

        String getText();
    }

    private final Color color;
    private final TextProvider provider;

    protected LineHUDLayer(TextProvider provider, Color color) {
        this.provider = provider;
        this.color = color;
    }

    @Override
    public void paint(Graphics2D canvas) {
        canvas.setColor(color);
        canvas.drawString(provider.getText(), 15, 40);
    }

    public static VisLayer create(TextProvider provider, Color color) {
        return new LineHUDLayer(provider, color);
    }
}
