package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class GraphTreeAction implements ActionListener {

  private JTree tree;

  public GraphTreeAction(JTree tree) {
    this.tree = tree;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String id = e.getActionCommand();
    String[] split = id.split("/",2);
	id = split[0]; // throw /variant away
    DefaultMutableTreeNode r = (DefaultMutableTreeNode) tree.getModel().getRoot();
    Enumeration en = r.depthFirstEnumeration();
    while (en.hasMoreElements()) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
      Object uo = node.getUserObject();
      if (uo instanceof NodeLeaf && id.equals(((NodeLeaf) uo).getId())) {
    	NodeLeaf nl = (NodeLeaf) uo;
    	if (split.length > 1) nl.setVariant(split[1]); else nl.setVariant((String)null);
        TreeNode[] nodes = node.getPath();
        tree.setSelectionPath(new TreePath(nodes));
        tree.scrollPathToVisible(tree.getSelectionPath());
        break;
      } else if(uo instanceof NodeVector && id.equals(((NodeVector) uo).info.getId())) {
    	  tree.setSelectionPath(new TreePath(node.getPath()));
    	  tree.scrollPathToVisible(tree.getSelectionPath());
    	  break;
      }
    }

  }



}
