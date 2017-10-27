package tt.jointeuclid2ni.probleminstance;


import java.util.Collection;
import java.util.List;
import java.util.Random;

import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.probleminstance.Environment;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.util.Util;
import tt.vis.environentcreator.PolygonParser;

public class DejviceProblemRandom extends TrajectoryCoordinationProblemImpl {

    private static final String OBSTACLE_FILE = "src/main/resources/problems/dejvice_precise.txt";

    Random random;

    public DejviceProblemRandom(int nAgents, int seed) {
        super(new Environment() {
            List<Region> polygons = PolygonParser.load(OBSTACLE_FILE);

            @Override
            public Collection<Region> getObstacles() {
                return polygons;
            }

            @Override
            public Region getBoundary() {
                return new Rectangle(new Point(0, 0), new Point(1000, 1000));
            }

        }, nAgents);

        random = new Random(seed);
        generateMissions();
    }

    protected void generateMissions() {
        for (int i = 0; i < nAgents; i++) {
            starts[i] = Util.sampleFreeSpace((Rectangle) environment.getBoundary(), environment.getObstacles(), random);
            targets[i] = Util.sampleFreeSpace((Rectangle) environment.getBoundary(), environment.getObstacles(), random);
            bodyRadiuses[i] = 15;
        }
    }


}
