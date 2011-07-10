package fi.dwo.client.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import fi.dwo.client.domain.CourseMap;
import fi.dwo.client.gui.ModuleTreePanel.TreeMap;

public class ModuleTreePopup extends MouseAdapter  {

	JPopupMenu popup;
	SelectStrategy strategy;
	private TreePath path;
	ModuleTreePanel panel;
	
	public ModuleTreePopup(ModuleTreePanel p) {
		panel = p;
	}


	public void mousePressed(MouseEvent e) {
		
		if (e.isPopupTrigger())
			popupTrigger(e);
	}

	private void popupTrigger(MouseEvent e) {
		
		JTree tree = (JTree) e.getSource();
		path = tree.getSelectionPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		Object o = node.getUserObject();
		if(o instanceof CourseMap)
		{
			popup = strategy.nodeAction((CourseMap) o);
		} else
			popup = strategy.nodeAction(panel.new TreeMap(node));
		if(popup != null)
			popup.show( tree, e.getX(), e.getY() );
	}

	public void mouseReleased(MouseEvent e) {
		if(e.isPopupTrigger())
			popupTrigger(e);
	}

	public void setPopup(SelectStrategy s) {
		strategy = s;
	}

}
