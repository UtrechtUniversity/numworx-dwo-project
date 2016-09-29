package fi.dwo.dwojapplet.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.dwojapplet.domain.AppletData;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import fi.dwo.rest.dom.entities.DomAppletConfig;

class AddConfigDwoAdminJPanel extends JPanel implements Comparator<AppletData >{

	class ImportScoAction extends AbstractAction {

		public ImportScoAction() {
			super("Open File");
			// TODO Auto-generated constructor stub
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			int ok = chooser.showOpenDialog((Component) e.getSource());
			if( ok == JFileChooser.APPROVE_OPTION)
			{
				File f = chooser.getSelectedFile();
				System.out.println("open "  +f );
			}
		}
		
	}
	
	
	private DomAppletConfig config;
	private JLabel idField;
	private JTextField nameField;
	private JTextField langField;
	private JTextArea dataField;
	private JComboBox<AppletData> appletField;

	private AddConfigDwoAdminJPanel(DomAppletConfig config) {
		super(new SpringLayout());
		this.config = config;
		String id = "-"; 
		try {
			id = String.valueOf(MySQLPersistenceId.getId(config.getId()));
		} catch (Exception e) {
		}
		idField   = new JLabel(id);
		nameField = new JTextField(config.getName());
		langField = new JTextField(config.getLanguage());
		dataField = new JTextArea(config.getLaunchdata(), 3, 30);
		AppletData[] list = new AppletData[1];
		list[0] = new AppletData();
		list[0].setId(config.getAppletID());
		list[0].setAppletName("");
		try {
			list = (AppletData[]) PersistenceFacade.instance().get(AppletData.class);
		} catch (PersistenceException e) {
		}
		Arrays.sort(list, this);
		AppletData current = list[0];
		for (int i = 0; i < list.length; i++) {
			AppletData appletData = list[i];
			if(appletData.getId() == config.getAppletID().intValue())
				current = appletData;
		}
		appletField = new JComboBox<AppletData>(list);
		appletField.setSelectedItem(current);

		add( new JLabel("id"));add(idField);
		add( new JLabel("naam")); add(nameField);
		add( new JLabel("taal")); add(langField);
		add( new JLabel("applet")); add(appletField);
		add( new JLabel("launchdata")); add(new JScrollPane(dataField));
		add( new JLabel("Import SCO")); add(new JButton(new ImportScoAction()));
		
        AddSchoolDialog.makeCompactGrid(this, //parent
                getComponentCount() / 2, 2,
                10, 10, //initX, initY
                10, 10); //xPad, yPad
	}

	static DomAppletConfig editDialog(JComponent parent, DomAppletConfig edit) {
		AddConfigDwoAdminJPanel panel = new AddConfigDwoAdminJPanel(edit);
		int ok = JOptionPane.showConfirmDialog(parent, panel, "Edit", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if(ok == JOptionPane.OK_OPTION)
			return panel.edit();
		return null;
	}

	public static DomAppletConfig addDialog(JComponent jtbl) {
		DomAppletConfig edit = new DomAppletConfig();
		// set reasonable defaults..
		edit.setName("- Template");
		edit.setLanguage(jtbl.getLocale().toString());
		edit.setLaunchdata("");
		edit.setAppletID(17);
		edit.setId(null);
		
		AddConfigDwoAdminJPanel panel = new AddConfigDwoAdminJPanel(edit);
		int ok = JOptionPane.showConfirmDialog(jtbl, panel, "Nieuw", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if(ok == JOptionPane.OK_OPTION)
			return panel.edit();
		return null;
	}

	private DomAppletConfig edit() {
		config.setAppletID(((AppletData) appletField.getSelectedItem()).getId());
		config.setLanguage(langField.getText());
		config.setLaunchdata(dataField.getText());
		config.setName(nameField.getText());
		return config;
	}

	@Override
	public int compare(AppletData o1, AppletData o2) {
		return o1.getAppletName().compareTo(o2.getAppletName());
	}

}
