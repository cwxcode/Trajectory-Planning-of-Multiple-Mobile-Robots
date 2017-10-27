package tt.jointeuclid2ni.probleminstance.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.AStarShortestPathSimple;

import tt.euclid2i.HeuristicToPoint;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.Trajectory;
import tt.euclid2i.discretization.ObstacleWrapper;
import tt.euclid2i.probleminstance.Environment;
import tt.euclid2i.region.Circle;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.trajectory.SegmentedTrajectoryFactory;
import tt.euclid2i.util.SeparationDetector;
import tt.euclid2i.util.Util;
import tt.jointeuclid2ni.probleminstance.AgentMission;
import tt.jointeuclid2ni.probleminstance.AgentMissionImpl;
import tt.jointeuclid2ni.probleminstance.TrajectoryCoordinationProblem;
import tt.jointeuclid2ni.probleminstance.generator.exception.MissionNotAddedException;
import tt.jointeuclid2ni.probleminstance.generator.exception.ProblemNotCreatedException;
import tt.jointtraj.util.Discretization;
import tt.util.Verbose;
import tt.vis.problemcreator.main.ExtensibleProblem;

public class ConflictGenerator {

    private static final int MAX_SPEED = 1;
    private static final int MAX_ATTEMPTS_PROBLEM = 500;
    private static final int MAX_ATTEMPTS_MISSION = 150;
    private static final int MAX_ATTEMPTS_START = 500;
    private static final int MAX_ATTEMPTS_TARGET = 500;
    private static final float RADIUS_BUFFER_GRACE_COEF = 1.3f; 

    private static DirectedGraph<Point, Line>[] toGraphArray(DirectedGraph<Point, Line> planningGraph, int nAgents) {

        @SuppressWarnings("unchecked")
		final DirectedGraph<Point, Line>[] directedGraphs = new DirectedGraph[nAgents];
        for (int a = 0; a < nAgents; a++) {
            directedGraphs[a] = planningGraph;
        }
        return directedGraphs;
    }
    
    private static Collection<Point>[] toEndpointArray(Collection<Point> docks, int nAgents) {
    	
    	@SuppressWarnings("unchecked")
    	Collection<Point>[] endpointArray = new Collection[nAgents];
    	for (int i = 0; i < endpointArray.length; i++) {
			endpointArray[i] = docks;
		}
    	
    	return endpointArray;
	}

    private static DirectedGraph<Point, Line>[] createGridGraphs(Environment environment, int[] bodyRadiuses, int[][] gridPattern, int step) {
    	int nAgents = bodyRadiuses.length;
    	@SuppressWarnings("unchecked")
        final DirectedGraph<Point, Line>[] directedGraphs = new DirectedGraph[nAgents];

        for (int a = 0; a < nAgents; a++) {
            directedGraphs[a] = Discretization.createGrid(environment, bodyRadiuses[a], gridPattern, step);
        }
        return directedGraphs;
    }

    static private List<Point>[] fillVertexSets(Rectangle missionRegion, DirectedGraph<Point, Line>[] graphs, int[] bodyRadiuses) {
        int nAgents = bodyRadiuses.length;
        @SuppressWarnings("unchecked")
        List<Point>[] vertexList = new List[nAgents];
        for (int i = 0; i < vertexList.length; i++) {
            Set<Point> vertexSet = graphs[i].vertexSet();

			Rectangle shrinkedMissionRegion = shrinkRectangle(missionRegion, bodyRadiuses[i]);

            vertexList[i] = new ArrayList<Point>();
            for (Point vertex : vertexSet) {
                if (shrinkedMissionRegion.isInside(vertex)) {
                    vertexList[i].add(vertex);
                }
            }
        }
        return vertexList;
    }

    public int calculateMaxTime(Environment environment) {
        Rectangle bounds = environment.getBoundary().getBoundingBox();
        return (int) (3 * bounds.getCorner1().distance(bounds.getCorner2()) / MAX_SPEED);
    }
    
    public static TrajectoryCoordinationProblem generateInstance(Environment environment,
		    int[] bodyRadiuses,
            float[] maxSpeeds,
		    int[][] gridPattern, 
		    int step,                                                                 
		    Rectangle missionRegion,
		    int maxTime,
		    boolean oneCluster, 
		    boolean startAndGoalsCanOverlap,
		    boolean sgAvoiding, 
		    Random rnd) throws ProblemNotCreatedException {
    	
        if (missionRegion == null) {
            missionRegion = environment.getBoundary().getBoundingBox();
        }
        
        DirectedGraph<Point, Line>[] planningGraphs = createGridGraphs(environment, bodyRadiuses, gridPattern, step);
        
        List<Point>[] endpoints = fillVertexSets(missionRegion, planningGraphs, bodyRadiuses);
        
        
		ExtensibleProblem problem = createProblem(environment, planningGraphs, bodyRadiuses, maxSpeeds, endpoints, maxTime, oneCluster, startAndGoalsCanOverlap, sgAvoiding, rnd);
        return problem;
    }
    
