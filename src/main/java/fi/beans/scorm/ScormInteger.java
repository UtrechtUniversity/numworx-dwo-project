// Source file: C:\\fi\\beans\\scorm\\ScormInteger.java
package fi.beans.scorm;

public class ScormInteger extends DataType {

    private int min = Integer.MIN_VALUE;
    private int max = Integer.MAX_VALUE;

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    /**
     * @roseuid 425E23C5009C
     */
    public ScormInteger() {
        setMustTabSheet(false);
    }

}
