// Source file: C:\\fi\\beans\\scorm\\ScormAppletIF.java
package fi.beans.scorm;

import java.util.Hashtable;

public interface ScormAppletIF {

    /**
     * @return java.lang.String
     * @roseuid 425395280290
     */
    public String getState();

    /**
     * @param state
     * @roseuid 4253952E0271
     */
    public void setState(String state);

    public void stopSco();

    /**
     * @return boolean
     * @roseuid 4253958A0203
     */
    public boolean hasEditMode();

    /**
     * @param launchdata
     * @return fi.beans.scorm.ScormEditComponentIF
     */
    public ScormEditComponentIF getEditComponent(Hashtable launchdata);

    /**
     * @return fi.beans.scorm.Parameter[]
     * @roseuid 42539737036B
     */
    public Parameter[] getEditableParameters();

    public Parameter[] getAllParameters();
}
