/*
 * Created on Mar 24, 2005
 *
 */
package fi.dwo.client.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.SchoolClass;
import fi.dwo.client.domain.Teacher;
import fi.dwo.client.system.ClassException;
import fi.dwo.client.system.TextMapper;

/**
 * The panel where a SchoolClass can be managed.
 * @author M.J.B. Kupers
 *
 */
public class ClassPanel extends Panel implements CenterSubPanel, ActionListener {

    private CenterPanel center;

    private DwoButton addClassButton;

    private Image removeImage, editImage, usersImage, assignImage;

    class ClassModel extends AbstractTableModel {

    	SchoolClass[] classes;
    	
		public ClassModel(SchoolClass[] classes) {
			super();
			this.classes = classes;
		}

		public int getColumnCount() {
			return 5;
		}

		public int getRowCount() {
			return classes.length;
		}

		public Object getValueAt(int row, int col) {
			switch(col) {
			case 0:
				return classes[row].getName();
			case 1:
				return usersImage;
			case 2:
				return editImage;
			case 3:
				return removeImage;
			case 4:
				return assignImage;
			}
			return null;
		}

		public Class getColumnClass(int col) {
			if(col>0)
				return Image.class;
			return super.getColumnClass(col);
		}

		public boolean isCellEditable(int row, int col) {
			return col > 0;
		}

		public void removeRow(int row) {
			SchoolClass[] sc = new SchoolClass[classes.length-1];
			System.arraycopy(classes, 0, sc, 0, row);
			System.arraycopy(classes, row+1, sc,row, sc.length-row);
			classes = sc;
			fireTableRowsDeleted(row,row);
		}
    	
    }

    public class ImageRenderer extends JLabel implements TableCellRenderer {

