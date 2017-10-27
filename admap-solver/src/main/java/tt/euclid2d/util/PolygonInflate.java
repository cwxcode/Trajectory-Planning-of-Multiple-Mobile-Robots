package tt.euclid2d.util;

import javax.vecmath.Vector2d;

import tt.euclid2d.Point;
import tt.euclid2d.region.Polygon;

public class PolygonInflate {

    public Point[] inflate(Polygon original, double inflateBy) {
        Point[] points = original.getPoints();
        Point[] inflatedPolygon = new Point[points.length*3];

        // find the geometric center
        float xSum = 0;
        float ySum = 0;

        for (int j = 0; j < points.length; j++) {
            xSum += points[j].x;
            ySum += points[j].y;
        }

        Point geomCenter = new Point(xSum/points.length, ySum/points.length);

        for (int j = 0; j < points.length; j++) {
            Vector2d v = new Vector2d(points[j].x, points[j].y);
            v.sub(geomCenter);
            v.normalize();
            v.scale(inflateBy);

            inflatedPolygon[3*j + 1] = new Point(points[j].x + v.x, points[j].y + v.y);

            //+ rotation with rotation matrix
            double alpha = Math.PI/4;
            v = new Vector2d(v.x*Math.cos(alpha) - v.y*Math.sin(alpha), v.x*Math.sin(alpha) + v.y*Math.cos(alpha));
            inflatedPolygon[3*j + 2] = new Point(points[j].x + v.x, points[j].y + v.y);

            //- rotation with rotation matrix
            double beta = -2*alpha;
            v = new Vector2d(v.x*Math.cos(beta) - v.y*Math.sin(beta), v.x*Math.sin(beta) + v.y*Math.cos(beta));
            inflatedPolygon[3*j] = new Point(points[j].x + v.x, points[j].y + v.y);

        }

        return inflatedPolygon;
    }

    public Point[] inflateBy2(Polygon original, double inflateBy) {
        Point[] points = original.getPoints();
        Point[] inflatedPolygon = new Point[points.length*2];

        // find the geometric center
        float xSum = 0;
        float ySum = 0;

        for (int j = 0; j < points.length; j++) {
            xSum += points[j].x;
            ySum += points[j].y;
        }

        Point geomCenter = new Point(xSum/points.length, ySum/points.length);

        for (int j = 0; j < points.length; j++) {
            Vector2d v = new Vector2d(points[j].x, points[j].y);
            v.sub(geomCenter);
            v.normalize();
            v.scale(inflateBy);

            //+ rotation with rotation matrix
            double alpha = Math.PI/4;
            v = new Vector2d(v.x*Math.cos(alpha) - v.y*Math.sin(alpha), v.x*Math.sin(alpha) + v.y*Math.cos(alpha));
            inflatedPolygon[2*j + 1] = new Point(points[j].x + v.x, points[j].y + v.y);

            //- rotation with rotation matrix
            double beta = -2*alpha;
            v = new Vector2d(v.x*Math.cos(beta) - v.y*Math.sin(beta), v.x*Math.sin(beta) + v.y*Math.cos(beta));
            inflatedPolygon[2*j] = new Point(points[j].x + v.x, points[j].y + v.y);

        }

        return inflatedPolygon;
    }

}
