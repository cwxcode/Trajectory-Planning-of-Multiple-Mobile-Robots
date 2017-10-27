package cz.agents.alite.vis.layer.toggle;

import cz.agents.alite.vis.layer.GroupLayer;
import java.awt.Graphics2D;

/**
 * A SwitchLayer can simply cycle drawing through sub-layers or draw nothing.
 *
 * The switching can be done by calling of the cycle() method.
 *
 *
 * @author Jiri Vokrinek
 */
public class SwitchLayer extends GroupLayer {

    private int enabled = -1;

    public SwitchLayer() {
    }

    public void cycle() {
        if (++enabled >= subLayers.size()) {
            enabled = -1;
        }
    }

    @Override
    public void paint(Graphics2D canvas) {
        if ((enabled > -1) && (enabled < subLayers.size())) {
            subLayers.get(enabled).paint(canvas);
        }
    }
}
