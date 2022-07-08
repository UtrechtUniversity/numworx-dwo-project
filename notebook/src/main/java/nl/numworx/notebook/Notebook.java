package nl.numworx.notebook;

import java.util.Locale;

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
		return new Editor(getLocale());
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
		return new Instance(getLocale());
	}

	@Override
	public String toString() {
		return "Notebook [locale=" + getLocale() + "]";
	}

}
