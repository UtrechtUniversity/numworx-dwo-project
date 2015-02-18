/**
 *
 */
package fi.beans.mathkit;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 *
 * @author Wim
 *
 */
public class MathKit extends HTMLEditorKit {

    public MathKit() {
        super();
    }

    // TODO Why are private functions now public?
    public FormuleContext getContext() {
        if (context == null) {
            context = new FormuleContext(super.getViewFactory());
        }
        return context;
    }

    // TODO Why are private functions now public?
    public void setStylePreferences(FormuleContext formuleContext) {
        context = formuleContext;
    }

    // --- EditorKit methods -------------------------
    /**
     * Get the MIME type of the data that this kit represents support for. This
     * kit supports the type <code>text/java</code>.
     *
     * @return
     */
    @Override
    public String getContentType() {
        return "text/html+mathml";
    }

    /**
     * Create a copy of the editor kit. This allows an implementation to serve
     * as a prototype for others, so that they can be quickly created.
     *
     * @return
     */
    @Override
    public Object clone() {
        MathKit kit = new MathKit();
        kit.context = context;
        return kit;
    }

    /**
     * Creates an uninitialized text storage model that is appropriate for this
     * type of editor.
     *
     * @return the model
     */
    @Override
    public Document createDefaultDocument() {
        return new FormuleDocument();
    }

    /**
     * Fetches a factory that is suitable for producing views of any models that
     * are produced by this kit. The default is to have the UI produce the
     * factory, so this method has no implementation.
     *
     * @return the view factory
     */
    @Override
    public final ViewFactory getViewFactory() {
        return getContext();
    }

    FormuleContext context;

    @Override
    protected Parser getParser() {
        return super.getParser();
    }

    @Override
    public void read(Reader in, Document doc, int pos) throws IOException,
            BadLocationException {
        super.read(in, doc, pos);
    }

// deze is niet goed voor MathML parts!
    @Override
    public void write(Writer out, Document doc, int pos, int len)
            throws IOException, BadLocationException {
        throw new Error("not implemented error");
        //super.write(out, doc, pos, len);
    }

}
