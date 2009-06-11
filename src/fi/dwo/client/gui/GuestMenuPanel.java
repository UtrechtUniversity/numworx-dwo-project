/*
 * Created on Feb 28, 2005
 *
 */
package fi.dwo.client.gui;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.system.TextMapper;

/**
 * This is a panel with the menu-options for a guest-user.
 * 
 * @author M.J.B. Kupers
 *  
 */
public class GuestMenuPanel extends Panel implements ActionListener {
    protected CenterPanel center;

    protected DwoButton mainMenuButton;

    /**
     * Creates a new GuestMenuPanel. The panel contains only a button for the
     * main menu.
     *  
     */
    public GuestMenuPanel() {
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setLayout(null);
        this.setSize(149, 481);

        /* Variables used to create items */
        FontMetrics fm;

        /* Add MainMenu button */
        mainMenuButton = new DwoButton(TextMapper.getText(TextMapper.GUIMNU_MAIN_MENU), GuiConstants.MAIN_BACKGROUND);
        fm = mainMenuButton.getFontMetrics(mainMenuButton.getFont());
        mainMenuButton.setSize(this.getSize().width - 20, fm.getHeight() + 10);
        mainMenuButton.setLocation(10, 10);
        mainMenuButton.addActionListener(this);
        mainMenuButton.setVisible(false);
        this.add(mainMenuButton);
        mainMenuButton.setVisible(true);

    }

    
    public void paint(Graphics g)
    {	if(GuiConstants.GUI_IMAGE_BG) {
	       	Point p = DwoHelper.getComponentLocation(this);
	       	g.drawImage(DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_COURSE),-p.x,-p.y,null);
    	}       
    	super.paint(g);
    }
     
    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mainMenuButton) {
            //center.loadCenter(new CourseChoisePanel());
        	center.loadCenter(GuiCreator.instance().getCourseChoisePanel());
        }
    }

    /**
     * Sets the centerpanel to communicate with.
     * 
     * @param centerPanel The centerPanel to communicate with.
     */
    public void setCenterPanel(CenterPanel centerPanel) {
        center = centerPanel;
    }
    
    public void hideClassList() {
        
    }
    
    public void showClassList() {
        
    }
    
    public void hideMainButton() {
    	mainMenuButton.setVisible(false);
    }
    

}