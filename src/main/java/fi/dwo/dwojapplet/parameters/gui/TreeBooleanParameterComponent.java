//Source file: C:\\parameters\\fi\\dwo\\parameters\\gui\\TreeBooleanParameterComponent.java

package fi.dwo.dwojapplet.parameters.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import fi.beans.scorm.Parameter;


public class TreeBooleanParameterComponent extends BooleanParameterComponent implements ActionListener
{
    private DeleteButton deleteButton;
    
    public TreeBooleanParameterComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue) 
    {
        super(parent, parameter, defaultValue);
    }
    
    public TreeBooleanParameterComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue, boolean isSub) {
        super(parent, parameter, defaultValue, isSub);
    }

    /**
     * Calls the parent generatePostItems, and adds a delete-button.
     * @see fi.dwo.parameters.gui.ParameterComponent#generatePostItems(java.awt.Component)
     */
    protected Component generatePostItems(Component after) {
        Component last = super.generatePostItems(after);
        deleteButton = new DeleteButton();
        deleteButton.setLocation(last.getLocation().x + last.getSize().width + 10, 1);
        deleteButton.setVisible(false);
        deleteButton.addActionListener(this);
        this.add(deleteButton);
        last = deleteButton;
        
        return last;
    }
	public void setColor(Color c) {
	    super.setColor(c);

	    if(deleteButton != null) {
	        deleteButton.setBackground(c);
		}
	}
	
	protected void isFocussed() {
	    super.isFocussed();

	    if(deleteButton != null) {
		    deleteButton.setVisible(true);
		    deleteButton.repaint();
		}
	}
	
	public void unFocus() {
	    super.unFocus();
		if(deleteButton != null) {
		    deleteButton.setVisible(false);
		    deleteButton.repaint();
		}	    
	}
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == deleteButton) {
            if(parent instanceof DeleteTreeItemIF) {
                ((DeleteTreeItemIF) parent).deleteItem(this);
            }
        }
    }

}
