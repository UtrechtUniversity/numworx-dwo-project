/**
 * 
 */
package fi.dwo.client.gui;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import fi.beans.appletutil.AppletUtil;
import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.DWO;
import fi.dwo.client.domain.DWOlight;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.ResultsModuleIF;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.domain.User;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.CourseException;
import fi.dwo.client.system.PersistenceException;

/**
 * @author Velth101
 *
 */
public class ExportImportDialog extends JDialog implements ActionListener, CourseContainer {

	
	class ImportTask extends JDialog implements Runnable, ActionListener, WindowListener {

		private ImportModuleModel model;
		public ImportTask(Dialog parent, ImportModuleModel model) throws HeadlessException {
			super(parent);
			this.model = model;
			fuse = true;
			initialize();
		}

		private void initialize() {
			setModal(true);
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			Box box = Box.createVerticalBox();
			setTitle("Kopiëer modules");
			box.add(bar);
			box.add(Box.createVerticalStrut(20));
			box.add(status);
			box.add(status1);
			box.add(Box.createVerticalStrut(20));
			JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
			p.add(cancel);
			cancel.addActionListener(this);
			addWindowListener(this);
			box.add(p);
			p.setAlignmentX(0.0f);
			box.setBorder(BorderFactory.createEmptyBorder(20, 22, 22, 22));
			getContentPane().add(box);
			
			status.setHorizontalAlignment(JLabel.LEFT);
			status1.setHorizontalAlignment(JLabel.LEFT);
			status.setAlignmentY(0.0f);
			status.setAlignmentX(0.0f);
			status1.setAlignmentX(0.0f); 
			bar.setAlignmentX(0.0f); 
			
			cancel.setAlignmentX(0.5f); 
			count = 0;
			Course[]c = model.getCourses();
			for (int i = 0; i < c.length; i++) {
				if(Boolean.TRUE.equals(model.getValueAt(i, 0)))
				{
					c[i].loadScos();
					count += c[i].getScoList().length;
				}
			}
			bar.setMaximum(count);
			bar.setMinimum(0);
			bar.setValue(0);
			invalidate();
			setSize(400,getPreferredSize().height);
			//pack();
		}
		

		JProgressBar bar = new JProgressBar();
		JLabel status  = new JLabel(" ");
		JLabel status1 = new JLabel(" ");
		JButton cancel = new JButton("cancel");
		int count;
		private boolean fuse;
		public void run() {
			int  n = 0;
			for(int i = 0; i < model.getCourses().length && fuse; i++) {
				if(Boolean.TRUE.equals(model.getValueAt(i, 0)))
				{
					Course c = (Course) model.getCourses()[i];
					status.setText("Importeer " + model.getValueAt(i, 1));
					status.invalidate();
					status1.setText("   ");
					status1.invalidate();
					Sco[] scos = c.getScoList();
					for (int j = 0; j < scos.length && fuse; j++) {
						status1.setText(" ... " + scos[j].getScoName());
						status1.invalidate();
						bar.setValue(n++);
						doit();
					}					
					
				} 	
			}
			dispose();
		}

		private void doit() {
			validate();
			repaint();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void actionPerformed(ActionEvent arg0) {
			fuse = false;
		}

		public void windowActivated(WindowEvent arg0) {
		}

		public void windowClosed(WindowEvent arg0) {
			fuse = false;
		}

		public void windowClosing(WindowEvent arg0) {
			fuse = false;
		}

		public void windowDeactivated(WindowEvent arg0) {
		}

		public void windowDeiconified(WindowEvent arg0) {
		}

		public void windowIconified(WindowEvent arg0) {
		}

		public void windowOpened(WindowEvent arg0) {
		}

	}

	private static final String COPY = "Copy";

	static class PijlIcon implements Icon {

		private Polygon p;

		public int getIconHeight() {
			return 30;
		}

		public int getIconWidth() {
			return 50;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(c.getForeground());
			p.translate(x,y);
			g.fillPolygon(p);
			p.translate(-x, -y);

		}

		PijlIcon() {
			this.p = new Polygon();
			int w1 = getIconWidth();
			int w2 = w1 - 10;
			
			p.addPoint(0, 12);
			p.addPoint(w2, 12);
			p.addPoint(w2,5);
			p.addPoint(w1,15);
			p.addPoint(w2,25);
			p.addPoint(w2,18);
			p.addPoint(0,18);

		}
	}


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
		Object[] imports;
		
		public Course[] getCourses() {
			return courses;
		}

		public void setCourses(Course[] courses) {
			if(courses == null)
				this.courses = new Course[0];
			else
				this.courses = courses;
			imports = new Object[courses.length];
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
			case 1: 
				return courses[rowIndex].getName();
			case 2:
				return "Preview";
			case 0: 
				return imports[rowIndex];
			}
			return null;
		}

