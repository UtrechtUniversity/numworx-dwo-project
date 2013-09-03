// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\domain\\Sco.java

package fi.dwo.client.domain;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.beans.base64code.StringCodeObject;
import fi.beans.scorm.PartialScoreIF;
import fi.beans.scorm.SCORM12APIInterface;
import fi.beans.scorm.ScormAdapter;
//import fi.dwo.client.gui.DwoMessageDialog;
import fi.dwo.client.gui.GuiCreator;
import fi.dwo.client.gui.ScoPanel;
import fi.dwo.client.persistence.DbAccessCreator;
import fi.dwo.client.persistence.MapperCreator;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.PersistenceException;
import fi.dwo.client.system.TextMapper;
import fi.dwo.client.gui.GuiConstants;

/**
 * This class is responsible for the Sco data.
 * It also implements the <code>SCORM12APIInterface</code> and functions as AppletStub for the applet.
 * @author M.J.B. Kupers
 *  
 */
public class Sco extends ScormAdapter implements LessonGroup, SCORM12APIInterface, AppletStub, Comparable, ScoEditor {
	private static final char REVIEWABLE = 'r';
	private static final char MERGABLE = 'm';

	private static final DecimalFormatSymbols US_DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols(Locale.US);

    private ScoEditor editor = this;
    private PropertyChangeSupport bean = new PropertyChangeSupport(this);
    
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		bean.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		bean.addPropertyChangeListener(propertyName, listener);
	}

	private void firePropertyChange(String propertyName, Object oldValue,
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

	private int scoID;

    private String name;

    private String description;

    private int appletID;

    private Applet applet;
    private AppletData appletData;
    private Boolean showScore;    
    
    protected Hashtable launchdata; // subclass implements lazyness

    public DwoIF dwo;

    private User user;

    private Course course;
    private boolean courseChanged;
    
    private int sequencenr;
    
    private ScoPanel sc;

	private boolean initialized, dataChanged;

	public boolean isDataChanged() {
		return dataChanged;
	}

	public void setDataChanged(boolean dataChanged) {
		this.dataChanged = dataChanged;
	}

	public boolean isCourseChanged() {
		return courseChanged;
	}

	public void setCourseChanged(boolean courseChanged) {
		this.courseChanged = courseChanged;
	}

	public static final String NORMAL = "normal";
	public static final String REVIEW = "review";
	public static final String BROWSE = "browse";
	private static final String LESSON_MODE = "cmi.mode";
	public static final String LAUNCH_DATA = "cmi.launch_data";
	public static final String LESSON_LOCATION = "cmi.location";
    private static final String SESSION_TIME = "session_time";
    private static final String CMI_SESSION_TIME = "cmi." + SESSION_TIME;
    private static final String TOTAL_TIME = "total_time";
    private static final String CMI_TOTAL_TIME = "cmi." + TOTAL_TIME;

    private static final String CORE_SESSION_TIME = "cmi.core." + SESSION_TIME;
    public static final String CREDIT  = "credit";
    public static final String NO_CREDIT = "no-credit";
    private static final String CREDIT_STATUS = "cmi.credit";
    public static final String DWO_GOTO_SCONR = "dwo.goto.sconr"; // writeonly.

	private String  lessonMode = NORMAL;

	private String lessonLocation;
	

    public String getLessonMode() {
		return lessonMode;
	}

	public void setLessonMode(String lessonMode) {
		this.lessonMode = lessonMode;
		lessonLocation = null;
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
		this.showScore = Boolean.valueOf(showScore);
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
        this.user = user;
        if(lastUser != this.user || user==null) {
            applet = null;
            //MapperCreator.instance(Applet.class).removeAllObjects();
            sc = null;
        }
        loadApplet();
        if(applet == null) { //something was wrong with creating the applet
            return null;
        }
        if(sc == null) {
            sc = new ScoPanel(this);
            sc.init();            
        } else if(lastUser != this.user || user==null) { //if the user is the same as last time, we don't need to refresh the applet 
            sc.init();
        }
        else{ //if(applet.getClass().getName().equals("fi.popupurlapplet.PopUpURLApplet")) {
           	sc.init();
        }
        return sc;
    }
    
    /**
     * Loads the applet corresponding to this sco.
     *  
     */
    private void loadApplet() {
        try {
            Applet lastApplet = applet;
            lessonLocation = null;
            if(applet == null) {
                Class clazz = (Class)PersistenceFacade.instance().get(appletID, Applet.class);
				try {
					applet = (Applet) clazz.newInstance();
				} catch (InstantiationException e) {
					throw new PersistenceException(PersistenceException.EX_UNKNOWN_ERROR, e);
				} catch (IllegalAccessException e) {
					throw new PersistenceException(PersistenceException.EX_UNKNOWN_ERROR, e);
				}
            }
            
            /* Some strange bug with java 1.4.2_07 in XP, we can only set the stub once */
            if(lastApplet != applet) {
                applet.setStub(this);
                sc = null; //something is changed, so the scopanel must created again
            }
        } catch (PersistenceException e) {
        	JOptionPane.showMessageDialog(null, e.getMessage());
        }
        
    }

    public AppletData getAppletData()
    {
    	if(appletData == null)
    	{
    		try {
				appletData = (AppletData)PersistenceFacade.instance().get(appletID, AppletData.class);
			} catch (PersistenceException e) {
				e.printStackTrace();
			}
    	}
    	return appletData;
    }
    
    public PartialScoreIF getPartialScoreIF() {
    	loadApplet();
    	if(applet instanceof PartialScoreIF)
    		return (PartialScoreIF) applet;
    	return new DefaultPartialScore(applet);
    }
    
    /**
     * Returs the name representing the LessonGroup object.
     * 
     * @return The name representing the LessonGroup object.
     *  
     */
    public String getName() {
        String[] arguments = new String[1];
        arguments[0] = "" + sequencenr;
	    return TextMapper.format((TextMapper.LG_SCO_NAME), arguments);
    }
    
    /**
     * Returns the name of the Sco.
     * @return The name of the sco.
     */
    public String getScoName() {
        return name;
    }

    /**
     * Returns the unique-identifier for the LessonGroup object.
     * 
     * @return The unique-identifier for the LessonGroup object.
     *  
     */
    public int getID() {
        return scoID;
    }

    /**
     * Initializes the SCORM LMS.
     * 
     * @param iParam An empty string must be passed for conformance to this
     *            standard. Values other than "" are reserved for future
     *            extensions.
     * @return String representing a boolean.
     *         <ul>
     *         <li><code>true</code> result indicates that the
     *         LMSInitialize("") was successful</li>
     *         <li><code>false</code> result indicates that the
     *         LMSInitialize("") was unsuccessful</li>
     *         </ul>
     *         If a return value of <code>false</code> is returned, then this
     *         signifies to the SCO that the LMS is in an unknown state and that
     *         any additional API calls will not be processed by the LMS.
     * @see fi.beans.scorm.SCORM12APIInterface#LMSInitialize(java.lang.String)
     *  
     */
    public String LMSInitialize(String iParam) {
    	initialized = true;
    	if(NORMAL.equals(getLessonMode()))
    		SetValue(SESSION_TIME, "00:00:00");
        return ok("true");
    }

    /**
     * Finish the LMS.
     * 
     * @param iParam An empty string must be passed for conformance to this
     *            standard. Values other than "" are reserved for future
     *            extensions.
     * @return result of LMSFinish is a String representing a boolean.
     *         <ul>
     *         <li><code>true</code> result indicates that the LMSFinish("")
     *         was successful</li>
     *         <li><code>false</code> result indicates that the LMSFinish("")
     *         was unsuccessful</li>
     *         </ul>
     *         If a return value of <code>true</code> is returned, then the
     *         SCO may no longer call any other API methods.If a return value of
     *         "false" is returned, then this signifies to the SCO that the LMS
     *         is in an unknown state and that any additional API calls may or
     *         may not be processed by the LMS.
     * @see fi.beans.scorm.SCORM12APIInterface#LMSFinish(java.lang.String)
     *  
     */
    public String LMSFinish(String iParam) {
    	if(initialized )
    	{
    		initialized = false;
    		if(NORMAL.equals(getLessonMode()))
    		{	    		
	    		String total_time = GetValue(TOTAL_TIME);
	    		String session_time = GetValue(SESSION_TIME);
	    		try {
System.err.println("total = ["+total_time+"] session =[" + session_time + "]");
					if(null == total_time || "".equals(total_time))
					{	
						total_time = "0000:00:00.00";
					}
					if(null == session_time || "".equals(session_time))
						return "true";
					StringTokenizer t = new StringTokenizer(total_time, ":");
	    			StringTokenizer s = new StringTokenizer(session_time, ":");
	    			int h = Integer.parseInt(t.nextToken()) + Integer.parseInt(s.nextToken());
	    			int m = Integer.parseInt(t.nextToken()) + Integer.parseInt(s.nextToken());
	    			double sec = Double.parseDouble(t.nextToken()) + Double.parseDouble(s.nextToken());
	    			if(sec >= 60) { sec -= 60; m += 1; }
	    			if(m >= 60) { m -= 60; h += 1; }
	    			StringBuffer result = new StringBuffer();
	    			DecimalFormat format = new DecimalFormat("##00", US_DECIMAL_FORMAT_SYMBOLS);
	    			result.append(format.format(h)); result.append(":");
	    			format.applyPattern("00");
	    			result.append(format.format(m)); result.append(":");
	    			format.applyPattern("00.##");
	    			result.append(format.format(sec));
	    			SetValue(TOTAL_TIME, result.toString());
System.err.println("sum = ["+result+"]");
	    		} catch (Exception e)
	    		{
	    			e.printStackTrace();
	    		}
    		}
    		return ok("true");
    	}
    	return ko("false");
    }

    /**
     * Returns the user-specific value for the iDataModelElement.
     * 
     * @param iDataModelElement The name of the parameter.
     * @return The user-specific value for the iDataModelElement.
     * @see fi.beans.scorm.SCORM12APIInterface#LMSGetValue(java.lang.String)
     *  
     */
    public String GetValue(String iDataModelElement) {
    	if(LESSON_MODE.equals(iDataModelElement))
    		return ok(getLessonMode());
    	if(LAUNCH_DATA.equals(iDataModelElement))
    	{
    		String value;
    		Hashtable ld = getLaunchdata();
    		value = (String)ld.get(LAUNCH_DATA);
    		if(value == null)
    			value = getLaunchdataString();
    		return ok(value);
    		
    	}
    	if(LESSON_LOCATION.equals(iDataModelElement))
    		return ok(getLessonLocation());
    	if(CREDIT_STATUS.equals(iDataModelElement))
    		return ok(getCreditStatus());
// session_ en total_time in 1.2 format.
    	if(CMI_SESSION_TIME.equals(iDataModelElement)||CMI_TOTAL_TIME.equals(iDataModelElement))
    	{
    		iDataModelElement = iDataModelElement.substring(4); // skip cmi.
    		return ok(to2004Time(from1_2Time(dwo.LMSGetValue(this, user, iDataModelElement))));
    	}
        return ok(dwo.LMSGetValue(this, user, iDataModelElement));  // null -> 101 else ok()
    }

    /**
     * Bepaal cmi.core.credit. Is gelijk aan is een assesment of niet.
     * @return credit/no-credit
     */
    public String getCreditStatus() {
		Course c = getCourse();
		if ( c != null ) {
			ClassCourse link = c.link;
			if(link != null && link.getType() == ClassCourse.ASSESMENT)
				return CREDIT;
		}
		return NO_CREDIT;
	}

	private String getLessonLocation() {
    	if(REVIEW.equals(lessonMode) && locationOverride != null) {
    		lessonLocation = locationOverride;
    		locationOverride = null;
    	}
    	if(REVIEW.equals(lessonMode) && lessonLocation != null)
    		return lessonLocation;
		return "";//dwo.LMSGetValue(this, user, LESSON_LOCATION);
	}

	/**
     * Sets the user-specific value for the iDataModelElement.
     * 
     * @param iDataModelElement The dataModelElement to set.
     * @param iValue The value to set.
     * @return String representing a boolean
     *         <ul>
     *         <li><code>true</code> result indicates that the LMSSetValue()
     *         was successful</li>
     *         <li><code>false</code> result indicates that the LMSSetValue()
     *         was unsuccessful</li>
     *         </ul>
     * @see fi.beans.scorm.SCORM12APIInterface#LMSSetValue(java.lang.String,
     *      java.lang.String)
     *  
     */
    public String SetValue(String iDataModelElement, String iValue) {
    	if(LESSON_MODE.equals(iDataModelElement))
    	{
    		// TODO set error op 'readonly' variable
    		return ko("false");
    	}
    	
    	if( DWO_GOTO_SCONR.equals (iDataModelElement)) {
    		if(NORMAL.equals(lessonMode))
	    		try {
	    			return tf(gotoSco(iValue, this, course, sc));
	    		} catch (Exception e) {
	    			//e.printStackTrace();
	    			
	    		}
    		return ko("false");
    	}
    	
    	
    	
    	if(NORMAL.equals(lessonMode))
    	{
			if(CMI_SESSION_TIME.equals(iDataModelElement))
			{
				iDataModelElement = SESSION_TIME;
				iValue = to1_2Time(from2004Time(iValue)); // sessiontime in 1.2 format.
			}
//			if(CMI_TOTAL_TIME.equals(iDataModelElement)) // NOT WRITABLE!
//			{
//				iDataModelElement = TOTAL_TIME;
//				iValue = to1_2Time(from2004Time(iValue)); // totaltime in 1.2 format.
//			}
    		return tf(dwo.LMSSetValue(this, user, iDataModelElement, iValue));
    	}

    	if(REVIEW.equals(lessonMode))
    	{
    		boolean ok = getReviewable(iDataModelElement);
    		if(ok)
    		{
    			return dwo.LMSSetValue(this, user, iDataModelElement, iValue);
    		} else if(LESSON_LOCATION.equals(iDataModelElement))
    		{
    			String last = lessonLocation;
    			lessonLocation = iValue;
    			firePropertyChange(LESSON_LOCATION, last, lessonLocation);
    			return ok("true");
    		}
    	}
    	// browse....
    	return ko("false");
    }

	/**
	 * @param iValue
	 * @param current TODO
	 * @param course TODO
	 * @return
	 * @throws NumberFormatException
	 */
	static public String gotoSco(String iValue, Object current, Course course, final Component sc) throws NumberFormatException {
		Sco[] list = course.getScoList();
		int sconr;
		for(sconr = 0; sconr < list.length; sconr++ ) {
			if(list[sconr].getScoName().startsWith(iValue)) {
				break; // found by prefix ? equals? 
			}
		}
		if(sconr == list.length) // not found, try numeric
			sconr = Integer.parseInt(iValue)-1; // 1..length
		final Object sco = sconr < 0 ? (Object)course : (Object)(course.getScoList()[sconr]); // array out of bounce?
		if(sco == current)
			return "false"; // no jump, no stop/start.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if(sc.isShowing()) {
					// setlessonmode nodig? TODO uitzoeken
					GuiCreator.instance().getMainPanel().getCenter().select(sco);
				}
			}
		});
		return "true";
	}

    
    private String features;

	private String locationOverride;

    private boolean getReviewable(String element) {
    	if (CMI_SESSION_TIME.equals(element)) // not reviewable!
    		return false;
    	if (LESSON_LOCATION.equals(element))		// special case. 
    		return false;
		if(features == null)
		{	features = getAppletData().getFeatures();
			if(features == null) features = "";
		}
		//System.out.println("Reviewable: "+(features.indexOf(REVIEWABLE)>=0));
		return features.indexOf(REVIEWABLE)>=0;
		
	}

    public boolean isMergable(Sco other) {
    	if(other.getAppletID() != getAppletID())
    		return false;
		if(features == null)
		{	features = getAppletData().getFeatures();
			if(features == null) features = "";
		}
		return features.indexOf(MERGABLE)>=0;
    	
    }
    
    
    
	/**
     * This call ensures to the SCO that the data sent, via an
     * <code>LMSSetValue()</code> call, will be persisted by the LMS upon
     * completion of the LMSCommit().
     * 
     * @param iParam An empty string must be passed for conformance to this
     *            standard. Values other than "" are reserved for future
     *            extensions.
     * @return String representing a boolean
     *         <ul>
     *         <li><code>true</code> result indicates that the LMSCommit("")
     *         was successful</li>
     *         <li><code>false</code> result indicates that the LMSCommit("")
     *         was unsuccessful</li>
     *         </ul>
     *         If a return value of <code>false</code> is returned, then this
     *         signifies to the SCO that the LMS is in an unknown state and that
     *         any additional API calls may or may not be processed by the LMS.
     * @see fi.beans.scorm.SCORM12APIInterface#LMSCommit(java.lang.String)
     *  
     */
    public String LMSCommit(String iParam) {
        return tf(dwo.LMSCommit(this, iParam));
    }

    /**
     * The SCO must have a way of assessing whether or not any given API call
     * was successful, and if it was not successful, what went wrong. This
     * method returns an error status code resulting from the previous API call.
     * Each time an API method is called (with the exception of this one,
     * <code>LMSGetErrorString</code>, and <code>LMSGetDiagnostic</code>--
     * the error methods), the error code is reset. The SCO may call the error
     * methods any number of times to retrieve the error code, and the code
     * cannot change until the next API call is made.
     * 
     * @return The return values are Strings that can be converted to integer
     *         numbers that identify errors falling into the following
     *         categories:
     *         <ul>
     *         <li>100's General errors</li>
     *         <li>200's Syntax errors</li>
     *         <li>300's LMS errors</li>
     *         <li>400's Data model errors</li>
     *         </ul>
     *         The following codes are available for error messages:
     *         <ul>
     *         <li>0 No error</li>
     *         <li>101 General exception</li>
     *         <li>201 Invalid argument error</li>
     *         <li>202 Element cannot have children</li>
     *         <li>203 Element not an array - cannot have count</li>
     *         <li>301 Not initialized</li>
     *         <li>401 Not implemented error</li>
     *         <li>402 Invalid set value, element is a keyword</li>
     *         <li>403 Element is read only</li>
     *         <li>404 Element is write only</li>
     *         <li>405 Incorrect Data Type</li>
     *         </ul>
     *         Additional codes TBD
     * @see fi.beans.scorm.SCORM12APIInterface#LMSGetLastError()
     *  
     */
    private String lastError = "0";
    private String ok(String ok) { lastError = "0"; return ok; }
    private String ko(String ko) { lastError = "101"; return ko;}
    private String tf(String tf) { lastError = "true".equals(tf) ? "0" : "101" ; return tf; }
    public String LMSGetLastError() {
        return lastError;
    }

    /**
     * This method enables the content to obtain a textual description of the
     * error represented by the error code number.
     * 
     * @param iErrorCode An integer number representing an error code.
     * 
     * @return A string that represents the verbal description of an error.
     * @return java.lang.String
     * @see fi.beans.scorm.SCORM12APIInterface#LMSGetErrorString(java.lang.String)
     *  
     */
    public String LMSGetErrorString(String iErrorCode) {
        return "";
    }

    /**
     * This method enables vendor-specific error descriptions to be developed
     * and accessed by the content. These would normally provide additional
     * detail regarding the error.
     * 
     * @param iErrorCode The parameter may take one of two forms.
     *            <ul>
     *            <li>An integer number representing an error code. This
     *            requests additional information on the listed error code.
     *            </li>
     * 
     * <li>"". An empty string. This requests additional information on the
     * last error that occurred.</li>
     * </ul>
     * 
     * @return The return value is a string that represents any vendor-desired
     *         additional information relating to either the requested error or
     *         the last error.
     * @see fi.beans.scorm.SCORM12APIInterface#LMSGetDiagnostic(java.lang.String)
     *  
     */
    public String LMSGetDiagnostic(String iErrorCode) {
        return "";
    }

    /**
     * Returns the applet of the sco.
     * 
     * @return The applet of the sco.
     */
    public Applet getApplet() {
        if(applet == null) {
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
     * Returns the unique-identifier of the sco.
     * 
     * @return The unique-identifier of the sco.
     */
    public int getScoID() {
        return scoID;
    }

    /**
     * Sets the unique-identifier of the sco.
     * 
     * @param scoID The unique-identifier of the sco.
     */
    public void setScoID(int scoID) {
        this.scoID = scoID;
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
     * <code>start</code> method is called. It becomes inactive just before
     * its <code>stop</code> method is called.
     * 
     * @return <code>true</code> if the applet is active; <code>false</code>
     *         otherwise.
     * @see java.applet.AppletStub#isActive()
     */
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
    public void appletResize(int width, int height) {
    }

    /**
     * Gets a handler to the applet's context.
     * 
     * @return the applet's context.
     * @see java.applet.AppletStub#getAppletContext()
     */
    public AppletContext getAppletContext() {
        return DwoHelper.getApplet().getAppletContext();
    }

    /**
     * Gets the base URL. This is the URL of the directory which contains the
     * applet.
     * 
     * @return the base {@link java.net.URL}of the directory which contains the
     *         applet.
     * @see java.applet.AppletStub#getCodeBase()
     */
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
     * @return the {@link java.net.URL}of the document that contains the
     *         applet.
     * @see java.applet.AppletStub#getDocumentBase()
     */
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
    public String getParameter(String name) {
    	if("deployVariantDWO".equals(name) && GuiConstants.DEPLOY_VARIANT.length()>0)
    		return GuiConstants.DEPLOY_VARIANT;
    	if(name.equals("language")) return TextMapper.getLanguage();
        if(name.equals("bgcolor")) 
        {	int red = GuiConstants.MAIN_BACKGROUND.getRed();
	        int green = GuiConstants.MAIN_BACKGROUND.getGreen();
	        int blue = GuiConstants.MAIN_BACKGROUND.getBlue();
        	int colorCode = 256*256*red+256*green+blue;
        	return "#"+ Integer.toHexString(colorCode);
        }
        Hashtable ld = getLaunchdata();
//        if (ld != null) {							// ld never unequal null
            Object result = ld.get(name);
            if(result != null) {
                return (String) result;
            }
//        }
        return DwoHelper.getApplet().getParameter(name);
    }

    /**
     * Returns the launchdata for the sco.
     * 
     * @return The launchdata for the sco.
     */
    public Hashtable getLaunchdata() {
        if(launchdata == null) { //No data specified we must have an empty hashtable
            launchdata = new Hashtable();
        }
        return launchdata;
    }
    
    public String getLaunchdataString() {
        Hashtable ld = getLaunchdata();
        return (new StringCodeObject(ld)).toString();
        
    }

    /**
     * Sets the launchdata for the sco.
     * 
     * @param launchdata The launchdata for the sco.
     */
    public void setLaunchdata(Hashtable launchdata) {
    	if(!isDataChanged())
    	{
    		if(this.launchdata == null)
    			setDataChanged(launchdata != null);
    		else
    			setDataChanged( !this.launchdata.equals(launchdata) );
    	}
        this.launchdata = launchdata;
    }

    public void setLaunchdataString(String ld) {
    	setLaunchdata((Hashtable) StringCodeObject.decodeStringToObject(ld));
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
				// Dialog: interne fout, sco niet goed afgesloten, mogelijk verlies van gegevens.
				
				
				e.printStackTrace();
				try {
					DbAccessCreator.instance().log(user.getID() + " Sco " + scoID +"," + applet + " exception in Sco.end: "+e.toString());
					StringWriter w = new StringWriter();
					PrintWriter pw = new PrintWriter(w);
					e.printStackTrace(pw);
					DbAccessCreator.instance().log(w.toString());
				
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (XmlRpcException e1) {
					e1.printStackTrace();
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
				e.printStackTrace();
			}
            applet = null;
            sc = null;
            
            
//            dwo.endSco(this);
        }
    }

    /**
     * Indicates if this is the deepest LessonGroup.
     * 
     * @return If this is the deepest LessonGroup it returns true. Otherwise it
     *         returns false.
     * @see fi.dwo.client.domain.LessonGroup#isDeepestLevel()
     */
    public boolean isDeepestLevel() {
        return true;
    }

    /**
     * Indicates if this is the highest LessonGroup.
     * 
     * @return If this is the highest LessonGroup it returns true. Otherwise it
     *         returns false.
     * @see fi.dwo.client.domain.LessonGroup#isHighestLevel()
     */
    public boolean isHighestLevel() {
        return false;
    }

    /**
     * Returns the Sco specific title for the LessonGroup.
     * 
     * @return The Sco specific title.
     * @see fi.dwo.client.domain.LessonGroup#getTitle()
     */
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
     * Returns the course of the sco.
     * 
     * @return The course of the sco.
     */
    public Course getCourse() {
        return course;
    }

    /**
     * Sets the course of the sco.
     * 
     * @param course The new course of the sco.
     */
    public void setCourse(Course course) {
    	if(!isCourseChanged())
    	{
    		setCourseChanged(this.course != course);
    	}
        this.course = course;
    }

    /**
     * Returns a title represents the parent item.
     * @return A title represents the parent item.
     * @see fi.dwo.client.domain.UserGroup#getParentTitle()
     */
    public String getParentTitle() {
        return TextMapper.getText(TextMapper.LG_SCO_PARENT);
    }

    /**
     * Returns a title represents the child item.
     * @return A title represents the child item.
     * @see fi.dwo.client.domain.UserGroup#getChildTitle()
     */
    public String getChildTitle() {
        return "";
    }

    /**
     * Returns a title represents the Ascending Order item.
     * @return A title represents the Ascending Order item.
     * @see fi.dwo.client.domain.UserGroup#getOrderAscTitle()
     */
    public String getOrderAscTitle() {
        return TextMapper.getText(TextMapper.LG_SCO_ORDER_ASC);
    }

    /**
     * Returns a title represents the Descending Order item.
     * @return A title represents the Descending Order item.
     * @see fi.dwo.client.domain.UserGroup#getOrderDescTitle()
     */
    public String getOrderDescTitle() {
        return TextMapper.getText(TextMapper.LG_SCO_ORDER_DESC);
    }
    
    /**
     * Returns the sequencenumber of the sco.
     * @return The sequencenumber of the sco.
     */
    public int getSequencenr() {
        return sequencenr;
    }
    
    /**
     * Sets the sequencenumber of the sco.
     * @param sequencenr The sequencenumber to set.
     */
    public void setSequencenr(int sequencenr) {
    	if(!isCourseChanged())
    	{
    		setCourseChanged(this.sequencenr != sequencenr);
    	}
        this.sequencenr = sequencenr;
    }

    /**
     * Returns a tooltip for the LessonGroup.
     * @return A tooltip for the LessonGroup.
     * @see fi.dwo.client.domain.LessonGroup#getToolTip()
     */
    public String getToolTip() {
        return name;
    }

    /**
     * Compares to sco's with each other on the sequencenumber.
     * @param o The other sco to compare.
     * @return  a negative integer, zero, or a positive integer as this sco his sequencenumber
     *		is less than, equal to, or greater than the specified sco.
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        Sco s = (Sco) o;
        if(sequencenr == s.getSequencenr())
            return 0;
        
        if(sequencenr < s.getSequencenr())
            return -1;
        
        return 1;
    }
    
    public String toString() {
    	return getScoName();
    }

	public Hashtable getEditLaunchdata() {
		return editor.getLaunchdata();
	}

	public void setEditor(ScoEditor editor) {
		if(editor == null)
			editor = this;
		this.editor = editor;
	}

	public void setEditLaunchdata(Hashtable params) {
		editor.setLaunchdata(params);
	}

	public void setUser(User u) {
		user = u;	
	}

	public void setLocationOverride(String loc) {
		this.locationOverride = loc;
		
	}

	public User getUser() {
		return this.user;
	}
}