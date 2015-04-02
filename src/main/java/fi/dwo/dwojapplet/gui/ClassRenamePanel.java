/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.system.TextMapper;
import fi.dwo.commons.util.RandomPasswordGenerator;
import fi.dwo.dwojapplet.domain.SchoolClass;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * ClassRenamePanel
 * 
 * Queries user for a new configuration of the SchoolClass attributes.
 * 
 * @author G.A.J. van der Plas
 */
public class ClassRenamePanel extends JPanel implements ActionListener,
		FocusListener {

	private Color STYLE_COLOUR;
	private final Color WARN_COLOR = Color.RED;

	// componentList
	private JLabel classLabel;
	private JTextField classTextField;
	private JCheckBox treeCB;
	private JCheckBox classKeyCB;
	private JLabel registrationKeyLabel;
	private JTextField registrationKeyTextField;

	// propertyList
	private String className;
	// private String classKeyTextName;
	private String registrationKey; // not null means it is set
	private boolean iconizer;

	public ClassRenamePanel() {

		// Constructing Pane
		classLabel = new JLabel(TextMapper.getText("klasnaam"));
		classTextField = new JTextField(25);
		classTextField.addFocusListener(this);
		classTextField.requestFocusInWindow();// request focus for typing

		treeCB = new JCheckBox(TextMapper.getText("boomstructuur?"));
		treeCB.addActionListener(this);

		classKeyCB = new JCheckBox(TextMapper.getText("klasssleutel gebruiken"));
		classKeyCB.addActionListener(this);

		registrationKeyLabel = new JLabel(
				TextMapper.getText("registratiesleutel"));
		registrationKeyLabel.setVisible(false);

		registrationKeyTextField = new JTextField(25);

		STYLE_COLOUR = registrationKeyTextField.getForeground();
		registrationKeyTextField
				.setToolTipText(TextMapper
						.getText("Geef een registratiesleutel van tussen de 5 en 100 karakters op."));
		registrationKeyTextField.addFocusListener(this);
		registrationKeyTextField.setVisible(false);
		// intialize a random password when the registration key is enabled and
		// the length is less than 5.

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		// local layout creates space for invisible objects
		layout.setHonorsVisibility(false);
		// link horizontal size of class and classKey textfields for prettier
		// layout
		layout.linkSize(SwingConstants.HORIZONTAL, classTextField,
				registrationKeyTextField);

		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.TRAILING)
								.addComponent(classLabel)
								.addComponent(registrationKeyLabel)
				)
				.addContainerGap()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.LEADING)
								.addComponent(classTextField)
								.addComponent(treeCB).addComponent(classKeyCB)
								.addComponent(registrationKeyTextField)));

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(classLabel)
								.addComponent(classTextField))
				.addComponent(treeCB)
				.addComponent(classKeyCB)
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(registrationKeyLabel)
								.addComponent(registrationKeyTextField)));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == treeCB) {
			if (treeCB.isSelected()) {
				setIconizer(true);
			} else {
				setIconizer(false);
			}
			classKeyCB.requestFocusInWindow();
		} else if (e.getSource() == classKeyCB) {
			if (classKeyCB.isSelected()) {
				registrationKeyLabel.setVisible(true);
				registrationKeyTextField.setVisible(true);
				registrationKeyTextField.requestFocusInWindow();
				if (getRegistrationKey() == null
						|| getRegistrationKey().length() < 5) {
					RandomPasswordGenerator generator = RandomPasswordGenerator
							.instance();
					String key = generator.Generate(5);
					setRegistrationKey(key);

				}
			} else {
				registrationKeyLabel.setVisible(false);
				registrationKeyTextField.setVisible(false);
				setRegistrationKey(null);

			}
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (e.getSource() == classTextField) {
			setClassName(classTextField.getText());
		} else if (e.getSource() == registrationKeyTextField) {
			setRegistrationKey(registrationKeyTextField.getText());
			if (getRegistrationKey() == null
					|| getRegistrationKey().length() == 0) {
				RandomPasswordGenerator generator = RandomPasswordGenerator
						.instance();
				String key = generator.Generate(5);
				setRegistrationKey(key);
			} else
				setRegistrationKey(registrationKeyTextField.getText());
			if (getRegistrationKey().length() < 5) {
				registrationKeyTextField.setForeground(WARN_COLOR);
			} else {
				registrationKeyTextField.setForeground(STYLE_COLOUR);
			}
		}
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className
	 *            the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
		this.classTextField.setText(className);
	}

	/**
	 * @return the registrationKey
	 */
	public String getRegistrationKey() {
		return registrationKey;
	}

	/**
	 * @param registrationKey
	 *            the registrationKey to set
	 */
	public void setRegistrationKey(String registrationKey) {
		this.registrationKey = registrationKey;
		this.registrationKeyTextField.setText(registrationKey);
	}

	/**
	 * @return the iconizer
	 */
	public boolean isIconizer() {
		return iconizer;
	}

	/**
	 * @param iconizer
	 *            
	 */
	public void setIconizer(boolean iconizer) {
		this.iconizer = iconizer;
		this.treeCB.setSelected(iconizer);
	}

	/**
	 * Sets the panel attributes according to the values of SchoolClass
	 * 
	 * @param sc
	 *            the SchoolClass
	 */
	public void setSchoolClass(SchoolClass sc) {
		setClassName(sc.getName());
		setIconizer(sc.hasIconizer());
		if (sc.getRegistrationKey() != null
				&& sc.getRegistrationKey().length() != 0) {
			this.classKeyCB.setSelected(true);
			registrationKeyLabel.setVisible(true);
			registrationKeyTextField.setVisible(true);
		}
		setRegistrationKey(sc.getRegistrationKey());
	}

}
