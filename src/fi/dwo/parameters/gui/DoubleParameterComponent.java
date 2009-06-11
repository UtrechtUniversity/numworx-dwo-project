// Source file: C:\\fi\\dwo\\parameters\\gui\\DoubleParameterComponent.java

package fi.dwo.parameters.gui;

import fi.beans.scorm.Parameter;

import java.awt.Component;
import java.util.Hashtable;

public class DoubleParameterComponent extends ParameterComponent {

    private DoubleTextField textField;
 
    
    public DoubleParameterComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue) {
    	this(parent, parameter, defaultValue, false);
    }
    /**
     * @param parent
     * @param parameter
     * @param defaultValue
     * @roseuid 425A5C2E02BF
     */
    public DoubleParameterComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue, boolean isSub) {
        super(parent, parameter, defaultValue, isSub);

        textField = new DoubleTextField();
        if(this.defaultValue.containsKey(parameter.getName())) {
            Double dblValue = (Double) this.defaultValue.get(parameter.getName());
            textField.setText(dblValue.toString());
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
            Double dblValue = (Double) this.defaultValue.get(parameter.getName());
            textField.setText(dblValue.toString());
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