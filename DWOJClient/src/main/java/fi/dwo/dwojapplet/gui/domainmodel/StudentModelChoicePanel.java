package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.JTree.DynamicUtilTreeNode;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JCheckBox;
import fi.beans.numworxlf.JRadioButton;
import fi.beans.numworxlf.JScrollPane;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdr;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdrPanel;

public class StudentModelChoicePanel extends JSplitPane implements TreeSelectionListener {
  public static class LeafNodeEditor extends AbstractCellEditor implements TreeCellEditor {

    private static final int XWIDTH = 20; // positie [x]
    private final ChoiceCellRenderer renderer;
    private final Collection<String> enabled;
    private final Consumer<TreePath> savePath;
    private  ChangeEvent changeEvent = null;
    private  JTree tree;
    private  NodeLeaf leaf;
    private boolean readonly;
 
    public LeafNodeEditor(JTree tree, boolean b, ChoiceCellRenderer renderer, Consumer<TreePath> savePath) {
       this.renderer = renderer;
       this.enabled = renderer.enabled;
       this.savePath = savePath;
       this.tree = tree;
       this.readonly = b;
   }

     private void savePath(TreePath p) {
    	 savePath.accept(p);
     }
    
     public Object getCellEditorValue() {
         JCheckBox checkbox = renderer.getLeafRenderer();
         leaf.setValue(checkbox.isSelected());
         return leaf;
     }

     @Override
     public boolean isCellEditable(EventObject event) {
         boolean returnValue = false;
         if (!readonly && event instanceof MouseEvent) {
             MouseEvent mouseEvent = (MouseEvent) event;
             TreePath path = tree.getPathForLocation(mouseEvent.getX(),
                     mouseEvent.getY());
             if (path != null) {
                 Rectangle rect = tree.getPathBounds(path);
                 int pos = mouseEvent.getX() - rect.x;
                if (pos > XWIDTH) return false;
                 Object node = path.getLastPathComponent();
                 if ((node != null) && (node instanceof DefaultMutableTreeNode)) {
                     DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
                     Object userObject = treeNode.getUserObject();
                     returnValue = ((treeNode.isLeaf()) && (userObject instanceof NodeLeaf));
                     if (returnValue && !enabled.isEmpty()) {
                    	 returnValue &= enabled.contains(((NodeLeaf) userObject).getInfo().getId());
                     }
                 }
             }
         }
         return returnValue;
     }

     public Component getTreeCellEditorComponent(JTree tree, Object value,
             boolean selected, boolean expanded, boolean leaf, int row) {
         Component editor = renderer.getTreeCellRendererComponent(tree, value,
                 true, expanded, leaf, row, true);
         // editor always selected / focused
         ItemListener itemListener = new ItemListener() {

             public void itemStateChanged(ItemEvent itemEvent) {
                 if (stopCellEditing()) {
                     fireEditingStopped();
                     //model.nodeStructureChanged(root);
                     TreePath path = tree.getSelectionPath();
                     if (path != null && path.getLastPathComponent() == value) {
                       savePath(path);
                     }
                     
                     tree.repaint();
                 }
             }  
         };
         if (editor instanceof JCheckBox) {
             ((JCheckBox) editor).addItemListener(itemListener);
         }
         this.leaf = (NodeLeaf) ((DefaultMutableTreeNode) value).getUserObject();
         return editor;
     }
 }

  public  static class ChoiceCellRenderer implements TreeCellRenderer {

    private JCheckBox    leafRenderer = new JCheckBox();
    private JRadioButton nonLeafRenderer = new JRadioButton();
    private final Collection<String> enabled;
    private Color selectionBorderColor, selectionForeground, selectionBackground,
            textForeground, textBackground;

    protected JCheckBox getLeafRenderer() {
        return leafRenderer;
    }

