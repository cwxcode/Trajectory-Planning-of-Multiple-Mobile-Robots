package cz.agents.alite.vis.layer;

import java.awt.Graphics2D;

import cz.agents.alite.vis.Vis;

/**
 * A default implementation of the {@link VisLayer} interface.
 *
 * @author Antonin Komenda
 */
public abstract class AbstractLayer implements VisLayer {

    private String helpOverrideString = null;

    @Override
    public void init(Vis vis) {
    }

    @Override
    public void deinit(Vis vis) {
    }

    @Override
    public void paint(Graphics2D canvas) {
    }

    @Override
    public String getLayerDescription() {
    return buildLayersDescription(getClass().toString() + " layer\n");
    }

    @Override
    public void setHelpOverrideString(String helpOverrideString) {
    this.helpOverrideString = helpOverrideString;
    }

    protected String getHelpOverrideString() {
    return helpOverrideString;
    }

    protected String buildLayersDescription(String description) {
    if (helpOverrideString != null) {
        return helpOverrideString;
    }
    return description;
    }

}
