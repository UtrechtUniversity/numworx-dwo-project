// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\ClassUsersPanel.java

package fi.dwo.client.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.Hashtable;

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

import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.SchoolClass;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.domain.User;
import fi.dwo.client.gui.ScoManagementPanel.ScoModel;
import fi.dwo.client.system.TextMapper;

/**
 * This class is a panel where the users of a SchoolClass can be viewed and removed.
 * @author M.J.B. Kupers
 *  
 */
public class ClassUsersPanel extends Panel implements CenterSubPanel/*, ActionListener*/ {

    private CenterPanel center;

    private SchoolClass schoolClass;

   // private Table tbl;

    //private Hashtable userDeletebuttons;

	Image removeImage;

	private JScrollPane tbl;

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
    	                tbl.setVisible(false);
    	                arguments = new String[1];
    	                arguments[0] = schoolClass.getName();
    	                String s = TextMapper.getText(TextMapper.GUIC_NO_STUDENTS);
    	                Label label = new Label(MessageFormat.format(s, arguments));
    	                label.setFont(GuiConstants.SCO_TEXT);
    	                FontMetrics fm = label.getFontMetrics(label.getFont());
    	                label.setSize(fm.stringWidth(label.getText()) + 10, fm.getHeight());
    	                label.setLocation((ClassUsersPanel.this.getSize().width/2) - (label.getSize().width/2), 100);
    	                ClassUsersPanel.this.add(label);
    	            }               	}
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
        this.setSize(627, 485);

        schoolClass = c;
        
        User[] users = c.getStudents();
        if(users.length == 0) {
            String[] arguments = new String[1];
            arguments[0] = c.getName();
            String s = TextMapper.getText(TextMapper.GUIC_NO_STUDENTS);
            Label label = new Label(MessageFormat.format(s, arguments));
            label.setFont(GuiConstants.SCO_TEXT);
            FontMetrics fm = label.getFontMetrics(label.getFont());
            label.setSize(fm.stringWidth(label.getText()) + 10, fm.getHeight());
            label.setLocation((this.getSize().width/2) - (label.getSize().width/2), 100);
            this.add(label);
            
        } else {

	        removeImage = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.REMOVE_STUDENT_IMAGE);
	        MediaTracker tr = new MediaTracker(this);
	        tr.addImage(removeImage, 0);
	        try {
	            tr.waitForAll();
	        } catch (Exception e) {
	        }
	
//	        Label l;
//	        DwoButton b;
//	        FontMetrics fm;
//	        int i;
	
	        JTable table = new JTable(new ClassUsersModel());
	        TableUtil.setDefaults(table, false, new ImageRenderer(), new ImageButtonEditor());
	        TableUtil.setJTableSizes(table);
	        tbl = new JScrollPane(table);
			tbl.setLocation(30, 0);

	        TableUtil.shrinkToFit(table, tbl, 602, 492);
	        this.add(tbl);
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
     * Returns a Panel that can functionate as a header panel.
     * 
     * @return A panel that can functionate as a header panel.
     * @see fi.dwo.client.gui.CenterSubPanel#getHeaderPanel()
     */
    public Container getHeaderPanel() {
        Panel p = new BorderedPanel(null);
        p.setBackground(GuiConstants.MAIN_BACKGROUND);
        p.setBounds(181, 20, 449, 71);
        this.add(p);

        /* My Profile-Label */
        Label l = new Label(TextMapper.getText(TextMapper.GUIC_STUDENTS) + " "
                + schoolClass.getName());
        l.setFont(GuiConstants.HEADER_TEXT);
        FontMetrics fm = l.getFontMetrics(l.getFont());
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        l.setLocation((p.getSize().width / 2) - (l.getSize().width / 2), (p.getSize().height / 2)
                - (l.getSize().height / 2));
        p.add(l);

        return p;
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     */
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