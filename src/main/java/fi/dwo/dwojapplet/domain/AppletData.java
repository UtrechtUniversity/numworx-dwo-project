package fi.dwo.dwojapplet.domain;

/**
 * Representatie van de mysql Applet table
 *
 * @author velth101
 *
 */
public class AppletData {

    private int id;
    private String appletName;
    private String className;
    private String jarName;
    private String features;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppletName() {
        return appletName;
    }

    public void setAppletName(String appletName) {
        this.appletName = appletName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    /**
     * @return the features
     */
    public String getFeatures() {
        return features;
    }

    /**
     * @param features the features to set
     */
    public void setFeatures(String features) {
        this.features = features;
    }

}
