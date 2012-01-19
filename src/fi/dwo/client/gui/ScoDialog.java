/*
 * Created on Mar 9, 2005
 *
 */
package fi.dwo.client.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.MessageFormat;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.SchoolClass;
import fi.dwo.client.domain.User;
import fi.dwo.client.system.TextMapper;

/**
 * This class is a dialog that shows the sco, with the results of the specified user.
 * 
 * @author M.J.B. Kupers
 *  
 */
public class ScoDialog extends JDialog implements ActionListener, WindowListener {

    public static class ClassModel extends AbstractListModel implements ComboBoxModel {

		private User[] students;
		private Object user;

		public ClassModel(SchoolClass s, User u) {
			students = s.getStudents();
			user = u;
		}

		public Object getElementAt(int i) {
			return students[i];
		}

		public int getSize() {
			return students.length;
		}

		public Object getSelectedItem() {
			return user;
		}

		public void setSelectedItem(Object u) {
			user = u;
		}

	}

	private ScoPanel scoPanel;

    private JButton closeButton;

    /**
     * Creates a new instance of a ScoDialog. It shows the sco, made by an user.
     * 
     * @param owner The owner component of the dialog.
     * @param title The title of the dialog.
     * @param modal If true, the dialog is modal.
     * @param sp The ScoPanel witch contains the data of the sco to show.
     */
    public ScoDialog(Component owner, String windowTitle, String title, boolean modal, ScoPanel sp) {
    	this(owner, windowTitle, createTitleBox(title), modal, sp);
    }
    
    public ScoDialog(Component owner, String windowTitle, Component hbox, boolean modal, ScoPanel sp) {
    	
    	
    	
        super(DwoHelper.getFrameForComponent(owner), windowTitle, modal);
        JPanel contentPane = new JPanel(new BorderLayout(0, 5));
        contentPane.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
        setContentPane(contentPane);
        scoPanel = sp;
        contentPane.setBackground(GuiConstants.MAIN_BACKGROUND);
        closeButton = new JButton(TextMapper.getText(TextMapper.BTN_CLOSE));

        closeButton.setSize(closeButton.getPreferredSize());
        closeButton.addActionListener(this);

       // this.pack();
        Insets insets = contentPane.getInsets();

        contentPane.add(hbox, BorderLayout.NORTH);

        scoPanel.setVisible(false);
        scoPanel.setPreferredSize(scoPanel.getSize());
        contentPane.add(scoPanel, BorderLayout.CENTER);
        scoPanel.setVisible(true);

        closeButton.setLocation(insets.left + 10, scoPanel.getSize().height
                + scoPanel.getLocation().y + 10);
        closeButton.setVisible(false);
        Box hbox1 = Box.createHorizontalBox();
        hbox1.add(Box.createHorizontalStrut(10));
        hbox1.add(closeButton);
        hbox1.add(Box.createHorizontalGlue());
        contentPane.add(hbox1, BorderLayout.SOUTH);
        closeButton.setVisible(true);

        pack();
        int x = 0;
        int y = 0;

        Point p = owner != null ? owner.getLocationOnScreen() : new Point(0, 0);
        Dimension parentSize = owner != null ? owner.getSize()
                : Toolkit.getDefaultToolkit().getScreenSize();
        Dimension mySize = getSize();
        x = p.x + (parentSize.width - mySize.width) / 2;
        y = p.y + (parentSize.height - mySize.height) / 2;
        
        if(x < 0) {
            x = 0;
        }
        if(y < 0) {
            y = 0;
        }

        setLocation(x, y);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(this);
    }

	private static Box createTitleBox(String title) {
		Box hbox;
        JLabel l = new JLabel(title);
        l.setFont(GuiConstants.RED_TEXT);
        FontMetrics fm = l.getFontMetrics(l.getFont());
        hbox = Box.createHorizontalBox();
        hbox.add(Box.createRigidArea(new Dimension(10, fm.getHeight())));
        hbox.add(l);
		return hbox;
	}

    /**
     * Shows a dialog to show a result of a sco and user.
     * 
     * @param parent The parent component of the dialog.
     * @param sp The ScoPanel witch contains the data of the sco to show.
     * @param ug The usergroup, witch is used for the title.
     */
    public static void showScoDialog(Component parent, ScoPanel sp, User ug) {
        String[] arguments = { sp.getSco().getScoName(), ug.getName() };
        String title = MessageFormat.format(TextMapper.getText(TextMapper.UG_RESULTS_OF_STUDENT), arguments);
        ScoDialog sd = new ScoDialog(parent, TextMapper.getText(TextMapper.GUIRS_RESULTS), title, true, sp);
        sd.show();
    }
    
    public static void showScoDialog(Component parent, final ScoPanel sp, final User u, final SchoolClass s) {
    	String[] arguments = { sp.getSco().getScoName(), "" };
    	String title = MessageFormat.format(TextMapper.getText(TextMapper.UG_RESULTS_OF_STUDENT), arguments);
    	Box hbox = createTitleBox(title);
    	JComboBox combo = new JComboBox();
    	ComboBoxModel model = new ClassModel(s, u);
		combo.setModel(model);
		combo.setRenderer(new DefaultListCellRenderer() {

			/* (non-Javadoc)
			 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
			 */
			public Component getListCellRendererComponent(JList list,
					Object u, int arg2, boolean arg3, boolean arg4) {
				// TODO Auto-generated method stub
				u = ((User) u).getName();
				return super.getListCellRendererComponent(list, u, arg2, arg3, arg4);
			}});
		combo.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent event) {
				User u = (User) event.getItem();
				switch (event.getStateChange()) {
				case ItemEvent.DESELECTED:
						sp.getSco().getApplet().stop();
						break;
				case ItemEvent.SELECTED:
						sp.getSco().setUser(u);
						sp.getSco().getApplet().start();
						sp.repaint();
						break;
				}
			}});
    	hbox.add(combo);
        ScoDialog sd = new ScoDialog(parent, TextMapper.getText(TextMapper.GUIRS_RESULTS), hbox, true, sp);
        sd.show();
    }
    
    
    public static void showScoPreview(Component parent, ScoPanel sp) {
        ScoDialog sd = new ScoDialog(parent, sp.getSco().getScoName(), "", true, sp);
        sd.show();        
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == closeButton) {
            setVisible(false);
            scoPanel.getSco().endWithoutSaving();
            dispose();
        }
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
        setVisible(false);
        scoPanel.getSco().endWithoutSaving();
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