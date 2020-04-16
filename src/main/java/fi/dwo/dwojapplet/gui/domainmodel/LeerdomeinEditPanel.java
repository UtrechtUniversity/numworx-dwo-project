package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.DynamicMBean;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.JTree.DynamicUtilTreeNode;
import javax.swing.border.TitledBorder;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.BorderUIResource.TitledBorderUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import fi.beans.dwomaccess.JSONEncoder;
import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JCheckBox;
import fi.beans.numworxlf.JComboBox;
import fi.beans.numworxlf.JOptionPane;
import fi.beans.numworxlf.JScrollPane;
import fi.beans.numworxlf.JTextField;
import fi.beans.numworxlf.NumworxTextFieldUI;
import fi.beans.private_base64code.StringCodeObject;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.gui.domainmodel.ExportAction.ExportPanel;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdr;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdrCache;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdrEditPanel;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdrPanel;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;

@Deprecated
class LeerdomeinEditPanel extends JPanel implements ActionListener, ExportPanel {

  private static final Logger LOG = Logger.getLogger(LeerdomeinEditPanel.class.getName());
  private String BEWERKEN = TextMapper.getText("edit");
  private String OPSLAAN  = TextMapper.getText(TextMapper.GUIP_BTN_SAVE);
  public final  Opslaan OPSLAAN_ACTION = new Opslaan();
  static final String WISKOPDR_SIG = "H4sIAAAAAA";
  public class Opslaan extends AbstractAction implements TreeSelectionListener {
    
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

    public void opslaan() {
      TreePath path = tree.getSelectionPath();
      if (path == null) return;
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
      Object u = node.getUserObject();
      if (u instanceof Node) {
        Node n = (Node)u;
        n.setTitle(subtitle.getText());
        String description = text.getText();
        if (editorCB.isSelected() && wiskOpdrEditPanel != null) {
          description = wiskOpdrEditPanel.getText();
          WiskOpdrPanel panel = WiskOpdr.getWiskOpdrPanel(description);
          Dimension size = panel.getMaximumSize();
          Dimension tsize = text.getMaximumSize();
          size.width = text.getPreferredSize().width;
          panel.setMaximumSize(size);
          panel.setBackground(Color.white);
          container.removeAll();
          container.add(panel);
          n.setDescriptionAsJSON(toJSON(description));
        } else {
          n.setDescriptionAsJSON(null);
        }
        n.setDescription(description);
        if (n instanceof NodeLeaf) {
          NodeLeaf nn = (NodeLeaf)n;
          commitEdit(init);commitEdit(learn); commitEdit(slip);
          nn.setInit((Double) init.getValue());
          nn.setLearn((Double) learn.getValue());
          nn.setSlip((Double) slip.getValue());
        }
      } else 
        node.setUserObject(subtitle.getText());
      if (node == root) {
        title.setText(subtitle.getText());
      }
      model.nodeChanged(node);
      left();
    }

    private void commitEdit(JFormattedTextField field) {
      try {
        field.commitEdit();
      } catch (ParseException e) {}
    }

    void left() {
      putValue(NAME, BEWERKEN);
      subtitle.setEditable(false); text.setEditable(false);
      slip.setEditable(false); init.setEditable(false); learn.setEditable(false);
      tree.setEnabled(true);language.setEnabled(true);title.setEnabled(true);
      editorCB.setEnabled(false);
    }

    public void bewerken() {
      TreePath path = tree.getSelectionPath();
      if (path == null) return;
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
      Object u = node.getUserObject();
      subtitle.setText(u.toString());
      subtitle.setEditable(true);text.setEditable(true);
      slip.setEditable(true); learn.setEditable(true); init.setEditable(true);
      tree.setEnabled(false);language.setEnabled(false);
      editorCB.setEnabled(true);
      setDescription(u);
      if (node == root) {
        title.setEnabled(false);
      }
      putValue(NAME, OPSLAAN);
    }

