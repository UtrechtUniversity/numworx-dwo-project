package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import fi.beans.numworxlf.JOptionPane;
import fi.dwo.dwojapplet.gui.ConfirmDialog;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelMethodInfo;

abstract class MethodeAction extends AbstractAction implements TreeSelectionListener {

  protected final static String KEY = "Key";

  
  String KOPPELING_LEERDOEL;
  String[] grJaarlagen;
  int aantalHoofdstukken[];
  boolean readonly;
  private Component owner;
  private TreePath path;
  
  MethodeAction(String name) {
    super(name);
    putValue(KEY, name);
  }
  
  String getName() {
    return getValue(NAME).toString();
  }
  String getKey() {
    return Objects.toString(getValue(KEY),null);
  }

  
  
  @Override
  public void actionPerformed(ActionEvent e) {
    //TreePath path = tree.getSelectionPath();
    if (path == null) return;
    
    Object node = path.getLastPathComponent();
    if (node instanceof MutableTreeNode) {
      DefaultMutableTreeNode mutable = (DefaultMutableTreeNode) node;
      Object o = mutable.getUserObject();
      if (o instanceof NodeLeaf) {
        actionOnLeaf( (NodeLeaf) o);
      }
    }
  }

  public KoppelingGRPanel getTab() {
    KoppelingGRPanel panel = createKoppelingPanel(true);
    panel.remove(panel.getBottomPanel());
    panel.remove(panel.getTopPanel());
    return panel;
  }

  protected void actionOnLeaf(NodeLeaf leaf) {
    String name = getKey();
    Map<String,Set<Integer>> methode = leaf.getMethode().getOrDefault(name, Collections.emptyMap());
    
    ConfirmDialog dialog = new ConfirmDialog(owner, "");
    KoppelingGRPanel panel = createKoppelingPanel(false);
    boolean[][] state = new boolean[aantalHoofdstukken.length][];
    for(int i = 0; i < aantalHoofdstukken.length; i++) {
      state[i] = new boolean[aantalHoofdstukken[i]];
      Set<Integer> set = methode.getOrDefault(grJaarlagen[i], Collections.emptySet());
      for( Integer j: set) {
        state[i][j-1] = true;
      }
    }
    panel.setState(state);
    
    dialog.setContentPane(panel);
    if(readonly) {
      panel.ok().addActionListener(dialog::cancel);
      //panel.ok().setText(TextMapper.getText(TextMapper.BTN_OK));
      panel.cancel().setVisible(false);
      panel.ok().setVisible(false);
      panel.setEnabled(false);
    } else {
      panel.ok().addActionListener(dialog::ok);
      panel.cancel().addActionListener(dialog::cancel);
    }
    dialog.pack();
    dialog.center();
    dialog.show();
    if (dialog.getOption() == JOptionPane.OK_OPTION) {
      Map<String, Set<Integer>> map = getMethodMap(panel);
      leaf.getMethode().put(name, map);
// Sync methodeinfos
      if (leaf.getMethodeInfos() != null) {
        Iterator<DomStudentModelMethodInfo> list = leaf.getMethodeInfos().iterator();
        while(list.hasNext()) {
          DomStudentModelMethodInfo item = list.next();
          if(item.getMethod() == null) {
            list.remove();
          } else {
          Map<String, Set<Integer>> m0 = leaf.getMethode().get(item.getMethod());
          if (m0 == null) list.remove();
          else {
            Set<Integer> m1 = m0.get(item.getBook());
            if (m1 == null) list.remove();
            else if (!m1.contains(item.getChapter())) list.remove();
          }}
        }
      }
    }
      
  }

  Map<String, Set<Integer>> getMethodMap(KoppelingGRPanel panel) {
    boolean[][] state;
    state = panel.getState();
    Map<String, Set<Integer>> map = new HashMap<>();
    for (int i = 0; i < grJaarlagen.length; i++ ) {
      Set<Integer> set = new TreeSet<>();
      for( int j = 0; j < aantalHoofdstukken[i]; j++) {
        if (state[i][j]) set.add(j+1);
      }
      if (!set.isEmpty()) map.put(grJaarlagen[i], set);
    }
    return map;
  }

  public void setMethodMap(KoppelingGRPanel panel, Map<String, Set<Integer>> map) {
    boolean[][] state;
    state = panel.getState();
    for (int i = 0; i < state.length; i++) {
      Set<Integer> set = map.getOrDefault(grJaarlagen[i], Collections.emptySet());
      boolean[] statei = state[i];
      for (int j = 0; j < statei.length; j++) {
          statei[j] = set.contains(j+1);
      }
    }
    panel.setState(state);
    
  }

  
  KoppelingGRPanel createKoppelingPanel(boolean filter) {
    return new KoppelingGRPanel(KOPPELING_LEERDOEL, grJaarlagen, aantalHoofdstukken, filter);
  }

  void setOwner(Component owner) {
    this.owner = owner;
  }

  @Override
  public void valueChanged(TreeSelectionEvent e) {
    if (e.isAddedPath()) {
      setPath(e.getNewLeadSelectionPath());
    }
  }

  void setPath(TreePath path) {
    this.path = path;
    setEnabled(path != null && getKey() != null);
  }

  void setTree(JTree tree) {
    setPath(tree.getSelectionPath());
    tree.addTreeSelectionListener(this);
  }

  void unsetTree(JTree tree) {
    setPath(null);
    tree.removeTreeSelectionListener(this);
  }
  
}