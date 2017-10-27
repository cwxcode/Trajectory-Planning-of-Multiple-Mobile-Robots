package tt.jointeuclid2ni.probleminstance;

import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.element.aggregation.LineElements;
import cz.agents.alite.vis.element.implemetation.LineImpl;
import cz.agents.alite.vis.layer.common.BackgroundLayer;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;
import cz.agents.alite.vis.layer.terminal.ArrowLayer;
import cz.agents.alite.vis.layer.terminal.ImageLayer;
import cz.agents.alite.vis.layer.toggle.KeyToggleLayer;

import org.jgrapht.Graph;

import tt.discrete.Trajectory;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.probleminstance.Environment;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.vis.RegionsLayer;
import tt.euclid2i.vis.RegionsLayer.RegionsProvider;
import tt.euclidtime3i.sipprrts.DynamicObstacles;
import tt.euclidtime3i.vis.TimeParameter;
import tt.util.AgentColors;
import tt.vis.*;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;


public class VisUtil {
	
	private static boolean SHOW_MISSION_NUMBER_START = true;
	private static boolean SHOW_MISSION_NUMBER_GOAL = false;
	
    private static final int SINGLE_TRAJECTORY_COLOR_SHIFT = 32;
    private static int singleTrajectoryColorCounter = 0;
    
    
    public static void initVisualization(final EarliestArrivalProblem problem, String title, int timeParameterStep) {
    	initVisualization(problem, title, null, timeParameterStep);
    }
    
    public static void initVisualization(final EarliestArrivalProblem problem, String title, File bgImageFile, int timeParameterStep) {
    	initVisualization(problem.getEnvironment(), title, bgImageFile, timeParameterStep);
    	
        if (TimeParameterHolder.time == null) {
            TimeParameterHolder.time = new TimeParameter(timeParameterStep);
            VisManager.registerLayer(ParameterControlLayer.create(TimeParameterHolder.time));
        }
    }

    public static void initVisualization(final Environment env, String title, File bgImageFile, int timeParameterStep) {
    	VisManager.registerLayer(ColorLayer.create(Color.WHITE));
    	
    	
    	BufferedImage img = null;
    	if (bgImageFile != null) {
    		img = ImageLayer.loadImage(bgImageFile);
    		java.awt.Rectangle bounds = new java.awt.Rectangle(img.getWidth(), img.getHeight());
    		VisManager.registerLayer(PictureLayer.create(img, bounds));
    	}
    	
        VisManager.setInitParam(title, 700, 700);
        VisManager.setSceneParam(new VisManager.SceneParams() {

            @Override
            public Point2d getDefaultLookAt() {

                Rectangle bounds = env.getBoundary().getBoundingBox();

                double x = bounds.getCorner1().x
                        + ((bounds.getCorner2().x - bounds.getCorner1().x) / 2);
                double y = bounds.getCorner1().y
                        + ((bounds.getCorner2().y - bounds.getCorner1().y) / 2);

                return new Point2d(x, y);
            }

            @Override
            public double getDefaultZoomFactor() {
                return 0.5;
            }
        });

        VisManager.init();
        
    }
    
