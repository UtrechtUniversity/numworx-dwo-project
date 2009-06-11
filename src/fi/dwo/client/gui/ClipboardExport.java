/**
 * 
 */
package fi.dwo.client.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import fi.dwo.client.domain.ResultScore;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.UserResultList;
import fi.dwo.client.gui.Exporter.ExportBuffer;

/**
 * Class that will export a tab separated table to the clipboard
 * @author Wim
 *
 */
public class ClipboardExport extends Exporter implements ClipboardOwner {

	private class ClipBoardBuffer extends ExportBuffer {

		StringBuffer sb = new StringBuffer();
		protected void export() {
			ClipboardExport.this.export(sb);
		}

		protected void export(String[] line) {
			ClipboardExport.this.export(sb,line);
		}
	}

	private Clipboard clipboard; 
	
	void useExportFrame() { clipboard = null; } 
	

    private void export(StringBuffer sb) {
    	final String toString = sb.toString();
    	if(clipboard != null ) {
    		StringSelection contents = new StringSelection(toString);
			clipboard.setContents(contents, this);
		} else {
			exportFrame(toString);
		}
	}

    /**
     * Hack. Een textarea mag wel copieren naar het clipboard.
     * @param contents
     */
    private void exportFrame(String contents) {
		final TextArea area = new TextArea(contents, 0, 0, TextArea.SCROLLBARS_NONE);
		Frame f = new Frame("Copy to Clipboard");
		f.setLayout(new BorderLayout());
		f.add(area,BorderLayout.CENTER);
		f.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				area.requestFocus();
				area.setCaretPosition(0);
			}
			public void windowActivated(WindowEvent e) {
				area.selectAll();
			}
			public void windowClosing(WindowEvent e) {
					e.getWindow().dispose();
			} });
		f.pack();
		f.setVisible(true);
		f.toFront();
	}

	/**
     * Append line array to the stringbuffer.
     * @param sb
     * @param line
     * @return sb
     */
	private StringBuffer export(StringBuffer sb, String[] line) {
		for (int i = 0; i < line.length; i++) {
			sb.append(line[i]);
			sb.append('\t');
		}
		sb.setCharAt(sb.length()-1, '\n');
		return sb;
	}

	private static ClipboardExport _instance;

	/**
     * Singleton pattern
     * @return the exporter.
     */
	public static Exporter instance() {
		if(_instance == null)
			_instance = new ClipboardExport();
		    try {
				_instance.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			} catch (SecurityException e) {
			}
		return _instance;
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}


	protected ExportBuffer createExportBuffer() {
		return new ClipBoardBuffer();
	}

}
