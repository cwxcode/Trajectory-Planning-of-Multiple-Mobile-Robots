package cz.agents.alite.vis.layer;

import java.awt.Graphics2D;

import cz.agents.alite.vis.Vis;


/**
 * A VisLayer describes one layer of data visualized by the VisManager.
 *
 * Each layer has a paint() method, which is called by the VisManager, if it wants
 * the layer to be drawn. The layer should use the Vis.getCanvas() method and
 * the transformation methods of the Vis singleton to draw its content.
 *
 * Additionally, each logical terminal layer should set a help string by the
 * setHelpOverrideString() method, to be correctly listed in the help layer
 * (if it is present in the VisManager).
 *
 *
 * @author Antonin Komenda
 */
public interface VisLayer {

    public void init(Vis vis);

    public void deinit(Vis vis);

    public void paint(Graphics2D canvas);

    public String getLayerDescription();

    public void setHelpOverrideString(String string);

}
