package tt.vis.problemcreator.states;

import cz.agents.alite.vis.layer.VisLayer;
import tt.euclid2i.Point;
import tt.euclid2i.vis.ProjectionTo2d;
import tt.vis.LabeledCircleLayer;
import tt.vis.LabeledCircleLayer.LabeledCircle;
import tt.vis.problemcreator.main.DialogUtils;
import tt.vis.problemcreator.main.ExtensibleProblem;
import tt.vis.problemcreator.patches.ProblemPatch;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DocksCreatingState implements CreatorState {

    private int radius;
    private List<Point> docks;
    private VisLayer dockCircleLayer;
	private Set<Point> candidatePositions;
	
	public DocksCreatingState(final int defaultRadius) {
		this(defaultRadius, null);
	}

    public DocksCreatingState(final int defaultRadius, final Set<Point> candidatePositions) {
        this.radius = DialogUtils.getNumber("Radius of the docks", defaultRadius);
        this.candidatePositions = candidatePositions;

        this.dockCircleLayer = LabeledCircleLayer.create(new LabeledCircleLayer.LabeledCircleProvider<Point>() {
            @Override
            public Collection<LabeledCircleLayer.LabeledCircle<Point>> getLabeledCircles() {
            	Collection<LabeledCircle<Point>> circles = new LinkedList<LabeledCircle<Point>>();
            	for (Point position : docks) {
            		circles.add(new LabeledCircleLayer.LabeledCircle<Point>(position, radius, "", Color.MAGENTA));
            	}
				return circles;
            }
        }, new ProjectionTo2d());
    }

    @Override
    public void paint(Graphics2D g) {
        if (docks != null)
            dockCircleLayer.paint(g);
    }

    @Override
    public void mouseClicked(Point point, int button) {
        if (button == MouseEvent.BUTTON1) {
            if (docks == null) {
                docks = new LinkedList<Point>();
            }
            if (candidatePositions != null && !candidatePositions.isEmpty()) {
            	docks.add(closestFromSet(candidatePositions, point));          
            } else {
            	docks.add(point);
            }
        }
    }

    static private Point closestFromSet(Set<Point> candidatePositions, Point point) {
		Point closestCandidate = null;
		for (Point candidate : candidatePositions) {
			if (closestCandidate == null || point.distance(closestCandidate) > point.distance(candidate) ) {
				closestCandidate = candidate;
			}
		}
		
		assert closestCandidate != null;
		
    	return closestCandidate;
	}

	@Override
    public void keyTyped(int key) {
    }

    @Override
    public boolean hasChanged() {
        return docks != null;
    }

    @Override
    public ProblemPatch getPatch() {

        return new ProblemPatch() {
            @Override
            public void apply(ExtensibleProblem problem) {
            	for (Point pos : docks) {
            		problem.addDock(pos);
            	}
            }

            @Override
            public void paint(Graphics2D g) {
                DocksCreatingState.this.paint(g);
            }
        };
    }
}
