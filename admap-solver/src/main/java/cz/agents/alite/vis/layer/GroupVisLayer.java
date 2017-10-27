package cz.agents.alite.vis.layer;

/**
 * A GroupVisLayer is an extension of the basic VisLayer to enable tree
 * composition of the layers.
 *
 * Each GroupVisLayer can have its own sub-layers. The decision, if the sub-layers
 * will be drawn, is on the particular GroupVisLayer. It enables to create
 * layers dynamically turning on and off another (sub-)layers.
 *
 *
 * @author Antonin Komenda
 */
public interface GroupVisLayer extends VisLayer {

    public void addSubLayer(VisLayer layer);

    public void removeSubLayer(VisLayer layer);

}
