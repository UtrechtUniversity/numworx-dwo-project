package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.util.Collections;
import java.util.Enumeration;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
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
import fi.beans.numworxlf.JTree;
import fi.dwo.dwojapplet.gui.GuiConstants;
import fi.dwo.dwojapplet.gui.domainmodel.LeerdomeinEditPanel2.VoorkennisAction;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdr;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdrPanel;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;

public class StudentResultsPanel extends JPanel implements Constants, TreeSelectionListener {

  
  class ExtraCellRenderer extends TreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(javax.swing.JTree tree, Object value, boolean sel,
        boolean expanded, boolean leaf, int row, boolean hasFocus) {
      Component label = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
      DomStudentModelScore<?> s = null;
      Node n = null;
      if (value instanceof DefaultMutableTreeNode) {
        n = (Node) ((DefaultMutableTreeNode) value).getUserObject();
        s = map.get(n.getInfo());
      }
      Box hb = Box.createHorizontalBox();
      hb.add(label);
      hb.add(Box.createHorizontalStrut(20));
      hb.add(Box.createHorizontalGlue());
      FontMetrics fontMetrics = tree.getFontMetrics(tree.getFont());
      Icon icn;
      if (s != null) {
         icn = createIcon(n, s, fontMetrics);
      } else {
         icn = new ScoreIcon(ScoreIcon.UNSURE, ScoreIcon.UNSURE, fontMetrics);
      }
      hb.add(new JLabel(icn));
      return hb;
    }
    
  }
  
  
  
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

    TreeCellRenderer renderer = new ExtraCellRenderer();
    renderer.updateUI();
    tree.setCellRenderer(renderer);
    tree.updateUI();
    tree.setBackground(COLOR20);
    renderer.setBackgroundNonSelectionColor(COLOR20);
    
    leftBox = new JPanel(new BorderLayout());
    JScrollPane scrollpane = new JScrollPane(tree);
    scrollpane.setViewportBorder(BorderFactory.createEmptyBorder());
    scrollpane.setBorder(BorderFactory.createEmptyBorder());
    scrollpane.setBackground(COLOR20);
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
    
    score = new JLabel(new ScoreIcon(0, 0, 0, 0, red.getFontMetrics(red.getFont())));
    Box b = LeerdomeinResultsPanel2.hb(LeerdomeinResultsPanel2.hgl(),  red, score, green, LeerdomeinResultsPanel2.hgl());   
    JPanel p = new JPanel(new BorderLayout(0,5));
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
      
    NodeVector vector = new NodeVector(model.getCategories(), model.getInfo(), locale, false); // use original info for hashmap
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
      FontMetrics fontMetrics = score.getFontMetrics(score.getFont());
      PIcon icon;
      DomStudentModelScore<?> s = map.get(n.getInfo());
      icon = createIcon(n, s, fontMetrics);

      score.setIcon(icon);
      
      red.setText(icon.getRedPercentage());
      green.setText(icon.getGreenPercentage());

      subtitle.setText(n.toString());
      setDescription(n);     
      calculatePath(path);     
    }
  }

  private PIcon createIcon(Node n, DomStudentModelScore<?> s, FontMetrics fontMetrics) {
    PIcon icon;
    if(n instanceof NodeLeaf) {
      if (s.getCount() == 0) {
        icon = new ScoreIcon(ScoreIcon.UNSURE, ScoreIcon.UNSURE, fontMetrics);
      } else if (s.getScore() > 0.5) {
        icon = new ScoreIcon(s.getScore(), ScoreIcon.UNSURE, fontMetrics);
      } else {
        icon = new ScoreIcon(ScoreIcon.UNSURE, s.getScore(), fontMetrics);
      }
    } else {
      icon = new SummaryIcon(s, fontMetrics);        
    }
    return icon;
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
      model.setRoot(LeerdomeinEditPanel2.filter(root, filter));      
    }
    
  }


  private Map<Object, DomStudentModelScore<?>> map = Collections.emptyMap();
  
  public void setScore(DomStudentModelStructureScore v) {
    this.structureScore = v;
    v.recalculateAncestors();
    map = new IdentityHashMap<>();
    DomStudentModelStructure r = context.getModelStructure();
    map.put(r.getInfo(), v);
    int sizei = r.getCategories().size();
    for (int i = 0; i < sizei; i++) {
      DomStudentModelCategory c = r.getCategories().get(i);
      DomStudentModelCategoryScore s = v.getCategories().get(i);
      map.put(c.getInfo(), s);
      putObjectiveScore(c.getObjectives(), s.getObjectives());
    }
  }

  private void putObjectiveScore(List<DomStudentModelObj> list, List<DomStudentModelObjectiveScore> scores) {
    if (list == null) return;
    int sizej = list.size();
    for (int j = 0; j < sizej; j++) {
      DomStudentModelObj o = list.get(j);
      DomStudentModelObjectiveScore so = scores.get(j);
      map.put(o.getInfo(), so);
      putObjectiveScore(o.getObjectives(), so.getChildren());    
    }
  }

}
