package tt.euclid2i;

import java.util.Collection;

import tt.euclid2i.region.Rectangle;
import tt.util.NotImplementedException;

public class Union implements Region {

	Collection<Region> regions;

	public Union(Collection<Region> regions) {
		super();
		this.regions = regions;
	}

	@Override
	public boolean intersectsLine(Point p1, Point p2) {
		throw new NotImplementedException();
	}

	@Override
	public boolean isInside(Point p) {
		for (Region region : regions) {
			if (region.isInside(p)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Rectangle getBoundingBox() {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Region r : regions) {
	        for (Point point : r.getBoundingBox().toPolygon().getPoints()) {
	            int X = point.getX();
	            int Y = point.getY();

	            if (X < minX) minX = X;
	            if (Y < minY) minY = Y;
	            if (X > maxX) maxX = X;
	            if (Y > maxY) maxY = Y;
	        }
        }

        return new Rectangle(new Point(minX,minY),new Point(maxX,maxY));
	}

}
