package tt.vis.problemcreator;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.layer.GroupLayer;
import cz.agents.alite.vis.layer.VisLayer;
import cz.agents.alite.vis.layer.common.HelpLayer;
import tt.euclid2i.region.Rectangle;
import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;
import tt.vis.problemcreator.hid.HIDAdapter;
import tt.vis.problemcreator.main.ProblemDesigner;

import java.awt.*;

public class ProblemDesignerLayer extends GroupLayer {

    private static String help = " [Problem Creator layer] \n" +
            " This layer allows to draw polygonal obstacles and specify starts and destinations for each agent.\n" +
            "\n" +
            " Keybaord controls:\n" +
            "   a - starts creating a new agent\n" +
            "   d - starts creating a new dock\n" +
            "   o - starts creating a new obstacle\n" +
            "   s - exports the currently displayed problem instace to XML format (by default written out to the standard output)\n" +
            "   <space> - saves the currently created agent or obstacle\n" +
            "   <backspace> - undo\n\n" +
            "  \n" +
            "   To finish specification of every new agent / obstacle press <space> or start specifying a new agent or obstacle";

    private ProblemDesigner creator;
    private HIDAdapter adapter;
    
    public static ProblemDesignerLayer create() {
    	return create(null);
    }

    public static ProblemDesignerLayer create(EarliestArrivalProblem inputProblem) {
        ProblemDesignerLayer problemLayer = new ProblemDesignerLayer(inputProblem);
        VisLayer helpLayer = HelpLayer.create();

        problemLayer.setHelpOverrideString(help);

        problemLayer.addSubLayer(helpLayer);
        return problemLayer;
    }

    protected ProblemDesignerLayer(EarliestArrivalProblem inputProblem) {
        this.creator = new ProblemDesigner(inputProblem);
        this.adapter = new HIDAdapter(creator);
    }

    protected ProblemDesignerLayer(EarliestArrivalProblem inputProblem, Rectangle bounds, int agentRadius, int agentSpeed) {
        this.creator = new ProblemDesigner(inputProblem, bounds, agentRadius, agentSpeed);
        this.adapter = new HIDAdapter(creator);
    }

    public void addListener(ProblemCreatedListener listener) {
        creator.addListener(listener);
    }

    @Override
    public void init(Vis vis) {
        super.init(vis);
        vis.addKeyListener(adapter);
        vis.addMouseListener(adapter);
    }

    @Override
    public void deinit(Vis vis) {
        super.deinit(vis);
        vis.removeKeyListener(adapter);
        vis.removeMouseListener(adapter);
    }

    @Override
    public void paint(Graphics2D canvas) {
        canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        creator.paint(canvas);
        super.paint(canvas);
    }
}
