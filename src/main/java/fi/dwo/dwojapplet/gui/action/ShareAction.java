package fi.dwo.dwojapplet.gui.action;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import javax.swing.AbstractAction;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;

@SuppressWarnings("serial")
public class ShareAction extends AbstractAction implements ClipboardOwner, Transferable{

	private final int lesson;
	private final boolean deepest;

	public ShareAction(int lesson, boolean deepest) {
		this(TextMapper.getText("copylink"), lesson, deepest);
	}

	ShareAction(String name, int lesson2, boolean deepest) {
		super(name);
		this.lesson = lesson2;
		this.deepest = deepest;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		clip.setContents(this , this);
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return CopyLabel.DATA_FLAVORS;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return DataFlavor.stringFlavor .equals (flavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {	
		StringBuilder builder = new StringBuilder();
		URL base = DwoHelper.getURL("dwo.jsp");
        int profile = DWO.getDwoProfileID();
		Locale locale = DwoHelper.getAu().getLocale();
		String type = isDeepestLevel() ? "sco": "course";
		int id = getID();
		builder.append( base );
		builder.append("?profile="); builder.append(profile);
		builder.append("&language="); builder.append(locale.getLanguage());
		builder.append("&"); builder.append(type);
		builder.append("ViewNr="); builder.append(id);
		return builder.toString();
	}

	int getID() {
		return lesson;
	}

	boolean isDeepestLevel() {
		return deepest;
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}

}
