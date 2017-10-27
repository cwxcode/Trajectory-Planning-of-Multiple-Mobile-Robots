package cz.agents.alite.vis.element;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public interface Sprite extends Image {

    Point3d getPosition();

    Vector3d getDirection();

}
