// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\domain\\Sco.java
package fi.dwo.dwojapplet.domain;

import fi.beans.scorm.PartialScoreIF;
import fi.beans.scorm.SCORM12APIInterface;
import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.gui.GuiConstants;
import fi.dwo.dwojapplet.gui.GuiCreator;
import fi.dwo.dwojapplet.gui.ScoPanel;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * This class is responsible for the Sco data. It also implements the
 * <code>SCORM12APIInterface</code> and functions as AppletStub for the applet.
 *
 * @author M.J.B. Kupers
 *
 */
public class Sco extends ScoBase implements LessonGroup, SCORM12APIInterface, AppletStub, Comparable, ScoEditor {
    private static final Logger log = Logger.getLogger(Sco.class.getName());

    private ScoEditor editor = this;
    private PropertyChangeSupport bean = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        bean.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        bean.addPropertyChangeListener(propertyName, listener);
    }

    void firePropertyChange(String propertyName, Object oldValue,
            Object newValue) {
        bean.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        bean.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        bean.removePropertyChangeListener(propertyName, listener);
    }

    private String name;

    private String description;

    private Applet applet;
    private Boolean showScore;

    private int sequencenr;

    private boolean initialized;

    public static void setDefaultLessonMode(String mode) {
        if (BROWSE.equals(mode)) {
            mode = BROWSE;
        } else {
            mode = NORMAL;
        }
        defaultLessonMode = mode;
    }

    /**
     * Creates a new Sco object.
     *
     */
    public Sco() {
        super(false);
        dwo = null;
        user = null;
        launchdata = null;
        sc = null;
    }

    public boolean isShowScore() {
        return !Boolean.FALSE.equals(showScore);
    }

    public Boolean getShowScore() {
        return showScore;
    }

    public void setShowScore(boolean showScore) {
        this.showScore = showScore;
    }

    public void setShowScore(Boolean showScore) {
        this.showScore = showScore;
    }

    /**
     * Returns a panel representing this sco. The panel contains the applet.
     *
     * @param dwo The dwo to communicate with.
     * @param user The user for the applet. His suspenddata will be used.
     * @return A panel representing this sco.
     *
     */
    public ScoPanel getScoPanel(DwoIF dwo, User user) {
        this.dwo = dwo;
        User lastUser = this.user;
        setUser(user);
        if (lastUser != this.user || user == null) {
            applet = null;
            //MapperCreator.instance(Applet.class).removeAllObjects();
            sc = null;
        }
        loadApplet();
        if (applet == null) { //something was wrong with creating the applet
            return null;
        }
        if (sc == null) {
            sc = new ScoPanel(this);
            sc.init();
        } else if (lastUser != this.user || user == null) { //if the user is the same as last time, we don't need to refresh the applet 
            sc.init();
        } else { //if(applet.getClass().getName().equals("fi.popupurlapplet.PopUpURLApplet")) {
            sc.init();
        }
        return sc;
    }

    /**
     * Loads the applet corresponding to this sco.
     *
     */
    protected void loadApplet() {
        try {
            Applet lastApplet = applet;
            //lessonLocation = null;
            if (applet == null) {
                Class clazz = (Class) PersistenceFacade.instance().get(appletID, Applet.class);
                try {
                    applet = (Applet) clazz.newInstance();
                } catch (InstantiationException e) {
                    throw new PersistenceException(PersistenceException.EX_UNKNOWN_ERROR, e);
                } catch (IllegalAccessException e) {
                    throw new PersistenceException(PersistenceException.EX_UNKNOWN_ERROR, e);
                }
            }

            /* Some strange bug with java 1.4.2_07 in XP, we can only set the stub once */
            if (lastApplet != applet) {
                applet.setStub(this);
                sc = null; //something is changed, so the scopanel must created again
            }
        } catch (PersistenceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

    }

    public PartialScoreIF getPartialScoreIF() {
        loadApplet();
        if (applet instanceof PartialScoreIF) {
            return (PartialScoreIF) applet;
        }
        return new DefaultPartialScore(applet);
    }

    /**
     * Returs the name representing the LessonGroup object.
     *
     * @return The name representing the LessonGroup object.
     *
     */
    @Override
    public String getName() {
        String[] arguments = new String[1];
        arguments[0] = "" + sequencenr;
        return TextMapper.format((TextMapper.LG_SCO_NAME), arguments);
    }

    /**
     * Returns the name of the Sco.
     *
     * @return The name of the sco.
     */
    public String getScoName() {
        return name;
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
        if (initialized) {
            return ko("false");
        }
        initialized = true;
        if (NORMAL.equals(getLessonMode())) {
            SetValue(SESSION_TIME, "00:00:00");
        }
        return ok("true");
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
        if (initialized) {
            initialized = false;
            if (NORMAL.equals(getLessonMode())) {
                String total_time = GetValue(TOTAL_TIME);
                String session_time = GetValue(SESSION_TIME);
                try {
                    System.err.println("total = [" + total_time + "] session =[" + session_time + "]");
                    if (null == total_time || "".equals(total_time)) {
                        total_time = "0000:00:00.00";
                    }
                    if (null == session_time || "".equals(session_time)) {
                        return "true";
                    }
                    StringTokenizer t = new StringTokenizer(total_time, ":");
                    StringTokenizer s = new StringTokenizer(session_time, ":");
                    int h = Integer.parseInt(t.nextToken()) + Integer.parseInt(s.nextToken());
                    int m = Integer.parseInt(t.nextToken()) + Integer.parseInt(s.nextToken());
                    double sec = Double.parseDouble(t.nextToken()) + Double.parseDouble(s.nextToken());
                    if (sec >= 60) {
                        sec -= 60;
                        m += 1;
                    }
                    if (m >= 60) {
                        m -= 60;
                        h += 1;
                    }
                    StringBuffer result = new StringBuffer();
                    DecimalFormat format = new DecimalFormat("##00", US_DECIMAL_FORMAT_SYMBOLS);
                    result.append(format.format(h));
                    result.append(":");
                    format.applyPattern("00");
                    result.append(format.format(m));
                    result.append(":");
                    format.applyPattern("00.##");
                    result.append(format.format(sec));
                    SetValue(TOTAL_TIME, result.toString());
                    System.err.println("sum = [" + result + "]");
                } catch (Exception e) {
                    log.log(Level.SEVERE,null,e);
                }
            }
            return ok("true");
        }
        return ko("false");
    }

    /**
     * @param iValue
     * @param current TODO
     * @param course TODO
     * @param sc
     * @return
     * @throws NumberFormatException
     */
    static public String gotoSco(String iValue, Object current, Course course, final Component sc) throws NumberFormatException {
        Sco[] list = course.getScoList();
        int sconr = list.length;
// XXX Sietske wil eerst op nummer daarna pas op titel
        try {
            sconr = Integer.parseInt(iValue) - 1;
        } catch (Exception e) {
        }
        if (sconr <= -1 || sconr >= list.length) {
            for (sconr = 0; sconr < list.length; sconr++) {
                if (list[sconr].getScoName().startsWith(iValue)) {
                    break; // found by prefix ? equals? 
                }
            }
        }
        if (sconr == list.length) // not found, try numeric
        {
            sconr = Integer.parseInt(iValue) - 1; // 1..length
        }
        final Object sco = sconr < 0 ? (Object) course : (Object) (course.getScoList()[sconr]); // array out of bounce?
        if (sco == current) {
            return "false"; // no jump, no stop/start.
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (sc.isShowing()) {
                    // setlessonmode nodig? TODO uitzoeken
                    GuiCreator.instance().getMainPanel().getCenter().select(sco);
                }
            }
        });
        return "true";
    }

    public boolean isMergable(Sco other) {
        if (other.getAppletID() != getAppletID()) {
            return false;
        }
        if (features == null) {
            features = getAppletData().getFeatures();
            if (features == null) {
                features = "";
            }
        }
        return features.indexOf(MERGABLE) >= 0;

    }

    /**
     * Returns the applet of the sco.
     *
     * @return The applet of the sco.
     */
    public Applet getApplet() {
        if (applet == null) {
            loadApplet();
        }
        return applet;
    }

    /**
     * Sets the applet of the sco.
     *
     * @param applet The new applet of the sco.
     */
    public void setApplet(Applet applet) {
        this.applet = applet;
    }

    /**
     * Returns the unique-identifier of the applet.
     *
     * @return The unique-identifier of the applet.
     */
    public int getAppletID() {
        return appletID;
    }

    /**
     * Sets the unique-identifier of the applet,
     *
     * @param appletID The unique-identifier of the applet.
     */
    public void setAppletID(int appletID) {
        this.appletID = appletID;
    }

    /**
     * Returns the description of the sco.
     *
     * @return The description of the sco.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the sco.
     *
     * @param description The description of the sco.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the name of the sco.
     *
     * @param name The name of the sco.
     */
    public void setName(String name) {
        this.name = name;
    }

    /* Applet Stub methods */
    /**
     * Determines if the applet is active. An applet is active just before its
     * <code>start</code> method is called. It becomes inactive just before its
     * <code>stop</code> method is called.
     *
     * @return <code>true</code> if the applet is active; <code>false</code>
     * otherwise.
     * @see java.applet.AppletStub#isActive()
     */
    @Override
    public boolean isActive() {
        return DwoHelper.getApplet().isActive();
    }

    /**
     * Called when the applet wants to be resized.
     *
     * @param width the new requested width for the applet.
     * @param height the new requested height for the applet.
     * @see java.applet.AppletStub#appletResize(int, int)
     */
    @Override
    public void appletResize(int width, int height) {
    }

    /**
     * Gets a handler to the applet's context.
     *
     * @return the applet's context.
     * @see java.applet.AppletStub#getAppletContext()
     */
    @Override
    public AppletContext getAppletContext() {
        return DwoHelper.getApplet().getAppletContext();
    }

    /**
     * Gets the base URL. This is the URL of the directory which contains the
     * applet.
     *
     * @return the base {@link java.net.URL}of the directory which contains the
     * applet.
     * @see java.applet.AppletStub#getCodeBase()
     */
    @Override
    public URL getCodeBase() {
        return DwoHelper.getApplet().getCodeBase();
    }

    /**
     * Gets the URL of the document in which the applet is embedded. For
     * example, suppose an applet is contained within the document: <blockquote>
     *
     * <pre>
     *
     *
     *      http://java.sun.com/products/jdk/1.2/index.html
     *
     *
     * </pre>
     *
     * </blockquote> The document base is: <blockquote>
     *
     * <pre>
     *
     *
     *      http://java.sun.com/products/jdk/1.2/index.html
     *
     *
     * </pre>
     *
     * </blockquote>
     *
     * @return the {@link java.net.URL}of the document that contains the applet.
     * @see java.applet.AppletStub#getDocumentBase()
     */
    @Override
    public URL getDocumentBase() {
        return DwoHelper.getApplet().getDocumentBase();
    }

    /**
     * Returns the value of the named parameter in the HTML tag. For example, if
     * an applet is specified as <blockquote>
     *
     * <pre>
     *
     *
     *   &lt;applet code=&quot;Clock&quot; width=50 height=50&gt;
     *   &lt;param name=Color value=&quot;blue&quot;&gt;
     *   &lt;/applet&gt;
     *
     *
     * </pre>
     *
     * </blockquote>
     * <p>
     * then a call to <code>getParameter("Color")</code> returns the value
     * <code>"blue"</code>.
     *
     * @param name a parameter name.
     * @return the value of the named parameter, or <tt>null</tt> if not set.
     * @see java.applet.AppletStub#getParameter(java.lang.String)
     */
    @Override
    public String getParameter(String name) {
        if ("deployVariantDWO".equals(name) && GuiConstants.DEPLOY_VARIANT.length() > 0) {
            return GuiConstants.DEPLOY_VARIANT;
        }
        if (name.equals("language")) {
            return TextMapper.getLanguage();
        }
        if (name.equals("bgcolor")) {
            int red = GuiConstants.MAIN_BACKGROUND.getRed();
            int green = GuiConstants.MAIN_BACKGROUND.getGreen();
            int blue = GuiConstants.MAIN_BACKGROUND.getBlue();
            int colorCode = 256 * 256 * red + 256 * green + blue;
            return "#" + Integer.toHexString(colorCode);
        }
// Support ScoID
        if ("scoViewNr".equals(name)) {
            return String.valueOf(getScoID());
        }
// export class id and class name
        if ("class_id".equals(name)) {
            SchoolClass tmpSc = User.getCurrentUser().getInClass();
            if (tmpSc != null) {
                return String.valueOf(tmpSc.getID());
            }
        }
        if ("class_name".equals(name)) {
            SchoolClass tmpSc = User.getCurrentUser().getInClass();
            if (tmpSc != null) {
                return String.valueOf(tmpSc);
            }
        }

        Hashtable ld = getLaunchdata();

        Object result = ld.get(name);
        if (result != null) {
            return (String) result;
        }
        return DwoHelper.getApplet().getParameter(name);
    }

    /**
     * Indicates that the applet must save the data.
     *
     */
    public synchronized void end() {
        if (applet != null) {
            try {
                applet.stop();
                applet.destroy();
            } catch (RuntimeException e) {
                //TODO Wim: Dialog: interne fout, sco niet goed afgesloten, mogelijk verlies van gegevens.
                JOptionPane.showMessageDialog(applet, e.getMessage());
                log.log(Level.SEVERE,null,e);
                try {

                    User localUser = user;
                    if (localUser == null) {
                        localUser = dwo.getUser();
                    }
                    log.log(Level.FINE, "{0} Sco {1},{2} exception in Sco.end: {3}", new Object[]{localUser.getID(), scoID, applet, e.toString()});
                    StringWriter w = new StringWriter();
                    PrintWriter pw = new PrintWriter(w);
                    e.printStackTrace(pw);
                    log.log(Level.FINE,w.toString());
                } catch (RuntimeException e1) {
                    log.log(Level.SEVERE,null,e1);
                }
            }
            applet = null;
            LMSFinish("");
//            dwo.endSco(this);
        }
    }

    public void endWithoutSaving() {
        if (applet != null) {
            try {
                applet.stop(); // dit bepaalt wel of niet saven van sco's 
                applet.destroy();
            } catch (RuntimeException e) {
                log.log(Level.SEVERE,null,e);
            }
            applet = null;
            sc = null;
            initialized = false;
        }
    }

    /**
     * Indicates if this is the deepest LessonGroup.
     *
     * @return If this is the deepest LessonGroup it returns true. Otherwise it
     * returns false.
     * @see fi.dwo.client.domain.LessonGroup#isDeepestLevel()
     */
    @Override
    public boolean isDeepestLevel() {
        return true;
    }

    /**
     * Indicates if this is the highest LessonGroup.
     *
     * @return If this is the highest LessonGroup it returns true. Otherwise it
     * returns false.
     * @see fi.dwo.client.domain.LessonGroup#isHighestLevel()
     */
    @Override
    public boolean isHighestLevel() {
        return false;
    }

    /**
     * Returns the Sco specific title for the LessonGroup.
     *
     * @return The Sco specific title.
     * @see fi.dwo.client.domain.LessonGroup#getTitle()
     */
    @Override
    public String getTitle() {
        String[] arguments = new String[1];
        if (course != null) {
            arguments[0] = course.getName();
        } else {
            arguments[0] = "";
        }
        return TextMapper.format((TextMapper.LG_SCOS_OF_COURSE), arguments);
    }

    /**
     * Returns a title represents the parent item.
     *
     * @return A title represents the parent item.
     * @see fi.dwo.client.domain.UserGroup#getParentTitle()
     */
    @Override
    public String getParentTitle() {
        return TextMapper.getText(TextMapper.LG_SCO_PARENT);
    }

    /**
     * Returns a title represents the child item.
     *
     * @return A title represents the child item.
     * @see fi.dwo.client.domain.UserGroup#getChildTitle()
     */
    @Override
    public String getChildTitle() {
        return "";
    }

    /**
     * Returns a title represents the Ascending Order item.
     *
     * @return A title represents the Ascending Order item.
     * @see fi.dwo.client.domain.UserGroup#getOrderAscTitle()
     */
    @Override
    public String getOrderAscTitle() {
        return TextMapper.getText(TextMapper.LG_SCO_ORDER_ASC);
    }

    /**
     * Returns a title represents the Descending Order item.
     *
     * @return A title represents the Descending Order item.
     * @see fi.dwo.client.domain.UserGroup#getOrderDescTitle()
     */
    @Override
    public String getOrderDescTitle() {
        return TextMapper.getText(TextMapper.LG_SCO_ORDER_DESC);
    }

    /**
     * Returns the sequencenumber of the sco.
     *
     * @return The sequencenumber of the sco.
     */
    public int getSequencenr() {
        return sequencenr;
    }

    /**
     * Sets the sequencenumber of the sco.
     *
     * @param sequencenr The sequencenumber to set.
     */
    public void setSequencenr(int sequencenr) {
        if (!isCourseChanged()) {
            setCourseChanged(this.sequencenr != sequencenr);
        }
        this.sequencenr = sequencenr;
    }

    /**
     * Returns a tooltip for the LessonGroup.
     *
     * @return A tooltip for the LessonGroup.
     * @see fi.dwo.client.domain.LessonGroup#getToolTip()
     */
    @Override
    public String getToolTip() {
        return name;
    }

    /**
     * Compares to sco's with each other on the sequencenumber.
     *
     * @param o The other sco to compare.
     * @return a negative integer, zero, or a positive integer as this sco his
     * sequencenumber is less than, equal to, or greater than the specified sco.
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Object o) {
        Sco s = (Sco) o;
        if (sequencenr == s.getSequencenr()) {
            return 0;
        }

        if (sequencenr < s.getSequencenr()) {
            return -1;
        }

        return 1;
    }

    @Override
    public String toString() {
        return getScoName();
    }

    public Hashtable getEditLaunchdata() {
        return editor.getLaunchdata();
    }

    public void setEditor(ScoEditor editor) {
        if (editor == null) {
            editor = this;
        }
        this.editor = editor;
    }

    public void setEditLaunchdata(Hashtable params) {
        editor.setLaunchdata(params);
    }

    public void setLocationOverride(String loc) {
        this.locationOverride = loc;

    }

    public Sco unwrap() {
        return this;
    }

}
