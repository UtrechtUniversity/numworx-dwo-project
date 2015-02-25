package fi.dwo.dwojapplet.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class HeaderPanel extends JPanel {

    private JLabel label;
    private JComponent buttonBox;
    private boolean scalable;
    private boolean scale;
    private Font origFont;
    private static final int MARGIN = 40;

    public HeaderPanel(String string) {
        super(null);
        setLayout(new MyBoxLayout(this, BoxLayout.LINE_AXIS));
        label = new JLabel(string.trim());
        add(label);

        Font f = GuiConstants.HEADER_TEXT;
        final boolean ibg = GuiConstants.GUI_IMAGE_BG;
        if (ibg) {
            f = new Font(f.getName(), f.getStyle(), 20);
        }
        label.setFont(f);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        label.setAlignmentY(BOTTOM_ALIGNMENT);
        origFont = f;
        setOpaque(!ibg);
        label.setHorizontalAlignment(ibg ? JLabel.LEFT : JLabel.CENTER);
        label.setVerticalAlignment(ibg ? JLabel.BOTTOM : JLabel.CENTER);
        setBackground(GuiConstants.MAIN_BACKGROUND);
        setForeground(GuiConstants.HEADER_COLOR);
        label.setForeground(getForeground()); // overerf color
        if (!ibg) {
            setBorder(MainPanel.createNBorder());
        } else {
            setBorder(BorderFactory.createEmptyBorder(0, 0, 1, 0));
        }
        setButtonBox(createButtonBox());
        //add(Box.createHorizontalGlue());
    }

    protected JComponent createButtonBox() {
        Box box = Box.createHorizontalBox();
        box.setBorder(BorderFactory.createEmptyBorder(38, 0, 0, 0));
        return box;
//		return null;
    }

    Dimension lastdim = new Dimension();

    private void scale() {
        if (scalable && !lastdim.equals(getSize())) {
            scale = scalable;
            getSize(lastdim);
        }
    }

    public HeaderPanel(String description, boolean b) {
        this(description);
        this.scalable = b;
        setButtonBox(createButtonBox());
        scale();
    }

    /* (non-Javadoc)
     * @see java.awt.Component#resize(java.awt.Dimension)
     */
    @Override
    public void resize(Dimension d) {

        super.resize(d);
        scale();
    }

    /* (non-Javadoc)
     * @see java.awt.Component#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {

        super.resize(width, height);
        scale();
    }

    /* (non-Javadoc)
     * @see java.awt.Component#setBounds(int, int, int, int)
     */
    @Override
    public void setBounds(int x, int y, int width, int height) {

        super.setBounds(x, y, width, height);
        scale();
    }

    /* (non-Javadoc)
     * @see java.awt.Component#setBounds(java.awt.Rectangle)
     */
    @Override
    public void setBounds(Rectangle r) {
        super.setBounds(r);
        scale();
    }


    /* (non-Javadoc)
     * @see java.awt.Component#setSize(java.awt.Dimension)
     */
    @Override
    public void setSize(Dimension d) {

        super.setSize(d);
        scale();
    }

    /* (non-Javadoc)
     * @see java.awt.Component#setSize(int, int)
     */
    @Override
    public void setSize(int width, int height) {

        super.setSize(width, height);
        scale();
    }

    @Override
    public void paint(Graphics g) {
        if (scale) {
            int width = label.getWidth();
            int height = label.getHeight();
//			Insets inset = getInsets();
//			width -= inset.left + inset.right;
//			height -= inset.top + inset.bottom;
            width -= MARGIN;
            Icon icon = label.getIcon();
            if (icon != null) {
                width -= label.getIconTextGap();
                width -= icon.getIconWidth();
            }
            String text = label.getText();
            Font f = origFont;
            while (f.getSize() > 8 && (g.getFontMetrics(f).stringWidth(text) > width || g.getFontMetrics(f).getHeight() > height)) {
                f = new Font(f.getName(), f.getStyle(), f.getSize() - 1);
            }
            setFont(f);
            scale = false;
        }
        super.paint(g);
    }

    /**
     * @return the buttonBox
     */
    JComponent getButtonBox() {
        return buttonBox;
    }

    /**
     * @param box the buttonBox to set
     */
    void setButtonBox(JComponent box) {
        if (buttonBox != null) {
            remove(buttonBox);
        }
        buttonBox = box;
        if (buttonBox != null) {
            add(buttonBox, 1);
            buttonBox.setAlignmentY(BOTTOM_ALIGNMENT);
            buttonBox.setMaximumSize(buttonBox.getPreferredSize());
        }
        invalidate();
    }

    /**
     * @param alignment
     * @see javax.swing.JLabel#setHorizontalAlignment(int)
     */
    public void setHorizontalAlignment(int alignment) {
        label.setHorizontalAlignment(alignment);
    }

    /**
     * @param icon
     * @see javax.swing.JLabel#setIcon(javax.swing.Icon)
     */
    public void setIcon(Icon icon) {
        label.setIcon(icon);
    }

    /**
     * @param iconTextGap
     * @see javax.swing.JLabel#setIconTextGap(int)
     */
    public void setIconTextGap(int iconTextGap) {
        label.setIconTextGap(iconTextGap);
    }

}
