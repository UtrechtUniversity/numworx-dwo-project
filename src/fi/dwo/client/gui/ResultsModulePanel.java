// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\ResultsModulePanel.java

package fi.dwo.client.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.MessageFormat;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

//import org.jdesktop.swingx.JXLabel;

import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.LessonGroup;
import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.ResultScore;
import fi.dwo.client.domain.ResultScoreIF;
import fi.dwo.client.domain.ResultsModuleIF;
import fi.dwo.client.domain.SchoolClass;
import fi.dwo.client.domain.User;
import fi.dwo.client.domain.UserGroup;
import fi.dwo.client.domain.UserResultList;
import fi.dwo.client.system.TextMapper;

/**
 * This class is a panel represents the resultscores of a group of users and a
 * group of lessons.
 * 
 * @author M.J.B. Kupers
 * @author Wim van Velthoven
 *  
 */
public class ResultsModulePanel extends JPanel implements
        ActionListener, CenterSubPanel {
    public class ImageEditor extends AbstractCellEditor implements
			TableCellEditor, ActionListener {

    	/**
		 * 
		 */
		public ImageEditor() {
			button.addActionListener(this);
			
		}

		JButton button = new JButton();
    	ImageIcon icon = new ImageIcon();
    	int col;
    	ResultsModel model;
    	JTable table;
    	public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
    		this.col = column;
    		model = (ResultsModel) table.getModel();
    		this.table = table;
    		if(value != null)
    		{
    			icon.setImage((Image) value); button.setIcon(icon);
    		} else
    			button.setIcon(null);
    		return button;
		}

		public Object getCellEditorValue() {
			return null;
		}

		public void actionPerformed(ActionEvent e) {
			if(sortedCol == col)
			{
				image = image == imageAsc? imageDesc: imageAsc;
			} else {
				sortedCol = col;
				image = col ==0 ?imageAsc: imageDesc;
			}
//			model.fireTableRowsUpdated(0, 0);
			fireEditingCanceled();

			if(col == 0)
			{
				if(image == imageAsc)
				{
					model.setData(domain.orderBy(new User(), ResultsModuleIF.ASC));
				} else
					model.setData(domain.orderBy(new User(), ResultsModuleIF.DESC));
			} else {
				int sort = image==imageAsc?ResultsModuleIF.ASC: ResultsModuleIF.DESC;
				LessonGroup lg = (LessonGroup) table.getColumnModel().getColumn(col).getHeaderValue();
				model.setData(domain.orderBy(lg, sort));
			}
			model.fireTableDataChanged();
			
//          if (e.getID() == ResultTableHeader.ACT_SORT_ASC) {
//          setData(domain.orderBy(new User(), ResultsModuleIF.ASC));
//      } else if (e.getID() == ResultTableHeader.ACT_SORT_DESC) {
//          setData(domain.orderBy(new User(), ResultsModuleIF.DESC));

			
		}

	}

	class HeaderListener extends MouseAdapter {

    	JTableHeader header;
    	int col;
		/* (non-Javadoc)
		 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseReleased(MouseEvent e) {
			header = (JTableHeader) e.getComponent();
			int col = header.columnAtPoint(e.getPoint());
			if(col != this.col)
				return;
			
			Object o = header.getColumnModel().getColumn(col).getHeaderValue();
			if(col == 0)
			{
				if(currentUserGroup != null) // een student, zoom naar alle klassen.
				{	GuiCreator.instance().setWait();
					sortedCol = -1;
                	setJData(domain.zoomOut(currentUserGroup));
                	currentUserGroup = null;
                	GuiCreator.instance().setReady();                        
				}				
			} else {
				LessonGroup lg = (LessonGroup)o;
				if (lg.isHighestLevel()) {
                    currentLessonGroup = lg;
                	GuiCreator.instance().setWait();
                	sortedCol=-1;
                    setJData(domain.zoomIn(lg));
                	GuiCreator.instance().setReady();
                } else {
                    currentLessonGroup = null;
                	GuiCreator.instance().setWait();
                	sortedCol=-1;
                    setJData(domain.zoomOut(lg));
                	GuiCreator.instance().setReady();
                }
			}
		}
		/* (non-Javadoc)
		 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
		 */
		public void mousePressed(MouseEvent e) {
			header = (JTableHeader) e.getComponent();
			col = header.columnAtPoint(e.getPoint());			
		}

	}

	private Image image, imageAsc, imageDesc, imageAscDesc;
	private int sortedCol;
	
	public class ResultsModel extends AbstractTableModel {

		private UserResultList[] data;
		private int rowCount, columnCount;

		public Class getColumnClass(int columnIndex) {
			if(columnIndex > 0)
				return Float.class;
			return super.getColumnClass(columnIndex);
		}


		public ResultsModel(Vector data) {
			setData(data);
		}


		/**
		 * @param data
		 */
		public void setData(Vector data) {
			rowCount = data.size();
			this.data = new UserResultList[rowCount];
			data.copyInto(this.data);
			columnCount = this.data[0].getResultScore().length+1;
		}

		public int getColumnCount() {
			return columnCount;
		}

		public int getRowCount() {
			return rowCount+1;
		}

		public Object getValueAt(int row, int col) {
			if(row == 0)
			{
				return imageAscDesc; // col == sortedCol?image:null;
			}	
			row--;
			if(col == 0) {
				return data[row].getResultScore()[0].getUserGroup().getName(); 
			}
// Float.valueOf(float) is 1.5
			return new Float( data[row].getResultScore()[col-1].getScore() );
		}
		
		public long getTotalTimeAt(int row, int col)
		{
			if(row == 0 || col == 0)
				return -1;
			return data[row-1].getResultScore()[col-1].getTotal_time();
		}


		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int row, int column) {
			if(row == 0)
				return true;
			row--;
			boolean isUser = data[row].getResultScore()[0].getUserGroup().isDeepestLevel();
			if(column > 0)
				return isUser &&
					data[row].getResultScore()[column-1].getLessonGroup().isDeepestLevel() && 
					data[row].getResultScore()[column-1].getScore() != 0.0f;
			return !isUser;
		}

	}
	
	public class FloatEditor extends AbstractCellEditor implements ActionListener, TableCellEditor
	{
		private JButton button = new JButton();
		private Float value;
		private ResultScore domain;

		/**
		 * 
		 */
		public FloatEditor() {
			super();
			button.addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {			
			domain.showResult();			
			fireEditingCanceled();
		}

		public Object getCellEditorValue() {
			return value;
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			
			this.value = (Float) value;
			TableCellRenderer renderer = table.getCellRenderer(row, column);
			Component component = renderer.getTableCellRendererComponent(table, value, true, true, row, column);
			row--;
			ResultsModel model = (ResultsModel) table.getModel();
			domain = model.data[row].getResultScore()[column-1];
			if(value.equals(ZERO))	
			{	
				fireEditingCanceled();
				return component;
			
			}
			button.setText(((JLabel) component).getText()); // ons kent ons!
			button.setBackground(component.getBackground());
			return button;
		}
		
	}
	
	static final Float ZERO = new Float(0);
	
	public class FloatRenderer extends JLabel implements TableCellRenderer {

		public FloatRenderer() {
			super();
			setOpaque(true);
			setHorizontalAlignment(SwingConstants.CENTER);

		}
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean selected, boolean hasFocus, int row, int col) {
			
			setFont(GuiConstants.NORMAL_TEXT);
			if(ZERO.equals(value))
			{
				setText("");
				if(selected)
					setBackground(table.getSelectionBackground());
				else
					setBackground(table.getBackground());
				setToolTipText(null);
			} else {
		        int red = 255;
		        int green = 255;
		        int blue = 0;
				float f = ((Float)value).floatValue();
		        if (f != 0) {
		            if(f == -1) { //it is -1 he did the course but has no score
		                f = 0;
		            }
		            if (f > 100) {
		                red = 0;
		            } else {
			            if (f < 50) {
			                green = (int) (green * (f / 50));
			            } else {
			                red = (int) (red * (1 - (f - 50) / 50));
			            }
		            }
		        }
		        if(red>255)red=255;
		        if(green>255)green=255;
		        if(blue>255)blue=255;
		        if(red<0)red=0;
		        if(green<0)green=0;
		        if(blue<0)blue=0;
		        setBackground(new Color(red, green, blue));
				setText( Math.round(f) + " %");
				ResultScoreIF domain;
				domain = ((UserResultList) data.get(row-1)).getResultScore()[col-1];
                if(domain.isDeepest())
                {
    				Object[] arguments = new Object[2];
                	arguments[0] = domain.getUserGroup().getName();
                	arguments[1] = domain.getLessonGroup().getToolTip();
                	String s = TextMapper.getText(TextMapper.GUIRS_TLTP_RESULT_SCORE_BUTTON);
                	s = MessageFormat.format(s, arguments);
                	ResultsModel model = (ResultsModel) table.getModel();
                	long totalTime = model.getTotalTimeAt(row, col);
// TODO maak hier eens iets moois van
                	if(totalTime > 0)
                	{	String time;
                		if(totalTime < 120000)
                			time = " (in " + (totalTime/1000) + " sec)";
                		else 
                			time = " (in " + (totalTime/60000) + " min)";
                		setText("<html><b>"+getText()+"</b>" + time+"</html>");
                	}
                	
                	setToolTipText(s);
                } else 
                	setToolTipText(null);
			}
			return this;
		}
	
	}
	
	public class ClassEditor extends AbstractCellEditor implements ActionListener, TableCellEditor
	{
		JButton button = new JButton();
		UserGroup value;
		/**
		 * 
		 */
		public ClassEditor() {
			button.addActionListener(this);
	        button.setHorizontalTextPosition(SwingConstants.LEFT);
	        button.setHorizontalAlignment(SwingConstants.LEADING);
	        button.setFont(GuiConstants.NORMAL_TEXT);
		}

		public void actionPerformed(ActionEvent e) {
			currentUserGroup = value;
        	GuiCreator.instance().setWait();
        	sortedCol=-1;
            setJData(domain.zoomIn(value));
        	GuiCreator.instance().setReady();
			fireEditingCanceled();
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			//this.value = (UserGroup) value;
			TableCellRenderer renderer = table.getCellRenderer(row, column);
			JLabel label = (JLabel) renderer.getTableCellRendererComponent(table, value, true, true, row, column);
			row--;
			ResultsModel model = (ResultsModel) table.getModel();
			this.value = model.data[row].getResultScore()[0].getUserGroup();
			button.setIcon(label.getIcon());
			button.setText(label.getText());
			return button;
		}

		public Object getCellEditorValue() {
			return value.getName();
		}
		
	}

	
	public class ClassRenderer extends DefaultTableCellRenderer {

		public Rectangle getVisibleRect() {
			return getBounds();
		}
		
		private boolean head;
		
		/* (non-Javadoc)
		 * @see javax.swing.JComponent#getPreferredSize()
		 */
		public Dimension getPreferredSize() {
			
			Dimension preferredSize = super.getPreferredSize();
			//System.out.println("presz" + preferredSize + " for " + getText());
			if(head) preferredSize.height += 8;
			return preferredSize;
		}

		public ClassRenderer(String zoom, boolean head) {
			super();
			if(zoom != null) {
	           Image image = DwoHelper.getResourceImage(zoom);
	           setIcon(new ImageIcon(image));
			}
	           //System.out.println("getIw " + getIcon().getIconWidth());
	        	setHorizontalTextPosition(SwingConstants.LEFT);
	        
	           this.head = head;
		}

		/* (non-Javadoc)
		 * @see org.jdesktop.swingx.JXLabel#paintComponent(java.awt.Graphics)
		 */
		protected void paintComponent(Graphics arg0) {
			// TODO Auto-generated method stub
			super.paintComponent(arg0);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			if(head)
			{
				setOpaque(true);
				JTableHeader header = table.getTableHeader();
				setForeground(table.getForeground());
				setBackground(new Color(230,230,230));
				if(value instanceof UserGroup)
					setText(((UserGroup) value).getTitle());
				else
				{	
					setValue(value);
					setFont(table.getFont());
					//setLineWrap(true);
				}
				//setBorder(UIManager.getBorder("TableHeader.cellBorder"));
				setBorder(BorderFactory.createLineBorder(new Color(230,230,230), 1));
				setHorizontalAlignment(SwingConstants.CENTER);
				if(value instanceof LessonGroup)
		        { 
					LessonGroup lg = ((LessonGroup)value);
					setText(lg.getName());
					String[] arguments = new String[1];
		        	if(lg.isHighestLevel())
		        		arguments[0] = lg.getChildTitle();        
		        	else 
		        		arguments[0] = lg.getParentTitle();
		        	String tooltip = TextMapper.getText(TextMapper.GUIRS_TLTP_ZOOM);
		        	setToolTipText(MessageFormat.format(tooltip, arguments));
					setVerticalAlignment(SwingConstants.TOP);
		        } else
				if(value instanceof UserGroup)
				{
					UserGroup ug = (UserGroup) value;
					String[] arguments = new String[1];
					if(ug.isHighestLevel())
						arguments[0] = ug.getChildTitle();
					else
						arguments[0] = ug.getParentTitle();
			        
			        String tooltip = TextMapper.getText(TextMapper.GUIRS_TLTP_ZOOM);
			        setToolTipText(MessageFormat.format(tooltip, arguments));
				} else
					setToolTipText(null);
				
				
				return this;
			}
			
			if(column==0)
			{
				Object[] arguments = { "leerlingen " + value };
				String tooltip = TextMapper.getText(TextMapper.GUIRS_TLTP_ZOOM);
				setToolTipText(MessageFormat.format(tooltip, arguments));
			} else
				setToolTipText(null);
				
			
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
					row, column);
		}

	}
	
	
	private ResultsModuleIF domain;

    private CenterPanel center;

    private JButton selectCoursesButton, copyButton;

    private JLabel label;

    private UserGroup currentUserGroup;

    private LessonGroup currentLessonGroup;
    
    private final static int MAX_NAME_LENGTH = 13;

    /**
     * Creates a new ResultsModulePanel. It shows the resultscores of a group of
     * users and a group of lessons.
     * 
     * @param rm
     */
    public ResultsModulePanel(ResultsModuleIF rm) {
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        //this.setSize(600, 480);
        this.setSize(600, 280);
        setPreferredSize(getSize());
        this.setLayout(new BorderLayout());
        domain = rm;

        imageAsc = DwoHelper.getResourceImage(GuiConstants.RESULTS_ORDER_ASC);
        imageDesc = DwoHelper.getResourceImage(GuiConstants.RESULTS_ORDER_DESC);
        imageAscDesc = DwoHelper.getResourceImage(GuiConstants.RESULTS_ORDER_ASCDESC);
// Track media before rendering.
        
        
        currentUserGroup = domain.getZoomedUserGroup();
        currentLessonGroup = domain.getZoomedLessonGroup();;
        int x;
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(GuiConstants.MAIN_BACKGROUND);
        add(buttonPanel,BorderLayout.NORTH);
        
        selectCoursesButton = new JButton(TextMapper.getText(TextMapper.GUIRS_BTN_SELECT_COURSES));
        selectCoursesButton.setSize(selectCoursesButton.getPreferredSize());
        selectCoursesButton.addActionListener(this);
        selectCoursesButton.setLocation(x = getSize().width - selectCoursesButton.getSize().width - 20, 3);
        selectCoursesButton.setToolTipText(TextMapper.getText(TextMapper.GUIRS_TLTP_SELECT_COURSES));
        selectCoursesButton.setVisible(false);
        buttonPanel.add(selectCoursesButton);
        selectCoursesButton.setVisible(true);

        copyButton = new JButton(/*FIXME*/ "Copy");
        copyButton.setSize(copyButton.getPreferredSize());
        copyButton.addActionListener(this);
        copyButton.setLocation(x - copyButton.getSize().width - 20, 3);
        //TODO copyButton.setToolTip(TextMapper.getText(TextMapper.GUIRS_TLTP_SELECT_COURSES));
        copyButton.setVisible(false);
        buttonPanel.add(copyButton);
        copyButton.setVisible(true);

        jtbl = null;
        Vector v = rm.getResults();
        setJData(v);
// eerste sortering op gebruikersnaam. TODO test of dit altijd ok is!
        setJData(domain.orderBy(new User(), ResultsModuleIF.ASC));

    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
    	if ( e.getSource() == copyButton)
    	{
       	    if(data != null)
    		{
       	    	int size = data.size();
       	    	UserResultList[] userresult =  new UserResultList[size];
       	    	data.toArray(userresult);
       	    	ClipboardExport.instance().export(userresult);
    		}   		
       	    return;
    	}
    	
    	
//        if (e.getSource() instanceof ResultTableHeader) {
//            ResultTableHeader rth = (ResultTableHeader) e.getSource();
//            if ((!lessons.containsKey(rth)) && (!users.containsKey(rth))) {
//                /* Order users */
//                if (e.getID() == ResultTableHeader.ACT_SORT_ASC) {
//                    setData(domain.orderBy(new User(), ResultsModuleIF.ASC));
//                } else if (e.getID() == ResultTableHeader.ACT_SORT_DESC) {
//                    setData(domain.orderBy(new User(), ResultsModuleIF.DESC));
//                } else if (e.getID() == ResultTableHeader.ACT_ZOOM_OUT) {
//                    if (currentUserGroup != null) {
//                    	GuiCreator.instance().setWait();
//                        setData(domain.zoomOut(currentUserGroup));
//                    	GuiCreator.instance().setReady();                        
//                    }
//                }
//            } else if (lessons.containsKey(rth)) {
//                /* Something done with a lesson */
//                LessonGroup lg = (LessonGroup) lessons.get(rth);
//                if (e.getID() == ResultTableHeader.ACT_SORT_ASC) {
//                    setData(domain.orderBy(lg, ResultsModuleIF.ASC));
//                } else if (e.getID() == ResultTableHeader.ACT_SORT_DESC) {
//                    setData(domain.orderBy(lg, ResultsModuleIF.DESC));
//                } else if (e.getID() == ResultTableHeader.ACT_ZOOM_IN) {
//                    currentLessonGroup = lg;
//                	GuiCreator.instance().setWait();
//                    setData(domain.zoomIn(lg));
//                	GuiCreator.instance().setReady();
//                } else if (e.getID() == ResultTableHeader.ACT_ZOOM_OUT) {
//                    currentLessonGroup = null;
//                	GuiCreator.instance().setWait();
//                    setData(domain.zoomOut(lg));
//                	GuiCreator.instance().setReady();
//                }
//            } else if (users.containsKey(rth)) {
//                /* Something done with a user */
//                UserGroup ug = (UserGroup) users.get(rth);
//                if (e.getID() == ResultTableHeader.ACT_SORT_ASC) {
//                    setData(domain.orderBy(ug, ResultsModuleIF.ASC));
//                } else if (e.getID() == ResultTableHeader.ACT_SORT_DESC) {
//                    setData(domain.orderBy(ug, ResultsModuleIF.DESC));
//                } else if (e.getID() == ResultTableHeader.ACT_ZOOM_IN) {
//                    currentUserGroup = ug;
//                	GuiCreator.instance().setWait();
//                    setData(domain.zoomIn(ug));
//                	GuiCreator.instance().setReady();
//                } else if (e.getID() == ResultTableHeader.ACT_ZOOM_OUT) {
//                    currentUserGroup = null;
//                	GuiCreator.instance().setWait();
//                    setData(domain.zoomOut(ug));
//                	GuiCreator.instance().setReady();
//                }
//            }
//        } 
    	else if (e.getSource() == selectCoursesButton) {
        	Course[] selectedCourses = SelectCoursesDialog.selectCourses(this, domain.getAllCourses(), domain.getSelectedCourse());
        	if(selectedCourses!=null)setJData(domain.selectCourses(selectedCourses, true));
        }
    }

    /**
     * Indicate that another panel is loaded and the connections of this panel
     * must be closed.
     */
    public void end() {

    }

    /**
     * Sets the centerpanel to communicate with.
     * 
     * @param centerPanel The centerPanel to communicate with.
     */
    public void setCenterPanel(CenterPanel centerPanel) {

    }

    /**
     * Returns a Panel that can functionate as a header panel.
     * 
     * @return A panel that can functionate as a header panel.
     * @see fi.dwo.client.gui.CenterSubPanel#getHeaderPanel()
     */
    public Component getHeaderPanel() {
    	return new HeaderPanel(TextMapper.getText(TextMapper.GUIRS_RESULTS));
    }
    
