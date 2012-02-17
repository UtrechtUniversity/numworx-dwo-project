// Source file: C:\\parameters\\fi\\dwo\\client\\gui\\ScoNameDialog.java

package fi.dwo.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import fi.dwo.client.domain.AppletConfig;
import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.system.TextMapper;

/**
 * This is a dialog for editing the SCO name and description.
 * 
 * @author M.J.B. Kupers
 *
 */
public class ScoNameDialog //extends Dialog implements ActionListener,
       // WindowListener
        {

//    private String scoName;
//
//    private String scoDescription;
//
//    private boolean confirmed;
//
//    private TextField name;
//
//    private TextArea description;
//
//    private JButton okButton;
//
//    private JButton cancelButton;
//
//    private ScoNameDialog(Component owner, String windowTitle, String scoName,
//            String scoDescription) {
//        super(DwoHelper.getFrameForComponent(owner),
//                windowTitle, true);
//        this.setLayout(new FlowLayout());
//        Panel contentPane = new Panel(null);
//        add(contentPane);
//        this.setBackground(GuiConstants.MAIN_BACKGROUND);
//        this.scoName = scoName;
//        this.scoDescription = scoDescription;
//        confirmed = false;
//
//        Label l;
//        FontMetrics fm;
//
//        /* Sconame label */
//        l = new Label(TextMapper.getText(TextMapper.GUISDLG_SCO_NAME) + ":");
//        l.setForeground(Color.black);
//        l.setFont(GuiConstants.NORMAL_TEXT);
//        fm = l.getFontMetrics(l.getFont());
//        l.setLocation(10, 30);
//        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
//        l.setVisible(false);
//        contentPane.add(l);
//        l.setVisible(true);
//
//        /* Sconame field */
//        name = new TextField(scoName);
//        name.setBounds(150, 28, 200, 20);
//        name.setVisible(false);
//        contentPane.add(name);
//        name.setVisible(true);
//
//        /* Scodescription label */
//        l = new Label(TextMapper.getText(TextMapper.GUISDLG_SCO_DESCRIPTION)
//                + ":");
//        l.setForeground(Color.black);
//        l.setFont(GuiConstants.NORMAL_TEXT);
//        fm = l.getFontMetrics(l.getFont());
//        l.setLocation(10, 55);
//        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
//        l.setVisible(false);
//        contentPane.add(l);
//        l.setVisible(true);
//
//        /* Scodescription field */
//        description = new TextArea(scoDescription, 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
//        description.setBounds(150, 53, 200, 100);
//        description.setVisible(false);
//        contentPane.add(description);
//        description.setVisible(true);
//
//        contentPane.setSize(360, 220);
//
//        /* Register button */
//        okButton = new JButton(TextMapper.getText(TextMapper.BTN_OK));//,  GuiConstants.MAIN_BACKGROUND);
//        okButton.setSize(okButton.getPreferredSize());
//        okButton.addActionListener(this);
//
//        /* Reset button */
//        cancelButton = new JButton(TextMapper.getText(TextMapper.BTN_CANCEL));//, GuiConstants.MAIN_BACKGROUND);
//        cancelButton.setSize(cancelButton.getPreferredSize());
//        cancelButton.addActionListener(this);
//
//        okButton.setLocation(
//                (contentPane.getSize().width / 2)
//                        - ((okButton.getSize().width
//                                + cancelButton.getSize().width + 5) / 2), 163);
//        contentPane.add(okButton);
//
//        cancelButton.setLocation(
//                (contentPane.getSize().width / 2)
//                        - ((okButton.getSize().width
//                                + cancelButton.getSize().width + 5) / 2)
//                        + okButton.getSize().width + 5, 163);
//        contentPane.add(cancelButton);
//
//        Point p = owner != null ? owner.getLocation() : new Point(0, 0);
//        Dimension parentSize = owner != null ? owner.getSize() : Toolkit
//                .getDefaultToolkit().getScreenSize();
//        Dimension mySize = getSize();
//        int x = p.x + (parentSize.width - mySize.width) / 2;
//        int y = p.y + (parentSize.height - mySize.height) / 2;
//
//        setLocation(x, y);
//        this.addWindowListener(this);
//        pack();
//    }

//    public static Sco addSco(Course course, AppletConfig appletConfig) {
//        return addSco(null, course, appletConfig);
//    }

    /**
     * @return fi.dwo.client.domain.Sco
     */
    public static Sco addSco(Component owner, Course course, AppletConfig appletConfig) {
        CourseNameDialog cnd = new CourseNameDialog(owner, TextMapper
                .getText(TextMapper.GUISDLG_TTL_ADD_SCO), 0, appletConfig.getName(), "",TextMapper.GUISDLG_SCO_NAME, TextMapper.GUISDLG_SCO_DESCRIPTION);
        cnd.setShowScore(true);
        cnd.show();
        if (cnd.isConfirmed()) {
            //System.out.println("voor hij wordt aangemaakt: " + appletConfig.getLaunchdata() + "; " + appletConfig.getAppletID());
            Sco s = GuiCreator.instance().addSco(course, appletConfig, cnd.getScoName(),
                    cnd.getScoDescription(), cnd.isShowScore());
            if(s == null) { //something went wrong, reshow the dialog
                s = addSco(owner, course, appletConfig);
            }
            //System.out.println("en nu...: " + s.getLaunchdataString());
            s.setCourse(course);
            return s;
        } else { //action canceled
            return null;
        }
    }

    public static boolean editSco(Sco sco) {
        return editSco(sco, DwoHelper.getApplet());
    }

    /**
     * @param sco
     * @return boolean
     */
    public static boolean editSco(Sco sco, Component owner) {
        CourseNameDialog cnd = new CourseNameDialog(owner, TextMapper
                .getText(TextMapper.GUISDLG_TTL_EDIT_SCO), sco.getScoID(), sco.getScoName(),
                sco.getDescription(), TextMapper.GUISDLG_SCO_NAME, TextMapper.GUISDLG_SCO_DESCRIPTION);
        cnd.setShowScore(sco.isShowScore());
        cnd.show();
        if (cnd.isConfirmed()) {
            String oldName = sco.getScoName();
            String oldDescription = sco.getDescription();
            Boolean oldShowScore = sco.getShowScore();
            sco.setName(cnd.getScoName());
            sco.setDescription(cnd.getScoDescription());
// keep null as long as possible. TRUE -> NULL if null
            if(cnd.isShowScore())
            {
            	if( sco.getShowScore() != null)
            		sco.setShowScore(Boolean.TRUE);
            } else {
            	sco.setShowScore(Boolean.FALSE);
            }
            	
            boolean result = GuiCreator.instance().updateSco(sco);
            if (!result) { //something went wrong. Reset the data and reshow the dialog.
                sco.setName(oldName);
                sco.setDescription(oldDescription);
                sco.setShowScore(oldShowScore);
                result = editSco(sco, owner);
            }

            return result;
        } else { //action canceled
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
//    public void actionPerformed(ActionEvent e) {
//        if (e.getSource() == cancelButton) {
//            this.setVisible(false);
//        } else if (e.getSource() == okButton) {
//            scoName = name.getText();
//            scoDescription = description.getText();
//            confirmed = true;
//            this.setVisible(false);
//        }
//
//    }
//
//    /**
//     * Invoked when the window is set to be the user's active window, which
//     * means the window (or one of its subcomponents) will receive keyboard
//     * events.
//     * 
//     * @param e
//     *            The WindowEvent.
//     * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
//     */
//    public void windowActivated(WindowEvent e) {
//    }
//
//    /**
//     * Invoked when a window has been closed as the result of calling dispose on
//     * the window.
//     * 
//     * @param e
//     *            The WindowEvent.
//     * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
//     */
//    public void windowClosed(WindowEvent e) {
//    }
//
//    /**
//     * Invoked when the user attempts to close the window from the window's
//     * system menu. If the program does not explicitly hide or dispose the
//     * window while processing this event, the window close operation will be
//     * cancelled.
//     * 
//     * @param e
//     *            The WindowEvent.
//     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
//     */
//    public void windowClosing(WindowEvent e) {
//        setVisible(false);
//        dispose();
//    }
//
//    /**
//     * Invoked when a window is no longer the user's active window, which means
//     * that keyboard events will no longer be delivered to the window or its
//     * subcomponents.
//     * 
//     * @param e
//     *            The WindowEvent.
//     * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
//     */
//    public void windowDeactivated(WindowEvent e) {
//    }
//
//    /**
//     * Invoked when a window is changed from a minimized to a normal state.
//     * 
//     * @param e
//     *            The WindowEvent.
//     * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
//     */
//    public void windowDeiconified(WindowEvent e) {
//    }
//
//    /**
//     * Invoked when a window is changed from a minimized to a normal state.
//     * 
//     * @param e
//     *            The WindowEvent.
//     * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
//     */
//    public void windowIconified(WindowEvent e) {
//    }
//
//    /**
//     * Invoked when a window is changed from a normal to a minimized state. For
//     * many platforms, a minimized window is displayed as the icon specified in
//     * the window's iconImage property.
//     * 
//     * @param e
//     *            The WindowEvent.
//     * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
//     */
//    public void windowOpened(WindowEvent e) {
//    }
//
//    /**
//     * @return Returns the confirmed.
//     */
//    public boolean isConfirmed() {
//        return confirmed;
//    }
//
//    /**
//     * @return Returns the scoDescription.
//     */
//    public String getScoDescription() {
//        return scoDescription;
//    }
//
//    /**
//     * @return Returns the scoName.
//     */
//    public String getScoName() {
//        return scoName;
//    }
}