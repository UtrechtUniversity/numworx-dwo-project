package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import fi.dwo.dwojapplet.gui.domainmodel.graph.EditableGraph;
import fi.dwo.dwojapplet.gui.domainmodel.graph.GraphNode;
import fi.dwo.dwojapplet.gui.domainmodel.methods.MethodsProperties;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class MethodListener implements ItemListener, ActionListener {
  public static final String BEGRIPPEN_EN_VAKTAAL = "Begrippen en vaktaal";

  final JCheckBox methodBox;
  InvisibleTreeModel methodModel;
  private PersistenceId activeMethod;
  final private JTree tree;
  final private TreeModel model;
  private Map<Object, Integer> treeOrder = new IdentityHashMap<>();
  private FilterAction filterAction;
  
  public MethodListener(JCheckBox box, JTree tree, FilterAction filteraction) {
    methodBox = box;
    this.tree = tree;
    this.model = tree.getModel();
    this.filterAction = filteraction;
    box.addItemListener(this);  
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    if (methodBox.isSelected()) {
      if (methodModel == null) {
        Map<String, InvisibleNode> nodes = new HashMap<>();
        DomMethod current = MethodsProperties.instance().getMethod(activeMethod);
        Object userObject = current.getMethod();
        InvisibleNode root = new InvisibleNode(userObject, true, true);
        int bookcount = current.books.size();
        for(int i = 0; i < bookcount; i++ ) {
          InvisibleNode book = new InvisibleNode(current.books.get(i), true, true);
          root.add(book);
          List<String> chapters = current.chapters.get(i);
          int chapsize = chapters.size();
          for(int j = 0; j < chapsize; j ++) {
            InvisibleNode chap = new InvisibleNode(chapters.get(j), true, true);
            book.add(chap);
            String key = current.key() + "-" + book.toString() + "-" + (j+1);
            nodes.put(key, chap);
            InvisibleNode benv = new InvisibleNode(BEGRIPPEN_EN_VAKTAAL, true, true);
            chap.add(benv);
            key = key + "-W:";
            nodes.put(key,  benv);
          }
        }
        insertAllLeafNodes2(nodes);
        methodModel = new InvisibleTreeModel(root);
        treeOrder.clear();
        filterMethod(filterAction.filter);
      }
      tree.setModel(methodModel);
    } else {
      tree.setModel(model);
    }
  }

  private void insertAllLeafNodes(Map<String, InvisibleNode> nodes) {
    Enumeration<DefaultMutableTreeNode> all = (Enumeration)((DefaultMutableTreeNode) model.getRoot()).depthFirstEnumeration();
    while( all.hasMoreElements() ) {
      Object o = all.nextElement().getUserObject();
      if (o instanceof NodeLeaf) {
        NodeLeaf nl = (NodeLeaf)o;
        Set<String> infos = GraphNode.extractInfos(nl.getMethode()).keySet();
        String title = nl.toString();
        for(String mi : infos) {
          if (title.startsWith("W:")) 
            mi += "-W:";
          nodes.computeIfPresent(mi, (k, n) -> { 
            InvisibleNode node = new InvisibleNode(new NodeLeaf(title, nl.getInfo(),nl.getLanguage(), false), false, true);
            treeOrder.put(node, treeOrder.size());
            insertMethod(n,node); return n; });
        }
      }
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private void insertAllLeafNodes2(Map<String, InvisibleNode> nodes) {
    Enumeration<DefaultMutableTreeNode> all;
    all = (Enumeration)((DefaultMutableTreeNode) model.getRoot()).depthFirstEnumeration();
    LinkedHashMap<String, NodeLeaf> links = new LinkedHashMap<>();
    HashMap<String, Set<String>> sets = new HashMap<>();
    while (all.hasMoreElements()) {
      Object o = all.nextElement().getUserObject();
      if (o instanceof NodeLeaf) {
        NodeLeaf n = (NodeLeaf) o;
        links.put(n.getId(), n);
        if (n.getVoorkennis() != null && !n.getVoorkennis().isEmpty()) 
          sets.put(n.getId(), new HashSet<>(n.getVoorkennis()));
      }     
    }
    // closure
    closure(sets);
    List<NodeLeaf> list = new Vector<>(links.values());
    Collections.sort(list, (a, b) -> {
      int result = 0;
        String ida = a.getId(); Set<String> sa = sets.getOrDefault(ida, Collections.emptySet());
        String idb = b.getId(); Set<String> sb = sets.getOrDefault(idb, Collections.emptySet());
        if (sa.contains(idb)) 
          result = +1;
        if (sb.contains(ida))
          result = -1;
      return result;
    });
    Iterator<NodeLeaf> nall = list.iterator();
    while( nall.hasNext() ) {
      Object o = nall.next();
      if (o instanceof NodeLeaf) {
        NodeLeaf nl = (NodeLeaf)o;
        Set<String> infos = GraphNode.extractInfos(nl.getMethode()).keySet();
        String title = nl.toString();
        for(String mi : infos) {
          if (title.startsWith("W:")) 
            mi += "-W:";
          nodes.computeIfPresent(mi, (k, n) -> { 
            InvisibleNode node = new InvisibleNode(new NodeLeaf(title, nl.getInfo(),nl.getLanguage(), false), false, true);
            treeOrder.put(node, treeOrder.size());
            insertMethod(n,node); return n; });
        }
      }
    }
  }

  
  private void closure(HashMap<String, Set<String>> sets) {
    boolean done;
    do { done = true;
      for(Map.Entry<String, Set<String>> entry: sets.entrySet()) {
        boolean added = false;
        if (! entry.getValue().isEmpty()) 
        for(String i: new HashSet<>(entry.getValue())) {
          Set<String> extra = sets.getOrDefault(i, Collections.emptySet());
          added = entry.getValue().addAll(extra) || added;
        }
        if (added)
          done = false;
      }
    } while(!done);
    
  }

  void setActiveMethod(PersistenceId am) {
    end();
    this.activeMethod = am;
    //methodBox.setText(MethodsProperties.instance().getMethod(am).getMethod());
    methodBox.setEnabled(am != null);
  }

  public void end() {
    tree.setModel(model);
    methodBox.setSelected(false);
    methodModel = null;
  }

  void filterMethod(Map<String, Map<String, Set<Integer>>> filter) {
    if (methodModel != null) {
      InvisibleNode root = (InvisibleNode) methodModel.getRoot();
      if (filter.isEmpty()) {
        methodModel.activateFilter(false);
      } else {
        methodModel.activateFilter(true);
        String key = DomMethod.key(activeMethod);
        Map<String, Set<Integer>> map = filter.getOrDefault(key, Collections.emptyMap());
        Enumeration<InvisibleNode> books = (Enumeration) root.children();
        while(books.hasMoreElements()) {
          InvisibleNode book = books.nextElement();
          String booktitle = book.toString();
          if (map.containsKey(booktitle)) {
            book.setVisible(true);
            Set<Integer> set = map.getOrDefault(booktitle, Collections.emptySet());
            int count = book.getChildCount();
            for(int i = 0; i < count; i++) {
              ((InvisibleNode) book.getChildAt(i)).setVisible(set.contains(Integer.valueOf(i+1)));
            }
          } else {
            book.setVisible(false);
          }
          
        }
      }
      methodModel.nodeStructureChanged(root);
    }
    
  }

  private void insertMethod(InvisibleNode parent, InvisibleNode node) {
    int count = parent.getChildCount();
    String title = node.toString();
    for (int i = 0; i < count; i++) {
      TreeNode child = parent.getChildAt(i);
      if (compareMethod(node, child, title, child.toString()) < 0) {
        parent.insert(node, i);
        return;
      }
    }
    parent.add(node);
  }

  // TreeOrder is nu nog gegenereerd, maar kan ook uit een 'database' komen. 
  private int compareMethod(InvisibleNode a, TreeNode b, String as, String bs) {
    Integer ia = treeOrder.get(a);
    Integer ib = treeOrder.get(b);
    if (ia != null && ib != null) return ia.compareTo(ib);
    
    return compareMethod(as, bs);
  }

  private int compareMethod(String as, String bs) {
    boolean wa = as == BEGRIPPEN_EN_VAKTAAL;
    boolean wb = bs == BEGRIPPEN_EN_VAKTAAL;
    if (wa && !wb) return +1;
    if (!wa && wb) return -1;
    return as.compareTo(bs);
  }

  public void setEditable(boolean b) {
    if (b) {
      methodBox.setSelected(false);
      methodBox.setVisible(false);
      tree.setModel(model);
      methodModel = null;      
    } else {
      methodBox.setVisible(true);      
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();
    if ("filter".equals(e.getActionCommand()) && source instanceof EditableGraph && methodModel != null) {
      EditableGraph graph = (EditableGraph) source;
      if ( graph.isShowing()) {
        Set<String> visible = graph.getVisibleNodes(); // id's of visible nodes
        methodModel.activateFilter(!visible.isEmpty());
        InvisibleNode root = (InvisibleNode) methodModel.getRoot();
        methodModel.setRoot(LeerdomeinEditPanel2.filter(root, visible));
        methodModel.nodeStructureChanged((TreeNode) model.getRoot());

      }
    }
  }

}
