package tt.jointeuclid2ni.probleminstance.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import org.apache.log4j.Logger;

import tt.euclid2i.Point;
import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;
import tt.jointeuclid2ni.probleminstance.TrajectoryCoordinationProblem;
import tt.jointeuclid2ni.probleminstance.TrajectoryCoordinationProblemImpl;
import tt.jointeuclid2ni.probleminstance.TrajectoryCoordinationProblemXMLDeserializer;
import tt.jointeuclid2ni.probleminstance.TrajectoryCoordinationProblemXMLSerializer;
import tt.jointeuclid2ni.probleminstance.VisUtil;
import tt.jointeuclid2ni.probleminstance.generator.exception.ProblemNotCreatedException;
import tt.util.Args;
import tt.util.Verbose;

public class GenerateRTInstance {
	
	private static Logger LOGGER = Logger.getLogger(GenerateRTInstance.class);

	private static final int FIRSTTASK = 5000;

	public static TrajectoryCoordinationProblem generateInstance(
			String environmentFile, int nAgents, int radius, float maxSpeed, int seed) throws ProblemNotCreatedException {

		EarliestArrivalProblem inInstance = readProblem(environmentFile);
		Random rnd = new Random(seed);
		Point[] starts = new Point[nAgents];

		// all endpoints without the starting positions
		LinkedList<Point> midPoints = new LinkedList<Point>(Arrays.asList(inInstance.getDocks()));	
		
		// generate starting positions
		for (int j = 0; j < nAgents; j++) {
			starts[j] = randomVertex(midPoints, rnd);
			midPoints.remove(starts[j]);
		}
		
		int[] bodyRadiuses = new int[nAgents];
		Arrays.fill(bodyRadiuses, radius);

		float[] maxSpeeds = new float[nAgents];
		Arrays.fill(maxSpeeds, maxSpeed);
		
		LOGGER.info("There is " + midPoints.size() + " endpoints distinct from start positions of robots.");

		TrajectoryCoordinationProblem outInstance 
			= new TrajectoryCoordinationProblemImpl(inInstance.getEnvironment(), starts, bodyRadiuses, maxSpeeds, inInstance.getPlanningGraph(), midPoints.toArray(new Point[0]));
		
		return outInstance;
	}

	private static Point randomVertex(Collection<Point> list, Random rnd) {
		int size = list.size();
		return list.toArray(new Point[0])[rnd.nextInt(size)];
	}

	private static EarliestArrivalProblem readProblem(String environmentFile) {
		EarliestArrivalProblem problem;

		try {
			File file = new File(environmentFile);
			FileInputStream inputStream = new FileInputStream(file);
			problem = TrajectoryCoordinationProblemXMLDeserializer
					.deserialize(inputStream);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(
					"Environment could not be loaded. File not found: " + e);
		}

		return problem;
	}

	public static void main(String[] args) throws ProblemNotCreatedException,
			FileNotFoundException {

		String environmentFile = Args.getArgumentValue(args, "-env", true);
		int agents = Integer.parseInt(Args.getArgumentValue(args, "-nagents", true));
		int radius = Integer.parseInt(Args.getArgumentValue(args, "-radius", true));
		float maxSpeed = Float.parseFloat(Args.getArgumentValue(args, "-maxspeed", true));
		int seed = Integer.parseInt(Args.getArgumentValue(args, "-seed", true));

		
		String outFile = Args.getArgumentValue(args, "-outfile", false);
		boolean showVis = Args.isArgumentSet(args, "-showvis");
		
		if (Args.isArgumentSet(args, "-verbose")) {
			Verbose.setVerbose(true);
		}

		TrajectoryCoordinationProblem instance = generateInstance(environmentFile,
				agents, radius, maxSpeed, seed);

		PrintStream outStream = System.out;

		if (outFile != null) {
			outStream = new PrintStream(new File(outFile));
		}

		if (showVis) {
			VisUtil.initVisualization(instance, "Generated Instance", null, 10);
			VisUtil.visualizeRelocationTaskCoordinationProblem(instance);
		}

		TrajectoryCoordinationProblemXMLSerializer.serialize(instance, outStream);

	}
}
