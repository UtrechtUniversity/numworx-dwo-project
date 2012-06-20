// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\RegisterPanel.java

package fi.dwo.client.gui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.Group;
import fi.dwo.client.system.RegisterException;
import fi.dwo.client.system.TextMapper;

/**
 * This class is a panel where a user can register himself at the dwo.
 * 
 * @author M.J.B. Kupers
 *  
 */
public class RegisterPanel extends JPanel implements ActionListener {
    private Group groupList[];

    private JTextField username;

    private JPasswordField password;

    private JPasswordField repassword;

    private JTextField firstname;

    private JTextField middlename;

    private JTextField lastname;

    private JTextField email;

    private JTextField schoollogin;

    private JPasswordField schoolpassword;

    private JButton registerButton;

    private JButton resetButton;

    private JButton backButton;

    private JComboBox groupChoice;

    /**
     * Creates a new RegisterPanel. At the registerpanel, a user can register
     * himself.
     * 
     * @param groups The possible groups wherefrom a user can be part of.
     */
    public RegisterPanel(Group[] groups) {
        groupList = groups;

        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setLayout(null);
        this.setSize(GuiConstants.DWO_WIDTH, GuiConstants.DWO_HEIGHT);
        //setPreferredSize(getSize()); // Sinds 1.5

        /* Variables used to create items */
        FontMetrics fm;
        JPanel p;
        JLabel l;

        /* Add FI logo */
        Image fiLogo;
        fiLogo = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.FI_LOGO_LOCATION);
        if(fiLogo == null)
        	fiLogo = DwoHelper.getResourceImage(GuiConstants.FI_LOGO_LOCATION);
    
    	ImagePanel ip = new ImagePanel(fiLogo);
    	ip.setLocation(getSize().width / 2 - 130, 50);
    	this.add(ip);
    	if(GuiConstants.GUI_IMAGE_BG) remove(ip);
    
        /* Register Label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIR_REGISTER));
        l.setFont(GuiConstants.HEADER_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(ip.getLocation().x + ip.getSize().width + 10, 70, fm.stringWidth(l.getText()), fm.getHeight());
        this.add(l);
        if(GuiConstants.GUI_IMAGE_BG) remove(l);

        /* Add Register-panel */
        p = new JPanel(null);
        p.setBorder(BorderFactory.createLineBorder(getForeground()));
        p.setBackground(GuiConstants.SUB_BACKGROUND);
        p.setBounds(getSize().width / 2 - 155, 140, 310, 105);
        this.add(p);

        /* registerinfo label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIR_REGISTERINFO) + ":");
        l.setForeground(GuiConstants.RED_COLOR);
        l.setFont(GuiConstants.RED_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(10, 5, fm.stringWidth(l.getText()), fm.getHeight());
        p.add(l);

        /* Username label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIR_USERNAME) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 30);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* Username field */
        username = new JTextField();
        username.setBounds(160, 28, 120, 20);
        p.add(username);

        /* Username mandatory label */
        l = createMandatoryLabel();
        l.setLocation(285, 28);
        p.add(l);

        /* Password label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIR_PASSWORD) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 55);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* Password field */
        password = new JPasswordField();
        password.setBounds(160, 53, 120, 20);
        password.setEchoChar('*');
        p.add(password);

        /* Password mandatory label */
        l = createMandatoryLabel();
        l.setLocation(285, 53);
        p.add(l);

        /* RePassword label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIR_RE_PASSWORD) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 80);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* RePassword field */
        repassword = new JPasswordField();
        repassword.setBounds(160, 78, 120, 20);
        repassword.setEchoChar('*');
        p.add(repassword);

        /* RePassword mandatory label */
        l = createMandatoryLabel();
        l.setLocation(285, 78);
        p.add(l);

        /* Add PersonalInfo-panel */
        p = new JPanel(null);
        p.setBorder(BorderFactory.createLineBorder(getForeground()));
        p.setBackground(GuiConstants.SUB_BACKGROUND);
        p.setBounds(getSize().width / 2 - 155, 244, 310, 130);
        this.add(p);

