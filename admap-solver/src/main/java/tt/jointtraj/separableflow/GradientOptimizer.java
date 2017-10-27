package tt.jointtraj.separableflow;

import java.util.Arrays;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer.Formula;

import tt.euclid2d.trajectory.DesiredTrajectoryDifferenceObjective;
import tt.euclid2d.trajectory.TrajectoryObjectiveFunctionAtPoint;
import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Point;
import tt.euclid2i.Trajectory;
import tt.euclid2i.trajectory.EvaluatedTrajectoryWrapper;
import tt.euclid2i.trajectory.LinearTrajectory;
import tt.euclid2i.trajectory.PointArrayTrajectory;
import tt.euclidtime3i.discretization.softconstraints.PenaltyFunction;
import tt.util.Verbose;


public class GradientOptimizer implements TrajectoryOptimizer {

	Point2d start;
	Point2d target;
	TrajectoryObjectiveFunctionAtPoint objective;
	float maxSpeed;
	int maxTime;
	int samplingInterval;
	double descentStep;
	double costEps;
	EvaluatedTrajectory currentTraj;
	final double H = 0.0001;
	boolean verbose;
	int maxIter;

	public GradientOptimizer(Point start, Point target, TrajectoryObjectiveFunctionAtPoint objective, float maxSpeed, int maxTime,
			int samplingInterval, double step, double costEps, int maxIter, boolean verbose) {
		super();
		this.start = new Point2d(start.x, start.y);
		this.target = new Point2d(target.x, target.y);
		this.objective = objective;
		this.maxSpeed = maxSpeed;
		this.maxTime = maxTime;
		this.samplingInterval = samplingInterval;
		this.descentStep = step;
		this.costEps = costEps;
		this.verbose = verbose;
		this.maxIter = maxIter;
		Verbose.setVerbose(verbose);
	}

	@Override
	public PenalizedEvaluatedTrajectory getOptimalTrajectoryUnconstrained(
			double maxAllowedCost, long runtimeDeadlineMs) {

		if (objective instanceof DesiredTrajectoryDifferenceObjective) {
			Trajectory desiredTraj = ((DesiredTrajectoryDifferenceObjective)objective).getDesiredTrajectory();
			return new PenalizedEvaluatedTrajectory(new EvaluatedTrajectoryWrapper(desiredTraj, 0), Double.NaN);
		} else {
			LinearTrajectory traj = new LinearTrajectory(0, new Point(
					(int) start.x, (int) start.y), new Point((int) target.x,
					(int) target.y), maxSpeed, maxTime, start.distance(target));
			return new PenalizedEvaluatedTrajectory(traj, Double.NaN);
		}
	}

	@Override
	public PenalizedEvaluatedTrajectory getOptimalTrajectoryConstrained(
			PenaltyFunction[] penaltyFunctions, Trajectory[] otherTrajectories,
			Trajectory initialTrajectory, double maxAllowedCost,
			long runtimeDeadlineMs) {

		if (initialTrajectory == null)  {
			if (objective instanceof DesiredTrajectoryDifferenceObjective) {
				initialTrajectory = ((DesiredTrajectoryDifferenceObjective)objective).getDesiredTrajectory();
			} else {
				initialTrajectory = getOptimalTrajectoryUnconstrained(maxAllowedCost, runtimeDeadlineMs);
			}
		}

		// Convert initial trajectory to the trajectory vector
		Point2d[] traj = new Point2d[maxTime / samplingInterval + 1];
		for (int i = 0; i < traj.length; i++) {
			Point point2i = initialTrajectory.get(i * samplingInterval);
			traj[i] = new Point2d(point2i.x, point2i.y);
		}

		traj = simpleGradientDescent(traj, penaltyFunctions, otherTrajectories);
		//traj = conjugateGradientDescent(traj, penaltyFunctions, otherTrajectories);

		double objectiveValue = computeObjective(objective, traj, samplingInterval);
		EvaluatedTrajectory eTraj = toTrajectory(traj, objectiveValue);
		this.currentTraj = eTraj;
		return new PenalizedEvaluatedTrajectory(eTraj, Double.NaN);
	}

