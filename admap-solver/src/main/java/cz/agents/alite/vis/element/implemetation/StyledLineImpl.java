package cz.agents.alite.vis.element.implemetation;

import java.awt.Color;

import javax.vecmath.Point3d;

import cz.agents.alite.vis.element.StyledLine;

public class StyledLineImpl implements StyledLine {

    public final Point3d from;
    public final Point3d to;
    public final Color color;
    public final int width;

    public StyledLineImpl(Point3d from, Point3d to, Color color, int width) {
        this.from = from;
        this.to = to;
        this.color = color;
        this.width = width;
    }

    @Override
    public Point3d getFrom() {
        return from;
    }

    @Override
    public Point3d getTo() {
        return to;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public int getStrokeWidth() {
        return width;
    }

}
