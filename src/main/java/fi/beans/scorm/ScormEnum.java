package fi.beans.scorm;

/**
 * Enumerated String. In DWO wordt dit een ComboBox.
 *
 * @author Velth101
 *
 */
public class ScormEnum extends ScormString {

    private static final String EMPTY[] = new String[]{""};
    private String[] items = EMPTY;

    public ScormEnum() {
        super();
    }

    public ScormEnum(String[] items) {
        super();
        this.items = items;
    }

    public String[] getItems() {
        return items;
    }

    public void setItems(String[] items) {
        if (items == null) {
            items = EMPTY;	// nooit null!
        }
        this.items = items;
    }

}
