package fi.dwo.dwojapplet.gui.domainmodel;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LeerdoelSortTest {

  private static final String NL = "nl";
  MethodListener l;
  private DefaultTreeModel model;
  private DefaultMutableTreeNode root;
  private InvisibleNode chapter;
  Map<String, InvisibleNode> map = new LinkedHashMap<>();
  
  @Before
  public void setUp() throws Exception {
    JTree tree = new JTree();
    root = new DefaultMutableTreeNode();
    model = new DefaultTreeModel(root);
    tree.setModel(model);
    l = new MethodListener(new JCheckBox(), tree, null);
    chapter = new InvisibleNode("null-null-null");
    map.put("null-null-null", chapter);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void test() {
      NodeLeaf object = new NodeLeaf(NL);
      InvisibleNode node = new InvisibleNode(object);
      root.add(node);
      l.insertAllLeafNodes2(map);
      assertEquals(1, l.treeOrder.size());
  }
  @Test
  public void test2() {
      NodeLeaf object = new NodeLeaf(NL);object.setTitle("o1");
      NodeLeaf o2 = new NodeLeaf(NL);o2.setTitle("o2");
      
      object.setVoorkennis(Collections.singletonList(o2.getId()));
      InvisibleNode node = new InvisibleNode(object);
      InvisibleNode n2 = new InvisibleNode(o2);
      root.add(node); root.add(n2);
      l.insertAllLeafNodes2(map);
      System.out.println(l.treeOrder);
      assertEquals(2, l.treeOrder.size());
      assertEquals(o2.getId(), ((NodeLeaf) ((DefaultMutableTreeNode) chapter.getChildAt(0)).getUserObject()).getId());
      
  }
  @Test
  public void test3() {
      NodeLeaf object = new NodeLeaf(NL); object.setTitle("o1");
      NodeLeaf o2 = new NodeLeaf(NL);     o2.setTitle("o2");
      NodeLeaf o3 = new NodeLeaf(NL);     o3.setTitle("o3");
      
      object.setVoorkennis(Collections.singletonList(o2.getId()));
      InvisibleNode node = new InvisibleNode(object);
      InvisibleNode n2 = new InvisibleNode(o2);
      InvisibleNode n3 = new InvisibleNode(o3);
      root.add(node); root.add(n3);root.add(n2);
      l.insertAllLeafNodes2(map);
      assertEquals(3, l.treeOrder.size());
      System.out.println(l.treeOrder);
      assertEquals(o2.getId(), ((NodeLeaf) ((DefaultMutableTreeNode) chapter.getChildAt(0)).getUserObject()).getId());
      
  }

}