    void setDescription(Object u) {
      if (u instanceof Node) {
        String description = ((Node) u).getDescription();
        if (description == null || description.startsWith(WISKOPDR_SIG)||description.isEmpty()) {
          if (text.isEditable()) {
            Locale locale = Locale.forLanguageTag(getLanguage());
            wiskOpdrEditPanel = WiskOpdr.getWiskOpdrEditPanel(description, locale, 500 , 325, 425, 300);
            wiskOpdrEditPanel.setBackground(Color.WHITE);
            container.removeAll();
            container.add(wiskOpdrEditPanel);
            wiskOpdrEditPanel.setRequestFocusEnabled(true);
            wiskOpdrEditPanel.setFocusable(true);
            wiskOpdrEditPanel.requestFocusInWindow();
          } else {
            WiskOpdrPanel panel = WiskOpdr.getWiskOpdrPanel(description);
            Dimension size = panel.getMaximumSize();
            Dimension tsize = text.getMaximumSize();
            size.width = text.getPreferredSize().width;
            panel.setMaximumSize(size);
            panel.setBackground(Color.WHITE);
            container.removeAll();
            container.add(panel);
          }
          editorCB.setSelected(true);
        } else {
          text.setText(description);
          wiskOpdrEditPanel = null;
          container.removeAll();
          container.add(pane);
          editorCB.setSelected(false);
        }
      } else {
        text.setText("");
        wiskOpdrEditPanel = null;
        container.removeAll();
        container.add(pane);
        editorCB.setSelected(false);
      }
    }

    void fillSelection() {
      TreePath path = tree.getSelectionPath();
      if (path == null) {
        subtitle.setText("");
        setDescription("");
        settings.setVisible(false);
        return;
      }
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
      Object u = node.getUserObject();
      subtitle.setText(u.toString()); 
      setDescription(u);
      if (u instanceof NodeLeaf) {
        DomStudentModelContextInfo info = ((NodeLeaf) u).getInfo();
        Double d = info.getSlip(); if (d == null) d = 0.05; // DEFAULT SLIP
        slip.setValue(d);
        d = info.getInit(); if (d == null) d = 0.5; // DEFAULT INIT;
        init.setValue(d);
        d = info.getLearn(); if (d == null) d = 0.2; // DEFAULT LEARN;
        learn.setValue(d);
        settings.setVisible(true);
      } else {
        settings.setVisible(false);
      }
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
      this(TextMapper.getText("delete"));
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
      this(TextMapper.getText("rename"));
    }

    public Wijzigen(String name) {
      super(name);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
      OPSLAAN_ACTION.bewerken();
    }

  }

  class VoorkennisAction extends AbstractAction {

