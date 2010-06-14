// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\SelectCoursesDialog.java

package fi.dwo.client.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.system.TextMapper;

/**
 * This class represents a dialog for selecting courses.
 * 
 * @author M.J.B. Kupers
 *  
 */
public final class SelectCoursesDialog extends JDialog implements ActionListener {

    private Course[] selectedCourses;

    private JButton okButton;

    private JButton cancelButton;
    
    private JButton selectAllButton;
    
    private JButton deselectAllButton;

	private JTable jTable;

    class CoursesModel extends AbstractTableModel {

    	Course[] courses;
    	Object[] select;
    	Object[] data;
    	
		public int getColumnCount() {
			return 3;
		}

		public int getRowCount() {
			return courses.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			switch(columnIndex) {
			case 0: return select[rowIndex];
			case 1: return courses[rowIndex].getName();
			case 2: return data[rowIndex];
			}
			return null;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if(columnIndex != 1)
				return Boolean.TRUE.getClass();
			return super.getColumnClass(columnIndex);
		}

		@Override
		public String getColumnName(int column) {
			switch(column) {
			case 0: return "";
			case 1: return "Module";
			case 2: return "data aanwezig";
			}
			return super.getColumnName(column);
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			switch(columnIndex) {
			case 0: return true;
			case 1: return false;
			case 2: return data[rowIndex] == Boolean.TRUE;
			}
			return super.isCellEditable(rowIndex, columnIndex);
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			switch(columnIndex) {
			case 0:
				select[rowIndex] = aValue;
				break;
			case 2:
				data[rowIndex] = aValue;
			}
			fireTableCellUpdated(rowIndex, columnIndex);
		}
		
    }
    
    
    /**
     * Creates a new instance of a SelectCoursesDialog. It shows a overview of
     * the selected courses, and gives an opportunity to select courses.
     * 
     * @param owner The owner component of the dialog.
     * @param title The title of the dialog.
     * @param modal If true, the dialog is modal.
     * @param allCourses A list of all course to select.
     * @param selectedCourses A list of all the currently selected courses.
     */
    public SelectCoursesDialog(Component owner, String title, boolean modal,
            Course[] allCourses, Course[] selectedCourses) {
        super(DwoHelper.getFrameForComponent(owner), title, modal);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(GuiConstants.MAIN_BACKGROUND);
        setBackground(GuiConstants.MAIN_BACKGROUND);
        setSize(600, 310);

        CoursesModel cm = new CoursesModel();
        cm.courses = allCourses;
        cm.data    = new Boolean[allCourses.length];
        cm.select  = new Boolean[allCourses.length];
        
        this.selectedCourses = null;
        
        /*
         * Create a Vector with all the selected courses. We can now easily
         * check if a course is selected
         */
        Vector vSelectedCourses = new Vector(selectedCourses.length);
        for (int i = 0; i < selectedCourses.length; i++) {
            vSelectedCourses.addElement(selectedCourses[i]);
        }

        jTable = new JTable(cm);

        for (int i = 0; i < allCourses.length; i++) {
            if (vSelectedCourses.contains(allCourses[i])) {
                cm.select[i] = Boolean.TRUE;
            }
        }

        
        
        JScrollPane pane = new JScrollPane(jTable);
        pane.getViewport().setBackground(getBackground());
        pane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        pane.setOpaque(false);
        contentPane.add(pane);
        
        Box southPane = Box.createHorizontalBox();
        southPane.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));
        contentPane.add(southPane, BorderLayout.SOUTH);
        /* Select All Button */
        selectAllButton = new JButton(TextMapper.getText(TextMapper.GUISC_BTN_SELECT_ALL));
        selectAllButton.addActionListener(this);
        southPane.add(selectAllButton);
        southPane.add(Box.createHorizontalStrut(5));
        /* Deselect All Button */
        deselectAllButton = new JButton(TextMapper.getText(TextMapper.GUISC_BTN_DESELECT_ALL));
        deselectAllButton.addActionListener(this);
        southPane.add(deselectAllButton);

        southPane.add(Box.createGlue());
        
        /* Cancel button */
        cancelButton = new JButton(TextMapper.getText(TextMapper.BTN_CANCEL));
        cancelButton.addActionListener(this);
        southPane.add(cancelButton);
        southPane.add(Box.createHorizontalStrut(5));

        /* Ok button */
        okButton = new JButton(TextMapper.getText(TextMapper.BTN_OK));
        okButton.addActionListener(this);
        southPane.add(okButton);


        // set location to center of parent
        int x = 0;
        int y = 0;

        Point p = owner != null ? owner.getLocation() : new Point(0, 0);
        Dimension parentSize = owner != null ? owner.getSize()
                : Toolkit.getDefaultToolkit().getScreenSize();
        Dimension mySize = getSize();
        x = p.x + (parentSize.width - mySize.width) / 2;
        y = p.y + (parentSize.height - mySize.height) / 2;

        setLocation(x, y);
    }

    /**
     * Shows the SelectCoursesDialog and returns the selected courses.
     * 
     * @param parent The parent component of the dialog.
     * @param allCourses A list of all the possible courses.
     * @param selectedCourses A list of all the selected courses.
     * @return A list of all the selected courses.
     */
    public static Course[] selectCourses(Component parent, Course[] allCourses,
            Course[] selectedCourses) {
        String title = TextMapper.getText(TextMapper.GUISC_TITLE);
        SelectCoursesDialog scd = new SelectCoursesDialog(parent, title, true, allCourses, selectedCourses);
        scd.show();
        return scd.getSelectedCourses();
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelButton) {
        	selectedCourses = null;
        	this.setVisible(false);
        } else if (e.getSource() == okButton) {
            Vector tmpSelected = new Vector();
            CoursesModel model = (CoursesModel) jTable.getModel();
            int len = model.getRowCount();
            for(int i = 0; i < len; i++ )
            {
            	if(Boolean.TRUE.equals(model.getValueAt(i, 0)))
            		tmpSelected.addElement(model.courses[i]);
            }

            selectedCourses = new Course[tmpSelected.size()];
            tmpSelected.copyInto(selectedCourses);
            this.hide();

        } else if (e.getSource() == selectAllButton) {
            TableModel model = jTable.getModel();
            int len = model.getRowCount();
            for(int i = 0; i < len; i++)
            	model.setValueAt(Boolean.TRUE, i, 0);
            
        } else if (e.getSource() == deselectAllButton) {
            TableModel model = jTable.getModel();
            int len = model.getRowCount();
            for(int i = 0; i < len; i++)
            	model.setValueAt(Boolean.FALSE, i, 0);
            
            
        }

    }

    /**
     * Returns all the selected courses.
     * 
     * @return All the selected courses.
     */
    public Course[] getSelectedCourses() {
        return selectedCourses;
    }

}