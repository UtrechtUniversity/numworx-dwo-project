package fi.dwo.client.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.CourseMap;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.DwoIF;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.domain.Teacher;
import fi.dwo.client.domain.User;
import fi.dwo.client.gui.action.BackupModuleAction;
import fi.dwo.client.gui.action.CutCopyAction;
import fi.dwo.client.gui.action.DeleteAction;
import fi.dwo.client.gui.action.ImportModuleAction;
import fi.dwo.client.gui.action.ImportScorm;
import fi.dwo.client.gui.action.NewAction;
import fi.dwo.client.gui.action.PasteAction;
import fi.dwo.client.gui.action.Save2004Action;
import fi.dwo.client.gui.action.SaveAppletAction;
import fi.dwo.client.gui.action.TeacherStrategy;
import fi.dwo.client.persistence.MapperCreator;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.PersistenceException;
import fi.dwo.client.system.TextMapper;

public class ModuleTreePanel extends JPanel implements TreeSelectionListener{

	public static final String STANDAARD_DWO_MODULES = "Standaard DWO modules";
	public static final String ALLE_MODULES = "Alle modules";
	public static String SCHOOL_MODULES = null;
	public static CourseMap STANDAARD_DWO_MAP;
	public static CourseMap SCHOOL_MAP;
	protected JTree tree;
	private JScrollPane pane;
	private JMenuBar bar;
	protected DwoIF dwo;
	private CenterPanel center;
	private IconizedPanel ip;
	
	class StandaardMap extends TreeMap {

		public StandaardMap(DefaultMutableTreeNode node) {
			super(node);
		}
		
		public Course[] getChildren() {
			Course[] result = dwo.getCourses();
			ArrayList list = new ArrayList(result.length);
			for (int i = 0; i < result.length; i++) {
				Course c = result[i];
				if(c.getSchoolID()==0) list.add(c);
			}
			return (Course[]) list.toArray(new Course[list.size()]);
		}
		
		public CourseMap getParent() {
			return null;
		}
	}
	
	class TopMap extends TreeMap {
		School school;
		
		TopMap(DefaultMutableTreeNode node, School school)
		{	
			super(node);
			this.school = school;
		}

		public Course[] getChildren() {
			Course[] result;
			try {
				result = (Course[]) PersistenceFacade.instance().get(Course.class, school);
				result = dwo.sequence(result);
			} catch (PersistenceException e) {
				e.printStackTrace();
				result = new Course[0];
			}
			return result;
		}

		public CourseMap getParent() {
			return null;
		}
		
	}
	
	
	class TreeMap implements CourseMap
	{
		
		TreeMap(DefaultMutableTreeNode node) {
			super();
			this.node = node;
		}

		DefaultMutableTreeNode node;

		public Object getUserObject() {
			return node.getUserObject();
		}

		public void addChild(Course c) {
		}

		public void removeChild(int i) {
		}

		public Course[] getChildren() {
			return getCourses(node);
		}

		public void setChildren(Course[] courses) {
		}

		public Set getChildNames() {
			HashSet names = new HashSet();
			Course[] courses = getChildren();
			for (int i = 0; i < courses.length; i++) {
				names.add(courses[i].getName());
			}
			return names;
		}

		public CourseMap getParentMap() {
			return null;
		}

	}
	
	class TreeCellRenderer extends DefaultTreeCellRenderer
	{
		Icon bookIcon; 
		boolean isCourse, isMap;
		private TreeCellRenderer() {
			super();
			Image book = DwoHelper.getResourceImage("resources/book.png");
			bookIcon = new ImageIcon(book);
		}

		public Icon getOpenIcon() {
			if(isCourse)
				return bookIcon;
			return super.getOpenIcon();
		}

		public Icon getClosedIcon() {
			if(isCourse)
				return bookIcon;
			return super.getClosedIcon();
		}

