package fi.dwo.dwojapplet.gui.print;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import nl.numworx.swingbrowser.api.SwingBrowser;
import nl.numworx.swingbrowser.api.SwingBrowserFactory;
import nl.numworx.swingbrowser.api.SwingBrowserProvider;
import nl.numworx.swingbrowser.print.PrintEvent;
import nl.numworx.swingbrowser.print.PrintListener;
import nl.numworx.swingbrowser.print.Printing;

@SuppressWarnings("serial")
public class PrintBrowser extends AbstractAction implements PrintListener {

	private static final Logger LOG = Logger.getLogger(PrintBrowser.class.getName());
	private static SwingBrowserFactory factory = new SwingBrowserProvider().getFactory();
	private SetupAction parent;
	private SwingBrowser browser;
	private JFrame frame;
	
	PrintBrowser(String name, Icon icon, SetupAction parent) {
		super(name, icon);
		this.parent = parent;
	}

	private PrintBrowser() { }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		LOG.info("start printing via browser " + e);
		browser = factory.newBrowser();
		frame = new JFrame("Printing");
		frame.setContentPane(browser.asComponent());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setSize(1024,768);
		frame.setVisible(true);
		Optional<Printing> opt = browser.printing();
		if (opt.isPresent()) {
			browser.loadURL("http://www.numworx.nl");
			browser.addConsoleListener(x -> LOG.info(x.getMessage()));
			browser.addStatusListener(s -> LOG.info(s.getStatus()));
			Printing p = opt.get();
			p.addPrintListener(this);
			p.start();
		}
		
	}

	@Override
	public void onPrint(PrintEvent e) {
		LOG.info("on print + "  + e);
		browser.printing().get().removePrintListener(this);
		try {
			browser.close();
			frame.dispose();
			frame = null;
			browser = null;
		} catch (IOException e1) {
			LOG.log(Level.WARNING, "onPrint", e1);
		}
	}

	
	public static void main(String[] args) {
		new PrintBrowser().actionPerformed(null);
		
	}
	
}
