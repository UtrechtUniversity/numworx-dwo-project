//Source file: C:\\parameters\\fi\\dwo\\parameters\\gui\\TreeStringParameter.java
package fi.dwo.dwojapplet.parameters.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import fi.beans.scorm.Parameter;

public class TreeStringParameterComponent extends StringParameterComponent implements ActionListener {

    private DeleteButton deleteButton;

    public TreeStringParameterComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue) {
        super(parent, parameter, defaultValue);
    }

    public TreeStringParameterComponent(ParameterComponentIF parent,
            Parameter parameter, Hashtable defaultValue, boolean isSub) {
        super(parent, parameter, defaultValue, isSub);
    }

    /**
     * Calls the parent generatePostItems, and adds a delete-button.
     *
     * @param after
     * @return 
     * @see
     * fi.dwo.parameters.gui.ParameterComponent#generatePostItems(java.awt.Component)
     */
    @Override
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

    @Override
    public void setColor(Color c) {
        super.setColor(c);

        if (deleteButton != null) {
            deleteButton.setBackground(c);
        }
    }

    @Override
    protected void isFocussed() {
        super.isFocussed();

        if (deleteButton != null) {
            deleteButton.setVisible(true);
            deleteButton.repaint();
        }
    }

    @Override
    public void unFocus() {
        super.unFocus();
        if (deleteButton != null) {
            deleteButton.setVisible(false);
            deleteButton.repaint();
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == deleteButton) {
            if (parent instanceof DeleteTreeItemIF) {
                ((DeleteTreeItemIF) parent).deleteItem(this);
            }
        }
    }

}
