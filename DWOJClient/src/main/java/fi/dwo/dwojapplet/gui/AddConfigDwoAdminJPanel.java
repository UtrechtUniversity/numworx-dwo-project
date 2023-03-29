package fi.dwo.dwojapplet.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JCheckBox;
import fi.beans.numworxlf.JComboBox;
import fi.beans.numworxlf.JOptionPane;
import fi.beans.numworxlf.JScrollPane;
import fi.beans.numworxlf.JTextField;
import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.AppletConfig;
import fi.dwo.dwojapplet.domain.AppletData;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.gui.action.ImportScorm;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureDwoAdminAppletManager;
import nl.uu.fi.dwo.rest.dom.entities.DomAppletConfig;
import nl.uu.fi.dwo.rest.dom.entities.DomAppletFull;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

class AddConfigDwoAdminJPanel extends JPanel implements Comparator<AppletData >{

	static class Zips extends javax.swing.filechooser.FileFilter {

		@Override
		public boolean accept(File f) {
			return f.getName().endsWith(".zip") || f.isDirectory();
		}

		@Override
		public String getDescription() {
			return "ZIP Files";
		}
		
	}
	
	private static final Zips ZIP_FILTER = new Zips();
	
	class ImportScoAction extends AbstractAction  {

		private JFileChooser chooser;

		public ImportScoAction() {
			super("Open ZIP");
			chooser = new fi.beans.numworxlf.JFileChooser();
			chooser.setFileFilter(ZIP_FILTER);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Component source = (Component) e.getSource();
			chooser.setLocale(source.getLocale());
			int ok = chooser.showOpenDialog(source);
			if (ok == JFileChooser.APPROVE_OPTION)
			{
				File f = chooser.getSelectedFile();
				Sco sco = new Sco();
				ImportScorm.readZip(f.getAbsolutePath(), sco);
				String configuration = sco.getLaunchdataString();
				dataField.setText(configuration);
			}
		}
	}

	class PreviewAction extends AbstractAction {
		PreviewAction() {
			super("Preview");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Component c = (Component)e.getSource();
            AppletConfig ac = getSelectedAppletConfig();
            if (ac != null) {
                ScoPanel sp = GuiCreator.instance().previewSco(ac);
                ScoDialog.showScoPreview(c, sp);
            } else {
                JOptionPane.showMessageDialog(c, TextMapper.getText(TextMapper.GUISDLG_MSG_NO_SELECTION));
            }
		}

	}

	private DomAppletConfig config;
	private JLabel idField;
	private JTextField nameField;
	private JTextField langField;
	private JCheckBox profileField;
	private JTextArea dataField;
	private JComboBox<AppletData> appletField;

    public AppletData[] getAppletData() throws Dwo2Exception  {
        List<DomAppletFull> list = SecureDwoAdminAppletManager.getApplets();
        AppletData[] result = new AppletData[list.size()];
        for (int i = 0; i < result.length; i++) {
          result[i] = getObjectFromReturn(list.get(i));
        }
        return result;
    }

    private AppletData getObjectFromReturn(DomAppletFull dom) throws Dwo2Exception {
      int id = MySQLPersistenceId.getNativeId(dom).intValue();
      AppletData s;
      s = new AppletData();
      s.setId(id);
      s.setAppletName(dom.getAppletName());
      s.setClassName(dom.getClassname());
      s.setFeatures(dom.getFeatures());
      s.setJarName(dom.getJarname());
      return s;
    }
	
	private AddConfigDwoAdminJPanel(DomAppletConfig config) {
		super(new SpringLayout());
		this.config = config;
		String id = "-"; 
		try {
			id = String.valueOf(MySQLPersistenceId.getNativeId(config));
		} catch (Exception e) {
		}
		idField   = new JLabel(id);
		nameField = new JTextField(config.getName());
		langField = new JTextField(config.getLanguage());
		profileField = new JCheckBox(DWO.getDwoProfile().getDwoProfileName());
		if(config.getDwoProfileId() == null) {
			profileField.setSelected(false);
			profileField.setEnabled(true);
		} else {
			profileField.setEnabled(config.getDwoProfileId().getId().equals(DWO.getDwoProfile().getId()));
			profileField.setSelected(profileField.isEnabled());
		}
		dataField = new JTextArea(config.getLaunchdata(), 2, 30);
		AppletData[] list = new AppletData[1];
		list[0] = new AppletData();
		list[0].setId(config.getAppletID());
		list[0].setAppletName("");
		try {
			list = getAppletData();
		} catch (Dwo2Exception e) {
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
		add( new JLabel("profiel")); add(profileField);
		add( new JLabel("applet")); add(appletField);
		add( new JLabel("launchdata")); add(new JScrollPane(dataField));
		add( new JLabel("import SCO")); 
		JButton b1 = new JButton(new ImportScoAction());
		JButton b2 = new JButton(new PreviewAction());
		Box hbox = Box.createHorizontalBox();
		hbox.add(b1);
		hbox.add(Box.createHorizontalStrut(20));
		hbox.add(b2);
		hbox.add(Box.createGlue());
		add(hbox);
		
        AddSchoolDialog.makeCompactGrid(this, //parent
                getComponentCount() / 2, 2,
                10, 10, //initX, initY
                10, 10); //xPad, yPad
	}

	public AppletConfig getSelectedAppletConfig() {
		AppletConfig ac = new AppletConfig();
		ac.setAppletID(((AppletData) appletField.getSelectedItem()).getId());
		ac.setLanguage(langField.getText());
		ac.setLaunchdata(dataField.getText());
		ac.setName(nameField.getText());
		return ac;
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
		edit.setDwoProfileId(null);
		
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
		if(profileField.isEnabled())
			config.setDwoProfileId(fromString(profileField.isSelected()));
		return config;
	}

	private DomDwoProfileId fromString(boolean select) {
		if(select)
			return DWO.getDwoProfile();
		else 
			return  null;
	}

	@Override
	public int compare(AppletData o1, AppletData o2) {
		return o1.getAppletName().compareTo(o2.getAppletName());
	}

}
