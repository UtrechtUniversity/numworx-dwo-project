package fi.dwo.dwojapplet.gui;

import fi.dwo.rest.dom.entities.DomNewSchoolLogin;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.rest.SecureUserAccountLoginsManager;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * <p>
 * This is dialog for registering to a school.</p>
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
public class RegisterMoreSchoolLoginsPanel extends JPanel implements ActionListener {
    private static final Logger LOG = Logger.getLogger(RegisterMoreSchoolLoginsPanel.class.getName());

    private JTextField schoollogin;

    private JPasswordField schoolpassword;

    private JButton registerButton;

    private JButton resetButton;

    private JButton backButton;

    private JComboBox groupChoice;

    /**
     * Creates a new RegisterPanel. At the register panel, a user can register
     * himself.
     *
     */
    public RegisterMoreSchoolLoginsPanel() {
//        groupList = groups;

        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setLayout(null);
        this.setSize(GuiConstants.CENTER_WIDTH / 2, GuiConstants.CENTER_HEIGHT / 2);
        this.setMinimumSize(this.getSize());
        /* Variables used to create items */
        FontMetrics fm;
        JPanel p;
        JLabel l;

        /* Add School-panel */
        p = new JPanel(null);
        //p.setBorder(BorderFactory.createLineBorder(Color.black));
        p.setBackground(GuiConstants.SUB_BACKGROUND);
        p.setBounds(getSize().width / 2 - 180, 10, 310, 120);
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
//        groupChoice.addItem(TextMapper.getText(TextMapper.GUIR_OPT_SELECT_GROUP));
        RoleType[] rl = DwoHelper.getRoles();
        for (int i = 1; i < rl.length; i++) {
            groupChoice.addItem(TextMapper.getText(rl[i].name()));
        }
//        RoleType[] rl = DwoHelper.getRoles();
//        for (int i = 0; i < rl.length; i++) {
//            //if(!groupList[i].getName().equals("ADMIN"))
//            groupChoice.addItem(TextMapper.getText(rl[i].name()));
//        }

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
        //p.setBorder(BorderFactory.createLineBorder(Color.black));
        p.setBackground(GuiConstants.SUB_BACKGROUND);
        p.setBounds(getSize().width / 2 - 180, 140, 310, 80);//487
        this.add(p);

        /* Register button */
        registerButton = new JButton(TextMapper.getText(TextMapper.GUIR_BTN_REGISTER));//, GuiConstants.SUB_BACKGROUND);
        fm = registerButton.getFontMetrics(registerButton.getFont());
        registerButton.setSize(registerButton.getPreferredSize());
        registerButton.setLocation(p.getX() + this.getWidth() / 2 - registerButton.getWidth(), p.getY() + p.getHeight() / 2 - registerButton.getHeight());
        registerButton.setLocation(3 * (p.getSize().width / 4)
                - (registerButton.getSize().width / 2), 10);
        p.add(registerButton);

        /* Reset button */
        backButton = new JButton(TextMapper.getText(TextMapper.BTN_CANCEL));//, GuiConstants.SUB_BACKGROUND);
        fm = backButton.getFontMetrics(backButton.getFont());
        backButton.setSize(backButton.getPreferredSize());

        backButton.setLocation((p.getSize().width / 4)
                - (backButton.getSize().width / 2), 10);
        p.add(backButton);

        registerButton.addActionListener(this);
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
//            if ((groupChoice.getSelectedIndex() == 0) && (schoollogin.getText().equals("")) && (schoolpassword.getText().equals(""))) {
//                //add nullschool login
//                try {
//                    DomNewSchoolLogin nur = new DomNewSchoolLogin();
//
//                    nur.setSchoolLogin(null);
//                    nur.setSchoolCode(null);
//                    nur.setRole(RoleType.STUDENT);
//                    SecureUserAccountLoginsManager.addASchoolLogin(nur);
//                    JOptionPane.showMessageDialog(this, TextMapper.getText(TextMapper.GUIR_MSG_REGISTERED), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
//                    GuiCreator.instance().loadPanel(GuiCreator.instance().getWelcomePanel());
//                }
//                catch (Dwo2Exception ex) {
//                    LOG.log(Level.WARNING, "Error adding schoollogin.",ex);
//                    GuiCreator.instance().ShowMessageDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.GUIW_ERR_NEW_SCHOOLLOGIN));
//                }
//            } else {
                RoleType role = null;
                role = DwoHelper.getRoles()[groupChoice.getSelectedIndex()+1];
                try {
                    DomNewSchoolLogin nur = new DomNewSchoolLogin();
                    nur.setRole(role);
                    nur.setSchoolLogin(schoollogin.getText());
                    nur.setSchoolCode(schoolpassword.getText());
                    SecureUserAccountLoginsManager.addASchoolLogin(nur); //throws Dwo2RestException.
                    GuiCreator.instance().ShowMessageDialog(this, TextMapper.getText(TextMapper.DLG_CONFIRM));
//                    center.loadCenter(new RegisterMoreSchoolLoginsPanel());
                    this.getParent().getParent().getParent().getParent().setVisible(false);
                }
                catch (Dwo2Exception ex) {
                    LOG.log(Level.WARNING, "Error adding schoollogin.",ex);
                    GuiCreator.instance().ShowMessageDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.GUIW_ERR_NEW_SCHOOLLOGIN));
                }

//            }
        } else if (e.getSource() == backButton) {
            //TODO fixe shameful hack.
            this.getParent().getParent().getParent().getParent().setVisible(false);
        }
    }

}