        /* personalinfo label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIR_PERSONALINFO) + ":");
        l.setForeground(GuiConstants.RED_COLOR);
        l.setFont(GuiConstants.RED_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(10, 5, fm.stringWidth(l.getText()), fm.getHeight());
        p.add(l);

        /* Firstname label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIR_FIRSTNAME) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 30);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* Firstname field */
        firstname = new JTextField();
        firstname.setBounds(160, 28, 120, 20);
        p.add(firstname);

        /* Firstname mandatory label */
        l = createMandatoryLabel();
        l.setLocation(285, 28);
        p.add(l);

        /* Middlename label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIR_MIDDLENAME) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 55);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* Middlename field */
        middlename = new JTextField();
        middlename.setBounds(160, 53, 120, 20);
        p.add(middlename);

        /* Lastname label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIR_LASTNAME) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 80);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* Lastname field */
        lastname = new JTextField();
        lastname.setBounds(160, 78, 120, 20);
        p.add(lastname);

        /* Lastname mandatory label */
        l = createMandatoryLabel();
        l.setLocation(285, 78);
        p.add(l);

        /* Email label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIR_EMAIL) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 105);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* Email field */
        email = new JTextField();
        email.setBounds(160, 103, 120, 20);
        p.add(email);

        /* Email mandatory label */
        l = createMandatoryLabel();
        l.setLocation(285, 103);
        p.add(l);

        /* Add School-panel */
        p = new JPanel(null);
        p.setBorder(BorderFactory.createLineBorder(Color.black));
        p.setBackground(GuiConstants.SUB_BACKGROUND);
        p.setBounds(getSize().width / 2 - 155, 373, 310, 115);
        this.add(p);

        /* schoolinfo label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIR_SCHOOLINFO) + ":");
        l.setForeground(GuiConstants.RED_COLOR);
        l.setFont(GuiConstants.RED_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(10, 5, fm.stringWidth(l.getText()), fm.getHeight());
        p.add(l);

        /* schoolinfo sublabel */
        l = new JLabel(TextMapper.getText(TextMapper.GUIR_MSG_PROVIDED_SCHOOL)
                + ":");
        l.setForeground(GuiConstants.RED_COLOR);
        l.setFont(GuiConstants.SMALL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(10, 22, fm.stringWidth(l.getText()), fm.getHeight());
        p.add(l);

        /* Schoologin label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIR_SCHOOLLOGIN) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 40);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* Schoologin field */
        schoollogin = new JTextField();
        schoollogin.setBounds(160, 38, 120, 20);
        p.add(schoollogin);

        /* Group label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIR_SCHOOLGROUP) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 65);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* Password field */
        groupChoice = new JComboBox();
        groupChoice.addItem(TextMapper.getText(TextMapper.GUIR_OPT_SELECT_GROUP));
        for (int i = 0; i < groupList.length; i++) {
            //if(!groupList[i].getName().equals("ADMIN"))
            groupChoice.addItem(TextMapper.getText(groupList[i].getName()));
        }
        groupChoice.setSize(groupChoice.getPreferredSize());
// past niet op de mac 
        groupChoice.setBounds(160, 63, Math.max(120, groupChoice.getWidth()), 20);
        p.add(groupChoice);

        /* Schoolpassword label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIR_SCHOOLPASSWORD) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 90);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* Schoolpassword field */
        schoolpassword = new JPasswordField();
        schoolpassword.setBounds(160, 88, 120, 20);
        schoolpassword.setEchoChar('*');
        p.add(schoolpassword);

        /* Add Button-panel */
        p = new JPanel(null);
        p.setBorder(BorderFactory.createLineBorder(Color.black));
        p.setBackground(GuiConstants.SUB_BACKGROUND);
        p.setBounds(getSize().width / 2 - 155, 487, 310, 35);
        this.add(p);

        /* Register button */
        registerButton = new JButton(TextMapper.getText(TextMapper.GUIR_BTN_REGISTER));//, GuiConstants.SUB_BACKGROUND);
        fm = registerButton.getFontMetrics(registerButton.getFont());
        registerButton.setSize(registerButton.getPreferredSize());

        /* Reset button */
        resetButton = new JButton(TextMapper.getText(TextMapper.GUIR_BTN_RESET));//, GuiConstants.SUB_BACKGROUND);
        fm = resetButton.getFontMetrics(resetButton.getFont());
        resetButton.setSize(resetButton.getPreferredSize());

        registerButton.setLocation((p.getSize().width / 2)
                - ((registerButton.getSize().width
                        + resetButton.getSize().width + 5) / 2), 5);
        p.add(registerButton);

        resetButton.setLocation((p.getSize().width / 2)
                - ((registerButton.getSize().width
                        + resetButton.getSize().width + 5) / 2)
                + registerButton.getSize().width + 5, 5);
        p.add(resetButton);

        backButton = new JButton(TextMapper.getText(TextMapper.GUIR_BTN_BACK));//, GuiConstants.MAIN_BACKGROUND);
        fm = backButton.getFontMetrics(backButton.getFont());
        backButton.setSize(backButton.getPreferredSize());
        backButton.setLocation(getSize().width / 2 - backButton.getSize().width
                / 2, 530);
        this.add(backButton);

        registerButton.addActionListener(this);
        resetButton.addActionListener(this);
        backButton.addActionListener(this);

        groupChoice.addItemListener(new GroupItemListener(schoolpassword));
    }

