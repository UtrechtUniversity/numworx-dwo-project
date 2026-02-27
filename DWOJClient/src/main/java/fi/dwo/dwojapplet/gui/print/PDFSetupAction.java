package fi.dwo.dwojapplet.gui.print;

import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.util.Collection;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import fi.beans.loader.Loader;

public class PDFSetupAction extends SetupAction {
	
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

	@Override
	protected PrinterJob createPrinterJob() {
		ClassLoader loader = Loader.create("dwojprint.jar", getClass().getClassLoader());
		try {
			@SuppressWarnings("unchecked")
			Class<PrinterJob> clz = (Class<PrinterJob>) Class.forName("fi.dwo.dwojprint.DWOJPrint", false, loader );
			return clz.newInstance();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
