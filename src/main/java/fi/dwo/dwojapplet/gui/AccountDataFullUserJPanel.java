package fi.dwo.dwojapplet.gui;

import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.commons.system.MD5;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.rest.SecureUserAccountManager;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.rest.util.Dwo2ExceptionTranslator;
import java.awt.Color;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Utility panel to fetch changing user-data.
 *
 */
public class AccountDataFullUserJPanel extends JPanel implements
        ActionListener {

    private static final Logger LOG = Logger.getLogger(AccountDataFullUserJPanel.class.getName());

    private DomUserFull user;
//    protected Group groupList[];
//    protected SchoolClass classList[];
    private JPasswordField oldpassword;

    private JPasswordField password;

    private JPasswordField repassword;

    private JTextField firstname;

    private JTextField middlename;

    private JTextField familyname;

    private JTextField email;

//    protected JTextField schoollogin;
    private JButton changeButton;

    private JButton resetButton;

    protected JButton deleteButton;
//    protected User user;
    private AccountDataProperties prop = new AccountDataProperties();

    /**
     * Creates a new ProfilePanel for the current user. The account of the
     * current user can be changed.
     *
     */
    public AccountDataFullUserJPanel() {
        try {
            prop.init();
            user = prop.getUser();
        } catch (Dwo2Exception ex) {
            GuiCreator.instance().ShowErrorDialog(this, ex);
            LOG.log(Level.SEVERE, null, ex);
        }
        //fetch user details.
//        groupList = groups;
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setSize(320, 500);
        this.setMinimumSize(this.getSize());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setPreferredSize(getSize());
        this.setLayout(null);

        /* Variables used to create items */
        FontMetrics fm;
        JPanel p;
        JLabel l;

        /* Add Register-panel */
        p = new JPanel(null);
//        p.setBorder(BorderFactory.createLineBorder(getForeground()));
        p.setBackground(GuiConstants.SUB_BACKGROUND);
        p.setBounds(getSize().width / 2 - 155, 20, 310, 130);
        this.add(p);

        /* registerinfo label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIP_REGISTERINFO) + ":");
        l.setForeground(GuiConstants.RED_COLOR);
        l.setFont(GuiConstants.RED_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(10, 5, fm.stringWidth(l.getText()), fm.getHeight());
        p.add(l);

        /* Username label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIP_USERNAME) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        l.setLocation(10, 30);
        p.add(l);

        /* Username Label */
        l = new JLabel(user.getUserName());
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        l.setLocation(160, 30);
        p.add(l);

        /* Old-Password label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIP_OLD_PASSWORD) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 55);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* Old-Password field */
        oldpassword = new JPasswordField();
        oldpassword.setBounds(160, 53, 120, 20);
        oldpassword.setEchoChar('*');
        p.add(oldpassword);

        /* Old-Password mandatory label */
        l = createMandatoryLabel();
        l.setLocation(285, 53);
        p.add(l);

        /* Password label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIP_PASSWORD) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 80);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* Password field */
        password = new JPasswordField();
        password.setBounds(160, 78, 120, 20);
        password.setEchoChar('*');
        p.add(password);

        /* RePassword label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIP_RE_PASSWORD) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 105);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* RePassword field */
        repassword = new JPasswordField();
        repassword.setBounds(160, 103, 120, 20);
        repassword.setEchoChar('*');
        p.add(repassword);

        /* Add PersonalInfo-panel */
        p = new JPanel(null);
//        p.setBorder(BorderFactory.createLineBorder(getForeground()));
        p.setBackground(GuiConstants.SUB_BACKGROUND);
        p.setBounds(getSize().width / 2 - 155, 149, 310, 130);
        this.add(p);

        /* personalinfo label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIP_PERSONALINFO) + ":");
        l.setForeground(GuiConstants.RED_COLOR);
        l.setFont(GuiConstants.RED_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(10, 5, fm.stringWidth(l.getText()), fm.getHeight());
        p.add(l);

        /* Firstname label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIP_FIRSTNAME) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 30);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* Firstname field */
        firstname = new JTextField();
        firstname.setText("");
        firstname.setBounds(160, 28, 120, 20);
        p.add(firstname);

        /* Firstname mandatory label */
        l = createMandatoryLabel();
        l.setLocation(285, 28);
        p.add(l);

        /* Middlename label */
        String middleNameLabel = TextMapper.getText(TextMapper.GUIP_MIDDLENAME);
        l = new JLabel(middleNameLabel + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 55);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* Middlename field */
        middlename = new JTextField();
        middlename.setText("");
        middlename.setBounds(160, 53, 120, 20);
        p.add(middlename);
// skip middlename for languages that do not support it.
//        boolean visible = prop.getUser().getInsertion().length() > 0 || middleNameLabel.length() > 0;
        middlename.setVisible(true);
        l.setVisible(true);
        int v = true ? 0 : 25;


        /* Lastname label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIP_LASTNAME) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 80 - v);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* Lastname field */
        familyname = new JTextField();
        familyname.setText("");
        familyname.setBounds(160, 78 - v, 120, 20);
        p.add(familyname);

        /* Lastname mandatory label */
        l = createMandatoryLabel();
        l.setLocation(285, 78 - v);
        p.add(l);

        /* Email label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIP_EMAIL) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 105 - v);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* Email field */
        email = new JTextField();
        email.setText("");
        email.setBounds(160, 103 - v, 120, 20);
        p.add(email);

        /* Email mandatory label */
        l = createMandatoryLabel();
        l.setLocation(285, 103 - v);
        p.add(l);
        Container c = p;

        addButtonsPanel(c);
    }

    /**
     * This method adds a button panel under the last panel. <BR>
     *
     * @param p2 The panel where under this panel must appear.
     */
    protected void addButtonsPanel(Container p2) {
        /* Add Button-panel */
        FontMetrics fm;

        int locationY = p2.getSize().height + p2.getLocation().y
                - 1;
        JPanel p = new JPanel(null);
//        p.setBorder(BorderFactory.createLineBorder(getForeground()));
        p.setBackground(GuiConstants.SUB_BACKGROUND);
        p.setBounds(getSize().width / 2 - 155, locationY, 310, 35);
        this.add(p);

        /* Change button */
        changeButton = new JButton(TextMapper.getText(TextMapper.GUIP_BTN_SAVE));//, GuiConstants.SUB_BACKGROUND);
        changeButton.setSize(changeButton.getPreferredSize());
        /* Reset button */
        resetButton = new JButton(TextMapper.getText(TextMapper.GUIP_BTN_RESET));//, GuiConstants.SUB_BACKGROUND);
        //fm = resetButton.getFontMetrics(resetButton.getFont());
        //resetButton.setSize(fm.stringWidth(resetButton.getLabel()) + 20, fm.getHeight() + 10);
        resetButton.setSize(resetButton.getPreferredSize());
        changeButton.setLocation((p.getSize().width / 2)
                - ((changeButton.getSize().width + resetButton.getSize().width + 5) / 2), 5);
        p.add(changeButton);

//        resetButton.setLocation((p.getSize().width / 2),5);
        resetButton.setLocation((p.getSize().width / 2)
                - ((changeButton.getSize().width + resetButton.getSize().width + 5) / 2)
                + changeButton.getSize().width + 5, 5);
        p.add(resetButton);

        // delete mag if user is geen single school student
        deleteButton = new JButton(TextMapper.getText(TextMapper.GUIP_BTN_DELETE_PROFILE));//, GuiConstants.MAIN_BACKGROUND);
        //fm = deleteButton.getFontMetrics(deleteButton.getFont());
        //deleteButton.setSize(fm.stringWidth(deleteButton.getLabel()) + 20, fm.getHeight() + 10);
        deleteButton.setSize(deleteButton.getPreferredSize());
        deleteButton.setLocation(getSize().width / 2
                - deleteButton.getSize().width / 2, p.getLocation().y
                + p.getSize().height + 10);
        deleteButton.setVisible(!prop.getUser().getSingleSchool());
        this.add(deleteButton);
        changeButton.addActionListener(this);
        resetButton.addActionListener(this);
        // delete mag if user is geen single school student
        deleteButton.addActionListener(this);
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

    /**
     * Invoked when an action occurs.
     *
     * @param e The ActionEvent.
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == resetButton) {
            /* Reset all the fields */
            oldpassword.setText("");
            password.setText("");
            repassword.setText("");
            firstname.setText(user.getGivenName());
            middlename.setText(user.getInsertion());
            familyname.setText(user.getFamilyName());
            email.setText(user.getEmail());
        } else if (e.getSource() == changeButton) {
            if (prop.getUser().getPassword().equals(MD5.getHashString(oldpassword.getText()))) {
                //update the data
                try {
                    prop.getUser().setGivenName(firstname.getText());
                    prop.getUser().setInsertion(middlename.getText());
                    prop.getUser().setFamilyName(familyname.getText());
                    prop.getUser().setEmail(email.getText());
                    if (password.getText().equals("")
                            && repassword.getText().equals(password.getText())) {
                        //leave password as it is
                    } else if (!password.getText().equals("")
                            && repassword.getText().equals(password.getText())) {
                        if (GuiCreator.instance().ShowConfirmDialog(this,
                                Dwo2ExceptionTranslator.getLocalizedCodeExplanation(DwoHelper.getLocale(), Dwo2ExceptionCode.User_ConfirmPasswordSwitch)
                        ) == JOptionPane.OK_OPTION) {
                            prop.getUser().setPassword(MD5.getHashString(password.getText()));
                        } else {
                            return;
                        }
                    } else {
                        //warn
                        GuiCreator.instance().ShowMessageDialog(this, TextMapper.getText(TextMapper.EXR_WRONG_SECOND_PASSWORD));
                        return;
                    }
                    prop.Update();
                    oldpassword.setText("");
                    GuiCreator.instance().ShowMessageDialog(this, TextMapper.getText(TextMapper.DLG_CONFIRM));
                } catch (Dwo2Exception ex) {
                    GuiCreator.instance().ShowErrorDialog(this, ex);
                }
            } else {
                GuiCreator.instance().ShowMessageDialog(this, TextMapper.getText(TextMapper.EXR_WRONG_USERNAME_PASSWORD));
            }
//            if (prop.getUser().getPassword().equals(MD5.getHashString(oldpassword.getText()))) {
//                //update the data
//                try {
//                    prop.getUser().setGivenName(firstname.getText());
//                    prop.getUser().setInsertion(middlename.getText());
//                    prop.getUser().setFamilyName(familyname.getText());
//                    prop.getUser().setEmail(email.getText());
//                    if (!password.getText().equals("")
//                            && repassword.getText().equals(password.getText())) {
//                        //updates password following some logic.
//                        prop.getUser().setPassword(MD5.getHashString(password.getText()));
//                    }
//                    prop.Update();
//                    oldpassword.setText("");
//                    GuiCreator.instance().ShowMessageDialog(this, TextMapper.getText(TextMapper.DLG_CONFIRM));
//                }
//                catch (Dwo2Exception ex) {
//                    GuiCreator.instance().ShowErrorDialog(this, ex);
//                }
//            } else {
//                GuiCreator.instance().ShowMessageDialog(this, TextMapper.getText(TextMapper.EXR_WRONG_USERNAME_PASSWORD));
//            }
        } else if (e.getSource() == deleteButton) {
            /* Delete the user account */
            while (JOptionPane.showConfirmDialog(this, TextMapper.getText(TextMapper.GUIP_CONFIRM_REMOVE_USER)
                    + "?", TextMapper.getText(TextMapper.GUIP_CONFIRM_REMOVE_USER_TITLE), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (ReauthenticatePanel.Reauthenticate(TextMapper.getText(TextMapper.GUIP_CONFIRM_REMOVE_USER_TITLE)).equals(ReauthenticateResult.SUCCEEDED)) {
                    try {
                        if (SecureUserAccountManager.removeAccountData()) {
                            GuiCreator.instance().logoff();
                        }
                        JOptionPane.showMessageDialog(this, TextMapper.getText(TextMapper.GUIP_MSG_USER_REMOVED), TextMapper.getText(TextMapper.GUIP_MSG_USER_REMOVED), JOptionPane.PLAIN_MESSAGE);
                    } catch (Dwo2Exception ex) {
                        Logger.getLogger(AccountDataFullUserJPanel.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(this, ex.getLocalizedCodeExplanation(DwoHelper.getLocale()), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN), JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                } else {
                    JOptionPane.showMessageDialog(this, TextMapper.getText(TextMapper.GUIW_ERR_LOGIN), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN), JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void setUser(DomUserFull aUser) {
        user = aUser;
//        oldpassword.setText(aUser.getPassword());
        oldpassword.setText("");
        password.setText("");
        repassword.setText("");
        firstname.setText(aUser.getGivenName());
        middlename.setText(aUser.getInsertion());
        familyname.setText(aUser.getFamilyName());
        email.setText(aUser.getEmail());
    }

    public DomUserFull getUser() {
        if (!password.getText().equals("")
                && repassword.getText().equals(password.getText())) {
            //updates password following some logic.
            user.setPassword(MD5.getHashString(password.getText()));
        }
        user.setGivenName(firstname.getText());
        user.setInsertion(middlename.getText());
        user.setFamilyName(familyname.getText());
        user.setEmail(email.getText());
        return user;
    }
}
