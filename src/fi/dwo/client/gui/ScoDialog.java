/*
 * Created on Mar 9, 2005
 *
 */
package fi.dwo.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.EventObject;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractCellEditor;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultSingleSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SingleSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import fi.beans.scorm.PartialScoreIF;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.SchoolClass;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.domain.User;
import fi.dwo.client.system.TextMapper;

/**
 * This class is a dialog that shows the sco, with the results of the specified user.
 * 
 * @author M.J.B. Kupers
 *  
 */
public class ScoDialog extends JDialog implements ActionListener, WindowListener, PropertyChangeListener {

    public static class ClassModel extends DefaultComboBoxModel {

		private Sco sco;

		public ClassModel(SchoolClass s, User u, Sco sco) {
			super(s.getStudents());
			this.sco = sco;
			setSelectedItem(u);
		}

		public List getScoreList(int i) {
			User u = (User) getElementAt(i);
			sco.setUser(u);
			return sco.getPartialScoreIF().getScoreMapList(sco);
		}
	}

	private ScoPanel scoPanel;

    private JButton closeButton;

	private JTable table;

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
        //setContentPane(contentPane);
        scoPanel = sp;
        contentPane.setBackground(GuiConstants.MAIN_BACKGROUND);
        closeButton = new JButton(TextMapper.getText(TextMapper.BTN_CLOSE));

        closeButton.setSize(closeButton.getPreferredSize());
        closeButton.addActionListener(this);

       // this.pack();
        Insets insets = contentPane.getInsets();
        
        contentPane.setOpaque(false);
        contentPane.add(hbox, BorderLayout.NORTH);
        
        scoPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
		scoPanel.setVisible(false);
        scoPanel.setPreferredSize(scoPanel.getSize());
        contentPane.add(scoPanel, BorderLayout.CENTER);
        scoPanel.setVisible(true);

        closeButton.setLocation(insets.left + 10, scoPanel.getSize().height
                + scoPanel.getLocation().y + 10);
        closeButton.setVisible(false);
        Box hbox1 = Box.createHorizontalBox();
        //hbox1.setOpaque(false);
        hbox1.add(Box.createHorizontalStrut(10));
        hbox1.add(closeButton);
        hbox1.add(Box.createHorizontalGlue());
        contentPane.add(hbox1, BorderLayout.SOUTH);
        closeButton.setVisible(true);

        
		
        JPanel basisPanel = new JPanel(new BorderLayout(0, 5));
        basisPanel.setBackground(new Color(200,227,255));
        basisPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		setContentPane(basisPanel);
        basisPanel.add(contentPane, BorderLayout.CENTER);
        
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
    	final JComboBox combo = new JComboBox();
    	final ClassModel model = new ClassModel(s, u, sp.getSco());
		Model tableModel = new Model(model, s.getName());
		final JTable table = new JTable(tableModel);
		table.setOpaque(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		combo.setModel(model);
		table.setRowSelectionInterval(combo.getSelectedIndex()+1, combo.getSelectedIndex()+1);
		DefaultListCellRenderer renderer = new DefaultListCellRenderer() {

			/* (non-Javadoc)
			 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
			 */
			public Component getListCellRendererComponent(JList list,
					Object u, int arg2, boolean arg3, boolean arg4) {
				u = ((User) u).getName();
				return super.getListCellRendererComponent(list, u, arg2, arg3, arg4);
			}};
		combo.setRenderer(renderer);
		final ItemListener itemListener = new ItemListener() {

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
						//list.setSelectedValue(u, false);
						int i = combo.getSelectedIndex()+1;
						table.setRowSelectionInterval(i, i);
						break;
				}
			}};
		combo.addItemListener(itemListener);
    	hbox.add(combo);
    	//hbox.setOpaque(false);
        ScoDialog sd = new ScoDialog(parent, TextMapper.getText(TextMapper.GUIRS_RESULTS), hbox, true, sp);
        sd.table = table;
        sp.getSco().addPropertyChangeListener(Sco.LESSON_LOCATION, sd);
        final Container content = sd.getContentPane();
        final IconizedPanel panel = new IconizedPanel("Leerlingen");
        panel.setBackground(new Color(200,227,255));
        
