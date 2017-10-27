package cz.agents.alite.vis;

import cz.agents.alite.vis.VisManager.SceneParams;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Point2d;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;

/**
 * Vis is a singleton holding the visualization window and the drawing canvas.
 *
 * Additionally, it also provides panning and zooming functionality for all visual elements drawn
 * using the transformation methods (transX(), transY(), transW(), transH()).
 *
 * The Vis singleton do not need to be explicitly initialized. The static calls do the
 * initialization automatically.
 *
 * @author Antonin Komneda
 */
public class Vis extends Canvas {

    private static final long serialVersionUID = 1093434407555503398L;

    private static String initTitle;
    private static int initCanvasWidth;
    private static int initCanvasHeight;
    private static Point2d initLookAt;
    private static double initZoomFactor;

    private static volatile Vis instance = null;

    // TODO: refactor - aggr
    private static double zoomFactor = 1.0;
    private static final Point2d offset = new Point2d(0, 0);
    private static final Point2d lastOffset = new Point2d(0, 0);
    private static boolean panning = false;
    private static Rectangle worldBounds;
    private static double zoomFactorBack = 1.0;
    private static final Point2d offsetBack = new Point2d(0, 0);
    
    private static boolean invertYAxis = false;

    private JFrame window;

    private boolean reinitializeBuffers = true;
    private BufferStrategy strategy;
    private Graphics2D graphics;

    private static Dimension size;

