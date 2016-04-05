package fi.dwo.dwojapplet.gui.action;

import fi.beans.dwomaccess.JSONEncoder;
import fi.beans.scorm.PartialScoreIF;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.AppletData;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.Sco;
import static fi.dwo.dwojapplet.domain.ScoBase.LAUNCH_DATA;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.gui.GuiConstants;
import java.applet.Applet;
import java.applet.AppletContext;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

class WrapSco extends Sco {
    private static final Logger LOG = Logger.getLogger(WrapSco.class.getName());

    /**
     * @param delegate
     */
    WrapSco(Sco delegate) {
        this.delegate = delegate;
        setEditor(delegate);
    }

    private Sco delegate;

    @Override
    public Sco unwrap() {
        return delegate;
    }

    /**
     * @param listener
     * @see
     * fi.dwo.client.domain.Sco#addPropertyChangeListener(java.beans.PropertyChangeListener)
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        delegate.addPropertyChangeListener(listener);
    }

    /**
     * @param propertyName
     * @param listener
     * @see fi.dwo.client.domain.Sco#addPropertyChangeListener(java.lang.String,
     * java.beans.PropertyChangeListener)
     */
    @Override
    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        delegate.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * @return @see fi.dwo.client.domain.ScoBase#getID()
     */
    @Override
    public int getID() {
        return delegate.getID();
    }

    /**
     * @param listener
     * @see
     * fi.dwo.client.domain.Sco#removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        delegate.removePropertyChangeListener(listener);
    }

    /**
     * @param propertyName
     * @param listener
     * @see
     * fi.dwo.client.domain.Sco#removePropertyChangeListener(java.lang.String,
     * java.beans.PropertyChangeListener)
     */
    @Override
    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        delegate.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * @return @see fi.dwo.client.domain.Sco#isShowScore()
     */
    @Override
    public boolean isShowScore() {
        return delegate.isShowScore();
    }

    /**
     * @return @see fi.dwo.client.domain.Sco#getShowScore()
     */
    @Override
    public Boolean getShowScore() {
        return delegate.getShowScore();
    }

    /**
     * @param key
     * @return
     * @see fi.beans.scorm.ScormAdapter#LMSGetValue(java.lang.String)
     */
    @Override
    public String LMSGetValue(String key) {
        System.out.println("GetValue " + key);
        return super.LMSGetValue(key);
    }

    /**
     * @param showScore
     * @see fi.dwo.client.domain.Sco#setShowScore(boolean)
     */
    @Override
    public void setShowScore(boolean showScore) {
        delegate.setShowScore(showScore);
    }

    /**
     * @param showScore
     * @see fi.dwo.client.domain.Sco#setShowScore(java.lang.Boolean)
     */
    @Override
    public void setShowScore(Boolean showScore) {
        delegate.setShowScore(showScore);
    }

    /**
     * @param iParam
     * @return
     * @see fi.dwo.client.domain.ScoBase#LMSCommit(java.lang.String)
     */
    @Override
    public String LMSCommit(String iParam) {
        return super.LMSCommit(iParam);
    }

    /**
     * @return @see fi.dwo.client.domain.Sco#getPartialScoreIF()
     */
    @Override
    public PartialScoreIF getPartialScoreIF() {
        return delegate.getPartialScoreIF();
    }

    /**
     * @return @see fi.dwo.client.domain.ScoBase#LMSGetLastError()
     */
    @Override
    public String LMSGetLastError() {
        return super.LMSGetLastError();
    }

    /**
     * @param iErrorCode
     * @return
     * @see fi.dwo.client.domain.ScoBase#LMSGetErrorString(java.lang.String)
     */
    @Override
    public String LMSGetErrorString(String iErrorCode) {
        return super.LMSGetErrorString(iErrorCode);
    }

    /**
     * @return @see fi.dwo.client.domain.Sco#getName()
     */
    @Override
    public String getName() {
        return delegate.getName();
    }

