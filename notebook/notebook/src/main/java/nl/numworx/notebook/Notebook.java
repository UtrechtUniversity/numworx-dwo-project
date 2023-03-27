package nl.numworx.notebook;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;

import javax.swing.Icon;

import org.cbook.cbookif.CBookContext;
import org.cbook.cbookif.CBookWidgetEditIF;
import org.cbook.cbookif.CBookWidgetIF;
import org.cbook.cbookif.CBookWidgetInstanceIF;

import fi.beans.mainframe.JApplet;
import fi.beans.wiskopdrbeans.InteractiePanel;
import fi.beans.wiskopdrbeans.WiskOpdrApplet;

@SuppressWarnings("serial")
public class Notebook extends JApplet implements CBookWidgetIF, WiskOpdrApplet {

	
	public InteractiePanel getInteractiePanel() {
		return new NotebookInteractiePanel(this);
	}

	public CBookWidgetEditIF getEditor(CBookContext arg0) {
		return new Editor(getLocale(), getHubBase());
	}

	public Notebook() {
		this(Locale.forLanguageTag("nl"));
	}
	public Notebook(Locale locale) {
		setLocale(locale);
	}
	
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	public CBookWidgetInstanceIF getInstance(CBookContext context) {
		return new Instance(getLocale(), getHubBase(), context);
	}

	@Override
	public String toString() {
		return "Notebook [locale=" + getLocale() + "]";
	}

	@Override
	public void init() {
		System.out.println(getCodeBase());
	}
	
	private URI hubBase;
	public URI getHubBase() {
		if (hubBase == null) {
			Properties p = new Properties();
			try {
				p.load(new URL(getCodeBase(), "DWO.properties").openStream());
			}  catch (IOException e) {
			}
			hubBase = URI.create(p.getProperty("hubUrlPath", "https://hub-dev.dwo.nl/"));
		}
		return hubBase;
		
	}
}