		public Icon getLeafIcon() {
			if(isCourse)
				return bookIcon;
			if(isMap)
				return getClosedIcon();
			return super.getLeafIcon();
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
// bookicon als het een course is
			isCourse =
				value instanceof DefaultMutableTreeNode &&
				((DefaultMutableTreeNode) value).getUserObject() instanceof Course &&
				!((Course) ((DefaultMutableTreeNode) value).getUserObject()).isWithChildren();
			isMap = value instanceof DefaultMutableTreeNode &&
				((DefaultMutableTreeNode) value).getUserObject() instanceof Sco;
// geen leaficon als het een lege map is
			isMap = leaf && !isMap;
			
			return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
					row, hasFocus);
		}
		
	}
	
	
	public ModuleTreePanel() {
		super(new BorderLayout());
		strategy = delegate;
		tree = new JTree();
		tree.setShowsRootHandles(true);
		tree.addTreeSelectionListener(this);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addMouseListener(mtp);
		pane = new JScrollPane(tree);
		pane.setViewportBorder(null);
		pane.setBorder(null);
		pane.setOpaque(false);
		add(pane, BorderLayout.CENTER);
		Box hbox = Box.createHorizontalBox();
		createMenubar(hbox);
		createCloseBtn(hbox);
		tree.setCellRenderer(new TreeCellRenderer());
	}

	protected void createMenubar(Box hbox) {
		bar = new JMenuBar();
		bar.setOpaque(false);
		bar.setVisible(GuiCreator.instance().getUser().hasRight(User.MODIFY_MODULES_RIGHT));
		JMenu menu; JMenuItem item;
		menu = new JMenu("Bestand");
		item = new JMenuItem("Nieuwe map");menu.add(item);item.setAction(new NewAction(true, true));
		item = new JMenuItem("Nieuwe module/activiteit");menu.add(item);item.setAction(new NewAction(false, false));
		if(DwoHelper.isApplication())
		{  	
			menu.addSeparator();
			item = new JMenuItem("Import module");menu.add(item);item.setAction(new ImportModuleAction());
			item = new JMenuItem("Backup module");menu.add(item);item.setAction(new BackupModuleAction());
			if( DwoHelper.isScormExportLoggedIn() || DwoHelper.isAppletExportLoggedIn())
				menu.addSeparator();
			if( DwoHelper.isScormExportLoggedIn() )
			{
				item = new JMenuItem("Import activiteit"); menu.add(item);item.setAction(new ImportScorm());
				item = new JMenuItem("Backup activiteit"); menu.add(item);item.setAction(new Save2004Action());
			}
			 if (DwoHelper.isAppletExportLoggedIn())
			{
				item = new JMenuItem("Export Applet");menu.add(item);item.setAction(new SaveAppletAction()); 
			}
		}
		bar.add(menu);
		menu = new JMenu("Bewerken");
		item = new JMenuItem("cut");menu.add(item);item.setAction(new CutCopyAction(true));item.setText(TextMapper.getText("cut"));
		item = new JMenuItem("copy");menu.add(item);item.setAction(new CutCopyAction(false));item.setText(TextMapper.getText("copy"));
		item = new JMenuItem("paste");menu.add(item);item.setAction(new PasteAction());
		menu.addSeparator();
		item = new JMenuItem("delete");menu.add(item);item.setAction(new DeleteAction());
// op dit moment is er nog geen user bekend.
//		if (dwo.getUser().hasRight(User.PROFILE_ADMIN_RIGHT))
//		{
//			menu.addSeparator();
//			item = new JMenuItem("publiceer");menu.add(item); // cut en paste in standaard modules.
//		}
		bar.add(menu);
		hbox.add(bar);
	}

	protected void createCloseBtn(Box toolbar) {
		ip = new IconizedPanel("Modules");
		JButton closeBtn = new JButton(ip.getCloseAction()); // TODO icon..
		closeBtn.setBorderPainted(false);
		closeBtn.setContentAreaFilled(false);
		toolbar.add(Box.createHorizontalGlue());
		toolbar.add(closeBtn);
		add(toolbar, BorderLayout.NORTH);
		ip.setWindow(this);
	}

	protected void setModel(TreeModel model)
	{
		tree.setModel(model);
	}
	
	protected void createModel(DwoIF dwo) {
		this.dwo = dwo;
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(ALLE_MODULES);
        DefaultMutableTreeNode dwonode  = new DefaultMutableTreeNode(STANDAARD_DWO_MODULES);
        STANDAARD_DWO_MAP = new StandaardMap(dwonode);
        root.add(dwonode);        
        DefaultMutableTreeNode schoolnode = null;
        if(dwo != null)
        {
        	GuiCreator instance = GuiCreator.instance();
        	if(instance.getMainPanel()!= null)
        		setCenterPanel(instance.getMainPanel().getCenter());
			User u = dwo.getUser();
        	School school = u.getSchool();
        	if(school != null)
        	{  	
        		SCHOOL_MODULES = "Modules " + school;
        		schoolnode = new DefaultMutableTreeNode(SCHOOL_MODULES);
        		SCHOOL_MAP = new TopMap(schoolnode, school);
        		if(dwo.getUser().hasRight(User.MODIFY_MODULES_RIGHT)) // TODO is dit de bedoeling?
        			root.add(schoolnode);
        	}
        	Course[] courses = instance.getCourseList();
        	sort(courses);
        	DefaultMutableTreeNode node; 
        	for (int i = 0; i < courses.length; i++) {
				Course course = courses[i];
				node = new DefaultMutableTreeNode(course);
				if(course.isWithChildren())
				{
					appendCourseMap(course, node);
				}
				insertScos(course, node);
				if(course.getSchoolID() == 0)
				{
					dwonode.add(node);
					course.setParentMap(STANDAARD_DWO_MAP);
				} else {
					course.setParentMap(SCHOOL_MAP);
					schoolnode.add(node);
				}
			}
        	
        }
        
        DefaultTreeModel model = new DefaultTreeModel(root);
        setModel(model);
	}
	
	private void sort(Course[] courses) {
		for (int i = 0; i < courses.length; i++) {
			Course course = courses[i];
			if(course.isWithChildren())
			{
				Course[] children = course.getChildren();
				course.setChildren(dwo.sequence(children));
				sort(children);
			}
		}
		
	}

	protected void insertScos(Course course, DefaultMutableTreeNode node) {
		course.loadScos();
		Sco[] scos = course.getScoList();
		if(scos != null)
		for (int i = 0; i < scos.length; i++) {
			Sco sco = scos[i];
			node.add(new DefaultMutableTreeNode(sco));
		}	
	}
	
	public void select(Object object)
	{
		if(object == null) // TODO is er wel een null value in de tree?
			return;
		// search in tree where userObject equals object
		pushSelect();
		TreeModel model = tree.getModel();
		Object root = model.getRoot();
		select(object, (DefaultMutableTreeNode) root);
		popSelect();
	}
	

	private void select(Object object, DefaultMutableTreeNode node) {
		node = find(object, node);
		if(node != null)
		{
			TreePath path = new TreePath(node.getPath());
			tree.setSelectionPath(path);
		}
	}

	private DefaultMutableTreeNode find(Object object, DefaultMutableTreeNode node) {
		Object userObject = node.getUserObject();
		if(userObject.equals(object))
		{
			return node;
		}
		Enumeration e = node.children();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode o = (DefaultMutableTreeNode) e.nextElement();
			o = find(object, o);
			if(o != null)
				return o; // early out.
		}
		return null;
	}

	public static ModuleTreePanel newInstance(DwoIF dwo)
	{
		ModuleTreePanel panel = new ModuleTreePanel();
		panel.createModel(dwo);
		return panel;
	}
	
	public static void create(DwoIF dwo)
	{
		JFrame frame = new JFrame("modules");
		ModuleTreePanel newInstance = newInstance(dwo);
		frame.setContentPane(newInstance.getIP());
		frame.pack();
		frame.setVisible(true);
		newInstance.select(ALLE_MODULES);
	
	}
	
    /**
     * Sets the centerpanel to communicate with.
     * 
     * @param centerPanel The centerPanel to communicate with.
     */
    public void setCenterPanel(CenterPanel centerPanel) {
        center = centerPanel;
    }

		
	public void valueChanged(TreeSelectionEvent e) {
		if(isSelect())
			return;
		TreePath path = e.getPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		boolean sel = e.isAddedPath();
		if(sel)
		{
			callStrategy(node);
		}
	}
	
	private SelectStrategy strategy;

	
	private boolean isSelect() {
		return cnt>0;
	}
	private int cnt;
	private void pushSelect() { cnt++; }
	private void popSelect()  { cnt--; }
	

	protected static Course[] getCourses(DefaultMutableTreeNode node) {
		Vector v = new Vector(node.getChildCount());
		Enumeration e = node.children();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode object = (DefaultMutableTreeNode) e.nextElement();
			Object uo = object.getUserObject();
			if(uo instanceof Course)
				v.add(uo);
		}
		int n = v.size();
		Course[] result = new Course[n];
		v.toArray(result);
		return result;
	}

	public IconizedPanel getIP() {
		return ip;
	}

	public JMenuBar getMenuBar() {
		return bar;
	}

	SelectStrategy getStrategy() {
		return strategy;
	}

	TeacherStrategy delegate = new TeacherStrategy(); // this order!
	ModuleTreePopup mtp = new ModuleTreePopup(this);
	
	void setStrategy(SelectStrategy strategy) {
		if(strategy == null)
		{	strategy = delegate;
		} 
		mtp.setPopup(strategy);
		this.strategy = strategy;
	}

	public void updateNode(Course course) {
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		DefaultMutableTreeNode node = find(course, root);
		node.removeAllChildren();
		insertScos(course, node);
		model.nodeStructureChanged(node);
	}

	public void updateNodeMap(CourseMap map) {
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		DefaultMutableTreeNode node = find(map.getUserObject(), root);
		node.removeAllChildren();
    	appendCourseMap(map, node);
    	model.nodeStructureChanged(node);
	}

	public void appendCourseMap(CourseMap map, DefaultMutableTreeNode node) {
		Course[] courses = map.getChildren();
    	DefaultMutableTreeNode child; 
    	for (int i = 0; i < courses.length; i++) {
			Course course = courses[i];
			course.setParentMap(map);
			child = new DefaultMutableTreeNode(course);
			node.add(child);
			if(course.isWithChildren())
				appendCourseMap(course, child);
			else
				insertScos(course, child);
    	}
	}


	public void toSelectedNode() {
		TreePath path = tree.getSelectionPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		callStrategy(node);
	}

	private void callStrategy(DefaultMutableTreeNode node) {
		Object o = node.getUserObject();
		if(o instanceof CourseMap)
		{
			strategy.nodeSelected((CourseMap) o);
		} else
		if(o == SCHOOL_MODULES)
			strategy.nodeSelected(SCHOOL_MAP);
		else if(o == STANDAARD_DWO_MODULES)
			strategy.nodeSelected(STANDAARD_DWO_MAP);
		else
			strategy.nodeSelected(new TreeMap(node));
	}
}

