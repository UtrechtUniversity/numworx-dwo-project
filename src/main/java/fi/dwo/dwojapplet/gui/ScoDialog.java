/*
 * Created on Mar 9, 2005
 *
 */
package fi.dwo.dwojapplet.gui;

import fi.beans.scorm.PartialScoreIF;
import fi.beans.scorm.SCORM12APIInterface;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.domain.ScoBase;
import static fi.dwo.dwojapplet.domain.ScoBase.REVIEW;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.gui.print.ComboBoxModelIterator;
import fi.dwo.dwojapplet.gui.print.PrintComponent;
import fi.dwo.dwojapplet.gui.print.PrintPanel;
import fi.dwo.dwojapplet.gui.print.PrinterEvent;
import fi.dwo.dwojapplet.gui.print.PrinterListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
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
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

/**
 * This class is a dialog that shows the sco, with the results of the specified
 * user.
 *
 * @author M.J.B. Kupers
 *
 */
public class ScoDialog extends JDialog implements ActionListener, WindowListener, PropertyChangeListener {

    static class CopyClipboardAction extends AbstractAction {

        private ClassModel model;

        public CopyClipboardAction(ClassModel model) {
            this(TextMapper.getText(TextMapper.GUIRS_BTN_COPY_TO_CLIPBOARD));
            this.model = model;
        }

