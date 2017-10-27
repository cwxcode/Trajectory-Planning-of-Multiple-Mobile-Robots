package tt.vis.problemcreator.states;

import cz.agents.alite.vis.layer.VisLayer;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.region.Polygon;
import tt.euclid2i.vis.RegionsLayer;
import tt.vis.problemcreator.patches.ObstaclePatch;
import tt.vis.problemcreator.patches.ProblemPatch;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Collections;

public class ObstacleCreatingState implements CreatorState {

    private Polygon current;
    private VisLayer layer;

    public ObstacleCreatingState() {
        this.current = new Polygon(new Point[0]);
        this.layer = RegionsLayer.create(new RegionsLayer.RegionsProvider() {
            @Override
            public Collection<? extends Region> getRegions() {
                return Collections.singletonList(current);
            }
        }, Color.black, new Color(255, 0, 0, 32));
    }

    @Override
    public void paint(Graphics2D g) {
        if (hasChanged())
            layer.paint(g);
    }

    @Override
    public void mouseClicked(Point point, int button) {
        if (button == MouseEvent.BUTTON1) {
            current.addPoint(point);
        }
    }

    @Override
    public void keyTyped(int key) {
    }

    @Override
    public boolean hasChanged() {
        return current.getPoints().length > 0;
    }

    @Override
    public ProblemPatch getPatch() {
        return new ObstaclePatch(current);
    }
}
