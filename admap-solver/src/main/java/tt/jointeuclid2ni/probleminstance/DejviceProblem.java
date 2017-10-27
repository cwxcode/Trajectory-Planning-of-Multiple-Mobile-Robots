package tt.jointeuclid2ni.probleminstance;


import tt.euclid2i.Point;

public class DejviceProblem extends DejviceProblemRandom {


    public DejviceProblem() {
        super(3, 0);
    }

    @Override
    protected void generateMissions() {
        starts[0] = new Point(125, 470);
        targets[0] = new Point(840, 800);
        bodyRadiuses[0] = 12;

        starts[1] = new Point(670, 860);
        targets[1] = new Point(220, 750);
        bodyRadiuses[1] = 12;

        starts[2] = new Point(400, 440);
        targets[2] = new Point(220, 960);
        bodyRadiuses[2] = 12;

    }
}