    private Vis() {
        super();

        double minZoomFactor = getMinimalZoomFactor(initCanvasWidth, initCanvasHeight);
        zoomFactorBack = zoomFactor = Math.max(initZoomFactor, minZoomFactor);


        // canvas
        setBounds(0, 0, initCanvasWidth, initCanvasHeight);
        size = new Dimension(initCanvasWidth, initCanvasHeight);
        window = new JFrame(initTitle);

        final JPanel panel = (JPanel) window.getContentPane();
        panel.setBounds(0, 0, initCanvasWidth, initCanvasHeight);
        panel.add(this);

        lookAt(initLookAt.x, initLookAt.y, zoomFactor);

        window.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent evt) {
                System.exit(0);
            }

        });
        window.addComponentListener(new ComponentListener() {

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentResized(ComponentEvent e) {
                reinitializeBuffers = true;
                refreshSize();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }

        });

        window.pack();

        // listeners
        addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
                final double zoomStep = 1.1;

                int rotation = mouseWheelEvent.getWheelRotation() * mouseWheelEvent.getScrollAmount();
                if (rotation < 0) {
                    offset.x -= transInvX(mouseWheelEvent.getX()) * zoomFactor  * (zoomStep - 1.0);
                    offset.y -= (invertYAxis ? (-1) : 1) * transInvY(mouseWheelEvent.getY()) * zoomFactor  * (zoomStep - 1.0);

                    zoomFactor *= zoomStep;
                } else {
                    
                	zoomFactor /= zoomStep;

                    offset.x += transInvX(getWidth() / 2) * zoomFactor * (zoomStep - 1.0);
                    offset.y += (invertYAxis ? (-1) : 1) * transInvY(getHeight() / 2) * zoomFactor * (zoomStep - 1.0);
                }

                limitTransformation();
            }

        });
        addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    panning = false;
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                    panning = true;
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
            }

        });
        addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
                if (panning) {
                    offset.x -= lastOffset.x - e.getX();
                    offset.y -= lastOffset.y - e.getY();

                    limitTransformation();
                }

                lastOffset.x = e.getX();
                lastOffset.y = e.getY();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                lastOffset.x = e.getX();
                lastOffset.y = e.getY();
            }

        });
        addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_HOME) {
                	lookAt(initLookAt.x, initLookAt.y, initZoomFactor);
                }
            }
        });

        // buffers
        reinitializeBuffers();
    }

	private void lookAt(double worldX, double worldY, double zoom) {
		zoomFactorBack = zoomFactor = Math.max(initZoomFactor, getMinimalZoomFactor(initCanvasWidth, initCanvasHeight));
		int offsetX = (int) ((transInvW(initCanvasWidth)/2.0 - worldX));
        int offsetY = (int) ((transInvH(initCanvasHeight)/2.0 - (invertYAxis ? (-1) : 1) * worldY));
        
        setPosition(offsetX, offsetY, zoom);
	}

    /**
     * sets initial parameters of the window, call this before creating the window
     */

    public static void setInitParam(String title, int canvasWidth, int canvasHeight) {
        initTitle = title;
        initCanvasWidth = canvasWidth;
        initCanvasHeight = canvasHeight;
        setSceneParams(new SceneParams());
    }

    public static void setSceneParams(SceneParams sceneParams) {
        worldBounds = sceneParams.getWorldBounds();
        initZoomFactor = sceneParams.getDefaultZoomFactor();
        initLookAt = sceneParams.getDefaultLookAt();
    }

    private boolean reinitializeBuffers() {
        if (reinitializeBuffers) {
            reinitializeBuffers = false;

            createBufferStrategy(2);
            strategy = getBufferStrategy();

            graphics = (Graphics2D) strategy.getDrawGraphics();
            graphics.setColor(Color.WHITE);
            graphics.setBackground(Color.BLACK);

            return true;
        }

        return false;
    }

    public static Vis getInstance() {
        if (instance == null) {
            synchronized (Vis.class) {
                if (instance == null) {
                    instance = new Vis();

                    // show window
                    instance.window.setVisible(true);
                    instance.window.requestFocus();
                    instance.requestFocus();
                }
            }
        }

        return instance;
    }

    public static Graphics2D getCanvas() {
        return getInstance().graphics;
    }

    public static void flip() {
        getInstance().strategy.show();

        if (getInstance().reinitializeBuffers()) {
            refreshSize();
            limitTransformation();
        }

        zoomFactorBack = zoomFactor;
        offsetBack.set(offset);
    }

    public static int transX(double x) {
        return (int) (offsetBack.x + x * zoomFactorBack);
    }

    public static int transY(double y) {
        return ((int)  (offsetBack.y + ((invertYAxis ? (-1) : 1) * y) * zoomFactorBack));
    }

    public static int transW(double w) {
        return (int) (w * zoomFactorBack);
    }

    public static int transH(double h) {
        return (int) (h * zoomFactorBack);
    }

    public static double transInvX(int x) {
        return (x - offsetBack.x) / zoomFactorBack;
    }

    public static double transInvY(int y) {
        return (invertYAxis ? (-1) : 1) * ((y - offsetBack.y) / zoomFactorBack);
    }

    public static double transInvW(int w) {
        return w / zoomFactorBack;
    }

    public static double transInvH(int h) {
        return h / zoomFactorBack;
    }

    public static int getWorldDimX() {
        return (int) Vis.size.getWidth();
    }

    public static int getWorldDimY() {
        return (int) Vis.size.getHeight();
    }

    public static Rectangle getWorldBounds() {
        return worldBounds;
    }

    /**
     * @deprecated
     */
    public static int getWorldSizeX() {
        return worldBounds.width;
    }

    /**
     * @deprecated
     */
    public static int getWorldSizeY() {
        return worldBounds.height;
    }

    public static double getZoomFactor() {
        return zoomFactorBack;
    }

    public static Point2d getOffset() {
        return offsetBack;
    }

    public static Point2d getCursorPosition() {
        return lastOffset;
    }

    public static Dimension getDrawingDimension() {
        return size;
    }

    public static void refreshSize() {
        size = getInstance().window.getContentPane().getSize();
    }

    public static Rectangle getWindowBounds() {
        return getInstance().window.getBounds();
    }

    public static void setWindowBounds(Rectangle rect) {
        getInstance().window.setBounds(rect);
    }

    public static void setWindowTitle(String title) {
        getInstance().window.setTitle(title);
    }

    public static void initWithBounds(Rectangle rect) {
        if (instance == null) {
            instance = new Vis();

            // show window
            getInstance().window.setBounds(rect);
            instance.window.setVisible(true);
            instance.requestFocus();
        }
    }

    public static void limitTransformation() {
        int windowWidth = size.width;
        int windowHeight = size.height;

        if (windowWidth > windowHeight) {
            if (zoomFactor < (double) windowWidth / worldBounds.width) {
                zoomFactor = (double) windowWidth / worldBounds.width;
            }
        } else {
            if (zoomFactor < (double) windowHeight / worldBounds.height) {
                zoomFactor = (double) windowHeight / worldBounds.height;
            }
        }

        if (offset.x/zoomFactor + worldBounds.x > 0) {
            offset.x = -(worldBounds.x) * zoomFactor;
        }

        if (offset.y/zoomFactor + worldBounds.y > 0) {
            offset.y = -(worldBounds.y) * zoomFactor;
        }

        if (worldBounds.x + offset.x/zoomFactor + worldBounds.width < windowWidth / zoomFactor) {
            offset.x = (windowWidth / zoomFactor - worldBounds.x - worldBounds.width)*zoomFactor;
        }

        if (worldBounds.y + offset.y/zoomFactor + worldBounds.height < windowHeight / zoomFactor) {
            offset.y = (windowHeight/zoomFactor - worldBounds.y - worldBounds.height)*zoomFactor;
        }
    }

    private double getMinimalZoomFactor(int windowWidth, int windowHeight) {
        if (windowWidth > windowHeight) {
            return (double) windowWidth / worldBounds.width;
        } else {
            return (double) windowHeight / worldBounds.height;
        }
    }

    public static void setPosition(double offsetX, double offsetY, double zoom) {
        offset.set(offsetX * zoom, offsetY * zoom);
        zoomFactor = zoom;
    }
    
    public static void setInvertYAxis(boolean enabled) {
    	invertYAxis = enabled;
    }

    /**
     * @deprecated use setSceneParams instead
     */
    public static void setPanningBounds(Rectangle bounds) {
        worldBounds = bounds;
    }

    public static String getTitle() {
        return initTitle;
    }

}
