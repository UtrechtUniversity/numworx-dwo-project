// Source file: C:\\fi\\beans\\scorm\\ScormTree.java
package fi.beans.scorm;

public class ScormTree extends DataType {

    private int maxItems;

    /**
     * @roseuid 425E23C501A5
     */
    public ScormTree() {
        setMustTabSheet(false);
        maxItems = -1;
    }

    public int getMaxItems() {
        return maxItems;
    }

    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }
}
