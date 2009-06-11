// Source file: C:\\fi\\dwo\\parameters\\gui\\TextParameterComponent.java

package fi.dwo.parameters.gui;

import java.awt.Component;
import java.awt.TextArea;
import java.util.Hashtable;

import fi.beans.scorm.Parameter;

public class TextParameterComponent extends ParameterComponent {

    private TextArea textArea;

    public TextParameterComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue) {
    	this(parent, parameter, defaultValue, false);
    }
   /**
     * @param parent
     * @param parameter
     * @param defaultValue
     * @roseuid 425A512602CE
     */
    public TextParameterComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue, boolean isSub) {
        super(parent, parameter, defaultValue, isSub);

        textArea = new TextArea("", 1, 1, TextArea.SCROLLBARS_VERTICAL_ONLY );

        if(this.defaultValue.containsKey(parameter.getName())) {
            textArea.setText((String) this.defaultValue.get(parameter.getName()));
        }
        int size = parameter.getType().getSize();
        if(size <= 0) {
            size = 20;
        }
        textArea.setSize(size * 8, 84);
        textArea.setLocation(preLabel.getLocation().x + preLabel.getSize().width + 10, 1);
        textArea.addFocusListener(this);
        textArea.setVisible(false);
        this.add(textArea);
        textArea.setVisible(true);
        Component last = generatePostItems(textArea);
        this.setSize(last.getLocation().x + last.getSize().width + 5, textArea.getSize().height + 2);
    }
    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ParameterComponentIF#reset()
     */
    public void reset() {
        if(this.defaultValue.containsKey(parameter.getName())) {
            textArea.setText((String) this.defaultValue.get(parameter.getName()));
        } else {
            textArea.setText("");
        }
    }
    
    /**
     * Adds the value of the textfield to the hashtable.
     * @see fi.dwo.parameters.gui.ParameterComponentIF#addParameters(java.util.Hashtable)
     */
    public void addParameters(Hashtable parameters) {
        String value = textArea.getText();
        addParameter(parameters, value);
    }
    
}