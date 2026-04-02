package fi.dwo.dwojapplet.gui.print;

import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import fi.beans.loader.Loader;
import fi.dwo.dwojapplet.domain.DwoHelper;

public class PDFSetupAction extends SetupAction {
	
	java.util.logging.Logger LOG = Logger.getLogger(getClass().getName());
	
	Collection<AbstractAction> enabled = Collections.emptySet();
	private final SetupAction delegate;

	public PDFSetupAction(String name, SetupAction delegate) {
		super(name);
		this.delegate = delegate;
	}

	public PDFSetupAction(String name, Icon icon, SetupAction delegate) {
		super(name, icon);
		this.delegate = delegate;
	}

	final static String jars[] = { "dwogwtprint.jar", "dwojprint.jar" }, jobs[] = { "fi.dwo.dwogwtprint.DWOGWTPrint", "fi.dwo.dwojprint.DWOJPrint" };
	
	
	@Override
	protected PrinterJob createPrinterJob() {
		int index = 0; 
		if (DwoHelper.noJXB) index = 1;
 		
		ClassLoader loader = Loader.create(jars[index], getClass().getClassLoader());
		try {
			@SuppressWarnings("unchecked")
			Class<PrinterJob> clz = (Class<PrinterJob>) Class.forName(jobs[index], false, loader );
			return clz.newInstance();
		} catch (ClassNotFoundException|InstantiationException|IllegalAccessException e) {
			LOG.log(Level.SEVERE, e.toString());
		}
		setEnabled(false);
		return super.createPrinterJob();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		boolean enable = job.printDialog(); // ons kent ons;
		enabled.forEach( t -> t.setEnabled(enable));
	}

	@Override
	PageFormat getPageFormat() {
		return delegate.getPageFormat();
	}

}