		public String getColumnName(int column) {
			switch(column) {
			case 1:  return "Module";
			case 0: 
			case 2:  return "";
			}
			return super.getColumnName(column);
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex != 1;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
		 */
		public Class getColumnClass(int column) {
			switch(column) {
			case 0: return Boolean.class;
			}
			return super.getColumnClass(column);
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
		 */
		public void setValueAt(Object value, int row, int column) {
			switch(column) {
			case 0:
				imports[row] = value;
				fireTableCellUpdated(row, column);
				return;
			}
		}

	}

	int profileID = 1;
	
	class ExportModuleModel extends AbstractTableModel {

		HashMap  dirty = new HashMap();
		Course[] courses;
		
		public ExportModuleModel(User user) throws PersistenceException {

			courses = PersistenceFacade.instance().getCourses(user);
			if(courses == null)
				courses = new Course[0];
// filter only courses within profile.
			Vector v = new Vector();
	        for(int i=0 ; i<courses.length; i++){
	        	Course course = courses[i];
				if(course.getDwoProfile() == profileID && course.getSchoolID()== user.getSchool().getSchoolID()) 
	        			v.addElement(course);
			}
			courses = new Course[v.size()];
			v.toArray(courses);

		}

		public int getColumnCount() {
			return 2;
		}

		public int getRowCount() {
			return courses.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if(columnIndex == 0)
				return courses[rowIndex].isExport()? Boolean.TRUE : Boolean.FALSE;
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
				return "";
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
				Course course = courses[rowIndex];
				boolean export = course.isExport();
				if( ((Boolean)aValue).booleanValue() != export)
				{	course.setExport(!export);
				    if(! dirty.containsKey(course))
				    	dirty.put(course, export?Boolean.TRUE:Boolean.FALSE);
					fireTableCellUpdated(rowIndex, columnIndex);
				}
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
			case 1: return schools[rowIndex].getName();
			}
			return null;
		}

		ExportSchoolModel(School[] s) {
			schools = s;
			export = new Object[s.length];			
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
				case 0: return "";
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
					fireTableCellUpdated(rowIndex, columnIndex);
					return;
			}
			
			super.setValueAt(aValue, rowIndex, columnIndex);
		}
		
	}

	private User user;
	private JTabbedPane pane;
	private JCheckBox enableImport;
	private ImportModuleModel importModuleModel;
	private JTable importModuleTable;
	private JPanel previewPanel;
	private Component coursePanel;
	private ExportModuleModel exportModuleModel;

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

		setTitle("Modules delen");
		pane = new JTabbedPane();
		JPanel exportPanel = new JPanel(new BorderLayout(5,5));
		JPanel importPanel = new JPanel(new BorderLayout());
		enableImport = new JCheckBox("<html>Ik wil meedoen in deze manier van uitwisselen en daarbij zichtbaar worden als school in de lijsten");
// From DATABASE
		enableImport.setSelected(user.getSchool().isExport());
// track changes
		enableImport.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent event) {
				
				School school = user.getSchool();
				boolean oldExport = school.isExport();
				if(oldExport == enableImport.isSelected())
					return;
				school.setExport(enableImport.isSelected());
				try {
					PersistenceFacade.instance().updateSchool(school);
				} catch (PersistenceException e) {
					school.setExport(oldExport);
					enableImport.setSelected(oldExport);
					JOptionPane.showMessageDialog(enableImport, e.getMessage(), e.getClass().getName(), JOptionPane.ERROR_MESSAGE);
				}
				ieEnabler();
				
			} });
		
		pane.insertTab("Modules opvragen", null, importPanel, null, 0);
		pane.insertTab("Modules beschikbaar stellen", null, exportPanel, null, 1);
		pane.insertTab("Toestaan" , null, enableImport, null, 2);

