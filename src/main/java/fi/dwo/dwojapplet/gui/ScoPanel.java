// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\ScoPanel.java
package fi.dwo.dwojapplet.gui;

import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JOptionPane;
import fi.beans.scorm.SCORM12APIInterface;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Sco;
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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;

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
    private static final Logger LOG = Logger.getLogger(ScoPanel.class.getName());

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
        this.setBorder(BorderFactory.createEmptyBorder(120, 0, 0, 0));
        //this.setBackground(Color.red);
        //this.setSize(789, 492);
        Dimension dim = DwoHelper.getApplet().getSize();

        this.setSize(1024, 768);
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
            appletStart();
            validate();
        } catch (Throwable e) {
            // TODO Applet is niet gestart!
            // Dialog: interne fout, sco niet goed geïnitialiseerd.
            LOG.log(Level.SEVERE,null,e);
            LOG.log(Level.FINE, "{0} Sco {1},{2} exception in ScoPanel.init: {3}", new Object[]{GuiCreator.instance().getUser().getID(), sco.getID(), applet, e.toString()});
            StringWriter w = new StringWriter();
            PrintWriter pw = new PrintWriter(w);
            LOG.log(Level.FINE,w.toString());
            LOG.log(Level.FINE,pw.toString());
        }
    }

	void appletStart() {
		try {
			applet.start();
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
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

    /* (non-Javadoc)
     * @see fi.dwo.dwojapplet.gui.CenterSubPanel#getSubHeaderColor()
     */
    @Override
    public Color getSubHeaderColor() {
      return Constants.COLOR20;
    }

    @Override
    public JComponent getSubHeaderPanel() {
      // TODO cleanup needed!!!!
              JPanel jp = new JPanel(new BorderLayout());
              jp.setBackground(GuiConstants.MAIN_BACKGROUND);
              jp.setOpaque(false);
              jp.setDoubleBuffered(false);
              Box hbox = Box.createHorizontalBox();
              //String text = sco.getScoName();
              JLabel l;
              //if(!scoView)
              //text = sco.getSequencenr() + ".  " + text;
//              HeaderPanel hp = new HeaderPanel(text, true); hp.hide();
//              hp.setHorizontalAlignment(SwingConstants.LEFT);
//              hp.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
              hbox.add(Box.createHorizontalStrut(10));
              hbox.setBorder(BorderFactory.createEmptyBorder(18, 0, 0, 0));
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
                  l.setForeground(fi.beans.numworxlf.Constants.COLOR13);
                  l.setOpaque(false);
                  hbox.add(l);
              }
      // goto course
              if (!scoView) {
                  courseButton = new LinkedLabel(sco.getCourse().getName());
                  //courseButton.setCursor(new JButton().getCursor()); // restore button cursor
                  courseButton.setNewForeground(fi.beans.numworxlf.Constants.COLOR13);
                  courseButton.setFont(GuiConstants.RED_TEXT);
                  courseButton.addActionListener(this);
                  //courseButton.setIcon(new ImageIcon(DwoHelper.getResourceImage(GuiConstants.BACK_COURSEMENU_IMAGE)));
                  courseButton.setHorizontalTextPosition(JButton.LEADING);
                  hbox.add(courseButton);
              }
              hbox.add(Box.createGlue());
              if (!scoView ) {
                  Image courseLogo;
                  Course course = sco.getCourse();
                  courseLogo = DwoHelper.getResourceImage(GuiConstants.BACK_COURSEMENU_IMAGE);
                  MediaTracker tr = new MediaTracker(this);
                  tr.addImage(courseLogo, 0);
                  try {
                      tr.waitForAll();
                  } catch (Exception e) {
                  }
                  
                  int w = courseLogo.getWidth(null);
                  int s = 2;
                  int h = courseLogo.getHeight(null);
                  s  =  (h-1)/30+1; 
      // bepaal s zodat h/s < 30       
                  
                  //courseLogo = courseLogo.getScaledInstance(w / s, h / s, Image.SCALE_SMOOTH);
                  tr.addImage(courseLogo, 0);
                  try {
                      tr.waitForAll();
                  } catch (Exception e) {
                  }
                  JButton b = new JButton(new ImageIcon(courseLogo));
                  b.setContentAreaFilled(false);
                  b.setBorderPainted(false);
                  b.addActionListener(e -> { center.select(sco.getCourse()); });
                  hbox.add(b);
      
                  hbox.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 10));
              }
      
//              jp.add(hp, BorderLayout.CENTER);
              jp.add(hbox, BorderLayout.NORTH);
//              if (!scoView && !courseView) // deeplink
//              {
//                  JComponent buttonBox = GuiCreator.instance().getButtonBox(this);
//                  if (buttonBox != null) {
//                      hp.setButtonBox(buttonBox);
//                  }
//              }
              {
                  jp.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // n shape border ....
              }
              jp.setSize(469, 50); // FIXME
              jp.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));
              jp.setPreferredSize(jp.getSize());
              jp.setMinimumSize(new Dimension(50, 50));
              jp.invalidate();
              //jp.validate();
              //jp.doLayout();
              //hbox.doLayout();
              return jp;
    }
    
    
    
    /**
     * Returns a Component that can function as a header panel.
     *
     * @return A Component that can function as a header panel.
     * @see fi.dwo.client.gui.CenterSubPanel#getHeaderPanel()
     */
    @Override
    public JComponent getHeaderPanel() {
// TODO cleanup needed!!!!
        String text = sco.getScoName();
//        boolean istoets = false; // FIXME moeilijk te bepalen
//        Icon icon = null;
//        if (istoets) 
//          icon = new ImageIcon(DwoHelper.getResourceImage("resources/zelftoets.png"));
//        else
//          icon = new ImageIcon(DwoHelper.getResourceImage("resources/lesstof.png"));
        HeaderPanel hp = new HeaderPanel(text, true);
        hp.setBackground(getSubHeaderColor());
        //hp.setIcon(icon);
        hp.setHorizontalAlignment(SwingConstants.LEFT);
        if (!scoView && !courseView) // deeplink
        {
            JComponent buttonBox = GuiCreator.instance().getButtonBox(this);
            if (buttonBox != null) {
                hp.setButtonBox(buttonBox);
            }
        }
        return hp;
    }

//    public void xxxpaintComponent(Graphics g) {
//        if (GuiConstants.GUI_IMAGE_BG) {
//            Point p = DwoHelper.getComponentLocation(this);
//            Image image = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_SCO);
//            if (image != null && p != null) {
//                g.drawImage(image, -p.x, -p.y, null);
//            }
//        }
//    }

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
//            LOG.log("ScoPanel.LMSSetValue(Level.FINE," + iDataModelElement + ", " + iValue + ")");
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
        if (e.getSource() == getSco().getCourse()) {
          center.reloadHeader();
        }

    }
}
