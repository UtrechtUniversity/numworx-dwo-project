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
    

    /**
     * Creates a new WelcomePanel with the posibilities to login (as guest) or
     * to register.
     *  
     */
    public WelcomePanel() {
    	super(null, true);
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setLayout(null);
        this.setSize(GuiConstants.DWO_WIDTH, GuiConstants.DWO_HEIGHT);
        this.setPreferredSize(getSize());
        this.setOpaque(true);
        
// TODO Let op, fi.dwo.VERSION wordt gegenereerd door de ant target 'version'
// en daarna (eenmalig) F5 om Version.jave in eclipse te importeren.
// Je hoeft dan niet steeds de datum van dit file te wijzigen!
// vind ik (Wim) een *GROOT* voordeel!
// en deze file verandert dan niet steeds om niets!
        FIButton fiButton = new FIButton("DWO",new String[]
			{	"versie-info: " + fi.dwo.VERSION.VERSION,
				"auteur: Peter Boon",
				"programmeur: M.J.B. Kupers,",
				"Wim van Velthoven",
				"Freudenthal Instituut",
				"www.fi.uu.nl",
				""
			});
		fiButton.setBounds(0,0,20,30);
		add(fiButton);

        /* Variables used to create items */
        JPanel p;
        JLabel l;
        FontMetrics fm;

        /* Add FI logo */
        Image fiLogo = null;
        fiLogo = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.WISWEB_LOGO_LOCATION);
        MediaTracker tr = new MediaTracker(this);
        tr.addImage(fiLogo, 0);
        try {
            tr.waitForAll();
        } catch (Exception e) {
        }
        ;
        ImagePanel ip = new ImagePanel(fiLogo);
        ip.setLocation(getSize().width / 2 - ip.getSize().width / 2, 440);
        ip.setVisible(false);
        this.add(ip);
        if(GuiConstants.GUI_IMAGE_BG) remove(ip);
        ip.setVisible(true);

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
        l.setBounds(getSize().width / 2 - fm.stringWidth(l.getText())/2, 520, fm.stringWidth(l.getText())+5, fm.getHeight());
        l.setVisible(false);
        this.add(l);
        if(GuiConstants.GUI_IMAGE_BG) remove(l);
        l.setVisible(true);
        
        l = new JLabel(TextMapper.getText(TextMapper.GUIM_DWO_SHORT));
        l.setFont(GuiConstants.HEADER_TEXT);
        l.setForeground(new Color(3,65,123));
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(getSize().width / 2 - fm.stringWidth(l.getText())/2, 20, fm.stringWidth(l.getText())+5, fm.getHeight());
        l.setVisible(false);
        this.add(l);
        if(GuiConstants.GUI_IMAGE_BG) remove(l);
        l.setVisible(true);
        
        l = new JLabel(TextMapper.getText(TextMapper.GUIM_DWO_FULL));
        l.setFont(new Font("SansSerif", Font.BOLD, 26));
        fm = l.getFontMetrics(l.getFont());
        l.setForeground(new Color(3,65,123));
        l.setBounds(getSize().width / 2 - fm.stringWidth(l.getText())/2, 65, fm.stringWidth(l.getText())+5, fm.getHeight());
        l.setVisible(false);
        this.add(l);
        if(GuiConstants.GUI_IMAGE_BG) remove(l);
        l.setVisible(true);

        /* Add Login-panel */
        p = new JPanel(null);
        //p.setFocusCycleRoot(true);
        p.setBorder(BorderFactory.createLineBorder(new Color(52,90,126)));
        p.setBackground(GuiConstants.SUB_BACKGROUND);
        //p.setBorderColor(new Color(52,90,126));
        p.setBounds(getSize().width / 2 - 130, 110, 260, 115);
        p.setVisible(false);
        this.add(p);
        p.setVisible(true);

        /* Inlogdata label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIW_LOGINDATA) + ":");
        l.setForeground(GuiConstants.RED_COLOR);
        l.setFont(GuiConstants.RED_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(10, 5, fm.stringWidth(l.getText()), fm.getHeight());
        l.setVisible(false);
        p.add(l);
        l.setVisible(true);

        /* Username label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIW_USERNAME) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 30);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        l.setVisible(false);
        p.add(l);
        l.setVisible(true);

        /* Username field */
        loginname = new JTextField();
        loginname.setBounds(130, 28, 120, 20);
        loginname.addActionListener(this);
        loginname.setVisible(false);
        p.add(loginname);
        loginname.setVisible(true);

        /* Password label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIW_PASSWORD) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 55);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        l.setVisible(false);
        p.add(l);
        l.setVisible(true);

        /* Password field */
        password = new JPasswordField();
        password.setBounds(130, 53, 120, 20);
        password.setEchoChar('*');
        password.addActionListener(this);
        password.setVisible(false);
        p.add(password);
        password.setVisible(true);

        /* Login button */
        loginButton = new JButton(TextMapper.getText(TextMapper.GUIW_BTN_LOGIN));//, GuiConstants.SUB_BACKGROUND);
        fm = loginButton.getFontMetrics(loginButton.getFont());
        loginButton.setSize(loginButton.getPreferredSize());
        loginButton.setLocation((p.getSize().width / 2)
                - (loginButton.getSize().width / 2), 80);
        loginButton.setVisible(false);
        p.add(loginButton);
        loginButton.setVisible(true);

        /* Add GuestLogin-panel */
        p = new JPanel(null);
        p.setBorder(BorderFactory.createLineBorder(new Color(52,90,126)));
        p.setBackground(GuiConstants.SUB_BACKGROUND);
        p.setBounds(getSize().width / 2 - 130, 235, 260, 85);
        //p.setBorderColor(new Color(52,90,126));
        p.setVisible(false);
        this.add(p);
        p.setVisible(true);

        /* Guestlogin label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIW_GUESTLOGIN) + ":");
        l.setForeground(GuiConstants.RED_COLOR);
        l.setFont(GuiConstants.RED_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(10, 5, fm.stringWidth(l.getText()), fm.getHeight());
        l.setVisible(false);
        p.add(l);
        l.setVisible(true);

        /* GuestLogin button */
        guestButton = new JButton(TextMapper.getText(TextMapper.GUIW_BTN_GUESTLOGIN));//, GuiConstants.SUB_BACKGROUND);
        fm = guestButton.getFontMetrics(guestButton.getFont());
        guestButton.setSize(guestButton.getPreferredSize());
        guestButton.setLocation((p.getSize().width / 2)
                - (guestButton.getSize().width / 2), 27);
        guestButton.setVisible(false);
        p.add(guestButton);
        guestButton.setVisible(true);

        /* GuestLogin message */
        l = new JLabel(TextMapper.getText(TextMapper.GUIW_MSG_WORK_NOT_SAVE));
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setSize(fm.stringWidth(l.getText()), fm.getHeight());
        l.setLocation((p.getSize().width / 2) - (l.getSize().width / 2), 60);
        l.setVisible(false);
        p.add(l);
        l.setVisible(true);

        /* Add Register-panel */
        p = new JPanel(null);
        p.setBorder(BorderFactory.createLineBorder(new Color(52,90,126)));
        p.setBackground(GuiConstants.SUB_BACKGROUND);
        p.setBounds(getSize().width / 2 - 130, 330, 260, 85);
        //p.setBorderColor(new Color(52,90,126));
        p.setVisible(false);
        this.add(p);
        p.setVisible(true);

        /* Register label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIW_REGISTER) + ":");
        l.setForeground(GuiConstants.RED_COLOR);
        l.setFont(GuiConstants.RED_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(10, 5, fm.stringWidth(l.getText()), fm.getHeight());
        l.setVisible(false);
        p.add(l);
        l.setVisible(true);

        /* Register button */
        registerButton = new JButton(TextMapper.getText(TextMapper.GUIW_BTN_REGISTER));//, GuiConstants.SUB_BACKGROUND);
        fm = registerButton.getFontMetrics(registerButton.getFont());
        registerButton.setSize(registerButton.getPreferredSize());
        registerButton.setLocation((p.getSize().width / 2)
                - (registerButton.getSize().width / 2), 27);
        registerButton.setVisible(false);
        p.add(registerButton);
        registerButton.setVisible(true);

        /* Register message */
        l = new JLabel(TextMapper.getText(TextMapper.GUIW_MSG_REGISTER_NEW));
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setSize(fm.stringWidth(l.getText()), fm.getHeight());
        l.setLocation((p.getSize().width / 2) - (l.getSize().width / 2), 60);
        l.setVisible(false);
        p.add(l);
        l.setVisible(true);

        loginButton.addActionListener(this);
        guestButton.addActionListener(this);
        registerButton.addActionListener(this);
        
    }
    
    public void paintComponent(Graphics g) {
    	if(GuiConstants.GUI_IMAGE_BG) {
	       	//Point p = DwoHelper.getComponentLocation(this);
	       	g.drawImage(DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_WELCOME),0,0,null);
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