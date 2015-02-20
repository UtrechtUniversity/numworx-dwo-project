// Source file: C:\\fi\\dwo\\parameters\\gui\\IntegerParameterComponent.java
package fi.dwo.dwojapplet.parameters.gui;

import java.awt.Component;
import java.util.Hashtable;

import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import fi.beans.scorm.Parameter;
import fi.beans.scorm.ScormInteger;

public class IntegerParameterComponent extends ParameterComponent {

    protected JFormattedTextField textField;
    protected Number n;

    public IntegerParameterComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue) {
        this(parent, parameter, defaultValue, false);
    }

    /**
     * @param parent
     * @param parameter
     * @param defaultValue
     * @param isSub
     * @roseuid 42567A550139
     */
    public IntegerParameterComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue, boolean isSub) {
        super(parent, parameter, defaultValue, isSub);

        ScormInteger i = (ScormInteger) parameter.getType();
        NumberFormatter numberFormatter = new NumberFormatter();
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setAllowsInvalid(false);
        numberFormatter.setMinimum(new Integer(i.getMin()));
        numberFormatter.setMaximum(new Integer(i.getMax()));
        DefaultFormatterFactory defaultFormatterFactory = new DefaultFormatterFactory();
        defaultFormatterFactory.setDefaultFormatter(numberFormatter);

        textField = new JFormattedTextField();
        textField.setFormatterFactory(defaultFormatterFactory);
        int n = Math.max(Math.min(0, i.getMax()), i.getMin());
        textField.setValue(this.n = new Integer(n));

        if (this.defaultValue.containsKey(parameter.getName())) {
            Object object = this.defaultValue.get(parameter.getName());
            textField.setText(object.toString());
        }

        int size = parameter.getType().getSize();
        if (size <= 0) {
            size = 20;
        }
        textField.setColumns(size);
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
    @Override
    public void reset() {
        if (this.defaultValue.containsKey(parameter.getName())) {
            Object intValue = this.defaultValue.get(parameter.getName());
            textField.setText(intValue.toString());
        } else {
            textField.setValue(n);
        }
    }

    /**
     * Adds the value of the textfield to the hashtable.
     *
     * @param parameters
     * @see
     * fi.dwo.parameters.gui.ParameterComponentIF#addParameters(java.util.Hashtable)
     */
    @Override
    public void addParameters(Hashtable parameters) {
        String value = textField.getText();
        addParameter(parameters, value);
    }

}
