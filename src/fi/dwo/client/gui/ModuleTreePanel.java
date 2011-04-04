package fi.dwo.client.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Box;
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
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.DwoIF;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.domain.User;

interface SelectStrategy {
	void nodeSelected(DefaultMutableTreeNode node);
}

public class ModuleTreePanel extends JPanel implements TreeSelectionListener, SelectStrategy {

	public static final String STANDAARD_DWO_MODULES = "Standaard DWO modules";
	public static final String ALLE_MODULES = "Alle modules";
	protected JTree tree;
	private JScrollPane pane;
	private JMenuBar bar;
	protected DwoIF dwo;
	private CenterPanel center;
	private IconizedPanel ip;
	
	
	public ModuleTreePanel() {
		super(new BorderLayout());
		strategy = this;
		tree = new JTree();
		tree.setShowsRootHandles(true);
		tree.addTreeSelectionListener(this);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		pane = new JScrollPane(tree);
		pane.setViewportBorder(null);
		pane.setBorder(null);
		pane.setOpaque(false);
		add(pane, BorderLayout.CENTER);
		Box hbox = Box.createHorizontalBox();
		createMenubar(hbox);
		createCloseBtn(hbox);
	}

	protected void createMenubar(Box hbox) {
		bar = new JMenuBar();
		bar.setVisible(false);
		JMenu menu; JMenuItem item;
		menu = new JMenu("Bestand");
		item = new JMenuItem("Import");
		menu.add(item);
		bar.add(menu);
		menu = new JMenu("Bewerken");
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
        root.add(dwonode);
        DefaultMutableTreeNode schoolnode = null;
        if(dwo != null)
        {
        	GuiCreator instance = GuiCreator.instance();
        	if(instance.getMainPanel()!= null)
        		setCenterPanel(instance.getMainPanel().getCenter());
			User u = instance.getUser();
        	School school = u.getSchool();
        	if(school != null)
        	{  	schoolnode = new DefaultMutableTreeNode("Modules " + school);
            	root.add(schoolnode);
        	}
        	Course[] courses = instance.getCourseList();
        	DefaultMutableTreeNode node; 
        	for (int i = 0; i < courses.length; i++) {
				Course course = courses[i];
				node = new DefaultMutableTreeNode(course);
				insertScos(course, node);
				if(course.getSchoolID() == 0)
				{
					dwonode.add(node);
				} else {
					schoolnode.add(node);
				}
			}
        	
        }
        
        DefaultTreeModel model = new DefaultTreeModel(root);
        setModel(model);
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		create(null);
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

    String lessonMode = Sco.NORMAL;
	String getLessonMode() {
		return lessonMode;
	}

	void setLessonMode(String lessonMode) {
		this.lessonMode = lessonMode;
	}
		
	public void valueChanged(TreeSelectionEvent e) {
		if(isSelect())
			return;
		TreePath path = e.getPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		boolean sel = e.isAddedPath();
		if(sel)
		{
			strategy.nodeSelected(node);
		}
	}
	
	private SelectStrategy strategy;

	public void nodeSelected(DefaultMutableTreeNode node) {
		Object value = node.getUserObject();
		
		GuiCreator instance = GuiCreator.instance();
		if(value instanceof Course)
		{
			Course c = (Course)value;
		    CoursePanel cp = (CoursePanel) instance.getCoursePanel(c);
		    cp.setLessonMode(getLessonMode());
		    center.loadCenter(cp);
		} else if (value instanceof String) // geen ondescheid tussen alle/school/standaard
		{
			CenterSubPanel panel;
			if(value == ALLE_MODULES)
			{				
				panel = new CourseChoisePanel(dwo.getDwoProfile());
			} else 
			{
				Course[] courses = getCourses(node);
				panel = new CourseChoisePanel(dwo.getDwoProfile(), courses, value);
				
			}
			center.loadCenter(panel); // undo side-effect 'select Alle_modules'
			
		} else if (value instanceof Sco) 
		{
			Sco s = (Sco)value;
		    CenterSubPanel csp = instance.getScoPanel(s);
		    if(csp != null) {
		    	s.setLessonMode(getLessonMode());
		        center.loadTotal(csp);
		    }
		}
	}
	
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

	void setStrategy(SelectStrategy strategy) {
		if(strategy == null)
			strategy = this;
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
}

