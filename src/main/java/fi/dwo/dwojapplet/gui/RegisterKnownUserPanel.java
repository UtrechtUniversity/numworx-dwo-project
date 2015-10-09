package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentRole;
import fi.dwo.commons.rest.entities.RestNewSchoolLogin;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Group;
import fi.dwo.dwojapplet.domain.rest.SecureUserAccountLoginsManager;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * <p>
 * This class is a panel where a known user can register himself for a (new)
 * school.</p>
 *
 * <p>
 * How to test manual:</p>
 *
 * <ul>
 * <li>Register once for each existing role. Then configurePanelsForUser and
 * goto to the Profile panel. Switch to that registered role, it should work.
 * </li>
 * <li> Try to register for an existing role, it should fail with a message.
 * </li>
 * <li> Register with a false school name, it should fail with a message.</li>
 * <li> Register with a false school code, it should fail with a message.<li>
 * <li> Register with a wrong role but correct school name and code, it should
 * fail with a message.</li>
 * <li> Test the reset button </li>
 * </ul>
 */
public class RegisterKnownUserPanel extends ContentPanel implements ActionListener {

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

    private JPanel dialog;

    /**
     * Creates a new RegisterPanel. At the register panel, a user can register
     * himself.
     *
     * @param groups The possible groups wherefrom a user can be part of.
     */
    public RegisterKnownUserPanel(Group[] groups) {
        groupList = groups;

        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setLayout(null);
        this.setSize(GuiConstants.DWO_WIDTH, GuiConstants.DWO_HEIGHT);
        dialog = new JPanel(null);
        dialog.setOpaque(false);
        dialog.setSize(getSize());
        //dialog.setBounds(getWidth() / 2 - 350, 0, 700, getHeight());
        this.add(dialog); // een extra layer....
        //setPreferredSize(getSize()); // Sinds 1.5

        /* Variables used to create items */
        FontMetrics fm;
        JPanel p;
        JLabel l;

        /* Add FI logo */
        Image fiLogo;
        fiLogo = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.FI_LOGO_LOCATION);
        if (fiLogo == null) {
            fiLogo = DwoHelper.getResourceImage(GuiConstants.FI_LOGO_LOCATION);
        }

        ImagePanel ip = new ImagePanel(fiLogo);
        ip.setLocation(getSize().width / 2 - 130, 50);
        dialog.add(ip);
        if (GuiConstants.GUI_IMAGE_BG) {
            dialog.remove(ip);
        }

        /* Register Label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIR_REGISTER));
        l.setFont(GuiConstants.HEADER_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(ip.getLocation().x + ip.getSize().width + 10, 70, fm.stringWidth(l.getText()), fm.getHeight());
        dialog.add(l);
        if (GuiConstants.GUI_IMAGE_BG) {
            dialog.remove(l);
        }

        /* Add Register-panel */
        p = new JPanel(null);
        p.setBorder(BorderFactory.createLineBorder(getForeground()));
        p.setBackground(GuiConstants.SUB_BACKGROUND);
        p.setBounds(getSize().width / 2 - 180, 110, 310, 95);//240
        dialog.add(p);

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

        /* Add School-panel */
        p = new JPanel(null);
        p.setBorder(BorderFactory.createLineBorder(Color.black));
        p.setBackground(GuiConstants.SUB_BACKGROUND);
        p.setBounds(getSize().width / 2 - 180, 215, 310, 125);//473
        dialog.add(p);

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
        List<PersistentRole> rl = DwoHelper.getRoles();
        for (int i = 0; i < groupList.length; i++) {
            //if(!groupList[i].getName().equals("ADMIN"))
            groupChoice.addItem(TextMapper.getText(rl.get(i).getGroupname()));
        }
        groupChoice.setSize(groupChoice.getPreferredSize());
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
        p.setBounds(getSize().width / 2 - 180, 350, 310, 80);//487
        dialog.add(p);

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
                + resetButton.getSize().width + 5) / 2), 10);
        p.add(registerButton);

        resetButton.setLocation((p.getSize().width / 2)
                - ((registerButton.getSize().width
                + resetButton.getSize().width + 5) / 2)
                + registerButton.getSize().width + 5, 10);
        p.add(resetButton);

        backButton = new JButton(TextMapper.getText(TextMapper.GUIR_BTN_BACK));//, GuiConstants.MAIN_BACKGROUND);
        fm = backButton.getFontMetrics(backButton.getFont());
        backButton.setSize(backButton.getPreferredSize());
        backButton.setLocation((p.getSize().width / 2)
                - ((backButton.getSize().width) / 2), 40);//630
        p.add(backButton);

        registerButton.addActionListener(this);
        resetButton.addActionListener(this);
        backButton.addActionListener(this);

        groupChoice.addItemListener(new GroupItemListener(schoolpassword));

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                int width = getWidth();
                // move dialogbox  horizontal
                dialog.setLocation(width / 2 - dialog.getWidth() / 2, dialog.getY());
            }
        });

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

    @Override
    public void paintComponent(Graphics g) {
//    	if(GuiConstants.GUI_IMAGE_BG) {
//	       	Point p = DwoHelper.getComponentLocation(this);
//	       	g.drawImage(DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_WELCOME),0,0,null);
//    	}       

        super.paintComponent(g);
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e The ActionEvent.
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            if ((groupChoice.getSelectedIndex() == 0) && (schoollogin.getText().equals("")) && (schoolpassword.getText().equals(""))) {
                try {
                    RestNewSchoolLogin nur = new RestNewSchoolLogin();

                    nur.setSchoolLogin(null);
                    nur.setSchoolCode(null);
                    nur.setRole(DwoHelper.getRoles().get(RoleType.STUDENT.ordinal()));
                    SecureUserAccountLoginsManager.addASchoolLogin(nur);
                    JOptionPane.showMessageDialog(this, TextMapper.getText(TextMapper.GUIR_MSG_REGISTERED), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
                    GuiCreator.instance().loadPanel(GuiCreator.instance().getWelcomePanel());
                }
                catch (Dwo2Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getLocalizedCodeExplanation(DwoHelper.getLocale()), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
                }
            } else {
                PersistentRole role = null;
                if (groupChoice.getSelectedIndex() > 0) {
                    role = DwoHelper.getRoles().get(groupChoice.getSelectedIndex() - 1);
                }
                try {
                    RestNewSchoolLogin nur = new RestNewSchoolLogin();
                    nur.setRole(role);
                    nur.setSchoolLogin(schoollogin.getText());
                    nur.setSchoolCode(schoolpassword.getText());
                    SecureUserAccountLoginsManager.addASchoolLogin(nur); //throws Dwo2RestException.
                    JOptionPane.showMessageDialog(this, TextMapper.getText(TextMapper.GUIR_MSG_REGISTERED), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
                    GuiCreator.instance().loadPanel(GuiCreator.instance().getWelcomePanel());
                }
                catch (Dwo2Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getLocalizedCodeExplanation(DwoHelper.getLocale()), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
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
