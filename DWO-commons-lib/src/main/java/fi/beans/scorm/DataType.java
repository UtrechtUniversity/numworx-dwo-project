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

    @Override
    public boolean getMustTabSheet() {
        return mustTabSheet;
    }

    @Override
    public void setMustTabSheet(boolean mustTabSheet) {
        this.mustTabSheet = mustTabSheet;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }
}
