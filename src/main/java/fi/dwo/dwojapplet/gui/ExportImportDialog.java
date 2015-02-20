/**
 *
 */
package fi.dwo.dwojapplet.gui;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonModel;
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

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.beans.appletutil.AppletUtil;
import fi.dwo.commons.exceptions.CourseException;
import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.DwoProfile;
import fi.dwo.dwojapplet.domain.ResultsModuleIF;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.gui.GuiCreatorTeacher.LazyAppletConfig;
import fi.dwo.dwojapplet.persistence.DbAccessCreator;
import fi.dwo.dwojapplet.persistence.MapperCreator;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;

/**
 * @author Velth101
 *
 */
public class ExportImportDialog extends JDialog implements ActionListener, CourseContainer {

    public DwoProfile profile;

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
            setTitle(TextMapper.getText("Kopiëer modules"));
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
            Course[] c = model.getCourses();
            for (int i = 0; i < c.length; i++) {
                if (Boolean.TRUE.equals(model.getValueAt(i, 0))) {
                    c[i].loadScos();
                    count += c[i].getScoList().length;
                }
            }
            bar.setMaximum(count);
            bar.setMinimum(0);
            bar.setValue(0);
            invalidate();
            setSize(400, getPreferredSize().height);
            //pack();
        }

        JProgressBar bar = new JProgressBar();
        JLabel status = new JLabel(" ");
        JLabel status1 = new JLabel(" ");
        JButton cancel = new JButton(TextMapper.getText("cancel"));
        int count;
        private boolean fuse;

        @Override
        public void run() {
            School s = user.getSchool();
            HashSet set = new HashSet();
            try {
                Course[] courses;
                if (map == null) {
                    courses = (Course[]) MapperCreator.instance(Course.class).get(s);
                } else {
                    courses = (Course[]) MapperCreator.instance(Course.class).get(map);
                }
                for (int i = 0; i < courses.length; i++) {
                    Course course = courses[i];
                    set.add(course.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            int n = 0;
            for (int i = 0; i < model.getCourses().length && fuse; i++) {
                if (Boolean.TRUE.equals(model.getValueAt(i, 0))) {
                    Course c = model.getCourses()[i];
                    Course newc = null;
                    String name = c.getName();
                    String description = c.getDescription();
                    name = CourseManagementPanel.replaceDuplicate(name, set);
                    try {
                        newc = PersistenceFacade.instance().addCourse(s, name, description, profile, map, false);
                        if (map != null) {
                            map.addChild(newc);
                        } else {
                            ModuleTreePanel.SCHOOL_MAP.addChild(newc);
                        }
                        newc.setScoList(new Sco[0]);
                    } catch (CourseException e) {
                        e.printStackTrace();
                    }
                    status.setText(TextMapper.format(TextMapper.GUIEID_MSG4, new Object[]{name}));
                    status.invalidate();
                    status1.setText("   ");
                    status1.invalidate();
                    Sco[] scos = c.getScoList();
                    for (int j = 0; j < scos.length && fuse; j++) {
                        status1.setText(" ... " + scos[j].getScoName());
                        status1.invalidate();
                        bar.setValue(n++);
                        doit(newc, scos[j]);
                    }

                }
            }
            dispose();
        }

        private void doit(Course course, Sco sco) {
            validate();
            repaint();
            try {
// TODO duplicate code.... create constructor LazyAppletConfig(Sco)
                String description = sco.getDescription();
                LazyAppletConfig config = new LazyAppletConfig();
                config.setSco(sco);
                String name = sco.getScoName();
                int aid = sco.getAppletID();
                int sid = sco.getScoID();
                config.setAppletID(aid);
                config.setAppletConfigID(-sid); // HACK HACK negatief = scoid
                config.setName(name);
                Sco news = PersistenceFacade.instance().addSco(course, config, name, description, sco.isShowScore());
// TODO common code?		
                news.setSequencenr(sco.getSequencenr()); // TODO noop?
                Sco[] oldsa = course.getScoList();
                Sco[] newsa = new Sco[oldsa.length + 1];
                System.arraycopy(oldsa, 0, newsa, 0, oldsa.length);
                newsa[oldsa.length] = news;
                course.setScoList(newsa);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            fuse = false;
        }

        @Override
        public void windowActivated(WindowEvent arg0) {
        }

        @Override
        public void windowClosed(WindowEvent arg0) {
            fuse = false;
        }

        @Override
        public void windowClosing(WindowEvent arg0) {
            fuse = false;
        }

        @Override
        public void windowDeactivated(WindowEvent arg0) {
        }

        @Override
        public void windowDeiconified(WindowEvent arg0) {
        }

        @Override
        public void windowIconified(WindowEvent arg0) {
        }

        @Override
        public void windowOpened(WindowEvent arg0) {
        }

    }

    private static final String COPY = "Copy";
    private static final School ALLE_SCHOLEN = new School(-1);

    static class PijlIcon implements Icon {

        private Polygon p;

        @Override
        public int getIconHeight() {
            return 30;
        }

        @Override
        public int getIconWidth() {
            return 50;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(c.getForeground());
            p.translate(x, y);
            g.fillPolygon(p);
            p.translate(-x, -y);

        }

        PijlIcon() {
            this.p = new Polygon();
            int w1 = getIconWidth();
            int w2 = w1 - 10;

            p.addPoint(0, 12);
            p.addPoint(w2, 12);
            p.addPoint(w2, 5);
            p.addPoint(w1, 15);
            p.addPoint(w2, 25);
            p.addPoint(w2, 18);
            p.addPoint(0, 18);

        }
    }

    class CellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

        ImportModuleModel model;
        int row;
        ActionListener listener;
        String command = "Preview";

        @Override
        public void actionPerformed(ActionEvent e) {
            if (listener != null) {
                ActionEvent ae = new ActionEvent(model.courses[row], ActionEvent.ACTION_PERFORMED, command);
                listener.actionPerformed(ae);
            }
            fireEditingCanceled();
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }

        @Override
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

        @Override
        public Object getElementAt(int index) {
            return school[index].getName();
        }

        @Override
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
            if (courses == null) {
                this.courses = new Course[0];
            } else {
                this.courses = courses;
            }
            imports = new Object[this.courses.length];
            fireTableDataChanged();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public int getRowCount() {
            return courses.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 1:
                    return courses[rowIndex].getName();
                case 2:
                    return "Preview";
                case 0:
                    return imports[rowIndex];
            }
            return null;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 1:
                    return TextMapper.getText("Module");
                case 0:
                case 2:
                    return "";
            }
            return super.getColumnName(column);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex != 1;
        }

        /* (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
         */
        @Override
        public Class getColumnClass(int column) {
            switch (column) {
                case 0:
                    return Boolean.class;
            }
            return super.getColumnClass(column);
        }

        /* (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
         */
        @Override
        public void setValueAt(Object value, int row, int column) {
            switch (column) {
                case 0:
                    imports[row] = value;
                    fireTableCellUpdated(row, column);
                    return;
            }
        }

    }

    int profileID = 1;

    class ExportModuleModel extends AbstractTableModel {

        HashMap dirty = new HashMap();
        Course[] courses;

        private void copyInto(CourseMap[] courseMaps, Vector v) {
            for (int i = 0; i < courseMaps.length; i++) {
                Course course = (Course) courseMaps[i];
                if (course.getDwoProfile() == profileID && course.getSchoolID() == user.getSchool().getSchoolID()) {
                    if (course.isWithChildren()) {
                        copyInto(course.getChildren(), v);
                    } else {
                        v.addElement(course);
                    }
                }
            }
        }

        public ExportModuleModel(User user) throws PersistenceException {

            courses = PersistenceFacade.instance().getCourses(user);
            if (courses == null) {
                courses = new Course[0];
            }
// filter only courses within profile.
            Vector v = new Vector();
            copyInto(courses, v);
            courses = new Course[v.size()];
            v.toArray(courses);

        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public int getRowCount() {
            return courses.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return courses[rowIndex].isExport() ? Boolean.TRUE : Boolean.FALSE;
            }
            return courses[rowIndex].getName();
        }

        @Override
        public Class getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Boolean.class;
                case 1:
                    return String.class;
            }
            return super.getColumnClass(columnIndex);
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "";
                case 1:
                    return TextMapper.getText("Module");
            }
            return super.getColumnName(column);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return true;
            }
            return super.isCellEditable(rowIndex, columnIndex);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                Course course = courses[rowIndex];
                boolean export = course.isExport();
                if (((Boolean) aValue).booleanValue() != export) {
                    course.setExport(!export);
                    if (!dirty.containsKey(course)) {
                        dirty.put(course, export ? Boolean.TRUE : Boolean.FALSE);
                    }
                    fireTableCellUpdated(rowIndex, columnIndex);
                }
            }

        }

    }

    class ExportSchoolModel extends AbstractTableModel {

        School[] schools;
        Object[] export;
        private Map map;

        @Override
        public int getColumnCount() {
            return 2;
        }

        public int getIndex(int schoolID) {
            Object o = map.get(new Integer(schoolID));
            if (o != null) {
                return ((Number) o).intValue();
            }
            return -1;
        }

        @Override
        public int getRowCount() {
            return export.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return export[rowIndex];
                case 1:
                    return schools[rowIndex].getName();
            }
            return null;
        }

        ExportSchoolModel(School[] s) {
            schools = s;
            export = new Object[s.length];
            map = new Hashtable();
            for (int i = 0; i < s.length; i++) {
                map.put(new Integer(s[i].getSchoolID()), new Integer(i));
            }
        }

        @Override
        public Class getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Boolean.class;
                case 1:
                    return String.class;
            }
            return super.getColumnClass(columnIndex);
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "";
                case 1:
                    return "School";
            }
            return super.getColumnName(column);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return (columnIndex == 0);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            switch (columnIndex) {
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
    private Course map;

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

        setTitle(TextMapper.getText(TextMapper.GUIC_COURSE_SHARE));
        pane = new JTabbedPane();
        JPanel exportPanel = new JPanel(new BorderLayout(5, 5));
        JPanel importPanel = new JPanel(new BorderLayout());
        enableImport = new JCheckBox(TextMapper.getText(TextMapper.GUIEID_MSG2));
// From DATABASE
        enableImport.setSelected(user.getSchool().isExport());
// track changes
        enableImport.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent event) {

                School school = user.getSchool();
                boolean oldExport = school.isExport();
                if (oldExport == enableImport.isSelected()) {
                    return;
                }
                school.setExport(enableImport.isSelected());
                try {
                    PersistenceFacade.instance().updateSchool(school);
                } catch (PersistenceException e) {
                    school.setExport(oldExport);
                    enableImport.setSelected(oldExport);
                    JOptionPane.showMessageDialog(enableImport, e.getMessage(), e.getClass().getName(), JOptionPane.ERROR_MESSAGE);
                }
                ieEnabler();

            }
        });

        pane.insertTab(TextMapper.getText("Modules opvragen"), null, importPanel, null, 0);
        pane.insertTab(TextMapper.getText("Modules beschikbaar stellen"), null, exportPanel, null, 1);
        pane.insertTab(TextMapper.getText("Toestaan"), null, enableImport, null, 2);