    private VoorkennisAction() {
      super(TextMapper.getText("voorkennis"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      TreePath path = tree.getSelectionPath();
      if (path == null) return;
      
      Object node = path.getLastPathComponent();
      if (node instanceof MutableTreeNode) {
        DefaultMutableTreeNode mutable = (DefaultMutableTreeNode) node;
        Object o = mutable.getUserObject();
        if (o instanceof NodeLeaf) {
          NodeLeaf leaf = (NodeLeaf) o;
          List<String> ids = leaf.getVoorkennis();
          if (ids == null) ids = Collections.emptyList();
          NodeVector v = (NodeVector) root.getUserObject();
          StudentModelChoicePanel panel = new StudentModelChoicePanel(v);
          panel.setObjectives(ids);
          int r = JOptionPane.showConfirmDialog(LeerdomeinEditPanel.this, panel, e.getActionCommand(), JOptionPane.OK_CANCEL_OPTION);
          if (r == JOptionPane.OK_OPTION) {
            panel.makeChoices();
            List<String> list = panel.getObjectives();
            leaf.setVoorkennis(list);
          }
        }}
    }
    
  }
  
  DefaultMutableTreeNode clipboard;
  
  class Knippen extends AbstractAction {
    Knippen() {
      super(TextMapper.getText("cut"));
    }
    
    @Override
    public void actionPerformed(ActionEvent arg0) {
      TreePath path = tree.getSelectionPath();
      if (path == null) return;
      
      Object node = path.getLastPathComponent();
      if (node == root) return;
      if (node instanceof MutableTreeNode) {
        DefaultMutableTreeNode mutable = (DefaultMutableTreeNode) node;
        TreeNode parent = mutable.getParent();
        mutable.removeFromParent();
        clipboard = mutable;
        model.nodeStructureChanged(parent);
        OPSLAAN_ACTION.fillSelection();
      
      }
    }
    
  }
  class Kopieren extends AbstractAction {
    Kopieren() {
      super(TextMapper.getText("copy"));
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      TreePath path = tree.getSelectionPath();
      if (path == null) return;
      
      Object node = path.getLastPathComponent();
      if (node == root) return;
      clipboard = copy(node);
    }
    
  }
  
  class Plakken extends AbstractAction {
    Plakken() {
      super(TextMapper.getText("paste"));
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (clipboard == null) return;
        TreePath path = tree.getSelectionPath();
        if (path == null) return;
        
        Object node = path.getLastPathComponent();
        if (node == root) {
          if (clipboard.isLeaf()) return;
          root.add(clipboard);
          model.nodeStructureChanged(root);
          tree.setSelectionPath(new TreePath(clipboard.getPath()));
          clipboard = copy(clipboard);
          tree.repaint();
          return;
        }
        if (node instanceof MutableTreeNode) {
          MutableTreeNode mutable = (MutableTreeNode) node;
          if (mutable.isLeaf()) {
            mutable = (MutableTreeNode) mutable.getParent();
          }
          ((DynamicUtilTreeNode) mutable).add(clipboard);
          model.nodeStructureChanged(mutable);
          tree.setSelectionPath(new TreePath(clipboard.getPath()));
          tree.repaint();
          clipboard = copy(clipboard);
          return;
        }
    }
    
  }
  
  class Omhoog extends AbstractAction {
    Omhoog() { super("Omhoog");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      TreePath path = tree.getSelectionPath();
      if (path == null) return;
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
      DynamicUtilTreeNode parent = (DynamicUtilTreeNode) node.getParent();
      int i = parent.getIndex(node);
      if (i > 0) {
        parent.remove(i);
        parent.insert(node, i-1);
        model.nodeStructureChanged(parent);
        tree.setSelectionPath(new TreePath(node.getPath()));
        tree.repaint();
      }
      
    }
  
  }
  
  class Omlaag extends AbstractAction {
    Omlaag() {super("Omlaag"); }

    @Override
    public void actionPerformed(ActionEvent e) {
      TreePath path = tree.getSelectionPath();
      if (path == null) return;
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
      DynamicUtilTreeNode parent = (DynamicUtilTreeNode) node.getParent();
      int i = parent.getIndex(node);
      if (i < parent.getChildCount()-1) {
        parent.remove(i);
        parent.insert(node, i+1);
        model.nodeStructureChanged(parent);
        tree.setSelectionPath(new TreePath(node.getPath()));
        tree.repaint();
      }
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
      //if ( node != root) return; // Only root can add subdomains
      
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
  
  public JTextField title, subtitle;
  JFormattedTextField slip, init, learn;
  JButton voorkennisBtn;
  public JComboBox<String>  language;
  JTree tree;
  DefaultTreeModel model;
  DynamicUtilTreeNode root;
  public JButton opslaan;
  JTextArea text;
  private WiskOpdrEditPanel wiskOpdrEditPanel;
  private JCheckBox editorCB;
  private Box bkt;
  public JMenuBar bar;
  private Box settings;
  
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
      Enumeration<TreeNode> children = root.breadthFirstEnumeration();
      while (children.hasMoreElements()) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) children.nextElement();
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
    bar = new JMenuBar();
    JMenu Bestand = new JMenu(TextMapper.getText("file"));
    JMenu Bewerken = new JMenu(TextMapper.getText("edit"));
    bar.add(Bestand);
      Bestand.add(new JMenuItem(new SubdomeinAction()));
      Bestand.add(new JMenuItem(new LeerdoelAction()));
      Bestand.addSeparator();
      Bestand.add(new JMenuItem(new ExportAction(this)));
    bar.add(Bewerken);
      Bewerken.add(new JMenuItem(new Knippen()));
      Bewerken.add(new JMenuItem(new Kopieren()));
      Bewerken.add(new JMenuItem(new Plakken()));
      Bewerken.add(new JMenuItem(new Wijzigen()));
      Bewerken.add(new JMenuItem(new Omhoog()));
      Bewerken.add(new JMenuItem(new Omlaag()));
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
    subtitle = new JTextField();
    opslaan = new JButton(OPSLAAN_ACTION);
    text = new JTextArea(10,30);
    text.setLineWrap(true);
    text.setWrapStyleWord(true);
    editorCB = new JCheckBox("Editor");editorCB.setSelected(true);
    //editorCB.addActionListener(this);
    wiskOpdrEditPanel = null;
    
    Box leftBox = Box.createVerticalBox(); 
    leftBox.add(titlebox);
    //leftBox.add(Box.createVerticalStrut(10));
    leftBox.add(bar);
    leftBox.add(new JScrollPane(tree));
    leftBox.add(Box.createVerticalGlue());
    add(leftBox);
    add(Box.createHorizontalGlue());
    add(Box.createHorizontalStrut(20));
    Box rightBox = Box.createVerticalBox();
    
    Box hbox = Box.createHorizontalBox();
    hbox.add(subtitle);
    //hbox.add(Box.createHorizontalStrut(20));
    //hbox.add(editorCB);
    hbox.add(opslaan);
    max = hbox.getPreferredSize();
    max.width = Integer.MAX_VALUE;
    hbox.setMaximumSize(max);
    
    rightBox.add(hbox);
    //rightBox.add(Box.createVerticalStrut(10+bar.getPreferredSize().height));
    pane = new JScrollPane(text);
    pane.setBackground(Color.white);
    pane.getViewport().setBackground(Color.white);
    container = new JPanel(new GridLayout());
    container.add(pane);
    rightBox.add(container);
    JLabel l;
    settings = Box.createVerticalBox();
    settings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Constants.COLOR15), "instellingen", TitledBorder.CENTER, TitledBorder.CENTER, null, Constants.COLOR15));    
    
