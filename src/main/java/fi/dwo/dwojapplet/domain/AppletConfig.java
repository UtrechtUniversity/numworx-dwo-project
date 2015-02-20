// Source file: C:\\parameters\\fi\\dwo\\client\\domain\\AppletConfig.java
package fi.dwo.dwojapplet.domain;

/**
 * This class is responsible for containing the data of the AppletConfig.<br>
 * <br>
 * An AppletConfig is a configurated applet.<br>
 * An applet could have differend modes. (for example <code>nabouwen</code>).
 * <br>
 * Every mode of the applet is an AppletConfig.
 *
 * @author M.J.B. Kupers
 *
 */
public class AppletConfig {

    private int appletConfigID;

    private int appletID;

    private String launchdata;
    private String name;
    private String language = "nl";

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    public AppletConfig() {

    }

    /**
     * @return Returns the appletID.
     */
    public int getAppletID() {
        return appletID;
    }

    public int getID() {
        return appletConfigID;
    }

    /**
     * @return Returns the launchdata.
     */
    public String getLaunchdata() {
        return launchdata;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param appletID The appletID to set.
     */
    public void setAppletID(int appletID) {
        this.appletID = appletID;
    }

    /**
     * @param launchdata The launchdata to set.
     */
    public void setLaunchdata(String launchdata) {
        this.launchdata = launchdata;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param appletConfigID The appletConfigID to set.
     */
    public void setAppletConfigID(int appletConfigID) {
        this.appletConfigID = appletConfigID;
    }
}
