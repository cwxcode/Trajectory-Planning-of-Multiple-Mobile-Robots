package tt.jointeuclid2ni.probleminstance.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import tt.euclid2i.Point;
import tt.euclid2i.discretization.LazyGrid;
import tt.euclid2i.probleminstance.Environment;
import tt.euclid2i.region.Rectangle;
import tt.jointeuclid2ni.probleminstance.AgentMission;
import tt.jointeuclid2ni.probleminstance.AgentMissionImpl;
import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;
import tt.jointeuclid2ni.probleminstance.TrajectoryCoordinationProblemXMLDeserializer;
import tt.jointeuclid2ni.probleminstance.TrajectoryCoordinationProblemXMLSerializer;
import tt.jointeuclid2ni.probleminstance.generator.exception.MissionNotAddedException;
import tt.jointeuclid2ni.probleminstance.generator.exception.ProblemNotCreatedException;
import tt.util.Args;
import tt.util.Verbose;
import tt.vis.problemcreator.main.ExtensibleProblem;

public class GenerateDenseGridInstance {

    private boolean verbose = false;
    private String environmentFile;
    private int agents;
    private int step;
    private int seed;
	private int cellSize;
	private int agentRadius;

    public GenerateDenseGridInstance(String environmentFile, int agents, int cellWidth, int agentRadius, int seed) {
        this.environmentFile = environmentFile;
        this.agents = agents;
        this.cellSize = cellWidth;
        this.agentRadius = agentRadius;
        this.seed = seed;
    }

    private void run() throws ProblemNotCreatedException {
        Verbose.setVerbose(verbose);
        Random random = new Random(seed);
        Environment environment = readEnvironment(environmentFile);

        ExtensibleProblem problem = new ExtensibleProblem();
        problem.setEnvironment(environment);

        LinkedList<Point> starts = new LinkedList<Point>();
        LinkedList<Point> targets = new LinkedList<Point>();

        for(int x=cellSize/2; x <= 1000-cellSize/2; x += cellSize) {
	        for(int y=cellSize/2; y <= 1000-cellSize/2; y += cellSize) {
	        	starts.add(new Point(x,y));
	        	targets.add(new Point(x,y));
	        }
        }

        for (int i=0; i<agents; i++) {
        	Point start = starts.remove(random.nextInt(starts.size()));
        	Point target = targets.remove(random.nextInt(targets.size()));
        	problem.addAgent(start, target, agentRadius, 1);
        }

        TrajectoryCoordinationProblemXMLSerializer.serialize(problem, System.out);
    }

    private Environment readEnvironment(String environmentFile) {
        EarliestArrivalProblem problem;

        try {
            File file = new File(environmentFile);
            FileInputStream inputStream = new FileInputStream(file);
            problem = TrajectoryCoordinationProblemXMLDeserializer.deserialize(inputStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Environment could not be loaded");
        }

        return problem.getEnvironment();
    }

    public static void main(String[] args) throws ProblemNotCreatedException {

        String environmentFile = Args.getArgumentValue(args, "-env", true);
        int agents = Integer.parseInt(Args.getArgumentValue(args, "-nagents", true));
        int radius = Integer.parseInt(Args.getArgumentValue(args, "-radius", true));
        int cellSize = Integer.parseInt(Args.getArgumentValue(args, "-cellsize", true));
        int seed = Integer.parseInt(Args.getArgumentValue(args, "-seed", true));

        GenerateDenseGridInstance main = new GenerateDenseGridInstance(environmentFile, agents, cellSize, radius, seed);
        main.run();
    }
}