    /**
     * @param key
     * @param value
     * @return
     * @see fi.beans.scorm.ScormAdapter#LMSSetValue(java.lang.String,
     * java.lang.String)
     */
    @Override
    public String LMSSetValue(String key, String value) {
        return super.LMSSetValue(key, value);
    }

    /**
     * @return @see fi.dwo.client.domain.Sco#getScoName()
     */
    @Override
    public String getScoName() {
        return delegate.getScoName();
    }

    /**
     * @param iErrorCode
     * @return
     * @see fi.dwo.client.domain.ScoBase#LMSGetDiagnostic(java.lang.String)
     */
    @Override
    public String LMSGetDiagnostic(String iErrorCode) {
        return super.LMSGetDiagnostic(iErrorCode);
    }

    /**
     * @param iParam
     * @return
     * @see fi.dwo.client.domain.Sco#LMSInitialize(java.lang.String)
     */
    @Override
    public String LMSInitialize(String iParam) {
        System.out.println("Initialize");
        super.LMSInitialize(iParam);
        return "true";
    }

    /**
     * @return @see fi.dwo.client.domain.ScoBase#isDataChanged()
     */
    @Override
    public boolean isDataChanged() {
        return delegate.isDataChanged();
    }

    /**
     * @param dataChanged
     * @see fi.dwo.client.domain.ScoBase#setDataChanged(boolean)
     */
    @Override
    public void setDataChanged(boolean dataChanged) {
        delegate.setDataChanged(dataChanged);
    }

    /**
     * @param iParam
     * @return
     * @see fi.dwo.client.domain.Sco#LMSFinish(java.lang.String)
     */
    @Override
    public String LMSFinish(String iParam) {
        return super.LMSFinish(iParam);
    }

    /**
     * @param courseChanged
     * @see fi.dwo.client.domain.ScoBase#setCourseChanged(boolean)
     */
    @Override
    public void setCourseChanged(boolean courseChanged) {
        delegate.setCourseChanged(courseChanged);
    }

    /**
     * @return @see fi.dwo.client.domain.ScoBase#getScoID()
     */
    @Override
    public int getScoID() {
        return delegate.getScoID();
    }

    /**
     * @param scoID
     * @see fi.dwo.client.domain.ScoBase#setScoID(int)
     */
    @Override
    public void setScoID(int scoID) {
        delegate.setScoID(scoID);
    }

    /**
     * @param u
     * @see fi.dwo.client.domain.ScoBase#setUser(fi.dwo.client.domain.User)
     */
    @Override
    public void setUser(User u) {
        super.setUser(u);
    }

    /**
     * @return @see fi.dwo.client.domain.ScoBase#getLessonMode()
     */
    @Override
    public String getLessonMode() {
        return super.getLessonMode();
    }

    /**
     * @param lessonMode
     * @see fi.dwo.client.domain.ScoBase#setLessonMode(java.lang.String)
     */
    @Override
    public void setLessonMode(String lessonMode) {
        super.setLessonMode(lessonMode);
    }

    /**
     * @return @see fi.dwo.client.domain.ScoBase#getAppletData()
     */
    @Override
    public AppletData getAppletData() {
        return delegate.getAppletData();
    }

    /**
     * @param iDataModelElement
     * @return
     * @see fi.dwo.client.domain.ScoBase#GetValue(java.lang.String)
     */
    @Override
    public String GetValue(String iDataModelElement) {
        System.out.println("wrap.GetValue " + iDataModelElement);
        if (LAUNCH_DATA.equals(iDataModelElement)) {
            String value = getLaunchdataString();
            return ok(value);
        }

        return super.GetValue(iDataModelElement);
    }

    /**
     * @return @see fi.dwo.client.domain.ScoBase#getCreditStatus()
     */
    @Override
    public String getCreditStatus() {
        return delegate.getCreditStatus();
    }

    /**
     * @param iDataModelElement
     * @param iValue
     * @return
     * @see fi.dwo.client.domain.ScoBase#SetValue(java.lang.String,
     * java.lang.String)
     */
    @Override
    public String SetValue(String iDataModelElement, String iValue) {
        return super.SetValue(iDataModelElement, iValue);
    }

