package cz.agents.alite.vis.layer;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cz.agents.alite.vis.Vis;

/**
 * A default implementation of the {@link GroupVisLayer} interface.
 *
 * @author Antonin Komenda
 */
public class GroupLayer extends AbstractLayer implements GroupVisLayer {

    protected final LinkedList<VisLayer> subLayers = new LinkedList<VisLayer>();

    protected GroupLayer() {
    }

    public LinkedList<VisLayer> getSubLayers() {
        return subLayers;
    }

    @Override
    public void init(Vis vis) {
        for (VisLayer layer : getSubLayers()) {
            layer.init(vis);
        }
    }

    @Override
    public void deinit(Vis vis) {
        for (VisLayer layer : getSubLayers()) {
            layer.deinit(vis);
        }
    }

    @Override
    public void addSubLayer(VisLayer layer) {
        subLayers.add(layer);
    }

    @Override
    public void removeSubLayer(VisLayer layer) {
        subLayers.remove(layer);
    }

    @Override
    public void paint(Graphics2D canvas) {
        // TODO slow, do it better way
        List<VisLayer> toIterateThrough = new ArrayList<VisLayer>(subLayers);
        for (VisLayer layer : toIterateThrough) {
            layer.paint(canvas);
        }
    }

    @Override
    public String getLayerDescription() {
        String description = "All sub-layers are always shown:";
        return buildLayersDescription(description);
    }

    public static GroupLayer create() {
        return new GroupLayer();
    }

    protected String buildLayersDescription(String description) {
        if (getHelpOverrideString() != null) {
            return getHelpOverrideString();
        }

        for (VisLayer layer : subLayers) {
            if (!layer.getLayerDescription().isEmpty()) {
                description += "<br/>   "
                        + layer.getLayerDescription().replace("   ", "      ").replace("\n",
                                "\n   ");
            }
        }
        return description;
    }

    public void clear() {
        subLayers.clear();
    }

}
