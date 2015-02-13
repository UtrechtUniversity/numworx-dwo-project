/*
 * Created on Apr 11, 2005
 *
 */
package fi.dwo.dwojapplet.parameters.gui;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.TextEvent;

import javax.swing.JTextField;

/**
 * TODO paste events.
 * @author M.J.B. Kupers
 * @deprecated gebruik JFormattedTextField
 */
public class IntegerTextField extends JTextField {

    String oldText = null;

    public IntegerTextField(String text, int columns) {
        super(text, columns);
        enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.TEXT_EVENT_MASK);
        oldText = getText();
    }

    public IntegerTextField(String text) {
        super(text);
        enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.TEXT_EVENT_MASK);
        oldText = getText();
    }

    public IntegerTextField() {
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

        // Digits, backspace, and delete are okay
        // Note that the minus sign is allowed, but not the decimal
        if (Character.isDigit(c) || (c == '\b') || (c == '\u007f') || 
            (c == '\u002d')) {
            super.processEvent(evt);
            return;
        }

        Toolkit.getDefaultToolkit().beep();
        kevt.consume();
    }

    // Should consume TextEvents for non-integer Strings
    // Store away the text in the tf for every TextEvent
    // so we can revert to it on a TextEvent (paste, or 
    // legal key in the wrong location) with bad text
    //
    protected void processTextEvent(TextEvent te) {
        // The empty string is okay, too
        String newText = getText();
        if (newText.equals("") || textIsInteger(newText)) {
            oldText = newText;
            //super.processTextEvent(te);
            return;
        }
        Toolkit.getDefaultToolkit().beep();
        setText(oldText);
    }

    // Returns true for Integers (zero and negative 
    // values are allowed).
    // Note that the empty string is not allowed. 
    // 
    private boolean textIsInteger(String textToCheck) {
        int value = -1;

        try {
            value = Integer.parseInt(textToCheck, 10);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    
    public int getIntValue() {
        int value = -1;

        try {
            value = Integer.parseInt(getText(), 10);
            return value;
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }
}
