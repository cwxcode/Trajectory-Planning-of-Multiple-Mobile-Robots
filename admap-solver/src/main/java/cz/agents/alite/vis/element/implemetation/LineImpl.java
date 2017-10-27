package cz.agents.alite.vis.element.implemetation;

import javax.vecmath.Point3d;

import cz.agents.alite.vis.element.Line;

public class LineImpl implements Line {

    public final Point3d from;
    public final Point3d to;

    public LineImpl(Point3d from, Point3d to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public Point3d getFrom() {
        return from;
    }

    @Override
    public Point3d getTo() {
        return to;
    }

}
