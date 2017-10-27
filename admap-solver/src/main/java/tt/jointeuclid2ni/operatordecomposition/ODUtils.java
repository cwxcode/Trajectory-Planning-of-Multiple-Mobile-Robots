package tt.jointeuclid2ni.operatordecomposition;

import org.jgrapht.GraphPath;

import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.SegmentedTrajectory;
import tt.euclid2i.trajectory.BasicSegmentedTrajectory;
import tt.euclid2i.trajectory.ConstantStepSegmentedTrajectory;
import tt.euclid2i.trajectory.SegmentedTrajectoryFactory;
import tt.euclidtime3i.discretization.Straight;

import java.util.ArrayList;
import java.util.List;

public class ODUtils {

    @SuppressWarnings("unchecked")
    public static EvaluatedTrajectory[] toTrajectories(GraphPath<ODNode, ODEdge> path, Point[] goals, int maxDuration, float speed, int timeStep) {
        if (path == null) {
            return null;
        } else {
            return divideIntoIndividualEdgeLists(path, goals, maxDuration, speed, timeStep);
        }
    }

    private static EvaluatedTrajectory[] divideIntoIndividualEdgeLists(GraphPath<ODNode, ODEdge> path, Point[] goals, int maxDuration, double speed, int timeStep) {
        List<ODEdge> edgeList = path.getEdgeList();
        ODNode startVertex = path.getStartVertex();
        int size = startVertex.getSize();

        List<Line>[] individualPaths = initializeLists(size);
        //double costs[] = new double[size];
        for (ODEdge edge : edgeList) {
            int i = edge.getSource().agentToExpand();
            Line line = edge.getLine();
            individualPaths[i].add(line);
        }

        return getEvaluatedTrajectories(startVertex.getAgentPositions(), individualPaths, timeStep, goals, size, maxDuration);
    }

    private static EvaluatedTrajectory[] getEvaluatedTrajectories(Point[] startVertex, List<Line>[] individualPaths, int edgeDuration, Point[] goals, int size, int maxDuration) {
        EvaluatedTrajectory[] trajectories = new EvaluatedTrajectory[size];

        for (int i = 0; i < size; i++) {
			if (individualPaths[i].isEmpty())
                trajectories[i] = SegmentedTrajectoryFactory.createStationaryTrajectory(startVertex[i], 0, maxDuration, 0);
            else
                trajectories[i] = createConstantEdgeDurationTrajectory(individualPaths[i], 0, edgeDuration, goals[i], true,  maxDuration);
        }

        return trajectories;
    }

    private static List<Line>[] initializeLists(int lists) {
        List<Line>[] individualPaths = new List[lists];
        for (int i = 0; i < lists; i++) {
            individualPaths[i] = new ArrayList<Line>();
        }
        return individualPaths;
    }
    
    public static SegmentedTrajectory createConstantEdgeDurationTrajectory(List<Line> edgeList, int startTime, int edgeDuration, Point goal, boolean waitOnGoalIsFree, int duration) {
        List<Straight> segments = new ArrayList<Straight>();
        int currentTime = startTime;
        int oppositeTime;
        double cost = 0;

        for (Line edge : edgeList) {
            Point start = edge.getStart();
            Point end = edge.getEnd();
            	
            oppositeTime = currentTime + edgeDuration;
            segments.add(new Straight(new tt.euclidtime3i.Point(start,currentTime), new tt.euclidtime3i.Point(end,oppositeTime)));

            if(waitOnGoalIsFree && start.equals(goal) && end.equals(goal)) {
            	cost += 0;
            } else {
            	cost += edgeDuration;
            }
            
            currentTime = oppositeTime;
        }

        return new ConstantStepSegmentedTrajectory(segments, edgeDuration, cost);
    }

}
