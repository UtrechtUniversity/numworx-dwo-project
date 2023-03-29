package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

class GraphTreeAction implements ActionListener {

  private JTree tree;

  GraphTreeAction(JTree tree) {
    this.tree = tree;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String id = e.getActionCommand();
    DefaultMutableTreeNode r = (DefaultMutableTreeNode) tree.getModel().getRoot();
    Enumeration en = r.depthFirstEnumeration();
    while (en.hasMoreElements()) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
      Object uo = node.getUserObject();
      if (uo instanceof NodeLeaf && id.equals(((NodeLeaf) uo).getId())) {
        TreeNode[] nodes = node.getPath();
        tree.setSelectionPath(new TreePath(nodes));
        tree.scrollPathToVisible(tree.getSelectionPath());
        break;
      }
    }

  }



}
