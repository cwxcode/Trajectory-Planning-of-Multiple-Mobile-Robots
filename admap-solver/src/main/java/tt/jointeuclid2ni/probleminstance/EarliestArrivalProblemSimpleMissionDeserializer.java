package tt.jointeuclid2ni.probleminstance;

import java.util.LinkedList;
import java.util.List;

import tt.euclid2i.Point;
import tt.euclid2i.probleminstance.Environment;

/**
 * Parses mission specification, i.e. start, destination and bodyRadius for each agent in a simple string format.
 * The expected format is:
 * StartX1,StartY1:TargetX1,TargetY1:BodyRadius1:MaxSpeed1;StartX2,StartY2:TargetX2,TargetY2:BodyRadius2:MaxSpeed2;...
 */

public class EarliestArrivalProblemSimpleMissionDeserializer {

    public static EarliestArrivalProblem deserialize(Environment env, String mission) {

        class Agent {

            public Agent(Point start, Point target, int bodyRadius, int maxSpeed) {
                super();
                this.start = start;
                this.target = target;
                this.bodyRadius = bodyRadius;
                this.maxSpeed = maxSpeed;
            }

            Point start;
            Point target;
            int bodyRadius;
            int maxSpeed;
        }

        List<Agent> agents = new LinkedList<Agent>();

        String[] agentsStr = mission.split(";");

        for (String agentStr : agentsStr) {
            String[] params = agentStr.split(":");

            if (params.length == 4) {
                String[] startStr = params[0].split(",");
                String[] targetStr = params[1].split(",");

                Point start = new Point(Integer.parseInt(startStr[0]), Integer.parseInt(startStr[1]));
                Point target = new Point(Integer.parseInt(targetStr[0]), Integer.parseInt(targetStr[1]));
                int bodyRadius = Integer.parseInt(params[2]);
                int maxSpeed = Integer.parseInt(params[3]);

                agents.add(new Agent(start, target, bodyRadius, maxSpeed));
            } else {
                throw new RuntimeException("Invalid mission specification format: " + agentStr);
            }
        }


        Point[] starts = new Point[agents.size()];
        Point[] targets = new Point[agents.size()];
        int[] bodyRadiuses = new int[agents.size()];
        int[] maxSpeeds = new int[agents.size()];


        for (int i = 0; i < agents.size(); i++) {
            starts[i] = agents.get(i).start;
            targets[i] = agents.get(i).target;
            bodyRadiuses[i] = agents.get(i).bodyRadius;
            maxSpeeds[i] = agents.get(i).maxSpeed;
        }

        return new TrajectoryCoordinationProblemImpl(env, starts, targets, bodyRadiuses, maxSpeeds, null);

    }
}
