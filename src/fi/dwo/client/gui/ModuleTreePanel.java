package fi.dwo.client.gui;

import java.awt.BorderLayout;
import java.awt.LayoutManager;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.DwoIF;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.domain.User;

public class ModuleTreePanel extends JPanel implements TreeSelectionListener {

	private JTree tree;
	private JScrollPane pane;
	private JMenuBar bar;
	private DwoIF dwo;
	private CenterPanel center;
	
	public ModuleTreePanel() {
		super(new BorderLayout());
		tree = new JTree();
		tree.setShowsRootHandles(true);
		tree.addTreeSelectionListener(this);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		pane = new JScrollPane(tree);
		add(pane, BorderLayout.CENTER);
		bar = new JMenuBar();
		JMenu menu; JMenuItem item;
		menu = new JMenu("Bestand");
		item = new JMenuItem("Import");
		menu.add(item);
		bar.add(menu);
		menu = new JMenu("Bewerk");
		bar.add(menu);
		JMenuItem closeBtn = new JMenuItem("<"); // TODO icon..
		closeBtn.setBorderPainted(false);
		closeBtn.setContentAreaFilled(false);
		bar.add(Box.createHorizontalGlue());
		bar.add(closeBtn);
		add(bar, BorderLayout.NORTH);
		
	}

	private void setModel(TreeModel model)
	{
		tree.setModel(model);
	}
	
	private void createModel(DwoIF dwo) {
		this.dwo = dwo;
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Alle modules");
        DefaultMutableTreeNode dwonode  = new DefaultMutableTreeNode("Standaard DWO modules");
        root.add(dwonode);
        DefaultMutableTreeNode schoolnode = null;
        if(dwo != null)
        {
        	GuiCreator instance = GuiCreator.instance();
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
	
	private void insertScos(Course course, DefaultMutableTreeNode node) {
		course.loadScos();
		Sco[] scos = course.getScoList();
		if(scos != null)
		for (int i = 0; i < scos.length; i++) {
			Sco sco = scos[i];
			node.add(new DefaultMutableTreeNode(sco));
		}	
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		create(null);
	}

	public static void create(DwoIF dwo)
	{
		JFrame frame = new JFrame("modules");
		ModuleTreePanel panel = new ModuleTreePanel();
		panel.createModel(dwo);
		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
		
	
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
		TreePath path = e.getPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		boolean sel = e.isAddedPath();
		if(sel)
		{
			Object userObject = node.getUserObject();
			System.out.println(userObject);
			if(userObject instanceof Course)
			{
				Course c = (Course)userObject;
	            CoursePanel cp = (CoursePanel) GuiCreator.instance().getCoursePanel(c);
	            cp.setLessonMode(getLessonMode());
	            center.loadCenter(cp);
			} else if (userObject instanceof String) // geen ondescheid tussen alle/school/standaard
			{
				center.loadCenter(GuiCreator.instance().getCourseChoisePanel());
			} else if (userObject instanceof Sco) 
			{
				Sco s = (Sco)userObject;
                CenterSubPanel csp = GuiCreator.instance().getScoPanel(s);
                if(csp != null) {
                	s.setLessonMode(getLessonMode());
                    center.loadTotal(csp);
                }
			}
		}
	}

}