	public static TrajectoryCoordinationProblem generateInstance(
			Environment environment, 
			DirectedGraph<Point, Line> planningGraph,
			Collection<Point> docks,
			int[] bodyRadiuses,
            float[] maxSpeeds,
            int maxTime,
			boolean oneCluster, 
			boolean startAndGoalsCanOverlap,
			boolean sgAvoiding, 
			Random rnd)
			throws ProblemNotCreatedException {

		int nAgents = bodyRadiuses.length;

		DirectedGraph<Point, Line>[] planningGraphs = toGraphArray(planningGraph, nAgents);
		Collection<Point>[] endpointsArray = toEndpointArray(docks, nAgents);
		
		ExtensibleProblem problem = createProblem(environment, planningGraphs, bodyRadiuses, maxSpeeds, endpointsArray, maxTime, oneCluster, startAndGoalsCanOverlap, sgAvoiding, rnd);

		if (planningGraph != null) {
			problem.setPlanningGraph(planningGraph);
		}

		return problem;
	}

	static private ExtensibleProblem createProblem(
    		Environment environment, 
    		DirectedGraph<Point, Line>[] planningGraphs, 
    		int[] bodyRadiuses,
            float[] maxSpeeds,
    		Collection<Point>[] possibleEndpoints, 
    		int maxTime, 
    		boolean requireConflict, 
    		boolean startAndGoalsCanOverlap,
    		boolean requireStartGoalAvoiding,
    		Random rnd) throws ProblemNotCreatedException {
    	
        ExtensibleProblem currentProblem = new ExtensibleProblem();
        currentProblem.setEnvironment(environment);

        for (int att = 0; att < MAX_ATTEMPTS_PROBLEM; att++) {
            try {
                Verbose.printf(" -- -- %d. attempt to fill the problem with missions%n", att);

                currentProblem.clearAgentMissions();
				addAgentMissions(currentProblem, planningGraphs, possibleEndpoints, bodyRadiuses, maxSpeeds,
                        maxTime, requireConflict, startAndGoalsCanOverlap, requireStartGoalAvoiding, rnd);
                return currentProblem;

            } catch (MissionNotAddedException ex) {
                Verbose.printf(" -- --  mission not added%n", att);
            }
        }

        throw new ProblemNotCreatedException();
    }

    static private void addAgentMissions(ExtensibleProblem problem, DirectedGraph<Point, Line>[] planningGraphs, Collection<Point>[] possibleEndpoints, int[] bodyRadiuses, float[] maxSpeeds, int maxTime, boolean requireConflict, boolean startAndGoalsCanOverlap, boolean requireStartGoalAvoiding, Random rnd) throws MissionNotAddedException {
        int nAgents = planningGraphs.length;
    	ArrayList<Trajectory> otherTrajectories = new ArrayList<Trajectory>();
        for (int i = 0; i < nAgents ; i++) {
        	addMission(problem, planningGraphs[i], possibleEndpoints[i], bodyRadiuses[i], maxSpeeds[i], maxTime, otherTrajectories, requireConflict, startAndGoalsCanOverlap, requireStartGoalAvoiding, rnd);
        }
    }

    static private List<Point> filterVertices(List<Point> list, Collection<Region> startGoalRegions, int agentBodyRadius) {

    	Collection<Region> inflatedRegions = Util.inflateRegions(startGoalRegions, agentBodyRadius);
    	LinkedList<Point> filtered = new LinkedList<Point>();
    	for (Point p : list) {
    		if (Util.isInFreeSpace(p, inflatedRegions)) {
    			filtered.add(p);
    		}
    	}

    	return filtered;
	}

