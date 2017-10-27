package tt.vis.problemcreator.hid;

import cz.agents.alite.vis.Vis;
import tt.euclid2i.Point;

import javax.vecmath.Point2d;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class HIDAdapter implements KeyListener, MouseListener {

    private HIDListener listener;

    public HIDAdapter(HIDListener listener) {
        this.listener = listener;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        switch (keyCode) {
            case KeyEvent.VK_A: //A - new agent
                listener.createNewAgent();
                break;
                
            case KeyEvent.VK_D: //D - new dock
                listener.createNewDock();
                break;

            case KeyEvent.VK_O: //O - new obstacle
                listener.createNewObject();
                break;

            case KeyEvent.VK_S: //S - save problem
                listener.constructAndSaveProblem();
                break;

            case KeyEvent.VK_BACK_SPACE: //BACKSPACE - erease current state || remove last state from list of patches
                listener.clear();
                break;

            case KeyEvent.VK_SPACE: //SPACE - save current space
                listener.save();
                break;

            default:
                listener.keyTyped(keyCode);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point2d cursor = Vis.getCursorPosition();

        int x = (int) Vis.transInvX((int) cursor.x);
        int y = (int) Vis.transInvY((int) cursor.y);

        listener.mouseClicked(new Point(x, y), e.getButton());
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
