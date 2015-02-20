package fi.beans.mathkit;

import java.net.URL;

import javax.swing.JTextPane;
import javax.swing.text.EditorKit;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;

public class JMathPane extends JTextPane {

	private URL base;
	
	public JMathPane() {
	}
	
	/** Shortcut.
	 * @param url base url
	 */
	public JMathPane(URL url) {
		base = url;
		getHTMLDocument().setBase(url);
	}

	private HTMLDocument getHTMLDocument() {
		return (HTMLDocument) getStyledDocument();
	}

	public JMathPane(StyledDocument doc) {
		super(doc);
	}

        @Override
	protected EditorKit createDefaultEditorKit() {
		return new MathKit();
	}

	private String text = "";
	
        @Override
	public String getText() {
		return text;
	}
        @Override
	public void setText(String text)
	{
		this.text = text;
		super.setText(text);
	}

	/**
	 * @return the base
	 */
	public URL getBase() {
		return base;
	}

	/**
	 * @param base the base to set
	 */
	public void setBase(URL base) {
		URL o = this.base;
		this.base = base;
		getHTMLDocument().setBase(base);
		if( (o == null && base != null) || (o != null && !o.equals(base)) )
			setText(text);
	}
	
	
	
	
}
