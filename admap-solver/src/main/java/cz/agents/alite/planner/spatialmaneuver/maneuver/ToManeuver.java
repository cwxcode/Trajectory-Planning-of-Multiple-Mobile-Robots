package cz.agents.alite.planner.spatialmaneuver.maneuver;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import cz.agents.alite.planner.spatialmaneuver.PathFindSpecification;
import cz.agents.alite.planner.spatialmaneuver.PathFindSpecification.GonioCache;


public class ToManeuver extends Maneuver {

    private static double TOLERANCE = 1e-5;

    private final Point3d end;
    private final Vector3d endDirection;
    private final Point3d endOrigin;
    private TurnManeuver turn, endTurn;
    private PitchManeuver pitch, endPitch;
    private StraightManeuver straight;
    private StraightManeuver endStraight;
    private SpiralManeuver spiral;
    private ToManeuver toManeuver;
    private boolean vertical = false;
    private boolean verticalSpiral = false;
    private boolean verticalCorrected = false;

    public ToManeuver(Point3d start, Vector3d direction, double time, Point3d end, Vector3d endDirection,
            PathFindSpecification specification) {
        super(start, direction, time, specification);

        if (Math.abs(direction.z) >= 1e-5 && Math.abs(endDirection.z) >= 1e-5) {
            throw new IllegalArgumentException("ToManeuver starts and ends in XY plane ("
                    + direction.z + ", " + endDirection.z + " ).");
        }

        // originize
        this.end = new Point3d(end);
        this.end.sub(start);
        this.endDirection = new Vector3d(endDirection);
        this.endDirection.normalize();
        this.endOrigin = end;

        // horizontal
        toEndHorizontalProcess();

        // vertical
        toEndVerticalProcess();
    }

    private void toEndHorizontalProcess() {
        if (new Vector2d(end.x, end.y).length() > 4*specification.getRadius()) {
            double dir = turnDirectionSwitch();
            toEndHorizontal(dir, 1);
        } else {
            // near start/end
            double[] lengths = new double[4];
            TurnManeuver[] turns = new TurnManeuver[4];
            StraightManeuver[] straights = new StraightManeuver[4];
            TurnManeuver[] endTurns =  new TurnManeuver[4];
            double[] directions = { -1, -1, -1, 1, 1, -1, 1, 1 };
            lengths[0] = lengths[1] = lengths[2] = lengths[3] = Double.POSITIVE_INFINITY;

            for (int i = 0; i < 4; ++i) {
                if (toEndHorizontal(directions[2*i], directions[2*i+1])) {
                    lengths[i] = getLength();
                    turns[i] = getTurn();
                    straights[i] = straight;
                    endTurns[i] = endTurn;
                }
            }

            int shortestIndex;
            if (lengths[0] < lengths[1] && lengths[0] < lengths[2] && lengths[0] < lengths[3]) {
                shortestIndex = 0;
            } else if (lengths[1] < lengths[2] && lengths[1] < lengths[3]) {
                shortestIndex = 1;
            } else if (lengths[2] < lengths[3]) {
                shortestIndex = 2;
            } else {
                shortestIndex = 3;
            }

            turn = turns[shortestIndex];
            straight = straights[shortestIndex];
            endTurn = endTurns[shortestIndex];
        }

//		not optimal but faster
//      dir = turnDirectionSwitch(end);
//      planned = toEndHorizontal(end, dir, 1);
//
//      // near start/end
//      if (!planned) {
//          planned = toEndHorizontal(end, -dir, 1);
//
//          if (!planned) {
//              planned = toEndHorizontal(end, dir, -1);
//          }
//      }
    }

