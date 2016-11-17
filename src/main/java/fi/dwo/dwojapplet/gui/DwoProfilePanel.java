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
import fi.dwo.dwojapplet.domain.rest.SecureDwoAdminProfileManager;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class DwoProfilePanel extends JPanel implements ActionListener,
		CenterSubPanel {

    private static final Logger LOG = Logger.getLogger(DwoProfilePanel.class.getName());

	private CenterPanel centerPanel;
	private Image removeImage;
	private Image editImage;
	private JButton addProfileBtn;
	private JComponent jtbl;
	
	class DwoProfileModel extends AbstractTableModel {

		private DomDwoProfileFull[] profiles;
		
		public DwoProfileModel(DomDwoProfileFull[] profiles) {
			super();
			this.profiles = profiles;
		}

		@Override
		public int getColumnCount() {
			return 7;
		}

		@Override
		public int getRowCount() {
			return profiles.length;
		}

		@Override
		public Object getValueAt(int row, int col) {
			DomDwoProfileFull current = profiles[row];
			switch(col) {
			case 0:	return MySQLPersistenceId.getId(current.getId());
			case 1: return current.getDwoProfileName();
			case 2: return current.getDwoProfileDescription();
			case 3: return current.getDwoProfileText();
			case 4: return current.getDwoProfileRights();
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
			case 1: return "name";
			case 2: return "header";
			case 3: return "text";
			case 4: return "rights";
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
	    	DwoProfileModel model;
	    	int row;

	    	public Component getTableCellEditorComponent(JTable table, Object value,
	    			boolean arg2, int row, int col) {
	    		this.value = value;
	    		JButton button = new JButton(new ImageIcon((Image)value));
	    		button.addActionListener(this);
	    		this.row = row;
	    		model = (DwoProfileModel) table.getModel();
	    		return button;
	    	}

	    	public Object getCellEditorValue() {
	    		return value;
	    	}

	    	public void actionPerformed(ActionEvent event) {
	            DomDwoProfileFull sc = model.profiles[row];
	    		if (value == editImage) {
					sc = AddProfileDwoAdminJPanel.editDialog(jtbl, sc.duplicate());
					if ( sc != null) {
	                	try {
							if (SecureDwoAdminProfileManager.updateProfile(sc));
								model.profiles[row] = sc;
						} catch (Dwo2Exception e) {
							LOG.log(Level.SEVERE, "edit profile", e);
							GuiCreator.instance().ShowErrorDialog(DwoProfilePanel.this, e);
						}
	                    model.fireTableCellUpdated(row, 0);
					}                

	    		} else if (value == removeImage) {
	                /* Delete the course */
	                if (JOptionPane.showConfirmDialog(DwoProfilePanel.this, TextMapper.getText(TextMapper.GUIC_MSG_DELETE_CLASS)
	                        + "?", TextMapper.getText(TextMapper.GUIC_DELETE_CLASS), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
	                    if (	true 
	                    		///instance.deleteProfile(sc)
	                        ) {
	        	            model.removeRow(row);
	                    }
	                }

	    		}
	    		fireEditingStopped();
	    	}

	}

	public DwoProfilePanel() {
		super(new BorderLayout(10,10));
		setBackground(GuiConstants.MAIN_BACKGROUND);
	    setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 10));
	       
        removeImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_CLASS_IMAGE);
        editImage = DwoHelper.getResourceImage(GuiConstants.EDIT_CLASS_IMAGE);
        
        addProfileBtn = new JButton("addDWOprofile");
        addProfileBtn.addActionListener(this);
        jtbl = buildJTable();
// layout
        add(jtbl, BorderLayout.CENTER);
        add(addProfileBtn, BorderLayout.SOUTH);
 	}

	private JComponent buildJTable() {
// TODO build model
		DomDwoProfileFull[] profiles;
		profiles = new DomDwoProfileFull[0];
		try {
			Collection<DomDwoProfileFull> list;
			list = SecureDwoAdminProfileManager.getProfiles();
			profiles = list.toArray(profiles);
		} catch (Dwo2Exception e) {
			LOG.log(Level.SEVERE, "getProfiles", e);
		}
		
		DwoProfileModel model = new DwoProfileModel(profiles);
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
// Names column       
        w = new JLabel("xxxxxxxxxxxxxx").getPreferredSize().width;
        column = table.getColumnModel().getColumn(1);
		column.setPreferredWidth(w);
        column.setMaxWidth(w);
// Rights column       
        w = new JLabel("rights ").getPreferredSize().width;
        column = table.getColumnModel().getColumn(4);
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
		DomDwoProfileFull nieuw = AddProfileDwoAdminJPanel.addDialog(jtbl);
		if(nieuw != null) {
			try {
				if (SecureDwoAdminProfileManager.submitProfile(nieuw))
				{
					remove(jtbl);
					jtbl = buildJTable();
					add(jtbl, BorderLayout.CENTER);
				}
			} catch (Dwo2Exception e) {
				LOG.log(Level.SEVERE, "add profile", e);
				GuiCreator.instance().ShowErrorDialog(jtbl, e);
			}
		}
		

	}

}