// if enableImport is not checked:
		
		ieEnabler();
		
		getContentPane().add(pane);
		exportModuleModel = new ExportModuleModel(user);
		School[] schools = (School[]) PersistenceFacade.instance().get(School.class, Boolean.TRUE);
		ExportSchoolModel exportSchoolModel = new ExportSchoolModel(schools);
		JTable exportModuleTable = new JTable(exportModuleModel);
		JTable exportSchoolTable = new JTable(exportSchoolModel);
		TableUtil.setJTableSizes(exportSchoolTable);
		TableUtil.setJTableSizes(exportModuleTable);
		TableColumn kolom = 
		exportSchoolTable.getColumnModel().getColumn(0);
		int prefWidth = kolom.getPreferredWidth();
		kolom.setMaxWidth(prefWidth);
		kolom.setMinWidth(prefWidth);
		kolom = 
		exportModuleTable.getColumnModel().getColumn(0);
		kolom.setMaxWidth(prefWidth);
		kolom.setMinWidth(prefWidth);
		kolom.setPreferredWidth(prefWidth);
		
		JScrollPane exportModules = new JScrollPane(exportModuleTable);
		JScrollPane exportSchools = new JScrollPane(exportSchoolTable);
		Box exportSplit  = Box.createHorizontalBox();
		exportSplit.add(exportModules);
		JLabel deelLabel = new JLabel("Delen met", new PijlIcon(), JLabel.CENTER);
		deelLabel.setVerticalTextPosition(JLabel.TOP);
		deelLabel.setHorizontalTextPosition(JLabel.CENTER);
		exportSplit.add(deelLabel);
		Box exportSchoolBox = Box.createVerticalBox();
		exportSchoolBox.add(exportSchools);
		JCheckBox exportAlleScholen = new JCheckBox("Alle scholen");
		exportSchoolBox.add(exportAlleScholen);
		exportSplit.add(exportSchoolBox);

		JLabel label = new JLabel("<html>(1) Selecteer een verzameling modules<br>(2) Selecteer een groep scholen<br><br>De geselecteerde modules worden beschikbaar<br>gesteld aan de geselecteerde scholen.");
		
		JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER)); p.add(label);
		exportPanel.add(p, BorderLayout.NORTH);
		exportPanel.add(exportSplit, BorderLayout.CENTER);
		Box buttonBox = Box.createHorizontalBox();
		p = new JPanel(new FlowLayout(FlowLayout.CENTER));p.add(buttonBox);
		exportPanel.add(p, BorderLayout.SOUTH);
		JButton exportOK = new JButton("OK");
		exportOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					persistCourses(exportModuleModel.dirty);
					ExportImportDialog.this.dispose();
			} });
		
		
		JButton exportCancel = new JButton("annuleer");
		exportCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExportImportDialog.this.dispose();
			}});
		JButton exportApply = new JButton("toepassen");
		exportApply.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				HashMap dirty = exportModuleModel.dirty;
				persistCourses(dirty);
				dirty.clear();
			}});
		buttonBox.add(exportOK);
		buttonBox.add(exportCancel);
		buttonBox.add(exportApply);
// importstuff
		
		JLabel header = new JLabel("<html>(1) Selecteer een school<br>" +
								   "(2) Bekijk eventueel de beschikbaar gestelde modules<br>" +
								   "(3) Selecteer één of meer modules voor gebruik in de eigen omgeving<br><br>" +
								   "De geselecteerde modules worden gekopiëerd naar de eigen omgeving<br>"+
								   "en kunnen gebruikt worden binnen de eigen school.");
		p = new JPanel(new FlowLayout(FlowLayout.CENTER)); p.add(header);
		importPanel.add(p, BorderLayout.NORTH);
		importModuleModel = new ImportModuleModel();
		final ImportSchoolModel importSchoolModel = new ImportSchoolModel(schools);
		final JLabel schoolLabel = new JLabel("          ");
		schoolLabel.setFont(new Font("Sans", Font.BOLD, 14));
		importModuleTable = new JTable(importModuleModel);
		//TableUtil.setJTableSizes(importModuleTable);
		kolom = importModuleTable.getColumnModel().getColumn(0);
		kolom.setMaxWidth(prefWidth);
		kolom.setPreferredWidth(prefWidth);
		kolom.setMinWidth(prefWidth);
		kolom = importModuleTable.getColumnModel().getColumn(2);
		TableCellRenderer cr;
		cr = importModuleTable.getCellRenderer(0, 2);
		Component c = new JButton("Preview");
		prefWidth = c.getPreferredSize().width+3;
		kolom.setMaxWidth(prefWidth);
		kolom.setPreferredWidth(prefWidth);
		kolom.setMinWidth(prefWidth);
		
		
		
		CellEditor editor = new CellEditor();
		editor.listener = this;
		importModuleTable.getColumnModel().getColumn(2).setCellEditor(editor);
		
		
		final JList importSchoolList = new JList(importSchoolModel);
		importSchoolList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		importSchoolList.addListSelectionListener(new ListSelectionListener() {
			int lastIndex = -1;
			public void valueChanged(ListSelectionEvent e) {
				System.err.println(e);
				if(e.getValueIsAdjusting())
				{
				    System.err.println("Is Adjusting???");
					return;
				}
				int index = importSchoolList.getSelectedIndex();
				if(index<0 ||lastIndex == index) return;
				lastIndex = index;
				System.err.println("index = " + index);
				School s = importSchoolModel.school[index];
				schoolLabel.setText("Modules " + s.getName());
				schoolLabel.invalidate();
				Course[] courses;
				try {
					ArrayList clist = new ArrayList();
					courses = (Course[]) PersistenceFacade.instance().getImportCourses(s, user.getSchool(), profileID);
				} catch (PersistenceException e1) {
					courses = null;
					e1.printStackTrace();
				}
// TODO pas op, als COPY aan de gang is, dan geen veranderingen aan importModuleModel
				//importModuleModel = new ImportModuleModel();
				importModuleModel.setCourses(courses);
				//importModuleTable.setModel(importModuleModel);
				repaint();
			}
		});
		JScrollPane importModules = new JScrollPane(importModuleTable);
		JScrollPane importSchools = new JScrollPane(importSchoolList);
