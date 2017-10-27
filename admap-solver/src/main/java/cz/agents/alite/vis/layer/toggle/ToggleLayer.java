package cz.agents.alite.vis.layer.toggle;

import java.awt.Graphics2D;

import cz.agents.alite.vis.layer.GroupLayer;

/**
 * A ToggleLayer can simply switch, if the sub-layers are drawn or not.
 *
 * The switching can be done by calling of the setEnabled() method.
 *
 *
 * @author Antonin Komenda
 */
public class ToggleLayer extends GroupLayer {

    private boolean enabled = true;

    public ToggleLayer() {
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void paint(Graphics2D canvas) {
        if (enabled) {
            super.paint(canvas);
        }
    }

}
