package tt.jointeuclid2ni;

import static tt.jointtraj.util.Util.getSumCost;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.discretization.LazyGrid;
import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;
import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblemSimpleMissionDeserializer;
import tt.jointeuclid2ni.probleminstance.TrajectoryCoordinationProblemXMLDeserializer;
import tt.jointeuclid2ni.solver.Algorithm;
import tt.jointeuclid2ni.solver.Algorithms;
import tt.jointeuclid2ni.solver.GraphType;
import tt.jointeuclid2ni.solver.HeuristicType;
import tt.jointeuclid2ni.solver.ObjectiveType;
import tt.jointeuclid2ni.solver.Parameters;
import tt.jointeuclid2ni.solver.impl.AlgorithmDPMC;
import tt.jointeuclid2ni.solver.impl.AlgorithmDPMD;
import tt.jointeuclid2ni.solver.impl.AlgorithmIIHP;
import tt.jointeuclid2ni.solver.impl.AlgorithmODCN;
import tt.jointeuclid2ni.solver.impl.AlgorithmODPiN;
import tt.jointeuclid2ni.solver.impl.AlgorithmPP;
import tt.jointeuclid2ni.solver.impl.AlgorithmPPSIPP;
import tt.jointtraj.solver.SearchResult;
import tt.util.Args;
import tt.util.Counters;

public class Solver {
	
    public static void main(String[] args) throws FileNotFoundException {
        Parameters params = parseArguments(args);
        Algorithm alg = getAlgorithm(params.method, params.args);
        solve(alg, params.problem, params);
    }

    public static Parameters parseArguments(String[] args) throws FileNotFoundException {
        Parameters params = new Parameters();

        if (args.length == 0 || Args.isArgumentSet(args, "-help")) {
            printHelp();
            System.exit(0);
        }
        
        params.args  = args;

        params.method = Args.getArgumentValue(args, "-method", true);
        String xml = Args.getArgumentValue(args, "-problemfile", true);
        String mission = Args.getArgumentValue(args, "-mission", false);
        String timeoutStr = Args.getArgumentValue(args, "-timeout", false);
        String maxtimeStr = Args.getArgumentValue(args, "-maxtime", true);
        String gridStepStr = Args.getArgumentValue(args, "-gridstep", false);
        String gridPatternStr = Args.getArgumentValue(args, "-grid", false);
        String roadmapStr = Args.getArgumentValue(args, "-roadmap", false);
        
        params.timeStep = Integer.parseInt(Args.getArgumentValue(args, "-timestep", false, "1"));
        params.samplingInterval = Integer.parseInt(Args.getArgumentValue(args, "-samplingInterval", false, "10"));
        
        params.waitMoveDuration = Integer.parseInt(Args.getArgumentValue(args, "-waitmove", false, "" + params.timeStep));
        String heuristicStr = Args.getArgumentValue(args, "-heuristic", false, "PERFECT");
        params.heuristic = HeuristicType.valueOf(heuristicStr);
        String objectiveStr = Args.getArgumentValue(args, "-objective", false, "ARRIVAL");
        params.objective = ObjectiveType.valueOf(objectiveStr);
        params.showVis = Args.isArgumentSet(args, "-showvis");
        params.printSummary = Args.isArgumentSet(args, "-summary");
        params.summaryPrefix = Args.getArgumentValue(args, "-summaryprefix", false, "");
        params.verbose = Args.isArgumentSet(args, "-verbose");
        String bgImgFileName = Args.getArgumentValue(args, "-bgimg", false, null);
        
        
        File file = new File(xml);
        params.fileName = file.getName();
        params.problem = TrajectoryCoordinationProblemXMLDeserializer.deserialize(new FileInputStream(file));
        
       	params.samplingInterval = 10;

	    // Load the PNG image as a background, if provided
	    if (bgImgFileName != null) {
		    File bgImgFile = new File(bgImgFileName);
		    if (bgImgFile.exists()) {
		    	params.bgImageFile = bgImgFile;
		    }        	
        }
        
        if (mission != null) {
            params.problem = EarliestArrivalProblemSimpleMissionDeserializer.deserialize(params.problem.getEnvironment(), mission);
        }

        int timeoutMs = Integer.MAX_VALUE;
        if (timeoutStr != null) {
            timeoutMs = Integer.parseInt(timeoutStr);
        }

        params.maxSpeed = 1;
        for (int i = 0; i < params.problem.getMaxSpeeds().length; i++) {
            if (params.problem.getMaxSpeed(i) > params.maxSpeed) {
                params.maxSpeed = params.problem.getMaxSpeed(i);
            }
        }

        params.maxTime = Integer.parseInt(maxtimeStr);
        if (gridStepStr != null) {
            params.graphType = GraphType.GRID;
            params.gridStep = Integer.parseInt(gridStepStr);
        }

        if (roadmapStr != null) {
            String[] parts = roadmapStr.split("/");
            if (parts.length != 2) {
                throw new RuntimeException("Invalid roadmap specifier " + roadmapStr + ". Expecting 'D/R', where D is the dispersion between the samples in the roadmap and R is the connection radius.");
            }
            params.graphType = GraphType.ROADMAP;
            params.roadmapDispersion = Integer.parseInt(parts[0]);
            params.roadmapConnectionRadius = Integer.parseInt(parts[1]);
        }

        if (gridPatternStr != null) {
            params.gridPattern = parsePattern(gridPatternStr);
        }

        params.startedAtMs = System.currentTimeMillis();
        params.runtimeDeadlineMs = System.currentTimeMillis() + timeoutMs;
        
        return params;
    }

