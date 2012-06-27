// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\WelcomePanel.java

package fi.dwo.client.gui;

import java.awt.Color;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.system.LoginException;
import fi.dwo.client.system.TextMapper;

import fi.beans.copyright.FIButton;

/**
 * This class represents the panel that is been showed when you start the
 * application.
 * 
 * @author M.J.B. Kupers
 *  
 */
public class WelcomePanel extends JPanel implements ActionListener {

    private JTextField loginname;

    private JPasswordField password;

    private JButton loginButton;

    private JButton guestButton;

    private JButton registerButton;

	FIButton fiButton;
    JPanel dialog;
	
	/**
	 * Layout manager voor de fiButton. 
	 * Hou de fiButton in de rechtsbovenhoek.
	 * @author wim
	 *
	 */
    private class FiButtonMover extends ComponentAdapter {

		/* (non-Javadoc)
		 * @see java.awt.event.ComponentAdapter#componentResized(java.awt.event.ComponentEvent)
		 */
		public void componentResized(ComponentEvent e) {
			super.componentResized(e);
			int width = getWidth();
			fiButton.setLocation(width-fiButton.getWidth(),fiButton.getY());
// extra: move dialogbox  horizontal
			dialog.setLocation(width / 2 - dialog.getWidth() / 2, dialog.getY());
		}
    	
    }

    /**
     * Creates a new WelcomePanel with the posibilities to login (as guest) or
     * to register.
     *  
     */
    
