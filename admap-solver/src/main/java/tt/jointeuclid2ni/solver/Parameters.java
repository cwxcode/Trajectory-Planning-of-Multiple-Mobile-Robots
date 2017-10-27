package tt.jointeuclid2ni.solver;

import java.io.File;
import java.util.Random;

import tt.euclid2i.discretization.LazyGrid;
import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;

public class Parameters {

	public String[] args;
	public EarliestArrivalProblem problem;
	
    public boolean showVis = false;
    public boolean printSummary = false;
    public String method = "";
    public String summaryPrefix = "";
    public boolean verbose = false;
    public String fileName;
    public int maxTime = 10000;
    public int gridStep = 0;
    public float maxSpeed;
    public long startedAtMs;
    public long runtimeDeadlineMs;
    public int waitMoveDuration;
    public int timeStep;
    public GraphType graphType;
    public int[][] gridPattern = LazyGrid.PATTERN_4_WAY;
    public HeuristicType heuristic;
    public ObjectiveType objective;

    public int roadmapDispersion;
    public int roadmapConnectionRadius;
	public File bgImageFile;
	public String activityLogFile;
	public int noOfClusters;
	public int samplingInterval;
	public double simSpeed;

    public int disturbanceQuantum;
    public double[] disturbanceProbs;
    public int disturbanceSeed;

    public int nTasks;
	public Random random;
	public boolean waitForKey;
}