	private Point2d[] conjugateGradientDescent(final Point2d[] traj,
			final PenaltyFunction[] penaltyFunctions, final Trajectory[] otherTrajectories) {

		MultivariateFunction objectiveFunction = new MultivariateFunction() {

			@Override
			public double value(double[] point) {
				double cost = computeTrajectoryCost(doubleArrayToPointArray(point), objective, (int) maxSpeed, penaltyFunctions, otherTrajectories, samplingInterval);
				Verbose.println("Asking for value at " + GradientOptimizer.toString(doubleArrayToPointArray(point)));
				return cost;
			}
		};

		MultivariateVectorFunction gradientFunction = new MultivariateVectorFunction() {
			@Override
			public double[] value(double[] point) {

				Verbose.print("Asking for gradient: " +  GradientOptimizer.toString(doubleArrayToPointArray(point)) + "\n");
				currentTraj = new PointArrayTrajectory(doubleArrayToPointArray(point), samplingInterval, 0.0);

				final double h = 0.0001;
				Vector2d[] gradient = computeGradient(
						doubleArrayToPointArray(point), objective, Double.NaN,  penaltyFunctions,
						otherTrajectories, h,
						maxSpeed,
						samplingInterval);

				//Verbose.print("Returning gradient: " +  GradientOptimizer.toString(gradient) + "\n");
				//Verbose.println("normalizing");
				//normalize(gradient);

				Verbose.print("Returning gradient: " +  GradientOptimizer.toString(gradient) + "\n");

				return tupleArrayToDoubleArray(gradient);
			}
		};

		NonLinearConjugateGradientOptimizer optimizer = new NonLinearConjugateGradientOptimizer(Formula.POLAK_RIBIERE, new SimpleValueChecker(-1, 0.001, 1000));

		PointValuePair minimizer = null;
		double[] initial = tupleArrayToDoubleArray(traj);
		minimizer = optimizer.optimize(
					new MaxEval(50000),
					new InitialGuess(initial),
					//SimpleBounds.unbounded(traj.length*2),
					GoalType.MINIMIZE,
					new ObjectiveFunction(objectiveFunction),
					new ObjectiveFunctionGradient(gradientFunction),
					new NonLinearConjugateGradientOptimizer.BracketingStep(0.001));


		Verbose.print("Optimization cost: " + minimizer.getValue() + " iter: " + optimizer.getIterations() + " sol: " + Arrays.toString(minimizer.getPoint())+ "\n");

		return doubleArrayToPointArray(minimizer.getPoint());
	}

	private Point2d[] simpleGradientDescent(Point2d[] traj,
			PenaltyFunction[] penaltyFunctions, Trajectory[] otherTrajectories) {
		double cost = computeTrajectoryCost(traj, objective, maxSpeed,
				penaltyFunctions, otherTrajectories, samplingInterval);

		if (verbose) {
			printPenalties(0, traj, objective, maxSpeed, penaltyFunctions,
					otherTrajectories, samplingInterval);
		}


		// descent
		int i = 0;
		boolean converged = false;
		while (!converged) {


			// compute gradient
			Vector2d[] gradient = computeGradient(traj, objective, cost, penaltyFunctions,
					otherTrajectories, H, maxSpeed, samplingInterval);

			if (verbose) {
				System.out.printf("\t\tGradNorm: %.4f Grad: %s \n", norm(gradient), toString(gradient));
			}

			normalize(gradient);

			// Normalize the gradient!!!
			traj = makeStep(traj, gradient, descentStep);

			currentTraj = toTrajectory(traj, cost);

			double oldCost = cost;
			cost = computeTrajectoryCost(traj, objective, maxSpeed, penaltyFunctions,
					otherTrajectories, samplingInterval);

			if (verbose) {
				printPenalties(i, traj, objective, maxSpeed, penaltyFunctions,
						otherTrajectories, samplingInterval);
			}


			double costDiff = cost - oldCost;

			// System.out.println("Current traj:" + toString(traj));
			if (verbose) {
				System.out.printf("\t\tCurrent Cost: %.2f CostDiff: %.8f \n", cost, costDiff);
			}

			if (Math.abs(costDiff) < costEps || i > maxIter) {
				converged = true;
			}
			i++;
		}
		return traj;
	}