    private double turnDirectionSwitch() {
        double tangentLength = direction.y*end.x - direction.x*end.y;
        boolean leftTurn = tangentLength <= -specification.getRadius()*2;
        boolean rightTurn = tangentLength >= specification.getRadius()*2;

        if (!leftTurn && !rightTurn) {
            double directionTangentLength = direction.y*endDirection.x - direction.x*endDirection.y;

            if (Math.abs(tangentLength) < TOLERANCE) {
                leftTurn = directionTangentLength < -TOLERANCE;
                rightTurn = directionTangentLength > TOLERANCE;
            } else {
                rightTurn = tangentLength > TOLERANCE;
                leftTurn = tangentLength < -TOLERANCE;
            }

            if (leftTurn || rightTurn) {
                double angle = direction.angle(endDirection);

                if ((directionTangentLength <= 0 || rightTurn) && (directionTangentLength >= 0 || leftTurn)) {
                    double ratio = Math.abs(tangentLength)/specification.getRadius();
                    double maxAngle = Math.acos(-ratio + 1);
                    boolean isInFront = direction.x*end.x + direction.y*end.y > -TOLERANCE;

                    if ((isInFront && angle > maxAngle)
                            || (!isInFront && angle < Math.PI - maxAngle)) {
                        leftTurn = !leftTurn;
                        rightTurn = !rightTurn;
                    }
                }
            } else {
                leftTurn = false;
                rightTurn = true;
            }
        }

        if (leftTurn) {
            return 1;
        }
        return -1;
    }

    private boolean toEndHorizontal(double dir, double endDir) {
        double radius = specification.getRadius();
        Point2d e = new Point2d(end.x + dir*direction.y*radius, end.y - dir*direction.x*radius);

        double turnAngle = 0;
        double turnEndAngle = 0;
        double straightLength = 0;

        double endTurnDir = endTurnDirectionSwitch(e, dir);
        endTurnDir *= endDir;

        if (endTurnDir > 0) {
            e.add(new Point2d(-dir*endDirection.y*radius, dir*endDirection.x*radius));
            turnAngle = Math.atan2(-dir*e.x, dir*e.y);
        } else {
            e.add(new Point2d(dir*endDirection.y*radius, -dir*endDirection.x*radius));
            e.scale(0.5);

            double quadNorm = e.y*e.y + e.x*e.x;
            double quadLen = Math.sqrt(quadNorm - radius*radius);
            turnAngle = Math.atan2((e.y*radius - dir*quadLen*e.x) / quadNorm,
                                    (e.x*radius + dir*quadLen*e.y) / quadNorm);
        }

        if (Double.isNaN(turnAngle)) {
            return false;
        }

        turnEndAngle = turnAngle;
        turnEndAngle -= Math.atan2(-dir*endDirection.x, dir*endDirection.y);

        turnAngle -= Math.atan2(-dir*direction.x, dir*direction.y);
        turn = new TurnManeuver(start, direction, time, -dir*radius, positiveAngle(dir*turnAngle), specification);

        if (endTurnDir > 0) {
            straightLength = new Vector2d(e.x, e.y).length();
            radius = -dir*radius;

            turnEndAngle = -dir*turnEndAngle;
        } else {
            straightLength = new Vector2d(e.x, e.y).length();
            straightLength = 2*Math.sqrt(straightLength*straightLength - radius*radius);
            radius = dir*radius;

            turnEndAngle = dir*turnEndAngle;
        }

        straight = new StraightManeuver(getTurn().getEnd(), getTurn().getEndDirection(),
                getTurn().getEndTime(), straightLength, specification);
        endTurn = new TurnManeuver(new Point3d(straight.getEnd()), straight.getEndDirection(), time,
                        radius, positiveAngle(turnEndAngle), specification);

        return true;
    }

    private int endTurnDirectionSwitch(Point2d e, double dir) {
        double radius = specification.getRadius();

        double tangentLength = dir*endDirection.y*e.x - dir*endDirection.x*e.y - radius;
        boolean leftEndTurn = tangentLength > TOLERANCE;
        boolean rightEndTurn = tangentLength < -TOLERANCE;

        if (!leftEndTurn && !rightEndTurn) {
            tangentLength = dir*endDirection.x*direction.x + dir*endDirection.y*direction.y;

            leftEndTurn = tangentLength < 0;
            rightEndTurn = tangentLength > 0;
        }

        if (leftEndTurn) {
            return 1;
        }
        return -1;
    }

