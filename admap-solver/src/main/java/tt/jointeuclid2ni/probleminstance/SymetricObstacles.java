package tt.jointeuclid2ni.probleminstance;

import java.util.Collection;
import java.util.LinkedList;

import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.probleminstance.Environment;
import tt.euclid2i.region.Rectangle;

public class SymetricObstacles extends TrajectoryCoordinationProblemImpl {

    Environment env;

    public SymetricObstacles() {

        super(new Environment() {
            Collection<Region> obstacles;

            {
                obstacles = new LinkedList<Region>();
                obstacles.add(new Rectangle(new Point(201, 205), new Point(408, 404)).toPolygon());
                obstacles.add(new Rectangle(new Point(202, 606), new Point(407, 803)).toPolygon());
                obstacles.add(new Rectangle(new Point(603, 207), new Point(806, 402)).toPolygon());
                obstacles.add(new Rectangle(new Point(604, 608), new Point(805, 801)).toPolygon());
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

        starts[0] = new Point(100, 100);
        targets[0] = new Point(900, 900);
        bodyRadiuses[0] = 60;

        starts[1] = new Point(100, 900);
        targets[1] = new Point(900, 100);
        bodyRadiuses[1] = 60;
    }

}

