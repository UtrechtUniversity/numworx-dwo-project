package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree.DynamicUtilTreeNode;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JScrollPane;
import fi.beans.numworxlf.JTextField;
import fi.beans.numworxlf.JTree;
import fi.dwo.dwojapplet.gui.GuiConstants;
import fi.dwo.dwojapplet.gui.domainmodel.LeerdomeinEditPanel2.VoorkennisAction;
import fi.dwo.dwojapplet.gui.domainmodel.LeerdomeinResultsPanel2.ScoreIcon;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdr;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdrPanel;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;

public class StudentResultsPanel extends JPanel implements Constants, TreeSelectionListener {

  private JLabel titleLabel;
  private Font font = GuiConstants.NORMAL_TEXT;
  private DynamicUtilTreeNode root;
  private InvisibleTreeModel model;
  private JTree tree;
  private JPanel leftBox;
  private Box leftSouth;
  private JPanel container;
  private JLabel subtitle;
  private Box settingsRO;
  private DomStudentModelContext context;
  private DomStudentModelStructureScore structureScore;
  private JLabel red, score, green;

  StudentResultsPanel(String student) {
    super(new BorderLayout());
    titleLabel = new JLabel("Resultaten " + student);
    add(titleLabel, BorderLayout.NORTH);
    titleLabel.setBackground(COLOR15);
    titleLabel.setForeground(COLOR20);
    titleLabel.setFont(font.deriveFont(24f));
    titleLabel.setOpaque(true);
    titleLabel.setHorizontalAlignment(JLabel.CENTER);
    titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

    JSplitPane split = new JSplitPane();
    BasicSplitPaneUI sui = (BasicSplitPaneUI) BasicSplitPaneUI.createUI(split);
    split.setUI(sui);
    BasicSplitPaneDivider divider = sui.getDivider();
    divider.setBorder(BorderFactory.createEmptyBorder());
    divider.setBackground(Constants.COLOR20);
    split.setDividerSize(20);
    split.setResizeWeight(0.8);
    split.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    split.setBackground(Constants.COLOR20);
    setBackground(Constants.COLOR20);
    add(split, BorderLayout.CENTER);
    add(split, BorderLayout.CENTER);
    String locale = JComponent.getDefaultLocale().getLanguage();
    NodeVector v = new NodeVector(locale);
    v.setTitle("Leerdomein");
    root = new DynamicUtilTreeNode(v,v);
    model = new InvisibleTreeModel(root);   
    tree = new JTree(model);

    TreeCellRenderer renderer = new TreeCellRenderer();
    renderer.updateUI();
    tree.setCellRenderer(renderer);
    tree.updateUI();
    
    leftBox = new JPanel(new BorderLayout());
    leftBox.setBorder(BorderFactory.createLineBorder(Constants.COLOR13));
    JScrollPane scrollpane = new JScrollPane(tree);
    scrollpane.setViewportBorder(BorderFactory.createEmptyBorder());
    scrollpane.setBorder(BorderFactory.createEmptyBorder());
    Dimension pref = scrollpane.getPreferredSize();
    pref.width = Math.max(580, pref.width); // 580 wide.
    scrollpane.setPreferredSize(pref);
    
    leftBox.add(scrollpane, BorderLayout.CENTER);
    leftSouth = Box.createHorizontalBox();
    JButton filter = new JButton(new FilterAction(this, this::filter));
    leftSouth.add(Box.createHorizontalGlue());
    leftSouth.add(filter);
    leftSouth.add(Box.createHorizontalGlue());
    leftSouth.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    leftBox.add(leftSouth, BorderLayout.SOUTH);
    split.setLeftComponent(leftBox);
    
    JPanel rightBox = new JPanel(new BorderLayout());
    subtitle = new JLabel();
    subtitle.setForeground(Color.WHITE);
    subtitle.setFont(font.deriveFont(Font.BOLD, 14f));
    subtitle.setBorder(BorderFactory.createEmptyBorder(4, 20, 4, 20));
    subtitle.setBackground(COLOR13);
    subtitle.setOpaque(true);
    container = new JPanel(new GridLayout(1,1));
    container.setPreferredSize(new Dimension(500 , 325));
    rightBox.add(subtitle, BorderLayout.NORTH);
    rightBox.add(container, BorderLayout.CENTER);

    settingsRO = Box.createHorizontalBox();
    settingsRO.setOpaque(true);
    settingsRO.setBackground(Constants.COLOR20);
    JButton voorkennisRO = new JButton(new VoorkennisAction(true,this,tree,root)); voorkennisRO.setFont(font);
    voorkennisRO.setPreferredSize(new Dimension(120,24));
    settingsRO.add(voorkennisRO);
    settingsRO.add(Box.createHorizontalGlue());
    JButton genrRO = new JButton(new GenRAction(true, this, tree)); genrRO.setFont(font);
    genrRO.setPreferredSize(new Dimension(140,24));
    settingsRO.add(genrRO);
    settingsRO.add(Box.createHorizontalStrut(10));
    JButton mwRO = new JButton(new MWAction(true, this, tree)); mwRO.setFont(font);
    mwRO.setPreferredSize(new Dimension(140,24));
    settingsRO.add(mwRO);
    settingsRO.setBorder(BorderFactory.createEmptyBorder(10,10,8,10));
  
    
    rightBox.add(settingsRO, BorderLayout.SOUTH);
    rightBox.setBorder(BorderFactory.createLineBorder(Constants.COLOR13));
    red = new JLabel("0%"); red.setForeground(LeerdomeinResultsPanel2.RED);
    green = new JLabel("0%"); green.setForeground(LeerdomeinResultsPanel2.GREEN);
    
    JComponent gemiddelde = score = new JLabel(new ScoreIcon(0, 0, 0, 0, red.getFontMetrics(red.getFont())));
    Box b = LeerdomeinResultsPanel2.hb(LeerdomeinResultsPanel2.ra(10,0),  red, gemiddelde, green, LeerdomeinResultsPanel2.hgl());   
    JPanel p = new JPanel(new BorderLayout());
    p.add(rightBox, BorderLayout.CENTER);
    p.add(b, BorderLayout.PAGE_START);
    
    split.setRightComponent(p);

    tree.addTreeSelectionListener(this);

  }

