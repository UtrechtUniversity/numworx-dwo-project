package fi.beans.mathkit;

import java.net.URL;
import java.util.Stack;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLDocument.HTMLReader;
import javax.swing.text.html.HTMLEditorKit;

public class FormuleDocument extends HTMLDocument {

    public FormuleDocument() {

    }

    // --- AbstractDocument methods ----------------------------
    @Override
    protected void insertUpdate(DefaultDocumentEvent chng, AttributeSet attr) {
        super.insertUpdate(chng, attr);
    }

    /**
     * Updates any document structure as a result of text removal. This will
     * happen within a write lock. The superclass behavior of updating the line
     * map is executed followed by placing a lexical update command on the
     * analyzer queue.
     *
     * @param chng the change event
     */
    @Override
    protected void removeUpdate(DefaultDocumentEvent chng) {
        super.removeUpdate(chng);

        // update comment marks
    }

    @Override
    protected Element createBranchElement(Element parent, AttributeSet a) {

        return super.createBranchElement(parent, a);
    }

    @Override
    protected Element createLeafElement(Element parent, AttributeSet a, int p0,
            int p1) {

        return super.createLeafElement(parent, a, p0, p1);
    }

    /**
     * Fetches the reader for the parser to use when loading the document with
     * HTML. This is implemented to return an instance of
     * <code>HTMLDocument.HTMLReader</code>. Subclasses can reimplement this
     * method to change how the document gets structured if desired. (For
     * example, to handle custom tags, or structurally represent character style
     * elements.)
     *
     * @param pos the starting position
     * @return the reader used by the parser to load the document
     */
    @Override
    public HTMLEditorKit.ParserCallback getReader(int pos) {
        Object desc = getProperty(Document.StreamDescriptionProperty);
        if (desc instanceof URL) {
            setBase((URL) desc);
        }
        FormuleReader reader = new FormuleReader(pos);
        return reader;
    }

    public static class MTag extends HTML.UnknownTag {

        MTag(String arg0) {
            super(arg0);
        }

    }
    public static final HTML.Tag MATH = new MTag("math");
    public static final HTML.Tag MN = new MTag("mn");
    public static final HTML.Tag MI = new MTag("mi");
    public static final HTML.Tag MO = new MTag("mo");
    public static final HTML.Tag MFRAC = new MTag("mfrac");
    public static final HTML.Tag MSQRT = new MTag("msqrt");
    public static final HTML.Tag MROW = new MTag("mrow");
    public static final HTML.Tag MSUP = new MTag("msup");
    public static final HTML.Tag MSUB = new MTag("msub");
    public static final HTML.Tag MSUBSUP = new MTag("msubsup");
    public static final HTML.Tag MFENCED = new MTag("mfenced");
    public static final HTML.Tag MTEXT = new MTag("mtext");
    public static final HTML.Tag MROOT = new MTag("mroot");
    public static final HTML.Tag MUNDER = new MTag("munder");
    public static final HTML.Tag MOVER = new MTag("mover");
    public static final HTML.Tag MUNDEROVER = new MTag("munderover");
    public static final HTML.Tag MSPACE = new MTag("mspace"); // ???
    public static final HTML.Tag MNONE = new MTag("none");
    public static final HTML.Tag MMULTISCRIPTS = new MTag("mmultiscripts");
    public static final HTML.Tag MPRESCRIPTS = new MTag("mprescripts");
    public static final HTML.Tag MTABLE = new MTag("mtable");
    public static final HTML.Tag MTR = new MTag("mtr");
    public static final HTML.Tag MTD = new MTag("mtd");

    public class FormuleReader extends HTMLReader {

        private static final float SUBSUPSCALE = 0.8f;

        int orgSize = 12, firstSize = 0;

        class MCharacterAction extends CharacterAction {

            MutableAttributeSet extra;

            @Override
            public void end(Tag t) {
                super.end(t);
                orgSize = popSize();
            }

