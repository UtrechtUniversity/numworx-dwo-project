package fi.dwo.dwojapplet.gui.print;

import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;

import javax.swing.AbstractAction;
import javax.swing.Icon;

class SetupAction extends AbstractAction {

	protected PageFormat pf;
	protected PrinterJob job;
	
	SetupAction(String name) {
		this(name, null);
	}

	SetupAction(String name, Icon icon) {
		super(name, icon);
		job = createPrinterJob();
		pf  = job.defaultPage();
	}

	protected PrinterJob createPrinterJob() {
		return PrinterJob.getPrinterJob();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		pf = job.pageDialog(pf);
	}

	PrinterJob getPrinterJob() {
		return job;
	}
	
	PageFormat getPageFormat() {
		return pf;
	}
}