	static private void addMission(ExtensibleProblem problem, DirectedGraph<Point, Line> planningGraph, Collection<Point> possibleEndpoints, int bodyRadius, float maxSpeed, int maxTime,
			ArrayList<Trajectory> otherTrajectories, boolean requireConflict, boolean startAndGoalsCanOverlap, boolean requireStartGoalAvoiding, Random rnd) throws MissionNotAddedException {
        
		int[] separations = calculateSeparations(bodyRadius, problem.getBodyRadiuses());
        Trajectory[] otherTrajectoriesArray = otherTrajectories.toArray(new Trajectory[otherTrajectories.size()]);

        int attempts = 0;
        do {
            
            AgentMission mission = generateMission(problem, bodyRadius, maxSpeed, possibleEndpoints, startAndGoalsCanOverlap, requireStartGoalAvoiding, planningGraph, problem.getAgentMissions(), rnd);
           	Trajectory trajectory = solveMission(planningGraph, mission, maxTime, problem.getAgentMissions(), requireStartGoalAvoiding);
           	
           	if (trajectory != null) {
               	if (otherTrajectories.isEmpty() || 
               		(requireConflict ? SeparationDetector.hasAnyPairwiseConflict(trajectory, otherTrajectoriesArray, separations, 5) : true)) {
               		problem.addAgent(mission);
                    otherTrajectories.add(trajectory);
                    return;
                }
           	}
        } while (attempts++ < MAX_ATTEMPTS_MISSION);

        throw new MissionNotAddedException();
    }

    static private int[] calculateSeparations(int bodyRadius, int[] otherBodyRadiuses) {
        int[] separations = new int[otherBodyRadiuses.length];
        for (int i = 0; i < separations.length; i++) {
            separations[i] = otherBodyRadiuses.length + bodyRadius;
        }
        return separations;
    }

	static private AgentMission generateMission(ExtensibleProblem problem,
			int bodyRadius, float maxSpeed, Collection<Point> possibleEndpoints,
			boolean startAndGoalsCanOverlap, boolean sgAvoiding, 
			DirectedGraph<Point, Line> planningGraph, AgentMission[] otherMissions, 
			Random rnd) throws MissionNotAddedException {

        Point start = null;
        int startPointAttempts = 0;
        
        boolean collidesWithOtherStart;
        boolean collidesWithOtherGoal;
        boolean connectivityLost; 
        do {
            start = randomVertex(possibleEndpoints, rnd);
            Verbose.printf(" -- -- -- -- -- trying start S:%s %n", start);

            if (startPointAttempts++ > MAX_ATTEMPTS_START) {
                throw new MissionNotAddedException();
            }
            
            collidesWithOtherStart = collidesWithOtherStart(start, bodyRadius, problem);
            collidesWithOtherGoal = (!startAndGoalsCanOverlap ? collidesWithOtherTarget(start, bodyRadius, problem) : false);
            connectivityLost = (sgAvoiding ? !maintainsConnectivityIfRemoved(start, null, bodyRadius, planningGraph, otherMissions) : false);
            Verbose.printf(" -- -- -- -- -- -- start collision: %b goal collision: %b connectivityLost: %b %n", collidesWithOtherStart, collidesWithOtherGoal, connectivityLost);
            
        } while (collidesWithOtherStart || collidesWithOtherGoal || connectivityLost);

        Point target = null;
        int targetPointAttempts = 0;
        do {
            target = randomVertex(possibleEndpoints, rnd);
            Verbose.printf(" -- -- -- -- -- trying goal G:%s %n", target);

            if (targetPointAttempts++ > MAX_ATTEMPTS_TARGET) {
                throw new MissionNotAddedException();
            }
            
            collidesWithOtherGoal = collidesWithOtherTarget(target, bodyRadius, problem);
            collidesWithOtherStart = (!startAndGoalsCanOverlap ? collidesWithOtherStart(target, bodyRadius, problem) : false);
            connectivityLost = (sgAvoiding ? !maintainsConnectivityIfRemoved(start, target, bodyRadius, planningGraph, otherMissions) : false);
            
        } while (start == target || collidesWithOtherGoal || collidesWithOtherStart || connectivityLost);

        Verbose.printf(" -- -- -- -- new random mission S:%s G:%s%n", start, target);

        return new AgentMissionImpl(start, target, bodyRadius, maxSpeed);
    }
	
	private static boolean maintainsConnectivityIfRemoved(Point start, Point target, 
			int bodyRadius, DirectedGraph<Point, Line> planningGraph, 
			AgentMission[] otherMissions) {
		
		for (int i=0; i<otherMissions.length; i++) {
			// check connectivity for agent i
			Point otherAgentStart = otherMissions[i].getStart();
			Point otherAgentTarget = otherMissions[i].getTarget(); 
			int otherAgentBodyRadius = otherMissions[i].getBodyRadius();
			AgentMission[] otherAgentOtherMissions = new AgentMission[otherMissions.length];
			
			int j=0;
			for (AgentMission mission : otherMissions) {
				if (!mission.getStart().equals(otherAgentStart)) {
					otherAgentOtherMissions[j] = mission; 
					j++;
				}
			}
			
			if (target == null) {
				// Hack! We will put start = target to be able to reuse removeStartsTargetsOfOtherAgents method
				otherAgentOtherMissions[j] = new AgentMissionImpl(start, start, bodyRadius, 1); 
			} else {
				otherAgentOtherMissions[j] = new AgentMissionImpl(start, target, bodyRadius, 1); 
			}
			
			DirectedGraph<Point, Line> augmentedGraph = removeStartsTargetsOfOtherAgents(planningGraph, otherAgentOtherMissions, otherAgentBodyRadius);
			
			if (!isReachable(otherAgentStart, otherAgentTarget, augmentedGraph)) {
				Verbose.printf(" -- -- -- -- -- -- reachablility lost between: %s and: %s %n", otherAgentStart, otherAgentTarget);
				return false;
			}
		}
		
		return true;
	}

