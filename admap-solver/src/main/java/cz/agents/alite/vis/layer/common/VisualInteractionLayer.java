package cz.agents.alite.vis.layer.common;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.layer.GroupLayer;
import cz.agents.alite.vis.layer.VisLayer;

public class VisualInteractionLayer extends CommonLayer {

    private VisualInteractionProvidingEntity entity;
    private MouseInputAdapter mouseListener;

    protected VisualInteractionLayer(VisualInteractionProvidingEntity entity) {
        this.entity = entity;
    }

    @Override
    public void init(Vis vis) {
        super.init(vis);

        mouseListener = new MouseInputAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                double x = Vis.transInvX(e.getX());
                double y = Vis.transInvY(e.getY());
                entity.interactVisually(x, y, e);
            }

        };
        vis.addMouseListener(mouseListener);
    }

    @Override
    public void deinit(Vis vis) {
        super.deinit(vis);

        vis.removeMouseListener(mouseListener);
    }

    public static VisLayer create(final VisualInteractionProvidingEntity entity) {
        GroupLayer group = GroupLayer.create();

        // interaction
        group.addSubLayer(new VisualInteractionLayer(entity));

        return group;
    }

    public static interface VisualInteractionProvidingEntity {

        public String getName();
        public void interactVisually(double x, double y, MouseEvent e);
    }

}
