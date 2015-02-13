// Source file: C:\\fi\\beans\\scorm\\Parameter.java

package fi.beans.scorm;

public class Parameter {

    protected String helpText;

    private String name;

    private String postLabel;

    private String preLabel;

    protected DataTypeIF type;

    /**
     * @roseuid 425E23C600BB
     */
    public Parameter(String name, String preLabel, DataTypeIF type) {
        this.name = name;
        this.preLabel = preLabel;
        this.type = type;
    }

    public String getHelpText() {
        return helpText;
    }

    public String getName() {
        return name;
    }

    public String getPostLabel() {
        return postLabel;
    }

    public String getPreLabel() {
        return preLabel;
    }

    public DataTypeIF getType() {
        return type;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPostLabel(String postLabel) {
        this.postLabel = postLabel;
    }

    public void setPreLabel(String preLabel) {
        this.preLabel = preLabel;
    }

    public void setType(DataTypeIF type) {
        this.type = type;
    }
}