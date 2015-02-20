/*
 * Created on Apr 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fi.dwo.dwojapplet.parameters.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

/**
 * @author M.J.B. Kupers
 *
 */
public class TestTabSheet extends Panel implements TabSheetIF, ActionListener {

    private JButton button;
    
    private boolean large;
    
    public TestTabSheet() {
        super(null);
        this.setBackground(ParameterComponent.SELECTED_COLOR_2);
        button = new JButton("Test" + Math.random());
        
        FontMetrics fm = button.getFontMetrics(button.getFont());
        button.setSize(fm.stringWidth(button.getLabel()) + 20, fm.getHeight() + 10);
        button.setLocation(10 + 10, 1);
        button.setVisible(false);
        button.addActionListener(this);
        this.add(button);
        button.setVisible(true);
        
        Component last = button;
        minSize = new Dimension(last.getLocation().x + last.getSize().width + 5, button.getSize().height + 2);
        maxSize = new Dimension(last.getLocation().x + last.getSize().width + 5, button.getSize().height + 100);
        this.setSize(minSize);
        
        
        large = false;

        
    }
    
    private Dimension minSize, maxSize;
    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.TabSheetIF#getComponent()
     */
    @Override
    public Component getComponent() {
        return this;
    }
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
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
    
	/**
     * Returns the <i>current</i> size as the minimum size.
     * @see java.awt.Component#getMinimumSize()
     */
    @Override
    public Dimension getMinimumSize() {
        return super.getSize();
    }

    /**
     * Returns the <i>current</i> size as the preferred size.
     * @see java.awt.Component#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize() {
        return super.getSize();
    }

}
