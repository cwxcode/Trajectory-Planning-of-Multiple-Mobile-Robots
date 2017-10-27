package cz.agents.alite.vis.element.implemetation;

import java.awt.Color;

import javax.vecmath.Point3d;

import cz.agents.alite.vis.element.StyledPoint;

public class StyledPointImpl implements StyledPoint {

    private final Point3d point;
    private final Color color;
    private final int width;

    public StyledPointImpl(Point3d point, Color color, int width) {
        this.point = point;
        this.color = color;
        this.width = width;
    }

    @Override
    public Point3d getPosition() {
        return point;
    }
    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public int getWidth() {
        return width;
    }

}
