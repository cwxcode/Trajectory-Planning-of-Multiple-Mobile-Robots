package cz.agents.alite.vis.element.implemetation;

import javax.vecmath.Point3d;

import cz.agents.alite.vis.element.Point;

public class PointImpl implements Point {

    public final Point3d position;

    public PointImpl(Point3d position) {
        this.position = position;
    }

    @Override
    public Point3d getPosition() {
        return position;
    }

}
