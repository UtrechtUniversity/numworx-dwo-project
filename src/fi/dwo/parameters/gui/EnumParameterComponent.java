package fi.dwo.parameters.gui;

import java.awt.Color;
import java.awt.Component;
import java.util.Hashtable;

import javax.swing.JComboBox;

import fi.beans.scorm.Parameter;
import fi.beans.scorm.ScormEnum;

public class EnumParameterComponent extends ParameterComponent implements ParameterComponentIF {

	private JComboBox comboBox;
	
	
	public EnumParameterComponent(ParameterComponentIF parent,
			Parameter parameter, Hashtable defaultValue, boolean isSub) {
		super(parent, parameter, defaultValue, isSub);
		ScormEnum e;
		e = (ScormEnum) parameter.getType();		
		comboBox = new JComboBox(e.getItems());

		if(defaultValue.containsKey(parameter.getName())) {
            comboBox.setSelectedItem(defaultValue.get(parameter.getName()));
        }
        int size = parameter.getType().getSize();
        if(size <= 0) {
            //size = 20;
            size = 30;
        }
        comboBox.setSize(size * 8, 21);
        comboBox.setLocation(preLabel.getLocation().x + preLabel.getSize().width + 10, 1);
        comboBox.addFocusListener(this);
        comboBox.setVisible(false);
        this.add(comboBox);
        comboBox.setVisible(true);
        Component last = generatePostItems(comboBox);
        this.setSize(last.getLocation().x + last.getSize().width + 5, 23);
	}

	public void addParameters(Hashtable parameters) {
        String value = comboBox.getSelectedItem().toString();
        addParameter(parameters, value);
	}

	public void reset() {
        if(defaultValue.containsKey(parameter.getName())) {
            comboBox.setSelectedItem(defaultValue.get(parameter.getName()));
        } else {
            comboBox.setSelectedIndex(0);
        }
    }

}
