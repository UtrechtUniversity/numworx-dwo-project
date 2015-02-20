// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\CourseIcon.java
package fi.dwo.dwojapplet.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicButtonUI;

import fi.dwo.dwojapplet.domain.Course;

/**
 * This class is a panel witch shows a icon of the course. If the course has no
 * icon specified, the default FI icon is showed.
 *
 * @author M.J.B. Kupers
 *
 */
public class CourseIcon extends JButton implements CourseIconIF {

    private Course course;

    private Image courseLogo;

    private Color textColor;

    @Override
    public String getUIClassID() {
        return "CourseIconUI";
    }

    @Override
    public void updateUI() {
        setUI(new CourseIconUI());
    }

    /**
     * Creates a new CourseIcon. This indicates an image of the course and the
     * name. It is clickable what generates an ActionEvent.
     *
     * @param course The Course wherefrom the Icon must created.
     */
    public CourseIcon(Course course) {
        super();
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setBorder(null);
        setBorderPainted(false);
        this.setContentAreaFilled(false);
        this.setFocusPainted(false);
        this.course = course;
        courseLogo = course.getCourseLogo();
        MediaTracker tr = new MediaTracker(this);
        tr.addImage(courseLogo, 0);
        try {
            tr.waitForAll();
        } catch (Exception e) {
        }
        ImageIcon icon = new ImageIcon(courseLogo) {
            @Override
            public int getIconHeight() {
                return 60;
            }
        };

        setIcon(icon);
        setVerticalTextPosition(JLabel.BOTTOM);
        setHorizontalTextPosition(JLabel.CENTER);
        setVerticalAlignment(JLabel.TOP);
        setHorizontalAlignment(JLabel.CENTER);
// Font Okay?
        setFont(new Font("SansSerif", Font.PLAIN, 13));
        setText(course.getName().trim());
        setSize(getPreferredSize());
    }

    /**
     * Returns the current Course.
     *
     * @return The current Course.
     * @see fi.dwo.client.gui.CourseIconIF#getCourse()
     */
    @Override
    public Course getCourse() {
        return course;
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#processMouseMotionEvent(java.awt.event.MouseEvent)
     */
    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        if (e.getID() == MouseEvent.MOUSE_ENTERED) {
            setForeground(GuiConstants.RED_COLOR);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else if (e.getID() == MouseEvent.MOUSE_EXITED) {
            setForeground(Color.black);
            setCursor(Cursor.getDefaultCursor());
        }
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(120, 120);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension result = super.getPreferredSize();
        if (result.width < 120) {
            result.width = 120;
        }
//        if(result.height < 120)
//        	result.height = 120;
        return getMinimumSize();
    }

    /**
     * Sets the tooltip of this component.
     *
     * @param toolTip The tooltip to set.
     * @see fi.beans.tooltip.ToolTipIF#setToolTip(java.lang.String)
     */
    public void setToolTip(String toolTip) {
        setToolTipText(toolTip);
    }

    /**
     * Returns the tooltip of this component.
     *
     * @return The tooltip of this component.
     * @see fi.beans.tooltip.ToolTipIF#getToolTip()
     */
    public String getToolTip() {
        return getToolTipText();
    }

    /**
     * Returns this component.
     *
     * @return This component.
     * @see fi.beans.tooltip.ToolTipIF#getComponent()
     */
    public Component getComponent() {
        return this;
    }
}

class CourseIconUI extends BasicButtonUI {

    @Override
    public void paint(Graphics g, JComponent c) {
        // TODO Auto-generated method stub
        super.paint(g, c);
    }

