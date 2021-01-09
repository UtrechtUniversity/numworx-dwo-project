package fi.dwo.dwojapplet.gui.domainmodel;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class InvisibleTreeModel extends DefaultTreeModel {

  protected boolean filterIsActive;

  public InvisibleTreeModel(TreeNode root) {
    this(root, false);
  }

  public InvisibleTreeModel(TreeNode root, boolean asksAllowsChildren) {
    this(root, asksAllowsChildren, false);
  }

  public InvisibleTreeModel(TreeNode root, boolean asksAllowsChildren,
      boolean filterIsActive) {
    super(root, asksAllowsChildren);
    this.filterIsActive = filterIsActive;
  }

  public void activateFilter(boolean newValue) {
    filterIsActive = newValue;
  }

  public boolean isActivatedFilter() {
    return filterIsActive;
  }

  public Object getChild(Object parent, int index) {
    if (filterIsActive) {
      if (parent instanceof InvisibleNode) {
        return ((InvisibleNode) parent).getChildAt(index,
            filterIsActive);
      }
    }
    return ((TreeNode) parent).getChildAt(index);
  }

  public int getChildCount(Object parent) {
    if (filterIsActive) {
      if (parent instanceof InvisibleNode) {
        return ((InvisibleNode) parent).getChildCount(filterIsActive);
      }
    }
    return ((TreeNode) parent).getChildCount();
  }

  /* (non-Javadoc)
   * @see javax.swing.tree.DefaultTreeModel#nodeChanged(javax.swing.tree.TreeNode)
   */
  @Override
  public void nodeChanged(TreeNode node) {
    final boolean old = filterIsActive;
    try {
      filterIsActive = false;
      if (!old) super.nodeChanged(node);
      else super.nodeStructureChanged(root);
    } finally {
      if (filterIsActive != old) {
        filterIsActive = old;
        super.nodeStructureChanged(root);
      }
    }
  }

}