// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\ScoPanel.java
package fi.dwo.dwojapplet.gui;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.beans.scorm.SCORM12APIInterface;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.persistence.DbAccessCreator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a panel witch shows an applet of the sco.
 *
 * @author M.J.B. Kupers
 *
 */
public class ScoPanel extends JPanel implements CenterSubPanel,
        SCORM12APIInterface, ActionListener {

    private static final Color HOME_COLOR = new Color(3, 65, 123);

    private Applet applet;
    private static final Logger log = Logger.getLogger(ScoPanel.class.getName());

    private Sco sco;

    private CenterPanel center;

    //private LinkedLabel mainMenuButton;
    private JButton mainMenuImageButton;

    private LinkedLabel courseButton;
//    private JButton courseImageButton;

    private JButton closeButton;

    private boolean scoView;

    private boolean courseView;

    private CardLayout layout;

    public ParameterManagementPanel tmp;

    /**
     * Creates a new ScoPanel with an applet of the specified sco.
     *
     * @param sco The sco wherefrom the applet must be showed.
     */
    public ScoPanel(Sco sco) {
        super(null);
        layout = new CardLayout();
        setLayout(layout);
        this.sco = sco;
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setOpaque(!GuiConstants.GUI_IMAGE_BG);
        //this.setBackground(Color.red);
        //this.setSize(789, 492);
        Dimension dim = DwoHelper.getApplet().getSize();

        this.setSize(dim.width - 11, dim.height - 108);
        this.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        setPreferredSize(new Dimension(1, 1)); // Applet volgt de maat der dingen
        applet = sco.getApplet();
        applet.setVisible(false);
//        applet.setSize(this.getSize().width-10, this.getSize().height); FIXME
// Er verschijnt een wit vlak ter grootte van de applet. Maak de applet size 0x0, vlak ook weg! 
        applet.setSize(0, 0);
        applet.setLocation(5, 5);
        addApplet();

        if (DwoHelper.umpc) {
            closeButton = new JButton("X");
            closeButton.setBounds(getSize().width - 18, 2, 16, 16);
            closeButton.addActionListener(this);
            add(closeButton, 0);
        }
        if (sco.getCourse() != null) {
            courseView = ((CoursePanel) sco.getCourse().getCoursePanel()).getCourseView();
        }
    }

    private void addApplet() {
        applet.setVisible(true);
        if (applet instanceof JApplet) {
            JApplet japplet = (JApplet) applet;
            JRootPane root = japplet.getRootPane();
            root.setBounds(japplet.getBounds());
            this.add(root, "rootpane");
            this.add(japplet, "applet");
            layout.show(this, "rootpane");
        } else {
            this.add(applet, "applet");
        }
    }

    public void setScoView(boolean b) {
        scoView = b;
    }

    /**
     * Initialiseer applet. applet moet een parent en stub hebben!
     */
    public void init() {
        String init = sco.LMSInitialize("");
// Duplicaat detectie
        if (!"true".equals(init)) {
// inform user.
            JOptionPane.showMessageDialog(this, "Duplicate initialization", "Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("duplicate detected");
        }

        try {
            applet.setLocation(10000, 10000); // let op, buiten beeld anders geflits.
            applet.setSize(getSize()); // zet applet een default size, voor oude applets

            applet.init();
            applet.start();
            validate();
        } catch (RuntimeException e) {
            // TODO Applet is niet gestart!
            // Dialog: interne fout, sco niet goed geïnitialiseerd.
            log.log(Level.SEVERE,null,e);
            try {
                DbAccessCreator.instance().log(GuiCreator.instance().getUser().getID() + " Sco " + sco.getID() + "," + applet + " exception in ScoPanel.init: " + e.toString());
                StringWriter w = new StringWriter();
                PrintWriter pw = new PrintWriter(w);
                e.printStackTrace(pw);
                DbAccessCreator.instance().log(w.toString());

            } catch (IOException e1) {
                log.log(Level.SEVERE,null,e1);
            } catch (XmlRpcException e1) {
                log.log(Level.SEVERE,null,e1);
            }
        }
    }

    /**
     * Indicate that another panel is loaded and the connections of this panel
     * must be closed.
     */
    @Override
    public void end() {
        sco.end();
    }

    /**
     * Sets the centerpanel to communicate with.
     *
     * @param centerPanel The centerPanel to communicate with.
     */
    @Override
    public void setCenterPanel(CenterPanel centerPanel) {
        center = centerPanel;
    }

    /**
     * Returns a Component that can function as a header panel.
     *
     * @return A Component that can function as a header panel.
     * @see fi.dwo.client.gui.CenterSubPanel#getHeaderPanel()
     */
    @Override
    public Component getHeaderPanel() {
// TODO cleanup needed!!!!
        JPanel jp = new JPanel(new BorderLayout());
        jp.setBackground(GuiConstants.MAIN_BACKGROUND);
        jp.setOpaque(!GuiConstants.GUI_IMAGE_BG);
        jp.setDoubleBuffered(false);
        Box hbox = Box.createHorizontalBox();
        String text = sco.getScoName();
        JLabel l;
        //if(!scoView)
        //text = sco.getSequencenr() + ".  " + text;
        HeaderPanel hp = new HeaderPanel(text, true);
        hp.setHorizontalAlignment(SwingConstants.LEFT);
        hp.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        hbox.add(Box.createHorizontalStrut(10));
// goto modules
        if (!scoView && !courseView) {
            mainMenuImageButton = new JButton(new ImageIcon(DwoHelper.getResourceImage(GuiConstants.BACK_MAINMENU_IMAGE)));
            mainMenuImageButton.setBorder(null);
            mainMenuImageButton.setContentAreaFilled(false);
            hbox.add(mainMenuImageButton);
            mainMenuImageButton.addActionListener(this);
// separator label
            l = new JLabel(" >> ");
            l.setFont(GuiConstants.RED_TEXT);
            l.setForeground(HOME_COLOR);
            l.setOpaque(false);
            hbox.add(l);
        }
// goto course
        if (!scoView) {
            courseButton = new LinkedLabel(sco.getCourse().getName());
            //courseButton.setCursor(new JButton().getCursor()); // restore button cursor
            courseButton.setNewForeground(HOME_COLOR);
            courseButton.setFont(GuiConstants.RED_TEXT);
            courseButton.addActionListener(this);
            courseButton.setIcon(
                    new ImageIcon(DwoHelper.getResourceImage(GuiConstants.BACK_COURSEMENU_IMAGE))
            );
            courseButton.setHorizontalTextPosition(JButton.LEADING);
            hbox.add(courseButton);
        }
        hbox.add(Box.createGlue());
        if (!scoView && !GuiConstants.GUI_IMAGE_BG) {
            Image courseLogo;
            Course course = sco.getCourse();
            courseLogo = course.getCourseLogo();
            MediaTracker tr = new MediaTracker(this);
            tr.addImage(courseLogo, 0);
            try {
                tr.waitForAll();
            } catch (Exception e) {
            }
            courseLogo = courseLogo.getScaledInstance(courseLogo.getWidth(null) / 2, courseLogo.getHeight(null) / 2, Image.SCALE_SMOOTH);
            tr.addImage(courseLogo, 0);
            try {
                tr.waitForAll();
            } catch (Exception e) {
            }
            l = new JLabel(new ImageIcon(courseLogo));
            hbox.add(l);

            hbox.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 10));
        }

        jp.add(hp, BorderLayout.CENTER);
        jp.add(hbox, BorderLayout.NORTH);
        if (!scoView && !courseView) // deeplink
        {
            JComponent buttonBox = GuiCreator.instance().getButtonBox(this);
            if (buttonBox != null) {
                hp.setButtonBox(buttonBox);
            }
        }
        if (!GuiConstants.GUI_IMAGE_BG) {
            jp.setBorder(MainPanel.createNBorder()); // n shape border ....
        }
        jp.setSize(469, 70); // FIXME
        jp.setMaximumSize(new Dimension(Short.MAX_VALUE, 70));
        jp.setPreferredSize(jp.getSize());
        jp.setMinimumSize(new Dimension(50, 70));
        jp.invalidate();
        //jp.validate();
        //jp.doLayout();
        //hbox.doLayout();
        return jp;
//    	
//    	
//    	
//    	
//    	Panel p = new BorderedPanel(null, BorderedPanel.NORTH
//                | BorderedPanel.EAST | BorderedPanel.WEST);
//    	if(GuiConstants.GUI_IMAGE_BG) {
//        	p = new BorderedPanel(null,0)
//            {
//	        	public void paint(Graphics g)
//	            {	Point p = DwoHelper.getComponentLocation(this);
//	            	g.drawImage(DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_SCO),-p.x,-p.y,null);
//	            	super.paint(g);
//	            }
//	        };
//        }
//        p.setBackground(GuiConstants.MAIN_BACKGROUND);
//        p.setBounds(181, 20, 469, 71);
//        if(GuiConstants.GUI_IMAGE_BG) p.setBounds(181, 0, 469, 81);
////        this.add(p);
//        
//        mainMenuImageButton = new JButton(new ImageIcon(DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.BACK_MAINMENU_IMAGE)));
//        mainMenuImageButton.setBorder(null);
//        mainMenuImageButton.setBackground(GuiConstants.MAIN_BACKGROUND);
//        mainMenuImageButton.setSize(18,18);
//        mainMenuImageButton.setLocation(8,8);
//        if(GuiConstants.GUI_IMAGE_BG) mainMenuImageButton.setLocation(48,0);
//		p.add(mainMenuImageButton);	
//		mainMenuImageButton.addActionListener(this);
//		
//        mainMenuButton = new LinkedLabel(TextMapper.getText(TextMapper.GUIMNU_MAIN_MENU));
//        mainMenuButton.setFont(GuiConstants.RED_TEXT);
//        mainMenuButton.setNewForeground(new Color(3,65,123));
//        FontMetrics fm = mainMenuButton.getFontMetrics(mainMenuButton.getFont());
//        mainMenuButton.setSize(fm.stringWidth(mainMenuButton.getText()) + 5, fm.getHeight() + 10);
//        mainMenuButton.setLocation(4+mainMenuImageButton.getLocation().x + mainMenuImageButton.getSize().width, 4);
//        mainMenuButton.addActionListener(this);
//        //mainMenuButton.setVisible(false);
//        //if(!scoView && !courseView)p.add(mainMenuButton);
//        //mainMenuButton.setVisible(true);
//        
//        
//        
//        JLabel l = new JLabel(">>");
//        l.setOpaque(false);
//        l.setFont(GuiConstants.RED_TEXT);
//        l.setForeground(new Color(3,65,123));
//        fm = l.getFontMetrics(l.getFont());
//        l.setSize(fm.stringWidth(l.getText()) + 5, fm.getHeight() + 10);
//        l.setLocation(5+mainMenuImageButton.getLocation().x + mainMenuImageButton.getSize().width, 4);
//        if(GuiConstants.GUI_IMAGE_BG) l.setLocation(5+mainMenuImageButton.getLocation().x + mainMenuImageButton.getSize().width, -4);
//        // l.setVisible(false);
//        if(!scoView && !courseView)p.add(l);
//        //l.setVisible(true);
//
//       
//		
//		courseButton = new LinkedLabel(sco.getCourse().getName());
//        courseButton.setFont(GuiConstants.RED_TEXT);
//        courseButton.setNewForeground(new Color(3,65,123));
//        fm = courseButton.getFontMetrics(courseButton.getFont());
//        courseButton.setSize(fm.stringWidth(courseButton.getText()) + 5, fm.getHeight() + 10);
//        if(courseView) courseButton.setLocation(20, 4);
//        else courseButton.setLocation(5+l.getLocation().x + l.getSize().width, 4);
//        if(GuiConstants.GUI_IMAGE_BG) courseButton.setLocation(5+l.getLocation().x + l.getSize().width, -4);
//        courseButton.addActionListener(this);
//        //courseButton.setVisible(false);
//        if(!scoView)p.add(courseButton);
//        //courseButton.setVisible(true);
//        
//        courseImageButton = new JButton(new ImageIcon(DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.BACK_COURSEMENU_IMAGE)));
//        courseImageButton.setBorder(null);
//        courseImageButton.setBackground(GuiConstants.MAIN_BACKGROUND);
//        courseImageButton.setSize(22,22);
//        courseImageButton.setLocation(5+courseButton.getLocation().x + courseButton.getSize().width,7);
//        if(GuiConstants.GUI_IMAGE_BG) courseImageButton.setLocation(5+courseButton.getLocation().x + courseButton.getSize().width,0);
//		p.add(courseImageButton);	
//		courseImageButton.addActionListener(this);
//
//        l = new JLabel(">>");
//        l.setFont(GuiConstants.RED_TEXT);
//        l.setForeground(new Color(3,65,123));
//        fm = l.getFontMetrics(l.getFont());
//        l.setSize(fm.stringWidth(l.getText()) + 5, fm.getHeight() + 10);
//        l.setLocation(courseButton.getLocation().x + courseButton.getSize().width, 4);
//        //l.setVisible(false);
//        //if(!scoView)p.add(l);
//        //l.setVisible(true);
//
//        /* My Sco-Label */
//        
//        if(!scoView)l = new JLabel(sco.getSequencenr() + ".  " + sco.getScoName());
//        else l = new JLabel(sco.getScoName());
//        l.setFont(GuiConstants.HEADER_TEXT);
//        fm = l.getFontMetrics(l.getFont());
//        l.setSize(fm.stringWidth(l.getText()) + 5, fm.getHeight());
//        if(!scoView)l.setLocation(20, mainMenuButton.getSize().height + mainMenuButton.getLocation().y + 10);
//        //if(GuiConstants.GUI_IMAGE_BG && !scoView) l.setLocation(20, mainMenuButton.getSize().height + mainMenuButton.getLocation().y + 28);
//        else l.setLocation(20, 20);
//        Font f;
//
//        int maxHeight = 26;//p.getSize().height - l.getLocation().y;
//
//        /* Scale the fontsize */
//        while ((l.getSize().width > p.getSize().width - 25)
//                || (l.getSize().height > maxHeight)) {
//            f = l.getFont();
//            l.setFont(new Font(f.getName(), f.getStyle(), f.getSize() - 1));
//            fm = l.getFontMetrics(l.getFont());
//            l.setSize(fm.stringWidth(l.getText()) + 5, fm.getHeight());
//        }
//        l.setVisible(false);
//        p.add(l);
//        l.setVisible(true);
//
//		Image courseLogo;
//		Course course = sco.getCourse();
//		courseLogo = course.getCourseLogo();
//        MediaTracker tr = new MediaTracker(this);
//        tr.addImage(courseLogo, 0);
//        try {
//            tr.waitForAll();
//        } catch (Exception e) {
//        }
//        courseLogo = courseLogo.getScaledInstance(courseLogo.getWidth(null)/2 , courseLogo.getHeight(null)/2 , Image.SCALE_SMOOTH);
//        tr.addImage(courseLogo, 0);
//        try {
//            tr.waitForAll();
//        } catch (Exception e) {
//        }
//        ImagePanel ip = new ImagePanel(courseLogo);
//        ip.setLocation(p.getSize().width - ip.getSize().width - 8 , 8);
//        if(!scoView) p.add(ip, 0);
//        if(GuiConstants.GUI_IMAGE_BG) p.remove(ip);
//        
//        return p;
    }

    public void xxxpaintComponent(Graphics g) {
        if (GuiConstants.GUI_IMAGE_BG) {
            Point p = DwoHelper.getComponentLocation(this);
            Image image = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_SCO);
            if (image != null && p != null) {
                g.drawImage(image, -p.x, -p.y, null);
            }
        }
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e The ActionEvent.
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (/* e.getSource() == mainMenuButton || */e.getSource() == mainMenuImageButton) {
            //   center.loadCenter(GuiCreator.instance().getCourseChoisePanel());
            center.select(ModuleTreePanel.ALLE_MODULES);
        } else if (e.getSource() == courseButton) {
            //center.loadCenter(GuiCreator.instance().getCoursePanel(sco.getCourse()));
            center.select(sco.getCourse());
        } // aanpassing umpc
        else if (DwoHelper.umpc && e.getSource() == closeButton) {
            center.loadCenter(GuiCreator.instance().getCoursePanel(sco.getCourse()));
            center.setLocation(5, 90);
            center.getParent().add(center);
        }
        //
    }

    /**
     * Initializes the SCORM LMS.
     *
     * @param iParam An empty string must be passed for conformance to this
     * standard. Values other than "" are reserved for future extensions.
     * @return String representing a boolean.
     * <ul>
     * <li><code>true</code> result indicates that the LMSInitialize("") was
     * successful</li>
     * <li><code>false</code> result indicates that the LMSInitialize("") was
     * unsuccessful</li>
     * </ul>
     * If a return value of <code>false</code> is returned, then this signifies
     * to the SCO that the LMS is in an unknown state and that any additional
     * API calls will not be processed by the LMS.
     * @see fi.beans.scorm.SCORM12APIInterface#LMSInitialize(java.lang.String)
     *
     */
    @Override
    public String LMSInitialize(String iParam) {
        return sco.LMSInitialize(iParam);
    }

    /**
     * Finish the LMS.
     *
     * @param iParam An empty string must be passed for conformance to this
     * standard. Values other than "" are reserved for future extensions.
     * @return result of LMSFinish is a String representing a boolean.
     * <ul>
     * <li><code>true</code> result indicates that the LMSFinish("") was
     * successful</li>
     * <li><code>false</code> result indicates that the LMSFinish("") was
     * unsuccessful</li>
     * </ul>
     * If a return value of <code>true</code> is returned, then the SCO may no
     * longer call any other API methods.If a return value of "false" is
     * returned, then this signifies to the SCO that the LMS is in an unknown
     * state and that any additional API calls may or may not be processed by
     * the LMS.
     * @see fi.beans.scorm.SCORM12APIInterface#LMSFinish(java.lang.String)
     *
     */
    @Override
    public String LMSFinish(String iParam) {
        return sco.LMSFinish(iParam);
    }

    /**
     * Returns the user-specific value for the iDataModelElement.
     *
     * @param iDataModelElement The name of the parameter.
     * @return The user-specific value for the iDataModelElement.
     * @see fi.beans.scorm.SCORM12APIInterface#LMSGetValue(java.lang.String)
     *
     */
    @Override
    public String LMSGetValue(String iDataModelElement) {
        return sco.LMSGetValue(iDataModelElement);
    }

    /**
     * Sets the user-specific value for the iDataModelElement.
     *
     * @param iDataModelElement The dataModelElement to set.
     * @param iValue The value to set.
     * @return String representing a boolean
     * <ul>
     * <li><code>true</code> result indicates that the LMSSetValue() was
     * successful</li>
     * <li><code>false</code> result indicates that the LMSSetValue() was
     * unsuccessful</li>
     * </ul>
     * @see fi.beans.scorm.SCORM12APIInterface#LMSSetValue(java.lang.String,
     * java.lang.String)
     *
     */
    @Override
    public String LMSSetValue(String iDataModelElement, String iValue) {
//        try {
//            DbAccessCreator.instance().log("ScoPanel.LMSSetValue(" + iDataModelElement + ", " + iValue + ")");
//        } catch(Exception e2) {
////            DwoMessageDialog.showMessageDialog(null, e2.getMessage());
//            
//        }        
        return sco.LMSSetValue(iDataModelElement, iValue);
    }

    /**
     * This call ensures to the SCO that the data sent, via an
     * <code>LMSSetValue()</code> call, will be persisted by the LMS upon
     * completion of the LMSCommit().
     *
     * @param iParam An empty string must be passed for conformance to this
     * standard. Values other than "" are reserved for future extensions.
     * @return String representing a boolean
     * <ul>
     * <li><code>true</code> result indicates that the LMSCommit("") was
     * successful</li>
     * <li><code>false</code> result indicates that the LMSCommit("") was
     * unsuccessful</li>
     * </ul>
     * If a return value of <code>false</code> is returned, then this signifies
     * to the SCO that the LMS is in an unknown state and that any additional
     * API calls may or may not be processed by the LMS.
     * @see fi.beans.scorm.SCORM12APIInterface#LMSCommit(java.lang.String)
     *
     */
    @Override
    public String LMSCommit(String iParam) {
        return sco.LMSCommit(iParam);
    }

    /**
     * The SCO must have a way of assessing whether or not any given API call
     * was successful, and if it was not successful, what went wrong. This
     * method returns an error status code resulting from the previous API call.
     * Each time an API method is called (with the exception of this one,
     * <code>LMSGetErrorString</code>, and <code>LMSGetDiagnostic</code>-- the
     * error methods), the error code is reset. The SCO may call the error
     * methods any number of times to retrieve the error code, and the code
     * cannot change until the next API call is made.
     *
     * @return The return values are Strings that can be converted to integer
     * numbers that identify errors falling into the following categories:
     * <ul>
     * <li>100's General errors</li>
     * <li>200's Syntax errors</li>
     * <li>300's LMS errors</li>
     * <li>400's Data model errors</li>
     * </ul>
     * The following codes are available for error messages:
     * <ul>
     * <li>0 No error</li>
     * <li>101 General exception</li>
     * <li>201 Invalid argument error</li>
     * <li>202 Element cannot have children</li>
     * <li>203 Element not an array - cannot have count</li>
     * <li>301 Not initialized</li>
     * <li>401 Not implemented error</li>
     * <li>402 Invalid set value, element is a keyword</li>
     * <li>403 Element is read only</li>
     * <li>404 Element is write only</li>
     * <li>405 Incorrect Data Type</li>
     * </ul>
     * Additional codes TBD
     * @see fi.beans.scorm.SCORM12APIInterface#LMSGetLastError()
     *
     */
    @Override
    public String LMSGetLastError() {
        return sco.LMSGetLastError();
    }

    /**
     * This method enables the content to obtain a textual description of the
     * error represented by the error code number.
     *
     * @param iErrorCode An integer number representing an error code.
     *
     * @return A string that represents the verbal description of an error.
     * @see
     * fi.beans.scorm.SCORM12APIInterface#LMSGetErrorString(java.lang.String)
     *
     */
    @Override
    public String LMSGetErrorString(String iErrorCode) {
        return sco.LMSGetErrorString(iErrorCode);
    }

    /**
     * This method enables vendor-specific error descriptions to be developed
     * and accessed by the content. These would normally provide additional
     * detail regarding the error.
     *
     * @param iErrorCode The parameter may take one of two forms.
     * <ul>
     * <li>An integer number representing an error code. This requests
     * additional information on the listed error code.
     * </li>
     *
     * <li>"". An empty string. This requests additional information on the last
     * error that occurred.</li>
     * </ul>
     *
     * @return The return value is a string that represents any vendor-desired
     * additional information relating to either the requested error or the last
     * error.
     * @see
     * fi.beans.scorm.SCORM12APIInterface#LMSGetDiagnostic(java.lang.String)
     *
     */
    @Override
    public String LMSGetDiagnostic(String iErrorCode) {
        return sco.LMSGetDiagnostic(iErrorCode);
    }

    /**
     * Returns the current sco.
     *
     * @return The current sco.
     */
    public Sco getSco() {
        return sco;
    }

    @Override
    public Object getUserObject() {
        return getSco();
    }

    /**
     * Returns the current object, as the object to add to a gui.
     *
     * @return the current object.
     * @see fi.dwo.client.gui.CenterSubPanel#getComponent()
     */
    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void stateChanged(ChangeEvent e) {


    }
}