    public WelcomePanel() {
    	this(false);
    }
    public WelcomePanel(boolean loginOnly) {
    	super(null, true);
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setLayout(null);
        this.setSize(GuiConstants.DWO_WIDTH, GuiConstants.DWO_HEIGHT);
        this.setPreferredSize(getSize());
        this.setOpaque(true);
        
        dialog = new JPanel(null);
        dialog.setOpaque(false);
        dialog.setBounds(getWidth()/2-200, 0, 400, getHeight());
        this.add(dialog);
        fiButton = new FIButton("DWO",new String[]
			{	"versie-info: " + fi.dwo.VERSION.VERSION,
				"auteur: Peter Boon",
				"programmeur: M.J.B. Kupers,",
				"Wim van Velthoven",
				"Freudenthal Instituut",
				"www.fisme.science.uu.nl",
				""
			});
		fiButton.setBounds(GuiConstants.DWO_WIDTH-30,0,20,30);
		add(fiButton);
		addComponentListener(new FiButtonMover()); // layout management voor FiButton
        /* Variables used to create items */
        JPanel p;
        JLabel l;
        FontMetrics fm;

        /* Add FI logo */
        Image fiLogo = null;
        fiLogo = DwoHelper.getResourceImage(GuiConstants.WISWEB_LOGO_LOCATION);
        
        ImagePanel ip = new ImagePanel(fiLogo);
        ip.setLocation(dialog.getWidth() / 2 - ip.getWidth() / 2, 440);
        dialog.add(ip);
        if(GuiConstants.GUI_IMAGE_BG) dialog.remove(ip);

        /* Warning Label */
        /*l = new Label("Helaas zijn er problemen met de DWO. Wordt aan gewerkt.");
        l.setFont(GuiConstants.RED_TEXT_ITALIC);
        l.setForeground(Color.red);
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(ip.getLocation().x + ip.getSize().width + 10, 70, fm.stringWidth(l.getText()), fm.getHeight());
        l.setVisible(false);
        this.add(l);
        l.setVisible(true);*/
        
        /* Welcome Label */
        //l = new Label(TextMapper.getText(TextMapper.GUIW_WELCOME) + "!");
        l = new JLabel(TextMapper.getText(TextMapper.GUIM_FI_NAME));
        l.setFont(new Font("SansSerif", Font.BOLD, 26));
        l.setForeground(new Color(3,65,123));
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(dialog.getWidth() / 2 - fm.stringWidth(l.getText())/2, 520, fm.stringWidth(l.getText())+5, fm.getHeight());
        dialog.add(l);
        if(GuiConstants.GUI_IMAGE_BG) dialog.remove(l);
        
        l = new JLabel(TextMapper.getText(TextMapper.GUIM_DWO_SHORT));
        l.setFont(GuiConstants.HEADER_TEXT);
        l.setForeground(new Color(3,65,123));
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(dialog.getWidth() / 2 - fm.stringWidth(l.getText())/2, 20, fm.stringWidth(l.getText())+5, fm.getHeight());
        dialog.add(l);
        if(GuiConstants.GUI_IMAGE_BG) dialog.remove(l);
        
        l = new JLabel(TextMapper.getText(TextMapper.GUIM_DWO_FULL));
        l.setFont(new Font("SansSerif", Font.BOLD, 26));
        fm = l.getFontMetrics(l.getFont());
        l.setForeground(new Color(3,65,123));
        l.setBounds(dialog.getWidth() / 2 - fm.stringWidth(l.getText())/2, 65, fm.stringWidth(l.getText())+5, fm.getHeight());
        dialog.add(l);
        if(GuiConstants.GUI_IMAGE_BG) dialog.remove(l);

        /* Add Login-panel */
        p = new JPanel(null);
        //p.setFocusCycleRoot(true);
        p.setBorder(BorderFactory.createLineBorder(new Color(52,90,126)));
        p.setBackground(GuiConstants.SUB_BACKGROUND);
        //p.setBorderColor(new Color(52,90,126));
        p.setBounds(dialog.getWidth() / 2 - 130, 110, 260, 115);
        dialog.add(p);

        /* Inlogdata label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIW_LOGINDATA) + ":");
        l.setForeground(GuiConstants.RED_COLOR);
        l.setFont(GuiConstants.RED_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(10, 5, fm.stringWidth(l.getText()), fm.getHeight());
        p.add(l);

        /* Username label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIW_USERNAME) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 30);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* Username field */
        loginname = new JTextField();
        loginname.setBounds(130, 28, 120, 20);
        loginname.addActionListener(this);
        p.add(loginname);

        /* Password label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIW_PASSWORD) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 55);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* Password field */
        password = new JPasswordField();
        password.setBounds(130, 53, 120, 20);
        password.setEchoChar('*');
        password.addActionListener(this);
        p.add(password);

        /* Login button */
        loginButton = new JButton(TextMapper.getText(TextMapper.GUIW_BTN_LOGIN));//, GuiConstants.SUB_BACKGROUND);
        fm = loginButton.getFontMetrics(loginButton.getFont());
        loginButton.setSize(loginButton.getPreferredSize());
        loginButton.setLocation((p.getSize().width / 2)
                - (loginButton.getSize().width / 2), 80);
        p.add(loginButton);

        loginButton.addActionListener(this);
        
        if(loginOnly) return;
        /* Add GuestLogin-panel */
        p = new JPanel(null);
        p.setBorder(BorderFactory.createLineBorder(new Color(52,90,126)));
        p.setBackground(GuiConstants.SUB_BACKGROUND);
        p.setBounds(dialog.getWidth() / 2 - 130, 235, 260, 85);
        //p.setBorderColor(new Color(52,90,126));
        dialog.add(p);

        /* Guestlogin label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIW_GUESTLOGIN) + ":");
        l.setForeground(GuiConstants.RED_COLOR);
        l.setFont(GuiConstants.RED_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(10, 5, fm.stringWidth(l.getText()), fm.getHeight());
        p.add(l);

        /* GuestLogin button */
        guestButton = new JButton(TextMapper.getText(TextMapper.GUIW_BTN_GUESTLOGIN));//, GuiConstants.SUB_BACKGROUND);
        fm = guestButton.getFontMetrics(guestButton.getFont());
        guestButton.setSize(guestButton.getPreferredSize());
        guestButton.setLocation((p.getSize().width / 2)
                - (guestButton.getSize().width / 2), 27);
        p.add(guestButton);

        /* GuestLogin message */
        l = new JLabel(TextMapper.getText(TextMapper.GUIW_MSG_WORK_NOT_SAVE));
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setSize(fm.stringWidth(l.getText()), fm.getHeight());
        l.setLocation((p.getSize().width / 2) - (l.getSize().width / 2), 60);
        p.add(l);

        /* Add Register-panel */
        p = new JPanel(null);
        p.setBorder(BorderFactory.createLineBorder(new Color(52,90,126)));
        p.setBackground(GuiConstants.SUB_BACKGROUND);
        p.setBounds(dialog.getWidth() / 2 - 130, 330, 260, 85);
        //p.setBorderColor(new Color(52,90,126));
        dialog.add(p);

        /* Register label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIW_REGISTER) + ":");
        l.setForeground(GuiConstants.RED_COLOR);
        l.setFont(GuiConstants.RED_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(10, 5, fm.stringWidth(l.getText()), fm.getHeight());
        p.add(l);

        /* Register button */
        registerButton = new JButton(TextMapper.getText(TextMapper.GUIW_BTN_REGISTER));//, GuiConstants.SUB_BACKGROUND);
        fm = registerButton.getFontMetrics(registerButton.getFont());
        registerButton.setSize(registerButton.getPreferredSize());
        registerButton.setLocation((p.getSize().width / 2)
                - (registerButton.getSize().width / 2), 27);
        p.add(registerButton);

        /* Register message */
        l = new JLabel(TextMapper.getText(TextMapper.GUIW_MSG_REGISTER_NEW));
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setSize(fm.stringWidth(l.getText()), fm.getHeight());
        l.setLocation((p.getSize().width / 2) - (l.getSize().width / 2), 60);
        p.add(l);
        
        guestButton.addActionListener(this);
        registerButton.addActionListener(this);
        
    }
    
