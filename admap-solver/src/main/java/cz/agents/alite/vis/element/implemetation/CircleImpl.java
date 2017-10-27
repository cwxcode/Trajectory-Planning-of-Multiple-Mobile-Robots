package cz.agents.alite.vis.element.implemetation;

import javax.vecmath.Point3d;

import cz.agents.alite.vis.element.Circle;

public class CircleImpl implements Circle {

    public final Point3d position;
    public final double radius;

    public CircleImpl(Point3d position, double radius) {
        this.position = position;
        this.radius = radius;
    }

    @Override
    public Point3d getPosition() {
        return position;
    }

    @Override
    public double getRadius() {
        return radius;
    }

}
