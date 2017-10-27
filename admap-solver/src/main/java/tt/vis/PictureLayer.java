package tt.vis;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.element.Image;
import cz.agents.alite.vis.element.aggregation.ImageElements;
import cz.agents.alite.vis.element.implemetation.ImageImpl;
import cz.agents.alite.vis.layer.GroupLayer;
import cz.agents.alite.vis.layer.VisLayer;
import cz.agents.alite.vis.layer.terminal.ImageLayer;
import cz.agents.alite.vis.layer.toggle.KeyToggleLayer;
import cz.agents.alite.vis.layer.toggle.LodToggleLayer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

public class PictureLayer extends ImageLayer {

    Rectangle bounds;

    protected PictureLayer(final BufferedImage image) {
        this(image, new Rectangle(0, 0, 1000, 1000));
    }

    protected PictureLayer(final BufferedImage image, Rectangle bounds) {
        super(new ImageElements() {

            @Override
            public Iterable<? extends Image> getImages() {
                return Arrays.asList(new ImageImpl(image));
            }

        });
        this.bounds = bounds;
    }

    @Override
    public void paint(Graphics2D canvas) {
        for (Image image : getImageElements().getImages()) {
            canvas.drawImage(image.getImage(), Vis.transX(bounds.getX()), Vis.transY(bounds.getY()), Vis.transW(bounds.getWidth()), Vis.transH(bounds.getHeight()), null);
        }
    }

    @Override
    public String getLayerDescription() {
        String description = "Layer contains background image.";
        return buildLayersDescription(description);
    }

    public static VisLayer create(BufferedImage image, Rectangle bounds, String toggleKey) {
        VisLayer background = new PictureLayer(image, bounds);
        background.setHelpOverrideString("[Blockades] The layer shows ground blockades in form of a bitmap (black = blockade).");

        KeyToggleLayer toggle = KeyToggleLayer.create(toggleKey);
        toggle.addSubLayer(background);

        return toggle;
    }

    public static VisLayer create(File imageFile, Rectangle bounds, String toggleKey) {
        return create(loadImage(imageFile), bounds, toggleKey);
    }

    public static VisLayer create(File detailedImage, File image, double zoomThreshold) {
        LodToggleLayer toggleDetailed = LodToggleLayer.create(Double.POSITIVE_INFINITY, zoomThreshold);
        toggleDetailed.addSubLayer(new PictureLayer(loadImage(detailedImage)));

        LodToggleLayer toggle = LodToggleLayer.create(zoomThreshold, 0);
        toggle.addSubLayer(new PictureLayer(loadImage(image)));

        GroupLayer group = GroupLayer.create();
        group.setHelpOverrideString("[Background] The layer shows informational image of the environment.");
        group.addSubLayer(toggleDetailed);
        group.addSubLayer(toggle);

        return group;
    }

    public static VisLayer create(BufferedImage image) {
        return new PictureLayer(image);
    }

    public static VisLayer create(BufferedImage image, Rectangle bounds) {
        return new PictureLayer(image,bounds);
    }

    public static VisLayer create(File file) {
        return create(loadImage(file));
    }

}
