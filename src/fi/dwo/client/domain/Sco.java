// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\domain\\Sco.java

package fi.dwo.client.domain;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;

import fi.beans.base64code.StringCodeObject;
import fi.beans.scorm.SCORM12APIInterface;
import fi.dwo.client.gui.DwoMessageDialog;
import fi.dwo.client.gui.ScoPanel;
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
public class Sco implements LessonGroup, SCORM12APIInterface, AppletStub, Comparable {
    private static final DecimalFormatSymbols US_DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols(Locale.US);

	private int scoID;

    private String name;

    private String description;

    private int appletID;

    private Applet applet;

    protected Hashtable launchdata; // subclass implements lazyness

    private ScoPanel scoPanel;

    public DwoIF dwo;

    private User user;

    private Course course;
    
    private int sequencenr;
    
    private ScoPanel sc;

	private boolean initialized;

	public static final String NORMAL = "normal";
	public static final String REVIEW = "review";
	public static final String BROWSE = "browse";
	public static final String LESSON_MODE = "cmi.core.lesson_mode";
	private String  lessonMode = NORMAL;
	

    public String getLessonMode() {
		return lessonMode;
	}

	public void setLessonMode(String lessonMode) {
		this.lessonMode = lessonMode;
	}

	/**
     * Creates a new Sco object.
     *  
     */
    public Sco() {
        dwo = null;
        user = null;
        launchdata = null;
        sc = null;
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
            MapperCreator.instance(Applet.class).removeAllObjects();
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
            if(applet == null) {
                applet = (Applet) PersistenceFacade.instance().get(appletID, Applet.class);
            }
            
            /* Some strange bug with java 1.4.2_07 in XP, we can only set the stub once */
            if(lastApplet != applet) {
                applet.setStub(this);
                sc = null; //something is changed, so the scopanel must created again
            }
        } catch (PersistenceException e) {
            DwoMessageDialog.showMessageDialog(null, e.getMessage());
        }
        
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
	    String s = TextMapper.getText(TextMapper.LG_SCO_NAME);
	    return MessageFormat.format(s, arguments);
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
    	LMSSetValue("cmi.core.session_time", "00:00:00");
        return true + "";
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
	    		String total_time = LMSGetValue("cmi.core.total_time");
	    		String session_time = LMSGetValue("cmi.core.session_time");
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
	    			LMSSetValue("cmi.core.total_time", result.toString());
System.err.println("sum = ["+result+"]");
	    		} catch (Exception e)
	    		{
	    			e.printStackTrace();
	    		}
    		}
    		return "true";
    	}
    	return "false";
    }

    /**
     * Returns the user-specific value for the iDataModelElement.
     * 
     * @param iDataModelElement The name of the parameter.
     * @return The user-specific value for the iDataModelElement.
     * @see fi.beans.scorm.SCORM12APIInterface#LMSGetValue(java.lang.String)
     *  
     */
    public String LMSGetValue(String iDataModelElement) {
    	if(LESSON_MODE.equals(iDataModelElement))
    		return getLessonMode();
        return dwo.LMSGetValue(this, user, iDataModelElement);
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
    public String LMSSetValue(String iDataModelElement, String iValue) {
    	if(LESSON_MODE.equals(iDataModelElement))
    	{
    		// TODO set error op 'readonly' variable
    		return "false";
    	}
        return dwo.LMSSetValue(this, iDataModelElement, iValue);
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
        return null;
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
    public String LMSGetLastError() {
        return "101";
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
        return null;
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
        this.launchdata = launchdata;
    }

    /**
     * Indicates that the applet must save the data.
     *  
     */
    public void end() {
        if (applet != null) {
            try {
				applet.stop();
				applet.destroy();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
            applet = null;
            LMSFinish("");
            
//            dwo.endSco(this);
        }
    }
    
    public void endWithoutSaving() {
        if (applet != null) {
            try {
				applet.destroy();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
            applet = null;
            scoPanel = null;
            
            
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
        String s = TextMapper.getText(TextMapper.LG_SCOS_OF_COURSE);
        return MessageFormat.format(s, arguments);
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
}