	private void normalize(Vector2d[] vector) {
		double norm = norm(vector);

		norm = Math.sqrt(norm);

		if (norm == 0.0) {
			return;
		} else {
			for (int i = 0; i < vector.length; i++) {
				vector[i].x /= norm;
				vector[i].y /= norm;
			}
		}
	}

	private double norm(Vector2d[] vector) {
		double norm = 0;

		for (int i = 0; i < vector.length; i++) {
			norm += vector[i].x * vector[i].x;
			norm += vector[i].y * vector[i].y;
		}
		return norm;
	}

	private static Point2d[] makeStep(Point2d[] traj, Vector2d[] gradient,
			double step) {

		for (int i = 0; i < traj.length; i++) {
			traj[i].x += step * (-gradient[i].x);
			traj[i].y += step * (-gradient[i].y);
		}

		return traj;
	}

	private static Vector2d[] computeGradient(Point2d[] traj, TrajectoryObjectiveFunctionAtPoint objective,
			double originalCost, PenaltyFunction[] penaltyFunctions,
			Trajectory[] otherTrajectories, double h,
			float speed, int samplingInterval) {
		Vector2d[] gradient = new Vector2d[traj.length];

		if (Double.isNaN(originalCost)) {
			originalCost = computeTrajectoryCost(traj, objective, speed, penaltyFunctions, otherTrajectories, samplingInterval);
		}

		if (Double.isInfinite(originalCost)) {
			originalCost = 1000000;
		}

		gradient[0] = new Vector2d(); // the start positions is fixed...

		for (int i = 1; i < traj.length - 1; i++) {

			gradient[i] = new Vector2d();

			{
				// compute gradient at X
				Point2d[] newTraj = Arrays.copyOf(traj, traj.length);
				newTraj[i] = new Point2d(traj[i].x + h, traj[i].y);
				double newCost = computeTrajectoryCost(newTraj, objective, speed,
						penaltyFunctions, otherTrajectories, samplingInterval);
				double iPosGradientX = (newCost - originalCost) / (double) h;
				gradient[i].x = iPosGradientX;
			}

			{
				// compute gradient at Y
				Point2d[] newTraj = Arrays.copyOf(traj, traj.length);
				newTraj[i] = new Point2d(traj[i].x, traj[i].y + h);
				double newCost = computeTrajectoryCost(newTraj, objective, speed,
						penaltyFunctions, otherTrajectories, samplingInterval);
				double iPosGradientY = (newCost - originalCost) / (double) h;
				gradient[i].y = iPosGradientY;
			}

			if (Double.isNaN(gradient[i].x) || Double.isNaN(gradient[i].y)) {
				System.out.println("!!!!" + gradient[i]);
			}
		}


		gradient[traj.length - 1] = new Vector2d(); // the goal positions is
													// fixed...

		return gradient;
	}

	private EvaluatedTrajectory toTrajectory(Point2d[] traj, double cost) {
		return new PointArrayTrajectory(traj, samplingInterval, cost);
	}

	static double computeTrajectoryCost(Point2d[] traj, TrajectoryObjectiveFunctionAtPoint objectiveFunction,
			float maxSpeed, PenaltyFunction[] penaltyFunctions,
			Trajectory[] otherTrajectories, int samplingInterval) {
		double cost = 0;
		cost += computeObjective(objectiveFunction, traj, samplingInterval);
		cost += computeMaxSpeedPenalties(traj, maxSpeed, samplingInterval);
		cost += computeSeparationPenalties(traj, penaltyFunctions,
				otherTrajectories, samplingInterval);
		return cost;
	}

