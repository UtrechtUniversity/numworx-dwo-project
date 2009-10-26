/**
 * 
 */
package fi.dwo.client.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

import com.sun.rsasign.s;

import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.User;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.PersistenceException;

/**
 * @author Velth101
 *
 */
public class ExportImportDialog extends JDialog implements ActionListener {

	
	class CellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener
	{

		ImportModuleModel model;
		int row;
		ActionListener    listener;
		String command = "Preview";
		
		public void actionPerformed(ActionEvent e) {
			if(listener != null)
			{
				ActionEvent ae = new ActionEvent(model.courses[row], ActionEvent.ACTION_PERFORMED, command);
				listener.actionPerformed(ae);
			}
			fireEditingCanceled();
		}

		public Object getCellEditorValue() {
			return null;
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			this.row = row;
			this.model = (ImportModuleModel) table.getModel();
			this.command = value.toString();
			JButton button = new JButton(command);
			button.addActionListener(this);
			
			return button;
		}
		
	}
	
	
	class ImportSchoolModel extends AbstractListModel {

		School[] school;
		
		public ImportSchoolModel(School[] schools) {
			this.school = schools;
		}

		public Object getElementAt(int index) {
			return school[index].getName();
		}

		public int getSize() {
			return school.length;
		}
	}
	
	class ImportModuleModel extends AbstractTableModel {

		Course[] courses = new Course[0];
		
		public Course[] getCourses() {
			return courses;
		}

		public void setCourses(Course[] courses) {
			if(courses == null)
				this.courses = new Course[0];
			else
				this.courses = courses;
			
			fireTableDataChanged();
		}

		public int getColumnCount() {
			return 3;
		}

		public int getRowCount() {
			return courses.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			switch(columnIndex)
			{
			case 0: 
				return courses[rowIndex].getName();
			case 1:
				return "Preview";
			case 2: 
				return "Copy";
			}
			return null;
		}

		public String getColumnName(int column) {
			switch(column) {
			case 0: return "Module";
			case 1:
			case 2:
					return "";
			}
			return super.getColumnName(column);
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex > 0;
		}

	}

	int profileID = 1;
	
	class ExportModuleModel extends AbstractTableModel {

		Object[] export;
		Course[] courses;
		
		public ExportModuleModel(User user) throws PersistenceException {

			courses = PersistenceFacade.instance().getCourses(user);
			if(courses == null)
				courses = new Course[0];
// filter only courses within profile.
			Vector v = new Vector();
	        for(int i=0 ; i<courses.length; i++){
	        	if(courses[i].getDwoProfile() == profileID) v.addElement(courses[i]);
			}
			courses = new Course[v.size()];
			v.toArray(courses);

			export = new Boolean[courses.length];
		}

		public int getColumnCount() {
			return 2;
		}

		public int getRowCount() {
			return export.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if(columnIndex == 0)
				return export[rowIndex];
			return courses[rowIndex].getName();
		}
		
		
		public Class getColumnClass(int columnIndex) {
			switch(columnIndex) {
			case 0: return Boolean.class;
			case 1: return String.class;
			}
			return super.getColumnClass(columnIndex);
		}

		public String getColumnName(int column) {
			switch(column) {
			case 0:
				return "Export";
			case 1:
				return "Module";
			}
			return super.getColumnName(column);
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if(columnIndex == 0)
				return true;
			return super.isCellEditable(rowIndex, columnIndex);
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if(columnIndex == 0)
			{
				export[rowIndex] = aValue;
				fireTableCellUpdated(rowIndex, columnIndex);
			}
			
		}
		
		
	}

	class ExportSchoolModel extends AbstractTableModel {

		School[] schools;
		Object[] export;
		
		public int getColumnCount() {
			return 2;
		}

		public int getRowCount() {
			return export.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			switch(columnIndex) 
			{
			case 0: return export[rowIndex];
			case 1: if(rowIndex == schools.length)
						return "Alle Scholen";
					return schools[rowIndex].getName();
			}
			return null;
		}

		ExportSchoolModel(School[] s) {
			schools = s;
			export = new Object[s.length+1];			
		}

		public Class getColumnClass(int columnIndex) {
			switch(columnIndex)
			{
			case 0: return Boolean.class;
			case 1: return String.class;			
			}
			return super.getColumnClass(columnIndex);
		}

		public String getColumnName(int column) {
			switch(column) {
				case 0: return "Export naar";
				case 1: return "School";
			}
			return super.getColumnName(column);
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return (columnIndex == 0);
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			switch(columnIndex)
			{
			case 0:
					export[rowIndex] = aValue;
					if(rowIndex == schools.length && Boolean.TRUE.equals(aValue)) 
					{
						for(int i = 0; i < schools.length; i++) 
						{
							export[i] = Boolean.FALSE;
						}
						fireTableDataChanged();
						return;
					} else if(Boolean.TRUE.equals(aValue)){
						export[schools.length] = Boolean.FALSE;
						fireTableDataChanged();
						return;
					}
					fireTableCellUpdated(rowIndex, columnIndex);
					return;
			}
			
			super.setValueAt(aValue, rowIndex, columnIndex);
		}
		
	}