    @Override
    protected void paintText(Graphics g, AbstractButton b, Rectangle textRect,
            String text) {

        g.setColor(b.getForeground());
        g.setFont(b.getFont());
        FontMetrics fm = g.getFontMetrics();
        text = b.getText().trim(); // originele text, anders lange text... (met ellipsis)
        int width = fm.stringWidth(text);
        if (width <= textRect.width) {
// easy case
            int y = textRect.y + fm.getAscent(); // naar baseline.
            int x = textRect.x + (textRect.width - width) / 2; // center...
            g.drawString(text, x, y);
        } else {
// uneasy case
            int y = textRect.y + fm.getAscent();
            int x = textRect.x;
            char[] data = strip(text.toCharArray());
            int offset = 0;
            int length = 0;
            int lastlength = data.length;
            do {
                do {

                    while (length + offset < data.length && !Character.isWhitespace(data[length + offset])) {
                        length++;
                    }
                    width = fm.charsWidth(data, offset, length);
                    if (width > textRect.width) {
                        length = lastlength;
                        break;
                    }
                    lastlength = length;
                    x = textRect.x + (textRect.width - width) / 2;
                    while (length + offset < data.length && Character.isWhitespace(data[length + offset])) {
                        length++;
                    }

                } while (offset + length < data.length);
                g.drawChars(data, offset, length, x, y);
                y += fm.getHeight();
                offset += length;
                while (offset < data.length && Character.isWhitespace(data[offset])) {
                    offset++;
                }
                length = 0;
            } while (offset < data.length);
        }
    }

    private static char[] strip(char[] data) {
        boolean space = false;
        for (int i = 1; i < data.length; i++) {
            boolean sp = Character.isWhitespace(data[i]);
            if (sp && space) {
                char[] ndata = new char[data.length - 1];
                System.arraycopy(data, 0, ndata, 0, i);
                System.arraycopy(data, i + 1, ndata, i, ndata.length - i);
                data = ndata;
                i--;
            }
            space = sp;
        }
        return data;
    }

    public CourseIconUI() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void installUI(JComponent c) {
        installDefaults((AbstractButton) c);
        installListeners((AbstractButton) c);
        installKeyboardActions((AbstractButton) c);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return getPreferredButtonSize((JButton) c, ((JButton) c).getIconTextGap());
    }

    public static Dimension getPreferredButtonSize(AbstractButton b, int textIconGap) {
        if (b.getComponentCount() > 0) {
            return null;
        }

        Icon icon = (Icon) b.getIcon();
        String text = b.getText();

        Font font = b.getFont();
        FontMetrics fm = b.getFontMetrics(font);

        Rectangle iconR = new Rectangle();
        Rectangle textR = new Rectangle();
        Rectangle viewR = new Rectangle(120, 120);

        SwingUtilities.layoutCompoundLabel(
                b, fm, text, icon,
                b.getVerticalAlignment(), b.getHorizontalAlignment(),
                b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
                viewR, iconR, textR, (text == null ? 0 : textIconGap)
        );

        fixTextR(fm, text, textR); // fixit

        /* The preferred size of the button is the size of 
         * the text and icon rectangles plus the buttons insets.
         */
        Rectangle r = iconR.union(textR);

        Insets insets = b.getInsets();
        r.width += insets.left + insets.right;
        r.height += insets.top + insets.bottom;

        return r.getSize();
    }

    private static void fixTextR(FontMetrics fm, String text, Rectangle textRect) {
        int width = fm.stringWidth(text);
        if (width <= textRect.width) {
// easy case
            return;
        } else {
// uneasy case
            int y = textRect.y + fm.getAscent();
            char[] data = strip(text.toCharArray());
            int offset = 0;
            int length = 0;
            int lastlength = data.length;
            do {
                do {

                    while (length + offset < data.length && !Character.isWhitespace(data[length + offset])) {
                        length++;
                    }
                    width = fm.charsWidth(data, offset, length);
                    if (width > textRect.width) {
                        length = lastlength;
                        break;
                    }
                    lastlength = length;
                    while (length + offset < data.length && Character.isWhitespace(data[length + offset])) {
                        length++;
                    }

                } while (offset + length < data.length);
                textRect.height = Math.max(textRect.height, y + fm.getDescent() - textRect.y);
                y += fm.getHeight();
                offset += length;
                while (offset < data.length && Character.isWhitespace(data[offset])) {
                    offset++;
                }
                length = 0;
            } while (offset < data.length);
        }

    }

}
