// Source file: C:\\parameters\\fi\\dwo\\client\\domain\\AppletConfig.java

package fi.dwo.client.domain;

/**
 * This class is responsible for containing the data of the AppletConfig.<br>
 * <br>
 * An AppletConfig is a configurated applet.<br>
 * An applet could have differend modes. (for example <code>nabouwen</code>). <br>
 * Every mode of the applet is an AppletConfig.
 * @author M.J.B. Kupers
 *
 */
public class DwoProfile {

    private int dwoProfileID;
    private String description;
    private String text;
    private String name;

    public DwoProfile() {

    }

    /**
     * @return Returns the dwoProfileID.
     */
    public int getID() {
        return dwoProfileID;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * @return Returns the text.
     */
    public String getText() {
        return text;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param dwoProfileID
     *            The dwoProfileID to set.
     */
    public void setID(int dwoProfileID) {
        this.dwoProfileID = dwoProfileID;
    }

    /**
     * @param text
     *            The text to set.
     */
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * @param description
     *            The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    
}