    public ChoiceCellRenderer(Collection<String> enabled) {
    	this.enabled = enabled;
        Font fontValue;
        fontValue = UIManager.getFont("Tree.font");
        if (fontValue != null) {
            leafRenderer.setFont(fontValue);
            nonLeafRenderer.setFont(fontValue);
        }
        Boolean booleanValue = (Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon");
        leafRenderer.setFocusPainted((booleanValue != null) && (booleanValue.booleanValue()));
        nonLeafRenderer.setFocusPainted((booleanValue != null) && (booleanValue.booleanValue()));
        selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
        selectionForeground = UIManager.getColor("Tree.selectionForeground");
        selectionBackground = UIManager.getColor("Tree.selectionBackground");
        textForeground = UIManager.getColor("Tree.textForeground");
        textBackground = UIManager.getColor("Tree.textBackground");
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean selected, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        JToggleButton returnValue;
        if (leaf) {
          returnValue = leafRenderer;
        } else
          returnValue = nonLeafRenderer;
    
          String stringValue = tree.convertValueToText(value, selected,
            expanded, leaf, row, false);
          returnValue.setText(stringValue);
          returnValue.setSelected(false);
          returnValue.setEnabled(tree.isEnabled());
          if (selected) {
            returnValue.setForeground(selectionForeground);
            returnValue.setBackground(selectionBackground);
          } else {
            returnValue.setForeground(textForeground);
            returnValue.setBackground(textBackground);
          }
          if (value instanceof DefaultMutableTreeNode) {
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            if (userObject instanceof Node) {
              Node node = (Node) userObject;
              String text = node.toString();
              String id = node.getInfo().getId();
              if (!enabled.isEmpty() && !enabled.contains(id)) returnValue.setEnabled(false);
//              Double factor = ids.get(id);
//              if (factor == null) factor = 1.0;
//              if (node.isValue() && node instanceof NodeLeaf && factor.doubleValue() <= 0.999)
//                text +=  " " + factor;
              returnValue.setText(text);
              returnValue.setSelected(node.isValue());
            }
          }
        return returnValue;
    }

  }

  JTree tree;
  DefaultTreeModel model;
  DynamicUtilTreeNode root;
  JLabel title;
  JTextArea description;
  final NodeVector studentModel;
  static final String WISKOPDR_SIG = "H4sIAAAAAA";

  public StudentModelChoicePanel(NodeVector studentModel) {
    this(studentModel, false);
  }
  
  private final Collection<String> enabled;
  
  public StudentModelChoicePanel(NodeVector studentModel, boolean readonly) {
	  this(studentModel, readonly, Collections.emptySet());
	  
  }
  
  public StudentModelChoicePanel(NodeVector studentModel, boolean readonly, Collection<String> enabled) {
    super();
    this.enabled = enabled;
    this.studentModel = studentModel;
//    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    NodeVector v = (studentModel);
    root = new DynamicUtilTreeNode(v, v);
    model = new DefaultTreeModel(root);   
    tree = new JTree(model);
    //tree.setMinimumSize(new Dimension(200,100));
    //tree.setPreferredSize(tree.getMinimumSize());
    tree.setCellEditor(new LeafNodeEditor(tree, readonly, new ChoiceCellRenderer(enabled), this::savePath));
    tree.setEditable(!readonly);
    tree.setCellRenderer(new ChoiceCellRenderer(enabled));
    Box leftBox = Box.createVerticalBox();
    setLeftComponent(leftBox);
    leftBox.add(new JLabel(v.toString()));
    leftBox.add(Box.createVerticalStrut(10));
    JScrollPane sp = new JScrollPane(tree);
    leftBox.add(sp);
    sp.setMinimumSize(new Dimension(300,100));
    sp.setPreferredSize(sp.getMinimumSize());

    //add(Box.createHorizontalStrut(10));
    Box rightBox = Box.createVerticalBox();
    setRightComponent(rightBox);
    
    title = new JLabel(v.toString());
    String descr = v.getDescription();
    description = new JTextArea(descr, 10, 30);
    description.setLineWrap(true);
    description.setWrapStyleWord(true);
    description.setEditable(false);
    scroll = new JScrollPane(description);
    if (descr != null && descr.startsWith(WISKOPDR_SIG))
    {
      WiskOpdrPanel panel = getWiskOpdrPanel(descr);
      scroll.setViewportView(panel);
    }
    slider = new JSlider(1, 10, 10);            slider.setVisible(false);
    slider.setToolTipText("factor");
    slider.setEnabled(!readonly);
    slider.setMajorTickSpacing(3);
    Hashtable<Number, JLabel> dict = new Hashtable<>();
    dict.put(slider.getMinimum(), new JLabel("min"));
    dict.put(slider.getMaximum(), new JLabel("max"));
    slider.setLabelTable(dict);
    slider.setPaintLabels(true);
    slider.setPaintTicks(true);
    
    rightBox.add(title);
    rightBox.add(Box.createVerticalStrut(10));
    rightBox.add(scroll);
    rightBox.add(slider);
    
    tree.addTreeSelectionListener(this);
    
    Dimension dim = getPreferredSize();
    dim.height = Math.max(dim.height, 450);
    setSize(dim);
    setPreferredSize(dim);
    setMinimumSize(dim);
    BasicSplitPaneUI sui = (BasicSplitPaneUI) BasicSplitPaneUI.createUI(this);
    this.setUI(sui);
    BasicSplitPaneDivider divider = sui.getDivider();
    divider.setBorder(BorderFactory.createEmptyBorder());
    divider.setBackground(Constants.COLOR20);
    this.setDividerSize(10);
    this.setResizeWeight(0.5);

  
  }

