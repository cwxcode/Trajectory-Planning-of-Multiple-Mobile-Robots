package tt.euclid2i.probleminstance;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;

import javax.vecmath.Point2d;

import tt.euclid2i.Region;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.vis.RegionsLayer;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;

public class VisUtil {
    public static void initVisualization(final Environment env, String title) {
        VisManager.setInitParam(title, 700, 700);
        VisManager.setSceneParam(new VisManager.SceneParams() {

            @Override
            public Point2d getDefaultLookAt() {

                Rectangle bounds = env.getBoundary().getBoundingBox();

                double x = bounds.getCorner1().x
                        + ((bounds.getCorner2().x - bounds.getCorner1().x) / 2);
                double y = bounds.getCorner1().y
                        + ((bounds.getCorner2().y - bounds.getCorner1().y) / 2);

                return new Point2d(x, y);
            }

            @Override
            public double getDefaultZoomFactor() {
                return 0.5;
            }

        });

        VisManager.init();
    }

    public static void visualizeEnvironment(final Environment env) {
        // background
        VisManager.registerLayer(ColorLayer.create(Color.WHITE));

        // boundary
        VisManager.registerLayer(RegionsLayer.create(
                new RegionsLayer.RegionsProvider() {

                    @Override
                    public Collection<Region> getRegions() {
                        return Collections.singleton(env.getBoundary());
                    }

                }, Color.RED, Color.GRAY));


        VisManager.registerLayer(RegionsLayer.create(
                new RegionsLayer.RegionsProvider() {

                    @Override
                    public Collection<Region> getRegions() {
                        return env.getObstacles();
                    }

                }, Color.BLACK, Color.GRAY));


        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());

    }
}
