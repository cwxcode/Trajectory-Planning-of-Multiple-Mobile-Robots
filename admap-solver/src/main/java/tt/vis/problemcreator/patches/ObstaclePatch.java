package tt.vis.problemcreator.patches;

import cz.agents.alite.vis.layer.VisLayer;
import tt.euclid2i.Region;
import tt.euclid2i.vis.RegionsLayer;
import tt.vis.problemcreator.main.ExtensibleProblem;

import java.awt.*;
import java.util.Collection;
import java.util.Collections;


public class ObstaclePatch implements ProblemPatch {

    private Region obstacle;
    private VisLayer layer;


    public ObstaclePatch(Region region) {
        this.obstacle = region;

        this.layer = RegionsLayer.create(new RegionsLayer.RegionsProvider() {
            @Override
            public Collection<? extends Region> getRegions() {
                return Collections.singletonList(obstacle);
            }
        }, Color.black, Color.LIGHT_GRAY);
    }

    @Override
    public void apply(ExtensibleProblem problem) {
        problem.addObstacle(obstacle);
    }

    @Override
    public void paint(Graphics2D g) {
        layer.paint(g);
    }
}
