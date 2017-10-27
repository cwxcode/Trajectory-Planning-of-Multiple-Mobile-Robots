package tt.vis.problemcreator.states;

import cz.agents.alite.vis.layer.VisLayer;
import tt.euclid2i.Point;
import tt.euclid2i.vis.ProjectionTo2d;
import tt.vis.LabeledCircleLayer;
import tt.vis.problemcreator.main.DialogUtils;
import tt.vis.problemcreator.main.ExtensibleProblem;
import tt.vis.problemcreator.patches.ProblemPatch;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class AgentCreatingState implements CreatorState {

    private int radius;
    private int maxSpeed;

    private int n;
    private Point start;
    private Point goal;
    private VisLayer startLayer;
    private VisLayer goalLayer;
    private Set<Point> candidatePositions;
    
    public AgentCreatingState(int i, final int defaultRadius, int defaultSpeed) {
    	this(i, defaultRadius, defaultSpeed, null);
    }

    public AgentCreatingState(int i, final int defaultRadius, int defaultSpeed, Set<Point> candidatePositions) {
        this.n = i;

        this.radius = DialogUtils.getNumber("Radius of the agent", defaultRadius);
        this.maxSpeed = DialogUtils.getNumber("MaxSpeed of the agent", defaultSpeed);
        
        this.candidatePositions = candidatePositions;
        
        this.startLayer = LabeledCircleLayer.create(new LabeledCircleLayer.LabeledCircleProvider<Point>() {
            @Override
            public Collection<LabeledCircleLayer.LabeledCircle<Point>> getLabeledCircles() {
                return Collections.singleton(new LabeledCircleLayer.LabeledCircle<Point>(start, radius, "s" + n, Color.BLUE));
            }
        }, new ProjectionTo2d());


        this.goalLayer = LabeledCircleLayer.create(new LabeledCircleLayer.LabeledCircleProvider<Point>() {
            @Override
            public Collection<LabeledCircleLayer.LabeledCircle<Point>> getLabeledCircles() {
                return Collections.singleton(new LabeledCircleLayer.LabeledCircle<Point>(goal, radius, "g" + n, Color.RED));
            }
        }, new ProjectionTo2d());

    }

    @Override
    public void paint(Graphics2D g) {
        if (goal != null)
            goalLayer.paint(g);
        if (start != null)
            startLayer.paint(g);
    }

    @Override
    public void mouseClicked(Point point, int button) {
    	
    	// snap to grid
        if (candidatePositions != null && !candidatePositions.isEmpty()) {
        	point = closestFromSet(candidatePositions, point);          
        }
    	
        if (button == MouseEvent.BUTTON1) {
            if (start == null) {
                start = point;

            } else if (goal == null) {
                goal = point;
            }
        }
    }

    @Override
    public void keyTyped(int key) {
    }

    @Override
    public boolean hasChanged() {
        return start != null && goal != null;
    }

    @Override
    public ProblemPatch getPatch() {

        return new ProblemPatch() {
            @Override
            public void apply(ExtensibleProblem problem) {
                problem.addAgent(start, goal, radius, maxSpeed);
            }


            @Override
            public void paint(Graphics2D g) {
                AgentCreatingState.this.paint(g);
            }
        };
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
}
