package tt.euclidtime3i.discretization.softconstraints;

import tt.euclid2i.Point;
import tt.euclid2i.Trajectory;

public class PenaltyIntegrator {

    public static double integratePenaltyArray(
            Trajectory thisTrajectory,
            PenaltyFunction[] penaltyFunctions,
            Trajectory[] otherTrajectories,
            int samplingInterval) {

        double penaltySum = 0;
        int thisTrajMaxTime = thisTrajectory.getMaxTime();
        
        for (int t = thisTrajectory.getMinTime(); t < thisTrajMaxTime; t += samplingInterval) {
            for (int j = 0; j < otherTrajectories.length; j++) {
                if (otherTrajectories[j] != null) {
                	int otherTrajMaxTime = otherTrajectories[j].getMaxTime();
                    if (t >= otherTrajectories[j].getMinTime() && t <= otherTrajMaxTime) {
                    	int tEnd = Math.min(t + samplingInterval, Math.min(thisTrajMaxTime, otherTrajMaxTime));
                    	
                    	int segmentLength = (tEnd-t);
                    	int tSample = t + segmentLength/2;
                    	                    	
                    	Point thisPos = thisTrajectory.get(tSample);
                    	Point otherPos = otherTrajectories[j].get(tSample);

                        penaltySum += penaltyFunctions[j].getPenalty(thisPos.distance(otherPos), tSample) * segmentLength;
                    }
                }
            }
        }
        return penaltySum;
    }

    public static double integratePenaltySingle( Trajectory thisTrajectory,
            PenaltyFunction penaltyFunction,
            Trajectory otherTrajectory,
            int samplingInterval) {

        double penaltySum = 0;
        int thisTrajMaxTime = thisTrajectory.getMaxTime();
        int otherTrajMaxTime = otherTrajectory.getMaxTime();
        int maxTime = Math.min(thisTrajMaxTime, otherTrajMaxTime);
        
        for (int t = thisTrajectory.getMinTime(); t < maxTime; t += samplingInterval) {
        	int tEnd = Math.min(t + samplingInterval, maxTime);
        	
        	int segmentLength = (tEnd-t);
        	int tSample = t + segmentLength/2;
        	                    	
        	Point thisPos = thisTrajectory.get(tSample);
        	Point otherPos = otherTrajectory.get(tSample);
        	
        	// fast check that does not require sqrt
        	int distSquare = (thisPos.x - otherPos.x) * (thisPos.x - otherPos.x)  + (thisPos.y - otherPos.y) * (thisPos.y - otherPos.y);
        	if (distSquare <= penaltyFunction.getSeparationSquare()) {
        		penaltySum += penaltyFunction.getPenalty(thisPos.distance(otherPos), tSample) * segmentLength;
        	}
        }
        return penaltySum;
    }

}
