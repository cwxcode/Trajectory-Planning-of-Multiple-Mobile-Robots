package tt.jointeuclid2ni.probleminstance;

import java.util.Collection;
import java.util.LinkedList;

import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.probleminstance.Environment;
import tt.euclid2i.region.Rectangle;

public class AsymetricObstacles extends TrajectoryCoordinationProblemImpl {

    public AsymetricObstacles() {

        super(new Environment() {
            Collection<Region> obstacles;

            {
                obstacles = new LinkedList<Region>();
                obstacles.add(new Rectangle(new Point(201, 205), new Point(408, 504)).toPolygon());
                obstacles.add(new Rectangle(new Point(610, 250), new Point(666, 501)).toPolygon());
            }

            @Override
            public Collection<Region> getObstacles() {
                return obstacles;
            }

            @Override
            public Region getBoundary() {
                return new Rectangle(new Point(0, 0), new Point(1000, 1000));
            }

        }, 2);

        generateMissions();

    }

    protected void generateMissions() {

        starts[0] = new Point(500, 100);
        targets[0] = new Point(650, 650);
        bodyRadiuses[0] = 40;

        starts[1] = new Point(650, 650);
        targets[1] = new Point(500, 100);
        bodyRadiuses[1] = 40;
    }
}
