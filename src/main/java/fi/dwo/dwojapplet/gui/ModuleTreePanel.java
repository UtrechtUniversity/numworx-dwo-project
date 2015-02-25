package fi.dwo.dwojapplet.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JTree.DynamicUtilTreeNode;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.Descriptor;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.DwoIF;
import fi.dwo.dwojapplet.domain.DwoProfile;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.gui.action.BackupModuleAction;
import fi.dwo.dwojapplet.gui.action.CutCopyAction;
import fi.dwo.dwojapplet.gui.action.DeleteAction;
import fi.dwo.dwojapplet.gui.action.ImportModuleAction;
import fi.dwo.dwojapplet.gui.action.ImportScorm;
import fi.dwo.dwojapplet.gui.action.NewAction;
import fi.dwo.dwojapplet.gui.action.PasteAction;
import fi.dwo.dwojapplet.gui.action.RenameAction;
import fi.dwo.dwojapplet.gui.action.Save2004Action;
import fi.dwo.dwojapplet.gui.action.SaveAppletAction;
import fi.dwo.dwojapplet.gui.action.TeacherStrategy;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModuleTreePanel extends JPanel implements TreeSelectionListener {
    private static final Logger log = Logger.getLogger(ModuleTreePanel.class.getName());

    public static String STANDAARD_DWO_MODULES = TextMapper.getText("Standaard DWO modules");

    public static void initialize(DwoProfile profile) {
        //DwoProfile profile = GuiCreator.instance().getDWO().getDwoProfile();
        if (profile.getID() != 1) // TODO overleg met Peter nodig.
        {
            STANDAARD_DWO_MODULES = profile.getHeader();
        } else {
            STANDAARD_DWO_MODULES = TextMapper.getText("Standaard DWO modules");
        }
    }

    public static final String ALLE_MODULES = TextMapper.getText("Alle modules");

    private boolean isPossible(Object o) {
        return o instanceof Course
                || o instanceof String
                || o instanceof Sco;
    }

    public static String SCHOOL_MODULES = null;
    public static CourseMap STANDAARD_DWO_MAP;
    public static CourseMap SCHOOL_MAP;
    public static TopMap TOP_LEVEL;
    protected JTree tree;
    private JScrollPane pane;
    private JMenuBar bar;
    protected DwoIF dwo;
    private CenterPanel center;
    private IconizedPanel ip;

    void setWait() {
//		if(dwo != null)
//			dwo.setWait();
        DwoHelper.getApplet().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    void setReady() {
//		if(dwo != null)
//			dwo.setReady();
        DwoHelper.getApplet().setCursor(Cursor.getDefaultCursor());
    }

    static class TopMap implements CourseMap, Descriptor {

        Descriptor delegate;

        public TopMap(Descriptor delegate) {
            super();
            this.delegate = delegate;
        }

        /**
         * @return @see fi.dwo.client.domain.Descriptor#getText()
         */
        public String getText() {
            return delegate.getText();
        }

        /**
         * @return @see fi.dwo.client.domain.Descriptor#getHeader()
         */
        public String getHeader() {
            return ALLE_MODULES;
        }

        public void addChild(Course c) {
        }

        public void removeChild(int i) {
        }

        public CourseMap[] getChildren() {
            CourseMap[] c1, c2, c3;
            c1 = STANDAARD_DWO_MAP.getChildren();
            return c1;
            /*if(SCHOOL_MAP==null)return c1;
             c2 = SCHOOL_MAP.getChildren();
             c3 = new Course[c1.length + c2.length];
             System.arraycopy(c1, 0, c3, 0, c1.length);
             System.arraycopy(c2, 0, c3, c1.length, c2.length);
             Arrays.sort(c3);
             return c3;*/
        }

        public void setChildren(CourseMap[] courses) {
        }

        public Object getUserObject() {
            return ALLE_MODULES;
        }

        public Set getChildNames() {
            Set s = SCHOOL_MAP.getChildNames();
            s.addAll(STANDAARD_DWO_MAP.getChildNames());
            return s;
        }

        public CourseMap getParentMap() {
            return null;
        }
    }

    static class StudentTopMap extends TopMap {

        CourseMap map;

        @Override
        public String getHeader() {
            return delegate.getHeader();
        }

        @Override
        public CourseMap[] getChildren() {
            return map.getChildren();
        }

        @Override
        public Object getUserObject() {
            return map.getUserObject();
        }

        @Override
        public Set getChildNames() {
            return map.getChildNames();
        }

        public StudentTopMap(Course course) {
            super(course);
            map = course;
        }

        public StudentTopMap(CourseMap map, Descriptor delegate) {
            super(delegate);
            this.map = map;
        }
    }

    class StandaardMap extends TreeMap {

        public StandaardMap(DefaultMutableTreeNode node) {
            super(node);
        }

//		public Course[] getChildren() {
//			Course[] result = dwo.getCourses();
//			ArrayList list = new ArrayList(result.length);
//			for (int i = 0; i < result.length; i++) {
//				Course c = result[i];
//				if(c.getSchoolID()==0) list.add(c);
//			}
//			return (Course[]) list.toArray(new Course[list.size()]);
//		}
        public CourseMap[] getChildrenFetch() {
            ArrayList v = new ArrayList();
            Enumeration children = node.children();
            while (children.hasMoreElements()) {
                DefaultMutableTreeNode object = (DefaultMutableTreeNode) children.nextElement();
                v.add(object.getUserObject());
            }
            return (CourseMap[]) v.toArray(Course.NO_CHILDREN);
        }

        private CourseMap[] children;

        @Override
        public CourseMap[] getChildren() {
            if (children == null) {
                children = getChildrenFetch();
            }
            return children;
        }

        public void setChildren(Course[] children) {
            this.children = children;
        }

        /* (non-Javadoc)
         * @see fi.dwo.client.gui.ModuleTreePanel.TreeMap#addChild(fi.dwo.client.domain.Course)
         */
        @Override
        public void addChild(Course child) {
            child.setParentID(0);
            if (getChildren() == null) {
                children = new Course[]{child};
            } else {
                int length = children.length;
                Course[] n = new Course[length + 1];
                System.arraycopy(children, 0, n, 0, length);
                n[length] = child;
                children = n;
            }

        }

        /* (non-Javadoc)
         * @see fi.dwo.client.gui.ModuleTreePanel.TreeMap#removeChild(int)
         */
        @Override
        public void removeChild(int index) {
            int length = children.length;
            ((Course) children[index]).setParentID(0);
            Course[] n = new Course[length - 1];
            System.arraycopy(children, 0, n, 0, index);
            System.arraycopy(children, index + 1, n, index, length - 1 - index);
            children = n;
        }

        public CourseMap getParent() {
            return null;
        }
    }

    class TreeMap implements CourseMap {

        private Object userObject;

        TreeMap(DefaultMutableTreeNode node) {
            super();
            this.node = node;
            userObject = node.getUserObject();
        }

        @Override
        public String toString() {
            return userObject.toString();
        }

        DefaultMutableTreeNode node;

        public Object getUserObject() {
            return userObject;
        }

        public void addChild(Course c) {
        }

        public void removeChild(int i) {
        }

        public CourseMap[] getChildren() {
            return getCourses(node);
        }

        public void setChildren(CourseMap[] courses) {
        }

        public Set getChildNames() {
            HashSet names = new HashSet();
            CourseMap[] courses = getChildren();
            for (int i = 0; i < courses.length; i++) {
                names.add(courses[i].toString());
            }
            return names;
        }

        public CourseMap getParentMap() {
            return null;
        }

    }

    class TreeCellRenderer extends DefaultTreeCellRenderer {

        Icon bookIcon;
        boolean isCourse, isMap;

        TreeCellRenderer() {
            super();
            Image book = DwoHelper.getResourceImage("resources/book.png");
            bookIcon = new ImageIcon(book);
        }

        @Override
        public Icon getOpenIcon() {
            if (isCourse) {
                return bookIcon;
            }
            return super.getDefaultOpenIcon();
        }

        @Override
        public Icon getClosedIcon() {
            if (isCourse) {
                return bookIcon;
            }
            return super.getDefaultClosedIcon();
        }

        @Override
        public Icon getLeafIcon() {
            if (isCourse) {
                return bookIcon;
            }
            if (isMap) {
                return getClosedIcon();
            }
            return super.getDefaultLeafIcon();
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {
// bookicon als het een course is
            isCourse
                    = value instanceof DefaultMutableTreeNode
                    && ((DefaultMutableTreeNode) value).getUserObject() instanceof Course
                    && !((Course) ((DefaultMutableTreeNode) value).getUserObject()).isWithChildren();
            isMap = value instanceof DefaultMutableTreeNode
                    && ((DefaultMutableTreeNode) value).getUserObject() instanceof Sco;
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
        TreeCellRenderer renderer = new TreeCellRenderer();
        renderer.updateUI();
        tree.setCellRenderer(renderer);
        tree.updateUI();;
    }

    protected void createMenubar(Box hbox) {
        bar = new JMenuBar();
        bar.setOpaque(false);
        bar.setVisible(GuiCreator.instance().getUser().hasRight(User.MODIFY_MODULES_RIGHT));
        JMenu menu;
        JMenuItem item;
        menu = new JMenu(TextMapper.getText("file"));
        item = new JMenuItem("Nieuwe map");
        menu.add(item);
        item.setAction(new NewAction(true, true));
        item = new JMenuItem("Nieuwe module/activiteit");
        menu.add(item);
        item.setAction(new NewAction(false, false));
        if (DwoHelper.isSecure()) {
            menu.addSeparator();
            item = new JMenuItem("Import module");
            menu.add(item);
            item.setAction(new ImportModuleAction());
            item = new JMenuItem("Backup module");
            menu.add(item);
            item.setAction(new BackupModuleAction());
            if (DwoHelper.isScormExportLoggedIn() || DwoHelper.isAppletExportLoggedIn()) {
                menu.addSeparator();
            }
            if (DwoHelper.isScormExportLoggedIn()) {
                item = new JMenuItem("Import activiteit");
                menu.add(item);
                item.setAction(new ImportScorm());
                item = new JMenuItem("Backup activiteit");
                menu.add(item);
                item.setAction(new Save2004Action());
            }
            if (DwoHelper.isAppletExportLoggedIn()) {
                item = new JMenuItem("Export Applet");
                menu.add(item);
                item.setAction(new SaveAppletAction());
            }
        }
        bar.add(menu);
        menu = new JMenu(TextMapper.getText("edit"));
        item = new JMenuItem();
        menu.add(item);
        item.setAction(new CutCopyAction(true));
        item.setText(TextMapper.getText("cut"));
        item.setActionCommand("cut");
        item = new JMenuItem();
        menu.add(item);
        item.setAction(new CutCopyAction(false));
        item.setText(TextMapper.getText("copy"));
        item.setActionCommand("copy");
        item = new JMenuItem("paste");
        menu.add(item);
        item.setAction(new PasteAction());
        menu.addSeparator();
        item = new JMenuItem(new RenameAction());
        menu.add(item);
        menu.addSeparator();
        item = new JMenuItem("delete");
        menu.add(item);
        item.setAction(new DeleteAction());
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
        ip = new IconizedPanel(TextMapper.getText(TextMapper.GUIMNU_MAIN_MENU));
        JButton closeBtn = new JButton(ip.getCloseAction()); // TODO icon..
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(closeBtn);
        add(toolbar, BorderLayout.NORTH);
        ip.setWindow(this);
    }

    protected void setModel(TreeModel model) {
        tree.setModel(model);
    }

    @Override
    public void setEnabled(boolean b) {
        tree.setEnabled(b);
        bar.getMenu(0).setEnabled(b);
        bar.getMenu(1).setEnabled(b);
    }

    class LazyMutableTreeNode extends DynamicUtilTreeNode {

        private Course course;

        public LazyMutableTreeNode(Course course) {
            super(course, Course.NO_CHILDREN);
            this.course = course;
            cachemap.put(course, this);
        }

        void setLoaded() {
            loadedChildren = true;
        }

        boolean isLoaded() {
            return loadedChildren;
        }

        @Override
        protected void loadChildren() {
            setWait(); // zijeffect: recusie!
            if (!loadedChildren) {
                if (course.isWithChildren()) {
                    CourseMap[] courses;
                    childValue = courses = course.getChildren();
                    loadedChildren = true;
                    for (int i = 0; i < courses.length; i++) {
                        ((Course) courses[i]).setParentMap(course); // FIXME rare plek voor deze link leggen?
                        insert(new LazyMutableTreeNode((Course) courses[i]), i);
                    }
                } else {
                    if (course.getScoList() == null) {
                        course.loadScos();
                    }
                    childValue = course.getScoList();
                    super.loadChildren();
                }
            }
            setReady();
        }

        @Override
        public void removeAllChildren() {
            setLoaded();
            super.removeAllChildren();
            loadedChildren = false;
        }

    }

    protected void createModel(DwoIF dwo) {
        this.dwo = dwo;

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(ALLE_MODULES);
        DefaultMutableTreeNode dwonode = new DefaultMutableTreeNode(STANDAARD_DWO_MODULES);
        STANDAARD_DWO_MAP = new StandaardMap(dwonode);
        root.add(dwonode);
        DefaultMutableTreeNode schoolnode = null;
        if (dwo != null) {
            GuiCreator instance = GuiCreator.instance();
            if (instance.getMainPanel() != null) {
                setCenterPanel(instance.getMainPanel().getCenter());
            }
            User u = dwo.getUser();
            School school = u.getSchool();
            if (school != null) {
                SCHOOL_MODULES = "Modules " + school;
                schoolnode = new DefaultMutableTreeNode(SCHOOL_MODULES);
                SCHOOL_MAP = new StandaardMap(schoolnode);
                root.add(schoolnode);
            }
            Course[] courses = instance.getCourseList();
            sort(courses);
            DefaultMutableTreeNode node;
            for (int i = 0; i < courses.length; i++) {
                Course course = courses[i];
                node = new LazyMutableTreeNode(course);
//				if(course.isWithChildren())
//				{
//					appendCourseMap(course, node);
//				}
//				insertScos(course, node);
                if (course.getSchoolID() == 0) {
                    if (course.getParentID() != 0) {
                        try {
                            Course parent = (Course) PersistenceFacade.instance().get(course.getParentID(), Course.class);
                            course.setParentMap(parent);
                            DefaultMutableTreeNode find = find(parent, dwonode);
                            if (find == null) {
                                find = dwonode;
                            }
                            if (find instanceof LazyMutableTreeNode) {
                                ((LazyMutableTreeNode) find).setLoaded();
                            }
                            find.add(node);
                        } catch (PersistenceException e) {
                            log.log(Level.SEVERE,null,e);
                        }
                    } else {
                        dwonode.add(node);
                        course.setParentMap(STANDAARD_DWO_MAP);
                    }
                } else {
                    if (course.getParentID() != 0) {
                        try {
                            Course parent = (Course) PersistenceFacade.instance().get(course.getParentID(), Course.class);
                            course.setParentMap(parent);
                            DefaultMutableTreeNode find = find(parent, schoolnode);
                            if (find == null) {
                                find = schoolnode;
                            }
                            if (find instanceof LazyMutableTreeNode) {
                                ((LazyMutableTreeNode) find).setLoaded();
                            }
                            find.add(node);
                        } catch (PersistenceException e) {
            
                            log.log(Level.SEVERE,null,e);
                        }

                    } else {
                        course.setParentMap(SCHOOL_MAP);
                        schoolnode.add(node);
                    }
                }
            }

        }
        if (!DwoHelper.isAdminLoggedIn()) {
            root = prune(root);
            root.setParent(null);
        } else {
            TOP_LEVEL = new TopMap(dwo.getDwoProfile()); // initialize TOP_LEVEL, if admin
        }

        DefaultTreeModel model = new DefaultTreeModel(root);
        setModel(model);
    }

    /**
     * Shorten root. Template pattern. Hier een dummy.
     *
     * @param root
     * @return root
     */
    protected DefaultMutableTreeNode prune(DefaultMutableTreeNode root) {
        TOP_LEVEL = new TopMap(dwo.getDwoProfile());
        return root;
    }

    private void sort_fout(Course[] courses) {
        // topology sort.

        boolean again;
        do {
            again = false;
            more:
            for (int i = 0; i < courses.length;) {
                Course c = courses[i];
                if (c.getParentID() != 0) {
                    for (int j = i + 1; j < courses.length; j++) {
                        if (c.getParentID() == courses[j].getID()) {
                            System.arraycopy(courses, i + 1, courses, i, j - i);
                            courses[j] = c;
                            again = true;
                            continue more;
                        }
                    }
                }
                i++;
            }
        } while (again);
    }

    // topologie sort maar behoud ordening.
    private void sort(Course[] courses) {
        boolean again;
        if (courses == null || courses.length <= 1) {
            return;
        }
        //System.err.println(Arrays.asList(courses));
        do {
            again = false;
            more:
            for (int i = 1; i < courses.length; i++) {
                Course c = courses[i];
                if (c.getParentID() == 0) { // toplevel
                    int j;
                    for (j = i - 1; j >= 0; j--) {
                        if (courses[j].getParentID() == 0) {
                            if (j == i - 1) {
                                break;
                            }
                            // move i to j+1
                            System.arraycopy(courses, j + 1, courses, j + 2, i - j - 1);
                            courses[j + 1] = c;
                            continue more;
                        }
                    }
                    if (j == -1) { // move i to 0, no 0's found
                        System.arraycopy(courses, 0, courses, 1, i);
                        courses[0] = c;
                        continue more;
                    }
                } else {
                    int pid = c.getParentID();
                    int j;
                    for (j = i - 1; j >= 0; j--) {
                        if (courses[j].getParentID() == pid || courses[j].getID() == pid) {
                            if (j == i - 1) {
                                break;
                            }
                            // move i to j+1
                            System.arraycopy(courses, j + 1, courses, j + 2, i - j - 1);
                            courses[j + 1] = c;
                            continue more;

                        }
                    }
                    if (j == -1) {
                        again = true; // still unsorted? Redo only if parent in i+1..length
                    }
                }
            }
        } while (again);
        //System.err.println(Arrays.asList(courses));
    }

    protected void insertScos(Course course, DefaultMutableTreeNode node) {
        course.loadScos();
        Sco[] scos = course.getScoList();
        if (scos != null) {
            for (int i = 0; i < scos.length; i++) {
                Sco sco = scos[i];
                node.add(new DefaultMutableTreeNode(sco));
            }
        }
    }

    public void select(Object object) {
        if (object == null) // TODO is er wel een null value in de tree?
        {
            return;
        }
        TreePath path = tree.getSelectionPath();
        if (path != null && ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject() == object) {
            return;
        }
        // search in tree where userObject equals object
        pushSelect();
        TreeModel model = tree.getModel();
        Object root = model.getRoot();
        select(object, (DefaultMutableTreeNode) root);
        popSelect();
    }

    private void select(Object object, DefaultMutableTreeNode node) {
        node = find(object, node);
        if (node != null) {
            TreeNode[] paths = node.getPath();
            TreePath path = new TreePath(paths); // Dit gaat fout bij studenten, als de tree gepruned is.
            tree.setSelectionPath(path);
        }
    }

    private Map cachemap = new IdentityHashMap();

    private DefaultMutableTreeNode find(Object object, DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (userObject.equals(object)) {
            return node;
        }
        DefaultMutableTreeNode o = (DefaultMutableTreeNode) cachemap.get(object);
        if (o != null) {
            return o;
        }
        if (!isPossible(object)) {
            return null;
        }

//System.err.println("FIND " + object + " START in " + node);
        Object parent = getParent(object);
        if (parent != null) {
            DefaultMutableTreeNode parentNode = find(parent, node);
            if (parentNode != null) {
//System.err.println(" found parent " + parent + " start in " + parentNode);
                node = parentNode;

                Enumeration e = node.children();
                while (e.hasMoreElements()) {
                    o = (DefaultMutableTreeNode) e.nextElement();
                    cachemap.put(o.getUserObject(), o);
                    if (object.equals(o.getUserObject())) {
                        cachemap.put(object, o);
//System.err.println("found " + o);
                        return o;
                    }
                }
            }
        } else {
//System.err.println("no parent, slow");
        }

        Enumeration e = node.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            o = (DefaultMutableTreeNode) e.nextElement();
            cachemap.put(o.getUserObject(), o);
            //o = find(object, o);
            if (object.equals(o.getUserObject())) {
                cachemap.put(object, o);
//System.err.println("found " + o);
                return o;
            }
            //if(o != null)
            //	return o; // early out.
        }
//System.err.println("not found");
        return null;
    }

    private Object getParent(Object object) {
        if (object instanceof Course) {
            Course c = (Course) object;

            CourseMap parentMap = c.getParentMap();
            if (parentMap == null) {
                int id = c.getParentID();
                try {
                    parentMap = (CourseMap) PersistenceFacade.instance().get(id, Course.class);
                    c.setParentMap(parentMap);
                } catch (PersistenceException e) {
                }

            }
            return parentMap;
        }
        if (object instanceof Sco) {
            Sco s = (Sco) object;
            return s.getCourse();
        }
        return null;
    }

    public static ModuleTreePanel newInstance(DwoIF dwo) {
        ModuleTreePanel panel = new ModuleTreePanel();
        panel.createModel(dwo);
        return panel;
    }

    public static ModuleTreePanel newStudentInstance(DwoIF dwo) {
        ModuleTreePanel panel = new ModuleTreePanel() {
            /*
             * Hier een aanpassing, voor leerlingen/gasten wordt de tree afgeknot tot het punt 
             * waar hij uitwaaiert.
             */
            @Override
            protected DefaultMutableTreeNode prune(DefaultMutableTreeNode root) {
                MutableTreeNode dwonode = (MutableTreeNode) root.getFirstChild();
                if (dwonode.getChildCount() == 0) {
                    root.remove(dwonode);
                }
                if (root.getChildCount() == 0) {
                    return root;
                }
                MutableTreeNode schoolnode = (MutableTreeNode) root.getLastChild();
                if (schoolnode.getChildCount() == 0) {
                    root.remove(schoolnode);
                }

                while (root.getChildCount() == 1) {

                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getFirstChild();
// alleen MAPPEN mogen root worden
                    if (child.getUserObject() instanceof Course
                            && !((Course) child.getUserObject()).isWithChildren()) {
                        break;
                    }
                    root = child;
                }
// zet TOP_LEVEL als startpunt voor "Module Overzicht"			
                if (root.getUserObject() instanceof Course) {
                    TOP_LEVEL = new StudentTopMap((Course) root.getUserObject());
                } else {
                    TOP_LEVEL = new TopMap(dwo.getDwoProfile());  // TODO wat als root=schoolnode of dwonode? en ADMIN
                }
                return root;
            }

        };
        panel.createModel(dwo);
        return panel;

    }

    public static void create(DwoIF dwo) {
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

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (isSelect()) {
            return;
        }
        TreePath path = e.getPath();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        boolean sel = e.isAddedPath();
        if (sel) {
            callStrategy(node);
        }
    }

    private SelectStrategy strategy;

    private boolean isSelect() {
        return cnt > 0;
    }
    private int cnt;

    private void pushSelect() {
        cnt++;
    }

    private void popSelect() {
        cnt--;
    }

    protected static Course[] getCourses(DefaultMutableTreeNode node) {
        Vector v = new Vector(node.getChildCount());
        Enumeration e = node.children();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode object = (DefaultMutableTreeNode) e.nextElement();
            Object uo = object.getUserObject();
            if (uo instanceof Course) {
                v.add(uo);
            }
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
        if (strategy == null) {
            strategy = delegate;
        }
        mtp.setPopup(strategy);
        this.strategy = strategy;
    }

    public void updateNode(Course course) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        DefaultMutableTreeNode node = find(course, root);
        removeChildrenFromCache(node, cachemap.values());
        node.removeAllChildren();

        if (!(node instanceof LazyMutableTreeNode)) {
            insertScos(course, node);
        }
        model.nodeStructureChanged(node);
    }

    private void removeChildrenFromCache(TreeNode node, Collection values) {
        if (notLoaded(node)) {
            return;
        }

        int len = node.getChildCount();
        for (int i = 0; i < len; i++) {
            TreeNode child = node.getChildAt(i);
            boolean b = values.remove(child);
            if (!child.isLeaf()) {
                removeChildrenFromCache(child, values);
            }
        }

    }

    private boolean notLoaded(TreeNode node) {
        if (node instanceof LazyMutableTreeNode) {
            return !((LazyMutableTreeNode) node).isLoaded();
        }
        return false;
    }

    public DefaultMutableTreeNode getRoot() {
        return (DefaultMutableTreeNode) tree.getModel().getRoot();
    }

    public void updateNodeMap(CourseMap map) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        DefaultMutableTreeNode node = find(map.getUserObject(), root);
        removeChildrenFromCache(node, cachemap.values());
        node.removeAllChildren();

        if (!(node instanceof LazyMutableTreeNode)) {
            appendCourseMap(map, node);
        }
        model.nodeStructureChanged(node);
    }

    private void appendCourseMap(CourseMap map, DefaultMutableTreeNode node) {
        CourseMap[] courses = map.getChildren();
        DefaultMutableTreeNode child;
        for (int i = 0; i < courses.length; i++) {
            Course course = (Course) courses[i];
            course.setParentMap(map);
            child = new LazyMutableTreeNode(course);
            node.add(child);
//			if(course.isWithChildren())
//				appendCourseMap(course, child);
//			else
//				insertScos(course, child);
        }
    }

    public void toSelectedNode() {
        TreePath path = tree.getSelectionPath();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        callStrategy(node);
    }

    public CourseMap toCourseMap(DefaultMutableTreeNode node) {
        Object o = node.getUserObject();
        if (o instanceof CourseMap) {
            return ((CourseMap) o);
        } else // vergeten?
        if (o instanceof Sco) {
            return (new TreeMap(node));
        } else if (o.equals(SCHOOL_MODULES)) {
            return (SCHOOL_MAP);
        } else if (o == STANDAARD_DWO_MODULES) {
            return (STANDAARD_DWO_MAP);
        }
        return TOP_LEVEL;
    }

    private void callStrategy(DefaultMutableTreeNode node) {
        strategy.nodeSelected(toCourseMap(node));
    }
}