    public static void visualizeRelocationTaskCoordinationProblem(final RelocationTaskCoordinationProblem problem) {
    	
    	KeyToggleLayer polygonsToggle = KeyToggleLayer.create("p");
    	
        // boundary
        polygonsToggle.addSubLayer(RegionsLayer.create(
                new RegionsLayer.RegionsProvider() {

                    @Override
                    public Collection<Region> getRegions() {
                        return Collections.singleton(problem.getEnvironment().getBoundary());
                    }

                }, Color.BLACK, Color.GRAY)
        );

        polygonsToggle.addSubLayer(RegionsLayer.create(
                new RegionsLayer.RegionsProvider() {

                    @Override
                    public Collection<Region> getRegions() {
                        return problem.getObstacles();
                    }

                }, Color.BLACK, Color.GRAY));
        
        VisManager.registerLayer(polygonsToggle);
        
        
        if (problem.getPlanningGraph() != null) {
        	VisUtil.visualizeGraph(problem.getPlanningGraph(), null);
        }
        
        // docks 
        final int DOCK_RADIUS = 25;
        KeyToggleLayer docksToggle = KeyToggleLayer.create("d");
        docksToggle.addSubLayer(LabeledCircleLayer.create(new LabeledCircleLayer.LabeledCircleProvider<tt.euclid2i.Point>() {

            @Override
            public Collection<LabeledCircleLayer.LabeledCircle<tt.euclid2i.Point>> getLabeledCircles() {
                LinkedList<LabeledCircleLayer.LabeledCircle<tt.euclid2i.Point>> list = new LinkedList<LabeledCircleLayer.LabeledCircle<tt.euclid2i.Point>>();
                Point[] docks = problem.getDocks();
                if (docks != null) {
	                for (int i = 0; i < docks.length; i++) {
	                    list.add(new LabeledCircleLayer.LabeledCircle<tt.euclid2i.Point>(docks[i], DOCK_RADIUS, "", Color.RED));
	                }
                }

                return list;
            }

        }, new tt.euclid2i.vis.ProjectionTo2d()));
        VisManager.registerLayer(docksToggle);

        // starts
        KeyToggleLayer tasksToggle = KeyToggleLayer.create("m");
        tasksToggle.setEnabled(true);
        
        tasksToggle.addSubLayer(LabeledCircleLayer.create(new LabeledCircleLayer.LabeledCircleProvider<tt.euclid2i.Point>() {

            @Override
            public Collection<LabeledCircleLayer.LabeledCircle<tt.euclid2i.Point>> getLabeledCircles() {
                LinkedList<LabeledCircleLayer.LabeledCircle<tt.euclid2i.Point>> list = new LinkedList<LabeledCircleLayer.LabeledCircle<tt.euclid2i.Point>>();
                Point[] starts = problem.getStarts();
                int[] radiuses = problem.getBodyRadiuses();
                for (int i = 0; i < starts.length; i++) {
                    list.add(new LabeledCircleLayer.LabeledCircle<tt.euclid2i.Point>(starts[i], radiuses[i], "" + i, Color.BLUE));
                }

                return list;
            }

        }, new tt.euclid2i.vis.ProjectionTo2d()));
        
        VisManager.registerLayer(tasksToggle);
        
        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());

    }

    public static void visualizeEarliestArrivalProblem(final EarliestArrivalProblem problem) {
    	
    	KeyToggleLayer polygonsToggle = KeyToggleLayer.create("p");
    	
        // boundary
        polygonsToggle.addSubLayer(RegionsLayer.create(
                new RegionsLayer.RegionsProvider() {

                    @Override
                    public Collection<Region> getRegions() {
                        return Collections.singleton(problem.getEnvironment().getBoundary());
                    }

                }, Color.BLACK, Color.GRAY)
        );

        polygonsToggle.addSubLayer(RegionsLayer.create(
                new RegionsLayer.RegionsProvider() {

                    @Override
                    public Collection<Region> getRegions() {
                        return problem.getObstacles();
                    }

                }, Color.BLACK, Color.GRAY));
        
        VisManager.registerLayer(polygonsToggle);
        
        
        if (problem.getPlanningGraph() != null) {
        	VisUtil.visualizeGraph(problem.getPlanningGraph(), null);
        }
        
        
        KeyToggleLayer missionToggle = KeyToggleLayer.create("m");
        missionToggle.setEnabled(false);
        
        // mission arrows        
		missionToggle.addSubLayer(ArrowLayer.create(new LineElements() {

			@Override
			public Color getColor() {
				return Color.BLUE;
			}

			@Override
			public int getStrokeWidth() {
				return 1;
			}

			@Override
			public Iterable<? extends cz.agents.alite.vis.element.Line> getLines() {
				ArrayList<cz.agents.alite.vis.element.Line> lines = new ArrayList<cz.agents.alite.vis.element.Line>();
				for (int i = 0; i < problem.getStarts().length; i++) {

					Point start = problem.getStart(i);
					Point end = problem.getTarget(i);

					if (start != null && end != null) {
						Point3d startLine = new Point3d(start.x, start.y, 0);
						Point3d endLine = new Point3d(end.x, end.y, 0);

						lines.add(new LineImpl(startLine, endLine));
					}

				}

				return lines;
			}
		}));
        
       // starts
       missionToggle.addSubLayer(LabeledCircleLayer.create(new LabeledCircleLayer.LabeledCircleProvider<tt.euclid2i.Point>() {

            @Override
            public Collection<LabeledCircleLayer.LabeledCircle<tt.euclid2i.Point>> getLabeledCircles() {
                LinkedList<LabeledCircleLayer.LabeledCircle<tt.euclid2i.Point>> list = new LinkedList<LabeledCircleLayer.LabeledCircle<tt.euclid2i.Point>>();

                for (int i = 0; i < problem.getStarts().length; i++) {
                    list.add(new LabeledCircleLayer.LabeledCircle<tt.euclid2i.Point>(problem.getStart(i), 
                    		problem.getBodyRadius(i), "" + (SHOW_MISSION_NUMBER_START ? i : "") , 
                    		Color.BLUE /*AgentColors.getColorForAgent(i)*/,
                    		Color.WHITE,
                    		Color.BLUE));
                }

                return list;
            }

        }, new tt.euclid2i.vis.ProjectionTo2d()));
        
        
        // destinations 
        missionToggle.addSubLayer(LabeledCircleLayer.create(new LabeledCircleLayer.LabeledCircleProvider<tt.euclid2i.Point>() {

            @Override
            public Collection<LabeledCircleLayer.LabeledCircle<tt.euclid2i.Point>> getLabeledCircles() {
                LinkedList<LabeledCircleLayer.LabeledCircle<tt.euclid2i.Point>> list = new LinkedList<LabeledCircleLayer.LabeledCircle<tt.euclid2i.Point>>();

                for (int i = 0; i < problem.getStarts().length; i++) {
                	if (problem.getTarget(i) != null)
                		list.add(new LabeledCircleLayer.LabeledCircle<tt.euclid2i.Point>(problem.getTarget(i), problem.getBodyRadius(i), "" + (SHOW_MISSION_NUMBER_GOAL ? i : ""), Color.BLUE));
                }

                return list;
            }

        }, new tt.euclid2i.vis.ProjectionTo2d()));
        

        

        VisManager.registerLayer(missionToggle);
        
        // docks 
        final int DOCK_RADIUS = 25;
        KeyToggleLayer docksToggle = KeyToggleLayer.create("d");
        docksToggle.addSubLayer(LabeledCircleLayer.create(new LabeledCircleLayer.LabeledCircleProvider<tt.euclid2i.Point>() {

            @Override
            public Collection<LabeledCircleLayer.LabeledCircle<tt.euclid2i.Point>> getLabeledCircles() {
                LinkedList<LabeledCircleLayer.LabeledCircle<tt.euclid2i.Point>> list = new LinkedList<LabeledCircleLayer.LabeledCircle<tt.euclid2i.Point>>();
                Point[] docks = problem.getDocks();
                if (docks != null) {
	                for (int i = 0; i < docks.length; i++) {
	                    list.add(new LabeledCircleLayer.LabeledCircle<tt.euclid2i.Point>(docks[i], DOCK_RADIUS, "", Color.RED));
	                }
                }

                return list;
            }

        }, new tt.euclid2i.vis.ProjectionTo2d()));
        VisManager.registerLayer(docksToggle);
        
        // Mission area
//        VisManager.registerLayer(
//        	KeyToggleLayer.create("r", true, 
//        	RegionsLayer.create(
//                new RegionsLayer.RegionsProvider() {
//
//                    @Override
//                    public Collection<Region> getRegions() {
//                        return Collections.singleton( (Region) new Rectangle(new Point(-150,-150), new Point(1150,1150))  );
//                    }
//
//            }, Color.LIGHT_GRAY, null)
//        ));
        

        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());

    }

    private static void visualizeSingleTrajectory(Trajectory trajectory, int bodyRadius, int maxTime) {
        final Trajectory[] trajInArr = {trajectory};
        final int[] radInArr = {bodyRadius};
        final int color = SINGLE_TRAJECTORY_COLOR_SHIFT + singleTrajectoryColorCounter++;

        VisManager.registerLayer(ColoredTrajectoriesLayer.create(
                new ColoredTrajectoriesLayer.TrajectoriesProvider<Point>() {
                    @Override
                    public tt.discrete.Trajectory<Point>[] getTrajectories() {
                        return trajInArr;
                    }
                }, new ColoredTrajectoriesLayer.ColorProvider() {
                    @Override
                    public Color getColor(int i) {
                        return AgentColors.getColorForAgent(color);
                    }
                }, new tt.euclid2i.vis.ProjectionTo2d(), 5, maxTime, 6, 's'
        ));
        VisManager.registerLayer(FastAgentsLayer.create(
                new FastAgentsLayer.TrajectoriesProvider() {

                    @Override
                    public tt.discrete.Trajectory<Point>[] getTrajectories() {
                        return trajInArr;
                    }

                    @Override
                    public int[] getBodyRadiuses() {
                        return radInArr;
                    }

                }, new FastAgentsLayer.ColorProvider() {
                    @Override
                    public Color getColor(int i) {
                        return AgentColors.getColorForAgent(color);
                    }
                }, TimeParameterHolder.time
        ));
    }

    public static void visualizeDynamicEnvironment(final DynamicObstacles dynamicEnv, int maxTime) {
        VisManager.registerLayer(ColoredTrajectoriesLayer.create(
                new ColoredTrajectoriesLayer.TrajectoriesProvider<Point>() {
                    @Override
                    public tt.discrete.Trajectory<Point>[] getTrajectories() {
                        return dynamicEnv.getObstacles();
                    }
                }, new ColoredTrajectoriesLayer.ColorProvider() {
                    @Override
                    public Color getColor(int i) {
                        return AgentColors.getColorForAgent(i);
                    }
                }, new tt.euclid2i.vis.ProjectionTo2d(), 5, maxTime, 6, 's'
        ));
        VisManager.registerLayer(FastAgentsLayer.create(
                new FastAgentsLayer.TrajectoriesProvider() {

                    @Override
                    public tt.discrete.Trajectory<Point>[] getTrajectories() {
                        return dynamicEnv.getObstacles();
                    }

                    @Override
                    public int[] getBodyRadiuses() {
                        return dynamicEnv.getObstacleRadiuses();
                    }

                }, new FastAgentsLayer.ColorProvider() {
                    @Override
                    public Color getColor(int i) {
                        return AgentColors.getColorForAgent(i);
                    }
                }, TimeParameterHolder.time
        ));
    }

    public static void visualizeGraph(final Graph<Point, Line> graphToVisualize,
                                      final Collection<Region> inflatedObstacles) {
    	
    	Color color = new Color(0.8f, 0.8f, 0.8f);
    	
        KeyToggleLayer toggleLayer = KeyToggleLayer.create("g");
        toggleLayer.setEnabled(false);
        toggleLayer.addSubLayer(
                GraphLayer.create(new GraphLayer.GraphProvider<tt.euclid2i.Point, Line>() {

                    @Override
                    public Graph<tt.euclid2i.Point, Line> getGraph() {
                        return graphToVisualize;
                    }
                }, new tt.euclid2i.vis.ProjectionTo2d(), color, color.darker(), 1, 4)
        );
        
        VisManager.registerLayer(toggleLayer);
        
        VisManager.registerLayer(KeyToggleLayer.create("i", false, 
        		RegionsLayer.create(new RegionsProvider() {

            @Override
            public Collection<? extends Region> getRegions() {
                if (inflatedObstacles == null) {
                    return Collections.EMPTY_LIST;
                } else {
                    return inflatedObstacles;
                }
            }
        }, Color.CYAN)));
        
        

        
    }

}