// TODO dit is niet goed
		JLabel view = new JLabel("Scholen");
		view.setBorder(BorderFactory.createRaisedBevelBorder());
		view.setHorizontalAlignment(SwingConstants.CENTER);
		importSchools.setColumnHeaderView(view);
		Box importSplit  = Box.createHorizontalBox();
		importSplit.add(importSchools);
		importSplit.add(Box.createHorizontalStrut(40));
		Box importModuleBox = Box.createVerticalBox();
		importModuleBox.add(schoolLabel);
		importModuleBox.add(importModules);
		importSplit.add(importModuleBox);
		
		importPanel.add(importSplit, BorderLayout.CENTER);
		buttonBox = Box.createHorizontalBox();
		importPanel.add(buttonBox, BorderLayout.SOUTH);
		buttonBox.add(Box.createGlue());
		JButton importOK = new JButton("Kopiëer");
		importOK.setActionCommand(COPY);
		importOK.addActionListener(this);
		
		buttonBox.add(importOK);
		buttonBox.add(Box.createHorizontalStrut(100));
// sizeen
		setSize(getContentPane().getPreferredSize());
		pack();
	}

	/**
	 * Update courses. 
	 * Als het fout gaat, cancel dat wat nog niet gepersist is.
	 * @param dirty
	 */
	protected void persistCourses(HashMap dirty) {
		Iterator iterator = dirty.entrySet().iterator();
		boolean inerror = false;
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			Course course = (Course) entry.getKey();
			boolean oldExport = Boolean.TRUE.equals(entry.getValue());
			if(oldExport != course.isExport())
				if(inerror)
					course.setExport(oldExport);
				else
				try {
System.out.println("persistCourses " + course.getName());
					PersistenceFacade.instance().updateCourse(course);
				} catch (CourseException e) {
					JOptionPane.showMessageDialog(this, e.getMessage(), e.getClass().getName(), JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
					inerror = true;
				}
		}
		if(inerror)
			exportModuleModel.fireTableDataChanged();
	}

	private void ieEnabler() {
		if(enableImport.isSelected())
		{
			pane.setEnabledAt(0, true);
			pane.setEnabledAt(1, true);
		} else
		{ 
			pane.setEnabledAt(0, false);
			pane.setEnabledAt(1, false);
			pane.getModel().setSelectedIndex(2);
		}
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
		if(e.getSource() instanceof Course)
		{
			Course course = (Course) e.getSource();
			CoursePanel cp = (CoursePanel) course.getCoursePanel();
			cp.lessonMode = Sco.BROWSE;
			this.previewPanel = cp;
			cp.setCenterPanel(this);
			JOptionPane.showConfirmDialog(this, previewPanel, e.getActionCommand(), JOptionPane.DEFAULT_OPTION);
		} else if (COPY.equals(e.getActionCommand())) 
		{
			ImportTask r = new ImportTask(this, importModuleModel);
			new Thread(r).start();
			r.show();
		}
		
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception 
	{
		DWO dwo = new DWO();
		dwo.setStub(new AppletStub() {

			public void appletResize(int arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}

			public AppletContext getAppletContext() {
				// TODO Auto-generated method stub
				return null;
			}

			public URL getCodeBase() {
				// TODO Auto-generated method stub
				return null;
			}

			public URL getDocumentBase() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getParameter(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			public boolean isActive() {
				// TODO Auto-generated method stub
				return false;
			}});
		AppletUtil au = new AppletUtil(dwo);
		DwoHelper.setAu(au);
		DwoHelper.applet = dwo;
		dwo.setSize(GuiConstants.DWO_WIDTH, GuiConstants.DWO_HEIGHT);
		new GuiCreator(dwo);
		User user = PersistenceFacade.instance().login("peterb");
		ExportImportDialog dialog = new ExportImportDialog(null, user, 1);		
		dialog.setVisible(true);
		System.exit(0);
	}

	public ResultsModuleIF getUserResultsModule(Course course) {
		return null;
	}

	public void hideClassList() {
	}
	public void loadCenter(CenterSubPanel cp) {
	}

	public void loadTotal(CenterSubPanel csp) {
		ScoPanel sp = (ScoPanel) csp;
		ScoDialog.showScoPreview(this, sp);
	}

	public void showClassList() {
	}

}