  private boolean[][] choices;
  final private Map<String, Double> ids = new TreeMap<>();
  private JScrollPane scroll;
  private JSlider slider;
  
  public List<String> getObjectives() {
//    return ids.entrySet().stream()
//          .map(e -> e.getKey() + (e.getValue() != null ? ("/" + e.getValue()): ""))
//          .collect(Collectors.toList());
	  return new ArrayList<String> (ids.keySet()); // no more /value
  }
  
  private void getObjectives(Object v, Map<String,Double> ids) {
    if (v instanceof NodeLeaf) {
      NodeLeaf leaf = (NodeLeaf) v;
      if (!leaf.isValue())
        ids.remove(leaf.getInfo().getId());
    } else if (v instanceof NodeVector) {
      NodeVector vector = (NodeVector) v;
      ids.remove(vector.getInfo().getId());
      vector.forEach(item -> getObjectives(item, ids));
    }
  }

  public boolean[][] getChoices() {
    return choices;
  }
  
  public void setObjectives(List<String> objectives) {
    ids.clear();
    objectives.forEach(s -> {
      String[] split = s.split("/");
      ids.put(split[0], split.length>1 ? Double.valueOf(split[1]): null);
    } );
    makeGUI();
    
  }
  
  
  public void makeChoices() {
// new style
    TreePath p = tree.getSelectionPath();
    if (p != null) savePath(p);
    getObjectives(root.getUserObject(), ids);    
  }

  public Component makeGUI() {
 // new style 
    if (ids != null) {
      @SuppressWarnings("unchecked")
      Enumeration<TreeNode> all = root.depthFirstEnumeration();
      while (all.hasMoreElements()) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) all.nextElement();
        Object u = node.getUserObject();
        if (u instanceof NodeLeaf) {
          NodeLeaf leaf = (NodeLeaf) u;
          leaf.setValue(ids.containsKey(leaf.getInfo().getId()));
        }
      }
    }
    model.nodeStructureChanged(root);

    @SuppressWarnings("unchecked")
    Enumeration<DefaultMutableTreeNode> all = (Enumeration) root.depthFirstEnumeration();
    while (all.hasMoreElements()) {
      DefaultMutableTreeNode node = all.nextElement();
      Object u = node.getUserObject();
      if (u instanceof NodeLeaf) {
        NodeLeaf leaf = (NodeLeaf) u;
        if (leaf.isValue()) {
          tree.makeVisible(new TreePath(node.getPath()));
        }
      }
    }
    return this;
  }

  @Override
  public void valueChanged(TreeSelectionEvent e) {
    TreePath[] paths = e.getPaths();
    for( TreePath p: paths) {
      if (! e.isAddedPath(p)) {
        savePath(p);        
      }
    }
    if (e.isAddedPath()) {
      TreePath path = tree.getSelectionPath();
      if (path == null) {
        title.setText("");
        description.setText("");
        scroll.setViewportView(description);
        return;
      }
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
      Object u = node.getUserObject();
      title.setText(u.toString());
      if (u instanceof Node) {
        String descr = ((Node) u).getDescription();
        Double factor = ids.get(((Node) u).getInfo().getId());
        if (factor == null) factor = 1.0; 
        slider.setValue(Math.round(slider.getMaximum() * factor.floatValue()));
        if (descr == null) descr = "";
        if (descr.startsWith(WISKOPDR_SIG)) {
          WiskOpdrPanel panel = getWiskOpdrPanel(descr);
          scroll.setViewportView(panel);
        } else {
          description.setText(descr);
          scroll.setViewportView(description);
        }
      } else {
        description.setText("");
        scroll.setViewportView(description);
      }
    }
    repaint();
  }

  private void savePath(TreePath p) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) p.getLastPathComponent();
    Object u = node.getUserObject();
    if (u instanceof Node) {
      ids.put(((Node) u).getInfo().getId(), (double)slider.getValue()/slider.getMaximum());
      model.nodeChanged(node);
    }
  }

  private WiskOpdrPanel getWiskOpdrPanel(String descr) {
    try {
      WiskOpdrPanel panel = WiskOpdr.getWiskOpdrPanel(descr);
      panel.setBackground(Color.WHITE);
      return panel;
    } finally {
    }
  }
}
