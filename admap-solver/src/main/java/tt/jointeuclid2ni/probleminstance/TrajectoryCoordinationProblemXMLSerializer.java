package tt.jointeuclid2ni.probleminstance;

import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jgrapht.DirectedGraph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.region.Circle;
import tt.euclid2i.region.Polygon;
import tt.euclid2i.region.Rectangle;


public class TrajectoryCoordinationProblemXMLSerializer {

    private TrajectoryCoordinationProblem problem;
    private OutputStream stream;
    private Document doc;

    public static void serialize(TrajectoryCoordinationProblem problem, OutputStream stream) {
        new TrajectoryCoordinationProblemXMLSerializer(problem, stream).serialize();
    }

    private TrajectoryCoordinationProblemXMLSerializer(TrajectoryCoordinationProblem problem, OutputStream stream) {
        this.problem = problem;
        this.stream = stream;

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            this.doc = docBuilder.newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void serialize() {
        // root
        Element problemElem = addElement(EarliestArrivalProblemXMLConstants.PROBLEM, doc);
        // env
        Element environmentElem = addElement(EarliestArrivalProblemXMLConstants.ENVIRONMENT, problemElem);
        // obstacles
        Element obstaclesElem = addElement(EarliestArrivalProblemXMLConstants.OBSTACLES, environmentElem);

        //int i = 0;
        for (Region region : problem.getObstacles()) {
            // obstacle
            if (region instanceof Polygon) {
            	Element obstacleElem = addElement(EarliestArrivalProblemXMLConstants.OBSTACLE, obstaclesElem);
	            String pointsStr = pointArrayToString(((Polygon) region).getPoints());
	            obstacleElem.appendChild(doc.createTextNode(pointsStr));
            } else if (region instanceof Circle) {
            	Circle circle = (Circle) region;
            	Element circleElem = addElement(EarliestArrivalProblemXMLConstants.CIRCLE, obstaclesElem);
            	circleElem.setAttribute("center", pointToString(circle.getCenter()));
            	circleElem.setAttribute("radius", Integer.toString(circle.getRadius()));
            } else {
            	throw new RuntimeException("Unsupported type of region: " + region);
            }
        }

        // boundary
        Element boundaryElem = addElement(EarliestArrivalProblemXMLConstants.BOUNDARY, environmentElem);
        Point[] points = null;
        
        Polygon boundaryPolygon = null;
        
        
        if (problem.getEnvironment().getBoundary() instanceof Rectangle) {
        	boundaryPolygon = ((Rectangle) problem.getEnvironment().getBoundary()).toPolygon();
        } else if (problem.getEnvironment().getBoundary() instanceof Polygon) {
        	boundaryPolygon = ((Polygon) problem.getEnvironment().getBoundary());
        } else  {
        	throw new RuntimeException("This type of boundary is not supported for serialization");
        }
        
    	if (boundaryPolygon.isFilledInside()) {
    		boundaryPolygon = boundaryPolygon.flip();
    	}
    	
    	points = boundaryPolygon.getPoints();
        
        addText(pointArrayToString(points), boundaryElem);

        //graph
        DirectedGraph<Point, Line> graph = problem.getPlanningGraph();
        if (graph != null) {
            Element graphElem = addElement(EarliestArrivalProblemXMLConstants.GRAPH, problemElem);

            Element verticesElem = addElement(EarliestArrivalProblemXMLConstants.VERTICES, graphElem);
            String verticesStr = pointArrayToString(graph.vertexSet().toArray(new Point[graph.vertexSet().size()]));
            verticesElem.appendChild(doc.createTextNode(verticesStr));

            Element edgesElem = addElement(EarliestArrivalProblemXMLConstants.EDGES, graphElem);

            StringBuilder sb = new StringBuilder();
            for (Line line : graph.edgeSet()) {
                sb.append(pointArrayToString(new Point[]{line.getStart(), line.getEnd()}) + ";");
            }
            edgesElem.appendChild(doc.createTextNode(sb.toString()));
        }

        // agents
        Element agentsElem = addElement(EarliestArrivalProblemXMLConstants.AGENTS, problemElem);

        for (int j = 0; j < problem.nAgents(); j++) {
            //agent
            Element agentElem = addElement(EarliestArrivalProblemXMLConstants.AGENT, agentsElem);

            agentElem.setAttribute(EarliestArrivalProblemXMLConstants.START, pointToString(problem.getStart(j)));
            if (problem.getTarget(j) != null)
            	agentElem.setAttribute(EarliestArrivalProblemXMLConstants.TARGET, pointToString(problem.getTarget(j)));
            agentElem.setAttribute(EarliestArrivalProblemXMLConstants.BODY_RADIUS, Integer.toString(problem.getBodyRadius(j)));
            agentElem.setAttribute(EarliestArrivalProblemXMLConstants.MAX_SPEED, String.format("%.4f", problem.getMaxSpeed(j)));
        }
        
        // docks
        if (problem.getDocks() != null) {
        	Element docksElem = addElement(EarliestArrivalProblemXMLConstants.DOCKS, problemElem);
            String docksStr = pointArrayToString(problem.getDocks());
            docksElem.appendChild(doc.createTextNode(docksStr));
        }
        
        // tasks
        if (problem.getRelocationTasks() != null) {
        	Element tasksElem = addElement(EarliestArrivalProblemXMLConstants.TASKS, problemElem);
        	for (RelocationTask task : problem.getRelocationTasks()) {
        		Element taskElem = addElement(EarliestArrivalProblemXMLConstants.TASK, tasksElem);
        		taskElem.setAttribute("time", Integer.toString(task.getIssueTime()));
        		taskElem.setAttribute("agent", Integer.toString(task.getAgentId()));
        		taskElem.setAttribute("goal", pointToString(task.getDestination()));
        	}
        }

        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(stream);

            transformer.transform(source, result);

        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private String pointArrayToString(Point[] pointArray) {
        StringBuilder sb = new StringBuilder();
        for (Point point : pointArray) {
            sb.append(point.x);
            sb.append(",");
            sb.append(point.y);
            sb.append(" ");
        }
        return sb.toString();
    }
    
    private String pointToString(Point point) {
        StringBuilder sb = new StringBuilder();
        sb.append(point.x);
        sb.append(",");
        sb.append(point.y);
        return sb.toString();
    }

    private Element addPointElement(Point point, Element parent) {
        Element pointElem = addElement(EarliestArrivalProblemXMLConstants.POINT, parent);

        Element xElem = addElement(EarliestArrivalProblemXMLConstants.X, pointElem);
        addText(Integer.toString(point.getX()), xElem);

        Element yElem = addElement(EarliestArrivalProblemXMLConstants.Y, pointElem);
        addText(Integer.toString(point.getY()), yElem);

        return pointElem;
    }

    private void addText(String text, Node parent) {
        parent.appendChild(doc.createTextNode(text));
    }

    private Element addElement(String name, Node parent) {
        Element element = doc.createElement(name);
        addText("", element);
        parent.appendChild(element);
        return element;
    }
}
