// Source file: C:\\fi\\dwo\\parameters\\gui\\FormulaParameterComponent.java
package fi.dwo.dwojapplet.parameters.gui;

import fi.beans.scorm.Parameter;
import java.util.Hashtable;

public class FormulaParameterComponent extends StringParameterComponent {

    public FormulaParameterComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue) {
        this(parent, parameter, defaultValue, false);
    }

    /**
     * @param parent
     * @param parameter
     * @param defaultValue
     * @param isSub
     * @roseuid 425A5C990196
     */
    public FormulaParameterComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue, boolean isSub) {
        super(parent, parameter, defaultValue, isSub);

    }
}
