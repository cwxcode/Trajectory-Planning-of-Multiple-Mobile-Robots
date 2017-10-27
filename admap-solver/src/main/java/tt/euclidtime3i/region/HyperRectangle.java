package tt.euclidtime3i.region;

import tt.euclidtime3i.Point;
import tt.euclidtime3i.Region;
import tt.util.NotImplementedException;

public class HyperRectangle implements Region {

    Point corner1;
    Point corner2;

    public HyperRectangle(Point corner1, Point corner2) {
        super();
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

	@Override
	public boolean intersectsLine(Point p1, Point p2) {
		throw new NotImplementedException();
	}

    @Override
    public boolean isInside(Point p) {
        return ( p.x >= corner1.x && p.x <= corner2.x &&
                 p.y >= corner1.y && p.y <= corner2.y &&
                 p.getTime() >= corner1.getTime() && p.getTime() <= corner2.z);
    }

    public Point getCorner1() {
        return corner1;
    }

    public Point getCorner2() {
        return corner2;
    }

    public int getXSize() {
        return corner2.x - corner1.x;
    }

    public int getYSize() {
        return corner2.y - corner1.y;
    }


    public int getTSize() {
        return corner2.z - corner1.z;
    }

    public Point getCenter() {
        return new Point(corner1.getX() + getXSize()/2,
                                corner1.getY() + getYSize()/2,
                                corner1.getTime() + getTSize()/2);
    }

	@Override
	public HyperRectangle getBoundingBox() {
		return this;
	}

	public boolean intersects(HyperRectangle boundingBox) {
		throw new NotImplementedException();
	}



}