    private static int[][] parsePattern(String gridPatternStr) {
        if (gridPatternStr.equals("4"))
            return LazyGrid.PATTERN_4_WAY;

        if (gridPatternStr.equals("8"))
            return LazyGrid.PATTERN_8_WAY;

        if (gridPatternStr.equals("16"))
            return LazyGrid.PATTERN_16_WAY;

        throw new RuntimeException("Unsupported grid pattern:" + gridPatternStr);
    }

    public static void printHelp() {
        System.out.println("Name");
        System.out.println("      solver.jar -- the multi-robot deconfliction problem solver");
        System.out.println();
        System.out.println("java -jar solver.jar -method method_abbr -problemfile path_to_xml_problem_file [-mission mission_spec]");
        System.out.println("                     -maxtime max_time_of_trajectories -gridstep discretization_step");
        System.out.println("                    [-timeout maximum_runtime_in_ms] [-showvis]");

        System.out.println();
        System.out.println("Options:");
        System.out.println();
        System.out.println("  -method: the name of method to be used for solving, currently supported:");
        System.out.println("  	'PP' = prioritized planning, 'ODCN' = operator decomposition, 'IIHP' = incremental iterative homotopy planner");
        System.out.println("  	'KDPMD' = k-step distributed penalty method with discrete trajectory optimizers, ");
        System.out.println("  	'KDPMC' = k-step distributed penalty method with continuous trajectory optimizers ");
        System.out.println();
        System.out.println("  -problemfile: a path to an XML file containing description fo the environment and and the mission");
        System.out.println();
        System.out.println("  -mission: mission descriptor, contains start, target and body radius of each agent in the environment. ");
        System.out.println("            If provided, overrides the mission from XML problem file. The mission has the following format: ");
        System.out.println("            StartX1,StartY1:TargetX1,TargetY1:BodyRadius1:MaxSpeed1;StartX2,StartY2:TargetX2,TargetY2:BodyRadius2:MaxSpeed2;...");
        System.out.println();
        System.out.println("  -maxtime: specifies maximum time (duration) of the resulting trajectories");
        System.out.println();
        System.out.println("  -grid: the degree (connectivity) of the planning grid. Currently supported 4, 8, 16. ");
        System.out.println();
        System.out.println("  -gridstep: specifies the step of the grid-discretization used in the trajectory planning routines");
        System.out.println();
        System.out.println("  -roadmap: parameters of a roadmap for planning in the form 'N/R'");
        System.out.println("            where N is the number of samples and R is the connection radius");
        System.out.println();
        System.out.println("  -heuristic: specifies the heuristic used during trajectory planning. Currently supported: L1, L2, PERFECT");
        System.out.println();
        System.out.println("  -timeout: specifies the runtime limit for the solver in miliseconds");
        System.out.println();
        System.out.println("  -showvis: turns on/off the visualization window");
        System.out.println();
        System.out.println("  -verbose: turns on the debugging messages");
        System.out.println();
        System.out.println("  -summary: prints out only the cost of the solution found, 'inf' if not solution found");
        System.out.println();
        System.out.println("  -summaryprefix: a string to be printed out right before the cost number");
        System.out.println();

        System.out.println();
        System.out.println("Output:");
        System.out.println();
        System.out.println("  Prints out a sampled version of the resulting trajectories, one trajectory per line in the following format:");
        System.out.println("  time1 posX1 posY1; time2 posX2 posY2; time3 posX3 posY3; ...");
    }
    
