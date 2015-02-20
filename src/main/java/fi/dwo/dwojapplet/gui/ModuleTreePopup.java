package fi.dwo.dwojapplet.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import fi.dwo.dwojapplet.domain.CourseMap;

public class ModuleTreePopup extends MouseAdapter {

    JPopupMenu popup;
    SelectStrategy strategy;
    private TreePath path;
    ModuleTreePanel panel;

    public ModuleTreePopup(ModuleTreePanel p) {
        panel = p;
        strategy = p.delegate;
    }

    @Override
    public void mousePressed(MouseEvent e) {

        if (e.isPopupTrigger()) {
            popupTrigger(e);
        }
    }

    private void popupTrigger(MouseEvent e) {
        e.consume();
        JTree tree = (JTree) e.getSource();
// which one?		
        //path = tree.getSelectionPath();
        path = tree.getClosestPathForLocation(e.getX(), e.getY());

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object o = node.getUserObject();
        if (o == ModuleTreePanel.SCHOOL_MODULES) {
            popup = strategy.nodeAction(ModuleTreePanel.SCHOOL_MAP);
        } else if (o == ModuleTreePanel.STANDAARD_DWO_MODULES) {
            popup = strategy.nodeAction(ModuleTreePanel.STANDAARD_DWO_MAP);
        } else if (o instanceof CourseMap) {
            popup = strategy.nodeAction((CourseMap) o);
        } else {
            popup = strategy.nodeAction(panel.new TreeMap(node));
        }
        if (popup != null) {
            popup.show(tree, e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            popupTrigger(e);
        }
    }

    public void setPopup(SelectStrategy s) {
        strategy = s;
    }

}
