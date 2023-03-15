package fi.beans.numworxlf;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.UIManager;
import javax.swing.plaf.OptionPaneUI;

public class JOptionPane extends javax.swing.JOptionPane {

  static String getString(Object key, Component c) {
    Locale l = (c == null) ? Locale.getDefault() : c.getLocale();
    return UIManager.getString(key, l);
  }

  private static int styleFromMessageType(int messageType) {
    switch (messageType) {
    case ERROR_MESSAGE:
        return JRootPane.ERROR_DIALOG;
    case QUESTION_MESSAGE:
        return JRootPane.QUESTION_DIALOG;
    case WARNING_MESSAGE:
        return JRootPane.WARNING_DIALOG;
    case INFORMATION_MESSAGE:
        return JRootPane.INFORMATION_DIALOG;
    case PLAIN_MESSAGE:
    default:
        return JRootPane.PLAIN_DIALOG;
    }
}
  static Window getWindowForComponent(Component parentComponent)
      throws HeadlessException {
      if (parentComponent == null)
          return getRootFrame();
      if (parentComponent instanceof Frame || parentComponent instanceof Dialog)
          return (Window)parentComponent;
      return getWindowForComponent(parentComponent.getParent());
  }

  private JDialog createDialog(Component parentComponent, String title,
                               int style)
                               throws HeadlessException {

                           final JDialog dialog;

                           Window window = getWindowForComponent(parentComponent);
                           if (window instanceof Frame) {
                               dialog = new JDialog((Frame)window, title, true);
                           } else {
                               dialog = new JDialog((Dialog)window, title, true);
                           }
//                           if (window instanceof SwingUtilities.SharedOwnerFrame) {
//                               WindowListener ownerShutdownListener =
//                                       SwingUtilities.getSharedOwnerFrameShutdownListener();
//                               dialog.addWindowListener(ownerShutdownListener);
//                           }
                           initDialog(dialog, style, parentComponent);
                           return dialog;
                       }
  private void initDialog(final JDialog dialog, int style, Component parentComponent) {
    dialog.setComponentOrientation(this.getComponentOrientation());
    Container contentPane = dialog.getContentPane();

    contentPane.setLayout(new BorderLayout());
    contentPane.add(this, BorderLayout.CENTER);
    dialog.setResizable(false);
    if (JDialog.isDefaultLookAndFeelDecorated()) {
        boolean supportsWindowDecorations =
          UIManager.getLookAndFeel().getSupportsWindowDecorations();
        if (supportsWindowDecorations) {
            dialog.setUndecorated(true);
            getRootPane().setWindowDecorationStyle(style);
        }
    }
    dialog.pack();
    dialog.setLocationRelativeTo(parentComponent);

    final PropertyChangeListener listener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent event) {
            // Let the defaultCloseOperation handle the closing
            // if the user closed the window without selecting a button
            // (newValue = null in that case).  Otherwise, close the dialog.
            if (dialog.isVisible() && event.getSource() == JOptionPane.this &&
                    (event.getPropertyName().equals(VALUE_PROPERTY)) &&
                    event.getNewValue() != null &&
                    event.getNewValue() != JOptionPane.UNINITIALIZED_VALUE) {
                dialog.setVisible(false);
            }
        }
    };

    WindowAdapter adapter = new WindowAdapter() {
        private boolean gotFocus = false;
        public void windowClosing(WindowEvent we) {
            setValue(null);
        }

        public void windowClosed(WindowEvent e) {
            removePropertyChangeListener(listener);
            dialog.getContentPane().removeAll();
        }

        public void windowGainedFocus(WindowEvent we) {
            // Once window gets focus, set initial focus
            if (!gotFocus) {
                selectInitialValue();
                gotFocus = true;
            }
        }
    };
    dialog.addWindowListener(adapter);
    dialog.addWindowFocusListener(adapter);
    dialog.addComponentListener(new ComponentAdapter() {
        public void componentShown(ComponentEvent ce) {
            // reset value to ensure closing works properly
            setValue(JOptionPane.UNINITIALIZED_VALUE);
        }
    });

    addPropertyChangeListener(listener);
}

  public static void showMessageDialog(Component parent, Object message) {
    showMessageDialog(parent, message, getString("OptionPane.messageDialogTitle", parent),
    javax.swing.JOptionPane.INFORMATION_MESSAGE);
  }
  
  public static void showMessageDialog(Component parent, Object message, String title, int type) {
    showMessageDialog(parent, message, title, type, null);
  }
  
  public static void showMessageDialog(Component parent, Object message, String title, int type, Icon icon) {
    showOptionDialog(parent, message, title, javax.swing.JOptionPane.DEFAULT_OPTION,
     type, icon, null, null);
  }
  
  public static int showConfirmDialog(Component parent, Object message, String title, int type) {
    return showConfirmDialog(parent, message, title, type,javax.swing.JOptionPane.QUESTION_MESSAGE);
  }
 
  public static int showConfirmDialog(Component parent, Object message, String title, int optionType, int messageType) {
    return showConfirmDialog(parent, message, title, optionType,
    messageType, null);
  }
 
  public static int showConfirmDialog(Component parent, Object message, String title, int optionType, int messageType, Icon icon) {
    return showOptionDialog(parent, message, title, optionType,
    messageType, icon, null, null);
  }
  
  
  public static int showOptionDialog(Component parent, Object message, String title, int optionType, int messageType, Icon icon, Object[] options, Object initialValue) {
    JOptionPane             pane = new JOptionPane(message, messageType,
                                                   optionType, icon,
                                                   options, initialValue);
    
    pane.setInitialValue(initialValue);
    pane.setComponentOrientation(((parent == null) ?
        javax.swing.JOptionPane.getRootFrame() : parent).getComponentOrientation());
    
    int style = styleFromMessageType(messageType);
    JDialog dialog = pane.createDialog(parent, title, style);
    
    pane.selectInitialValue();
    dialog.setVisible(true);
    dialog.dispose();
    
    Object        selectedValue = pane.getValue();
    
    if(selectedValue == null)
        return javax.swing.JOptionPane.CLOSED_OPTION;
    if(options == null) {
        if(selectedValue instanceof Integer)
            return ((Integer)selectedValue).intValue();
        return javax.swing.JOptionPane.CLOSED_OPTION;
    }
    for(int counter = 0, maxCounter = options.length;
        counter < maxCounter; counter++) {
        if(options[counter].equals(selectedValue))
            return counter;
    }
    return javax.swing.JOptionPane.CLOSED_OPTION;
  }
 
  
  
  
  
  
  public JOptionPane() {
  }

  public JOptionPane(Object message) {
    super(message);
  }

  public JOptionPane(Object message, int messageType) {
    super(message, messageType);
  }

  public JOptionPane(Object message, int messageType, int optionType) {
    super(message, messageType, optionType);
  }

  public JOptionPane(Object message, int messageType, int optionType, Icon icon) {
    super(message, messageType, optionType, icon);
 }

  public JOptionPane(Object message, int messageType, int optionType, Icon icon, Object[] options) {
    super(message, messageType, optionType, icon, options);
  }

  public JOptionPane(Object message, int messageType, int optionType, Icon icon, Object[] options,
      Object initialValue) {
    super(message, messageType, optionType, icon, options, initialValue);
  }

  /* (non-Javadoc)
   * @see javax.swing.JOptionPane#updateUI()
   */

  @Override
  public void updateUI() {
    setUI(NumworxOptionPaneUI.createUI(this));
  }

}
