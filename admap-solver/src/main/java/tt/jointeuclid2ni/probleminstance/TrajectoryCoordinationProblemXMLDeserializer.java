package tt.jointeuclid2ni.probleminstance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.probleminstance.Environment;
import tt.euclid2i.region.Circle;
import tt.euclid2i.region.Polygon;
import tt.euclid2i.region.Rectangle;

public class TrajectoryCoordinationProblemXMLDeserializer {

	private static Logger LOGGER = Logger.getLogger(EarliestArrivalProblem.class);

    private class Agents {

        private int size;
        private List<Point> starts;
        private List<Point> targets;
        private List<Integer> radiuses;
        private List<Float> maxSpeeds;

        private Agents() {
            this.size = 0;
            this.starts = new ArrayList<Point>();
            this.targets = new ArrayList<Point>();
            this.radiuses = new ArrayList<Integer>();
            this.maxSpeeds = new ArrayList<Float>();
        }

        public void add(Point start, Point target, int radius, float maxSpeed) {
            starts.add(start);
            targets.add(target);
            radiuses.add(radius);
            maxSpeeds.add(maxSpeed);
            size++;
        }
    }

    private Document doc;

    public static TrajectoryCoordinationProblemImpl deserialize(InputStream stream) {
        return new TrajectoryCoordinationProblemXMLDeserializer(stream).deserialize();
    }

