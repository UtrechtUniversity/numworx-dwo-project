// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\LoggedInPanel.java
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Guest;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.gui.action.LogoutAction;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * This class represents a panel that shows who is logged in and a button to
 * logg of.
 *
 * @author M.J.B. Kupers
 *
 */
public class LoggedInPanel extends Box {

    private JButton logoffButton;

//    final class LoggedInPanelButton extends JButton {
//
//		LoggedInPanelButton(String label) {
//			super(label);
//		}
//
//		/* (non-Javadoc)
//		 * @see javax.swing.JComponent#getMaximumSize()
//		 */
//		public Dimension getMaximumSize() {
//			return new Dimension(LoggedInPanel.this.getWidth()-MARGIN,getPreferredSize().height);
//		}
//	}
    /**
     * Creates a new LoggedInPanel. It shows who is logged in, and a button to
     * log of.
     */
    public LoggedInPanel() {
        super(BoxLayout.Y_AXIS);
        if (GuiConstants.GUI_IMAGE_BG) {
            setOpaque(false);
        } else {
            setBorder(MainPanel.createNBorder());
            setBackground(GuiConstants.MAIN_BACKGROUND);
            setOpaque(true);
        }
        // fill the box
        // label Ingelogd: 
        // label user 
        // button 'logout'
        User user = GuiCreator.instance().getUser();
        boolean gast = user instanceof Guest;
        JLabel loggedin = new JLabel(
                gast
                        ? TextMapper.getText(TextMapper.GUIL_NOT_LOGGED_IN).trim()
                        : TextMapper.getText(TextMapper.GUIL_LOGGED_IN_AS) + ":");
        loggedin.setFont(GuiConstants.NORMAL_TEXT);
        loggedin.setAlignmentX(CENTER_ALIGNMENT);
        JLabel username = null;
        if (!gast) {
            if (GuiConstants.GUI_IMAGE_BG) {
                loggedin.setText(TextMapper.getText(DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getRoleName())+" "+user.getName());
                loggedin.setHorizontalAlignment(SwingConstants.RIGHT);
            } else {
                username = new JLabel(user.getName());
                username.setAlignmentX(RIGHT_ALIGNMENT);
                username.setFont(GuiConstants.NORMAL_TEXT);
            }
        }
        logoffButton = new JButton(new LogoutAction());
        logoffButton.setAlignmentX(CENTER_ALIGNMENT);

        add(Box.createVerticalStrut(4));
        add(loggedin);
        add(Box.createVerticalStrut(2));
        if (username != null) {
            add(Box.createVerticalStrut(2));
            add(username);
        }
        add(Box.createVerticalGlue());
        if (user.canLogout()) {
            add(logoffButton);
            add(Box.createVerticalStrut(4));
        }
    }

    public void setGuiImage(Image image) {
    }

    /**
     * check valid! Bij een setLayout(null) wordt niet meer automatische
     * gevalideerd. Kan weg als DWO een LayoutManager heeft.
     *
     * @see javax.swing.JComponent#paint(java.awt.Graphics)
     * @see java.awt.LayoutManager
     */
    @Override
    public void paint(Graphics g) {
        if (isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        super.paint(g);
    }

    void setLogoutAction(Action action) {
        logoffButton.setAction(action);
    }
}
