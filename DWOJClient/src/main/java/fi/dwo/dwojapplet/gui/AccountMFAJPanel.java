package fi.dwo.dwojapplet.gui;

import java.awt.Color;
import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

import fi.beans.numworxlf.JLabel;
import fi.beans.numworxlf.JTextField;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserMFAManager;
import nl.uu.fi.dwo.rest.dom.mfa.MFA;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

@SuppressWarnings("serial")
class AccountMFAJPanel extends JPanel {
	
	JTextField secretField;
	JTextArea  recoveryArea;
	JLabel qrLabel;
	private boolean verified;
	JTextField code;

	public AccountMFAJPanel(MFA mfa) {
		SpringLayout layout = new SpringLayout();
		setBackground(Color.white);
		setLayout(layout);
		JLabel secret = new JLabel("Secret:");
		add(secret);
		secretField = new JTextField(mfa.secret);
		secretField.setEditable(false);
		add(secretField);
		JLabel recovery = new JLabel("Recovery codes:");
		add(recovery);
		StringBuilder lines = new StringBuilder();
		mfa.recovery.forEach(line -> lines.append(line).append("\n"));
		recoveryArea = new JTextArea(lines.toString());
		recoveryArea.setEditable(false);
		recoveryArea.setBorder(BorderFactory.createLineBorder(Color.black));
		add(recoveryArea);
		try {
			Image im;
			if (mfa.qr.startsWith("data:")) {
				int komma = mfa.qr.indexOf(',');
				String base64 = mfa.qr.substring(komma+1);
				byte[] data = Base64.getDecoder().decode(base64);
				im = getToolkit().createImage(data);
			} else
				im = getToolkit().createImage(new URL(mfa.qr));
			qrLabel = new JLabel(new ImageIcon(im));
			add(new JLabel("QRCode:"));
			add(qrLabel);
		} catch (MalformedURLException e) {
		}
		JLabel codelabel = new JLabel("2FA code:");
		add(codelabel);
		code = new JTextField();
		code.setColumns(7);
		code.addActionListener((ev) -> {
			try {
				verified = SecureUserMFAManager.verify(code.getText());
				if (verified) code.setBorder(BorderFactory.createLineBorder(Color.GREEN));
				else code.setBorder(BorderFactory.createLineBorder(Color.red));
			} catch (Dwo2Exception e) {
				code.setBorder(BorderFactory.createLineBorder(Color.red));
				code.setText("");
			}
		});
		add(code);
		
	
        AddSchoolDialog.makeCompactGrid(this, //parent
                getComponentCount() / 2, 2,
                10, 10, //initX, initY
                10, 10); //xPad, yPad
	}

	public boolean isVerified() {
		return verified;
	}


}