	private static Trajectory solveMission(DirectedGraph<Point, Line> graph,
			AgentMission mission, int maxTime, AgentMission[] otherMissions,
			boolean requireStartGoalAvoiding) {
    	
        Point target = mission.getTarget();
        Point start = mission.getStart();
        int bodyRadius = mission.getBodyRadius();
        
        if (requireStartGoalAvoiding) {
        	graph = removeStartsTargetsOfOtherAgents(graph, otherMissions, bodyRadius);
        }

        GraphPath<Point, Line> path = AStarShortestPathSimple.findPathBetween(graph, new HeuristicToPoint(target), start, target);

        if (path == null)
            return null;

        assert isReachable(start, target, graph);
        
        return SegmentedTrajectoryFactory.createConstantSpeedTrajectory(path, 0, (int)mission.getMaxSpeed(),
                maxTime, path.getWeight() / MAX_SPEED);
    }

    static private DirectedGraph<Point, Line> removeStartsTargetsOfOtherAgents(
			DirectedGraph<Point, Line> directedGraph, AgentMission[] missions, int bodyRadius) {

    	Collection<Region> startTargetRegions = new LinkedList<Region>();

    	for (int i=0; i<missions.length; i++) {
    		startTargetRegions.add(new Circle(missions[i].getStart(), missions[i].getBodyRadius() + (int) Math.ceil(bodyRadius*RADIUS_BUFFER_GRACE_COEF)));
    		startTargetRegions.add(new Circle(missions[i].getTarget(), missions[i].getBodyRadius()  + (int) Math.ceil(bodyRadius*RADIUS_BUFFER_GRACE_COEF)));
    	}

    	return new ObstacleWrapper<Point, Line>(directedGraph, startTargetRegions);
	}

	static private boolean collidesWithOtherStart(Point start, int bodyRadius, ExtensibleProblem currentProblem) {
		AgentMission[] otherMissions = currentProblem.getAgentMissions();
        for (AgentMission mission : otherMissions) {
            double sep = mission.getBodyRadius() + (int) Math.ceil(bodyRadius*RADIUS_BUFFER_GRACE_COEF);
            double distance = mission.getStart().distance(start);

            // sometimes it happened that agents with exactly sep == distance were considered in collision by separation detector
            if (sep / distance > 0.99)
                return true;
        }
        return false;
    }

	static private boolean collidesWithOtherTarget(Point start, int bodyRadius, ExtensibleProblem currentProblem) {
        AgentMission[] otherMissions = currentProblem.getAgentMissions();
        for (AgentMission mission : otherMissions) {
            double sep = mission.getBodyRadius() + (int) Math.ceil(bodyRadius*RADIUS_BUFFER_GRACE_COEF);
            double distance = mission.getTarget().distance(start);

            // sometimes it happend that agents with exactly sep == distance were considered in collision by separation detector
            if (sep / distance > 0.99)
                return true;
        }
        return false;
    }
	
	static private boolean isReachable(Point from, Point to, DirectedGraph<Point, Line> graph) {
		Queue<Point> open = new LinkedList<Point>();
		Set<Point> closed = new HashSet<Point>();
		
		open.add(from);
		
		while(!open.isEmpty()) {
			Point current = open.poll();
			
			if (current.equals(to)) {
				return true;
			}
			
			for (Line edge : graph.outgoingEdgesOf(current)) {
				Point neighbor = Graphs.getOppositeVertex(graph, edge, current);
				if (!closed.contains(neighbor)) {
					open.add(neighbor);
					closed.add(neighbor);
				}
			}
		}
		
		return false;
	}
    
    private static Point randomVertex(Collection<Point> list, Random rnd) {
        int size = list.size();
        return list.toArray(new Point[0])[rnd.nextInt(size)];
    }



    protected static Rectangle shrinkRectangle(Rectangle rect, int by) {
            return new Rectangle(new Point(rect.getCorner1().x+by, rect.getCorner1().y+by),
                                 new Point(rect.getCorner2().x-by, rect.getCorner2().y-by)
                                 );
    }

}
