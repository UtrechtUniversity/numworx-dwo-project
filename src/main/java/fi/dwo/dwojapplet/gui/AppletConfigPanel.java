package fi.dwo.dwojapplet.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.rest.SecureDwoAdminConfigManager;
import fi.dwo.dwojapplet.domain.rest.SecureDwoAdminProfileManager;
import fi.dwo.dwojapplet.gui.DwoProfilePanel.DwoProfileModel;
import fi.dwo.dwojapplet.gui.DwoProfilePanel.ImageButtonEditor;
import fi.dwo.rest.dom.entities.DomAppletConfig;
import fi.dwo.rest.dom.entities.DomDwoProfile;
import fi.dwo.rest.exceptions.Dwo2Exception;

class AppletConfigPanel extends JPanel implements CenterSubPanel, ActionListener {

    private static final Logger LOG = Logger.getLogger(AppletConfigPanel.class.getName());

	private CenterPanel centerPanel;
	private Image removeImage;
	private Image editImage;
	private JButton addProfileBtn;
	private JComponent jtbl;
	
	class AppletConfigModel extends AbstractTableModel {

		private DomAppletConfig[] config;
		
		public AppletConfigModel(DomAppletConfig[] config) {
			super();
			this.config = config;
		}

		@Override
		public int getColumnCount() {
			return 7;
		}

		@Override
		public int getRowCount() {
			return config.length;
		}

		@Override
		public Object getValueAt(int row, int col) {
			DomAppletConfig current = config[row];
			switch(col) {
			case 0:	return MySQLPersistenceId.getId(current.getId());
			case 1: return current.getAppletID();
			case 2: return current.getName();
			case 3: return current.getLanguage();
			case 4: return current.getLaunchdata();
			case 5: return editImage;
			case 6: return removeImage;
			}
			return null;
		}

		public void removeRow(int row) {
			
			fireTableDataChanged();
		}

		@Override
		public Class<?> getColumnClass(int col) {
			switch(col) {
			case 5:
			case 6:
				return Image.class;
			default:
				return super.getColumnClass(col);
			}
		}

		@Override
		public String getColumnName(int col) {
			switch(col) {
			case 0: return "id";
			case 1: return "applet";
			case 2: return "naam";
			case 3: return "taal";
			case 4: return "launchdata";
			default: return "";
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			switch(columnIndex) {
			case 5:
			case 6: 
					return true;
			}
			return super.isCellEditable(rowIndex, columnIndex);
		}
		
	}

	   public class ImageButtonEditor extends AbstractCellEditor implements
		TableCellEditor, ActionListener {

	    	Object value;
	    	AppletConfigModel model;
	    	int row;

	    	public Component getTableCellEditorComponent(JTable table, Object value,
	    			boolean arg2, int row, int col) {
	    		this.value = value;
	    		JButton button = new JButton(new ImageIcon((Image)value));
	    		button.addActionListener(this);
	    		this.row = row;
	    		model = (AppletConfigModel) table.getModel();
	    		return button;
	    	}

	    	public Object getCellEditorValue() {
	    		return value;
	    	}

	    	public void actionPerformed(ActionEvent event) {
	            DomAppletConfig sc = model.config[row];
	    		if (value == editImage) {
					sc = AddConfigDwoAdminJPanel.editDialog(jtbl, sc.duplicate());
					if ( sc != null) {
	                	try {
							if (SecureDwoAdminConfigManager.updateConfig(sc));
								model.config[row] = sc;
						} catch (Dwo2Exception e) {
							LOG.log(Level.SEVERE, "edit config", e);
							GuiCreator.instance().ShowErrorDialog(AppletConfigPanel.this, e);
						}
	                    model.fireTableCellUpdated(row, 0);
					}                

	    		} else if (value == removeImage) {
	                /* Delete the course */
	                if (JOptionPane.showConfirmDialog(AppletConfigPanel.this, TextMapper.getText(TextMapper.GUIC_MSG_DELETE_CLASS)
	                        + "?", TextMapper.getText(TextMapper.GUIC_DELETE_CLASS), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
	                    try {
							SecureDwoAdminConfigManager.removeConfig(sc);
						} catch (Dwo2Exception e) {
							LOG.log(Level.SEVERE, "remove config", e);
							GuiCreator.instance().ShowErrorDialog(AppletConfigPanel.this, e);
						}
					    refresh();
	                }

	    		}
	    		fireEditingStopped();
	    	}

	}

	public AppletConfigPanel() {
		super(new BorderLayout(10,10));
		setBackground(GuiConstants.MAIN_BACKGROUND);
	    setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 10));
	       
        removeImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_CLASS_IMAGE);
        editImage = DwoHelper.getResourceImage(GuiConstants.EDIT_CLASS_IMAGE);
        
        addProfileBtn = new JButton("Template toevoegen");
        addProfileBtn.addActionListener(this);
        jtbl = buildJTable();
// layout
        add(jtbl, BorderLayout.CENTER);
        add(addProfileBtn, BorderLayout.SOUTH);
 	}

	private JComponent buildJTable() {
// TODO build model
		DomAppletConfig[] profiles;
		profiles = new DomAppletConfig[0];
		try {
			Collection<DomAppletConfig> list;
			list = SecureDwoAdminConfigManager.getConfigurations(DwoHelper.getAu().getLocale());
			profiles = list.toArray(profiles);
		} catch (Dwo2Exception e) {
			LOG.log(Level.SEVERE, "getProfiles", e);
		}
		
		AppletConfigModel model = new AppletConfigModel(profiles);
// build table
		
		JTable table = new JTable(model);
		
    	TableUtil.setDefaults(table, true, new ImageRenderer(), new ImageButtonEditor());
        table.setRowMargin(0);

        ///TableUtil.setJTableSizes(table);
        int w = new JLabel("0000").getPreferredSize().width;
// ID column
        TableColumn column = table.getColumnModel().getColumn(0);
		column.setPreferredWidth(w);
        column.setMaxWidth(w);
// Applet column       
        w = new JLabel("applet ").getPreferredSize().width;
        column = table.getColumnModel().getColumn(1);
		column.setPreferredWidth(w);
        column.setMaxWidth(w);
// language column       
        w = new JLabel("taal ").getPreferredSize().width;
        column = table.getColumnModel().getColumn(3);
		column.setPreferredWidth(w);
        column.setMaxWidth(w);
// icons
        w = 32;
        column = table.getColumnModel().getColumn(5);
		column.setPreferredWidth(w);
        column.setMaxWidth(w);
        column = table.getColumnModel().getColumn(6);
		column.setPreferredWidth(w);
        column.setMaxWidth(w);
        
        
        
        
        
		return new JScrollPane(table);
	}

	public void stateChanged(ChangeEvent e) {
	}

	public void end() {
	}

	public Component getHeaderPanel() {
    	return new HeaderPanel(TextMapper.getText(TextMapper.DWO_PROFILE_ADMIN));
	}

	public void setCenterPanel(CenterPanel centerPanel) {
		this.centerPanel = centerPanel;
	}

	public JComponent getComponent() {
		return this;
	}

	public Object getUserObject() {
		return null;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		DomAppletConfig nieuw = AddConfigDwoAdminJPanel.addDialog(jtbl);
		if(nieuw != null) {
			try {
				if (SecureDwoAdminConfigManager.submitConfig(nieuw))
				{
					refresh();
				}
			} catch (Dwo2Exception e) {
				LOG.log(Level.SEVERE, "add config", e);
				GuiCreator.instance().ShowErrorDialog(jtbl, e);
			}
		}
		

	}

	private void refresh() {
		remove(jtbl);
		jtbl = buildJTable();
		add(jtbl, BorderLayout.CENTER);
	}

	
	
}
