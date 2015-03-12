// Source file: C:\\fi\\beans\\scorm\\ExtendedParameter.java
package fi.beans.scorm;

public class ExtendedParameter extends Parameter {

    private Parameter subParameters[];

    /**
     * @param name
     * @param preLabel
     * @param type
     * @roseuid 425E23C60157
     */
    public ExtendedParameter(String name, String preLabel, DataTypeIF type) {
        super(name, preLabel, type);
        subParameters = new Parameter[0];
    }

    public Parameter[] getSubParameters() {
        return subParameters;
    }

    public void setSubParameters(Parameter[] subParameters) {
        this.subParameters = subParameters;
    }

    public void addSubParameter(Parameter subParameter) {
        Parameter[] newSub = new Parameter[subParameters.length + 1];
        System.arraycopy(subParameters, 0, newSub, 0, subParameters.length);
        newSub[newSub.length - 1] = subParameter;
        subParameters = newSub;
    }

    @Override
    public String getHelpText() {
        if (type instanceof ScormGroup) {
            String txt = "";
            String tmp;

            if ((helpText != null) && (!helpText.equals(""))) {
                txt = helpText;
                txt += "\n\n";
            }
            for (Parameter subParameter : subParameters) {
                tmp = subParameter.getHelpText();
                if ((tmp != null) && (!tmp.equals(""))) {
                    txt += subParameter.getPreLabel() + ":\n";
                    txt += tmp + "\n";
                    txt += "\n\n";
                }
            }
            return txt;
        } else {
            return helpText;
        }
    }

}