    bkt = Box.createHorizontalBox();
    JLabel parametersLabel = new JLabel("Knowledge tracing parameters:");
      parametersLabel.setForeground(Constants.COLOR15);
      bkt.add(parametersLabel);
      bkt.add(Box.createHorizontalGlue());
    bkt.add(l = new JLabel("Init "));
      l.setForeground(Constants.COLOR15);
      init = new JFormattedTextField(NumberFormat.getInstance());
      init.setUI(NumworxTextFieldUI.createUI(init));
      init.setPreferredSize(new Dimension(50,20));
      init.setMaximumSize(new Dimension(50,30));
      bkt.add(init);
    bkt.add(l=new JLabel(" Learn "));
    l.setForeground(Constants.COLOR15);
      learn = new JFormattedTextField(NumberFormat.getInstance());
      learn.setUI(NumworxTextFieldUI.createUI(learn));
      learn.setPreferredSize(new Dimension(50,20));
      learn.setMaximumSize(new Dimension(50,30));
      bkt.add(learn);
    bkt.add(l=new JLabel(" Slip "));
    l.setForeground(Constants.COLOR15);
      slip = new JFormattedTextField(NumberFormat.getInstance());
      slip.setUI(NumworxTextFieldUI.createUI(slip));
      slip.setColumns(6);
      slip.setPreferredSize(new Dimension(50,20));
      slip.setMaximumSize(new Dimension(50,30));
     bkt.add(slip);

    Dimension pref = bkt.getPreferredSize();
    bkt.setMinimumSize(pref);
    pref.width = Short.MAX_VALUE;
    bkt.setMaximumSize(pref);
    rightBox.add(Box.createVerticalStrut(10));
    if (DwoHelper.isTest())
    {
     voorkennisBtn = new JButton(new VoorkennisAction()); settings.add(voorkennisBtn);
     voorkennisBtn.setAlignmentX(LEFT_ALIGNMENT);
     settings.add(Box.createVerticalStrut(10));
     bkt.setAlignmentX(LEFT_ALIGNMENT);
    }
    
    settings.add(bkt);
    rightBox.add(settings);
    add(rightBox);
        
