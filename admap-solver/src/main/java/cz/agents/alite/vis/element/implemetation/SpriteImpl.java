package cz.agents.alite.vis.element.implemetation;

import java.awt.image.BufferedImage;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import cz.agents.alite.vis.element.Sprite;

public class SpriteImpl extends ImageImpl implements Sprite {

    public final Point3d position;
    public final Vector3d direction;

    public SpriteImpl(Point3d position, Vector3d direction, BufferedImage image) {
        super(image);

        this.position = position;
        this.direction = direction;
    }

    @Override
    public Point3d getPosition() {
        return position;
    }

    @Override
    public Vector3d getDirection() {
        return direction;
    }

}