	private User user;

	/**
	 * @throws HeadlessException
	 * @throws PersistenceException 
	 */
	public ExportImportDialog() throws HeadlessException, PersistenceException {
		initialize();
	}

	private void initialize() throws PersistenceException {
		setModal(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE); // one shot!

		setTitle("Dit is de title");
		JTabbedPane pane = new JTabbedPane();
		JPanel exportPanel = new JPanel(new BorderLayout());
		JPanel importPanel = new JPanel(new BorderLayout());
		pane.insertTab("Export", null, exportPanel, "exporteer stuff", 0);
		pane.insertTab("Import", null, importPanel, "importeer stuff", 1);
		getContentPane().add(pane);
// exportstuff		
		ExportModuleModel exportModuleModel = new ExportModuleModel(user);
		School[] schools = (School[]) PersistenceFacade.instance().get(School.class);
		ExportSchoolModel exportSchoolModel = new ExportSchoolModel(schools);
		JTable exportModuleTable = new JTable(exportModuleModel);
		JTable exportSchoolTable = new JTable(exportSchoolModel);
		JScrollPane exportModules = new JScrollPane(exportModuleTable);
		JScrollPane exportSchools = new JScrollPane(exportSchoolTable);
		JSplitPane exportSplit  = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, exportModules, exportSchools);
		//exportSplit.setDividerLocation(-1);
		exportPanel.add(new JLabel("Exporteer modules naar opgegeven scholen"), BorderLayout.NORTH);
		exportPanel.add(exportSplit, BorderLayout.CENTER);
		Box buttonBox = Box.createHorizontalBox();
		exportPanel.add(buttonBox, BorderLayout.SOUTH);
		JButton exportOK = new JButton("OK");
		JButton exportCancel = new JButton("annuleer");
		JButton exportApply = new JButton("toepassen");
		buttonBox.add(exportOK);
		buttonBox.add(exportCancel);
		buttonBox.add(exportApply);
// importstuff
		final ImportModuleModel importModuleModel = new ImportModuleModel();
		final ImportSchoolModel importSchoolModel = new ImportSchoolModel(schools);
		JTable importModuleTable = new JTable(importModuleModel);
		CellEditor editor = new CellEditor();
		editor.listener = this;
		importModuleTable.getColumnModel().getColumn(1).setCellEditor(editor);
		importModuleTable.getColumnModel().getColumn(2).setCellEditor(editor);
		
		JList importSchoolList = new JList(importSchoolModel);
		importSchoolList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		importSchoolList.addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting())
					return;
				int index = e.getFirstIndex();
				School s = importSchoolModel.school[index];
				Course[] courses;
				try {
					courses = (Course[]) PersistenceFacade.instance().get(Course.class, s);
				} catch (PersistenceException e1) {
					courses = null;
					e1.printStackTrace();
				}
				importModuleModel.setCourses(courses);
				
			}
		});
		JScrollPane importModules = new JScrollPane(importModuleTable);
		JScrollPane importSchools = new JScrollPane(importSchoolList);
// TODO dit is niet goed
		JLabel view = new JLabel("Scholen");
		view.setBorder(BorderFactory.createRaisedBevelBorder());
		view.setHorizontalAlignment(SwingConstants.CENTER);
		importSchools.setColumnHeaderView(view);
		JSplitPane importSplit  = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, importSchools, importModules);
		importPanel.add(new JLabel("Importeer modules van andere scholen"), BorderLayout.NORTH);
		importPanel.add(importSplit, BorderLayout.CENTER);
		buttonBox = Box.createHorizontalBox();
		importPanel.add(buttonBox, BorderLayout.SOUTH);
		JButton importOK = new JButton("OK");
		buttonBox.add(importOK);
		JCheckBox enableImport = new JCheckBox("Ik wil importeren");
		buttonBox.add(enableImport);
// sizeen
		setSize(getContentPane().getPreferredSize());
		pack();
	}

	/**
	 * @param owner
	 * @throws HeadlessException
	 */
	public ExportImportDialog(Dialog owner) throws HeadlessException {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param p 
	 * @throws HeadlessException
	 * @throws PersistenceException 
	 */
	public ExportImportDialog(Frame owner, User u, int p) throws HeadlessException, PersistenceException {
		super(owner);
		this.user = u;
		this.profileID = p;
		initialize();
	}

	/**
	 * @param owner
	 * @param modal
	 * @throws HeadlessException
	 */

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		System.out.println(e);
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception 
	{
		User user = PersistenceFacade.instance().login("peterb");
		ExportImportDialog dialog = new ExportImportDialog(null, user, 1);		
		dialog.setVisible(true);
		System.exit(0);
	}

}