    private void toEndVerticalProcess() {
        if (Math.abs(end.z) > TOLERANCE) {
            endTurn.start.z = start.z + end.z;
            vertical = true;

            double dir = -1;
            if (end.z < 0) {
                dir = 1;
            }

            toEndSpiral(dir, endTurn.start);
            assert endPitch != null && pitch != null: "Null pitch";
        }
    }

    private void toEndSpiral(double dir, Point3d end) {
        double pitchRadius = specification.getPitchRadius();

        double height = Math.abs(end.z - getTurn().getEnd().z);
        double length = end.distance(new Point3d(getTurn().getEnd().x, getTurn().getEnd().y, end.z));

        int loops = getLoopCount(height, length);

        double pitchAngle = computePitchAngle(height, length, loops);
        pitch = new PitchManeuver(getTurn().getEnd(), getTurn().getEndDirection(), getTurn().getEndTime(), -dir*pitchRadius,
                pitchAngle, specification);

        double pitchLength = pitch.start.distance(
                new Point3d(pitch.getEnd().x, pitch.getEnd().y, pitch.start.z));
        double lengthStraight = computeStraightLength(length, pitchAngle, pitchLength);

        if (lengthStraight < -TOLERANCE) {
            verticalCorrected = true;
            Point3d to = new Point3d(
                    end.x - getTurn().getEndDirection().x * 2 * lengthStraight,
                    end.y - getTurn().getEndDirection().y * 2 * lengthStraight, end.z);
            try {
                toEndSpiral(dir, to);
            } catch(Exception th) {
//                th.printStackTrace();
                throw new RuntimeException("To end spiral exception: "+th.toString());
            }

            return;
        }

        straight.setStartDirection(pitch);

        if (loops >= 1) {
            verticalSpiral = true;
            straight.setLength(lengthStraight);

            spiral = new SpiralManeuver(straight.getEnd(), straight.getEndDirection(), straight.getEndTime(), loops, specification);

            endStraight = new StraightManeuver(spiral.getEnd(), spiral.getEndDirection(), spiral.getEndTime(),
                    lengthStraight, specification);

            endPitch = new PitchManeuver(endStraight.getEnd(), endStraight.getEndDirection(),
                    endStraight.getEndTime(), dir*pitchRadius, pitchAngle, specification);
        } else {
            straight.setLength(lengthStraight*2);

            endPitch = new PitchManeuver(straight.getEnd(), straight.getEndDirection(), straight.getEndTime(), dir*pitchRadius,
                    pitchAngle, specification);
        }

        if (verticalCorrected) {
            toManeuver = new ToManeuver(endPitch.getEnd(), endPitch.getEndDirection(),
                    endPitch.getEndTime(), endOrigin, endDirection, specification);
        }
    }

    private double computePitchAngle(double height, double length, int loops) {
        double pitchRadius = specification.getPitchRadius();
        double radius = specification.getRadius();

        height = height/2 - pitchRadius;
        length = length/2;

        Point3d e = new Point3d(height, length + Math.PI*radius*loops, 0);
        double quadNorm = e.y*e.y + e.x*e.x;
        double quadLen = Math.sqrt(quadNorm - pitchRadius*pitchRadius);
        double pitchAngle = Math.atan2((e.y*pitchRadius + quadLen*e.x) / quadNorm,
                                (e.x*pitchRadius - quadLen*e.y) / quadNorm);
        return Math.PI - pitchAngle;
    }

    private double computeStraightLength(double length, double pitchAngle, double pitchLength) {
        length = length/2;

        return (length - pitchLength)/Math.cos(pitchAngle);
    }

    private double positiveAngle(double angle) {
        if (Math.abs(angle) < TOLERANCE) {
            return 0;
        }
        if (angle < 0) {
            return 2*Math.PI + angle;
        }
        if (angle > 2*Math.PI) {
            return -2*Math.PI + angle;
        }
        return angle;
    }