  void setContext(DomStudentModelContext context) {
    this.context = context;
    DomStudentModelStructure model = context.getModelStructure();
    String locale = getLocale().getLanguage();
    
    model = AdviseMeResultManager.restructure(model, locale, context);
      
    NodeVector vector = new NodeVector(model.getCategories(), model.getInfo(), locale);
    this.model.setRoot(root = new DynamicUtilTreeNode(vector, vector));

    this.subtitle.setText(vector.toString());
    //this.title2.setText(vector.toString());
    //this.tekst.setText(vector.getDescription());
    setDescription(vector);
    this.model.nodeStructureChanged(root);

  }

  void setDescription(Object u) {
    if (u instanceof Node) {
      String description = ((Node) u).getDescription();
      if (description == null || description.startsWith(LeerdomeinEditPanel2.WISKOPDR_SIG)||description.isEmpty()) {
         {
          WiskOpdrPanel panel = WiskOpdr.getWiskOpdrPanel(description);
          panel.setBackground(Color.WHITE);
          JScrollPane pane =  new JScrollPane(panel);
          pane.setBorder(BorderFactory.createEmptyBorder());
          pane.setViewportBorder(BorderFactory.createEmptyBorder());
          pane.setBackground(Color.WHITE);
          pane.getViewport().setBackground(Color.WHITE);
          container.removeAll();
          container.add(pane);
        }
      } else {
        container.removeAll();
      }
    } else {
      container.removeAll();
    }
  }

  
  
  @Override
  public void valueChanged(TreeSelectionEvent e) {
    if (e.isAddedPath()) {
      TreePath path = tree.getSelectionPath();
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
      Node n = (Node)node.getUserObject();
      subtitle.setText(n.toString());
      setDescription(n);     
      calculatePath(path);     
    }
  }

  private int[] getPath(TreePath path) {
    if (path == null) return null;
    Object[] o = path.getPath();
    int[] result = new int[o.length];
    for (int i = 0; i < result.length; i++) {
      result[i] = ((Node) ((DefaultMutableTreeNode) o[i]).getUserObject()).getPath();
    }
    return result;
  }

  private void calculatePath(TreePath path) {
    int[] ipath = getPath(path);
    if (ipath == null || ipath.length == 1) {
//      calculateROOT(scores, results.getModel());
    } else if (ipath.length == 2) {
//      calculateCategories(scores, results.getModel(), ipath[1]);
    } else if (ipath.length >= 3) {
//      calculateObjectives(scores, results.getModel(), ipath);
    }
  }

  public void filter(Map<String,Map<String,Set<Integer>>> filter) {
    if (filter.isEmpty()) {
      model.activateFilter(false);
      if (model.getRoot() != root) model.setRoot(root);
    } else {
      model.activateFilter(true);
      model.setRoot(filter(root, filter));      
    }
    
  }

  private DefaultMutableTreeNode filter(DefaultMutableTreeNode parent,
      Map<String, Map<String, Set<Integer>>> filter) {
    InvisibleNode node;
    if (!(parent instanceof InvisibleNode)) {
      node = new InvisibleNode(parent.getUserObject());
      node.setAllowsChildren(parent.getAllowsChildren());
      Enumeration<?> children = parent.children();
      while (children.hasMoreElements()) {
        DefaultMutableTreeNode object = (DefaultMutableTreeNode) children.nextElement();
        node.add(filter(object, filter));
      }
    } else {
      node = (InvisibleNode) parent;
    }
    if (node.isLeaf() && !node.getAllowsChildren()) {
      NodeLeaf leaf = (NodeLeaf) node.getUserObject();
      Map<String, Map<String, Set<Integer>>> methodes = leaf.getMethode();
      node.setVisible(contains(filter, methodes));
    } else {
      int cnt = node.getChildCount(true);
      node.setVisible(cnt != 0);
    }

    return node;
  }

  private boolean contains(Map<String, Map<String, Set<Integer>>> filter,
      Map<String, Map<String, Set<Integer>>> methodes) {
    for (Map.Entry<String, Map<String, Set<Integer>>> entry : filter.entrySet()) {
      Map<String, Set<Integer>> map = methodes.getOrDefault(entry.getKey(), Collections.emptyMap());
      if (map.isEmpty()) continue;
      for (Map.Entry<String, Set<Integer>> m : entry.getValue().entrySet()) {
        Set<Integer> chapters = new TreeSet<>(map.getOrDefault(m.getKey(), Collections.emptySet()));
        chapters.retainAll(m.getValue());
        if (!chapters.isEmpty()) return true;
      }
    }
    return false;
  }

  public void setScore(DomStudentModelStructureScore v) {
    this.structureScore = v;
  }

}