//    private void setToolTips(UserGroup ug, ResultTableHeader rth) {
//        String[] arguments = new String[1];
//        arguments[0] = ug.getChildTitle();
//        
//        String tooltip = TextMapper.getText(TextMapper.GUIRS_TLTP_ZOOM);
//        rth.setToolTipZoomIn(MessageFormat.format(tooltip, arguments));
//        arguments[0] = ug.getParentTitle();
//        rth.setToolTipZoomOut(MessageFormat.format(tooltip, arguments));
//        
//        arguments[0] = ug.getOrderAscTitle();
//        tooltip = TextMapper.getText(TextMapper.GUIRS_TLTP_ZOOM_ORDER);
//        rth.setToolTipSortAsc(MessageFormat.format(tooltip, arguments));
//        arguments[0] = ug.getOrderDescTitle();
//        rth.setToolTipSortDesc(MessageFormat.format(tooltip, arguments));
//    }
//
//    private void setToolTips(LessonGroup lg, ResultTableHeader rth) {
//        String[] arguments = new String[1];
//        arguments[0] = lg.getChildTitle();        
//        String tooltip = TextMapper.getText(TextMapper.GUIRS_TLTP_ZOOM);
//        rth.setToolTipZoomIn(MessageFormat.format(tooltip, arguments));
//        arguments[0] = lg.getParentTitle();
//        rth.setToolTipZoomOut(MessageFormat.format(tooltip, arguments));
//        
//        arguments[0] = lg.getOrderAscTitle();
//        tooltip = TextMapper.getText(TextMapper.GUIRS_TLTP_ZOOM_ORDER);
//        rth.setToolTipSortAsc(MessageFormat.format(tooltip, arguments));
//        arguments[0] = lg.getOrderDescTitle();
//        rth.setToolTipSortDesc(MessageFormat.format(tooltip, arguments));
//        
//        rth.setToolTipLabel(lg.getToolTip());
//    }

    private Vector data;
    
    private JComponent jtbl;
    public void setJData(Vector data) {
    	
    	//if(true) { setData(data); return; }
    	
    	if(jtbl != null) {
    		remove(jtbl);
    		jtbl = null;
    		
    	}
    	if(label != null) {
    		remove(label);
    		label = null;
    	}
    	this.data = data;
    	if(data.size()>0) {
    		
    		setTotalen();
    		
    		selectCoursesButton.setVisible(currentLessonGroup == null);
            LessonGroup lg;
            UserGroup ug;
            ResultScore[] results = ((UserResultList) data.elementAt(0)).getResultScore();

            ug = results[0].getUserGroup();
            lg = results[0].getLessonGroup();

    	
            final ImageRenderer imageRenderer = new ImageRenderer();
            imageRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            //imageRenderer.setBorder(BorderFactory.createRaisedBevelBorder());
    		JTable table = new JTable(new ResultsModel(data)) {
    			
				public TableCellRenderer getCellRenderer(int row, int column) {
					if(row == 0)
						return imageRenderer;
					return super.getCellRenderer(row, column);
				}

				/* (non-Javadoc)
				 * @see javax.swing.JTable#getCellEditor(int, int)
				 */
				public TableCellEditor getCellEditor(int row, int column) {
					if(row==0)
						return new ImageEditor();
					return super.getCellEditor(row, column);
				}
				
				
				
     		} ;
    		table.setDefaultRenderer(Float.class, new FloatRenderer());
    		table.setDefaultEditor(Float.class, new FloatEditor());
    		table.setBackground(GuiConstants.MAIN_BACKGROUND);
    		table.getTableHeader().setBackground(table.getBackground());
    		table.getTableHeader().setReorderingAllowed(false);
    		table.getTableHeader().addMouseListener(new HeaderListener());
    		
    		TableColumnModel columnModel = table.getColumnModel();
    		columnModel.getColumn(0).setHeaderValue(results[0].getUserGroup());
    		if(ug.isHighestLevel())
    		{	
    			columnModel.getColumn(0).setCellRenderer(new ClassRenderer(GuiConstants.RESULTS_ZOOM_IN, false));
    			ClassRenderer classRenderer = new ClassRenderer(null, true);
    			classRenderer.setFont(GuiConstants.RESULTS_HEADER_TEXT);
    			columnModel.getColumn(0).setHeaderRenderer(classRenderer);
    			columnModel.getColumn(0).setCellEditor(new ClassEditor());
    			
    		} else {
    			ClassRenderer classRenderer = new ClassRenderer(GuiConstants.RESULTS_ZOOM_OUT, true);
    			classRenderer.setFont(GuiConstants.RESULTS_HEADER_TEXT);
    			columnModel.getColumn(0).setHeaderRenderer(classRenderer);
    		}
			int len = table.getColumnCount();
			boolean in = true;
			in = lg.isHighestLevel();
			TableCellRenderer renderer;
			
			ClassRenderer drenderer = new ClassRenderer(GuiConstants.RESULTS_ZOOM_OUT, true);
// TODO een groot IF statement graag FIXME tussen activiteiten en modules.
			
// activiteiten
			drenderer.setHorizontalAlignment(SwingConstants.CENTER);
			drenderer.setVerticalTextPosition(SwingConstants.BOTTOM);
			drenderer.setHorizontalTextPosition(SwingConstants.CENTER);
			drenderer.setHorizontalAlignment(SwingConstants.CENTER);
// modules
			MultiLineTableCellRenderer mrenderer = new MultiLineTableCellRenderer(in?3:1,in?20:10);
			String zoom =  in?GuiConstants.RESULTS_ZOOM_IN:GuiConstants.RESULTS_ZOOM_OUT;
			Image image = DwoHelper.getResourceImage(zoom);
			
	        IconBorder border = new IconBorder(new ImageIcon(image));
	        mrenderer.setBorder(border);
	        
	        if(in)
	        	renderer = mrenderer;
	        else 
	        	renderer = drenderer;
   			for(int i = 1; i < len; i++)
    		{
   				columnModel.getColumn(i).setHeaderValue(results[i-1].getLessonGroup());
				columnModel.getColumn(i).setHeaderRenderer(renderer);
    		}
    		
    		TableUtil.setJTableSizes(table);
    		int sum = 0;
   			for(int i = 1; i < len; i++)
    		{
   				TableColumn column = columnModel.getColumn(i);
				int width = column.getPreferredWidth();
				if(width > 100) width = 100;
				sum += width;
				//System.out.println(i + " : " + width + " " + sum);
   				column.setMinWidth(width);
   				column.setMaxWidth(width);
    		}
   			
   			
   			table.validate();
            table.setSize(table.getPreferredSize());
        	JPanel panel = new JPanel(new BorderLayout());
        	panel.add(table.getTableHeader(),BorderLayout.NORTH);
        	panel.add(table, BorderLayout.CENTER);

        	table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    		//JScrollPane pane = new JScrollPane(panel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    		//pane.getViewport().setBackground(Color.RED);
    		//TableUtil.setBorder(pane);
    		table.setGridColor(new Color(210,210,210));
    		jtbl = new JPanel(new BorderLayout());
    		JLabel title = new JLabel(lg.getTitle()); // FIXME je juiste naam...
    		title.setOpaque(false);
    		jtbl.setOpaque(false);
    		jtbl.add(title, BorderLayout.NORTH);
    		//TableUtil.shrinkToFit(table, pane, 600, 470);
    		jtbl.add(panel, BorderLayout.CENTER);
    		
    		//Dimension pref = jtbl.getPreferredSize();
    		//pref.width = Math.min(623-10, pref.width);
    		//pref.height = Math.min(487-10-10, pref.height);
    		//jtbl.setSize(623-10, 487-10-10);
    		//jtbl.setSize(pref);
    		
            jtbl.setLocation(10, 10+5);
            jtbl.validate();
            add(jtbl);
            Dimension pref = jtbl.getPreferredSize();
    		pref.height += 40;
            setPreferredSize(pref);
            jtbl.invalidate();
            validate();
            repaint();
 
    	} else {
            label = new JLabel(TextMapper.getText(TextMapper.GUIRS_NO_RESULTS));
            label.setFont(GuiConstants.SCO_TEXT);
            FontMetrics fm = label.getFontMetrics(label.getFont());
            label.setSize(fm.stringWidth(label.getText()) + 10, fm.getHeight());
            label.setLocation((this.getSize().width/2) - (label.getSize().width/2), 100);
            selectCoursesButton.setVisible(false);
            this.add(label);
        }
    }
    
    
    
