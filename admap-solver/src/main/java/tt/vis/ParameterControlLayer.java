package tt.vis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.layer.AbstractLayer;
import cz.agents.alite.vis.layer.VisLayer;

public class ParameterControlLayer extends AbstractLayer {

    public interface ParameterProvider {
        String getName();
        String getValue();
        char getIncreaseKey();
        char getDecreaseKey();
        char getResetKey();

        void increased();
        void decreased();
        void reset();
    }

    private ParameterProvider parameterProvider;

    ParameterControlLayer(ParameterProvider parameterProvider) {
        this.parameterProvider = parameterProvider;
    }

    @Override
    public void init(Vis vis) {
        super.init(vis);

        vis.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == parameterProvider.getIncreaseKey()) {
                    parameterProvider.increased();
                } else if (e.getKeyChar() == parameterProvider.getDecreaseKey()) {
                    parameterProvider.decreased();
                } else if (e.getKeyChar() == parameterProvider.getResetKey()) {
                    parameterProvider.reset();
                }
            }
        });
    }

    @Override
    public void paint(Graphics2D canvas) {
        canvas.setColor(Color.BLUE);
        canvas.drawString(parameterProvider.getName() + ": " + parameterProvider.getValue(), 15, 20);
    }

    public static VisLayer create(ParameterProvider parameterProvider) {
         return new ParameterControlLayer(parameterProvider);
    }

}
