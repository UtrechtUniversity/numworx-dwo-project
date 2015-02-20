//Source file: C:\\parameters\\fi\\dwo\\parameters\\gui\\TreeTabSheet.java
package fi.dwo.dwojapplet.parameters.gui;

import java.awt.Component;
import java.util.Hashtable;

import fi.beans.scorm.Parameter;

public class TreeTabSheet extends ParameterComponent implements TabSheetIF {

    public TreeTabSheet(ParameterComponentIF parent, Parameter parameter, Hashtable defaultValue) {
        super(parent, parameter, defaultValue, false);
        if (preLabel != null) {
            remove(preLabel);
        }

        if (postLabel != null) {
            remove(postLabel);
        }

        if (helpButton != null) {
            remove(helpButton);
        }

    }

    @Override
    public Component getComponent() {
        return this;
    }

}
