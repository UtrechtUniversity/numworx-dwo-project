// Source file: C:\\fi\\dwo\\parameters\\gui\\IntegerParameterComponent.java

package fi.dwo.parameters.gui;

import java.awt.Component;
import java.util.Hashtable;

import fi.beans.scorm.Parameter;

public class IntegerParameterComponent extends ParameterComponent {
    protected IntegerTextField textField;

    
    public IntegerParameterComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue) {
    	this(parent, parameter, defaultValue, false);
    }
    /**
     * @param parent
     * @param parameter
     * @param defaultValue
     * @roseuid 42567A550139
     */
    public IntegerParameterComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue, boolean isSub) {
        super(parent, parameter, defaultValue, isSub);

        
        textField = new IntegerTextField();
        
        if(this.defaultValue.containsKey(parameter.getName())) {
            Object object = this.defaultValue.get(parameter.getName());
			textField.setText(object.toString());
        }
        
        int size = parameter.getType().getSize();
        if(size <= 0) {
            size = 20;
        }
        textField.setSize(size * 8, 21);
        textField.setLocation(preLabel.getLocation().x + preLabel.getSize().width + 10, 1);
        textField.addFocusListener(this);
        textField.setVisible(false);
        this.add(textField);
        textField.setVisible(true);
        Component last = generatePostItems(textField);
        this.setSize(last.getLocation().x + last.getSize().width + 5, Math.max(textField.getSize().height + 2, last.getSize().height + 2));
        

    }

    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ParameterComponentIF#reset()
     */
    public void reset() {
        if(this.defaultValue.containsKey(parameter.getName())) {
            Object intValue = this.defaultValue.get(parameter.getName());
            textField.setText(intValue.toString());
        } else {
            textField.setText("");
        }
    }
    
    /**
     * Adds the value of the textfield to the hashtable.
     * @see fi.dwo.parameters.gui.ParameterComponentIF#addParameters(java.util.Hashtable)
     */
    public void addParameters(Hashtable parameters) {
        String value = textField.getText();
        addParameter(parameters, value);
    }
    
}