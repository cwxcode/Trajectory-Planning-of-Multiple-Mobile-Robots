package tt.euclid2d.region;

import math.geom2d.line.Line2D;
import tt.euclid2d.Point;

public class Rectangle implements Region {

    Point corner1;
    Point corner2;

    public Rectangle(Point corner1, Point corner2) {
        super();
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    @Override
    public boolean intersectsLine(Point p1, Point p2) {
        Line2D line = new Line2D(p1.x, p1.y, p2.x, p2.y);
        return Line2D.intersects(line, new Line2D(corner1.x, corner1.y, corner1.x, corner2.y)) ||
                Line2D.intersects(line, new Line2D(corner1.x, corner2.y, corner2.x, corner2.y)) ||
                Line2D.intersects(line, new Line2D(corner2.x, corner2.y, corner2.x, corner1.y)) ||
                Line2D.intersects(line, new Line2D(corner2.x, corner1.y, corner1.x, corner1.y));
    }

    @Override
    public boolean isInside(Point p) {
        return corner1.x <= p.x && p.x <= corner2.x &&
                corner1.y <= p.y && p.y <= corner2.y;
    }

    public Point getCorner1() {
        return corner1;
    }

    public Point getCorner2() {
        return corner2;
    }

}
