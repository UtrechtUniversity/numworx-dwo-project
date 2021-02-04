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
import java.util.Enumeration;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
  private class LeafNodeEditor extends AbstractCellEditor implements TreeCellEditor {

    private static final int XWIDTH = 20; // positie [x]
    private  ChoiceCellRenderer renderer = new ChoiceCellRenderer();
    private  ChangeEvent changeEvent = null;
    private  JTree tree;
    private  NodeLeaf leaf;
    private boolean readonly;

     public LeafNodeEditor(JTree tree) {
         this.tree = tree;
     }
     public LeafNodeEditor(JTree tree, boolean b) {
       this.tree = tree;
       this.readonly = b;
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
                     repaint();
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

  public  class ChoiceCellRenderer implements TreeCellRenderer {

    private JCheckBox    leafRenderer = new JCheckBox();
    private JRadioButton nonLeafRenderer = new JRadioButton();
    private Color selectionBorderColor, selectionForeground, selectionBackground,
            textForeground, textBackground;

    protected JCheckBox getLeafRenderer() {
        return leafRenderer;
    }

    public ChoiceCellRenderer() {
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
          if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            if (userObject instanceof Node) {
              Node node = (Node) userObject;
              returnValue.setText(node.toString() + " " + ids.getOrDefault(node.getInfo().getId(), 1.0));
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
  
  public StudentModelChoicePanel(NodeVector studentModel, boolean readonly) {
    super();
    this.studentModel = studentModel;
//    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    NodeVector v = (studentModel);
    root = new DynamicUtilTreeNode(v, v);
    model = new DefaultTreeModel(root);   
    tree = new JTree(model);
    //tree.setMinimumSize(new Dimension(200,100));
    //tree.setPreferredSize(tree.getMinimumSize());
    tree.setCellEditor(new LeafNodeEditor(tree, readonly));
    tree.setEditable(!readonly);
    tree.setCellRenderer(new ChoiceCellRenderer());
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
    if (descr.startsWith(WISKOPDR_SIG))
    {
      WiskOpdrPanel panel = getWiskOpdrPanel(descr);
      scroll.setViewportView(panel);
    }
    slider = new JSlider(1, 10, 10);
    slider.setToolTipText("factor");
    slider.setEnabled(!readonly);
    
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
    return ids.entrySet().stream()
          .map(e -> e.getKey() + (e.getValue() != null ? ("/" + e.getValue()): ""))
          .collect(Collectors.toList());
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
        Double factor = ids.getOrDefault(((Node) u).getInfo().getId(), 1.0);
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
