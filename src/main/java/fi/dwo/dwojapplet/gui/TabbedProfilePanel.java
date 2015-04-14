// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\ProfilePanel.java
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.exceptions.LoginException;
import fi.dwo.commons.exceptions.RegisterException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Group;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.gui.panels.JPanelMyProfile;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;

/**
 * This class represents a panel for the current user to change his account.
 *
 * @author M.J.B. Kupers
 *
 */
public class TabbedProfilePanel extends JPanelMyProfile implements CenterSubPanel,
        ActionListener {

    protected Group groupList[];

    protected SchoolClass classList[];

    protected JPasswordField oldpassword;

    protected JPasswordField password;

    protected JPasswordField repassword;

    protected JTextField firstname;

    protected JTextField middlename;

    protected JTextField lastname;

    protected JTextField email;

    protected JTextField schoollogin;

    private JPasswordField schoolpassword;

    protected JButton changeButton;

    protected JButton resetButton;

    protected JButton deleteButton;

    private JComboBox groupChoice;

    protected User user;

    /**
     * Creates a new ProfilePanel for the current user. The account of the
     * current user can be changed.
     *
     * @param groups The possible groups wherefrom a user can be part of.
     */
    public TabbedProfilePanel(Group[] groups) {
        super();
    }

 

    /**
     * Indicate that another panel is loaded and the connections of this panel
     * must be closed.
     */
    @Override
    public void end() {

    }

    /**
     * Sets the centerpanel to communicate with.
     *
     * @param centerPanel The centerPanel to communicate with.
     */
    @Override
    public void setCenterPanel(CenterPanel centerPanel) {

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
            firstname.setText(user.getFirstname());
            middlename.setText(user.getMiddleName());
            lastname.setText(user.getLastName());
            email.setText(user.getEmail());
            if (user.getSchool() == null) {
                schoollogin.setText("");
                groupChoice.setSelectedIndex(0);
                schoolpassword.setText("");
                schoolpassword.setVisible(true);
            } else {
                if (user.getInClass() != null) {
                    groupChoice.setSelectedItem(user.getInClass().getName());
                } else {
                    groupChoice.setSelectedIndex(0);
                }
            }

        } else if (e.getSource() == changeButton) {
            /* Change the account */
            boolean correct = true;
            String pwd = password.getText();
            String repwd = repassword.getText();
            if (pwd.equals("") && repwd.equals("")) {
                pwd = oldpassword.getText();
                repwd = oldpassword.getText();
            }
            if (user.getSchool() == null) {
                /* User was not linked to a school */
                if ((groupChoice.getSelectedIndex() == 0) && (schoollogin.getText().equals("")) && (schoolpassword.getText().equals(""))) {
                    /* Don't link the user to a school */
                    try {
                        GuiCreator.instance().changeAccount(oldpassword.getText(), pwd, repwd, firstname.getText(), middlename.getText(), lastname.getText(), email.getText());
                    } catch (RegisterException exc) {
                        JOptionPane.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
                        correct = false;
                    }

                } else {
                    /* Link the user to a school */
                    Group g = null;
                    if (groupChoice.getSelectedIndex() > 0) {
                        g = groupList[groupChoice.getSelectedIndex() - 1];
                    }
                    try {
                        GuiCreator.instance().changeAccount(oldpassword.getText(), pwd, repwd, firstname.getText(), middlename.getText(), lastname.getText(), email.getText(), schoollogin.getText(), g, schoolpassword.getText());
                    } catch (RegisterException exc) {
                        JOptionPane.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
                        correct = false;
                    }

                }
            } 
            //TODO MANY TO MANY: parse class management panel.
//            else {
//                /* User was already linked to a school */
//                SchoolClass sc = null;
//                if ((user.getInClass() == null)
//                        && (groupChoice.getSelectedIndex() != 0)) {
//                    /* User not linked to a class and a class is chosen */
//                    sc = classList[groupChoice.getSelectedIndex() - 1];
//                } else {
//                    /*
//                     * User was already linked to a class, but maybe he is in an
//                     * other class?
//                     */
//                    sc = classList[groupChoice.getSelectedIndex()];
//                }
//                try {
//                    if (sc != null) {
//                        /* Add user to a class */
//                        GuiCreator.instance().changeAccount(oldpassword.getText(), pwd, repwd, firstname.getText(), middlename.getText(), lastname.getText(), email.getText(), sc);
//                    } else {
//                        GuiCreator.instance().changeAccount(oldpassword.getText(), pwd, repwd, firstname.getText(), middlename.getText(), lastname.getText(), email.getText());
//                    }
//                } catch (RegisterException exc) {
//                    JOptionPane.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
//                    correct = false;
//                }
//
//            }

            if (correct) {
                /*
                 * The data is changed correctly, show a message for
                 * successfully changed data in a dialog
                 */
                try {
                    JOptionPane.showMessageDialog(this, TextMapper.getText(TextMapper.GUIP_MSG_PROFILE_CHANGED));
                    /* Evil trick to refresh user info */
                    if (password.getText().equals("")) {
                        // TODO this erases the canLogout flag.
                        GuiCreator.instance().clearCurrentUserData();
                        GuiCreator.instance().login(user.getUsername(), oldpassword.getText());
                    } else {
                        GuiCreator.instance().clearCurrentUserData();
                        GuiCreator.instance().login(user.getUsername(), password.getText());
                    }
                } catch (LoginException exc) {
                    JOptionPane.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN), JOptionPane.ERROR_MESSAGE);

                }
            }

        } else if (e.getSource() == deleteButton) {
            /* Delete the user account */
            if (JOptionPane.showConfirmDialog(this, TextMapper.getText(TextMapper.GUIP_CONFIRM_REMOVE_USER)
                    + "?", TextMapper.getText(TextMapper.GUIP_CONFIRM_REMOVE_USER_TITLE), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                GuiCreator.instance().deleteUser();
            }
        }

    }

    /**
     * Returns a Panel that can functionate as a header panel.
     *
     * @return A panel that can functionate as a header panel.
     * @see fi.dwo.client.gui.CenterSubPanel#getHeaderPanel()
     */
    @Override
    public Component getHeaderPanel() {
        return new HeaderPanel(TextMapper.getText(TextMapper.GUIP_MY_PROFILE));
    }

    /**
     * Returns the current object, as the object to add to a gui.
     *
     * @return the current object.
     * @see fi.dwo.client.gui.CenterSubPanel#getComponent()
     */
    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public Object getUserObject() {

        return null;
    }

    @Override
    public void stateChanged(ChangeEvent e) {


    }
}
