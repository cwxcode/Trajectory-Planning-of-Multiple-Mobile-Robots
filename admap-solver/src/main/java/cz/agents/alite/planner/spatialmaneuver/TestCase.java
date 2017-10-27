package cz.agents.alite.planner.spatialmaneuver;

import java.io.PrintStream;

import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import cz.agents.alite.planner.spatialmaneuver.maneuver.ExpandManeuver;
import cz.agents.alite.planner.spatialmaneuver.maneuver.ExpandManeuverType;
import cz.agents.alite.planner.spatialmaneuver.maneuver.ExpandManeuvers;
import cz.agents.alite.planner.spatialmaneuver.maneuver.Maneuver;
import cz.agents.alite.planner.spatialmaneuver.maneuver.ManeuverSpecification;
import cz.agents.alite.planner.spatialmaneuver.maneuver.ManeuverSpecification.LevelConstants;
import cz.agents.alite.planner.spatialmaneuver.zone.CylinderZone;
import cz.agents.alite.planner.spatialmaneuver.zone.GroupZone;
import cz.agents.alite.planner.spatialmaneuver.zone.TransformZone;


public class TestCase {

    public static void main(String[] args) {
        // prepare randomized zone coordinates
//        Double[] zoneCoordinates = new Double[50];
//        Random rnd = new Random();
//        for (int i = 0; i < 50; i++) {
//            double d = 0;
//            if (i % 2 == 0) {
//                d = rnd.nextDouble() * -70.0 - 60.0;
//            } else {
//                d = rnd.nextDouble() * 40.0 + 15.0;
//            }
//            zoneCoordinates[i] = d;
//        }

        PrintStream zonesOut = null;
        PrintStream planOut = null;
        //try {
            //zonesOut = new PrintStream("zones.txt");
            //planOut = new PrintStream("plan.txt");
            zonesOut = System.out;
            planOut = System.out;
        //} catch (FileNotFoundException e) {
        //    e.printStackTrace();
        //}

        Double[] zoneCoordinates = new Double[] {-101.02725616307006, 15.305811591820348, -88.01192283629695, 53.08022584083321, -80.49804979170028, 38.21821532341733, -72.63516401779411, 17.098109971968643, -68.4586509799322, 18.011077871019566, -84.8359892136039, 18.7183578280018, -64.4362737553108, 46.073556589504996, -98.67812114568987, 22.18876921133171, -61.01628037071553, 34.45303937230179, -63.13857976003385, 34.15323362222517, -122.21480108914005, 33.66482865400013, -95.16441120202569, 28.96250825449893, -69.73083342057593, 46.96183267647535, -88.05013190713689, 50.982709861532655, -112.30945829675409, 28.086953932478593, -127.2767303955609, 45.47501409824151, -79.06299910512003, 26.444952288734875, -123.91198492882786, 27.15911027519182, -65.39976770487186, 51.794610718902014, -80.81839582769123, 44.36201507682334};
        final GroupZone zone  = new GroupZone();
        Double firstValue = null;
        for (Double d : zoneCoordinates) {
            if (firstValue != null) {
                zone.add(new TransformZone(
                        new CylinderZone(new Vector2d(5, 5), 200),
                        new Vector3d(firstValue, d, 0),
                        new Vector2d(1, 1),
                        0));
                firstValue = null;
            } else {
                firstValue = d;
            }
        }
        DebugTextZoneVisitor zoneVisitor = new DebugTextZoneVisitor(zonesOut);
        zone.accept(zoneVisitor);

        // plan trajectory constants
        LevelConstants[] levelConstants =  new LevelConstants[2];
        levelConstants[0] = new LevelConstants(2, 12, 8);

        // expand maneuvers
        ExpandManeuvers expandManeuvers = new ExpandManeuvers();
        expandManeuvers.getManeuver().add(new ExpandManeuver(ExpandManeuverType.STRAIGHT));
        //expandManeuvers.getManeuver().add(new ExpandManeuver(ExpandManeuverType.STRAIGHT_BACKWARDS));
        expandManeuvers.getManeuver().add(new ExpandManeuver(ExpandManeuverType.TURN_LEFT));
        expandManeuvers.getManeuver().add(new ExpandManeuver(ExpandManeuverType.TURN_RIGHT));
        expandManeuvers.getManeuver().add(new ExpandManeuver(ExpandManeuverType.TURN_TO_END));
        expandManeuvers.getManeuver().add(new ExpandManeuver(ExpandManeuverType.TO_END));

        // plan trajectory
        PathFindSpecification pathFindSpecification = new PathFindSpecification(70, zone, new ManeuverSpecification(null, levelConstants, expandManeuvers));

        try {
            pathFindSpecification.setFromTo(new Point3d(-135, 10, 50), new Point3d(-55, 50, 50),
                    new Vector3d(0, 1, 0), new Vector3d(0, 1, 0));
            Maneuver maneuver = PathFinderByManeuvers.findPath(pathFindSpecification);

            DebugTextManeuverVisitor visitor = new DebugTextManeuverVisitor(planOut);
            while (maneuver != maneuver.getPredecessor()) {
                maneuver.accept(visitor);
                maneuver = maneuver.getPredecessor();
            }

            planOut.print("-135, 10, 50, ");
            planOut.println("0, 0, 1.57");
        } catch (Exception e) {
            e.printStackTrace();
        }

        zonesOut.close();
        planOut.close();
    }

}
