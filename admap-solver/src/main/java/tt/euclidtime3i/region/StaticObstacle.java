package tt.euclidtime3i.region;

import tt.euclid2i.region.Rectangle;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.Region;


public class StaticObstacle implements Region {
	tt.euclid2i.Region staticObstacle;

	public StaticObstacle(tt.euclid2i.Region staticObstacle) {
		super();
		this.staticObstacle = staticObstacle;
	}

	@Override
	public boolean intersectsLine(Point p1, Point p2) {
		return staticObstacle.intersectsLine(p1.getPosition(), p2.getPosition());
	}

	@Override
	public boolean isInside(Point p) {
		return staticObstacle.isInside(p.getPosition());
	}

	@Override
	public HyperRectangle getBoundingBox() {
		Rectangle boundingBox2i = staticObstacle.getBoundingBox();
		return new HyperRectangle(new Point(boundingBox2i.getCorner1(), Integer.MIN_VALUE),
				new Point(boundingBox2i.getCorner2(), Integer.MAX_VALUE));
	}


}
