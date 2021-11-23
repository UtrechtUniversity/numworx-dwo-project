package nl.numworx.uploadwidget;

import java.util.Locale;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.cbook.cbookif.CBookContext;
import org.cbook.cbookif.CBookWidgetEditIF;
import org.cbook.cbookif.CBookWidgetIF;
import org.cbook.cbookif.CBookWidgetInstanceIF;

import fi.beans.mainframe.JApplet;
import fi.beans.wiskopdrbeans.InteractiePanel;
import fi.beans.wiskopdrbeans.WiskOpdrApplet;
import nl.numworx.uploadwidget.module.Components;
import nl.numworx.uploadwidget.module.DaggerComponents;

@SuppressWarnings("serial")
public class UploadWidget extends JApplet implements WiskOpdrApplet, CBookWidgetIF, CBookContext {

	public static void main(String[] args) {
	}

	public UploadWidget() {
		this(JComponent.getDefaultLocale());
	}

	public UploadWidget(Locale locale) {
		setLocale(locale);
		builder = DaggerComponents.builder();
	}

	Components.Builder builder;
	
	@Override
	public CBookWidgetEditIF getEditor(CBookContext arg0) {
		return builder.context(arg0).build().editor();
	}

	@Override
	public Icon getIcon() {
		return new UploadIcon();
	}

	@Override
	public CBookWidgetInstanceIF getInstance(CBookContext arg0) {
		return builder.context(arg0).build().instance();
	}

	@Override
	public InteractiePanel getInteractiePanel() {
		return builder.context(this).build().interactiePanel();
	}

	@Override
	public Object getProperty(String arg0) {
		return null;
	}
	
	public String toString() {
		return "Upload Widget";
	}
	
}