    /**
     * Creates a new label with a asterisk. It can be used to indicate that a
     * field is mandatory.
     * 
     * @return A label with the caption of a asterisk.
     */
    private JLabel createMandatoryLabel() {
        JLabel mandatoryLabel = new JLabel("*");
        mandatoryLabel.setForeground(GuiConstants.RED_COLOR);
        mandatoryLabel.setFont(GuiConstants.RED_TEXT);
        FontMetrics fm = mandatoryLabel.getFontMetrics(mandatoryLabel.getFont());
        mandatoryLabel.setSize(fm.stringWidth(mandatoryLabel.getText()) + 5, fm.getHeight());
        return mandatoryLabel;
    }
    
    public void paintComponent(Graphics g) {
    	if(GuiConstants.GUI_IMAGE_BG) {
	       	Point p = DwoHelper.getComponentLocation(this);
	       	g.drawImage(DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_WELCOME),0,0,null);
    	}       
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            if ((groupChoice.getSelectedIndex() == 0) && (schoollogin.getText().equals("")) && (schoolpassword.getText().equals(""))) {
                try {
                    GuiCreator.instance().register(username.getText(), password.getText(), repassword.getText(), firstname.getText(), middlename.getText(), lastname.getText(), email.getText());
                } catch (RegisterException exc) {
                    JOptionPane.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
                }
            } else {
                Group g = null; 
                if(groupChoice.getSelectedIndex() > 0) {
                    g = groupList[groupChoice.getSelectedIndex() - 1];
                }
                try {
                    GuiCreator.instance().register(username.getText(), password.getText(), repassword.getText(), firstname.getText(), middlename.getText(), lastname.getText(), email.getText(), schoollogin.getText(), g, schoolpassword.getText());
                } catch (RegisterException exc) {
                    JOptionPane.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (e.getSource() == resetButton) {
            username.setText("");
            password.setText("");
            repassword.setText("");
            firstname.setText("");
            middlename.setText("");
            lastname.setText("");
            email.setText("");
            schoollogin.setText("");
            groupChoice.setSelectedIndex(0);
            schoolpassword.setText("");
            schoolpassword.setVisible(true);
        } else if (e.getSource() == backButton) {
            GuiCreator.instance().loadPanel(GuiCreator.instance().getWelcomePanel());
        }

    }

}