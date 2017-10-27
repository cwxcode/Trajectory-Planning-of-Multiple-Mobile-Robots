package tt.jointeuclid2ni.operatordecomposition;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclidtime3i.Geometry3i;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ODNode {

    private int size;
    private int agentToExpand;

    private int[] bodyRadiuses;
    private Point[] agentPositions;
    private Line[] lines;

    public static ODNode start(Point[] starts, int[] bodyRadiuses) {
        return new ODNode(starts, bodyRadiuses);
    }

    ODNode(Point[] values, Line[] lines, int toExpand, int[] bodyRadiuses) {
        this.size = values.length;
        this.agentPositions = values;
        this.lines = lines;
        this.agentToExpand = toExpand;
        this.bodyRadiuses = bodyRadiuses;
    }

    ODNode(Point[] starts, int[] bodyRadiuses) {
        this(starts, new Line[starts.length], 0, bodyRadiuses);
    }

    public Set<ODEdge> expand(Set<Line> edges, boolean agentsCanWait) {
        Set<ODEdge> expanded = new HashSet<ODEdge>(edges.size());
        
        if (agentsCanWait) {
        	edges = new HashSet<Line>(edges);
        	edges.add(new Line(agentPositions[agentToExpand], agentPositions[agentToExpand]));
        }
        
		for (Line line : edges) {
			if (canExpand(line)) {
				ODNode successor = createSuccessor(line);
				expanded.add(new ODEdge(this, successor, line));
			}
		}
        return expanded;
    }

    public boolean canExpand(final Line line) {
        tt.euclid2d.Point a = line.getStart().toPoint2d();
        tt.euclid2d.Point b = line.getEnd().toPoint2d();

        for (int i = 0; i < agentToExpand; i++) {
            Line otherLine = lines[i];
            int separation = bodyRadiuses[i] + bodyRadiuses[agentToExpand];

            tt.euclid2d.Point c = otherLine.getStart().toPoint2d();
            tt.euclid2d.Point d = otherLine.getEnd().toPoint2d();
            
            // analytic collision check
            if (Geometry3i.distanceInEqualInitAndEndTime(a, b, c, d) < separation)
                return false;
        }
        return true;
    }

    public boolean isComplete() {
        return agentToExpand == 0;
    }

    public int getSize() {
        return size;
    }

    public Point[] getAgentPositions() {
        return agentPositions;
    }

    public Point agentPositionsToExpand() {
        return agentPositions[agentToExpand];
    }

    public int agentToExpand() {
        return agentToExpand;
    }

    private ODNode createSuccessor(Line line) {
        Point[] childValues = agentPositions.clone();
        Line[] childLines = lines.clone();

        childValues[agentToExpand] = line.getEnd();
        childLines[agentToExpand] = line;
        int childToExpand = (agentToExpand + 1) % size;

        return new ODNode(childValues, childLines, childToExpand, bodyRadiuses);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ODNode odNode = (ODNode) o;

        if (agentToExpand != odNode.agentToExpand) return false;
        if (!Arrays.equals(agentPositions, odNode.agentPositions)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = agentToExpand;
        result = 31 * result + Arrays.hashCode(agentPositions);
        return result;
    }
}
