package cz.agents.alite.vis.layer;

import cz.agents.alite.vis.Vis;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * DraggableLayer allows to move another layer by mouse over screen. The another layer must implement {@link
 * Draggable} interface.
 * @author Ondrej Hrstka (ondrej.hrstka at agents.fel.cvut.cz)
 *
 * @see Draggable
 * @see cz.agents.alite.vis.layer.terminal.textBackgroundLayer.TextBackgroundLayer
 */
public class DraggableLayer extends AbstractLayer implements MouseListener, MouseMotionListener {

    private final int mouseButton;
    private final Draggable draggable;

    private int lastX, lastY;
    private boolean dragging = false;

    /**
     * Binds DraggableLayer with given {@code draggable}. Returned layer containts both of these layers.
     * @param mouseButton mouse button on which the layer should be dragged
     * @param draggable layer to bind
     * @return composition of the given {@code draggable} and newly created instance of {@link DraggableLayer}
     *
     * @see MouseEvent
     */
    public static GroupLayer create(int mouseButton, Draggable draggable) {
        GroupLayer out = GroupLayer.create();

        out.addSubLayer(draggable);
        out.addSubLayer(new DraggableLayer(mouseButton, draggable));

        return out;
    }

    /**
     * /**
     * Binds DraggableLayer with given {@code draggable}. Returned layer containts both of these layers. The layer
     * reacts on left mouse button.
     * @param draggable layer to bind
     * @return composition of the given {@code draggable} and newly created instance of {@link DraggableLayer}
     *
     * @see MouseEvent
     */
    public static GroupLayer create(Draggable draggable) {
        return create(MouseEvent.BUTTON1, draggable);
    }

    private DraggableLayer(int mouseButton, Draggable draggable) {
        this.mouseButton = mouseButton;
        this.draggable = draggable;
    }

    @Override
    public void init(Vis vis) {
        vis.addMouseListener(this);
        vis.addMouseMotionListener(this);
    }

    @Override
    public void deinit(Vis vis) {
        vis.removeMouseMotionListener(this);
        vis.removeMouseListener(this);

    }

    @Override
    public String getLayerDescription() {
        return "";
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragging && draggable.isActive()) {
            int deltaX = e.getX() - lastX;
            int deltaY = e.getY() - lastY;

            lastX = e.getX();
            lastY = e.getY();

            draggable.moveLocation(deltaX, deltaY);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == mouseButton && draggable.isActive() && e.getClickCount() == 1 &&
                draggable.isInArea(e.getPoint().x, e.getPoint().y)) {
            dragging = true;
            lastX = e.getX();
            lastY = e.getY();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        dragging = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    /**
     * This interface allows for layer to be moved on screen by drag and drop.
     */
    public interface Draggable extends VisLayer {

        /**
         * Method decides if the layer is active and if the dragging is allowed. This method determines if
         * {@link DraggableLayer} will react on mouse click by dragging the layer or not.
         * @return {@code true} if the layer is active and allows dragging, {@code false} otherwise.
         */
        public boolean isActive();

        /**
         * Tests whether given coordinates are in area of interest given by implementation. This method determines if
         * {@link DraggableLayer} will react on mouse click by dragging the layer or not.
         * @param x x-coordinate of mouse click
         * @param y y-coordinate of mouse click
         * @return {@code true} if the layer is allowed to drag from given coordinates, {@code false} otherwise.
         */
        public boolean isInArea(int x, int y);

        /**
         * This method is called during dragging. The parameters represent amount of change in which the layer should
         * be moved
         * @param deltaX delta in x-coordinates
         * @param deltaY delta in y-coordinates
         */
        public void moveLocation(int deltaX, int deltaY);
    }
}
