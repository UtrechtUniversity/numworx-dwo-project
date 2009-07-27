// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\ProfilePanel.java

package fi.dwo.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
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
import fi.dwo.client.domain.SchoolClass;
import fi.dwo.client.domain.User;
import fi.dwo.client.system.LoginException;
import fi.dwo.client.system.RegisterException;
import fi.dwo.client.system.TextMapper;

/**
 * This class represents a panel for the current user to change his account.
 * 
 * @author M.J.B. Kupers
 *  
 */
public class ProfilePanel extends JPanel implements CenterSubPanel,
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
    public ProfilePanel(Group[] groups) {
        user = GuiCreator.instance().getUser();
        groupList = groups;
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setSize(600, 480);
        this.setPreferredSize(getSize());
        this.setLayout(null);

        /* Variables used to create items */
        FontMetrics fm;
        JPanel p;
        JLabel l;

        /* Add Register-panel */
        p = new JPanel(null); p.setBorder(BorderFactory.createLineBorder(getForeground()));
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
        l = new JLabel(user.getUsername());
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
        l.setVisible(!user.isReadonly());
        p.add(l);

        /* Old-Password field */
        oldpassword = new JPasswordField();
        oldpassword.setBounds(160, 53, 120, 20);
        oldpassword.setEchoChar('*');
        oldpassword.setVisible(!user.isReadonly());
        p.add(oldpassword);

        /* Old-Password mandatory label */
        l = createMandatoryLabel();
        l.setLocation(285, 53);
        l.setVisible(!user.isReadonly());
        p.add(l);

        /* Password label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIP_PASSWORD) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 80);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        l.setVisible(!user.isReadonly());
        p.add(l);

        /* Password field */
        password = new JPasswordField();
        password.setBounds(160, 78, 120, 20);
        password.setEchoChar('*');
        password.setVisible(!user.isReadonly());
        p.add(password);

        /* RePassword label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIP_RE_PASSWORD) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 105);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        l.setVisible(!user.isReadonly());
        p.add(l);

        /* RePassword field */
        repassword = new JPasswordField();
        repassword.setBounds(160, 103, 120, 20);
        repassword.setEchoChar('*');
        repassword.setVisible(!user.isReadonly());
        p.add(repassword);

        /* Add PersonalInfo-panel */
        p = new JPanel(null); p.setBorder(BorderFactory.createLineBorder(getForeground()));
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
        firstname.setText(user.getFirstname());
        firstname.setBounds(160, 28, 120, 20);
        firstname.setEditable(!user.isReadonly());
        p.add(firstname);

        /* Firstname mandatory label */
        l = createMandatoryLabel();
        l.setLocation(285, 28);
        p.add(l);

        /* Middlename label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIP_MIDDLENAME) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 55);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* Middlename field */
        middlename = new JTextField();
        middlename.setText(user.getMiddleName());
        middlename.setBounds(160, 53, 120, 20);
        middlename.setEditable(!user.isReadonly());
        p.add(middlename);

        /* Lastname label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIP_LASTNAME) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 80);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* Lastname field */
        lastname = new JTextField();
        lastname.setText(user.getLastName());
        lastname.setBounds(160, 78, 120, 20);
        lastname.setEditable(!user.isReadonly());
        p.add(lastname);

        /* Lastname mandatory label */
        l = createMandatoryLabel();
        l.setLocation(285, 78);
        p.add(l);

        /* Email label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIP_EMAIL) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 105);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        p.add(l);

        /* Email field */
        email = new JTextField();
        email.setText(user.getEmail());
        email.setBounds(160, 103, 120, 20);
        email.setEditable(!user.isReadonly());
        p.add(email);

        /* Email mandatory label */
        l = createMandatoryLabel();
        l.setLocation(285, 103);
        p.add(l);
        Container c = p;
// readonly users are unable to set school stuff.
// TODO hoe zit dat dan precies?
        if(!user.isReadonly() || user.getSchool() != null )
        {
        	add(c = getUserPanel(p));
        } else {
        	getUserPanel(p);
        }
        if(!user.isReadonly())
        	addButtonsPanel(c);

    }

    /**
     * This method returns the specific panel for the user. <BR>
     * Can be overridden by subclasses.
     * 
     * @param p2 The panel where under this panel must appear.
     * @return A panel representing user-specific information.
     */
    protected Container getUserPanel(Container p2) {
        int posY = p2.getLocation().y + p2.getSize().height - 1;
        /* We don't know the school */

        JPanel p;
        JLabel l;
        FontMetrics fm;
        if (user.getSchool() == null) {
            /* Add School-panel */
            p = new JPanel(null); p.setBorder(BorderFactory.createLineBorder(getForeground()));
            p.setBackground(GuiConstants.SUB_BACKGROUND);
            p.setBounds(getSize().width / 2 - 155, posY, 310, 115);
            this.add(p);

            /* schoolinfo label */
            l = new JLabel(TextMapper.getText(TextMapper.GUIP_SCHOOLINFO) + ":");
            l.setForeground(GuiConstants.RED_COLOR);
            l.setFont(GuiConstants.RED_TEXT);
            fm = l.getFontMetrics(l.getFont());
            l.setBounds(10, 5, fm.stringWidth(l.getText()), fm.getHeight());
            p.add(l);

            /* schoolinfo sublabel */
            l = new JLabel(TextMapper.getText(TextMapper.GUIP_MSG_PROVIDED_SCHOOL)
                    + ":");
            l.setForeground(GuiConstants.RED_COLOR);
            l.setFont(GuiConstants.SMALL_TEXT);
            fm = l.getFontMetrics(l.getFont());
            l.setBounds(10, 22, fm.stringWidth(l.getText()), fm.getHeight());
            p.add(l);

            /* Schoologin label */
            l = new JLabel(TextMapper.getText(TextMapper.GUIP_SCHOOLLOGIN) + ":");
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
            l = new JLabel(TextMapper.getText(TextMapper.GUIP_SCHOOLGROUP) + ":");
            l.setForeground(Color.black);
            l.setFont(GuiConstants.NORMAL_TEXT);
            fm = l.getFontMetrics(l.getFont());
            l.setLocation(10, 65);
            l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
            p.add(l);

            /* Password field */
            groupChoice = new JComboBox();
            groupChoice.setBackground(p.getBackground());
            groupChoice.setFont(GuiConstants.NORMAL_TEXT);
            groupChoice.addItem(TextMapper.getText(TextMapper.GUIP_OPT_SELECT_GROUP));
            for (int i = 0; i < groupList.length; i++) {
                groupChoice.addItem(TextMapper.getText(groupList[i].getName()));
            }
            groupChoice.setBounds(160, 63, 120, 20);
            p.add(groupChoice);

            /* Schoolpassword label */
            l = new JLabel(TextMapper.getText(TextMapper.GUIP_SCHOOLPASSWORD)
                    + ":");
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
            groupChoice.addItemListener(new GroupItemListener(schoolpassword));
            
        } else {
            /* We know the school, so show the classlist */
            /* Add School-panel */
            p = new JPanel(null); p.setBorder(BorderFactory.createLineBorder(getForeground()));
            p.setBackground(GuiConstants.SUB_BACKGROUND);
            p.setBounds(getSize().width / 2 - 155, posY, 310, 80);
            this.add(p);

            /* schoolinfo label */
            l = new JLabel(TextMapper.getText(TextMapper.GUIP_SCHOOLINFO) + ":");
            l.setForeground(GuiConstants.RED_COLOR);
            l.setFont(GuiConstants.RED_TEXT);
            fm = l.getFontMetrics(l.getFont());
            l.setBounds(10, 5, fm.stringWidth(l.getText()), fm.getHeight());
            p.add(l);

            /* Schoologin label */
            l = new JLabel(TextMapper.getText(TextMapper.GUIP_SCHOOLLOGIN) + ":");
            l.setForeground(Color.black);
            l.setFont(GuiConstants.NORMAL_TEXT);
            fm = l.getFontMetrics(l.getFont());
            l.setLocation(10, 30);
            l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
            p.add(l);

            /* Schoologin label */
            l = new JLabel(user.getSchool().getName());
            if(l.getText() == null) {
                l.setText("");
            }
            l.setForeground(Color.black);
            l.setFont(GuiConstants.NORMAL_TEXT);
            fm = l.getFontMetrics(l.getFont());
            l.setLocation(160, 30);
            l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
            p.add(l);

            /* Class label */
            l = new JLabel(TextMapper.getText(TextMapper.GUIP_CLASS) + ":");
            l.setForeground(Color.black);
            l.setFont(GuiConstants.NORMAL_TEXT);
            fm = l.getFontMetrics(l.getFont());
            l.setLocation(10, 55);
            l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
            p.add(l);

            /* Class field */
            groupChoice = new JComboBox();
            groupChoice.setBackground(p.getBackground());
            groupChoice.setFont(GuiConstants.NORMAL_TEXT);
            if (user.getInClass() == null) {
                groupChoice.addItem(TextMapper.getText(TextMapper.GUIP_OPT_SELECT_GROUP));
            }

            classList = user.getSchool().getClassList();

            for (int i = 0; i < classList.length; i++) {
                groupChoice.addItem(classList[i].getName());
            }
            if (user.getInClass() != null) {
                groupChoice.setSelectedItem(user.getInClass().getName());
            }
            groupChoice.setBounds(160, 53, 120, 20);
            groupChoice.setEnabled(!user.isReadonly());
            p.add(groupChoice);
        }

        return p;

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
        JPanel p = new JPanel(null); p.setBorder(BorderFactory.createLineBorder(getForeground()));
        p.setBackground(GuiConstants.SUB_BACKGROUND);
        p.setBounds(getSize().width / 2 - 155, locationY, 310, 35);
        this.add(p);

        /* Change button */
        changeButton = new JButton(TextMapper.getText(TextMapper.GUIP_BTN_SAVE));//, GuiConstants.SUB_BACKGROUND);
        //fm = changeButton.getFontMetrics(changeButton.getFont());
        //changeButton.setSize(fm.stringWidth(changeButton.getLabel()) + 20, fm.getHeight() + 10);
        changeButton.setSize(changeButton.getPreferredSize());
        /* Reset button */
        resetButton = new JButton(TextMapper.getText(TextMapper.GUIP_BTN_RESET));//, GuiConstants.SUB_BACKGROUND);
        //fm = resetButton.getFontMetrics(resetButton.getFont());
        //resetButton.setSize(fm.stringWidth(resetButton.getLabel()) + 20, fm.getHeight() + 10);
        resetButton.setSize(resetButton.getPreferredSize());
        changeButton.setLocation((p.getSize().width / 2)
                - ((changeButton.getSize().width + resetButton.getSize().width + 5) / 2), 5);
        p.add(changeButton);

        resetButton.setLocation((p.getSize().width / 2)
                - ((changeButton.getSize().width + resetButton.getSize().width + 5) / 2)
                + changeButton.getSize().width + 5, 5);
        p.add(resetButton);

        deleteButton = new JButton(TextMapper.getText(TextMapper.GUIP_BTN_DELETE_PROFILE));//, GuiConstants.MAIN_BACKGROUND);
        //fm = deleteButton.getFontMetrics(deleteButton.getFont());
        //deleteButton.setSize(fm.stringWidth(deleteButton.getLabel()) + 20, fm.getHeight() + 10);
        deleteButton.setSize(deleteButton.getPreferredSize());
        deleteButton.setLocation(getSize().width / 2
                - deleteButton.getSize().width / 2, p.getLocation().y
                + p.getSize().height + 10);
        this.add(deleteButton);

        changeButton.addActionListener(this);
        resetButton.addActionListener(this);
        if(!user.isReadonly() && user.canLogout())
            deleteButton.addActionListener(this);
        else 
            deleteButton.setVisible(false);
    }

    /**
     * Indicate that another panel is loaded and the connections of this panel
     * must be closed.
     */
    public void end() {

    }

    /**
     * Sets the centerpanel to communicate with.
     * 
     * @param centerPanel The centerPanel to communicate with.
     */
    public void setCenterPanel(CenterPanel centerPanel) {

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
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
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
                    if(groupChoice.getSelectedIndex() > 0) {
                        g = groupList[groupChoice.getSelectedIndex() - 1];
                    }
                    try {
                        GuiCreator.instance().changeAccount(oldpassword.getText(), pwd, repwd, firstname.getText(), middlename.getText(), lastname.getText(), email.getText(), schoollogin.getText(), g, schoolpassword.getText());
                    } catch (RegisterException exc) {
                        JOptionPane.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
                        correct = false;
                    }

                }
            } else {
                /* User was already linked to a school */
                SchoolClass sc = null;
                if ((user.getInClass() == null)
                        && (groupChoice.getSelectedIndex() != 0)) {
                    /* User not linked to a class and a class is chosen */
                    sc = classList[groupChoice.getSelectedIndex() - 1];
                } else {
                    /*
                     * User was already linked to a class, but maybe he is in an
                     * other class?
                     */
                    sc = classList[groupChoice.getSelectedIndex()];
                }
                try {
                    if (sc != null) {
                        /* Add user to a class */
                        GuiCreator.instance().changeAccount(oldpassword.getText(), pwd, repwd, firstname.getText(), middlename.getText(), lastname.getText(), email.getText(), sc);
                    } else {
                        GuiCreator.instance().changeAccount(oldpassword.getText(), pwd, repwd, firstname.getText(), middlename.getText(), lastname.getText(), email.getText());
                    }
                } catch (RegisterException exc) {
                    JOptionPane.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
                    correct = false;
                }

            }

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
   
    public Component getHeaderPanel() {
    	return new HeaderPanel(TextMapper.getText(TextMapper.GUIP_MY_PROFILE));
    }

    /**
     * Returns the current object, as the object to add to a gui.
     * 
     * @return the current object.
     * @see fi.dwo.client.gui.CenterSubPanel#getComponent()
     */
    public Component getComponent() {
        return this;
    }
}