    public void paintComponent(Graphics g) {
    	if(GuiConstants.GUI_IMAGE_BG) {
	       	//Point p = DwoHelper.getComponentLocation(this);
	       Image guiImage = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_WELCOME);
    		if(guiImage == null)
    		{   // Oops.
    			GuiConstants.GUI_IMAGE_BG = false;
    			super.paintComponent(g);
    			return;
    		}
	       
	       g.drawImage(guiImage,0,0,null);
	       	
	       	int w = getSize().width;
	       	int h = getSize().height;
	       	int dw = w-800;
	       	int dh = h-600;
	       	int rand = 20;
	       	int strook = 100;
	       	
	       	g.drawImage(guiImage,0,600-rand-strook,800-rand-strook,h-rand,0,600-rand-strook,800-rand-strook,600-rand,null);
	       	g.drawImage(guiImage,800-rand-strook,0,w-rand,600-rand-strook,800-rand-strook,0,800-rand,600-rand-strook,null);
	       	g.drawImage(guiImage,0,h-rand,800-rand-strook,h,0,600-rand,800-rand-strook,600,null);
	       	g.drawImage(guiImage,w-rand,0,w,600-rand-strook,800-rand,0,800,600-rand-strook,null);
	       	g.drawImage(guiImage,800-rand-strook,600-rand-strook,w-rand,h-rand,800-rand-strook,600-rand-strook,800-rand,600-rand,null);
	       	g.drawImage(guiImage,800-rand-strook,h-rand,w-rand,h,800-rand-strook,600-rand,800-rand,600,null);
	       	g.drawImage(guiImage,w-rand,600-rand-strook,w,h-rand,800-rand,600-rand-strook,800,600-rand,null);
	    	g.drawImage(guiImage,w-rand,h-rand,w,h,800-rand,600-rand,800,600,null);
	    	
    	} else
    		super.paintComponent(g);
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if ((src == loginButton) || (src == loginname) || (src == password)) {
            try {
                GuiCreator.instance().login(loginname.getText(), password.getText());
                DwoHelper.setCookie("dwoUserName", loginname.getText());
                DwoHelper.setCookie("dwoPassWord", password.getText());
            } catch (LoginException exc) {
                JOptionPane.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN), JOptionPane.ERROR_MESSAGE);
            }
        } else if (src == guestButton) {
            try {
                GuiCreator.instance().login();
            } catch (LoginException exc) {
                JOptionPane.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN), JOptionPane.ERROR_MESSAGE);
            }
        } else if (src == registerButton) {
            GuiCreator.instance().toRegister();
        }

    }
    
    public void setUsername(String username) {
        this.loginname.setText(username);
    }
    
    public void setPassword(String password) {
        this.password.setText(password);
    }
 }