    public static Algorithm getAlgorithm(String method, String[] custsomArgs) {
    	
    	Algorithms algorithmName = null; 
        try {
        	algorithmName = Algorithms.valueOf(method);
        } catch (IllegalArgumentException ex) {}

        Algorithm algorithm;
        switch (algorithmName) {
            case PP:
                algorithm = new AlgorithmPP();
                break;

            case KDPMD:
                algorithm = new AlgorithmDPMD(custsomArgs);
                break;

            case KDPMC:
                algorithm = new AlgorithmDPMC(custsomArgs);
                break;

            case IIHP:
                algorithm = new AlgorithmIIHP();
                break;

            case ODPIN:
                algorithm = new AlgorithmODPiN();
                break;

            case ODCN:
                algorithm = new AlgorithmODCN();
                break;

            case PP_SIPP:
                algorithm = new AlgorithmPPSIPP();
                break;

            default:
                throw new RuntimeException("Unknown method");
        }
        
        return algorithm;
    }

    public static void solve(Algorithm algorithm, EarliestArrivalProblem problem, Parameters params) {
    	
    	SearchResult result;
    	try {    	
    		result = algorithm.solve(params.problem, params);
            if (params.printSummary) {
                printSummary(result.getTrajectories(), params, toStatus(result));
            } else {
                if (result.foundSolution()) {
                    tt.jointtraj.util.Util.exportTrajectories(result.getTrajectories(), new PrintWriter(System.out), params.timeStep, params.maxTime);
                } else {
                    System.out.println("No solution found.");
                }
            }
    	} catch (OutOfMemoryError e) {
    		printSummary(null, params, Status.OUTOFMEMORY);
    	}
    }
    
    public static Status toStatus(SearchResult searchResult) {
    	if (searchResult.finished) {
    		if (searchResult.foundSolution()) {
    			return Status.SUCCESS;
    		} else {
    			return Status.FAIL;
    		}
    	} else {
    		return Status.TIMEOUT;
    	}
    }
    
    static enum Status {SUCCESS, FAIL, TIMEOUT, OUTOFMEMORY, NA}

    public static void printSummary(EvaluatedTrajectory[] trajs, Parameters params, Status status) {
        String costStr;
        if (trajs != null) {
            double cost = getSumCost(trajs);
            costStr = String.format("%.2f", cost);
        } else {
            costStr = "inf";
        }

        long runtimeElapsed = System.currentTimeMillis() - params.startedAtMs;

        System.out.print(params.summaryPrefix);
        System.out.print(costStr + ";");
        System.out.print(runtimeElapsed + ";");
        System.out.print(Counters.expandedStatesCounter + ";");
        System.out.print(status.toString() + ";");
        System.out.println();
    }
}
