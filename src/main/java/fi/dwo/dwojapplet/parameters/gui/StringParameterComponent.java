// Source file: C:\\fi\\dwo\\parameters\\gui\\StringParameterComponent.java

package fi.dwo.dwojapplet.parameters.gui;

import java.awt.Component;
import java.util.Hashtable;

import javax.swing.JTextField;

import fi.beans.scorm.Parameter;

public class StringParameterComponent extends ParameterComponent {
    
    private JTextField textField;

    public StringParameterComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue) {
    	this(parent, parameter, defaultValue, false);
    }
   /**
     * @param parent
     * @param parameter
     * @param defaultValue
     * @roseuid 42567A5601E4
     */
    public StringParameterComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue, boolean isSub) {
        super(parent, parameter, defaultValue, isSub);
        
        textField = new JTextField();

        if(this.defaultValue.containsKey(parameter.getName())) {
            textField.setText((String) this.defaultValue.get(parameter.getName()));
        }
        int size = parameter.getType().getSize();
        if(size <= 0) {
            //size = 20;
            size = 30;
        }
        textField.setSize(size * 8, 21);
        textField.setLocation(preLabel.getLocation().x + preLabel.getSize().width + 10, 1);
        textField.addFocusListener(this);
        textField.setVisible(false);
        this.add(textField);
        textField.setVisible(true);
        Component last = generatePostItems(textField);
        this.setSize(last.getLocation().x + last.getSize().width + 5, 23);
        

    }
    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ParameterComponentIF#reset()
     */
    public void reset() {
        if(this.defaultValue.containsKey(parameter.getName())) {
            textField.setText((String) this.defaultValue.get(parameter.getName()));
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