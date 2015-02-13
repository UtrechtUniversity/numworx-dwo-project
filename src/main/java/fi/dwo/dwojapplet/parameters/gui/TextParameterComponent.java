// Source file: C:\\fi\\dwo\\parameters\\gui\\TextParameterComponent.java

package fi.dwo.dwojapplet.parameters.gui;

import java.awt.Component;
import java.util.Hashtable;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import fi.beans.scorm.Parameter;

public class TextParameterComponent extends ParameterComponent {

    private JTextArea textArea;

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

        textArea = new JTextArea("", 1, 1 );

        if(this.defaultValue.containsKey(parameter.getName())) {
            textArea.setText((String) this.defaultValue.get(parameter.getName()));
        }
        int size = parameter.getType().getSize();
        if(size <= 0) {
            size = 20;
        }
        textArea.addFocusListener(this);
        JScrollPane pane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setLocation(preLabel.getLocation().x + preLabel.getSize().width + 10, 1);
        pane.setSize(size * 8, 84);
		this.add(pane);
        Component last = generatePostItems(pane);
        this.setSize(last.getLocation().x + last.getSize().width + 5, pane.getSize().height + 2);
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