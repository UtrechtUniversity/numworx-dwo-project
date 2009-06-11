// Source file: C:\\fi\\dwo\\parameters\\gui\\TreeParameterComponent.java

package fi.dwo.parameters.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import fi.beans.scorm.Parameter;
import fi.dwo.client.gui.DwoButton;

public class TreeParameterComponent extends ParameterComponent implements ActionListener {
    
    private DwoButton button;
    
    private boolean large;
    
    private Dimension minSize, maxSize;


    public TreeParameterComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue) {
    	this(parent, parameter, defaultValue, false);
    }
   /**
     * @param parent
     * @param parameter
     * @param defaultValue
     * @roseuid 425A5C0D00AB
     */
    public TreeParameterComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue, boolean isSub) {
        super(parent, parameter, defaultValue, isSub);
        
        button = new DwoButton("Test");
        
        FontMetrics fm = button.getFontMetrics(button.getFont());
        button.setSize(fm.stringWidth(button.getLabel()) + 20, fm.getHeight() + 10);
        button.setLocation(preLabel.getLocation().x + preLabel.getSize().width + 10, 1);
        button.setVisible(false);
        button.addActionListener(this);
        button.addFocusListener(this);
        this.add(button);
        button.setVisible(true);
        
        Component last = generatePostItems(button);
        minSize = new Dimension(last.getLocation().x + last.getSize().width + 5, button.getSize().height + 2);
        maxSize = new Dimension(last.getLocation().x + last.getSize().width + 5, button.getSize().height + 100);
        this.setSize(minSize);
        
        large = false;

    }

    /* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
	    if(e.getSource() == button) {
	        if(large) {
	            this.setSize(minSize);
	        } else {
	            this.setSize(maxSize);
	        }
	        large = !large;
	    }
	}
	
	
}