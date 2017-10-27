package cz.agents.alite.planner.spatialmaneuver.zone;

import java.util.List;

import javax.vecmath.Point3d;

/**
 * @author Antonin Komenda
 *
 */
public interface Zone {

    public boolean testPoint(final Point3d point);

    @Deprecated
    public boolean testLine(final Point3d point1, final Point3d point2, final Point3d outPoint);
    
    public boolean testLine(final Point3d point1, final Point3d point2);
    
    public List<Point3d> findLineIntersections(final Point3d point1, final Point3d point2);

    public void accept(ZoneVisitor visitor);

}
