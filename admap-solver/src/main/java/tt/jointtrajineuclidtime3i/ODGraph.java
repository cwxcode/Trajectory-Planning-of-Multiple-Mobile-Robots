package tt.jointtrajineuclidtime3i;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.SingleEdgeGraphPath;

import tt.euclid2i.trajectory.StraightSegmentTrajectory;
import tt.euclidtime3i.EvaluatedTrajectory;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.Trajectory;
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.region.MovingCircle;
import tt.euclidtime3i.trajectory.Trajectories;
import tt.euclidtime3i.util.IntersectionChecker;
import tt.util.NotImplementedException;

/** Operator Decomposition (Trevor Standley and Korf, AAAI 2010).  **/
public class ODGraph implements DirectedGraph<State, Transition> {

    private DirectedGraph<tt.euclidtime3i.Point, Straight> motionModels[];
    private int[] bodyRadiuses;

    public ODGraph(DirectedGraph<tt.euclidtime3i.Point, Straight>[] motionModels, int[] bodyRadiuses) {
        this.motionModels = motionModels;
        this.bodyRadiuses = bodyRadiuses;
    }

    @Override
    public Transition addEdge(State arg0, State arg1) {
       throw new NotImplementedException();
    }

    @Override
    public boolean addEdge(State arg0, State arg1, Transition arg2) {
        throw new NotImplementedException();
    }

    @Override
    public boolean addVertex(State arg0) {
        throw new NotImplementedException();
    }

    @Override
    public boolean containsEdge(Transition arg0) {
        throw new NotImplementedException();
    }

    @Override
    public boolean containsEdge(State arg0, State arg1) {
        throw new NotImplementedException();
    }

    @Override
    public boolean containsVertex(State p) {
        return true;
    }

    @Override
    public Set<Transition> edgeSet() {
        throw new NotImplementedException();
    }

    @Override
    public Set<Transition> edgesOf(State vertex) {
        Set<Transition> edges = new HashSet<Transition>();
        edges.addAll(incomingEdgesOf(vertex));
        edges.addAll(outgoingEdgesOf(vertex));
        return edges;
    }

    @Override
    public Set<Transition> getAllEdges(State start, State end) {
        throw new NotImplementedException();
    }

    @Override
    public Transition getEdge(State start, State end) {
        throw new NotImplementedException();
    }

    @Override
    public EdgeFactory<State, Transition> getEdgeFactory() {
        return null;
    }

    @Override
    public State getEdgeSource(Transition edge) {
        return edge.getStart();
    }

    @Override
    public State getEdgeTarget(Transition edge) {
        return edge.getEnd();
    }
    @Override
    public double getEdgeWeight(Transition edge) {
        return edge.getCost();
    }


    @Override
    public Set<Transition> removeAllEdges(State arg0, State arg1) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeEdge(Transition arg0) {
        throw new NotImplementedException();
    }

    @Override
    public Transition removeEdge(State arg0, State arg1) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeVertex(State arg0) {
        throw new NotImplementedException();
    }

    @Override
    public Set<State> vertexSet() {
        throw new NotImplementedException();
    }

    @Override
    public int inDegreeOf(State vertex) {
        throw new NotImplementedException();
    }

    @Override
    public Set<Transition> incomingEdgesOf(State vertex) {
        throw new NotImplementedException();
    }

    @Override
    public int outDegreeOf(State vertex) {
        return outgoingEdgesOf(vertex).size();
    }

    @Override
    public Set<Transition> outgoingEdgesOf(State vertex) {
        Set<Transition> outEdges = null;
        if (vertex.trajectoriesEndInTheSameTime()) {
            outEdges = extendAgent(vertex, 0);
        } else {
            // find the leftmost agent that is not assigned yet...
            int latestTrajectoryEnd = Integer.MIN_VALUE;
            for (int i = 0; i < vertex.nAgents(); i++) {
                if (latestTrajectoryEnd < vertex.get(i).getMaxTime()) {
                    latestTrajectoryEnd = vertex.get(i).getMaxTime();
                }
            }

            int i = 0;
            while (i < vertex.nAgents() && vertex.get(i).getMaxTime() == latestTrajectoryEnd) {
                i++;
            }
            // ... and extend
            outEdges = extendAgent(vertex, i);
        }

        return outEdges;
    }

    protected Set<Transition> extendAgent(State vertex, int i) {
        Set<Transition> transitions = new LinkedHashSet<Transition>();
        Trajectory traj = vertex.get(i);
        tt.euclidtime3i.Point lastPoint = traj.get(traj.getMaxTime());

        Set<Straight> edges = motionModels[i].outgoingEdgesOf(lastPoint);
        for (Straight edge : edges) {
            SingleEdgeGraphPath<Point, Straight> graphPath = new SingleEdgeGraphPath<tt.euclidtime3i.Point, Straight>(motionModels[i],edge);
            EvaluatedTrajectory deltaTraj = Trajectories.convertFromEuclid2iTrajectory(new StraightSegmentTrajectory(graphPath, edge.getEnd().getTime() - edge.getStart().getTime()));

            // Check if the extension is conflict-free

            //Collection<tt.euclid2i.Trajectory> otherTrajs = new LinkedList<tt.euclid2i.Trajectory>();
            Collection<tt.euclidtime3i.Region> otherRegions = new LinkedList<tt.euclidtime3i.Region>();
            for (int j = 0; j < vertex.nAgents(); j++) {
                if (j != i) {
                    //otherTrajs.add(Trajectories.converetToEuclid2iTrajectory(vertex.get(j)));
                    otherRegions.add(new MovingCircle(Trajectories.convertToEuclid2iTrajectory(vertex.get(j)), bodyRadiuses[j]));
                }
            }

            MovingCircle thisRegion = new MovingCircle(Trajectories.convertToEuclid2iTrajectory(deltaTraj), bodyRadiuses[i]);

            //boolean consistent = !SeparationDetector.hasConflict(Trajectories.converetToEuclid2iTrajectory(deltaTraj), otherTrajs, separation, SAMPLING_INTERVAL);
            boolean consistent = !IntersectionChecker.intersect(thisRegion, otherRegions);

            if (consistent) {
                // Create the transition/edge
                EvaluatedTrajectory[] trajs = new tt.euclidtime3i.EvaluatedTrajectory[nAgents()];
                trajs[i] = deltaTraj;
                transitions.add(new Transition(trajs, vertex, 0));
            }
        }

        return transitions;
    }

    @Override
    public boolean removeAllEdges(Collection<? extends Transition> arg0) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeAllVertices(Collection<? extends State> arg0) {
        throw new NotImplementedException();
    }

    public int nAgents() {
        return motionModels.length;
    }
}
