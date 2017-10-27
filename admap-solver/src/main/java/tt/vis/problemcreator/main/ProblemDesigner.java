package tt.vis.problemcreator.main;

import cz.agents.alite.vis.layer.GroupLayer;
import cz.agents.alite.vis.layer.VisLayer;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.vis.RegionsLayer;
import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;
import tt.jointeuclid2ni.probleminstance.TrajectoryCoordinationProblem;
import tt.vis.LineHUDLayer;
import tt.vis.problemcreator.ProblemCreatedListener;
import tt.vis.problemcreator.hid.HIDListener;
import tt.vis.problemcreator.patches.ProblemPatch;
import tt.vis.problemcreator.states.AgentCreatingState;
import tt.vis.problemcreator.states.CreatorState;
import tt.vis.problemcreator.states.DocksCreatingState;
import tt.vis.problemcreator.states.EmptyState;
import tt.vis.problemcreator.states.ObstacleCreatingState;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ProblemDesigner implements HIDListener {

    private static final int DEFAULT_RADIUS = 20;
    private static final int DEFAULT_SPEED = 1;

    private EarliestArrivalProblem inputProblem;
    private Rectangle bounds;
    private int defaultRadius;
    private int defaultSpeed;

    private List<ProblemCreatedListener> listeners;
    private CreatorState currentState;

    private Stack<ProblemPatch> patches;
    private VisLayer hudLayer;
    private VisLayer boundsLayer;

    private HUDTextProvider hudTextProvider;
    int agentCounter = 0;

    public ProblemDesigner(EarliestArrivalProblem inputProblem, Rectangle areaBounds, int agentRadius, int agentSpeed) {
    	this.inputProblem = inputProblem;
        this.currentState = EmptyState.getInstance();
        this.patches = new Stack<ProblemPatch>();

        this.bounds = areaBounds;
        this.defaultRadius = agentRadius;
        this.defaultSpeed = agentSpeed;

        this.listeners = new ArrayList<ProblemCreatedListener>();

        this.hudLayer = GroupLayer.create();
        this.hudTextProvider = new HUDTextProvider("Welcome!",
                "Use left-mouse button and keys 'a' (agent), 'o' (obstacle), 's' (save to XML), <backspace> (undo) and <space> (finish current element). ");

        this.hudLayer = LineHUDLayer.create(hudTextProvider, Color.BLUE);
        this.boundsLayer = new RegionsLayer(new RegionsLayer.RegionsProvider() {
            @Override
            public Collection<? extends Region> getRegions() {
                return Collections.singletonList(bounds);
            }
        }, Color.black, null);
    }

    public ProblemDesigner(EarliestArrivalProblem inputProblem) {
        this(inputProblem, DialogUtils.getRectangle("Bounds", "1000x1000"), DEFAULT_RADIUS, DEFAULT_SPEED);
    }

    public void addListener(ProblemCreatedListener listener) {
        listeners.add(listener);
    }

    public void constructAndSaveProblem() {
        if (currentState.hasChanged()) {
            hudTextProvider.setInfo("Finish current work first by saving the object.");
            return;
        }
        
        ExtensibleProblem problem;
        if (inputProblem != null) {
        	problem = new ExtensibleProblem(inputProblem);
        } else {
        	problem = new ExtensibleProblem();
        	problem.setBoundary(bounds);
        }

        for (ProblemPatch patch : patches) {
            patch.apply(problem);
        }
        
        notifyListeners(problem);
        hudTextProvider.setInfo("Current problem exported!");
    }

    private void notifyListeners(TrajectoryCoordinationProblem problem) {
        for (ProblemCreatedListener listener : listeners) {
            listener.handleProblem(problem);
        }
    }

    public void save() {
        if (currentState.hasChanged()) {
            patches.add(currentState.getPatch());
            currentState = EmptyState.getInstance();
            hudTextProvider.setInfo("Saved successfully");
        } else {
            hudTextProvider.setInfo("Nothing to save");
        }
    }

    public void clear() {
        if (currentState != EmptyState.getInstance()) {
            currentState = EmptyState.getInstance();

            hudTextProvider.setInfo("Creation canceled");
        } else {
            if (patches.isEmpty()) {
                hudTextProvider.setInfo("Nothing to undo");
            } else {
                hudTextProvider.setInfo("Last action undone");
                patches.pop();
            }
        }
    }

    public void createNewObject() {
        if (currentState == EmptyState.getInstance()) {
            currentState = new ObstacleCreatingState();
            hudTextProvider.setAction("Creating new obstacle");
            hudTextProvider.setInfo("---");

        } else if (currentState.hasChanged()) {
            patches.add(currentState.getPatch());
            currentState = new ObstacleCreatingState();
            hudTextProvider.setAction("Creating new obstacle");
            hudTextProvider.setInfo("Previous work was saved successfully.");

        } else {
            hudTextProvider.setInfo("Finish this first, or cancel the creation by backspace.");
        }
    }

    public void createNewAgent() {
        if (currentState == EmptyState.getInstance()) {
        	if (inputProblem != null && inputProblem.getPlanningGraph() != null) {
        		currentState = new AgentCreatingState(agentCounter++, defaultRadius, defaultSpeed, inputProblem.getPlanningGraph().vertexSet());
        	} else {
        		currentState = new AgentCreatingState(agentCounter++, defaultRadius, defaultSpeed);
        	}
            hudTextProvider.setAction("Creating new agent");
            hudTextProvider.setInfo("---");

        } else if (currentState.hasChanged()) {
            patches.add(currentState.getPatch());
            currentState = new AgentCreatingState(agentCounter++, defaultRadius, defaultSpeed);
            hudTextProvider.setAction("Creating new agent");
            hudTextProvider.setInfo("Previous work was saved successfully.");

        } else {
            hudTextProvider.setInfo("Finish this first, or cancel the creation by backspace.");
        }
    }
    
    public void createNewDock() {
        if (currentState == EmptyState.getInstance()) {
        	if (inputProblem != null && inputProblem.getPlanningGraph() != null) {
        		currentState = new DocksCreatingState(defaultRadius, inputProblem.getPlanningGraph().vertexSet());
        	} else {
        		currentState = new DocksCreatingState(defaultRadius);
        	}
            hudTextProvider.setAction("Creating new dock");
            hudTextProvider.setInfo("---");

        } else if (currentState.hasChanged()) {
            patches.add(currentState.getPatch());
            currentState = new DocksCreatingState(defaultRadius);
            hudTextProvider.setAction("Creating new dock");
            hudTextProvider.setInfo("Previous work was saved successfully.");

        } else {
            hudTextProvider.setInfo("Finish this first, or cancel the creation by backspace.");
        }
    }

    public void keyTyped(int keyCode) {
        if (currentState != EmptyState.getInstance())
            currentState.keyTyped(keyCode);
    }

    public void mouseClicked(Point point, int button) {
        if (currentState != EmptyState.getInstance())
            currentState.mouseClicked(point, button);
    }

    public void paint(Graphics2D g) {
        boundsLayer.paint(g);

        for (ProblemPatch patch : patches) {
            patch.paint(g);
        }

        currentState.paint(g);
        hudLayer.paint(g);
    }

    private class HUDTextProvider implements LineHUDLayer.TextProvider {

        private String action;
        private String info;

        private HUDTextProvider(String action, String info) {
            this.action = action;
            this.info = info;
        }

        private void setAction(String action) {
            this.action = action;
        }

        private void setInfo(String info) {
            this.info = info;
        }

        @Override
        public String getText() {
            return action + " : " + info;
        }
    }
}
