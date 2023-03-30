package fi.dwo.dwojapplet.gui.print;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.JComponent;
import javax.swing.RootPaneContainer;

import fi.dwo.dwojapplet.domain.Sco;

public class PrintComponent implements Printable {

	private Component component;
	private Printable delegate, frontpage;

	public PrintComponent(Component component) {
		this.component = component;
		if(component instanceof Printable) 
		{
			delegate = (Printable) component;
			this.component = null;
		}
		if(this.component instanceof RootPaneContainer) {
			this.component = ((RootPaneContainer) component).getContentPane();
		}
	}

	public PrintComponent(Component component, Sco sco) {
		this(component);
		frontpage = new Frontpage(sco);
	}
	
	
	
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		if(frontpage != null) {
			if( pageIndex == 0)
				return frontpage.print(graphics, pageFormat, pageIndex);
			pageIndex --;
	    }
		
		
		if(delegate != null) 
			return delegate.print(graphics, pageFormat, pageIndex);

		Graphics2D g2d = (Graphics2D)graphics;
	    g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
	    if(pageIndex == 0) {
	    	component.print(g2d);
	    	return PAGE_EXISTS;
	    }
		return NO_SUCH_PAGE;
	}

}
