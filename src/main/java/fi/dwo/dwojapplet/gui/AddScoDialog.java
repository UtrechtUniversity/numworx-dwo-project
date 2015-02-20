// Source file: C:\\parameters\\fi\\dwo\\client\\gui\\AddScoDialog.java

package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.AppletConfig;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Sco;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;


/**
 * This class shows in a dialog a list with all the possible <code>AppletConfig</code>'s.<br>
 * The user can choose one AppletConfig and at a confirm the <code>ScoNameDialog</code> is showed.
 * 
 * @author M.J.B. Kupers
 *
 */
public class AddScoDialog extends JDialog implements ActionListener,
        WindowListener {
	
	class ScoTable extends JPanel implements Scrollable {

		
		/**
		 * @param layout
		 */
		public ScoTable(LayoutManager layout) {
			super(layout);
		}

                @Override
		public Dimension getPreferredScrollableViewportSize() {
			int h;
			if(getComponentCount() == 0)
				h = 300;
			else 
				h = 12*getComponent(0).getPreferredSize().height;
			return new Dimension(585, h);
		}

                @Override
		public int getScrollableBlockIncrement(Rectangle visibleRect,
				int orientation, int direction) {
			if(orientation == SwingConstants.VERTICAL)
			{
				int height2 = visibleRect.height;
				if(getComponentCount() != 0) {
					int h = getComponent(0).getHeight();
					if(height2 > h)
						height2 -= height2 % h;
					else 
						height2 = h;
				} 
				return height2;
			}
			return visibleRect.width;
		}

                @Override
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}

                @Override
		public boolean getScrollableTracksViewportWidth() {
			return false;
		}

                @Override
		public int getScrollableUnitIncrement(Rectangle visibleRect,
				int orientation, int direction) {
			if(orientation == SwingConstants.VERTICAL && getComponentCount()!= 0)
				return getComponent(0).getHeight();
			return 20;
		}
		
	}
	
	
    private AppletConfig[] appletConfigs;
    private AppletConfig[] selectedConfigs;

    private JButton okButton;

    private JButton cancelButton;

    private JButton previewButton;

    private Hashtable radioApplet;

    private JPanel table;
    private JLabel tableTitle;

    private LinkedLabel allItems;

    private boolean confirmed;
    
    private AppletConfig selectedAppletConfig;
    
    private ButtonGroup checkboxGroup;

    /** 
     * Box om '(x) standaard (o) eigen act' aan toe te voegen.
     */
    private Box titleBox;

    /**
     * Creates a new AddScoDialog.
     * @param owner The parent component of the dialog.
     * @param appletConfigs The different appletConfigs which can be chosen.
     */
    public AddScoDialog(Component owner, AppletConfig[] appletConfigs) {
        super(DwoHelper.getFrameForComponent(owner),
                TextMapper.getText(TextMapper.GUISDLG_TTL_ADD_SCO), true);
        JPanel contentPane = new JPanel(null);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        	
        setContentPane(contentPane);  // box is nooit opaque, 
        getContentPane().setBackground(GuiConstants.MAIN_BACKGROUND);
        
        setBackground(GuiConstants.MAIN_BACKGROUND);
//        contentPane.setSize(600, 380-TOP-BOTTOM);
//        contentPane.setPreferredSize(contentPane.getSize());
        confirmed = false;
        this.appletConfigs = appletConfigs;
        selectedConfigs = appletConfigs;
        titleBox = Box.createHorizontalBox();
        titleBox.add(Box.createRigidArea(new Dimension(10,20)));
        JLabel l = new JLabel(TextMapper.getText(TextMapper.GUISDLG_MSG_SELECT_SCO) + ":");
        l.setFont(GuiConstants.RESULTS_HEADER_TEXT);

        titleBox.add(l);
        titleBox.add(Box.createHorizontalGlue());
        contentPane.add(titleBox);
        
        Box panel = Box.createHorizontalBox();
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.black));
        panel.add(Box.createRigidArea(new Dimension(10,20)));
        contentPane.add(panel);
        l = new JLabel(TextMapper.getText(TextMapper.GUISDLG_SHOW) + ": ");
        l.setFont(GuiConstants.NORMAL_TEXT);
        panel.add(l);
        panel.add(Box.createHorizontalGlue());
        LinkedLabel ll = new LinkedLabel(TextMapper.getText(TextMapper.GUISDLG_ALL));
        allItems = ll;
        ll.setFont(GuiConstants.NORMAL_TEXT);
        ll.addActionListener(this);
        panel.add(ll);
        
        String s;
        for(int i = 65; i < 91; i++) {
        	panel.add(Box.createHorizontalGlue());
            l = new JLabel("-");
            l.setFont(GuiConstants.NORMAL_TEXT);
            panel.add(l);
        	panel.add(Box.createHorizontalGlue());

            s = "" + ((char) i);
            ll = new LinkedLabel(s);
            ll.setFont(GuiConstants.NORMAL_TEXT);
            ll.addActionListener(this);
            panel.add(ll);
        }
        panel.add(Box.createHorizontalStrut(10));
        
        table = new ScoTable(null);
        table.setBackground(GuiConstants.MAIN_BACKGROUND);
        table.setLayout(new BoxLayout(table, BoxLayout.Y_AXIS));
        tableTitle = new JLabel();
        tableTitle.setText(allItems.getText() + ":");
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(table.getBackground());
        JPanel jp = new JPanel(new BorderLayout());
        jp.setBackground(GuiConstants.MAIN_BACKGROUND);
        jp.add(tableTitle, BorderLayout.NORTH);
        jp.add(scrollPane, BorderLayout.CENTER);
        jp.setBorder(BorderFactory.createEmptyBorder(4, 10, 2, 5));
		contentPane.add(jp);
        selectChar(null);
        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add(Box.createHorizontalStrut(10));
        /* Preview Button */
        previewButton = new JButton(TextMapper
                .getText(TextMapper.GUISDLG_BTN_PREVIEW_SCO));
        previewButton.addActionListener(this);
        buttonBox.add(previewButton);

        buttonBox.add(Box.createHorizontalGlue());
        /* Cancel button */
        cancelButton = new JButton(TextMapper.getText(TextMapper.BTN_CANCEL));
        cancelButton.addActionListener(this);
        buttonBox.add(cancelButton);
        buttonBox.add(Box.createHorizontalStrut(20));

        /* Ok button */
        okButton = new JButton(TextMapper
                .getText(TextMapper.GUISDLG_BTN_ADD_SCO));
        okButton.addActionListener(this);
        buttonBox.add(okButton);
        buttonBox.add(Box.createHorizontalStrut(10));
        contentPane.add(buttonBox);
        contentPane.add(Box.createVerticalStrut(2));
        // set location to center of parent
        int x = 0;
        int y = 0;

        Point p = owner != null ? owner.getLocation() : new Point(0, 0);
        Dimension parentSize = owner != null ? owner.getSize() : Toolkit
                .getDefaultToolkit().getScreenSize();
        Dimension mySize = getSize();
        x = p.x + (parentSize.width - mySize.width) / 2;
        y = p.y + (parentSize.height - mySize.height) / 2;

        setLocation(x, y);
        this.addWindowListener(this);
        pack();
    }
    
    
    /**
     * Een dialogbox met twee extra radiobuttons voor eigen en algemene activiteiten.
     * 
     * @param owner
     * @param ac
     * @param acT
     */
    public AddScoDialog(Component owner, final AppletConfig[] ac, final AppletConfig[] acT)
    {
        this(owner, ac);
// Swap tussen eigen en algemene activiteiten.
        ButtonGroup swap = new ButtonGroup();
        Container contentPane = (Container) getComponent(0);
// FIXME: i18n
        final JRadioButton algemeen = new JRadioButton(TextMapper.getText(TextMapper.GUISDLG_RB_STANDARD_SCOS));//standaard
        final JRadioButton eigen    = new JRadioButton(TextMapper.getText(TextMapper.GUISDLG_RB_OWN_SCOS));//eigen activiteiten
        algemeen.setBackground(GuiConstants.MAIN_BACKGROUND);
        eigen.setBackground(GuiConstants.MAIN_BACKGROUND);
        swap.add(algemeen);
        swap.add(eigen);
        algemeen.setSelected(true);
        titleBox.remove(titleBox.getComponentCount()-1); // remove Glue
        titleBox.add(Box.createHorizontalStrut(30));
        titleBox.add(algemeen);
        titleBox.add(Box.createHorizontalStrut(15));
        titleBox.add(eigen);
        titleBox.add(Box.createHorizontalGlue());
        ItemListener listener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                if(eigen == e.getSource())
                {
                    AddScoDialog.this.appletConfigs = acT;
                 } else { 
                    AddScoDialog.this.appletConfigs = ac;
                }
                selectChar(null); // impliciet Alles?
            } 
        };
        eigen.addItemListener(listener);
        algemeen.addItemListener(listener);
    }

    /**
     * Show only the AppletConfigs where the name starts with the specified String.
     * @param s The string to compare with the AppletConfig name. If the name starts with s, the AppletConfig is showed.
     */
    private void selectChar(String s) {
        /*
         * Every radiobutton maps an appletconfig. So if we know the
         * radiobutton, we know the corresponding appletconfig
         */
        radioApplet = new Hashtable();
        
        table.removeAll();
        table.invalidate();
        
        JRadioButton cb;
        Component[] cmpArr;
        checkboxGroup = new ButtonGroup();
        if(s == null) {
            selectedConfigs = appletConfigs;
        } else {
            selectedConfigs = new AppletConfig[0];
            AppletConfig[] tmp;
            s = s.toLowerCase();
            for(int i = 0; i < appletConfigs.length; i++) {
                if(appletConfigs[i].getName().toLowerCase().startsWith(s)) {
                    /* Create a larger array and add the item */
                    tmp = new AppletConfig[selectedConfigs.length + 1];
                    System.arraycopy(selectedConfigs, 0, tmp, 0, selectedConfigs.length);
                    tmp[tmp.length - 1] = appletConfigs[i];
                    selectedConfigs = tmp;
                }
            }
        }

        for (int i = 0; i < selectedConfigs.length; i++) {
            cb = new JRadioButton(selectedConfigs[i].getName(), false);
            cb.setBackground(GuiConstants.MAIN_BACKGROUND);
            checkboxGroup.add(cb);
            cb.setFont(GuiConstants.NORMAL_TEXT);
            radioApplet.put(cb.getModel(), selectedConfigs[i]);
            table.add(cb);
        }
        Container parent = table.getParent();
        if(parent != null) {
        	parent.validate();
        	parent.repaint();
        }
    }

    /**
     * Creates a new Sco in the specified Course.
     * @param course The course where the new Sco must be created.
     * @return The new Sco.
     * @deprecated gebruik {@link #addSco(Component, Course)}
     */
    public static Sco addSco(Course course) {
        return addSco(null, course);
    }
    
    /**
     * Creates a new Sco in the specified Course.
     * @param owner The parent of the dialog witch is showed to create the new Sco.
     * @param course The course where the new Sco must be created.
     * @return The new Sco.
     */
    public static Sco addSco(Component owner, Course course) {
        AppletConfig[] ac = GuiCreator.instance().getAppletConfig();
        AppletConfig[] acT = null;//GuiCreator.instance().getAppletConfigFromTeacher();
        if((ac == null) || (ac.length == 0)) {
            JOptionPane.showMessageDialog(owner, TextMapper.getText(TextMapper.GUISDLG_MSG_NO_APPLETS));
            return null;
        } else {
            //String title = TextMapper.getText(TextMapper.GUISDLG_TTL_ADD_SCO);
            AddScoDialog asd;
            if(acT == null || acT.length == 0)
                asd = new AddScoDialog(owner, ac);
            else 
                asd = new AddScoDialog(owner, ac, acT);
            asd.show();
            if(asd.isConfirmed()) {
                AppletConfig selected = asd.getSelectedAppletConfig();
                if(asd != null) {
                    Sco s = ScoNameDialog.addSco(owner, course, selected);
                    return s;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
        @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelButton) {
            this.setVisible(false);
        } else if (e.getSource() == okButton) {
            AppletConfig ac = getSelectedAppletConfig();
            if(ac != null) {
                confirmed = true;
                this.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, TextMapper.getText(TextMapper.GUISDLG_MSG_NO_SELECTION));
            }
        } else if (e.getSource() == previewButton) {
            AppletConfig ac = getSelectedAppletConfig();
            if(ac != null) {
	            ScoPanel sp = GuiCreator.instance().previewSco(ac);
	            ScoDialog.showScoPreview(this, sp);
            } else {
                JOptionPane.showMessageDialog(this, TextMapper.getText(TextMapper.GUISDLG_MSG_NO_SELECTION));
            }
        } else if (e.getSource() instanceof LinkedLabel) {
            tableTitle.setText(((LinkedLabel) e.getSource()).getText() + ":");
            if(e.getSource() == allItems) {
                selectChar(null);
            } else {
                String s = ((LinkedLabel) e.getSource()).getText();
                selectChar(s);
            }
        }

    }

    /**
     * Invoked when the window is set to be the user's active window, which
     * means the window (or one of its subcomponents) will receive keyboard
     * events.
     * 
     * @param e
     *            The WindowEvent.
     * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
     */
        @Override
    public void windowActivated(WindowEvent e) {
   }

    /**
     * Invoked when a window has been closed as the result of calling dispose on
     * the window.
     * 
     * @param e
     *            The WindowEvent.
     * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
     */
        @Override
    public void windowClosed(WindowEvent e) {
    }

    /**
     * Invoked when the user attempts to close the window from the window's
     * system menu. If the program does not explicitly hide or dispose the
     * window while processing this event, the window close operation will be
     * cancelled.
     * 
     * @param e
     *            The WindowEvent.
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
        @Override
    public void windowClosing(WindowEvent e) {
        setVisible(false);
        dispose();
    }

    /**
     * Invoked when a window is no longer the user's active window, which means
     * that keyboard events will no longer be delivered to the window or its
     * subcomponents.
     * 
     * @param e
     *            The WindowEvent.
     * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
     */
        @Override
    public void windowDeactivated(WindowEvent e) {
    }

    /**
     * Invoked when a window is changed from a minimized to a normal state.
     * 
     * @param e
     *            The WindowEvent.
     * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
     */
        @Override
    public void windowDeiconified(WindowEvent e) {
    }

    /**
     * Invoked when a window is changed from a minimized to a normal state.
     * 
     * @param e
     *            The WindowEvent.
     * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
     */
        @Override
    public void windowIconified(WindowEvent e) {
    }

    /**
     * Invoked when a window is changed from a normal to a minimized state. For
     * many platforms, a minimized window is displayed as the icon specified in
     * the window's iconImage property.
     * 
     * @param e
     *            The WindowEvent.
     * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
     */
        @Override
    public void windowOpened(WindowEvent e) {
    }

    /**
     * @return Returns the selectedAppletConfig.
     */
    public AppletConfig getSelectedAppletConfig() {
        ButtonModel cb = checkboxGroup.getSelection();
        selectedAppletConfig = null;
        if(cb != null) {
            selectedAppletConfig = (AppletConfig) radioApplet.get(cb);
        }
        //System.out.println("Selected appletConfig: " + selectedAppletConfig.getAppletID() + "; " + selectedAppletConfig.getName() + "; " + selectedAppletConfig.getLaunchdata());
        return selectedAppletConfig;
    }
    /**
     * @return Returns the confirmed.
     */
    public boolean isConfirmed() {
        return confirmed;
    }
    /**
     * @param confirmed The confirmed to set.
     */
    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}