// if enableImport is not checked:
        ieEnabler();

        getContentPane().add(pane);

        JComponent exportModules;
        if (CenterPanel.isIconizer() && false) {
// Dit werkt niet!	Geen idee waarom niet		
            ModuleTreePanel mtp = new ModuleTreePanel();
            mtp.createModel(GuiCreator.instance().dwo);
            exportModules = mtp;
            mtp.setPreferredSize(new Dimension(200, 100));
            mtp.setMinimumSize(mtp.getPreferredSize());
        } else {
            exportModuleModel = new ExportModuleModel(user);
            JTable exportModuleTable = new JTable(exportModuleModel);
            TableUtil.setJTableSizes(exportModuleTable);
            TableColumn kolom
                    = exportModuleTable.getColumnModel().getColumn(0);
            int prefWidth = kolom.getPreferredWidth();
            kolom.setMaxWidth(prefWidth);
            kolom.setMinWidth(prefWidth);
            kolom.setPreferredWidth(prefWidth);
            exportModules = new JScrollPane(exportModuleTable);

        }
        School[] schools = (School[]) PersistenceFacade.instance().get(School.class, Boolean.TRUE);
        final ExportSchoolModel exportSchoolModel = new ExportSchoolModel(schools);
        JTable exportSchoolTable = new JTable(exportSchoolModel);
        TableUtil.setJTableSizes(exportSchoolTable);
        TableColumn kolom
                = exportSchoolTable.getColumnModel().getColumn(0);
        int prefWidth = kolom.getPreferredWidth();
        kolom.setMaxWidth(prefWidth);
        kolom.setMinWidth(prefWidth);
        JScrollPane exportSchools = new JScrollPane(exportSchoolTable);
        Box exportSplit = Box.createHorizontalBox();
        exportSplit.add(exportModules);
        JLabel deelLabel = new JLabel(TextMapper.getText("Delen met"), new PijlIcon(), JLabel.CENTER);
        deelLabel.setVerticalTextPosition(JLabel.TOP);
        deelLabel.setHorizontalTextPosition(JLabel.CENTER);
        exportSplit.add(deelLabel);
        Box exportSchoolBox = Box.createVerticalBox();
        exportSchoolBox.add(exportSchools);
        final JCheckBox exportAlleScholen = new JCheckBox(TextMapper.getText("Alle scholen"));
        exportSchoolBox.add(exportAlleScholen);
        exportSplit.add(exportSchoolBox);

        initializeExportSchoolModels(exportSchoolModel, exportAlleScholen.getModel());

        JLabel label = new JLabel(TextMapper.getText(TextMapper.GUIEID_MSG3));

        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.add(label);
        exportPanel.add(p, BorderLayout.NORTH);
        exportPanel.add(exportSplit, BorderLayout.CENTER);
        Box buttonBox = Box.createHorizontalBox();
        p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.add(buttonBox);
        exportPanel.add(p, BorderLayout.SOUTH);
        JButton exportOK = new JButton("OK");
        exportOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                persistCourses(exportModuleModel.dirty);
                updateSchoolTo(user.getSchool(), exportSchoolModel, exportAlleScholen.isSelected());
                ExportImportDialog.this.dispose();
            }
        });

        JButton exportCancel = new JButton(TextMapper.getText(TextMapper.BTN_CANCEL));
        exportCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExportImportDialog.this.dispose();
            }
        });
        JButton exportApply = new JButton(TextMapper.getText("toepassen"));
        exportApply.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                HashMap dirty = exportModuleModel.dirty;
                persistCourses(dirty);
                updateSchoolTo(user.getSchool(), exportSchoolModel, exportAlleScholen.isSelected());
                dirty.clear();
            }
        });
        buttonBox.add(exportOK);
        buttonBox.add(exportCancel);
        buttonBox.add(exportApply);