	static void printPenalties(int iter, Point2d[] traj, TrajectoryObjectiveFunctionAtPoint objectiveFunction,
			float maxSpeed, PenaltyFunction[] penaltyFunctions,
			Trajectory[] otherTrajectories, int samplingInterval) {

		double objectiveValue = computeObjective(objectiveFunction, traj, samplingInterval);
		double maxSpeedPenalty = computeMaxSpeedPenalties(traj, maxSpeed, samplingInterval);
		double separation = computeSeparationPenalties(traj, penaltyFunctions,
				otherTrajectories, samplingInterval);

		double total = objectiveValue + maxSpeed + separation;

		System.out.printf("Iter: %d objectiveValue: %.4f, Max speed: %.4f, Separation: %.4f. Total: %.4f \n", iter, objectiveValue, maxSpeedPenalty, separation, total);
	}

	static double computeSeparationPenalties(Point2d[] traj,
			PenaltyFunction[] penaltyFunctions, Trajectory[] otherTrajectories,
			int samplingInterval) {
		double cost = 0;
		for (int t = 1; t < traj.length; t++) {
			for (int j = 0; j < otherTrajectories.length; j++) {
				Point otherPos = otherTrajectories[j].get(t * samplingInterval);
				if (otherPos != null) {
					cost += samplingInterval
							* penaltyFunctions[j].getPenalty(traj[t].distance(new Point2d(otherPos.x,otherPos.y)), t* samplingInterval );
				}
			}
		}
		return cost;
	}

	static double computeMaxSpeedPenalties(Point2d[] traj, float maxSpeed,
			int samplingInterval) {
		double cost = 0;
		for (int i = 1; i < traj.length; i++) {
			cost += softExpBarrier(traj[i - 1].distance(traj[i]),
					samplingInterval * (maxSpeed));
		}
		return cost;
	}

	static double computeObjective(TrajectoryObjectiveFunctionAtPoint objective, Point2d[] traj, int samplingInterval) {
		double cost = 0;
		for (int t = 0; t < traj.length; t++) {
			double value = objective.getCost(new tt.euclid2d.Point(traj[t].x, traj[t].y), t*samplingInterval);
			cost += samplingInterval * value;
		}
		return cost;
	}

	static double softLogBarrier(double x, double dmax) {
		if (x < dmax) {
			return -Math.log(dmax - x) + Math.log(dmax);
		} else {
			return Double.POSITIVE_INFINITY;
		}
	}

	static double softExpBarrier(double x, double dmax) {
		double base = 2;
		return Math.pow(base, Math.abs(x) - dmax) - Math.pow(base, -dmax);
	}

	public EvaluatedTrajectory getCurrentTraj() {
		return currentTraj;
	}

	static String toString(Tuple2d[] points) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < points.length; i++) {
			sb.append(String.format("(%.2f, %.2f)", points[i].x, points[i].y));
		}
		sb.append(")");
		return sb.toString();
	}

	static double[] tupleArrayToDoubleArray(Tuple2d[] pointArray) {
		double [] doubleArray = new double[pointArray.length*2];
		for (int i = 0; i < pointArray.length; i++) {
			doubleArray[i*2] = pointArray[i].x;
			doubleArray[i*2+1] = pointArray[i].y;
		}
		return doubleArray;
	}

	static Point2d[] doubleArrayToPointArray(double[] doubleArray) {
		Point2d[] pointArray = new Point2d[doubleArray.length/2];
		for (int i = 0; i < doubleArray.length; i+=2) {
			pointArray[i/2] = new Point2d(doubleArray[i], doubleArray[i+1]);
		}
		return pointArray;
	}

}
