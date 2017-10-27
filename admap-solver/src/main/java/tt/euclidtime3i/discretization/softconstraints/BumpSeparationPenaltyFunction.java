package tt.euclidtime3i.discretization.softconstraints;

public class BumpSeparationPenaltyFunction implements PenaltyFunction {

    private double maxPenalty;
    private double steepness;
	private double minSeparation;
	private double minPenalty;
	private int separationSquare;
	
    public BumpSeparationPenaltyFunction(double maxPenalty, double minSeparation, double steepness) {
    	this(maxPenalty, minSeparation, steepness, 0);
    }

    public BumpSeparationPenaltyFunction(double maxPenalty, double minSeparation, double steepness, double minPenalty) {
        super();
        this.maxPenalty = maxPenalty;
        this.minSeparation = minSeparation;
        this.steepness = steepness;
        this.minPenalty = minPenalty;
        
        this.separationSquare = (int) Math.ceil(minSeparation);
        this.separationSquare *= separationSquare; // ^2
    }

    @Override
    public double getPenalty(double dist, double t) {

    	if (dist <= minSeparation) {
    		double penalty = (maxPenalty/Math.exp(-steepness)) * Math.exp(-(steepness/(1-Math.pow(dist/minSeparation,2.0))));
    		return Math.max(penalty, minPenalty);
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