//panel.setOpaque(true);
//panel.setBackground(Color.green);
        JPanel vbox = new JPanel(new BorderLayout())
//        { 
//        	public Dimension getPreferredSize() { 
//        		super.getPreferredSize();
//        		return getSize();
//        	}
//        }
        ; 
        vbox.setOpaque(false);
//vbox.setBackground(Color.blue);vbox.setOpaque(true);
        JButton btn = new JButton(panel.getCloseAction());
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
		vbox.add(btn, BorderLayout.NORTH);
		vbox.add(new Mover(6), BorderLayout.EAST);
		TableUtil.setJTableSizes(table);
		int cols = table.getColumnCount();
		for(int i = 0; i<cols; i++ ) {
			TableColumn column = table.getColumnModel().getColumn(i);
			int pref = column.getPreferredWidth();
			column.setMaxWidth(pref);
			column.setMinWidth(pref);
		}
		//table.setTableHeader(null);
		table.setMaximumSize(table.getPreferredSize());
		table.setMinimumSize(table.getPreferredSize());
		JPanel x = new JPanel(new BorderLayout());
		x.setOpaque(false);
		//x.setBackground(new Color(200,227,255));
		x.add(table, BorderLayout.CENTER);
		x.add(table.getTableHeader(), BorderLayout.NORTH);
		
		table.setDefaultRenderer(Integer.class, new IntegerRenderer(sp.getSco()));
	    table.setDefaultEditor(Integer.class, new IntegerEditor(combo, tableModel, sp.getSco()));
	    table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting())
				{
					int i = table.getSelectedRow()-1;
					if(i >= 0)
						combo.setSelectedIndex(i);
				}
				
			}});
	    
		//vbox.add(new JScrollPane(x, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
		vbox.add(x, BorderLayout.CENTER);
		//vbox.setSize(table.getSize());
        panel.add(vbox);
        vbox.addComponentListener(new ComponentAdapter() {

			public void componentResized(ComponentEvent event) {
				//JComponent c = (JComponent) event.getSource();
				//c.setPreferredSize(c.getSize());
				panel.invalidate();
				panel.setSize(panel.getPreferredSize());
				
				content.validate();
			}} );
        content.add(panel, BorderLayout.WEST);
        panel.setIconized(true);
        content.invalidate();
        sd.show();
    }
    
    // GR: Goed: new Color(142,190,67);
    // GR: Fout: Color.white
    // GR: Noscore lightGray
    
    static class IntegerRenderer extends DefaultTableCellRenderer  {
    	
    	private Color noScoreColor = Color.lightGray;
    	private Sco sco;
    	
		public IntegerRenderer(Sco sco) {
			this.sco = sco;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			int max = 100;
// focus as column selector
			try {
				String s = sco.LMSGetValue(Sco.LESSON_LOCATION);
				hasFocus = column == 1+Integer.parseInt(s); // selected column...
			} catch (NumberFormatException e) {
				hasFocus = false;
			}
			
			super.getTableCellRendererComponent(table, value, false, hasFocus && isSelected, row, column); // hasFocus even niet gebruikt...
// patch background
			if(value != null && row > 0)
			{
				row --;
				Model model = (Model) table.getModel();
				String m = (String) ((Map) model.getScoreList(row).get(column-1)).get(PartialScoreIF.SCORE_MAX);
				if(m != null && !"".equals(m))
					max = Integer.parseInt(m);
				Color bg = calcColor( ((Number)value).floatValue(), max);
				if(!hasFocus && !isSelected)bg = bg.darker();
				setBackground(bg);
			}
			else 
				setBackground(Color.white);			
			return this;
		}

		private Color calcColor(float floatValue, int max) {
			if(max == 0)
				return noScoreColor;
			
			float g = floatValue/max;
			float f = 1 - g;
			
			//int red = Math.round(goedColor.getRed() * g + foutColor.getRed() * f);
			//int gr = Math.round(goedColor.getGreen() * g + foutColor.getGreen() * f);
			//int bl = Math.round(goedColor.getBlue() * g + foutColor.getBlue() * f);
			
			int red = 255;
			int gr = 255;
			if (g < 0.5) {
				gr = (int) (gr * (2*g));
            } else {
                red = (int) (red * (2*f));
            }
			
			red = Math.min(255,red);
			gr = Math.min(255,gr);
			red = Math.max(0,red);
			gr = Math.max(0,gr);
			return new Color(red, gr, 0);
		}

    }
    
    static class IntegerEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    	IntegerEditor(JComboBox combo, Model tableModel, Sco sco) {
			super();
			this.combo = combo;
			this.model = tableModel;
			this.sco = sco;
			button = new JButton();
			button.setBorderPainted(false);
			button.addActionListener(this);
			button.setContentAreaFilled(true);
		}

		Object value;
    	JButton button;
    	JComboBox combo;
    	Model model;
    	Sco sco;
    	int n,page;
		private JTable table;
		public void actionPerformed(ActionEvent arg0) {
			String loc = (String) ((Map) model.getScoreList(n).get(page)).get(PartialScoreIF.LOCATION);
			if(loc != null)
			{	sco.setLocationOverride(loc);
				table.repaint();
			}
			combo.setSelectedIndex(n);
			combo.repaint();
			fireEditingCanceled();
		}

		public Object getCellEditorValue() {
			return value;
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int n, int col) {
			this.value = value;
			this.n = n-1;
			this.page = col-1;
			this.table = table;
			if(value == null)
			{
				button.setText("");
				button.setBackground(Color.white);
				return button;
			}
			button.setText(value.toString());
			button.setBackground(Color.gray);
			return button;
		}
    }
    
    static class Model extends AbstractTableModel {
    	
    	List[] lists;
    	String klas = "klas";
    	private List getScoreList(int i) {
    		if(lists[i] == null)
    			lists[i] = model.getScoreList(i);
    		return lists[i];
    	}
    	
    	Model(ClassModel model, String klas) {
			super();
			this.model = model;
			lists = new List[model.getSize()];
			this.klas = klas;
    	}
    	
		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
		 */
		public Class getColumnClass(int col) {
			if(col > 0) return Integer.class;
			return super.getColumnClass(col);
		}


		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int row, int col) {
			
			if(col > 0 && row > 0 )
				return true;
			return super.isCellEditable(row, col);
		}


		private ClassModel model;
		public int getColumnCount() {
			return getScoreList(0).size()+1;
		}

		public int getRowCount() {
			return model.getSize()+1;
		}

		public Object getValueAt(int row, int col) {
			if(row == 0)
			{
				if(col == 0) return "max";
				try {
					return new Integer( ((Map) getScoreList(row).get(col-1)).get(PartialScoreIF.SCORE_MAX).toString());
				} catch (Exception e) {
					return null;
				}
			}
			row --;
			
			
			if(col == 0)
				return ((User) model.getElementAt(row)).getName();
			try {
				return new Integer(((Map) getScoreList(row).get(col-1)).get(PartialScoreIF.SCORE_RAW).toString());
			} catch (Exception e) {
				return null;
			}
		}

		public String getColumnName(int column) {
			if(column == 0)
				return klas;
			return Integer.toString(column);
		}
    	
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
            windowClosing(null);
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
        scoPanel.getSco().removePropertyChangeListener(Sco.LESSON_LOCATION, this);
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


	public void propertyChange(PropertyChangeEvent evt) {
		table.repaint();
	}
    
}