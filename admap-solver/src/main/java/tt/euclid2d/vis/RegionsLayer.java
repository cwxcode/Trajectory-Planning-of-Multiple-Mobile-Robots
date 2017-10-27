package tt.euclid2d.vis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedList;

import tt.euclid2d.region.Region;
import tt.euclid2d.region.Rectangle;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.layer.AbstractLayer;
import cz.agents.alite.vis.layer.VisLayer;

public class RegionsLayer extends AbstractLayer {

    public static class RegionsProvider {
        public Collection<Region> getRegions() { return new LinkedList<Region>(); };
    }

    private RegionsProvider regionsProvider;
    private Color edgeColor;
    private Color fillColor;

    RegionsLayer() {
    }

    public RegionsLayer(RegionsProvider regionsProvider, Color edgeColor, Color fillColor) {
        this.regionsProvider = regionsProvider;
        this.edgeColor = edgeColor;
        this.fillColor = fillColor;
    }

    @Override
    public void paint(Graphics2D canvas) {

        super.paint(canvas);

        Collection<Region> regions = regionsProvider.getRegions();

        for (Region region : regions) {
            if (region instanceof Rectangle) {
                Rectangle rect = (Rectangle) region;

                canvas.setColor(fillColor);
                canvas.fillRect(Vis.transX(rect.getCorner1().x), Vis.transY(rect.getCorner1().y),
                        Vis.transX(rect.getCorner2().x) -  Vis.transX(rect.getCorner1().x),
                        Vis.transY(rect.getCorner2().y) -  Vis.transY(rect.getCorner1().y));

                canvas.setColor(edgeColor);
                canvas.drawRect(Vis.transX(rect.getCorner1().x), Vis.transY(rect.getCorner1().y),
                        Vis.transX(rect.getCorner2().x) -  Vis.transX(rect.getCorner1().x),
                        Vis.transY(rect.getCorner2().y) -  Vis.transY(rect.getCorner1().y));
            }
        }

    }

    public static VisLayer create(final RegionsProvider regionsProvider, final Color edgeColor, final Color fillColor) {
        return new RegionsLayer(regionsProvider, edgeColor, fillColor);
    }
}
