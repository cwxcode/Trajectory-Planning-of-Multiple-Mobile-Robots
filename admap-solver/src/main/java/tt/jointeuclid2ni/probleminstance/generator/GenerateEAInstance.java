package tt.jointeuclid2ni.probleminstance.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import cz.agents.alite.communication.acquaintance.Plan;
import tt.euclid2i.Point;
import tt.euclid2i.discretization.LazyGrid;
import tt.euclid2i.region.Rectangle;
import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;
import tt.jointeuclid2ni.probleminstance.TrajectoryCoordinationProblem;
import tt.jointeuclid2ni.probleminstance.TrajectoryCoordinationProblemXMLDeserializer;
import tt.jointeuclid2ni.probleminstance.TrajectoryCoordinationProblemXMLSerializer;
import tt.jointeuclid2ni.probleminstance.VisUtil;
import tt.jointeuclid2ni.probleminstance.generator.exception.ProblemNotCreatedException;
import tt.util.Args;
import tt.util.Verbose;

public class GenerateEAInstance {
	
	private static final int MAXTIME = 2000;

	public static TrajectoryCoordinationProblem generateInstance(
			String environmentFile, int agents, int radius, float maxSpeed, int patternNumber,
			int step, int seed, Rectangle missionArea, boolean oneCluster, boolean startAndGoalsCanOverlap, boolean sgAvoiding) throws ProblemNotCreatedException {

        EarliestArrivalProblem problem = readProblem(environmentFile);

        int[][] pattern = null;
        if (patternNumber != 0) {
            pattern = getLazyGridPattern(patternNumber);
        }

        int[] bodyRadiuses = prefillBodyRadiuses(agents, radius);
        float[] maxSpeeds = prefillMaxSpeeds(agents, maxSpeed);
        
        TrajectoryCoordinationProblem instance;
        if (problem.getPlanningGraph() != null) {
        	Collection<Point> docks;
        	if (problem.getDocks() != null) {
        		docks = Arrays.asList(problem.getDocks());
        	} else {        		
        		docks = problem.getPlanningGraph().vertexSet();
        	}
        	
        	// filter out docks that are not in the mission area
        	if (missionArea != null) {
        		LinkedList<Point> docksInMissionArea = new LinkedList<Point>();
        		for (Point dock : docks) {
        			if (missionArea.isInside(dock)) {
        				docksInMissionArea.add(dock);
        			}
        		}
        		docks = docksInMissionArea;
        	}
        	
			instance = ConflictGenerator.generateInstance(
					problem.getEnvironment(), problem.getPlanningGraph(),
					docks, bodyRadiuses, maxSpeeds, MAXTIME, oneCluster, startAndGoalsCanOverlap, sgAvoiding,
					new Random(seed));
        } else {
			instance = ConflictGenerator.generateInstance(
					problem.getEnvironment(), bodyRadiuses, maxSpeeds, pattern, step,
					missionArea, MAXTIME, oneCluster, startAndGoalsCanOverlap, sgAvoiding, 
					new Random(seed));
        }
        
        return instance;
    }

    private static int[] prefillBodyRadiuses(int agents, int radius) {
        int[] bodyRadiuses = new int[agents];
        Arrays.fill(bodyRadiuses, radius);
        return bodyRadiuses;
    }

    private static float[] prefillMaxSpeeds(int agents, float maxSpeed) {
        float[] maxSpeeds = new float[agents];
        Arrays.fill(maxSpeeds, maxSpeed);
        return maxSpeeds;
    }

    private static int[][] getLazyGridPattern(int patternNumber) {
        int[][] pattern;
        switch (patternNumber) {
            case 4:
                pattern = LazyGrid.PATTERN_4_WAY;
                break;
            case 8:
                pattern = LazyGrid.PATTERN_8_WAY;
                break;
            case 16:
                pattern = LazyGrid.PATTERN_16_WAY;
                break;
            default:
                throw new RuntimeException("No such grid pattern " + patternNumber);
        }
        return pattern;
    }

    private static EarliestArrivalProblem readProblem(String environmentFile) {
        EarliestArrivalProblem problem;

        try {
            File file = new File(environmentFile);
            FileInputStream inputStream = new FileInputStream(file);
            problem = TrajectoryCoordinationProblemXMLDeserializer.deserialize(inputStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Environment could not be loaded. File not found: " + e);
        }

        return problem;
    }

    public static void main(String[] args) throws ProblemNotCreatedException, FileNotFoundException {

        String environmentFile = Args.getArgumentValue(args, "-env", true);
        int agents = Integer.parseInt(Args.getArgumentValue(args, "-nagents", true));
        int radius = Integer.parseInt(Args.getArgumentValue(args, "-radius", true));
        float maxSpeed = Float.parseFloat(Args.getArgumentValue(args, "-maxspeed", false, "1.0"));

        int patternNumber = 0;
        String patternNumberStr = Args.getArgumentValue(args, "-gridpattern", false);
        if (patternNumberStr != null)
            patternNumber = Integer.parseInt(patternNumberStr);

        int step = 0;
        String stepStr = Args.getArgumentValue(args, "-gridstep", false);

        if (stepStr != null)
            step = Integer.parseInt(stepStr);
        
        
        boolean oneCluster =  Args.isArgumentSet(args, "-onecluster");
        boolean sgNoOverlap = Args.isArgumentSet(args, "-sgnooverlap");
        boolean sgAvoiding = Args.isArgumentSet(args, "-sgavoiding");
        

        int seed = Integer.parseInt(Args.getArgumentValue(args, "-seed", true));

        String outFile = Args.getArgumentValue(args, "-outfile", false);
        boolean showVis = Args.isArgumentSet(args, "-showvis");

        if (Args.isArgumentSet(args, "-verbose")) {
        	Verbose.setVerbose(true);
        }

        Rectangle missionArea = null;
        if (Args.isArgumentSet(args, "-missionarea")) {
            String missionAreaStr = Args.getArgumentValue(args, "-missionarea", false);
            String[] parts = missionAreaStr.split("x");
            assert parts.length == 2;
            
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            
            missionArea = new Rectangle(
                    new Point(0, 0),
                    new Point(x, y)
            );
        }

        TrajectoryCoordinationProblem instance
        	= generateInstance(environmentFile, agents, radius, maxSpeed, patternNumber, step, seed, missionArea, oneCluster, !sgNoOverlap, sgAvoiding);

        PrintStream outStream = System.out;

        if (outFile != null) {
        	outStream = new PrintStream(new File(outFile));
        }

        if (showVis) {
        	VisUtil.initVisualization(instance, "Generated Instance", null, 10);
        	VisUtil.visualizeEarliestArrivalProblem(instance);
        }

        TrajectoryCoordinationProblemXMLSerializer.serialize(instance, outStream);



    }
}
