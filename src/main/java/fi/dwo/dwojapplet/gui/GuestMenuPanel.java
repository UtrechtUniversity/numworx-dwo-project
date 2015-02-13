/*
 * Created on Feb 28, 2005
 *
 */
package fi.dwo.dwojapplet.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;


import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.DwoIF;
import fi.dwo.client.system.TextMapper;

/**
 * This is a panel with the menu-options for a guest-user.
 * 
 * @author M.J.B. Kupers
 *  
 */
public class GuestMenuPanel extends JPanel implements ActionListener {

	/**
	 * Stretchable JButton voor het menu panel
	 * @author wim
	 *
	 */
	private static final int MARGIN = 16;
    public final class MenuPanelButton extends JButton {

		public  MenuPanelButton(String label) {
			super(label);
			init();
		}
		public  MenuPanelButton(Action action) {
			super(action);
			init();
		}

		private void init() {
			this.setMargin(new Insets(2,10,2,10));
			
// niet instelbaar!
	        this.setFont(new Font("Arial", Font.BOLD, 12));
		}

		/* (non-Javadoc)
		 * @see javax.swing.JComponent#getMaximumSize()
		 */
		public Dimension getMaximumSize() {
			return new Dimension(GuestMenuPanel.this.getWidth()-MARGIN,getPreferredSize().height);
		}
	}

	public static class HRuler extends JPanel {
    	public HRuler() {
    		setOpaque(false);
    		setBackground(Color.black);
    	}
    	public Dimension getPreferredSize() {
    		return new Dimension(1000, 1);
    	}
    	public Dimension getMaximumSize() {
    		return getPreferredSize();
    	}
    	public Dimension getMinimumSize() { 
    		return new Dimension(1,1);
    	}
	}

	
	
	protected CenterPanel center;

    protected JButton mainMenuButton;

	protected DwoIF dwo;

    /**
     * Creates a new GuestMenuPanel. The panel contains only a button for the
     * main menu.
     *  
     */
    public GuestMenuPanel() {
    	this(null);
    }


	public GuestMenuPanel(DwoIF dwo) {
		this.dwo = dwo;
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setDoubleBuffered(false);
        setOpaque(!GuiConstants.GUI_IMAGE_BG);
        createGap();
        createButtons();
        add(Box.createVerticalGlue());
	}


	/* (non-Javadoc)
	 * @see java.awt.Component#setBounds(int, int, int, int)
	 */
	public void setBounds(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		super.setBounds(x, y, width, height);
	}


	protected void createGap() {
		add(Box.createVerticalStrut(5));
	}


	/* (non-Javadoc)
	 * @see java.awt.Container#add(java.awt.Component)
	 */
	public Component add(JComponent component) {
		component.setAlignmentX(Component.CENTER_ALIGNMENT);
		return super.add(component);
	}


	protected void createButtons() {
		createGap();
        /* Add MainMenu button */
        mainMenuButton = new MenuPanelButton(TextMapper.getText(TextMapper.GUIMNU_MAIN_MENU));
        mainMenuButton.addActionListener(this);
        this.add(mainMenuButton);
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
        	center.reset();
        	center.end(); // must be idempotent 
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
    	invalidate();
    }

    public void setEditing(boolean b) {
    	
    }
    

}