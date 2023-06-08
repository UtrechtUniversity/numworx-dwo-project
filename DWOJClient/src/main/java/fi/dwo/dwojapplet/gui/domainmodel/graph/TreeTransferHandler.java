package fi.dwo.dwojapplet.gui.domainmodel.graph;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import fi.dwo.dwojapplet.gui.domainmodel.NodeLeaf;

public class TreeTransferHandler extends TransferHandler {

  @Override
  protected Transferable createTransferable(JComponent c) {
    if (c instanceof JTree) {
      JTree tree = (JTree) c;
      TreePath path = tree.getSelectionPath();
      if (path != null) {
        Object node = path.getLastPathComponent();
        if (tree.getModel().isLeaf(node)) {
          Object object = ((DefaultMutableTreeNode) node).getUserObject();
          if (object instanceof NodeLeaf) {
            String id = ((NodeLeaf) object).getId();
            return new StringSelection(id);
          }
        }
      }
    }
    return null;
  }

  public int getSourceActions(JComponent c) {
    return COPY;
}

}
