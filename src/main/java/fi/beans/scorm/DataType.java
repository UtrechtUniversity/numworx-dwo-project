// Source file: C:\\fi\\beans\\scorm\\DataType.java

package fi.beans.scorm;

public class DataType implements DataTypeIF {
    private boolean mustTabSheet;

    private int size;

    /**
     * @roseuid 425E23C40399
     */
    public DataType() {
        size = -1;
        mustTabSheet = false;
        

    }

    public boolean getMustTabSheet() {
        return mustTabSheet;
    }

    public void setMustTabSheet(boolean mustTabSheet) {
        this.mustTabSheet = mustTabSheet;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}