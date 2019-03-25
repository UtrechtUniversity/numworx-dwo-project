package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JTree.DynamicUtilTreeNode;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;

public class LeerdomeinEditPanel extends JPanel {

  private String BEWERKEN = "Bewerken";
  private String OPSLAAN  = "Opslaan";
  private Opslaan OPSLAAN_ACTION = new Opslaan();

  class Opslaan extends AbstractAction implements TreeSelectionListener {
    
    Opslaan() {
      this(BEWERKEN);
    }

    Opslaan(String name) {
      super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (getValue(NAME) == BEWERKEN) {
        bewerken();
      } else 
      if (getValue(NAME) == OPSLAAN) {
        opslaan();
      }
    }

    void opslaan() {
      TreePath path = tree.getSelectionPath();
      if (path == null) return;
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
      Object u = node.getUserObject();
      if (u instanceof Node) {
        Node n = (Node)u;
        n.setTitle(subtitle.getText());
        n.setDescription(text.getText());
      } else 
        node.setUserObject(subtitle.getText());
      if (node == root) {
        title.setText(subtitle.getText());
      }
      model.nodeChanged(node);
      left();
    }

    void left() {
      putValue(NAME, BEWERKEN);
      subtitle.setEditable(false); text.setEditable(false);
      tree.setEnabled(true);language.setEnabled(true);title.setEnabled(true);
    }

    void bewerken() {
      TreePath path = tree.getSelectionPath();
      if (path == null) return;
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
      Object u = node.getUserObject();
      subtitle.setText(u.toString());
      if (u instanceof Node) {
        text.setText(((Node) u).getDescription());
      } else {
        text.setText("");
      }
      subtitle.setEditable(true);text.setEditable(true);
      tree.setEnabled(false);language.setEnabled(false);
      if (node == root) {
        title.setEnabled(false);
      }
      putValue(NAME, OPSLAAN);
    }

    void fillSelection() {
      TreePath path = tree.getSelectionPath();
      if (path == null) {
        subtitle.setText("");
        text.setText("");
        return;
      }
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
      Object u = node.getUserObject();
      subtitle.setText(u.toString());
      if (u instanceof Node) {
        text.setText(((Node) u).getDescription());
      } else 
        text.setText("");
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
      System.out.println(e);
      System.out.println(tree.getSelectionPath());
      if (e.isAddedPath()) {
        System.out.println("ADDED");
        fillSelection();
      }
    }
  }

  class Verwijderen extends AbstractAction {

    Verwijderen() {
      this("verwijderen");
    }

    Verwijderen(String name) {
      super(name);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
      TreePath path = tree.getSelectionPath();
      if (path == null) return;
      
      Object node = path.getLastPathComponent();
      if (node == root) return;
      if (node instanceof MutableTreeNode) {
        DefaultMutableTreeNode mutable = (DefaultMutableTreeNode) node;
        TreeNode parent = mutable.getParent();
        mutable.removeFromParent();
        model.nodeStructureChanged(parent);
        OPSLAAN_ACTION.fillSelection();
      }
    }

  }

  class Wijzigen extends AbstractAction {

    public Wijzigen() {
      this("wijzigen");
    }

    public Wijzigen(String name) {
      super(name);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
      OPSLAAN_ACTION.bewerken();
    }

  }

  class LeerdoelAction extends AbstractAction {

    public LeerdoelAction() {
      this("nieuw leerdoel");
    }

    public LeerdoelAction(String name) {
      super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      TreePath path = tree.getSelectionPath();
      if (path == null) return;
      
      Object node = path.getLastPathComponent();
      if (root == node) return;
      
      if (node instanceof MutableTreeNode) {
        MutableTreeNode mutable = (MutableTreeNode) node;
        if (mutable.isLeaf()) return;
        int index = mutable.getChildCount();
        Node leaf = new NodeLeaf(getLanguage());
        leaf.setTitle("Leerdoel-" + (index+1));
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(leaf, false);
        mutable.insert(child, index);
        model.nodesWereInserted(mutable, new int[] {index});
        tree.setSelectionPath(new TreePath(child.getPath()));
        OPSLAAN_ACTION.bewerken();
        subtitle.requestFocusInWindow();
        subtitle.selectAll();
      }
    }

  }

  public class SubdomeinAction extends AbstractAction {

    public SubdomeinAction() {
      this("nieuw subdomein");
    }

    public SubdomeinAction(String name) {
      super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      TreePath path = tree.getSelectionPath();
      if (path == null) return;
      
      Object node = path.getLastPathComponent();
      if ( node != root) return; // Only root can add subdomains
      
      if (node instanceof MutableTreeNode) {
        MutableTreeNode mutable = (MutableTreeNode) node;
        if (mutable.isLeaf()) return;
        int index = mutable.getChildCount();
        Node vector = new NodeVector(getLanguage());
        vector.setTitle("Untitled-" + (index+1));
        DefaultMutableTreeNode child = new DynamicUtilTreeNode(vector,vector);
        mutable.insert(child, index);
        model.nodesWereInserted(mutable, new int[] {index});
        tree.setSelectionPath(new TreePath(child.getPath()));
        OPSLAAN_ACTION.bewerken();
        subtitle.requestFocusInWindow();
        subtitle.selectAll();
     }
    }

  }

  static final String[] LANGUAGES = { "nl", "de", "en", "fr" };
  
  JTextField title, subtitle;
  JComboBox<String>  language;
  JTree tree;
  DefaultTreeModel model;
  DynamicUtilTreeNode root;
  JButton opslaan;
  JTextArea text;
  
