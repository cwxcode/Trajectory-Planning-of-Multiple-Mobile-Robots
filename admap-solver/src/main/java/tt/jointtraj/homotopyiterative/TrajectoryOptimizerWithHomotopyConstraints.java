package tt.jointtraj.homotopyiterative;

import org.jscience.mathematics.number.Complex;

import tt.jointtraj.separableflow.TrajectoryOptimizer;
import tt.planner.homotopy.hvalue.HValuePolicy;

public interface TrajectoryOptimizerWithHomotopyConstraints extends TrajectoryOptimizer{
	void setHomotopyPolicy(HValuePolicy hPolicy);
	Complex getHValueOfLastPath();
}
