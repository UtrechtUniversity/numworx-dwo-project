/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.system.MD5;
import fi.dwo.commons.system.TextMapper;
import static fi.dwo.commons.system.TextMapper.BTN_CANCEL;
import static fi.dwo.commons.system.TextMapper.BTN_OK;
import static fi.dwo.commons.system.TextMapper.LBL_PASSWORD;
import static fi.dwo.commons.system.TextMapper.LBL_USERNAME;
import fi.dwo.dwojapplet.domain.DwoHelper;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ReauthenticatePanel extends JPanel implements ActionListener {

    private JLabel usernameLabel = new JLabel(TextMapper.getText(LBL_USERNAME));
    private JTextField usernameField = new JTextField(25);
    private JLabel passwordLabel = new JLabel(TextMapper.getText(LBL_PASSWORD));
    private JPasswordField passwordField = new JPasswordField(25);
    private JLabel messageLabel = new JLabel("");

    public ReauthenticatePanel() {
        usernameField.setEditable(false);
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        // local layout creates space for invisible objects
        layout.setHonorsVisibility(false);
		// link horizontal size of class and classKey textfields for prettier
        // layout
//        layout.linkSize(SwingConstants.HORIZONTAL, usernameLabel,
//                usernameField);
        layout.setHorizontalGroup(layout.createParallelGroup().addComponent(messageLabel).addGroup(
                layout.createSequentialGroup()
                .addGroup(
                        layout.createParallelGroup(
                                GroupLayout.Alignment.TRAILING)
                        .addComponent(usernameLabel)
                        .addComponent(passwordLabel)
                )
                .addContainerGap(10,50)
                .addGroup(
                        layout.createParallelGroup(
                                GroupLayout.Alignment.LEADING)
                        .addComponent(usernameField)
                        .addComponent(passwordField))
        ));

        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(messageLabel).addGroup(layout
                .createSequentialGroup()
                .addComponent(messageLabel)
                .addGroup(
                        layout.createParallelGroup(
                                GroupLayout.Alignment.BASELINE)
                        .addComponent(usernameLabel)
                        .addComponent(usernameField))
                .addGroup(
                        layout.createParallelGroup(
                                GroupLayout.Alignment.BASELINE)
                        .addComponent(passwordLabel)
                        .addComponent(passwordField))));
    }

    static public boolean Reauthenticate(String msg) {
        ReauthenticatePanel panel = new ReauthenticatePanel();
        panel.messageLabel.setText(msg);

        panel.usernameField.setText(DwoHelper.getCurrentUser().getUsername());
        String[] options = new String[]{TextMapper.getText(BTN_OK), TextMapper.getText(BTN_CANCEL)};
        int option = JOptionPane.showOptionDialog(null, panel, "",
                JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[1]);
        if (option == 0) // pressing OK button
        {
             return DwoHelper.getCurrentUser().getPasswd().equals(MD5.getHashString(panel.passwordField.getText()));
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