		private ImageIcon icon = new ImageIcon();

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean selected, boolean hasFocus, int row, int col) {
			Image image = (Image)value;
			icon.setImage(image);
			setIcon(icon);
			setHorizontalAlignment(SwingConstants.CENTER);
			setOpaque(true);
			Object[] arguments = new Object[]  { table.getValueAt(row, 0) };
			switch(col) {
			case 1:	String s = TextMapper.getText(TextMapper.GUIC_TLTP_USERS_CLASS);
	    			setToolTipText(MessageFormat.format(s, arguments));
	    			break;
			case 2: setToolTipText(TextMapper.getText(TextMapper.GUIC_TLTP_EDIT_CLASS));
				break;
			case 3: String format = TextMapper.getText(TextMapper.GUIC_TLTP_DELETE_CLASS);
					setToolTipText(MessageFormat.format(format, arguments));
				break;
			case 4: format = TextMapper.getText(TextMapper.GUIC_TLTP_ASSIGN_CLASS);
					setToolTipText(MessageFormat.format(format, arguments));
				break;
			default:
				setToolTipText("Message " + col); // TODO ....
			}
			if(selected)
			{
				setBackground(table.getSelectionBackground());
			} else {
				setBackground(table.getBackground());
			}
			return this;
		}

	}
    public class ImageButtonEditor extends AbstractCellEditor implements
	TableCellEditor, ActionListener {

    	Object value;
    	ClassModel model;
    	int row;

    	public Component getTableCellEditorComponent(JTable table, Object value,
    			boolean arg2, int row, int col) {
    		this.value = value;
    		JButton button = new JButton(new ImageIcon((Image)value));
    		button.addActionListener(this);
    		this.row = row;
    		model = (ClassModel) table.getModel();
    		return button;
    	}

    	public Object getCellEditorValue() {
    		return value;
    	}

    	public void actionPerformed(ActionEvent event) {
            SchoolClass sc = model.classes[row];
    		if (value == editImage) {
                String newName = JOptionPane.showInputDialog(ClassPanel.this, TextMapper.getText(TextMapper.GUIC_MSG_RENAME_CLASS));
                if ((newName != null) && (!newName.equals("")) && GuiCreator.instance().renameClass(sc, newName)) {
                    center.loadMenu();
                    model.fireTableCellUpdated(row, 0);
                }                

    		} else if (value == removeImage) {
                /* Delete the course */
                if (JOptionPane.showConfirmDialog(ClassPanel.this, TextMapper.getText(TextMapper.GUIC_MSG_DELETE_CLASS)
                        + "?", TextMapper.getText(TextMapper.GUIC_DELETE_CLASS), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    if (GuiCreator.instance().deleteClass(sc)) {
                        center.loadMenu();
        	            model.removeRow(row);
                    }
                }

    		} else if (value == usersImage) {
                center.loadCenter(GuiCreator.instance().getClassUsersPanel(sc));
    		} else if (value == assignImage) {
                //setData(domain.selectCourses(SelectCoursesDialog.selectCourses(this, domain.getAllCourses(), domain.getSelectedCourse()), true));
                Course[] allCourses = GuiCreator.instance().getCourseList();
                Course[] selectedCourses = SelectCoursesDialog.selectCourses(ClassPanel.this,GuiCreator.instance().getCourseList(),sc.getSelectedSchoolCourses());
                if(selectedCourses!=null)sc.saveSelectedCourses(allCourses,selectedCourses);

    		}
    		fireEditingStopped();
    	}

}

    private JScrollPane jtbl;
    
    private void buildJTable() {
    	if(jtbl != null)
    	{
    		remove(jtbl);
    		jtbl = null;
    	}
    	
    	JTable table = new JTable();
    	jtbl = new JScrollPane(table);
    	jtbl.getViewport().setBackground(GuiConstants.MAIN_BACKGROUND);
        if(GuiCreator.instance().getUser() instanceof Teacher){
	        Teacher t = (Teacher) GuiCreator.instance().getUser();
	        SchoolClass[] classes = t.getClasses();
	        table.setModel(new ClassModel(classes));
        } else
        	return;
        
    	TableUtil.setDefaults(table, false, new ImageRenderer(), new ImageButtonEditor());
        TableUtil.setJTableSizes(table);
        jtbl.setLocation(30, addClassButton.getSize().height
                + addClassButton.getLocation().y + 15);
        TableUtil.setBorder(jtbl);
        TableUtil.shrinkToFit(table, jtbl, 520, 405);
        jtbl.setVisible(false);
        this.add(jtbl);
        jtbl.setVisible(true);

    }
    
    
    /**
     * Creates a new ClassPanel witch shows a list of classes.
     * 
     */
    public ClassPanel() {
        super(null);
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setSize(620, 485);

        /* Add Remove-class image */
        MediaTracker tr = new MediaTracker(this);
        removeImage = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.REMOVE_CLASS_IMAGE);
        editImage = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.EDIT_CLASS_IMAGE);
        usersImage = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.USERS_CLASS_IMAGE);
        assignImage = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.ASSIGN_CLASS_IMAGE);
        tr.addImage(removeImage, 0);
        tr.addImage(editImage, 1);
        tr.addImage(usersImage, 2);
        tr.addImage(assignImage, 3);
        try {
            tr.waitForAll();
        } catch (Exception e) {
        }

        FontMetrics fm;

        addClassButton = new DwoButton(TextMapper.getText(TextMapper.GUIC_ADD_CLASS));
        fm = addClassButton.getFontMetrics(addClassButton.getFont());
        addClassButton.setSize(fm.stringWidth(addClassButton.getLabel()) + 20, fm.getHeight() + 10);
        addClassButton.addActionListener(this);
        addClassButton.setLocation(30, 10);
        addClassButton.setVisible(false);
        this.add(addClassButton);
        addClassButton.setVisible(true);
        
        buildJTable();

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
        center = centerPanel;
    }

    /**
     * Returns a Panel that can function as a header panel.
     * 
     * @return A panel that can function as a header panel.
     * @see fi.dwo.client.gui.CenterSubPanel#getHeaderPanel()
     */
    
    public Component getHeaderPanel() {
    	return new HeaderPanel(TextMapper.getText(TextMapper.GUIC_CLASS_MANAGEMENT));
    }


    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     */
    public void actionPerformed(ActionEvent e) {
    	if(e.getSource() == addClassButton) {
            try {
                GuiCreator.instance().addClass();
                center.loadMenu();
                buildJTable();
            } catch (ClassException e1) {
                JOptionPane.showMessageDialog(this, e1.getMessage(), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
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