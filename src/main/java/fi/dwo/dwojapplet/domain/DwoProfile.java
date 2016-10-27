// Source file: C:\\parameters\\fi\\dwo\\client\\domain\\AppletConfig.java
package fi.dwo.dwojapplet.domain;

import fi.dwo.dwojapplet.gui.GuiCreator;

/**
 * This class is responsible for containing the DWO configuration. Singleton.
 *
 * @author M.J.B. Kupers
 *
 */
public class DwoProfile implements Descriptor {

    public static char READONLY = 'r';
    public static char PREVIEW = 'p';
    // Limited is dat je niet als gast en alleen met "goedgekeurde" schoolid's er in mag.
    // Goedgekeurd is een "school.properties" bestand.
    public static char LIMITED = 'l'; // goed voor rekenwise en consorten.
    private int dwoProfileID;
    private String description;
    private String text;
    private String name;
    private static String rights = "";

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
     * @deprecated gebruik {@link #getHeader()}
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return Returns the text.
     */
    @Override
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
     * @param dwoProfileID The dwoProfileID to set.
     */
    public void setID(int dwoProfileID) {
        this.dwoProfileID = dwoProfileID;
    }

    /**
     * @param text The text to set.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the rights
     */
    public static String getRights() {
        return rights;
    }

    /**
     * @param rights the rights to set
     */
    public static void setRights(String rights) {
        DwoProfile.rights = rights;
    }

    public static boolean hasRight(char right) {
        return rights.indexOf(right) >= 0;
    }

    @Override
    public String getHeader() {
        return getDescription();
    }

    /**
     * get Courses. Note: layer inconsistency: domain/gui
     * @return 
     */
    @Override
    public CourseMap[] getChildren() {
        return GuiCreator.instance().getCourseList();
    }
}
