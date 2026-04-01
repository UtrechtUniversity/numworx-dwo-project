package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.Component;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.DwoHelper;

public class TreeCellRenderer extends DefaultTreeCellRenderer {

    Icon bookIcon, folderIcon;
    boolean isCourse, isMap;

    public TreeCellRenderer() {
        super();
        Image book = DwoHelper.getResourceImage("resources/book.png");
        bookIcon = new ImageIcon(book);
        folderIcon = new ImageIcon(DwoHelper.getResourceImage("resources/folder.png"));
        setTextNonSelectionColor(fi.beans.numworxlf.Constants.COLOR15);
        setTextSelectionColor(java.awt.Color.WHITE);
        setBackgroundSelectionColor(fi.beans.numworxlf.Constants.COLOR14);
    }

    @Override
    public Icon getOpenIcon() {
        if (isCourse) {
            return bookIcon;
        }
        return folderIcon;
    }

    @Override
    public Icon getClosedIcon() {
        if (isCourse) {
            return bookIcon;
        }
        return folderIcon;
    }

    @Override
    public Icon getLeafIcon() {
        if (isCourse) {
            return bookIcon;
        }
        if (isMap) {
            return getClosedIcon();
        }
        return super.getDefaultLeafIcon();
    }

    @Override
    public Component getTreeCellRendererComponent(javax.swing.JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
//bookicon als het een course is
        isCourse
                = value instanceof DefaultMutableTreeNode
                && ((DefaultMutableTreeNode) value).getUserObject() instanceof Course
                && !((Course) ((DefaultMutableTreeNode) value).getUserObject()).isWithChildren();
        isCourse = false;
        isMap = value instanceof DefaultMutableTreeNode
                && ((DefaultMutableTreeNode) value).getUserObject() instanceof NodeLeaf;
//geen leaficon als het een lege map is
        isMap = leaf && !isMap;

        return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
                row, hasFocus);
    }

}