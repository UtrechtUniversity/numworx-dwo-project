//Source file: N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\DwoMessageDialog.java

package fi.dwo.client.gui;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.StringTokenizer;

import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.system.TextMapper;

/**
 * <P>
 * OptionPane makes it easy to pop up a standard dialog box that prompts users
 * for a value or informs them of something.
 * </P>
 * 
 * <P>
 * While the OptionPane class may appear complex because of the large number of
 * methods, almost all uses of this class are one-line calls to one of the
 * static showXxxDialog methods shown below:
 * </P>
 * 
 * <P>
 * <UL>
 * <LI>showConfirmDialog asks a confirming question, like yes/no/cancel.</LI>
 * <LI>showInputDialog prompts for some input.</LI>
 * <LI>showMessageDialog tells the user about something that has happened.
 * </LI>
 * </UL>
 * </P>
 * 
 * <P>
 * This class may be freely redistributed for non-profit purposes.
 * </P>
 * 
 * @author Martin Stepp
 *         (http://www.cs.arizona.edu/~stepp/source/OptionPane.java) - tweaked
 *         by M.J.B. Kupers
 * @deprecated use javax.swing.JOptionPane
 * @see javax.swing.JOptionPane
 */

public class DwoMessageDialog extends Dialog implements ActionListener,
        ItemListener, WindowListener {
    // constants
    /**
     * This constant is returned by OptionPane.showConfirmDialog when the user
     * clicks the Yes button.
     */
    public static final int YES_OPTION = 0;

    /**
     * This constant is returned by OptionPane.showConfirmDialog when the user
     * clicks the No button.
     */
    public static final int NO_OPTION = 1;

    /**
     * This constant is returned by OptionPane.showConfirmDialog and
     * OptionPane.showInputDialog when the user clicks the Cancel button.
     */
    public static final int CANCEL_OPTION = -1;

    public static final int YES_NO_OPTION = 3;

    public static final int YES_NO_CANCEL_OPTION = 4;

    public static final int OK_OPTION = 5;

    public static final int OK_CANCEL_OPTION = 6;

    public static final int INFORMATION_MESSAGE = 7;

    public static final int QUESTION_MESSAGE = 8;

    public static final int ERROR_MESSAGE = 9;

    public static final int PLAIN_MESSAGE = 10;

    public static final int WARNING_MESSAGE = 11;

    // instance vars
    private static int CONFIRM_TYPE = 0;

    private static int INPUT_TYPE = 1;

    private static int MESSAGE_TYPE = 2;

    private static int OPTION_TYPE = 3;

    private Dialog my_dialog;

    private DwoButton my_yesButton, my_noButton, my_okButton, my_cancelButton;

    private Checkbox[] my_choiceButtons;

    private int my_type;

    private int my_selectedIndex = CANCEL_OPTION;

    private TextField my_field;

    private Object my_choice = null;

    private Object[] my_options;

    /*
     * Constructs a new OptionPane of the given type with the given Frame as its
     * parent, with the given window title, displaying the given message.
     * 
     * If parent is non-null, the OptionPane will center itself with respect to
     * the parent. Otherwise, the OptionPane will locate itself at (0, 0).
     * 
     * Acceptable types are OptionPane.CONFIRM_TYPE, OptionPane.INPUT_TYPE, and
     * OptionPane.MESSAGE_TYPE.
     */
    private DwoMessageDialog(Component parent, String message, String title,
            int type, int subtype) {
        super(DwoHelper.getFrameForComponent(parent), title, true);
        init(parent, message, title, type, subtype, null, null);
    }

    private DwoMessageDialog(Component parent, String message, String title,
            int type, int subtype, Object[] options, Object initialOption) {
        super(DwoHelper.getFrameForComponent(parent), title, true);
        init(parent, message, title, type, subtype, options, initialOption);
    }

    private void init(Component parent, String message, String title, int type,
            int subtype, Object[] options, Object initialOption) {
        my_type = type;
        my_options = options;

        // set initial option selected
        if (options != null) {
            for (int ii = 0; ii < options.length; ii++) {
                if (options[ii].equals(initialOption)) {
                    my_selectedIndex = ii;
                    my_choice = new Integer(ii);
                }
            }

            if (my_choice == null)
                my_choice = new Integer(CANCEL_OPTION);
        }
        setResizable(false);

        // construct components
        my_yesButton = new DwoButton(TextMapper.getText(TextMapper.BTN_YES));
        my_noButton = new DwoButton(TextMapper.getText(TextMapper.BTN_NO));
        my_okButton = new DwoButton(TextMapper.getText(TextMapper.BTN_OK));
        my_cancelButton = new DwoButton(TextMapper.getText(TextMapper.BTN_CANCEL));
        my_field = new TextField(10);

        // event listening
        my_yesButton.addActionListener(this);
        my_noButton.addActionListener(this);
        my_okButton.addActionListener(this);
        my_cancelButton.addActionListener(this);
        my_field.addActionListener(this);

        // layout
        Panel contentPane = new Panel(new BorderLayout());
        Panel centerPanel = new Panel();
        Panel southPanel = new Panel();
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        contentPane.setBackground(GuiConstants.MAIN_BACKGROUND);
        centerPanel.setBackground(GuiConstants.MAIN_BACKGROUND);
        southPanel.setBackground(GuiConstants.MAIN_BACKGROUND);

        Panel labelPanel = new Panel(new GridLayout(0, 1));
        StringTokenizer tokenizer = new StringTokenizer(message, "\n");
        while (tokenizer.hasMoreTokens())
            labelPanel.add(new Label(tokenizer.nextToken()));

        if (type == CONFIRM_TYPE) {
            centerPanel.add(labelPanel);
            southPanel.add(my_yesButton);
            southPanel.add(my_noButton);
            if (subtype == YES_NO_CANCEL_OPTION)
                southPanel.add(my_cancelButton);
        } else if (type == INPUT_TYPE) {
            centerPanel.setLayout(new BorderLayout());
            centerPanel.add(labelPanel, BorderLayout.CENTER);
            centerPanel.add(my_field, BorderLayout.SOUTH);
            southPanel.add(my_okButton);
            if (subtype == OK_CANCEL_OPTION)
                southPanel.add(my_cancelButton);
        } else if (type == MESSAGE_TYPE) {
            centerPanel.add(labelPanel);
            southPanel.add(my_okButton);
        } else if (type == OPTION_TYPE) {
            if (options == null)
                throw new IllegalArgumentException("null options list");

            centerPanel.setLayout(new GridLayout(0, 1));
            centerPanel.add(labelPanel);
            CheckboxGroup cbg = new CheckboxGroup();

            int numOptions = options.length;
            my_choiceButtons = new Checkbox[numOptions];
            for (int ii = 0; ii < numOptions; ii++) {
                my_choiceButtons[ii] = new Checkbox(options[ii].toString(), options[ii] == initialOption, cbg);
                my_choiceButtons[ii].addItemListener(this);
                centerPanel.add(my_choiceButtons[ii]);
            }

            southPanel.add(my_okButton);
            if (subtype == OK_CANCEL_OPTION)
                southPanel.add(my_cancelButton);
        }

        contentPane.add(centerPanel, BorderLayout.CENTER);
        contentPane.add(southPanel, BorderLayout.SOUTH);
        add(contentPane);
        pack();

        // set location to center of parent
        int x = 0;
        int y = 0;

        Point p = parent != null ? parent.getLocation() : new Point(0, 0);
        Dimension parentSize = parent != null ? parent.getSize()
                : Toolkit.getDefaultToolkit().getScreenSize();
        Dimension mySize = getSize();
        x = p.x + (parentSize.width - mySize.width) / 2;
        y = p.y + (parentSize.height - mySize.height) / 2;

        setLocation(x, y);
        this.addWindowListener(this);
    }

    /** Processes ActionEvents in this OptionPane. */
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();

        if (source == my_yesButton)
            my_choice = new Integer(YES_OPTION);
        if (source == my_noButton)
            my_choice = new Integer(NO_OPTION);
        else if (source == my_okButton || source == my_field) {
            if (my_type == OPTION_TYPE)
                my_choice = new Integer(my_selectedIndex);
            else
                my_choice = my_field.getText();
        } else if (source == my_cancelButton)
            my_choice = null;

        setVisible(false);
        dispose();
    }

    /** Processes ItemEvents in this OptionPane. */
    public void itemStateChanged(ItemEvent event) {
        Object source = event.getSource();
        for (int ii = 0; ii < my_choiceButtons.length; ii++)
            if (source == my_choiceButtons[ii]) {
                my_selectedIndex = ii;
            }
    }

    /** Shows this OptionPane and returns the resulting input, if any. */
    private Object showDialog() {
        show();
        return my_choice;
    }

    /**
     * Shows a confirmation dialog with the given message, using the given
     * component as its parent.
     * 
     * @return an integer corresponding to the button the user pressed. The
     *         integer may be one of OptionPane.YES_OPTION,
     *         OptionPane.NO_OPTION, or OptionPane.CANCEL_OPTION.
     */
    public static int showConfirmDialog(Component parent, Object message) {
        return showConfirmDialog(parent, message, TextMapper.getText(TextMapper.DLG_CONFIRM), YES_NO_CANCEL_OPTION);
    }

    public static int showConfirmDialog(Component parent, Object message,
            String title, int type) {
        Integer i = (Integer) (new DwoMessageDialog(parent, (String) message, title, CONFIRM_TYPE, type).showDialog());
        return (i != null) ? i.intValue() : CANCEL_OPTION;
    }

    /**
     * Shows a dialog asking for input, with the given message, using the given
     * component as its parent.
     * 
     * @return the input text typed by the user; null if the user presses
     *         Cancel.
     */
    public static String showInputDialog(Component parent, Object message) {
        return showInputDialog(parent, message, TextMapper.getText(TextMapper.DLG_ENTER_INPUT), OK_CANCEL_OPTION);
    }

    public static String showInputDialog(Component parent, Object message,
            String title, int type) {
        return (String) (new DwoMessageDialog(parent, (String) message, title, INPUT_TYPE, type).showDialog());
    }

    /**
     * Shows a dialog displaying the given message, using the given component as
     * its parent.
     */
    public static void showMessageDialog(Component parent, Object message) {
        showMessageDialog(parent, message, TextMapper.getText(TextMapper.DLG_MESSAGE), OK_OPTION);
    }

    public static void showMessageDialog(Component parent, Object message,
            String title, int type) {
        new DwoMessageDialog(parent, (String) message, title, MESSAGE_TYPE, type).showDialog();
    }

    public static int showOptionDialog(Component parent, String message,
            String title, int type, int unused, Object unusedIcon,
            Object[] options, Object initialValue) {
        Integer i = (Integer) new DwoMessageDialog(parent, (String) message, title, OPTION_TYPE, type, options, initialValue).showDialog();
        return (i != null) ? i.intValue() : CANCEL_OPTION;
    }

    /**
     * Invoked when the window is set to be the user's active window, which means the window (or one of its subcomponents) will receive keyboard events.
     * @param e The WindowEvent.
     * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
     */
    public void windowActivated(WindowEvent e) {
    }

    /**
     * Invoked when a window has been closed as the result of calling dispose on the window.
     * @param e The WindowEvent.
     * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
     */
    public void windowClosed(WindowEvent e) {
    }

    /**
     * Invoked when the user attempts to close the window from the window's system menu. If the program does not explicitly hide or dispose the window while processing this event, the window close operation will be cancelled.
     * @param e The WindowEvent.
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    public void windowClosing(WindowEvent e) {
        my_choice = null;

        setVisible(false);
        dispose();
    }

    /**
     * Invoked when a window is no longer the user's active window, which means that keyboard events will no longer be delivered to the window or its subcomponents.
     * @param e The WindowEvent.
     * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
     */
    public void windowDeactivated(WindowEvent e) {
    }

    /**
     * Invoked when a window is changed from a minimized to a normal state.
     * @param e The WindowEvent.
     * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
     */
    public void windowDeiconified(WindowEvent e) {
    }

    /**
     * Invoked when a window is changed from a minimized to a normal state.
     * @param e The WindowEvent.
     * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
     */
    public void windowIconified(WindowEvent e) {
    }

    /**
     * Invoked when a window is changed from a normal to a minimized state. For many platforms, a minimized window is displayed as the icon specified in the window's iconImage property.
     * @param e The WindowEvent.
     * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
     */
    public void windowOpened(WindowEvent e) {
    }

}