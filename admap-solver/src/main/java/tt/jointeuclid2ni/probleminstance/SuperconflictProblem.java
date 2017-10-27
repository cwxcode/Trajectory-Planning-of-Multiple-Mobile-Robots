package tt.jointeuclid2ni.probleminstance;

import tt.euclid2i.Point;
import tt.euclid2i.probleminstance.Environment;
import tt.euclid2i.probleminstance.RandomEnvironment;

public class SuperconflictProblem extends TrajectoryCoordinationProblemImpl {

    int agentBodyRadius;

    public SuperconflictProblem(int nAgents, int agentBodyRadius) {
        this(new RandomEnvironment(1000, 1000, 0, 300, 0), nAgents, agentBodyRadius);
    }

    public SuperconflictProblem(Environment environment, int nAgents, int agentBodyRadius) {
       super(environment, nAgents);
       this.agentBodyRadius = agentBodyRadius;
       generateMissions();
    }

    protected void generateMissions() {
        final double ANGLE_BETWEEN_AIRPLANES = (2 * Math.PI ) / nAgents;
        double CIRCLE_RADIUS = 400;
        Point CENTER = new Point(500, 500);

        for (int i=0; i < nAgents; i++) {
            double angle = i*ANGLE_BETWEEN_AIRPLANES;

            Point start = new Point(
            		(int) Math.round((CENTER.x + Math.cos(angle)*CIRCLE_RADIUS)),
            		(int) Math.round((CENTER.y + Math.sin(angle)*CIRCLE_RADIUS)));
            
            Point target = new Point(
            		(int) Math.round((CENTER.x + Math.cos(angle+Math.PI)*CIRCLE_RADIUS)), 
            		(int) Math.round((CENTER.y + Math.sin(angle+Math.PI)*CIRCLE_RADIUS)));

            starts[i] = start;
            targets[i] = target;
            bodyRadiuses[i] = this.agentBodyRadius;

        }
    }

}

