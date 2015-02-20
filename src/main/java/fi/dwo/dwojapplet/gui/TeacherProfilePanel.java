// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\TeacherProfilePanel.java
package fi.dwo.dwojapplet.gui;

import java.awt.Button;
import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import fi.dwo.commons.exceptions.LoginException;
import fi.dwo.commons.exceptions.RegisterException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Group;

/**
 * This class represents a panel for the current user (who is a teacher) to
 * change his account.
 *
 * @author M.J.B. Kupers
 *
 */
public class TeacherProfilePanel extends ProfilePanel {

    private Button addClassButton;

    /**
     * Creates a new ProfilePanel for the current user who is a teacher. The
     * account of the teacher can be changed.
     */
    public TeacherProfilePanel() {
        /* The teacher has already chosen a group */
        super(new Group[0]);
    }

    /**
     * This method returns the specific panel for the teacher. Because there is
     * no teacher-specific information, it returns a panel with the height of
     * zero pixels.
     *
     * @param lastPanel The panel where under this panel must appear.
     * @return A panel representing user-specific information.
     */
    @Override
    protected Container getUserPanel(Container lastPanel) {
        int posY = lastPanel.getLocation().y + lastPanel.getSize().height - 1;
        JPanel p;
        p = new JPanel(null); //p.setBorder(BorderFactory.createLineBorder(getForeground()));
        p.setBackground(GuiConstants.SUB_BACKGROUND);
        p.setBounds(getSize().width / 2 - 155, posY, 0, 0);
        return p;

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
            /* Reset the inputfields */
            oldpassword.setText("");
            password.setText("");
            repassword.setText("");
            firstname.setText(user.getFirstname());
            middlename.setText(user.getMiddleName());
            lastname.setText(user.getLastName());
            email.setText(user.getEmail());

        } else if (e.getSource() == changeButton) {
            /* Change the account */
            String pwd = password.getText();
            String repwd = repassword.getText();
            if (pwd.equals("") && repwd.equals("")) {
                pwd = oldpassword.getText();
                repwd = oldpassword.getText();
            }
            try {
                GuiCreator.instance().changeAccount(oldpassword.getText(), pwd, repwd, firstname.getText(), middlename.getText(), lastname.getText(), email.getText());
                JOptionPane.showMessageDialog(this, TextMapper.getText(TextMapper.GUIP_MSG_PROFILE_CHANGED));
                /* Evil trick to refresh user info */
                if (password.getText().equals("")) {
                    GuiCreator.instance().clearCurrentUserData();
                    GuiCreator.instance().login(user.getUsername(), oldpassword.getText());
                } else {
                    GuiCreator.instance().clearCurrentUserData();
                    GuiCreator.instance().login(user.getUsername(), password.getText());
                }
            } catch (RegisterException exc) {
                JOptionPane.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
            } catch (LoginException exc) {
                JOptionPane.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN), JOptionPane.ERROR_MESSAGE);
            }

        } else if (e.getSource() == deleteButton) {
            if (JOptionPane.showConfirmDialog(this, TextMapper.getText(TextMapper.GUIP_CONFIRM_REMOVE_USER)
                    + "?", TextMapper.getText(TextMapper.GUIP_CONFIRM_REMOVE_USER_TITLE), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                GuiCreator.instance().deleteUser();
            }
        }

    }
}