    private TrajectoryCoordinationProblemXMLDeserializer(InputStream stream) {

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            this.doc = docBuilder.parse(stream);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TrajectoryCoordinationProblemImpl deserialize() {
        Environment environment = parseEnvironment();
        Agents agents = parseAgents();
        DirectedGraph<Point, Line> graph = parseGraph();
        Point[] docks = parseDocks();
        List<RelocationTask> tasks = parseTasks();

        Point[] starts = agents.starts.toArray(new Point[agents.size]);
        Point[] targets = agents.targets.toArray(new Point[agents.size]);
        int[] radiuses = ArrayUtils.toPrimitive(agents.radiuses.toArray(new Integer[agents.size]));
        float[] maxSpeeds = ArrayUtils.toPrimitive(agents.maxSpeeds.toArray(new Float[agents.size]));

        return new TrajectoryCoordinationProblemImpl(environment, starts, targets, radiuses, maxSpeeds, graph, docks, tasks);
    }

    private Point[] parseDocks() {
    	NodeList docksNL = doc.getElementsByTagName(EarliestArrivalProblemXMLConstants.DOCKS);
    	if(docksNL.getLength() == 0) {
    		return null;
    	}
    	
        assert docksNL.getLength() == 1;
        if (docksNL.item(0).getChildNodes().getLength() == 0) {
        	return null;
        }
        
        assert docksNL.item(0).getChildNodes().item(0).getNodeType() == Node.TEXT_NODE;
        String docksStr = docksNL.item(0).getTextContent();
        
        return stringToPointArray(docksStr);
	}
    
    private List<RelocationTask> parseTasks() {
    	
    	LinkedList<RelocationTask> tasks = new LinkedList<RelocationTask>();
    	
    	NodeList tasksNL = doc.getElementsByTagName(EarliestArrivalProblemXMLConstants.TASKS);
    	if(tasksNL.getLength() == 0) {
    		return null;
    	}
    	
    	Element tasksElem = (Element) tasksNL.item(0);

    	NodeList taskNL = tasksElem.getElementsByTagName(EarliestArrivalProblemXMLConstants.TASK);
    	
    	for (int i=0; i < taskNL.getLength(); i++) {
    		Element elem = (Element) taskNL.item(i);
    		assert elem.getNodeName().equals(EarliestArrivalProblemXMLConstants.TASK);
    		
    		int time = Integer.parseInt(elem.getAttribute("time"));
    		int agentId = Integer.parseInt(elem.getAttribute("agent"));
    		Point goal = stringToPoint(elem.getAttribute("goal"));
    		
    		tasks.add(new RelocationTaskImpl(time, agentId, goal));
    	} 	
    	
    	return tasks;
    	
	}

	private DirectedGraph<Point, Line> parseGraph() {
        long startTime = System.currentTimeMillis();
        DirectedWeightedMultigraph<Point, Line> graph = null;

        NodeList graphNL = doc.getElementsByTagName(EarliestArrivalProblemXMLConstants.GRAPH);

        if (graphNL.getLength() == 1) {

            graph = new DirectedWeightedMultigraph<Point, Line>(
                    new EdgeFactory<Point, Line>() {

                        @Override
                        public Line createEdge(Point sourceVertex,
                                               Point targetVertex) {
                            return new Line(sourceVertex, targetVertex);
                        }
                    }) {
						private static final long serialVersionUID = 1L;

						@Override
						public double getEdgeWeight(Line line) {
							return line.getDistance();
						}

            };

            NodeList verticesNL = doc.getElementsByTagName(EarliestArrivalProblemXMLConstants.VERTICES);
            assert verticesNL.getLength() == 1;
            NodeList nl = verticesNL.item(0).getChildNodes();
            assert nl.getLength() == 1 && nl.item(0).getNodeType() == Node.TEXT_NODE;
            String verticesListStr = ((Text) nl.item(0)).getData();

            Point[] vertices = stringToPointArray(verticesListStr);
            for (Point vertex : vertices) {
                graph.addVertex(vertex);
            }

            NodeList edgesNL = doc.getElementsByTagName(EarliestArrivalProblemXMLConstants.EDGES);
            assert edgesNL.getLength() == 1;
            assert edgesNL.item(0).getChildNodes().item(0).getNodeType() == Node.TEXT_NODE;
            String edgesListStr = edgesNL.item(0).getTextContent();

            String[] edgesStr = edgesListStr.split(";");
            for (String edgeStr : edgesStr) {
                if (!edgeStr.isEmpty()) {
                    String[] pointsStr = edgeStr.split(" ");

                    Point start = stringToPoint(pointsStr[0]);
                    Point end = stringToPoint(pointsStr[1]);

                    Line edge = graph.addEdge(start, end);
                }
            }

            LOGGER.debug("Graph (|V|=" + graph.vertexSet().size() + ", |E|=" + graph.edgeSet().size() + ") parsing took " + (System.currentTimeMillis() - startTime) + " ms");

            return graph;
        } else {
            return null;
        }
    }

    private Point[] stringToPointArray(String pointListStr) {

        String[] pointsStrArray = pointListStr.split(" ");
        LinkedList<Point> points = new LinkedList<Point>();
        for (String pointStr : pointsStrArray) {
            if (!pointStr.isEmpty()) {
                Point point = stringToPoint(pointStr);
                points.add(point);
            }
        }
        return points.toArray(new Point[points.size()]);
    }

    private Point stringToPoint(String verticeStr) {
        String[] xyStr = verticeStr.split(",");
        Point point = new Point(Integer.parseInt(xyStr[0]), Integer.parseInt(xyStr[1]));
        return point;
    }

    private Agents parseAgents() {
        Agents agents = new Agents();
        NodeList agentElements = doc.getElementsByTagName(EarliestArrivalProblemXMLConstants.AGENT);

        for (int i = 0; i < agentElements.getLength(); i++) {
            Element agent = (Element) agentElements.item(i);

            Point start = null;
            Point target = null;
            int radius;
            float maxSpeed;
            
            if (agent.hasAttribute(EarliestArrivalProblemXMLConstants.START)) {
            	// use simple syntax
            	start = stringToPoint(agent.getAttribute(EarliestArrivalProblemXMLConstants.START));
            	if (agent.hasAttribute(EarliestArrivalProblemXMLConstants.TARGET))
            		target = stringToPoint(agent.getAttribute(EarliestArrivalProblemXMLConstants.TARGET));
            	radius = Integer.parseInt(agent.getAttribute(EarliestArrivalProblemXMLConstants.BODY_RADIUS));
            	maxSpeed = Float.parseFloat(agent.getAttribute(EarliestArrivalProblemXMLConstants.MAX_SPEED));
            } else {
            	// use verbose syntax
                NodeList startNL = agent.getElementsByTagName(EarliestArrivalProblemXMLConstants.START);
                try {
                    start = parsePoint((Element) startNL.item(0));
                } catch (Exception ex) {
                    throw new RuntimeException("Error while parsing start" + formatOutput(agent));
                }

                NodeList targetNL = agent.getElementsByTagName(EarliestArrivalProblemXMLConstants.TARGET);
                try {
                    target = parsePoint((Element) targetNL.item(0));
                } catch (Exception ex) {
                    throw new RuntimeException("Error while parsing target" + formatOutput(agent));
                }

                NodeList radiusNL = agent.getElementsByTagName(EarliestArrivalProblemXMLConstants.BODY_RADIUS);
                try {
                    radius = Integer.parseInt(radiusNL.item(0).getTextContent());
                } catch (Exception ex) {
                    throw new RuntimeException("Error while parsing radius" + formatOutput(agent));
                }

                NodeList maxSpeedNL = agent.getElementsByTagName(EarliestArrivalProblemXMLConstants.MAX_SPEED);
                try {
                    maxSpeed = Float.parseFloat(maxSpeedNL.item(0).getTextContent());
                } catch (Exception ex) {
                    System.err.printf("Error while parsing maxSpeed for %d. agent - value set do default (1)%n", i);
                    maxSpeed = 1;
                }
            }

            agents.add(start, target, radius, maxSpeed);
        }

        return agents;
    }

    private Environment parseEnvironment() {

    	List<Region> obstacles = null; 
    	NodeList obstacleNL = doc.getElementsByTagName(EarliestArrivalProblemXMLConstants.OBSTACLES);
    	if (obstacleNL.getLength() == 1) {
    		NodeList obstacleChildren = obstacleNL.item(0).getChildNodes();
    		obstacles = parseObstacles(obstacleChildren);
    	}
    	
        Rectangle bounds = parseBounds();
        Region boundary = parseBoundary();

        if (boundary == null) {
            if (bounds != null) {
                boundary = bounds.toOutFilledPolygon();
            } else {
                boundary = computeBoundsAroundObstacles(obstacles);
            }
        }
        
        // Compute statistics
        
        int obstacleCount = 0;
        int cornerCount = 0;
        for (Region obstacle : obstacles) {
        	obstacleCount++;
        	if (obstacle instanceof Polygon) {
        		cornerCount += ((Polygon) obstacle).getPoints().length;
        	}
        }
        
        if (boundary instanceof Polygon) {
        	cornerCount += ((Polygon) boundary).getPoints().length; 
        }
        
        LOGGER.debug("The environment consist of boundary and " + obstacleCount + " obstacles, with total " + cornerCount + " corners.");

        final Region boundaryRegionFinal = boundary;

        final List<Region> obstaclesFinal = obstacles;
        return new Environment() {
            @Override
            public Collection<Region> getObstacles() {
                return obstaclesFinal;
            }

            @Override
            public Region getBoundary() {
                return boundaryRegionFinal;
            }
        };
    }

    private Rectangle computeBoundsAroundObstacles(List<Region> obstacles) {
        int minx = Integer.MAX_VALUE;
        int miny = Integer.MAX_VALUE;
        int maxx = Integer.MIN_VALUE;
        int maxy = Integer.MIN_VALUE;
        for (Region obstacle : obstacles) {
            Rectangle bbox = obstacle.getBoundingBox();
            minx = Math.min(minx, bbox.getCorner1().x);
            miny = Math.min(miny, bbox.getCorner1().y);
            maxx = Math.max(maxx, bbox.getCorner2().x);
            maxy = Math.max(maxy, bbox.getCorner2().y);
        }

        return new Rectangle(new Point(minx, miny), new Point(maxx, maxy));
    }

    private Polygon parseBoundary() {
        Element boundaryElement = (Element) doc.getElementsByTagName(EarliestArrivalProblemXMLConstants.BOUNDARY).item(0);
        if (boundaryElement != null) {
            try {
                Polygon boundaryPolygon = new Polygon(stringToPointArray(boundaryElement.getTextContent()));
                return boundaryPolygon;
            } catch (Exception ex) {
                throw new RuntimeException("Error while parsing" + formatOutput(boundaryElement));
            }
        } else {
            return null;
        }
    }

    private Rectangle parseBounds() {
        Element boundsElement = (Element) doc.getElementsByTagName(EarliestArrivalProblemXMLConstants.BOUNDS).item(0);
        if (boundsElement != null) {
            NodeList cornerElements = boundsElement.getElementsByTagName(EarliestArrivalProblemXMLConstants.POINT);

            try {
                Point corner1 = parsePoint((Element) cornerElements.item(0));
                Point corner2 = parsePoint((Element) cornerElements.item(1));

                return new Rectangle(corner1, corner2);
            } catch (Exception ex) {
                throw new RuntimeException("Error while parsing" + formatOutput(boundsElement));
            }
        } else {
            return null;
        }
    }

    private List<Region> parseObstacles(NodeList obstacleElements) {
        final List<Region> obstacles = new ArrayList<Region>();

        for (int i = 0; i < obstacleElements.getLength(); i++) {
        	Region region = null;
            if (obstacleElements.item(i).getNodeName().equals("obstacle")) {
            	region = parseObstacle((Element) obstacleElements.item(i));
            } else if (obstacleElements.item(i).getNodeName().equals("circle")) {
            	region = parseCircle((Element) obstacleElements.item(i));
            } 
            if (region != null) {
            	obstacles.add(region);
            }
        }

        return obstacles;
    }

    private Polygon parseObstacle(Element obstacle) {
        try {

            boolean forceFilledInside = false;
            if (obstacle.hasAttribute("filledinside")) {
                forceFilledInside = true;
            }

            NodeList pointElements = obstacle.getElementsByTagName(EarliestArrivalProblemXMLConstants.POINT);

            Point[] pointsArray;
            if (pointElements.getLength() == 0) {
                // Polygon is defined using the simple syntax
                String text = obstacle.getTextContent();
                pointsArray = stringToPointArray(text);
            } else {
                // Polygon is defined using the verbose syntax
                List<Point> points = new ArrayList<Point>();

                for (int i = 0; i < pointElements.getLength(); i++) {
                    Node pointElement = pointElements.item(i);
                    points.add(parsePoint((Element) pointElement));
                }

                pointsArray = points.toArray(new Point[points.size()]);
            }

            if (forceFilledInside) {
                if (!Polygon.isClockwise(pointsArray)) {
                    ArrayUtils.reverse(pointsArray);
                }
            }

            return new Polygon(pointsArray);

        } catch (Exception ex) {
            throw new RuntimeException("Error while parsing" + formatOutput(obstacle));
        }
    }
    
    private Region parseCircle(Element obstacle) {
        	
        	Point center = stringToPoint(obstacle.getAttribute("center"));
        	int radius = Integer.parseInt(obstacle.getAttribute("radius"));
        	return new Circle(center, radius);
    }

    private Point parsePoint(Element pointElement) {
        NodeList xElement = pointElement.getElementsByTagName(EarliestArrivalProblemXMLConstants.X);
        NodeList yElement = pointElement.getElementsByTagName(EarliestArrivalProblemXMLConstants.Y);

        int x = Integer.valueOf(xElement.item(0).getTextContent());
        int y = Integer.valueOf(yElement.item(0).getTextContent());

        return new Point(x, y);
    }

    public String formatOutput(Node node) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(node);

            transformer.transform(source, result);
            return result.getWriter().toString();
        } catch (TransformerException e) {

            e.printStackTrace();
            return "";
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        TrajectoryCoordinationProblem deserialize = deserialize(new FileInputStream(new File("src/main/resources/problems/dejvice.xml")));
        TrajectoryCoordinationProblemXMLSerializer.serialize(deserialize, new FileOutputStream(new File("src/main/resources/problems/dejvice_tst.xml")));
    }
}
