// Source file: C:\\fi\\dwo\\parameters\\gui\\DoubleTextField.java

package fi.dwo.parameters.gui;

import java.awt.AWTEvent;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.TextEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JTextField;

public class DoubleTextField extends JTextField {

    String oldText = null;

    public DoubleTextField(String text, int columns) {
        super(text, columns);
        enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.TEXT_EVENT_MASK);
        oldText = getText();
    }

    public DoubleTextField(String text) {
        super(text);
        enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.TEXT_EVENT_MASK);
        oldText = getText();
    }

    public DoubleTextField() {
        super();
        enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.TEXT_EVENT_MASK);
        oldText = getText();
    }

    // Consume non-digit KeyTyped events
    // Note that processTextEvent kind of eliminates the need for this
    // function, but this is neater, since ideally, it would prevent
    // the text from appearing at all.  Sigh.  See bugid 4100317/4114565.
    //
    protected void processEvent(AWTEvent evt) {
        int id = evt.getID();
        if (id != KeyEvent.KEY_TYPED) {
            super.processEvent(evt);
            return;
        }

        KeyEvent kevt = (KeyEvent) evt;
        char c = kevt.getKeyChar();

        boolean isDecimalChar = (c == '\u002c') || (c == '\u002e');
        
        if(isDecimalChar) {
            
            c = new DecimalFormatSymbols(Locale.getDefault()).getDecimalSeparator();
            kevt.setKeyChar(c);
        }
        
        // Digits, backspace, and delete are okay
        // Note that the minus sign is allowed, but not the decimal
        if (Character.isDigit(c) || (c == '\b') || (c == '\u007f') || 
            (c == '\u002d') || isDecimalChar) {
            super.processEvent(evt);
            return;
        }

        Toolkit.getDefaultToolkit().beep();
        kevt.consume();
    }

    // Should consume TextEvents for non-double Strings
    // Store away the text in the tf for every TextEvent
    // so we can revert to it on a TextEvent (paste, or 
    // legal key in the wrong location) with bad text
    //
    protected void processTextEvent(TextEvent te) {
        // The empty string is okay, too
        String newText = getText();
        if (newText.equals("") || textIsDouble(newText)) {
            oldText = newText;
            //super.processTextEvent(te);
            return;
        }
        Toolkit.getDefaultToolkit().beep();
        setText(oldText);
    }

    // Returns true for Doubles (zero and negative 
    // values are allowed).
    // Note that the empty string is not allowed. 
    // 
    private boolean textIsDouble(String textToCheck) {
        double value = -1;

        char c = ',';
        NumberFormat nf = DecimalFormat.getInstance();
        if(nf instanceof DecimalFormat) {
            c = ((DecimalFormat) nf).getDecimalFormatSymbols().getDecimalSeparator();
        }
        textToCheck = textToCheck.replace(c, '.');
        
        try {
            value = Double.parseDouble(textToCheck);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    
    public double getDoubleValue() {
        double value = -1;

        char c = ',';
        NumberFormat nf = DecimalFormat.getInstance();
        if(nf instanceof DecimalFormat) {
            c = ((DecimalFormat) nf).getDecimalFormatSymbols().getDecimalSeparator();
        }
        String s = getText().replace(c, '.');
        try {
            value = Double.parseDouble(s);
            return value;
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }
}
