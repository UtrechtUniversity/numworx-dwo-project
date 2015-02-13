// Source file: C:\\fi\\beans\\scorm\\ScormEditComponentIF.java

package fi.beans.scorm;

import java.awt.Component;
import java.util.Hashtable;

public interface ScormEditComponentIF {

    /**
     * @return java.awt.Component
     * @roseuid 425A4E340119
     */
    public Component getComponent();

    /**
     * @return java.lang.String
     * @roseuid 425A4E4102CE
     */
    public Hashtable getLaunchData();
    
    public void setState(Hashtable launchData);
    
    public void end();
    
    public void reset();
}