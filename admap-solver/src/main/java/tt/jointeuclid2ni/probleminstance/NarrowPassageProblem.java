package tt.jointeuclid2ni.probleminstance;

import java.util.Collection;
import java.util.LinkedList;

import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.probleminstance.Environment;
import tt.euclid2i.region.Rectangle;

public class NarrowPassageProblem extends TrajectoryCoordinationProblemImpl {

    public NarrowPassageProblem() {

        super(new Environment() {

            @Override
            public Collection<Region> getObstacles() {
                Collection<Region> obstacles = new LinkedList<Region>();
                obstacles.add(new Rectangle(new Point(0, 0), new Point(1000, 350)).toPolygon());
                obstacles.add(new Rectangle(new Point(0, 0), new Point(450, 450)).toPolygon());
                obstacles.add(new Rectangle(new Point(550, 0), new Point(1000, 450)).toPolygon());
                obstacles.add(new Rectangle(new Point(0, 550), new Point(1000, 1000)).toPolygon());
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

        starts[0] = new Point(100, 500);
        targets[0] = new Point(900, 500);
        bodyRadiuses[0] = 40;

        starts[1] = new Point(900, 500);
        targets[1] = new Point(100, 500);
        bodyRadiuses[1] = 40;
    }

}

