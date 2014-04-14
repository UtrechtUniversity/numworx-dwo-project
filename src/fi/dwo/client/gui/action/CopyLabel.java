package fi.dwo.client.gui.action;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import fi.dwo.client.system.TextMapper;

public class CopyLabel extends AbstractAction implements MouseListener, ClipboardOwner, Transferable {

	private static final DataFlavor[] DATA_FLAVORS = new DataFlavor[] { DataFlavor.stringFlavor };
	private String text;
	private JPopupMenu popup;

	public CopyLabel(String text) {
		super(TextMapper.getText("copy"));
		this.text = text;
		this.popup = new JPopupMenu();
		this.popup.add(this);
	}

	public void actionPerformed(ActionEvent e) {
		Clipboard clip = popup.getToolkit().getSystemClipboard();
		clip.setContents(this , this);
	}

	public void mousePressed(MouseEvent e) {
		if(e.isPopupTrigger())
			popup.show(e.getComponent(), e.getX(), e.getY());
	}

	public void mouseReleased(MouseEvent e) {
		if(e.isPopupTrigger())
			popup.show(e.getComponent(), e.getX(), e.getY());
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}

	public DataFlavor[] getTransferDataFlavors() {
		return DATA_FLAVORS;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return DataFlavor.stringFlavor .equals (flavor);
	}

	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		return text;
	}

}
