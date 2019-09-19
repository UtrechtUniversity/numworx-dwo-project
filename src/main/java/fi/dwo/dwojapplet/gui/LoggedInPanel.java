// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\LoggedInPanel.java
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Guest;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.gui.action.LogoutAction;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import fi.dwo.dwojapplet.gui.numworx.Constants;
import fi.dwo.dwojapplet.gui.numworx.JButton;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicMenuBarUI;
import javax.swing.plaf.basic.BasicMenuItemUI;

/**
 * This class represents a panel that shows who is logged in and a button to
 * logg of.
 *
 * @author M.J.B. Kupers
 *
 */
public class LoggedInPanel extends JPanel {

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
        super(new FlowLayout(FlowLayout.TRAILING,0,10), false);
        setOpaque(false);
        if (GuiConstants.GUI_IMAGE_BG) {
            setOpaque(false);
        } else {
            setBorder(MainPanel.createNBorder());
            setBackground(GuiConstants.MAIN_BACKGROUND);
            setOpaque(true);
        }
        //setBorder(BorderFactory.createLineBorder(Color.red));
        // fill the box
        // label Ingelogd: 
        // label user 
        // button 'logout'
        User user = GuiCreator.instance().getUser();
        boolean gast = user instanceof Guest;
        ImageIcon icon = new ImageIcon(DwoHelper.getResourceImage("resources/account.png"));
        JMenu loggedin = new JMenu(
                gast
                        ? TextMapper.getText(TextMapper.GUIL_NOT_LOGGED_IN).trim()
                        : TextMapper.getText(TextMapper.GUIL_LOGGED_IN_AS) + ":")
            
//            {
//
//              /* (non-Javadoc)
//               * @see javax.swing.JComponent#paint(java.awt.Graphics)
//               */
//              @Override
//              public void paint(Graphics g) {
//                // TODO Auto-generated method stub
//                //
//                super.paint(g);
//              } 
//          
//          
//            }
            ;
        loggedin.setIcon(icon);
        loggedin.setFont(GuiConstants.NORMAL_TEXT);
        loggedin.setHorizontalTextPosition(SwingConstants.LEADING);
        loggedin.setUI(new BasicMenuItemUI() {

          /* (non-Javadoc)
           * @see javax.swing.plaf.basic.BasicMenuItemUI#installDefaults()
           */
          @Override
          protected void installDefaults() {
            // TODO Auto-generated method stub
            super.installDefaults();
            selectionBackground = Constants.colorBlue3;
          }           
          
        } );
        loggedin.setBorderPainted(false);
        loggedin.setContentAreaFilled(false);
        loggedin.setOpaque(false);
        JMenuBar bar = new JMenuBar();
        bar.setUI(new BasicMenuBarUI());
        bar.setBorderPainted(false);
        bar.setDoubleBuffered(false);
        bar.setOpaque(false);
        if (!gast) {
          
          DomSchoolsRolesAndClassesV2 schoolLogins = DwoHelper.getSchoolLogins();
          JLabel school = new JLabel(schoolLogins.getActiveSchoolRoleAndClass().getSchool().getSchoolName());
          JLabel name = new JLabel(TextMapper.getText(schoolLogins.getActiveSchoolRoleAndClass().getRole().getRoleName())+" "+user.getName());
          name.setAlignmentX(1.0f);
          school.setAlignmentX(1.0f);
          loggedin.add(school);
          loggedin.add(name);
          loggedin.addSeparator();
          
        }
        loggedin.add(new LogoutAction());
        bar.add(loggedin);
        JLabel username = null;
        if (!gast) {
            if (GuiConstants.GUI_IMAGE_BG) {
                loggedin.setText(/*TextMapper.getText(DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getRole().getRoleName())+" "+*/user.getName());
                loggedin.setHorizontalAlignment(SwingConstants.RIGHT);
            } else {
                username = new JLabel(user.getName());
                username.setAlignmentX(RIGHT_ALIGNMENT);
                username.setFont(GuiConstants.NORMAL_TEXT);
            }
        }
        logoffButton = new JButton(new LogoutAction());
        logoffButton.setAlignmentX(CENTER_ALIGNMENT);

        //add(Box.createVerticalStrut(4));
        //add(loggedin); 
        add(bar);

//        setOpaque(true);
//        setBackground(Color.GREEN);

        //add(Box.createVerticalStrut(2));
        if (username != null) {
            //add(Box.createVerticalStrut(2));
            //add(username);
        }
        //add(Box.createVerticalGlue());
        if (user.canLogout()) {
            //add(logoffButton);
            //add(Box.createVerticalStrut(4));
        }
    }

    public void setGuiImage(Image image) {
    }

//    /**
//     * check valid! Bij een setLayout(null) wordt niet meer automatische
//     * gevalideerd. Kan weg als DWO een LayoutManager heeft.
//     *
//     * @see javax.swing.JComponent#paint(java.awt.Graphics)
//     * @see java.awt.LayoutManager
//     */
//    @Override
//    public void paint(Graphics g) {
//        if (isOpaque()) {
//            g.setColor(getBackground());
//            g.fillRect(0, 0, getWidth(), getHeight());
//        }
//        super.paint(g);
//    }

    void setLogoutAction(Action action) {
        logoffButton.setAction(action);
    }
}
