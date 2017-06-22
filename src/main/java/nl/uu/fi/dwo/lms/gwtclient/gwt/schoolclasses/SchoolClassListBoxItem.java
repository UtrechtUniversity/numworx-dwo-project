package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

/**
 * SchoolClassListBoxItem for ListBoxes and Cell renderings.
 * 
 * @author G.A.J. van der Plas
 */
public class SchoolClassListBoxItem {

    private String key; //unique
    private String schoolclassName;

    public SchoolClassListBoxItem(String aKey, String value) {
        key = aKey;
        schoolclassName = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String aKey) {
        key = aKey;
    }

    /**
     * @return the schoolclassName
     */
    public String getSchoolclassName() {
        return schoolclassName;
    }

    /**
     * @param schoolclassName the schoolclassName to set
     */
    public void setSchoolclassName(String schoolclassName) {
        this.schoolclassName = schoolclassName;
    }
}
