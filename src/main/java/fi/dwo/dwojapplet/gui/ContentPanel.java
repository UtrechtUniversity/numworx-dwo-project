/**
 *
 */
package fi.dwo.dwojapplet.gui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;

import javax.swing.JPanel;

import fi.dwo.dwojapplet.domain.DwoHelper;

/**
 * @author wim
 *
 */
public class ContentPanel extends JPanel {

    /**
     *
     */
    public ContentPanel() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param isDoubleBuffered
     */
    public ContentPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param layout
     */
    public ContentPanel(LayoutManager layout) {
        super(layout);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param layout
     * @param isDoubleBuffered
     */
    public ContentPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void paintComponent(Graphics g) {
        if (GuiConstants.GUI_IMAGE_BG) {
            //Point p = DwoHelper.getComponentLocation(this);
            Image guiImage = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_WELCOME);
            if (guiImage == null) {   // Oops.
                GuiConstants.GUI_IMAGE_BG = false;
                super.paintComponent(g);
                return;
            }

            g.drawImage(guiImage, 0, 0, null);

            int w = getSize().width;
            int h = getSize().height;
            int dw = w - 800;
            int dh = h - 600;
            int rand = 20;
            int strook = 100;

            g.drawImage(guiImage, 0, 600 - rand - strook, 800 - rand - strook, h - rand, 0, 600 - rand - strook, 800 - rand - strook, 600 - rand, null);
            g.drawImage(guiImage, 800 - rand - strook, 0, w - rand, 600 - rand - strook, 800 - rand - strook, 0, 800 - rand, 600 - rand - strook, null);
            g.drawImage(guiImage, 0, h - rand, 800 - rand - strook, h, 0, 600 - rand, 800 - rand - strook, 600, null);
            g.drawImage(guiImage, w - rand, 0, w, 600 - rand - strook, 800 - rand, 0, 800, 600 - rand - strook, null);
            g.drawImage(guiImage, 800 - rand - strook, 600 - rand - strook, w - rand, h - rand, 800 - rand - strook, 600 - rand - strook, 800 - rand, 600 - rand, null);
            g.drawImage(guiImage, 800 - rand - strook, h - rand, w - rand, h, 800 - rand - strook, 600 - rand, 800 - rand, 600, null);
            g.drawImage(guiImage, w - rand, 600 - rand - strook, w, h - rand, 800 - rand, 600 - rand - strook, 800, 600 - rand, null);
            g.drawImage(guiImage, w - rand, h - rand, w, h, 800 - rand, 600 - rand, 800, 600, null);

        } else {
            super.paintComponent(g);
        }
    }

}
