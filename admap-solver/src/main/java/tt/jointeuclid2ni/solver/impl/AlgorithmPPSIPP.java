package tt.jointeuclid2ni.solver.impl;

import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.toggle.KeyToggleLayer;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPathSimple;
import org.jgrapht.util.HeuristicToGoal;
import org.jgrapht.util.heuristics.PerfectHeuristic;
import tt.euclid2i.*;
import tt.euclid2i.Point;
import tt.euclid2i.discretization.LazyGrid;
import tt.euclid2i.util.Util;
import tt.euclidtime3i.sipp.SippEdge;
import tt.euclidtime3i.sipp.SippGoal;
import tt.euclidtime3i.sipp.SippHeuristic;
import tt.euclidtime3i.sipp.SippNode;
import tt.euclidtime3i.sipp.SippUtils;
import tt.euclidtime3i.sipp.SippWrapper;
import tt.euclidtime3i.sipp.intervals.Interval;
import tt.euclidtime3i.sipprrts.DynamicObstaclesImpl;
import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;
import tt.euclidtime3i.sipp.*;
import tt.jointtraj.solver.SearchResult;
import tt.util.NotImplementedException;
import tt.vis.GraphLayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AlgorithmPPSIPP extends AbstractAlgorithm {

    public AlgorithmPPSIPP() {
        super();
    }

    @Override
    public SearchResult solveProblem() {
        DirectedGraph<Point, Line>[] graph = createGraphs(problem);

        List<SegmentedTrajectory> trajectories = new ArrayList<SegmentedTrajectory>();
        List<Integer> bodyRadiuses = new ArrayList<Integer>();

        for (int i = 0; i < problem.nAgents(); i++) {
            SegmentedTrajectory[] trajArr = trajectories.toArray(new SegmentedTrajectory[trajectories.size()]);
            int[] radArr = new int[bodyRadiuses.size()];
            for (int j = 0; j < radArr.length; j++) {
                radArr[j] = bodyRadiuses.get(j);
            }

            DynamicObstaclesImpl environment = new DynamicObstaclesImpl(trajArr, radArr, params.maxTime);

            SippWrapper wrapper = new SippWrapper(graph[i], environment, problem.getBodyRadius(i), (int) problem.getMaxSpeed(i), 2, params.maxTime);
            SippNode start = new SippNode(problem.getStart(i), Interval.toInfinity(0), 0);
            SippHeuristic heuristic = new SippHeuristic(getHeuristic(graph[i], problem.getTarget(i)),1);
            SippGoal goal = new SippGoal(problem.getTarget(i), params.maxTime);

            GraphPath<SippNode, SippEdge> path = AStarShortestPathSimple.findPathBetween(wrapper, heuristic, start, goal);
            SegmentedTrajectory trajectory = SippUtils.parseTrajectory(path, params.maxTime);

            if (trajectory == null)
                return new SearchResult(null, false);

            trajectories.add(trajectory);
            bodyRadiuses.add(problem.getBodyRadius(i));
        }

        return new SearchResult(trajectories.toArray(new EvaluatedTrajectory[trajectories.size()]), true);
    }

    public HeuristicToGoal<Point> getHeuristic(Graph<Point, Line> graph, Point goal) {
        switch (params.heuristic) {
            case PERFECT:
                return new PerfectHeuristic<Point, Line>(graph, goal);
            case L1:
                return new tt.euclid2i.discretization.L1Heuristic(goal);
            case L2:
                return new tt.euclid2i.discretization.L1Heuristic(goal);
            default:
                throw new NotImplementedException();
        }
    }

    private DirectedGraph<Point, Line>[] createGraphs(EarliestArrivalProblem problem) {
        DirectedGraph<Point, Line> graph[] = new DirectedGraph[problem.nAgents()];
        for (int i = 0; i < problem.nAgents(); i++) {
            graph[i] = preparePlanningGraphAgent(i);
        }
        return graph;
    }

    protected DirectedGraph<Point, Line> preparePlanningGraphAgent(int i) {

        Collection<Region> inflatedObstacles = Util.inflateRegions(problem.getObstacles(), problem.getBodyRadius(i));

        final DirectedGraph<tt.euclid2i.Point, Line> grid = new LazyGrid(
                problem.getStart(i),
                inflatedObstacles,
                problem.getEnvironment().getBoundary().getBoundingBox(),
                params.gridPattern,
                params.gridStep).generateFullGraph();


        KeyToggleLayer toggleLayer = KeyToggleLayer.create("g");
        toggleLayer.addSubLayer(
                GraphLayer.create(new GraphLayer.GraphProvider<tt.euclid2i.Point, Line>() {

                    @Override
                    public Graph<tt.euclid2i.Point, Line> getGraph() {
                        return grid;
                    }
                }, new tt.euclid2i.vis.ProjectionTo2d(), Color.GRAY, Color.GRAY, 1, 4)
        );
        VisManager.registerLayer(toggleLayer);
        // create spatio-temporal graph

        return grid;
    }
}
