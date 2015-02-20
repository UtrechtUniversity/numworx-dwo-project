// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\MenuPanel.java

package fi.dwo.dwojapplet.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.Border;

import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoIF;
import fi.dwo.dwojapplet.domain.User;

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
        @Override
	protected void createButtons() {
		super.createButtons();
        createMenuButtons();
        addClassList();
	}

	protected void createMenuButtons() {
		createGap();
        /* Add MainMenu button */
        myProfileButton = new MenuPanelButton(TextMapper.getText(TextMapper.GUIMNU_MY_PROFILE));
        myProfileButton.addActionListener(this);
        this.add(myProfileButton);
	}


	/**
     * Creates a new MenuPanel for the user. It contains the parent items (from
     * GuestMenuPanel) and a button to show the profile for editing.
     */
    public MenuPanel() {
//        setDebugGraphicsOptions(DebugGraphics.FLASH_OPTION);
    }

  
    public MenuPanel(DwoIF dwo) {
		super(dwo);
	}

	/**
     * Adds the name of the class of the user to the panel. Can be overridden by
     * subclasses.
     *  
     */
    protected void addClassList() {
        JLabel l;
        createRuler();
        Box p = new Box(BoxLayout.PAGE_AXIS);
        p.setOpaque(false);
        /* Add class-info */
        User u = GuiCreator.instance().getUser();
        if (u.getInClass() != null) {
            l = new JLabel(TextMapper.getText(TextMapper.GUIMNU_STUDENT_IN_CLASS)
                    + ":");
            l.setOpaque(false);
            l.setFont(GuiConstants.NORMAL_TEXT);
            p.add(l);
            l = new JLabel("-  " + u.getInClass().getName());
            l.setOpaque(false);
            l.setFont(GuiConstants.NORMAL_TEXT);
            p.add(l);
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
            p.add(l);

            l = new JLabel(TextMapper.getText(TextMapper.GUIMNU_STUDENT_NO_CLASS_1));
            l.setOpaque(false);
            l.setFont(GuiConstants.RED_TEXT);
            l.setForeground(Color.red);
            l.setBorder(border);
            p.add(l);
            
            l = new JLabel(TextMapper.getText(TextMapper.GUIMNU_STUDENT_NO_CLASS_2));
            l.setOpaque(false);
            l.setFont(GuiConstants.RED_TEXT);
            l.setForeground(Color.red);
            l.setBorder(border);
            p.add(l);
            
            l = new JLabel(TextMapper.getText(TextMapper.GUIMNU_STUDENT_NO_CLASS_3));
            l.setOpaque(false);
            l.setFont(GuiConstants.RED_TEXT);
            l.setForeground(Color.red);
            l.setBorder(border);
            p.add(l);
            
            l = new JLabel(TextMapper.getText(TextMapper.GUIMNU_STUDENT_NO_CLASS_4));
            l.setOpaque(false);
            l.setFont(GuiConstants.RED_TEXT);
            l.setForeground(Color.red);
            l.setBorder(border);
            p.add(l);
            add(p);
        }

    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
        @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        if (e.getSource() == myProfileButton) {
            center.loadCenter(GuiCreator.instance().getProfilePanel());
            center.reset();
        }

    }
}