package fi.dwo.dwojapplet.gui.print;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrintIterator implements Printable {

	private static final Logger LOG = Logger.getLogger("fi.dwo.dwojapplet.gui.print.PrintIterator");
	
	private Iterator<Printable> pages;
	private Iterable<Printable> collection;
	private Printable current;
	int page, offset;
	public PrintIterator(Iterable<Printable> collection) {
		this.collection = collection;
		page = Integer.MAX_VALUE;
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		if(pageIndex < page ) {
			current = null;
			pages = collection.iterator();
			offset = 0;
			page = -1;
			if(pageIndex != 0) {
				LOG.log(Level.WARNING, "not skipping pages!");
			}
		}

		if(current == null) {
			if ( ! pages.hasNext()) return NO_SUCH_PAGE;
			current = pages.next();
		}

		if(pageIndex == page) {
			return current.print(graphics, pageFormat, pageIndex-offset);
		}
				
		int exist = current.print(graphics, pageFormat, pageIndex-offset);
		while(exist == NO_SUCH_PAGE && pages.hasNext())
		{
			current = pages.next();
			offset = pageIndex;
			exist = current.print(graphics, pageFormat, 0);
		}
		page = pageIndex;
		return exist;
	}

}
