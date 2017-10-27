package cz.agents.alite.vis.element.implemetation;

import java.awt.Color;

import javax.vecmath.Point3d;

import cz.agents.alite.vis.element.FilledStyledCircle;

public class FilledStyledCircleImpl implements FilledStyledCircle {

    public final Point3d position;
    public final double radius;
    public final Color color;
    public final Color fillColor;

    public FilledStyledCircleImpl(Point3d position, double radius, Color color, Color fillColor) {
        this.position = position;
        this.radius = radius;
        this.color = color;
        this.fillColor = fillColor;
    }

    @Override
    public Point3d getPosition() {
        return position;
    }

    @Override
    public double getRadius() {
        return radius;
    }

    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public Color getFillColor() {
        return null;
    }

}
