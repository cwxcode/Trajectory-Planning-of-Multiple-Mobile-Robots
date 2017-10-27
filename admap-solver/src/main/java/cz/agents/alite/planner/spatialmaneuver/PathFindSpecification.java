package cz.agents.alite.planner.spatialmaneuver;

import java.util.Random;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import cz.agents.alite.planner.spatialmaneuver.maneuver.Maneuver;
import cz.agents.alite.planner.spatialmaneuver.maneuver.ManeuverSpecification;
import cz.agents.alite.planner.spatialmaneuver.zone.Zone;


/**
 * Specification of path finding problem problem. It contains from/to points and vectors,
 * plane parameters, maneuver constant lookup no flight zones and some support
 * info for {@link PathFinderByManeuvers}.
 *
 * @author Antonin Komenda
 *
 */
public final class PathFindSpecification {
    private static final Random RANDOM = new Random();

    private static final int TEMPORARY_MANEUVER_BUFFER_SIZE = 20;

    private Point3d from, to;
    private Vector3d fromDirection, toDirection;

    private Zone zone;
    private ManeuverSpecification maneuverSpecification;

    private double radius;
    private double pitchRadius;
    private double maxPitch;

    private double velocity;

    private double maxSpiralHeight;
    private GonioCache maxPitchGonioCache;
    private Maneuver temporaryManuverBuffer[] = new Maneuver[TEMPORARY_MANEUVER_BUFFER_SIZE];
    private int hashBitSize[] = new int[] {0, 0, 0};

    /**
     * Creates anonymous {@link PathFindSpecification} with no start/end points and vectors.
     *
     * @param planeType plane parameters
     * @param zone no flight zones
     * @param maneuverSpecification maneuver parameters
     */
    public PathFindSpecification(double radius, Zone zone, ManeuverSpecification maneuverSpecification) {
        this(null, null, null, null, radius, zone, maneuverSpecification);
    }

    /**
     * Creates {@link PathFindSpecification} from parameters.
     *
     * @param from starting point
     * @param to finish point
     * @param fromDirection starting direction vector
     * @param toDirection finish direction vector
     * @param planeType plane parameters
     * @param zone no flight zones
     * @param maneuverSpecification maneuver parameters
     */
    public PathFindSpecification(Point3d from, Point3d to, Vector3d fromDirection, Vector3d toDirection, double radius, Zone zone, ManeuverSpecification maneuverSpecification) {
        this.from = from;
        this.to = to;
        this.fromDirection = fromDirection;
        this.toDirection = toDirection;

        this.zone = zone;
        this.maneuverSpecification = maneuverSpecification;

        this.radius = radius;
        this.pitchRadius = 70;
        this.maxPitch = Math.PI / 2;

        this.velocity = 500.0;

        if (fromDirection != null) {
            fromDirection.normalize();
        }
        if (toDirection != null) {
            toDirection.normalize();
        }

        maxPitchGonioCache = new GonioCache(maxPitch, pitchRadius);

        maxSpiralHeight = 2*Math.PI*radius*maxPitchGonioCache.tan;
    }

    public Point3d getFrom() {
        return from;
    }

    public Vector3d getFromDirection() {
        return fromDirection;
    }

    public Point3d getTo() {
        return to;
    }

    public Vector3d getToDirection() {
        return toDirection;
    }

    /**
     * Method sets from/to points and vectors.
     *
     * @param from starting point
     * @param to finish point
     * @param fromDirection starting direction vector
     * @param toDirection finish direction vector
     */
    public void setFromTo(Point3d from, Point3d to, Vector3d fromDirection, Vector3d toDirection) {
        this.from = from;
        this.to = to;
        this.fromDirection = fromDirection;
        this.toDirection = toDirection;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public Zone getZone() {
        return zone;
    }

    public double getPitchRadius() {
        return pitchRadius;
    }

    public ManeuverSpecification getManeuverSpecification() {
        return maneuverSpecification;
    }

    public double getRadius() {
        return radius;
    }

    public double getMaxPitch() {
        return maxPitch;
    }

    public double getVelocity() {
        return velocity;
    }

    public GonioCache getMaxPitchGonioCache() {
        return maxPitchGonioCache;
    }

    public double getMaxSpiralHeight() {
        return maxSpiralHeight;
    }

    public Maneuver[] getTemporaryManeuverBuffer() {
        return temporaryManuverBuffer;
    }

    public int getPositionHashCode(Point3d point, int bitCount) {
        int hashCode;
        Vector3d translation = new Vector3d(1000, 1000, 1000);
        Vector3d size = new Vector3d(1000 - translation.x, 1000 - translation.y, 1000 - translation.z);

        if (hashBitSize[0] == 0) {
            double minSize = Math.min(Math.min(size.x, size.y), size.z);
            double log2X = Math.log10(size.x/minSize)/Math.log10(2);
            double log2Y = Math.log10(size.y/minSize)/Math.log10(2);
            double log2Z = Math.log10(size.y/minSize)/Math.log10(2);

            double bitOffset = (bitCount-1 - log2X - log2Y - log2Z) / 3 ;

            hashBitSize[0] = (int)Math.round(log2X + bitOffset);
            hashBitSize[1] = (int)Math.round(log2Y + bitOffset);
            hashBitSize[2] = (int)Math.round(log2Z + bitOffset);

            if (hashBitSize[0] < 0) hashBitSize[0] = 0;
            if (hashBitSize[1] < 0) hashBitSize[1] = 0;
            if (hashBitSize[2] < 0) hashBitSize[2] = 0;
        }

        hashCode  = (int)Math.round((point.x - translation.x)/size.x*(1 << hashBitSize[0]));
        hashCode <<= hashBitSize[1];
        hashCode |= (int)Math.round((point.y - translation.y)/size.y*(1 << hashBitSize[1]));
        hashCode <<= hashBitSize[2];
        hashCode |= (int)Math.round((point.z - translation.z)/size.z*(1 << hashBitSize[2]));

        return hashCode;
    }


    public static final class GonioCache {

        public double sin;
        public double cos;
        public double tan;
        public double radiusSin;
        public double radiusCos;

        /**
         * @param maxPitch
         * @param pitchRadius
         */
        public GonioCache(double angle, double radius) {
            sin = Math.sin(angle);
            cos = Math.cos(angle);
            tan = Math.tan(angle);
            radiusSin = radius*sin;
            radiusCos = radius*cos;
        }

    }


    /**
     * Method checks validity of start/end points and vectors.
     *
     * @param zone no flight zones for start/end points collision test
     * @throws InvalidFromPointException
     * @throws InvalidToPointException
     */
    public void check() {
        if (zone.testPoint(from)) {
            throw new IllegalArgumentException("InvalidFromPointException");
        }
        if (zone.testPoint(to)) {
            throw new IllegalArgumentException("InvalidToPointException");
        }

        if (Math.abs(to.z - from.z) < 1e-5) {
            to.z = from.z;
        }
    }

    public Random getRandom() {
        return RANDOM;
    }

}
