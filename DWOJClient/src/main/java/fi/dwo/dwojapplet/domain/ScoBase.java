package fi.dwo.dwojapplet.domain;

import fi.beans.private_base64code.StringCodeObject;
import fi.beans.scorm.ScormAdapter;
import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.dwojapplet.gui.ScoPanel;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

public abstract class ScoBase extends ScormAdapter {
private static final Logger LOG = Logger.getLogger(ScoBase.class.getName());

    public static final String NORMAL = "normal";
    public static final String REVIEW = "review";
    public static final String BROWSE = "browse";
    protected static String defaultLessonMode = NORMAL;
    protected int scoID;
    public DWO dwo;
    protected User user;
    protected SchoolClass cls;
    public static final String LESSON_MODE = "cmi.mode";
    public static final String LAUNCH_DATA = "cmi.launch_data";
    public static final String LESSON_LOCATION = "cmi.location";
    public static final String COMPLETION_STATUS = "cmi.completion_status";
    protected static final String SESSION_TIME = "session_time";
    protected static final String CMI_SESSION_TIME = "cmi." + SESSION_TIME;
    protected static final String TOTAL_TIME = "total_time";
    protected static final String CMI_TOTAL_TIME = "cmi." + TOTAL_TIME;
    private static final String CORE_SESSION_TIME = "cmi.core." + SESSION_TIME;
    public static final String CREDIT = "credit";
    public static final String NO_CREDIT = "no-credit";
    protected static final String CREDIT_STATUS = "cmi.credit";
    private static final char REVIEWABLE = 'r';
    protected static final char MERGABLE = 'm';
    public static final char JSON_OUT = 'J';
    protected static final char JSON_IN = 'j';

    protected static final DecimalFormatSymbols US_DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols(Locale.US);
    public static final String DWO_GOTO_SCONR = "dwo.goto.sconr";
    public String features;

    /**
     * Returns the unique-identifier for the LessonGroup object.
     *
     * @return The unique-identifier for the LessonGroup object.
     *
     */
    public int getID() {
        return scoID;
    }

    protected int appletID;
    private AppletData appletData;
    protected Hashtable launchdata;
    protected Course course;
    private boolean courseChanged;
    protected ScoPanel sc;
    private boolean dataChanged;

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
     * @see fi.beans.scorm.SCORM12APIInterface#LMSGetLastError()
     *
     */
    private String lastError = "0";
    private String lessonMode = defaultLessonMode;
    protected String lessonLocation;
    protected String locationOverride;

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
    public String LMSCommit(String iParam) {
        return tf(dwo.LMSCommit(this, iParam));
    }

    public ScoBase(boolean isCOCDmodel) {
        super(isCOCDmodel);
        
    }

    protected String ok(String ok) {
        lastError = "0";
        return ok;
    }

    protected String ko(String ko) {
        lastError = "101";
        return ko;
    }

    protected String tf(String tf) {
        lastError = "true".equals(tf) ? "0" : "101";
        return tf;
    }

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
     * @see
     * fi.beans.scorm.SCORM12APIInterface#LMSGetErrorString(java.lang.String)
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
    public String LMSGetDiagnostic(String iErrorCode) {
        return "";
    }

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

    public void setUser(User u, SchoolClass sc) {
        if (u != user) {
            if (!isReview() && !isBrowse()) {
                lessonLocation = null; // in reviewmode de sco is de baas over de lessonlocation, 
            }
            user = u;
            cls = sc;
        }
    }

    public String getLessonMode() {
        return lessonMode;
    }

    public void setLessonMode(String lessonMode) {
        if (NORMAL.equals(lessonMode)) {
            lessonMode = defaultLessonMode;
        }
        this.lessonMode = lessonMode;
        //lessonLocation = null;
    }

