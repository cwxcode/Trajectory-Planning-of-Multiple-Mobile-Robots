package cz.agents.alite.vis.layer.terminal;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.vecmath.Vector3d;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.element.Sprite;
import cz.agents.alite.vis.element.aggregation.SpriteElements;

public class SpriteLayer extends TerminalLayer {

    private final SpriteElements spriteElements;

    SpriteLayer(SpriteElements spriteElements) {
        this.spriteElements = spriteElements;
    }

    @Override
    public void paint(Graphics2D canvas) {
        Dimension dim = Vis.getDrawingDimension();
        for (Sprite sprite : spriteElements.getSprites()) {
            int x1 = Vis.transX(sprite.getPosition().x) - sprite.getImage().getWidth() / 2;
            int y1 = Vis.transY(sprite.getPosition().y) - sprite.getImage().getHeight() / 2;
            int x2 = Vis.transX(sprite.getPosition().x) + sprite.getImage().getWidth() / 2;
            int y2 = Vis.transY(sprite.getPosition().y) + sprite.getImage().getHeight() / 2;
            if (x2 > 0 && x1 < dim.width  && y2 > 0 && y1 < dim.height) {
                Vector3d vec = sprite.getDirection();

                AffineTransform transform = AffineTransform.getTranslateInstance(x1, y1);
                transform.concatenate(AffineTransform.getRotateInstance(vec.x, vec.y,
                        sprite.getImage().getWidth() / 2, sprite.getImage().getHeight() / 2));
                canvas.drawImage(sprite.getImage(), transform, null);
            }
        }
    }

    @Override
    public String getLayerDescription() {
        String description = "Layer shows image spritesO.";
        return buildLayersDescription(description);
    }

    public static SpriteLayer create(SpriteElements spriteElements) {
        return new SpriteLayer(spriteElements);
    }

}