    private int getLoopCount(double height, double length) {
        double pitchRadius = specification.getPitchRadius();
        double maxSpiralHeight = specification.getMaxSpiralHeight();
        GonioCache maxPitchGonioCache = specification.getMaxPitchGonioCache();
        double loopsHeight = height - pitchRadius + maxPitchGonioCache.radiusCos
                - (length - maxPitchGonioCache.radiusSin)*maxPitchGonioCache.tan
                + pitchRadius/maxPitchGonioCache.cos - pitchRadius;
        int loops = (int)Math.ceil(loopsHeight / maxSpiralHeight);
        if (loops < 0) loops = 0;
        return loops;
    }

    @Override
    public Point3d getEnd() {
        return getLastSubManeuver().getEnd();
    }

    @Override
    public Vector3d getEndDirection() {
        return getLastSubManeuver().getEndDirection();
    }

    @Override
    public double getLength() {
        double length = getTurn().getLength() + straight.getLength();
        if (vertical) {
            length += pitch.getLength()
                    + endPitch.getLength();
        }
        if (verticalSpiral) {
            length += spiral.getLength() + endStraight.getLength();
        }
        if (!verticalCorrected) {
            length += endTurn.getLength();
        } else {
            length += toManeuver.getLength();
        }

        return length;
    }

    public TurnManeuver getTurn() {
        return turn;
    }

    public StraightManeuver getStraight() {
        return straight;
    }

    public boolean isVertical() {
        return vertical;
    }

    public PitchManeuver getPitch() {
        return pitch;
    }

    public PitchManeuver getEndPitch() {
        return endPitch;
    }

    public boolean isVerticalSpiral() {
        return verticalSpiral;
    }

    public SpiralManeuver getSpiral() {
        return spiral;
    }

    public StraightManeuver getEndStraight() {
        return endStraight;
    }

    public boolean isVerticalCorrected() {
        return verticalCorrected;
    }

    public TurnManeuver getEndTurn() {
        return endTurn;
    }

    public ToManeuver getToManeuver() {
        return toManeuver;
    }

    private Maneuver getLastSubManeuver() {
        if (!verticalCorrected) {
            return endTurn;
        } else {
            return toManeuver;
        }
    }

    @Override
    public void setPredecessor(Maneuver predecessor) {
        super.setPredecessor(predecessor);

        getTurn().setPredecessor(predecessor);
        predecessor = getTurn();
        if (vertical) {
            pitch.setPredecessor(predecessor);
            predecessor = pitch;
        }
        straight.setPredecessor(predecessor);
        predecessor = straight;
        if (verticalSpiral) {
            spiral.setPredecessor(predecessor);
            predecessor = spiral;
            endStraight.setPredecessor(predecessor);
            predecessor = endStraight;
        }
        if (vertical) {
            endPitch.setPredecessor(predecessor);
            predecessor = endPitch;
        }
        if (!verticalCorrected) {
            endTurn.setPredecessor(predecessor);
            predecessor = endTurn;
        } else {
            toManeuver.setPredecessor(predecessor);
            predecessor = toManeuver;
        }
    }

    @Override
    public boolean isIntersectingFullZone() {
        boolean isIntersecting = straight.isIntersectingFullZone() || getTurn().isIntersectingFullZone();

        if (!isIntersecting && vertical) {
            isIntersecting = pitch.isIntersectingFullZone() || endPitch.isIntersectingFullZone();
        }

        if (!isIntersecting) {
            if (!verticalCorrected) {
                isIntersecting = endTurn.isIntersectingFullZone();
            } else {
                isIntersecting = toManeuver.isIntersectingFullZone();
            }
        }

        if (!isIntersecting && verticalSpiral) {
            isIntersecting = endStraight.isIntersectingFullZone() || spiral.isIntersectingFullZone();
        }

        return isIntersecting;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public TurnManeuver getTurnToEndManuever() {
        return getTurn();
    }

    public Maneuver getTurnPitchToEndManuever() {
        if (vertical && !verticalSpiral) {
            return new TurnPitchManeuver(getTurn(), pitch, specification);
        }

        return null;
    }

    @Override
    public void accept(ManeuverVisitor visitor) {
        visitor.visit(this);
    }

}
