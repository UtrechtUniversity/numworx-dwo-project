package fi.beans.numworxlf;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.TreeUI;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

public class JTree extends javax.swing.JTree {

  public JTree() {
  }

  public JTree(Object[] value) {
    super(value);
  }

  public JTree(Vector<?> value) {
    super(value);
  }

  public JTree(Hashtable<?, ?> value) {
    super(value);
  }

  public JTree(TreeNode root) {
    super(root);
  }

  public JTree(TreeModel newModel) {
    super(newModel);
  }

  public JTree(TreeNode root, boolean asksAllowsChildren) {
    super(root, asksAllowsChildren);
  }

  @Override
  public void updateUI() {
    setUI(NumworxTreeUI.createUI(this));

    //SwingUtilities.updateRendererOrEditorUI(getCellRenderer());
    //SwingUtilities.updateRendererOrEditorUI(getCellEditor());
  }

}
