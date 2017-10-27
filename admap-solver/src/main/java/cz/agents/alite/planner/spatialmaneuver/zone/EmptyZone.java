package cz.agents.alite.planner.spatialmaneuver.zone;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point3d;

public class EmptyZone implements Zone {

    @Override
    public boolean testLine(Point3d point1, Point3d point2, Point3d outPoint) {
        return false;
    }

    @Override
    public boolean testPoint(Point3d point) {
        return false;
    }
    
    @Override
	public boolean testLine(Point3d point1, Point3d point2) {
		return false;
	}

	@Override
	public List<Point3d> findLineIntersections(Point3d point1, Point3d point2) {
		return new LinkedList<Point3d>();
	}

    @Override
    public void accept(ZoneVisitor visitor) {
        visitor.visit(this);
    }

}