// importstuff

        JLabel header = new JLabel(TextMapper.getText(TextMapper.GUIEID_MSG1));
        p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.add(header);
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
        prefWidth = c.getPreferredSize().width + 3;
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

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                int index = importSchoolList.getSelectedIndex();
                if (index < 0 || lastIndex == index) {
                    return;
                }
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
        JLabel view = new JLabel(TextMapper.getText("Scholen"));
        view.setBorder(BorderFactory.createRaisedBevelBorder());
        view.setHorizontalAlignment(SwingConstants.CENTER);
        importSchools.setColumnHeaderView(view);
        Box importSplit = Box.createHorizontalBox();
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
        JButton importOK = new JButton(TextMapper.getText("copy"));
        importOK.setActionCommand(COPY);
        importOK.addActionListener(this);

        buttonBox.add(importOK);
        buttonBox.add(Box.createHorizontalStrut(100));
// sizeen
        setSize(getContentPane().getPreferredSize());
        pack();
    }

    private void initializeExportSchoolModels(
            ExportSchoolModel exportSchoolModel, ButtonModel model) {
        Hashtable wheredef = new Hashtable();
        wheredef.put("schoolFrom", new Integer(user.getSchool().getSchoolID()));
        try {
            Vector result = DbAccessCreator.instance().getTable("tblfromto", wheredef);
            Enumeration e = result.elements();
            while (e.hasMoreElements()) {
                Hashtable row = (Hashtable) e.nextElement();
                int to = ((Number) row.get("schoolTo")).intValue();
                if (to == ALLE_SCHOLEN.getSchoolID()) {
                    model.setSelected(true);
                } else {
                    int index = exportSchoolModel.getIndex(to);
                    if (index >= 0) {
                        exportSchoolModel.export[index] = Boolean.TRUE;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlRpcException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    protected void updateSchoolTo(School from,
            ExportSchoolModel exportSchoolModel, boolean all) {
        int cnt = all ? 1 : 0;
        int rows = exportSchoolModel.getRowCount();
        for (int i = 0; i < rows; i++) {
            if (Boolean.TRUE.equals(exportSchoolModel.export[i])) {
                cnt++;
            }
        }
        School[] to = new School[cnt];
        int index = 0;
        for (int i = 0; i < rows; i++) {
            if (Boolean.TRUE.equals(exportSchoolModel.export[i])) {
                to[index++] = exportSchoolModel.schools[i];
            }
        }
        if (all) {
            to[index] = ALLE_SCHOLEN;
        }
        boolean result
                = PersistenceFacade.instance().updateSchoolTo(from, to);
    }

    /**
     * Update courses. Als het fout gaat, cancel dat wat nog niet gepersist is.
     *
     * @param dirty
     */
    protected void persistCourses(HashMap dirty) {
        Iterator iterator = dirty.entrySet().iterator();
        boolean inerror = false;
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Course course = (Course) entry.getKey();
            boolean oldExport = Boolean.TRUE.equals(entry.getValue());
            if (oldExport != course.isExport()) {
                if (inerror) {
                    course.setExport(oldExport);
                } else {
                    try {
//System.out.println("persistCourses " + course.getName());
                        PersistenceFacade.instance().updateCourse(course);
                    } catch (CourseException e) {
                        JOptionPane.showMessageDialog(this, e.getMessage(), e.getClass().getName(), JOptionPane.ERROR_MESSAGE);
                        e.printStackTrace();
                        inerror = true;
                    }
                }
            }
        }
        if (inerror) {
            exportModuleModel.fireTableDataChanged();
        }
    }

    private void ieEnabler() {
        if (enableImport.isSelected()) {
            pane.setEnabledAt(0, true);
            pane.setEnabledAt(1, true);
        } else {
            pane.setEnabledAt(0, false);
            pane.setEnabledAt(1, false);
            pane.getModel().setSelectedIndex(2);
        }
    }

    /**
     * @param owner
     * @param u
     * @param p
     * @throws HeadlessException
     * @throws PersistenceException
     */
    public ExportImportDialog(Frame owner, User u, DwoProfile p) throws PersistenceException {
        super(owner);
        this.user = u;
        this.profileID = p.getID();
        profile = p;
        initialize();
    }

    /**
     * @param e
     * @param owner
     * @param modal
     * @throws HeadlessException
     */

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof Course) {
            Course course = (Course) e.getSource();
            CoursePanel cp = (CoursePanel) course.getCoursePanel();
            cp.setLessonMode(Sco.BROWSE);
            this.previewPanel = cp;
            cp.setCenterPanel(this);
            showCourseDialog(this, previewPanel, course.getName());
        } else if (COPY.equals(e.getActionCommand())) {
            ImportTask r = new ImportTask(this, importModuleModel);
            new Thread(r).start();
            r.show();
        }

    }

    // TODO DIALOG met [sluiten] knop.
    private void showCourseDialog(JDialog parent,
            JPanel content, String title) {
        JDialog jd = new JDialog(parent, title, true);
        jd.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        jd.setContentPane(content);
        jd.pack();
// center...
        int x = parent.getX() + parent.getWidth() / 2;
        int y = parent.getY() + parent.getHeight() / 2;
        jd.setLocation(x - jd.getWidth() / 2, y - jd.getHeight() / 2);
        jd.setVisible(true);
    }

    /**
     * @param args
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        DWO dwo = new DWO();
        dwo.setStub(new AppletStub() {

            @Override
            public void appletResize(int arg0, int arg1) {
            }

            @Override
            public AppletContext getAppletContext() {
                return null;
            }

            @Override
            public URL getCodeBase() {
                return null;
            }

            @Override
            public URL getDocumentBase() {
                return null;
            }

            @Override
            public String getParameter(String arg0) {
                return null;
            }

            @Override
            public boolean isActive() {
                return false;
            }
        });
        AppletUtil au = new AppletUtil(dwo);
        DwoHelper.setAu(au);
        DwoHelper.setApplet(dwo);
        dwo.setSize(GuiConstants.DWO_WIDTH, GuiConstants.DWO_HEIGHT);
        new GuiCreator(dwo);
        User user = PersistenceFacade.instance().login("peterb");
        DwoProfile p = new DwoProfile();
        p.setID(1);
        ExportImportDialog dialog = new ExportImportDialog(null, user, p);
        dialog.setVisible(true);
        System.exit(0);
    }

    @Override
    public ResultsModuleIF getUserResultsModule(Course course) {
        return null;
    }

    @Override
    public void hideClassList() {
    }

    @Override
    public void loadCenter(CenterSubPanel cp) {
    }

    @Override
    public void loadTotal(CenterSubPanel csp) {
        ScoPanel sp = (ScoPanel) csp;
        ScoDialog.showScoPreview(this, sp);
    }

    @Override
    public void showClassList() {
    }

    public void setMap(CourseMap map) {
        this.map = map instanceof Course ? (Course) map : null;
    }

}
