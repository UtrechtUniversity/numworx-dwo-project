package fi.dwo.dwojapplet.gui.print;

import java.awt.event.ActionEvent;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;

import javax.swing.Icon;
import javax.swing.SwingWorker;

public class PDFStudent extends PrintStudent {
	
	Collection<Printable> iterable;

	public PDFStudent(String name, Icon icon, PDFSetupAction parent) {
		super(name, icon, parent);
	}

	@Override
	protected boolean printDialog(PrinterJob job) {
		return true; // no dialog
	}

	void setPrintable(Printable printable) {
		this.printable = printable;
		iterable = Collections.singleton(printable);
	}

	public void setPrintable(Collection<Printable> iterable) {
		this.iterable = iterable;
		printable = new PrintIterator(iterable);		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final PrinterJob job = parent.getPrinterJob();
		SwingWorker<Object, Object> worker = new SwingWorker<Object,Object>() {
			public Object doInBackground() {
				fire(STARTED);
				int count = 0;
				final int size = iterable.size();
				try {
					count = 1;
					Iterator<Printable> iterator = iterable.iterator();
					
					while (iterator.hasNext()) {
						printable = iterator.next();
						job.setPrintable(printable, parent.getPageFormat());
						job.setJobName(printable.toString());
						job.print();
						setProgress(count * 100 / size);
						count++;
					}
				} catch (PrinterException e1) {
					LOG.log(Level.SEVERE, "print action " + count, e1);
				}
				fire(STOPPED);
				return null;
			}
		};
		worker.addPropertyChangeListener( new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				System.err.println(evt);
				
			}
		});
		worker.execute();
	}

}
