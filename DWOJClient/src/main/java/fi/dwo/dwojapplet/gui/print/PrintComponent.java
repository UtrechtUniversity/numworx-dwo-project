package fi.dwo.dwojapplet.gui.print;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.RootPaneContainer;

import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.gui.action.WrapSco;
import nl.numworx.swingbrowser.scorm.SCORM2004APIInterface;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;

public class PrintComponent implements Printable, SCORM2004APIInterface {

	private Component component;
	private Printable delegate, frontpage;
	private Sco sco;
	private WrapSco wrap;
	

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
		this.sco = sco;
		this.wrap = new WrapSco(sco);
		wrap.dwo = sco.dwo;
	}
	
	public String toString() {
		String user = sco.LMSGetValue("cmi.learner_name"); // zo laat mogelijk..
		if (user != null && !user.isEmpty())
			return user;
		return super.toString();
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

	@Override
	public String Initialize(String dummy) {
		return "true";
	}

	@Override
	public String Commit(String dummy) {
		return "false";
	}

	@Override
	public String Terminate(String dummy) {
		return "true";
	}

	@Override
	public String GetValue(String key) {
		if ("dme.server_url".equals(key))
			return StoredRestManager.getInstance().getAuthenticator().getServerUrlPath().toExternalForm();
		if ("cmi.launch_data".equals(key))
			return wrap.GetValue(key);
		return sco.GetValue(key);
	}

	@Override
	public String SetValue(String key, String value) {
		return "false";
	}

	@Override
	public String GetLastError() {
		return "0";
	}

	@Override
	public String GetDiagnostic(String iErrorCode) {
		return "";
	}

	@Override
	public String GetErrorString(String iErrorCode) {
		return "";
	}

}