    /**
     * @param other
     * @return
     * @see fi.dwo.client.domain.Sco#isMergable(fi.dwo.client.domain.Sco)
     */
    @Override
    public boolean isMergable(Sco other) {
        return delegate.isMergable(other);
    }

    /**
     * @return @see fi.dwo.client.domain.Sco#getAppletID()
     */
    @Override
    public int getAppletID() {
        return delegate.getAppletID();
    }

    /**
     * @param appletID
     * @see fi.dwo.client.domain.Sco#setAppletID(int)
     */
    @Override
    public void setAppletID(int appletID) {
        delegate.setAppletID(appletID);
    }

    /**
     * @return @see fi.dwo.client.domain.Sco#getDescription()
     */
    @Override
    public String getDescription() {
        return delegate.getDescription();
    }

    /**
     * @param description
     * @see fi.dwo.client.domain.Sco#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(String description) {
        delegate.setDescription(description);
    }

    /**
     * @return @see fi.dwo.client.domain.ScoBase#getUser()
     */
    @Override
    public User getUser() {
        return super.getUser();
    }

    /**
     * @param name
     * @see fi.dwo.client.domain.Sco#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        delegate.setName(name);
    }

    private Hashtable wrapdata = new Hashtable();

    /**
     * @return @see fi.dwo.client.domain.ScoBase#getLaunchdata()
     */
    @Override
    public Hashtable getLaunchdata() {
        return wrapdata;
    }