            @Override
            public void start(Tag t, MutableAttributeSet attr) {
                pushSize(orgSize);
                super.start(t, attr);
                charAttr.addAttribute(MATH, t);

                if (firstSize != 0) {
//System.err.println(t + " first " + firstSize);
                    StyleConstants.setFontSize(charAttr, firstSize);
                    firstSize = 0;
                } else {
//System.err.println(t + " next " + orgSize);
                    StyleConstants.setFontSize(charAttr, orgSize);
                }

                if (extra != null) {
                    charAttr.addAttributes(extra);
                }
            }
        }

        private final Object ENDTAG = HTML.Attribute.ENDTAG; // TODO

        private Stack sizeStack = new Stack();

        private int popSize() {
            return ((Number) sizeStack.pop()).intValue();
        }

        private void pushSize(int v) {
            sizeStack.push(v);
        }

        FormuleReader(int offset, int popDepth, int pushDepth, Tag insertTag) {
            super(offset, popDepth, pushDepth, insertTag);
            
        }

        FormuleReader(int offset) {
            super(offset);
            HTMLReader.CharacterAction ca = new MCharacterAction();

            HTMLReader.BlockAction ba = new HTMLReader.BlockAction() {

                @Override
                public void end(Tag t) {
                    super.end(t);
                    orgSize = popSize();
                }

                @Override
                public void start(Tag t, MutableAttributeSet attr) {
                    pushSize(orgSize);
                    if (firstSize != 0) {
                        StyleConstants.setFontSize(attr, firstSize);
                        orgSize = firstSize;
                        firstSize = 0;

                    } else {
                        StyleConstants.setFontSize(attr, orgSize);
                    }

                    attr.addAttribute(ContentElementName, t.toString());
                    super.start(t, attr);
                }
            };
            HTMLReader.BlockAction subpa = new HTMLReader.BlockAction() {
                @Override
                public void start(Tag t, MutableAttributeSet attr) {
                    pushSize(orgSize);
                    if (firstSize == 0) {
                        firstSize = orgSize;
                        orgSize = Math.round(orgSize * SUBSUPSCALE);
                    }
                    StyleConstants.setFontSize(attr, firstSize);
                    super.start(t, attr);
                }

                @Override
                public void end(Tag t) {
                    super.end(t);
                    orgSize = popSize();
                }
            };

            MCharacterAction mi = new MCharacterAction();
            mi.extra = new SimpleAttributeSet();
            StyleConstants.setItalic(mi.extra, true);

            registerTag(MATH, ba);
            registerTag(MN, ca);
            registerTag(MI, mi);
            registerTag(MFRAC, ba);
            registerTag(MROW, ba);
            registerTag(MSQRT, ba);
            registerTag(MSUP, subpa);
            registerTag(MSUB, subpa);
            registerTag(MFENCED, ba);
            registerTag(MO, ca);
            registerTag(MTEXT, ca);
            registerTag(MSUBSUP, subpa);
            registerTag(MROOT, subpa);
            registerTag(MUNDER, subpa);
            registerTag(MOVER, subpa);
            registerTag(MUNDEROVER, subpa);
            registerTag(MSPACE, ba);
            registerTag(MTABLE, ba);
            registerTag(MTR, ba);
            registerTag(MTD, ba);
            registerTag(MNONE, ba);
            registerTag(MMULTISCRIPTS, subpa);
            registerTag(MPRESCRIPTS, ba);
        }

        @Override
        protected void addSpecialElement(Tag t, MutableAttributeSet a) {
            //System.err.println("UNKNOWN TAG: "  + t + " " + a);
            super.addSpecialElement(t, a);
        }

        @Override
        public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
            if (t instanceof HTML.UnknownTag) // TODO betere test voor ...
            {
                if (MATH.equals(t)) {
                    orgSize = getFont(a).getSize();
                }

                if (MSPACE.equals(t) || MNONE.equals(t) || MPRESCRIPTS.equals(t)) // empty tag 
                {
                    if (!"true".equals(a.getAttribute(ENDTAG))) // remove endtag
                    {
                        handleStartTag(t, a, pos);
                        handleEndTag(t, pos);
                    }
                } else if ("true".equals(a.getAttribute(ENDTAG))) {
                    handleEndTag(t, pos);
                } else {
                    handleStartTag(t, a, pos);
                }
                return;
            }
            super.handleSimpleTag(t, a, pos);
        }

    }
}