    tree.addTreeSelectionListener(OPSLAAN_ACTION);
    
  }

  public DefaultMutableTreeNode copy(Object node) {
    if (node instanceof DefaultMutableTreeNode) {
      DefaultMutableTreeNode mutable = (DefaultMutableTreeNode) node;
      if (mutable.isLeaf()) {
        return new DefaultMutableTreeNode(new NodeLeaf((NodeLeaf)mutable.getUserObject()));
      } else {
        NodeVector v = new NodeVector( (NodeVector) mutable.getUserObject());
        DynamicUtilTreeNode copy = new DynamicUtilTreeNode(v, v);
        for(int i = 0; i < mutable.getChildCount(); i++) {
          copy.add(copy(mutable.getChildAt(i)));
        }
        return copy;
      }
    }
    return null;
  }

  DomStudentModelStructure structure;
  private JScrollPane pane;
  private Container container;
  public void setModel(DomStudentModelStructure model) {
    String locale = getLocale().getLanguage();
    if (model == null) {
      model = new DomStudentModelStructure();
      model.setInfo(new DomStudentModelContextInfo(new TreeMap<>(), new TreeMap<>()));
      model.getInfo().getTitle().put(locale, "Model");
      model.getInfo().getDescription().put(locale, "");
      
      model.setCategories(new ArrayList<>());
    }
    language.setSelectedItem(locale);
    NodeVector vector = new NodeVector(model.getCategories(), model.getInfo(), locale);
    this.model.setRoot(root = new DynamicUtilTreeNode(vector, vector));
    this.title.setText(vector.toString());
    this.subtitle.setText("");
    text.setEditable(false);
    OPSLAAN_ACTION.setDescription("");
    this.model.nodeStructureChanged(root);
    this.structure = model;
    settings.setVisible(false);
    OPSLAAN_ACTION.left();
  }

  private String getLanguage() {
    return language.getSelectedItem().toString();
  }

  private String toJSON(String string) {
    StringWriter writer = new StringWriter();
    try {
      Hashtable map = (Hashtable) StringCodeObject.decodeStringToObject(string, WiskOpdrCache.getInstance().getClassLoader());
      JSONEncoder.encode(map, writer, null);
    } catch (Exception e) {
      LOG.log(Level.WARNING, "toJSON", e);
    } 
    return writer.toString();

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
    Enumeration<TreeNode> children = root.children();
    while (children.hasMoreElements()) {
      DefaultMutableTreeNode object = (DefaultMutableTreeNode) children.nextElement();
      u = (Node) object.getUserObject();
      DomStudentModelCategory cat = new DomStudentModelCategory();
      cat.setInfo(u.getInfo());
      List<DomStudentModelObj> objectives = new ArrayList<>(object.getChildCount());
      cat.setObjectives(objectives );
      Enumeration<TreeNode> kids = object.children();
      while (kids.hasMoreElements()) {
        DefaultMutableTreeNode kid = (DefaultMutableTreeNode) kids.nextElement();
        u = (Node) kid.getUserObject();
        DomStudentModelObj objective = new DomStudentModelObj();
        objective.setInfo(u.getInfo());
        objectives.add(objective);
        if (!kid.isLeaf()) {
          setObjectiveChildren(objective, kid.getChildCount(), kid.children());
        }
      }
      categories.add(cat);
    }
    result.setCategories(categories);
    return result;
  }

  private void setObjectiveChildren(DomStudentModelObj node, int childCount,
      Enumeration<? extends TreeNode> children) {
    // TODO Auto-generated method stub
    System.out.println("add children");
    List<DomStudentModelObj> objectives = new ArrayList<>(childCount);
    node.setObjectives(objectives);
    while (children.hasMoreElements()) {
      DefaultMutableTreeNode kid =
          (DefaultMutableTreeNode) children.nextElement();
      Node u = (Node) kid.getUserObject();
      DomStudentModelObj objective = new DomStudentModelObj();
      objective.setInfo(u.getInfo());
      objectives.add(objective);
      if (!kid.isLeaf()) {
        setObjectiveChildren(objective, kid.getChildCount(), kid.children());
      }
      
    }
    
  }

  @Override
  public void actionPerformed(ActionEvent ev) {
    Object src = ev.getSource();
    if(src == editorCB)
    {   if(editorCB.isSelected())
        {   if(wiskOpdrEditPanel==null)
            {   
                Locale locale = Locale.forLanguageTag(getLanguage());
                wiskOpdrEditPanel = WiskOpdr.getWiskOpdrEditPanel("", locale, 500 , 325, 425, 300 );
                wiskOpdrEditPanel.setPreferredSize(new Dimension(400,350));
                wiskOpdrEditPanel.setBackground(Color.white);
            }
            container.removeAll();
            container.add(wiskOpdrEditPanel);
            wiskOpdrEditPanel.setRequestFocusEnabled(true);
            wiskOpdrEditPanel.setFocusable(true);
            wiskOpdrEditPanel.requestFocusInWindow();
        }
        else if(wiskOpdrEditPanel!=null)
        {   
            container.removeAll();
        	container.add(pane);
        }
    }  }

  @Override
  public Component asComponent() {
    return this;
  }  
  
}
