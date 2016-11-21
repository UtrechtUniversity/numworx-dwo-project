package fi.dwo.dwojapplet.gui.print;

import java.awt.event.ActionEvent;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Icon;

class PrintStudent extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4212398671483491492L;

	private PrinterEvent STARTED = new PrinterEvent(this, PrinterEvent.STARTED);
	private PrinterEvent STOPPED = new PrinterEvent(this, PrinterEvent.STOPPED);
	
	private void fire(PrinterEvent e) {
		if(listener != null)
			listener.onPrint(e);
	}
	
	
	Logger LOG = Logger.getLogger(getClass().getName());
	
	private SetupAction parent;
	private Printable  printable;
	private PrinterListener listener;
	
	/**
	 * @return the listener
	 */
	PrinterListener getListener() {
		return listener;
	}

	/**
	 * @param listener the listener to set
	 */
	void setListener(PrinterListener listener) {
		this.listener = listener;
	}

	PrintStudent(String name, Icon icon, SetupAction parent) {
		super(name, icon);
		this.parent = parent;
		setEnabled(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		PrinterJob job = parent.getPrinterJob();
		job.setPrintable(printable, parent.getPageFormat());
		boolean doPrint = job.printDialog();
		if( doPrint ) {
			fire(STARTED);
			try {
				job.print();
			} catch (PrinterException e1) {
				LOG.log(Level.SEVERE, "print action", e1);
			}
			fire(STOPPED);
		}	
	}

	/**
	 * @param printable the printable to set
	 */
	void setPrintable(Printable printable) {
		this.printable = printable;
		setEnabled(printable != null);
	}

	Printable getPrintable() {
		return printable;
	}

}