    /**
     * @return @see fi.dwo.client.domain.ScoBase#getLaunchdataString()
     */
    @Override
    public String getLaunchdataString() {
        Map ld = new HashMap(getEditLaunchdata());
        StringWriter bos = new StringWriter();
        try {
            Writer out = bos;
            JSONEncoder.encode(ld, out);
            out.close();
        } catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
        }
        return bos.toString();
    }

    /**
     * @param width
     * @param height
     * @see fi.dwo.client.domain.Sco#appletResize(int, int)
     */
    @Override
    public void appletResize(int width, int height) {
        super.appletResize(width, height);
    }

    /**
     * @return @see fi.dwo.client.domain.ScoBase#getLaunchdataBytes()
     */
    @Override
    public byte[] getLaunchdataBytes() {
        return delegate.getLaunchdataBytes();
    }

    /**
     * @return @see fi.dwo.client.domain.Sco#getAppletContext()
     */
    @Override
    public AppletContext getAppletContext() {
        return super.getAppletContext();
    }

    /**
     * @param launchdata
     * @see fi.dwo.client.domain.ScoBase#setLaunchdata(java.util.Hashtable)
     */
    @Override
    public void setLaunchdata(Hashtable launchdata) {
        delegate.setLaunchdata(launchdata);
    }

    /**
     * @return @see fi.dwo.client.domain.Sco#getCodeBase()
     */
    @Override
    public URL getCodeBase() {
        return super.getCodeBase();
    }

    /**
     * @param ld
     * @see fi.dwo.client.domain.ScoBase#setLaunchdataString(java.lang.String)
     */
    @Override
    public void setLaunchdataString(String ld) {
    }

    /**
     * @return @see fi.dwo.client.domain.Sco#getDocumentBase()
     */
    @Override
    public URL getDocumentBase() {
        return super.getDocumentBase();
    }

    /**
     * @return @see fi.dwo.client.domain.ScoBase#getCourse()
     */
    @Override
    public Course getCourse() {
        return delegate.getCourse();
    }

    /**
     * @param course
     * @see fi.dwo.client.domain.ScoBase#setCourse(fi.dwo.client.domain.Course)
     */
    @Override
    public void setCourse(Course course) {
        delegate.setCourse(course);
    }

    /**
     * @param f
     * @return
     * @see fi.dwo.client.domain.ScoBase#hasFeature(char)
     */
    @Override
    public boolean hasFeature(char f) {
        return delegate.hasFeature(f);
    }

    /**
     * @param name
     * @return
     * @see fi.dwo.client.domain.Sco#getParameter(java.lang.String)
     */
    @Override
    public String getParameter(String name) {
        if ("url".equals(name)) {
            String language = TextMapper.getLanguage();
            language = "?locale=" + language;
            return GuiConstants.PLAYER + language + "#cmi.launch_data:" + getScoID(); // FIXME correct url.
        }
        if ("debug".equals(name)) {
            return null;
        }
        return super.getParameter(name);
    }

    /**
     *
     * @see fi.dwo.client.domain.Sco#end()
     */
    @Override
    public void end() {
        dwo.setCurrentSco(null);
        super.end();
    }

    /**
     *
     * @see fi.dwo.client.domain.Sco#endWithoutSaving()
     */
    @Override
    public void endWithoutSaving() {
        super.endWithoutSaving();
    }

    /**
     * @return @see fi.dwo.client.domain.Sco#getTitle()
     */
    @Override
    public String getTitle() {
        return delegate.getTitle();
    }

    /**
     * @return @see fi.dwo.client.domain.Sco#getParentTitle()
     */
    @Override
    public String getParentTitle() {
        return delegate.getParentTitle();
    }

    /**
     * @return @see fi.dwo.client.domain.Sco#getChildTitle()
     */
    @Override
    public String getChildTitle() {
        return delegate.getChildTitle();
    }

    /**
     * @return @see fi.dwo.client.domain.Sco#getOrderAscTitle()
     */
    @Override
    public String getOrderAscTitle() {
        return delegate.getOrderAscTitle();
    }

    /**
     * @return @see fi.dwo.client.domain.Sco#getOrderDescTitle()
     */
    @Override
    public String getOrderDescTitle() {
        return delegate.getOrderDescTitle();
    }

    /**
     * @return @see fi.dwo.client.domain.Sco#getSequencenr()
     */
    @Override
    public int getSequencenr() {
        return delegate.getSequencenr();
    }

    /**
     * @return @see fi.dwo.client.domain.Sco#getToolTip()
     */
    @Override
    public String getToolTip() {
        return delegate.getToolTip();
    }

    /**
     * @param o
     * @return
     * @see fi.dwo.client.domain.Sco#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Object o) {
        return delegate.compareTo(o);
    }

    /**
     * @param arg0
     * @return
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object arg0) {
        return delegate.equals(arg0);
    }

    /**
     *
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void loadApplet() {
        Applet applet;
        Class<Applet> clazz;
        try {
            clazz = (Class<Applet>) Class.forName("fi.popupurlapplet.PopUpURLApplet");
            applet = clazz.newInstance();
        } catch (Exception e) {
            LOG.log(Level.SEVERE,null,e);
            applet = new Applet();
        }
        applet.setStub(this);
        super.setApplet(applet);
    }

    /**
     * @return @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    /**
     * @return @see fi.dwo.client.domain.ScoBase#isCourseChanged()
     */
    @Override
    public boolean isCourseChanged() {
        return delegate.isCourseChanged();
    }

    /**
     * @return @see fi.dwo.client.domain.Sco#isActive()
     */
    @Override
    public boolean isActive() {
        return delegate.isActive();
    }

    /**
     * @return @see fi.dwo.client.domain.Sco#isDeepestLevel()
     */
    @Override
    public boolean isDeepestLevel() {
        return delegate.isDeepestLevel();
    }

    /**
     * @return @see fi.dwo.client.domain.Sco#isHighestLevel()
     */
    @Override
    public boolean isHighestLevel() {
        return delegate.isHighestLevel();
    }

    /**
     * @param sequencenr
     * @see fi.dwo.client.domain.Sco#setSequencenr(int)
     */
    @Override
    public void setSequencenr(int sequencenr) {
        delegate.setSequencenr(sequencenr);
    }

    /**
     * @return @see fi.dwo.client.domain.Sco#toString()
     */
    @Override
    public String toString() {
        return delegate.toString();
    }

    /**
     * @param loc
     * @see fi.dwo.client.domain.Sco#setLocationOverride(java.lang.String)
     */
    @Override
    public void setLocationOverride(String loc) {
        delegate.setLocationOverride(loc);
    }

}
