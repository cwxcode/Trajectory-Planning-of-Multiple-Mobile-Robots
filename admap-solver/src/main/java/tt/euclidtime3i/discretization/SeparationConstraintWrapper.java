package tt.euclidtime3i.discretization;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.GraphDelegator;

import tt.euclid2i.SegmentedTrajectory;
import tt.euclid2i.Trajectory;
import tt.euclid2i.trajectory.BasicSegmentedTrajectory;
import tt.euclid2i.util.SeparationDetector;
import tt.euclidtime3i.Point;

@SuppressWarnings("serial")
public class SeparationConstraintWrapper extends GraphDelegator<Point, Straight> implements DirectedGraph<Point, Straight>  {

    private Trajectory[] otherTrajs;
    private int[] separations;
    
    final static int DEFAULT_SAMPLING_INTERVAL = 10;
    final static int USE_ANALYTIC_SAMPLING = (-1);
	private int samplingInterval; 
	
//	public SeparationConstraintWrapper(DirectedGraph<Point, Straight> g, Trajectory[] otherTrajs, int[] separations) {
//		this(g, otherTrajs, separations, DEFAULT_SAMPLING_INTERVAL);
//	}

    public SeparationConstraintWrapper(DirectedGraph<Point, Straight> g, Trajectory[] otherTrajs, int[] separations, int samplingInterval) {
        super(g);
        this.otherTrajs = otherTrajs;
        this.separations = separations;
        this.samplingInterval = samplingInterval;
    }

    @Override
    public Set<Straight> outgoingEdgesOf(Point vertex) {
        Set<Straight> allEdges = super.outgoingEdgesOf(vertex);
        Set<Straight> consistentEdges = new LinkedHashSet<Straight>();

        for (Straight edge : allEdges) {
            if (consistent(edge, otherTrajs, separations)) {
                consistentEdges.add(edge);
            }
        }

        return consistentEdges;
    }


    private boolean consistent(Straight e, Trajectory otherTrajs[], int[] separations) {
        int duration = e.getEnd().getTime() - e.getStart().getTime();
        Trajectory thisTraj = new BasicSegmentedTrajectory(Arrays.asList(e), duration, super.getEdgeWeight(e));

        SegmentedTrajectory[] segmentedTrajs = new SegmentedTrajectory[otherTrajs.length];
        for (int i=0; i<otherTrajs.length; i++) {
            assert(otherTrajs[i] instanceof SegmentedTrajectory);
            segmentedTrajs[i] = (SegmentedTrajectory) otherTrajs[i];
        }

        if (samplingInterval != USE_ANALYTIC_SAMPLING) {
        	return !SeparationDetector.hasAnyPairwiseConflict(thisTraj, otherTrajs, separations, samplingInterval);
        } else {
        	return !SeparationDetector.hasAnyPairwiseConflictAnalytic((tt.euclid2i.SegmentedTrajectory) thisTraj, segmentedTrajs, separations);
        }
    }

}
