// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\ClassUsersPanel.java

package fi.dwo.client.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.SchoolClass;
import fi.dwo.client.domain.User;
import fi.dwo.client.system.TextMapper;

/**
 * This class is a panel where the users of a SchoolClass can be viewed and removed.
 * @author M.J.B. Kupers
 * @author Velth101
 *  
 */
public class ClassUsersPanel extends JPanel implements CenterSubPanel/*, ActionListener*/ {

    private CenterPanel center;

    private SchoolClass schoolClass;

	Image removeImage;

	//private Box tbl;

    class ClassUsersModel extends AbstractTableModel {

		private User[] students;

		ClassUsersModel() {
			refresh();
		}

		private void refresh() { 
			students = schoolClass.getStudents();
			fireTableDataChanged();
		}
		
		public int getColumnCount() {
			return 2;
		}

		public int getRowCount() {
			return students.length;
		}

		public Object getValueAt(int row, int col) {
			switch (col) {
			case 0:
				return students[row].getName();
			case 1:
				return removeImage;
			}
			
			return null;
		}

		public Class getColumnClass(int col) {
			if(col > 0)
				return Image.class;
			return super.getColumnClass(col);
		}

		public boolean isCellEditable(int row, int col) {
			return col > 0;
		}
    	
    }
	public class ImageRenderer extends JLabel implements TableCellRenderer {

		private ImageIcon icon = new ImageIcon();

		/**
		 * 
		 */
		private ImageRenderer() {
			setHorizontalAlignment(SwingConstants.CENTER);
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean selected, boolean hasFocus, int row, int col) {
			Image image = (Image)value;
			if(image != null) {
				icon.setImage(image);
				setIcon(icon);
			} else {
				setIcon(null);
			}
			setHorizontalAlignment(SwingConstants.CENTER);
			setOpaque(true);
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
    	ClassUsersModel model;
    	int row;

    	public Component getTableCellEditorComponent(JTable table, Object value,
    			boolean arg2, int row, int col) {
    		this.value = value;
    		JButton button = new JButton(new ImageIcon((Image)value));
    		button.addActionListener(this);
    		this.row = row;
    		model = (ClassUsersModel) table.getModel();
    		return button;
    	}

    	public Object getCellEditorValue() {
    		return value;
    	}

    	public void actionPerformed(ActionEvent event) {
            User u = schoolClass.getStudents()[row];
    		if (value == removeImage) {
                String[] arguments = new String[1];
                arguments[0] = u.getName();
                String msg = TextMapper.getText(TextMapper.GUIC_MSG_DELETE_STUDENT);
                msg = MessageFormat.format(msg, arguments);
                if (JOptionPane.showConfirmDialog(ClassUsersPanel.this, msg
                        + "?", TextMapper.getText(TextMapper.GUIC_DELETE_STUDENT), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                	
                	u.setInClass(null);
                	schoolClass.disconnect(u);
                 	model.refresh();
    	            if(model.getRowCount() == 0) {
    	                //tbl.setVisible(false);
    	                arguments = new String[1];
    	                arguments[0] = schoolClass.getName();
    	                String s = TextMapper.getText(TextMapper.GUIC_NO_STUDENTS);
    	                JLabel label = new JLabel(MessageFormat.format(s, arguments));
    	                label.setFont(GuiConstants.SCO_TEXT);
    	                //FontMetrics fm = label.getFontMetrics(label.getFont());
    	                //label.setSize(fm.stringWidth(label.getText()) + 10, fm.getHeight());
    	                //label.setLocation((ClassUsersPanel.this.getSize().width/2) - (label.getSize().width/2), 100);
    	                label.setAlignmentY(0.24f);
    	                ClassUsersPanel.this.removeAll();
    	                ClassUsersPanel.this.add(label);
    	                ClassUsersPanel.this.repaint();
    	            }
    	        }
            } 
    		fireEditingStopped();
    	}

}

    
    /**
     * Creates a new ClassUsersPanel witch shows the students of the class.
     * 
     * @param c The SchoolClass of the ClassUsersPanel.
     */
    public ClassUsersPanel(SchoolClass c) {
        super(null);
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createEmptyBorder(15, 30, 0, 0));

        schoolClass = c;
        
        User[] users = c.getStudents();
        if(users.length == 0) {
            String[] arguments = new String[1];
            arguments[0] = c.getName();
            String s = TextMapper.getText(TextMapper.GUIC_NO_STUDENTS);
            JLabel label = new JLabel(MessageFormat.format(s, arguments));
            label.setFont(GuiConstants.SCO_TEXT);
			label.setAlignmentY(0.24f);
            this.add(label);
            
        } else {

	        removeImage = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.REMOVE_STUDENT_IMAGE);
	        MediaTracker tr = new MediaTracker(this);
	        tr.addImage(removeImage, 0);
	        try {
	            tr.waitForAll();
	        } catch (Exception e) {
	        }
	        JTable table = new JTable(new ClassUsersModel());
	        TableUtil.setDefaults(table, false, new ImageRenderer(), new ImageButtonEditor());
	        TableUtil.setJTableSizes(table);
			TableUtil.setBorder(table);
			
			Dimension size = table.getPreferredSize();
			if(size.width < 602)
				size.width = 602;
			table.setMaximumSize(size);
			table.setAlignmentX(0);
			table.setAlignmentY(0);
	        add(table);
	        add(Box.createHorizontalGlue());
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
        center = centerPanel;
    }

    /**
     * Returns a Panel that can function as a header panel.
     * 
     * @return A panel that can function as a header panel.
     * @see fi.dwo.client.gui.CenterSubPanel#getHeaderPanel()
     */
    public Component getHeaderPanel() {
    	return new HeaderPanel(TextMapper.getText(TextMapper.GUIC_STUDENTS) + " "
                + schoolClass.getName());
    }

//    /**
//     * Invoked when an action occurs.
//     * 
//     * @param e The ActionEvent.
//     */
//    public void actionPerformed(ActionEvent e) {
//        if (e.getSource() instanceof ImageButton) {
//            User u = (User) userDeletebuttons.get(e.getSource());
//            String[] arguments = new String[1];
//            arguments[0] = u.getName();
//            String msg = TextMapper.getText(TextMapper.GUIC_MSG_DELETE_STUDENT);
//            msg = MessageFormat.format(msg, arguments);
//            
//            if (DwoMessageDialog.showConfirmDialog(this, msg
//                    + "?", TextMapper.getText(TextMapper.GUIC_DELETE_STUDENT), DwoMessageDialog.YES_NO_OPTION) == DwoMessageDialog.YES_OPTION) {
//	            u.setInClass(null);
//	            schoolClass.disconnect(u);
//	            tbl.removeRow((ImageButton) e.getSource());
//	            if(tbl.getNrRows() == 0) {
//	                tbl.setVisible(false);
//	                arguments = new String[1];
//	                arguments[0] = schoolClass.getName();
//	                String s = TextMapper.getText(TextMapper.GUIC_NO_STUDENTS);
//	                Label label = new Label(MessageFormat.format(s, arguments));
//	                label.setFont(GuiConstants.SCO_TEXT);
//	                FontMetrics fm = label.getFontMetrics(label.getFont());
//	                label.setSize(fm.stringWidth(label.getText()) + 10, fm.getHeight());
//	                label.setLocation((this.getSize().width/2) - (label.getSize().width/2), 100);
//	                this.add(label);
//	                
//	            }
//            }
//        }
//
//    }

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