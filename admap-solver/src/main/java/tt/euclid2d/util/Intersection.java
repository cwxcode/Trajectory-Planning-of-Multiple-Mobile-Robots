package tt.euclid2d.util;

import tt.euclid2d.Point;

public class Intersection {

    /**
     * Determines if a line segment of two line segments (x1,y1)-->(x2,y2) and (x1,y1)-->(x2,y2) intersect.
     * If parameter closed is set to true, the extreme points are also considered part of the line segment.
     *
     * @return true if the line segments intersect or overlap
     */
    public static boolean linesIntersect(
            double x1, double y1,
            double x2, double y2,
            double x3, double y3,
            double x4, double y4,
            boolean closed) {

        if (boxesOverlap(x1,y1,x2,y2, x3,y3,x4,y4, closed)) {
            double det = (x1-x2)*(y3-y4) - (y1-y2)*(x3-x4);
            if (det == 0) {
                // lines are parallel

                // convert to parametric form y = a*x + b
                double a1 = (y2-y1)/(x2-x1); // slope of line 1
                double b1 = y1 - a1*x1; // intercept of line 2
                double a2 = (y4-y3)/(x4-x3);
                double b2 = y3 - a2*x3;

                //assert a1 == a2;

                return (b1 == b2);
            } else {
                // find intersection point
                double x = ((x1*y2-y1*x2)*(x3-x4) - (x1-x2)*(x3*y4-y3*x4)) / det;
                double y = ((x1*y2-y1*x2)*(y3-y4) - (y1-y2)*(x3*y4-y3*x4)) / det;

                return onLineSegment(x, y, x1, y1, x2, y2, closed) && onLineSegment(x, y, x3, y3, x4, y4, closed);
            }
        }

        return false;
    }

    public static boolean linesIntersect(Point line1Start, Point line1End,
            Point line2Start, Point line2End,
            boolean closed) {
        double x1 = line1Start.x;
        double y1 = line1Start.y;

        double x2 = line1End.x;
        double y2 = line1End.y;

        double x3 = line2Start.x;
        double y3 = line2Start.y;

        double x4 = line2End.x;
        double y4 = line2End.y;

        return linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4, closed);
    }

    public static boolean linesOverlap(Point line1Start, Point line1End,
            Point line2Start, Point line2End, boolean closed) {
        double x1 = line1Start.x;
        double y1 = line1Start.y;

        double x2 = line1End.x;
        double y2 = line1End.y;

        double x3 = line2Start.x;
        double y3 = line2Start.y;

        double x4 = line2End.x;
        double y4 = line2End.y;

        return (boxesOverlap(x1,y1,x2,y2, x3,y3,x4,y4, closed) &&
            ((x1-x2)*(y3-y4) - (y1-y2)*(x3-x4)) == 0 /* lines are parallel*/);

    }

    public static Point getLineIntersectionPoint(Point line1Start, Point line1End,
            Point line2Start, Point line2End, boolean closed) {
        double x1 = line1Start.x;
        double y1 = line1Start.y;

        double x2 = line1End.x;
        double y2 = line1End.y;

        double x3 = line2Start.x;
        double y3 = line2Start.y;

        double x4 = line2End.x;
        double y4 = line2End.y;

        double det = (x1-x2)*(y3-y4) - (y1-y2)*(x3-x4);

        if ( det == 0) {
            // the lines are parallel
            if (boxesOverlap(x1,y1,x2,y2, x3,y3,x4,y4, closed)) {
                return null;
            } else {
                return null;
            }

        } else {
            double x = ((x1*y2-y1*x2)*(x3-x4) - (x1-x2)*(x3*y4-y3*x4)) / det;
            double y = ((x1*y2-y1*x2)*(y3-y4) - (y1-y2)*(x3*y4-y3*x4)) / det;

            // check if the intersection lies on both line segments
            if (onLineSegment(x, y, x1, y1, x2, y2, closed) && onLineSegment(x, y, x3, y3, x4, y4, closed)) {
                return new Point(x,y);
            } else {
                return null;
            }

        }
    }

    /**
     *
     * @param closed if set to true, the boundary is considered part of the box
     * @return true iff a point (x,y) lies in a box defined by two corners
     * (bx1, by2) and (bx2, by2)
     *
     */
    private static boolean inBox(double x, double y,
            double bx1, double by1,
            double bx2, double by2,
            boolean closed) {

        double xmin = Math.min(bx1, bx2);
        double xmax = Math.max(bx1, bx2);
        double ymin = Math.min(by1, by2);
        double ymax = Math.max(by1, by2);
        if (closed) {
            return ( xmin <= x && x <= xmax && ymin <= y && y <= ymax );
        } else {
            return ( xmin < x && x < xmax && ymin < y && y < ymax );
        }

    }

    /**
     * @return true iff box defined by two corners(b1x1, b1y2) and (b1x2, b1y2) and
     * the box defined by two corners(b2x1, b2y2) and (b2x2, b2y2) overlap
     */
    private static boolean boxesOverlap(double ax1, double ay1, double ax2, double ay2,
                               double bx1, double by1, double bx2, double by2, boolean closed) {

        double axmin = Math.min(ax1, ax2);
        double axmax = Math.max(ax1, ax2);
        double aymin = Math.min(ay1, ay2);
        double aymax = Math.max(ay1, ay2);

        double bxmin = Math.min(bx1, bx2);
        double bxmax = Math.max(bx1, bx2);
        double bymin = Math.min(by1, by2);
        double bymax = Math.max(by1, by2);

        boolean separated;
        if (closed) {
            separated = axmax < bxmin || axmin > bxmax || aymax < bymin || aymin > bymax;
        } else {
            separated = axmax <= bxmin || axmin >= bxmax || aymax <= bymin || aymin >= bymax;
        }

        return !separated;
    }

    private static boolean onLineSegment(double x, double y, double x1, double y1, double x2, double y2, boolean closed) {
        // assumes that x,y lies on a line (containing (x1,y1) and (x2,y2))
        // check if the bounding box of the line segment
        boolean inBox = inBox(x,y,x1,y1,x2,y2, true);

        if (closed) {
            return inBox;
        }
        else {
            // open
            return inBox && !(x == x1 && y == y1) && !(x == x2 && y == y2);
        }

    }

}
