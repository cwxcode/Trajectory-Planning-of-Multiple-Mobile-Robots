package tt.euclidtime3i.discretization.softconstraints;

public class ConstantSeparationPenaltyFunction implements PenaltyFunction {

    private double penalty;
	private double minSeparation;
	private int separationSquare;

    public ConstantSeparationPenaltyFunction(double penalty, double minSeparation) {
        super();
        this.penalty = penalty;
        this.minSeparation = minSeparation;
        this.separationSquare = (int) Math.ceil(minSeparation);
        this.separationSquare *= separationSquare; // ^2
    }

    @Override
    public double getPenalty(double dist, double t) {
        if (dist <= minSeparation) {
        	return penalty;
        } else {
            return 0;
        }
    }

    public double getMinSeparation() {
		return minSeparation;
	}

	@Override
	public int getSeparationSquare() {
		return separationSquare;
	}
    
    
}