    public AppletData getAppletData() {
        if (appletData == null) {
            try {
                appletData = PersistenceFacade.instance().getAppletData(appletID);
            } catch (PersistenceException e) {
                LOG.log(Level.SEVERE,null,e);
            }
        }
        return appletData;
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
    public String GetValue(String iDataModelElement) {
        if (LESSON_MODE.equals(iDataModelElement)) {
            return ok(getLessonMode());
        }
        if (LAUNCH_DATA.equals(iDataModelElement)) {
            String value;
            Hashtable ld = getLaunchdata();
            value = (String) ld.get(LAUNCH_DATA);
            if (value == null) {
                value = getLaunchdataString();
            }
            return ok(value);

        }
        if (LESSON_LOCATION.equals(iDataModelElement)) {
            return ok(getLessonLocation());
        }
        if (CREDIT_STATUS.equals(iDataModelElement)) {
            return ok(getCreditStatus());
        }
        // session_ en total_time in 1.2 format.
        if (CMI_SESSION_TIME.equals(iDataModelElement) || CMI_TOTAL_TIME.equals(iDataModelElement)) {
            iDataModelElement = iDataModelElement.substring(4); // skip cmi.
            return ok(to2004Time(from1_2Time(dwo.LMSGetValue(this, user, cls, iDataModelElement))));
        }
// MC2 feature
        if(iDataModelElement.startsWith("dme."))
        {
        	if("dme.team".equals(iDataModelElement))
        	{	if(user.getInClass() != null)
        		return ok(user.getInClass().getName());
        	    if(cls != null) 
        	      return ok(cls.getName());
        	
        	} else if ("dme.abo_type".equals(iDataModelElement))
        	{
        	  try {
              return ok(DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getSchool().getAboType().name());
              } catch (Exception e) {
                LOG.log(Level.WARNING, "get dme.abo_type", e);
                return ok(AboType.standard.name());
              }
        	} else if ("dme.authorization".equals(iDataModelElement)) {
        	  return ok(StoredRestManager.getInstance().getBasicAuthString());
        	} else if ("dme.sco_name".equals(iDataModelElement)) {
        		return ok(getScoName());
        	}
        	return ko("");
        }
        return ok(dwo.LMSGetValue(this, user, cls, iDataModelElement));  // null -> 101 else ok()
    }

    public String getScoName() {
    	if (sc != null) return sc.getSco().getScoName();
		return "";
	}

	/**
     * Bepaal cmi.core.credit. Is gelijk aan is een assesment of niet.
     *
     * @return credit/no-credit
     */
    public String getCreditStatus() {
        Course c = getCourse();
        if (c != null) {
            ClassCourse link = c.link;
            if (link != null && link.getType() == ClassCourse.ASSESSMENT) {
                return CREDIT;
            }
        }
        return NO_CREDIT;
    }

    public String getLessonLocation() {
        if ((isReview() || isBrowse()) && locationOverride != null) {
            lessonLocation = locationOverride;
            locationOverride = null;
        }
        if (lessonLocation != null) {
            return lessonLocation;
        }
        return dwo.LMSGetValue(this, user, cls, LESSON_LOCATION);
    }

    /**
     * @return
     */
    private boolean isReview() {
        return REVIEW.equals(lessonMode);
    }

    private boolean isBrowse() {
        return BROWSE.equals(lessonMode);
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
    public String SetValue(String iDataModelElement, String iValue) {
        if (LESSON_MODE.equals(iDataModelElement)) {
            // TODO set error op 'readonly' variable
            return ko("false");
        }

        if (DWO_GOTO_SCONR.equals(iDataModelElement)) {
            if (NORMAL.equals(lessonMode)) {
                try {
                    return tf(Sco.gotoSco(iValue, this, course, sc));
                } catch (Exception e) {
		    			//LOG.log(Level.SEVERE,null,e);

                }
            }
            return ko("false");
        }

        if (NORMAL.equals(lessonMode)) {
            if (CMI_SESSION_TIME.equals(iDataModelElement)) {
                iDataModelElement = SESSION_TIME;
                iValue = to1_2Time(from2004Time(iValue)); // sessiontime in 1.2 format.
            }
	//			if(CMI_TOTAL_TIME.equals(iDataModelElement)) // NOT WRITABLE!
            //			{
            //				iDataModelElement = TOTAL_TIME;
            //				iValue = to1_2Time(from2004Time(iValue)); // totaltime in 1.2 format.
            //			}
// track location in normal mode.				
            if (LESSON_LOCATION.equals(iDataModelElement)) {
                lessonLocation = iValue;
            }

            return tf(dwo.LMSSetValue(this, user, cls, iDataModelElement, iValue));
        }

        if (isReview()) {
            boolean ok = getReviewable(iDataModelElement);
            if (ok) {
                String value = dwo.LMSSetValue(this, user, cls, iDataModelElement, iValue);
                firePropertyChange(iDataModelElement, null, iValue);
                return value;
            } else if (LESSON_LOCATION.equals(iDataModelElement)) {
                String last = lessonLocation;
                lessonLocation = iValue;
                firePropertyChange(LESSON_LOCATION, last, lessonLocation);
                return ok("true");
            }
        }
        // browse....
        return ko("false");
    }

    void firePropertyChange(String property, Object last,
            Object lessonLocation3) {
		// Stub
    }

    public User getUser() {
        return this.user;
    }

    private boolean getReviewable(String element) {
        if (CMI_SESSION_TIME.equals(element)) // not reviewable!
        {
            return false;
        }
        if (LESSON_LOCATION.equals(element)) // special case. 
        {
            return false;
        }
        if (COMPLETION_STATUS.equals(element)) {
            return true;
        }
        if (features == null) {
            features = getAppletData().getFeatures();
            if (features == null) {
                features = "";
            }
        }
        //System.out.println("Reviewable: "+(features.indexOf(REVIEWABLE)>=0));
        return features.indexOf(REVIEWABLE) >= 0;

    }

    /**
     * Returns the launchdata for the sco.
     *
     * @return The launchdata for the sco.
     */
    public Hashtable getLaunchdata() {
        if (launchdata == null) { //No data specified we must have an empty hashtable
            launchdata = new Hashtable();
        }
        return launchdata;
    }

    public String getLaunchdataString() {
        Hashtable ld = getLaunchdata();
        return (new StringCodeObject(ld)).toString();

    }

    public byte[] getLaunchdataBytes() {
        Map ld = new HashMap(getLaunchdata());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            OutputStream zip = new GZIPOutputStream(bos);
            Writer out = new OutputStreamWriter(zip, "UTF-8");
            jsonEncode(ld, out);
            out.close();
        } catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
        }
        return bos.toByteArray();
    }

	protected abstract void jsonEncode(Map ld, Writer out) throws IOException;
//	
//	
//	{
//		JSONEncoder.encode(ld, out);
//	}

    /**
     * Sets the launchdata for the sco.
     *
     * @param launchdata The launchdata for the sco.
     */
    public void setLaunchdata(Hashtable launchdata) {
        if (!isDataChanged()) {
            if (this.launchdata == null) {
                setDataChanged(launchdata != null);
            } else {
                setDataChanged(!this.launchdata.equals(launchdata));
            }
        }
        this.launchdata = launchdata;
    }

    public void setLaunchdataString(String ld) {
        setLaunchdata((Hashtable) StringCodeObject.decodeStringToObject(ld, null));
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
        if (!isCourseChanged()) {
            setCourseChanged(this.course != course);
        }
        this.course = course;
    }

    public boolean hasFeature(char f) {
        if (features != null) {
            return features.indexOf(f) >= 0;
        }
        AppletData data = getAppletData();
        if (data != null) {
            features = data.getFeatures();
            return features != null && features.indexOf(f) >= 0;
        }
        return false;
    }

    public SchoolClass getSchoolClass() {
      return cls;
    }

}
