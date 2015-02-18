//Source file: C:\\parameters\\fi\\beans\\scorm\\TreeParameter.java
package fi.beans.scorm;

public class TreeParameter extends ExtendedParameter {

    private String itemCountName;
    private String itemLabel;

    public TreeParameter(String name, String preLabel) {
        super(name, preLabel, new ScormTree());
    }

    public TreeParameter(String name, String preLabel, DataTypeIF type) {
        super(name, preLabel, type);
    }

    /**
     * @return Returns the itemCountName.
     */
    public String getItemCountName() {
        return itemCountName;
    }

    /**
     * @param itemCountName The itemCountName to set.
     */
    public void setItemCountName(String itemCountName) {
        this.itemCountName = itemCountName;
    }

    /**
     * @return Returns the itemLabel.
     */
    public String getItemLabel() {
        return itemLabel;
    }

    /**
     * @param itemLabel The itemLabel to set.
     */
    public void setItemLabel(String itemLabel) {
        this.itemLabel = itemLabel;
    }
    /* (non-Javadoc)
     * @see fi.beans.scorm.ExtendedParameter#addSubParameter(fi.beans.scorm.Parameter)
     */

    @Override
    public void addSubParameter(Parameter subParameter) {
        super.addSubParameter(subParameter);
        if ((getSubParameters().length > 1) || subParameter.getType().getMustTabSheet()) {
            getType().setMustTabSheet(true);
        }
    }
    /* (non-Javadoc)
     * @see fi.beans.scorm.ExtendedParameter#setSubParameters(fi.beans.scorm.Parameter[])
     */

    @Override
    public void setSubParameters(Parameter[] subParameters) {
        super.setSubParameters(subParameters);
        if (subParameters.length > 1) {
            getType().setMustTabSheet(true);
        } else if ((subParameters.length == 1) && (subParameters[0].getType().getMustTabSheet())) {
            getType().setMustTabSheet(true);
        }
    }
}