//    /**
//     * Sets the data of the ResultsModule Gui object.
//     * 
//     * @param data An array of UserResultList objects to show.
//     * @see fi.dwo.client.gui.ResultsModuleGuiIF#setData(java.util.Vector)
//     * @deprecated gebruik setJData
//     */
//    private void xxxsetData(Vector data) {
////        this.setVisible(false);
//        if (tbl != null) {
//            tbl.setVisible(false);
//            this.remove(tbl);
//            tbl = null;
//        }
//        
//        if(label != null) {
//            label.setVisible(false);
//            this.remove(label);
//            label = null;
//        }
//        this.data = data;
//        if (data.size() > 0) {
//            if(currentLessonGroup == null) {
//                selectCoursesButton.setVisible(true);
//            } else {
//                selectCoursesButton.setVisible(false);                
//            }
//            lessons = new Hashtable();
//            users = new Hashtable();
//            ResultScore[] results = ((UserResultList) data.elementAt(0)).getResultScore();
//            tbl = new Table(results.length + 1);
//            tbl.setVisible(false);
//            tbl.setBackground(GuiConstants.SUB_BACKGROUND);
//            //tbl.setBackground(GuiConstants.MAIN_BACKGROUND);
//            
//            //tbl.setComponentBackground(GuiConstants.MAIN_BACKGROUND);
//            tbl.setComponentBackground(GuiConstants.CELL_BACKGROUND);
//            
//            tbl.showBorder();
//
//            Component[] components = new Component[results.length + 1];
//
//            int i;
//            int j;
//            LessonGroup lg;
//            UserGroup ug;
//
//            ug = results[0].getUserGroup();
//            lg = results[0].getLessonGroup();
//
//            //tbl.setTitle(ug.getTitle() + " / " + lg.getTitle());
//            tbl.setTitle(lg.getTitle());
//            ResultTableHeader rth;
//
//            /* Add the headers */
//            components[0] = new Label("");
//
//            String name;
//
//            for (i = 0; i < results.length; i++) {
//                lg = results[i].getLessonGroup();
//                name = lg.getName();
//                //if(name.length() > MAX_NAME_LENGTH) {
//                //    name = name.substring(0, MAX_NAME_LENGTH - 3) + "...";
//                //}
//                if(lg instanceof Course)
//                {
//                	rth = new ResultTableHeader(name, ResultTableHeader.HORIZONTAL, true, !lg.isDeepestLevel(), !lg.isHighestLevel(), true, true);
//                }
//                else
//                {
//                	rth = new ResultTableHeader(name, ResultTableHeader.HORIZONTAL, true, !lg.isDeepestLevel(), !lg.isHighestLevel(), true, false);
//                }
//                
//                rth.addActionListener(this);
//                setToolTips(lg, rth);
//                
//                components[i + 1] = rth;
//                lessons.put(rth, lg);
//            }
//            
//            tbl.addRow(components, TableCell.CENTER,false);
//            
//            components = new Component[1];
//            //rth = new ResultTableHeader(ug.getType(), ResultTableHeader.HORIZONTAL, true, false, !ug.isHighestLevel(), false);
//            rth = new ResultTableHeader(ug.getTitle(), ResultTableHeader.HORIZONTAL, true, false, !ug.isHighestLevel(), false, false);
//            rth.setFont(GuiConstants.RESULTS_HEADER_TEXT);
//            rth.addActionListener(this);
//            setToolTips(ug, rth);
//            components[0] = rth;
//            tbl.addRow(components, TableCell.CENTER, false);
//
//            
//
//            /* Add the data */
//            for (i = 0; i < data.size(); i++) {
//                results = ((UserResultList) data.elementAt(i)).getResultScore();
//                components = new Component[results.length + 1];
//
//                ug = results[0].getUserGroup();
//                rth = new ResultTableHeader(ug.getName(), ResultTableHeader.HORIZONTAL, false, !ug.isDeepestLevel(), false, false, false);
//                rth.addActionListener(this);
//                setToolTips(ug, rth);
//                components[0] = rth;
//                users.put(components[0], ug);
//
//                for (j = 0; j < results.length; j++) {
//// FIXME dit hoort hier niet thuis maar moet private zijn
//                	lg = results[j].getLessonGroup();
//                    ug = results[j].getUserGroup();
//                    int corrTotaal = 1;
//// average course/students
//                    if(lg instanceof Course)
//                    {
//                    	Course course = (Course)lg;
//                    	if(course.getScoList() == null)
//                    		course.loadScos();
//                    	corrTotaal= course.getScoList().length;
//                    }
//                    if(ug instanceof SchoolClass)
//                    {
//                    	SchoolClass schoolClass = (SchoolClass)ug;
//                    	User[] u = schoolClass.getStudents();
//                    	if(u!=null)
//                    		corrTotaal *= u.length;
//                    }
//                    results[j].setCorrTotaal(corrTotaal);
//                    
//                    components[j + 1] = results[j].getGui();
//                }
//
//                tbl.addRow(components, TableCell.CENTER, false);
//            }
//
//            //tbl.setSize(560, 400);
//            tbl.setSize(623, 487);
//            tbl.setLocation(10, 10);
//            
//            //tbl.setSize(590, 420);
//            //tbl.setLocation(20, 60);
//            
//            tbl.setVisible(false);
//            this.add(tbl);
//            tbl.setVisible(true);
//
//        } else {
//            label = new Label(TextMapper.getText(TextMapper.GUIRS_NO_RESULTS));
//            label.setFont(GuiConstants.SCO_TEXT);
//            FontMetrics fm = label.getFontMetrics(label.getFont());
//            label.setSize(fm.stringWidth(label.getText()) + 10, fm.getHeight());
//            label.setLocation((this.getSize().width/2) - (label.getSize().width/2), 100);
//            selectCoursesButton.setVisible(false);
//            this.add(label);
//        }
//
////        this.setVisible(true);
//   }
    
    private void setTotalen() { 
    	ResultScore[] results;
		UserGroup ug;
		LessonGroup lg;
		int i, j;
		for (i = 0; i < data.size(); i++) {
			results = ((UserResultList) data.elementAt(i)).getResultScore();
			ug = results[0].getUserGroup();
			for (j = 0; j < results.length; j++) {
				// FIXME dit hoort hier niet thuis maar moet private zijn
				lg = results[j].getLessonGroup();
				ug = results[j].getUserGroup();
				int corrTotaal = 1;
				// average course/students
				if (lg instanceof Course) {
					Course course = (Course) lg;
					if (course.getScoList() == null)
						course.loadScos();
					corrTotaal = course.getScoList().length;
				}
				if (ug instanceof SchoolClass) {
					SchoolClass schoolClass = (SchoolClass) ug;
					User[] u = schoolClass.getStudents();
					if (u != null)
						corrTotaal *= u.length;
				}
				results[j].setCorrTotaal(corrTotaal);
			}
		}
    }
    
    
    /**
	 * Returns the current object, as the object to add to a gui.
	 * 
	 * @return the current object.
	 * @see fi.dwo.client.gui.CenterSubPanel#getComponent()
	 */
    public Component getComponent() {
        return this;
    }
}