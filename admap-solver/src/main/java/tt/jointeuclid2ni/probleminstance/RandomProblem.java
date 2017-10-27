package tt.jointeuclid2ni.probleminstance;

import java.util.Collection;
import java.util.Random;

import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.probleminstance.Environment;
import tt.euclid2i.probleminstance.RandomEnvironment;
import tt.euclid2i.util.Util;

public class RandomProblem extends TrajectoryCoordinationProblemImpl {

    int agentBodyRadius;
    Random random;

    public RandomProblem(int nAgents, int agentSizeRadius, int seed) {
        this(new RandomEnvironment(1000, 1000, 5, 150, seed), nAgents, agentSizeRadius, seed);
    }

    public RandomProblem(Environment environment, int nAgents, int agentSizeRadius, int seed) {
        super(environment, nAgents);
        this.agentBodyRadius = agentSizeRadius;
        this.random = new Random(seed);
        generateMissions();
    }

    protected void generateMissions() {

        int trials = 0;

        Collection<Region> inflatedObstaclesCommon = Util.inflateRegions(getObstacles(), agentBodyRadius);

        for (int i = 0; i < nAgents; i++) {
            Point start;
            Point target;
            bodyRadiuses[i] = agentBodyRadius;

            do {
                start = Util.sampleFreeSpace(getEnvironment().getBoundary().getBoundingBox(), inflatedObstaclesCommon, random);
                target = Util.sampleFreeSpace(getEnvironment().getBoundary().getBoundingBox(), inflatedObstaclesCommon, random);

                trials++;
                if (trials > 10000) {
                    throw new CannotPlaceAgentsException();
                }
            }
            while (!isUniqueStart(start, bodyRadiuses[i]) || !isUniqueTarget(target, bodyRadiuses[i]));

            starts[i] = start;
            targets[i] = target;
        }
    }

}

