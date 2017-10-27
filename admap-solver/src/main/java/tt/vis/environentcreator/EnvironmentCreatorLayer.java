package tt.vis.environentcreator;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.layer.GroupLayer;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.vis.RegionsLayer;

import javax.vecmath.Point2d;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;

public class EnvironmentCreatorLayer extends GroupLayer {

    private static final char SAVE_POLYGON = ' ';   //space
    private static final char SAVE_LIST = 's';
    private static final char REMOVE_POLYGON = 8;   //backspace

    private PolygonCreator polygonCreator;

    public static EnvironmentCreatorLayer create() {
        return new EnvironmentCreatorLayer();
    }

    public EnvironmentCreatorLayer() {
        this.polygonCreator = new PolygonCreator();
        initialize();
    }


    private void handleKey(KeyEvent e) {
        //System.out.println((int) e.getKeyChar());

        switch (e.getKeyChar()) {
            case SAVE_POLYGON:
                polygonCreator.savePolygon();
                break;

            case REMOVE_POLYGON:
                polygonCreator.clearLast();
                break;

            case SAVE_LIST:
                polygonCreator.savePolygon();
                polygonCreator.saveList();
                break;
        }
    }

    private void handleMouse(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            addPointToPolygon();
        }
    }

    private void addPointToPolygon() {
        Point2d cursor = Vis.getCursorPosition();
        int x = (int) Vis.transInvX((int) cursor.x);
        int y = (int) Vis.transInvY((int) cursor.y);
        polygonCreator.addPoint(new Point(x, y));
    }

    private void initialize() {
        addSubLayer(RegionsLayer.create(new RegionsLayer.RegionsProvider() {
            @Override
            public Collection<? extends Region> getRegions() {
                return polygonCreator.getPolygons();
            }
        }, Color.black, Color.gray));
        addSubLayer(RegionsLayer.create(new RegionsLayer.RegionsProvider() {
            @Override
            public Collection<? extends Region> getRegions() {
                return polygonCreator.getCurrent();
            }
        }, Color.black, transparent(Color.red, 128)));

        initializeListeners();
    }

    private void initializeListeners() {
        Vis vis = Vis.getInstance();

        vis.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouse(e);
            }
        });
        vis.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                handleKey(e);
            }
        });
    }

    private static Color transparent(Color c, int a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
    }
}
