package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.JTree.DynamicUtilTreeNode;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherStudentModelManager;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScorePerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class LeerdomeinResultsPanel extends JPanel implements TreeSelectionListener, ActionListener {

  private static final Logger LOG = Logger.getLogger(LeerdomeinResultsPanel.class.getName());
  private DomStudentModelContext context;
  
  class SchoolKlas {
    final DomSchoolClass delegate;

    public SchoolKlas(DomSchoolClass delegate) {
      super();
      this.delegate = delegate;
    }

    @Override
    public String toString() {
      if (delegate == null) {
        return "Kies klas";
      }
      return delegate.getSchoolClassName();
    }
    
  }
  

  JComboBox<SchoolKlas> klassen;
  
  JTree tree;

  private DefaultTreeModel model;
  private MutableTreeNode root;
  private JLabel title, title2;
  private JTextArea tekst;
  private JTable table;
  
  
  public LeerdomeinResultsPanel() {
    super(null);
    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
    
    klassen = new JComboBox<>(new SchoolKlas[] { new SchoolKlas(null) });
    klassen.addActionListener(this);
    root = new DefaultMutableTreeNode("root");
    model = new DefaultTreeModel(root);
    tree = new JTree(model);
    title = new JLabel();
    title2 = new JLabel();
    tekst = new JTextArea(20,20);
    
    Box vbox = Box.createVerticalBox();
    vbox.add(new JScrollPane(tree));
    vbox.add(title);
    vbox.add(new JScrollPane(tekst));
    
    add(vbox);
    vbox = Box.createVerticalBox();
    Box hbox = Box.createHorizontalBox();
    hbox.add(klassen);
    hbox.add(title2);
    
    vbox.add(hbox);
    
    table = new JTable();
    vbox.add(new JScrollPane(table));
    vbox.add(Box.createVerticalGlue());
    
    add(vbox);
    tree.addTreeSelectionListener(this);
    
    
    
  }

  public void setContext(DomStudentModelContext context) {
    this.context = context;
    DomStudentModelStructure model = context.getModelStructure();
    String locale = getLocale().getLanguage();
    NodeVector vector = new NodeVector(model.getCategories(), model.getInfo(), locale);
    this.model.setRoot(root = new DynamicUtilTreeNode(vector, vector));

    this.title.setText(vector.toString());
    this.title2.setText(vector.toString());
    this.tekst.setText(vector.getDescription());
    
    this.model.nodeStructureChanged(root);

  }
  
  public void setClasses(List<DomSchoolClass> list) {
    for(DomSchoolClass i : list) {
      klassen.addItem(new SchoolKlas(i));
    }
  }

  @Override
  public void valueChanged(TreeSelectionEvent e) {
    if (e.isAddedPath()) {
      TreePath path = tree.getSelectionPath();
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
      Node n = (Node)node.getUserObject();
      title.setText(n.toString());
      title2.setText(n.toString());
      tekst.setText(n.getDescription());
    }

    
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource()==klassen) {
      SchoolKlas klas = klassen.getItemAt(klassen.getSelectedIndex());
      if (klas != null) {
        DomSchoolClass dom = klas.delegate;
        DomStudentModelScorePerTeacher scores = new DomStudentModelScorePerTeacher();
        scores.setSchoolClasses(Collections.singletonList(new DomMapEntry<>(dom.getId(), dom)));
        scores.setStudentModelContexts(Collections.singletonList(new DomMapEntry<>(context.getId(), context)));
        try {
          scores = SecureTeacherStudentModelManager.getScores(scores);
          TableModel tmodel = new DefaultTableModel(scores.getStudents().size(), 2);
          for (int i = 0; i < tmodel.getRowCount(); i++ ) {
            String u = scores.getStudents().get(i).getValue().getDisplayName();
            tmodel.setValueAt(u, i, 0);
            DomStudentModelStructureScore v = scores.getStudentScores().get(i).getDomStudentModelStructureScore();
            String result = v.getCount() + " " + v.getScore();
            tmodel.setValueAt(result, i, 1);
          }
          table.setModel(tmodel);
          
        } catch (Dwo2Exception e1) {
          LOG.log(Level.SEVERE, "getScores", e1);
        }
      } else {
        table.setModel(new DefaultTableModel(0,2));
      }
    }
    
  }
  
}