  public LeerdomeinEditPanel() {
    super(null);
    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    title = new JTextField("Leerdomein",30);
    title.addActionListener(ev -> {
      String text = title.getText();
      Node n = (Node) root.getUserObject();
      n.setTitle(text);
      model.nodeChanged(root);
    });
    language = new JComboBox<>(LANGUAGES);
    language.setEditable(true);
    language.addActionListener(ev -> {
      @SuppressWarnings("unchecked")
      Enumeration<DefaultMutableTreeNode> children = root.breadthFirstEnumeration();
      while (children.hasMoreElements()) {
        DefaultMutableTreeNode node = children.nextElement();
        ((Node) node.getUserObject()).setLanguage(getLanguage());
      }
      title.setText(root.getUserObject().toString());
      model.nodeStructureChanged(root);
    });
    String locale = JComponent.getDefaultLocale().getLanguage();
    NodeVector v = new NodeVector(locale);
    v.setTitle("Leerdomein");
    root = new DynamicUtilTreeNode(v,v);
    model = new DefaultTreeModel(root);   
    tree = new JTree(model);
    language.setSelectedItem(locale); 
 // Menu
    JMenuBar bar = new JMenuBar();
    JMenu Bestand = new JMenu("Bestand");
    JMenu Bewerken = new JMenu("Bewerken");
    bar.add(Bestand);
      Bestand.add(new JMenuItem(new SubdomeinAction()));
      Bestand.add(new JMenuItem(new LeerdoelAction()));
    bar.add(Bewerken);
      Bewerken.add(new JMenuItem(new Wijzigen()));
      Bewerken.add(new JMenuItem(new Verwijderen()));
    
    bar.add(Box.createHorizontalGlue());
 // insert into panel
    Box titlebox = Box.createHorizontalBox();
    titlebox.add(title);
    titlebox.add(Box.createHorizontalGlue());
    titlebox.add(language);
    Dimension max = titlebox.getPreferredSize();
    max.width = Integer.MAX_VALUE;
    titlebox.setMaximumSize(max);
// Right
    subtitle = new JTextField(30);
    opslaan = new JButton(OPSLAAN_ACTION);
    text = new JTextArea(10,30);
    text.setLineWrap(true);
    text.setWrapStyleWord(true);
    
    Box leftBox = Box.createVerticalBox();
    
    leftBox.add(titlebox);
    leftBox.add(Box.createVerticalStrut(10));
    leftBox.add(bar);
    leftBox.add(new JScrollPane(tree));
    leftBox.add(Box.createVerticalGlue());
    add(leftBox);
    add(Box.createHorizontalGlue());
    add(Box.createHorizontalStrut(20));
    Box rightBox = Box.createVerticalBox();
    
    Box hbox = Box.createHorizontalBox();
    hbox.add(subtitle);
    hbox.add(Box.createHorizontalStrut(20));
    hbox.add(opslaan);
    max = hbox.getPreferredSize();
    max.width = Integer.MAX_VALUE;
    hbox.setMaximumSize(max);
    
    rightBox.add(hbox);
    rightBox.add(Box.createVerticalStrut(10+bar.getPreferredSize().height));
    rightBox.add(new JScrollPane(text));
    add(rightBox);
        
    tree.addTreeSelectionListener(OPSLAAN_ACTION);
    
  }

  DomStudentModelStructure structure;
  public void setModel(DomStudentModelStructure model) {
    if (model == null) {
      model = new DomStudentModelStructure();
      model.setInfo(new DomStudentModelContextInfo(new TreeMap<>(), new TreeMap<>()));
      model.setCategories(new ArrayList<>());
    }
    String locale = getLocale().getLanguage();
    language.setSelectedItem(locale);
    NodeVector vector = new NodeVector(model.getCategories(), model.getInfo(), locale);
    this.model.setRoot(root = new DynamicUtilTreeNode(vector, vector));
    this.title.setText(vector.toString());
    this.subtitle.setText("");
    this.text.setText("");
    this.model.nodeStructureChanged(root);
    this.structure = model;
    OPSLAAN_ACTION.left();
  }

  private String getLanguage() {
    return language.getSelectedItem().toString();
  }

  public void setEditable(boolean b) {
  }

  @SuppressWarnings("unchecked")
  public DomStudentModelStructure getModel() {
    DomStudentModelStructure result = new DomStudentModelStructure();
    Node u;
    u = (Node) root.getUserObject();
    result.setInfo(u.getInfo());
    List<DomStudentModelCategory> categories = new ArrayList<>(root.getChildCount());
    result.setCategories(categories);
    Enumeration<DefaultMutableTreeNode> children = root.children();
    while (children.hasMoreElements()) {
      DefaultMutableTreeNode object = children.nextElement();
      u = (Node) object.getUserObject();
      DomStudentModelCategory cat = new DomStudentModelCategory();
      cat.setInfo(u.getInfo());
      List<DomStudentModelObj> objectives = new ArrayList<>(object.getChildCount());
      cat.setObjectives(objectives );
      Enumeration<DefaultMutableTreeNode> kids = object.children();
      while (kids.hasMoreElements()) {
        DefaultMutableTreeNode kid = kids.nextElement();
        u = (Node) kid.getUserObject();
        DomStudentModelObj objective = new DomStudentModelObj();
        objective.setInfo(u.getInfo());
        objectives.add(objective);
      }
      categories.add(cat);
    }
    result.setCategories(categories);
    return result;
  }
  
  
}
