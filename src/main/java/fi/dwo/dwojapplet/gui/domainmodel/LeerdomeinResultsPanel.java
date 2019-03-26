package fi.dwo.dwojapplet.gui.domainmodel;

import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.JTree.DynamicUtilTreeNode;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;

public class LeerdomeinResultsPanel extends JPanel implements TreeSelectionListener {

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
  
  
  public LeerdomeinResultsPanel() {
    super(null);
    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
    
    klassen = new JComboBox<>(new SchoolKlas[] { new SchoolKlas(null) });
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
  
}