        public CopyClipboardAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ClipboardExport.instance().export(model);

        }

    }

    private static class SaveRestore implements PrinterListener {
    	Object   current;
    	String location; // TODO
    	ClassModel model;
    	  	
		public SaveRestore(ClassModel model2) {
			model = model2;
		}


		@Override
		public void onPrint(PrinterEvent e) {
			switch(e.getType()) {
			case PrinterEvent.STARTED: 
				current = model.getSelectedItem();
				break;
			case PrinterEvent.STOPPED:
				model.setSelectedItem(current);
			}
			
		}
    	
    	
    }
    
    
    
    /**
     * Decorator om Sco voor losse User. FIXME losse API met USER en Sco. Extra
     * class tussen Sco en ScormAdapter?
     *
     * @author wim
     *
     */
    public static class API extends ScoBase implements SCORM12APIInterface {

        private String creditStatus;
        private Sco sco;
        
        public API(Sco sco, User u) {
            super(false);
            this.features = sco.features;
            this.creditStatus = sco.getCreditStatus();
            this.dwo = sco.dwo;
            setLaunchdata(sco.getLaunchdata());
            setScoID(sco.getScoID());
            this.sco = sco;
            setUser(u);
            setLessonMode(REVIEW);
        }

        @Override
        public String LMSInitialize(String iParam) {
            return "false";
        }

        @Override
        public void setUser(User u) {
            user = u;
        }

        @Override
        public String LMSFinish(String iParam) {
            return "false";
        }

        /* (non-Javadoc)
         * @see fi.dwo.client.domain.ScoBase#getCreditStatus()
         */
        @Override
        public String getCreditStatus() {
            return creditStatus;
        }

        /* (non-Javadoc)
         * @see fi.beans.scorm.ScormAdapter#LMSGetValue(java.lang.String)
         */
        @Override
        public String LMSGetValue(String key) {
            return super.LMSGetValue(key);
        }

        /* (non-Javadoc)
         * @see fi.beans.scorm.ScormAdapter#LMSSetValue(java.lang.String, java.lang.String)
         */
        @Override
        public String LMSSetValue(String key, String value) {
            return super.LMSSetValue(key, value);
        }

		@Override
		protected void jsonEncode(Map ld, Writer out) throws IOException {
			sco.jsonEncode(ld, out);			
		}

    }

    public static class ClassModel extends DefaultComboBoxModel<User> {

        private Sco sco;
        private int index;
        private List[] lists;
        private ChangeListener listener;
        public void setListener(ChangeListener listener) {
			this.listener = listener;
		}

		int getIndex() {
			return index;
		}

		public ClassModel(SchoolClass s, User u, Sco sco) {
            super(sorted(s.getStudents()));
            this.sco = sco;
            setSelectedItem(u);
            index = getIndexOf(u);
            if(index < 0) index = 0;
            lists = new List[getSize()];
            lists[index] = getScoreListSync(index);
            
        }

        private void setComplete(int i, Boolean b) {
            API api = getApi(i);
            String value = "unknown";
            if (Boolean.TRUE.equals(b)) {
                value = "completed";
            } else if (Boolean.FALSE.equals(b)) {
                value = "incomplete";
            }
            api.SetValue(ScoBase.COMPLETION_STATUS, value);
        }

        private API getApi(int i) {
            return new API(sco, getUser(i));
        }

        private Boolean getComplete(int i) {
            API api = getApi(i);
            String value = api.GetValue(ScoBase.COMPLETION_STATUS);
            if ("completed".equals(value)) {
                return Boolean.TRUE;
            }
            if ("incomplete".equals(value)) {
                return Boolean.FALSE;
            }
            return null;
        }

        private static User[] sorted(User[] students) {
            Arrays.sort(students);
            return students;
        }

        public synchronized List getScoreListSync(int i) {
            return sco.getPartialScoreIF().getScoreMapList(getApi(i));
        }
        
        public synchronized List getScoreList(final int i) {
        	if(true) return getScoreListSync(i);
        	if(lists[i] != null) 
        		return lists[i];
        	final List list = new ArrayList();
        	SwingWorker<List,?> worker = new SwingWorker<List, Object>() {

				@Override
				protected List doInBackground() throws Exception {
					return getScoreListSync(i);
				}

				protected void done() {
					try {
						List result = get();
						list.addAll(result);
						if(listener != null)
							listener.stateChanged(new ChangeEvent(this));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
        	
        	
        	};
        	worker.execute();
        	return lists[i] = list;
        	
        }

        public User getUser(int i) {
            return (User) getElementAt(i);
        }
    }

    private ScoPanel scoPanel;

    private JButton closeButton;
    private JCheckBox studentSeal;
    private JButton globalSeal;

    private JTable table;

	private PrintPanel printer;

    /**
     * Creates a new instance of a ScoDialog. It shows the sco, made by an user.
     *
     * @param owner The owner component of the dialog.
     * @param windowTitle
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

        contentPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        //setContentPane(contentPane);
        scoPanel = sp;
        contentPane.setBackground(GuiConstants.MAIN_BACKGROUND);
        closeButton = new JButton(TextMapper.getText(TextMapper.BTN_CLOSE));

        closeButton.setSize(closeButton.getPreferredSize());
        closeButton.addActionListener(this);

        globalSeal = new JButton("Alles verzegelen");
        globalSeal.setVisible(false);
        globalSeal.addActionListener(this);
        studentSeal = new JCheckBox("Verzegeld voor deze leerling");
        studentSeal.setOpaque(false);
        studentSeal.setVisible(false);
        // this.pack();
        Insets insets = contentPane.getInsets();

        contentPane.setOpaque(false);
        Box hhbox = Box.createHorizontalBox();
        hhbox.add(hbox);
        contentPane.add(hhbox, BorderLayout.NORTH);

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
        hbox1.add(Box.createHorizontalStrut(10));
        if(true) {
// insert printer on bottom      
        hhbox.add(Box.createHorizontalStrut(10));
        hhbox.add(Box.createGlue());
        printer = new PrintPanel();
        PrintComponent component = new PrintComponent(scoPanel.getSco().getApplet(), scoPanel.getSco());
        printer.setComponent(component);
// into hbox1        
        hhbox.add(printer.asComponent());hbox1.add(Box.createHorizontalStrut(20));
        } 
        hbox1.add(globalSeal);
        hbox1.add(Box.createHorizontalStrut(10));
        hbox1.add(Box.createHorizontalGlue());

        hbox1.add(studentSeal);
        hbox1.add(Box.createHorizontalStrut(50));

        contentPane.add(hbox1, BorderLayout.SOUTH);
        closeButton.setVisible(true);

        JPanel basisPanel = new JPanel(new BorderLayout(0, 5));
        basisPanel.setBackground(new Color(200, 227, 255));
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

        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }

        setLocation(x, y);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(this);
    }

    static void resetSeal(ScoPanel sp, ButtonModel sealmodel) {
        boolean selected = "completed".equals(sp.LMSGetValue("cmi.completion_status"));
        sealmodel.setSelected(selected);
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
    private static void showScoDialog(Component parent, ScoPanel sp, User ug) {
        String[] arguments = {sp.getSco().getScoName(), ug.getName()};
        String title = TextMapper.format((TextMapper.UG_RESULTS_OF_STUDENT), arguments);
        ScoDialog sd = new ScoDialog(parent, TextMapper.getText(TextMapper.GUIRS_RESULTS), title, true, sp);
        sd.show();
    }

    private static void refreshApplet(ScoPanel sp) {
    	sp.getSco().getApplet().stop();
    	sp.appletStart();
    }
    
    
    public static void showScoDialog(Component parent, final ScoPanel sp, final User u, final SchoolClass s) {
        String[] arguments = {sp.getSco().getScoName(), ""};
        String title = TextMapper.format((TextMapper.UG_RESULTS_OF_STUDENT), arguments);
        Box hbox = createTitleBox(title);
        final JComboBox<User> combo = new JComboBox<User>(); // TODO wegwerken.....
        final JLabel userLabel = new JLabel(u.getName());
        final ClassModel model = new ClassModel(s, u, sp.getSco());
        Model tableModel = new Model(model, s.getName());
        final JTable table = new JTable(tableModel);
        final ButtonModel sealmodel = new JCheckBox.ToggleButtonModel();
        table.setOpaque(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        combo.setModel(model);
        table.setRowSelectionInterval(combo.getSelectedIndex() + 1, combo.getSelectedIndex() + 1);
        final ItemListener itemListener = new ItemListener() {

			public void itemStateChanged(ItemEvent event) {
				User u = (User) event.getItem();
				switch (event.getStateChange()) {
				case ItemEvent.DESELECTED:
						sp.getSco().setUser(u);
						sp.getSco().getApplet().stop();
						break;
				case ItemEvent.SELECTED:
						sp.getSco().setUser(u);
						userLabel.setText(u.getName());
						sp.appletStart();
						sp.repaint();
						//list.setSelectedValue(u, false);
						int i = combo.getSelectedIndex()+1;
						table.setRowSelectionInterval(i, i);
						resetSeal(sp, sealmodel);
						break;
				}
			}};
		combo.addItemListener(itemListener);
		hbox.add(userLabel);
        ScoDialog sd = new ScoDialog(parent, TextMapper.getText(TextMapper.GUIRS_RESULTS), hbox, true, sp);
        sd.table = table;
        resetSeal(sp, sealmodel);
        sd.studentSeal.setModel(sealmodel);
        sd.studentSeal.setVisible(true);
        sd.globalSeal.setVisible(true);
        sealmodel.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean selected = sealmodel.isSelected();
            	sp.getSco().getApplet().stop();
            	sp.LMSSetValue("cmi.completion_status", selected ? "completed" : "incomplete");
           		sp.appletStart(); 
            }
        });

        sp.getSco().addPropertyChangeListener(Sco.LESSON_LOCATION, sd);
        
        sd.printer.setIterable(new ComboBoxModelIterator<User>(model, sd.printer.getComponent()));
        sd.printer.addPrinterListener(new SaveRestore(model));
        final Container content = sd.getContentPane();
        final IconizedPanel panel = new IconizedPanel(TextMapper.getText(TextMapper.GUIS_STUDENTS));
        panel.setBackground(new Color(200, 227, 255));

        JPanel vbox = new JPanel(new BorderLayout());
        vbox.setOpaque(false);
        JButton btn = new JButton(panel.getCloseAction());
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        vbox.add(btn, BorderLayout.NORTH);
        vbox.add(new Mover(6), BorderLayout.EAST);
        TableUtil.setJTableSizes(table);
        int cols = table.getColumnCount();
        for (int i = 0; i < cols; i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            int pref = column.getPreferredWidth();
            column.setMaxWidth(pref);
            column.setMinWidth(pref);
        }
        table.setMaximumSize(table.getPreferredSize());
        table.setMinimumSize(table.getPreferredSize());
        JPanel x = new JPanel(new BorderLayout());
        x.setOpaque(false);
        x.add(table, BorderLayout.CENTER);
        x.add(table.getTableHeader(), BorderLayout.NORTH);
        table.getTableHeader().setOpaque(false);

        table.setDefaultRenderer(Integer.class, new IntegerRenderer(sp.getSco()));
        table.setDefaultEditor(Integer.class, new IntegerEditor(combo, tableModel, sp.getSco()));
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int i = table.getSelectedRow() - 1;
                    if (i >= 0) {
                        combo.setSelectedIndex(i);
                    }
                }

            }
        });

        JScrollPane comp = new JScrollPane(x);
        comp.setOpaque(false);
        comp.getViewport().setOpaque(false);
        vbox.add(comp, BorderLayout.CENTER);
        Component copybtn = new JButton(new CopyClipboardAction(model));
        JPanel p = new JPanel(false);
        p.setOpaque(false);
        p.add(copybtn);
        vbox.add(p, BorderLayout.SOUTH);
        panel.setWindow(vbox);
        comp.setBorder(null);
        comp.setViewportBorder(null);
        vbox.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent event) {
                content.validate();
                content.repaint();
            }
        });
        content.add(panel, BorderLayout.WEST);
// start met correcte sizes.
        panel.getWindow().setSize(panel.getWindow().getPreferredSize());
        panel.setIconized(true);
        content.invalidate();
        sd.show();
    }

    // GR: Goed: new Color(142,190,67);
    // GR: Fout: Color.white
    // GR: Noscore lightGray

    static class IntegerRenderer extends DefaultTableCellRenderer {

        private static final Insets INSETS = new Insets(0, 0, 0, 0);
		private static final Color CORRECTED_COLOR = new Color(0,0,255,100);
		private static final int SIZE = 10;
		private static final Border CORRECTED_BORDER = new Border() {

			@Override
			public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
				g.setColor(CORRECTED_COLOR);
				Polygon p = new Polygon();
				x += width; y += height;
				p.addPoint(x, y);
				p.addPoint(x, y-SIZE);
				p.addPoint(x-SIZE, y);
				g.fillPolygon(p);
			}

			@Override
			public Insets getBorderInsets(Component c) {
				return INSETS;
			}

			@Override
			public boolean isBorderOpaque() {
				return false;
			}};

		private Color noScoreColor = Color.lightGray;
        private Sco sco;

        public IntegerRenderer(Sco sco) {
            this.sco = sco;
        }

        /* (non-Javadoc)
         * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
         */
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            int max = 100;
// focus as column selector
            try {
                String s = sco.GetValue(Sco.LESSON_LOCATION);
                hasFocus = column == 1 + Integer.parseInt(s); // selected column...
            } catch (NumberFormatException e) {
                hasFocus = false;
            }

            super.getTableCellRendererComponent(table, value, false, hasFocus && isSelected, row, column); // hasFocus even niet gebruikt...
// patch background
            if (value != null && row > 0) {
                row--;
                Model model = (Model) table.getModel();
                Map map = (Map) model.getScoreList(row).get(column - 1);
				String m = (String) map.get(PartialScoreIF.SCORE_MAX);
                if (m != null && !"".equals(m)) {
                    max = Integer.parseInt(m);
                }
                Color bg = calcColor(((Number) value).floatValue(), max);
                if (!hasFocus && !isSelected) {
                    bg = bg.darker();
                }
                setBackground(bg);
// render corrected marker
                String is = (String) map.get("isCorrected");
                if("true".equals(is)) {
                	setBorder(CORRECTED_BORDER);
                } else
                	setBorder(null);
            } else if (row > 0) {
                Color bg = Color.lightGray; // OK of nog te donker?
                if (!hasFocus && !isSelected) {
                    bg = bg.darker();
                }
                setBackground(bg);
            } else {
                setBackground(Color.white);
            }
            return this;
        }

        private Color calcColor(float floatValue, int max) {
            if (max == 0) {
                return noScoreColor;
            }

            float g = floatValue / max;
            float f = 1 - g;

            //int red = Math.round(goedColor.getRed() * g + foutColor.getRed() * f);
            //int gr = Math.round(goedColor.getGreen() * g + foutColor.getGreen() * f);
            //int bl = Math.round(goedColor.getBlue() * g + foutColor.getBlue() * f);
            int red = 255;
            int gr = 255;
            if (g < 0.5) {
                gr = (int) (gr * (2 * g));
            } else {
                red = (int) (red * (2 * f));
            }

            red = Math.min(255, red);
            gr = Math.min(255, gr);
            red = Math.max(0, red);
            gr = Math.max(0, gr);
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
        int n, page;
        private JTable table;

        @Override
        public void actionPerformed(ActionEvent arg0) {
            String loc = (String) ((Map) model.getScoreList(n).get(page)).get(PartialScoreIF.LOCATION);
            if (loc != null) {
                sco.setLocationOverride(loc);
                table.repaint();
            }
            combo.setSelectedIndex(n);
            //combo.repaint();
            fireEditingCanceled();
        }

        @Override
        public Object getCellEditorValue() {
            return value;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int n, int col) {
            this.value = value;
            this.n = n - 1;
            this.page = col - 1;
            this.table = table;
            if (value == null) {
                button.setText("");
                button.setBackground(Color.white);
                return button;
            }
            button.setText(value.toString());
            button.setBackground(Color.gray);
            return button;
        }
    }

    static class Model extends AbstractTableModel implements ChangeListener {

        List[] lists;
        Boolean global;
        String klas = "klas";
        int index;
        private List getScoreList(int i) {
            if (lists[i] == null) {
                lists[i] = model.getScoreList(i);
            }
            return lists[i];
        }

        Model(ClassModel model, String klas) {
            this.model = model;
            this.index = model.getIndex();
            model.setListener(this);
            lists = new List[model.getSize()];
            this.klas = klas;
            global = Boolean.TRUE;

            for (int i = 0; i < model.getSize(); i++) {
                if (!global.equals(model.getComplete(i))) {
                    global = Boolean.FALSE;
                    break;
                }
            }
        }

        /* (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
         */
        @Override
        public Class getColumnClass(int col) {
            if (col != 0) {
                return Integer.class;
            }
            return super.getColumnClass(col);
        }


        /* (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int row, int col) {

            if (col > 0 && row > 0 || col == 1) {
                return true;
            }
            return super.isCellEditable(row, col);
        }

        private ClassModel model;

        @Override
        public int getColumnCount() {
            return getScoreList(index).size() + 1;
        }

        @Override
        public int getRowCount() {
            return model.getSize() + 1;
        }

        @Override
        public Object getValueAt(int row, int col) {
            if (row == 0) {
                if (col == 0) {
                    return "max";
                }
                try {
                    return Integer.valueOf(((Map) getScoreList(row).get(col - 1)).get(PartialScoreIF.SCORE_MAX).toString());
                } catch (Exception e) {
                    return null;
                }
            }
            row--;

            if (col == 0) {
                return (model.getUser(row)).getName();
            }
            try {
                return Integer.valueOf(((Map) getScoreList(row).get(col - 1)).get(PartialScoreIF.SCORE_RAW).toString());
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public String getColumnName(int column) {
            if (column == 0) {
                return klas;
            }
// XXX 
            try {
                return ((Map) getScoreList(index).get(column - 1)).get(PartialScoreIF.DESCRIPTION).toString();
            } catch (Exception e) {
            }
            return Integer.toString(column);
        }

		@Override
		public void stateChanged(ChangeEvent e) {
			fireTableDataChanged();
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
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == closeButton) {
            windowClosing(null);
        }
        if (e.getSource() == globalSeal) {
            if (JOptionPane.showConfirmDialog(this, "De activiteit voor alle leerlingen verzegelen?", "Verzegelen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            	scoPanel.getSco().getApplet().stop();
                Model m = (Model) table.getModel();
                ClassModel model = m.model;
                for (int i = 0; i < model.getSize(); i++) {
                    model.setComplete(i, Boolean.TRUE);
                }
                studentSeal.setSelected(true);
                scoPanel.appletStart();
            }
        }
    }

    /**
     * Invoked when the window is set to be the user's active window, which
     * means the window (or one of its subcomponents) will receive keyboard
     * events.
     *
     * @param e The WindowEvent.
     * @see
     * java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
     */
    @Override
    public void windowActivated(WindowEvent e) {
    }

    /**
     * Invoked when a window has been closed as the result of calling dispose on
     * the window.
     *
     * @param e The WindowEvent.
     * @see
     * java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
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
     * @param e The WindowEvent.
     * @see
     * java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosing(WindowEvent e) {
        setVisible(false);
        scoPanel.getSco().removePropertyChangeListener(Sco.LESSON_LOCATION, this);
        scoPanel.getSco().endWithoutSaving();
        dispose();
    }

    /**
     * Invoked when a window is no longer the user's active window, which means
     * that keyboard events will no longer be delivered to the window or its
     * subcomponents.
     *
     * @param e The WindowEvent.
     * @see
     * java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
     */
    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    /**
     * Invoked when a window is changed from a minimized to a normal state.
     *
     * @param e The WindowEvent.
     * @see
     * java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
     */
    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    /**
     * Invoked when a window is changed from a minimized to a normal state.
     *
     * @param e The WindowEvent.
     * @see
     * java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
     */
    @Override
    public void windowIconified(WindowEvent e) {
    }

    /**
     * Invoked when a window is changed from a normal to a minimized state. For
     * many platforms, a minimized window is displayed as the icon specified in
     * the window's iconImage property.
     *
     * @param e The WindowEvent.
     * @see
     * java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
     */
    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        table.repaint();
    }

}
