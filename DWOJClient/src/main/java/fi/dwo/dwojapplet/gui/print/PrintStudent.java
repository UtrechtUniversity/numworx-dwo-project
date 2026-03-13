package fi.dwo.dwojapplet.gui.print;

import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Icon;

class PrintStudent extends AbstractAction implements Pageable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4212398671483491492L;

	protected PrinterEvent STARTED = new PrinterEvent(this, PrinterEvent.STARTED);
	protected PrinterEvent STOPPED = new PrinterEvent(this, PrinterEvent.STOPPED);
	
	protected void fire(PrinterEvent e) {
		if(listener != null)
			listener.onPrint(e);
	}
	
	
	Logger LOG = Logger.getLogger(getClass().getName());
	
	protected SetupAction parent;
	Printable  printable;
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
		job.setPageable(this);
		boolean doPrint = printDialog(job);
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

	protected boolean printDialog(PrinterJob job) {
		return job.printDialog();
	}

	/**
	 * @param printable the printable to set
	 */
	void setPrintable(Printable printable) {
		this.printable = printable;
		setEnabled(printable != null);
		pages = Pageable.UNKNOWN_NUMBER_OF_PAGES;
	}
	
	void setNumberOfPages(int pages) {
		this.pages = pages; // -1 is unknown
	}
	
	void setPrintablePages(Printable printable, int pages) {
		setPrintable(printable);
		setNumberOfPages(pages);
	}

	Printable getPrintable() {
		return printable;
	}

	int pages = Pageable.UNKNOWN_NUMBER_OF_PAGES;

	@Override
	public int getNumberOfPages() {
		return pages;
	}

	@Override
	public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
		return parent.getPageFormat();
	}

	@Override
	public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
		return printable;
	}

}
