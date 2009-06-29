// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\MenuPanel.java

package fi.dwo.client.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Color;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.User;
import fi.dwo.client.system.TextMapper;

/**
 * This class is the menupanel for the user who logged in.
 * 
 * @author M.J.B. Kupers
 *  
 */
public class MenuPanel extends GuestMenuPanel {


	private JButton myProfileButton;

    public void createRuler() {
        add(Box.createVerticalStrut(10));
        add(new HRuler());
        add(Box.createVerticalStrut(15));
    }
    
    /* (non-Javadoc)
	 * @see fi.dwo.client.gui.GuestMenuPanel#createButtons()
	 */
	protected void createButtons() {
		super.createButtons();
        createMenuButtons();
        addClassList();
	}

	protected void createMenuButtons() {
		createGap();
		/* Variables used to create items */
        //FontMetrics fm;

        /* Add MainMenu button */
        myProfileButton = new MenuPanelButton(TextMapper.getText(TextMapper.GUIMNU_MY_PROFILE));
			
			
        //myProfileButton.setBackground(GuiConstants.MAIN_BACKGROUND);
        //fm = myProfileButton.getFontMetrics(myProfileButton.getFont());
        //myProfileButton.setSize(this.getSize().width - 20, fm.getHeight() + 10);
        //myProfileButton.setLocation(10, 40);
        myProfileButton.addActionListener(this);
        //myProfileButton.setVisible(false);
        this.add(myProfileButton);
        //myProfileButton.setVisible(true);
	}


	/**
     * Creates a new MenuPanel for the user. It contains the parent items (from
     * GuestMenuPanel) and a button to show the profile for editing.
     */
    public MenuPanel() {
        super();

    }

  
    public void paint(Graphics g) {
    	if(GuiConstants.GUI_IMAGE_BG) {
	       	Point p = DwoHelper.getComponentLocation(this);
	       	g.drawImage(DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_COURSE),-p.x,-p.y,null);
    	}       
    	super.paint(g);
    } 
    
    /**
     * Adds the name of the class of the user to the panel. Can be overridden by
     * subclasses.
     *  
     */
    protected void addClassList() {
        /* Variables used to create items */
       //FontMetrics fm;
        JLabel l;
        createRuler();
        Box p = new Box(BoxLayout.PAGE_AXIS) {

			/* (non-Javadoc)
			 * @see javax.swing.JComponent#paint(java.awt.Graphics)
			 */
			public void paint(Graphics g) {
				// TODO Auto-generated method stub
				super.paint(g);
			} 
        	
        	
        };
        p.setOpaque(false);
        /* Add class-info */
        User u = GuiCreator.instance().getUser();
        if (u.getInClass() != null) {
            l = new JLabel(TextMapper.getText(TextMapper.GUIMNU_STUDENT_IN_CLASS)
                    + ":");
            l.setOpaque(false);
            l.setFont(GuiConstants.NORMAL_TEXT);
            //fm = l.getFontMetrics(l.getFont());
            //l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
            //l.setLocation(10, 85);
            //l.setVisible(false);
            p.add(l);
            //l.setVisible(true);

            l = new JLabel("-  " + u.getInClass().getName());
            l.setOpaque(false);
            l.setFont(GuiConstants.NORMAL_TEXT);
            //fm = l.getFontMetrics(l.getFont());
            //l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
            //l.setLocation(20, 100);
            //l.setVisible(false);
            p.add(l);
            //l.setVisible(true);
            add(p);
        }
// if user is readonly, geen rode tekst die alleen maar afleid.
// TODO nadenken of er niet mischien een andere tekst moet komen?
        else if (u.getSchool() != null && !u.isReadonly())
        {	
        	// 10 pixels margin.
        	Border border = BorderFactory.createEmptyBorder(0, 10, 0, 0);
        	l = new JLabel(TextMapper.getText(TextMapper.GUIMNU_STUDENT_NO_CLASS_0));
       		l.setOpaque(false);
            l.setFont(GuiConstants.RED_TEXT);
            l.setForeground(Color.red);
            l.setBorder(border);
            //fm = l.getFontMetrics(l.getFont());
            
            //l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
            //l.setLocation(10, 100);
            //l.setVisible(false);
            p.add(l);
            //l.setVisible(true);
            
            l = new JLabel(TextMapper.getText(TextMapper.GUIMNU_STUDENT_NO_CLASS_1));
            l.setOpaque(false);
            l.setFont(GuiConstants.RED_TEXT);
            l.setForeground(Color.red);
            l.setBorder(border);
            //fm = l.getFontMetrics(l.getFont());
            //l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
            //l.setLocation(10, 120);
            //l.setVisible(false);
            p.add(l);
            //l.setVisible(true);
            
            l = new JLabel(TextMapper.getText(TextMapper.GUIMNU_STUDENT_NO_CLASS_2));
            l.setOpaque(false);
            l.setFont(GuiConstants.RED_TEXT);
            l.setForeground(Color.red);
            l.setBorder(border);
            //fm = l.getFontMetrics(l.getFont());
            //l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
            //l.setLocation(10, 140);
            //l.setVisible(false);
            p.add(l);
            //l.setVisible(true);
            
            l = new JLabel(TextMapper.getText(TextMapper.GUIMNU_STUDENT_NO_CLASS_3));
            l.setOpaque(false);
            l.setFont(GuiConstants.RED_TEXT);
            l.setForeground(Color.red);
            //fm = l.getFontMetrics(l.getFont());
            l.setBorder(border);
            //l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
            //l.setLocation(10, 160);
            //l.setVisible(false);
            p.add(l);
            //l.setVisible(true);
            
            l = new JLabel(TextMapper.getText(TextMapper.GUIMNU_STUDENT_NO_CLASS_4));
            l.setOpaque(false);
            l.setFont(GuiConstants.RED_TEXT);
            l.setForeground(Color.red);
            l.setBorder(border);
            //fm = l.getFontMetrics(l.getFont());
            //l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
            //l.setLocation(10, 180);
            //l.setVisible(false);
            p.add(l);
            //l.setVisible(true);
            add(p);
            
        }

    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        if (e.getSource() == myProfileButton) {
            center.loadCenter(GuiCreator.instance().getProfilePanel());
        }

    }
}