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
import java.awt.BorderLayout;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.SchoolClass;
import fi.dwo.client.domain.User;
import fi.dwo.client.persistence.MapperCreator;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.LoginException;
import fi.dwo.client.system.RegisterException;
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

	Image removeImage, editImage, userImage;

	//private Box tbl;


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
    	UserModel model;
    	int row;

    	public Component getTableCellEditorComponent(JTable table, Object value,
    			boolean arg2, int row, int col) {
    		this.value = value;
    		JButton button = new JButton(new ImageIcon((Image)value));
    		button.addActionListener(this);
    		this.row = row;
    		model = (UserModel) table.getModel();
    		return button;
    	}

    	public Object getCellEditorValue() {
    		return value;
    	}

    	public void actionPerformed(ActionEvent event) {
            User u = model.userList[row];
			if(value == model.userImage )
			{
				try {
					MapperCreator.instance(User.class).removeObject(u.getID()); // not good enough, need fresh copy.
					GuiCreator.instance().login(u.getUsername(), null);
				} catch (LoginException e) {
					e.printStackTrace();
				}
			} else
			if(value == model.editImage)
			{
				try {
					String newPassword =  JOptionPane.showInputDialog(ClassUsersPanel.this, TextMapper.getText(TextMapper.GUIP_PASSWORD), u.getUsername(), JOptionPane.QUESTION_MESSAGE);
					if(newPassword != null)
					{
						PersistenceFacade.instance().changeAccount(u, null, newPassword, u.getFirstname(), u.getMiddleName(), u.getLastName(), u.getEmail());
						model.fireTableRowsUpdated(row, row);
						JOptionPane.showMessageDialog(ClassUsersPanel.this, TextMapper.getText(TextMapper.GUIP_MSG_PROFILE_CHANGED));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}	else
			if (value == model.removeImage) {
            String[] arguments = new String[1];
            arguments[0] = u.getName();
            String msg = TextMapper.getText(TextMapper.GUIC_MSG_DELETE_STUDENT);
            msg = MessageFormat.format(msg, arguments);
            if (JOptionPane.showConfirmDialog(ClassUsersPanel.this, msg
                    + "?", TextMapper.getText(TextMapper.GUIC_DELETE_STUDENT), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            	
            	u.setInClass(null);
            	schoolClass.disconnect(u);
             	model.deleteRow(row);
	            if(model.getRowCount() == 0) {
	                //tbl.setVisible(false);
	                arguments = new String[1];
	                arguments[0] = schoolClass.getName();
	                String s = TextMapper.getText(TextMapper.GUIC_NO_STUDENTS);
	                JLabel label = new JLabel(MessageFormat.format(s, arguments));
	                label.setFont(GuiConstants.SCO_TEXT);
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
        Box vbox = Box.createVerticalBox();
        User[] users = c.getStudents();
        if(users.length == 0) {
            String[] arguments = new String[1];
            arguments[0] = c.getName();
            String s = TextMapper.getText(TextMapper.GUIC_NO_STUDENTS);
            JLabel label = new JLabel(MessageFormat.format(s, arguments));
            label.setFont(GuiConstants.SCO_TEXT);
			label.setAlignmentY(0.24f);
			
			vbox.setAlignmentX(0);
			vbox.setAlignmentY(0);
			vbox.add(label);
			
			vbox.add(Box.createVerticalStrut(20));
	        add(vbox);
			
            
        } else {

	        removeImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_STUDENT_IMAGE);
	        userImage = DwoHelper.getResourceImage("resources/student.png" );
	        editImage = DwoHelper.getResourceImage(GuiConstants.EDIT_SCO_IMAGE);
	        
	        MediaTracker tr = new MediaTracker(this);
	        tr.addImage(removeImage, 0);
	        tr.addImage(userImage, 0);
	        tr.addImage(editImage, 0);
	        try {
	            tr.waitForAll();
	        } catch (Exception e) {
	        }
	        
			UserModel dm = new UserModel();
			dm.userList = users; users = null;
			dm.editImage = editImage;
			dm.removeImage = removeImage;
			dm.teacherImage = userImage;
			dm.userImage = userImage;
			JTable table = new JTable(dm);

	        //JTable table = new JTable(new ClassUsersModel());
	        TableUtil.setDefaults(table, true, new ImageRenderer(), new ImageButtonEditor());
			School school = dm.userList[0].getSchool();
			SchoolClassTableRenderer renderer = new SchoolClassTableRenderer(school);
			table.setDefaultRenderer(SchoolClass.class, new SchoolClassTableRenderer(renderer.getItems()));
	    	table.setDefaultEditor(SchoolClass.class, new DefaultCellEditor(renderer));

			TableUtil.setBorder(table);
	    	TableUtil.setJTableSizes(table);
			

			
			//vbox.setAlignmentX(0);
			vbox.setAlignmentY(0);
			Dimension size = table.getPreferredSize();
//			table.setMinimumSize(size);
//			if(size.width < 702)
//				size.width = 702;
//			table.setMaximumSize(size);
	        JTableHeader tableHeader = table.getTableHeader();
			vbox.add(tableHeader);
			vbox.add(table);
			add(vbox);
	        add(Box.createHorizontalGlue());
        }
		RegisterClassListButton registerClassListButton = new RegisterClassListButton(schoolClass);

		if(GuiCreator.instance().getUser().hasRight(User.CHANGE_CLASS_RIGHT))
			vbox.add(registerClassListButton);
        
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
    public JComponent getComponent() {
        return this;
    }



	public Object getUserObject() {
		// TODO Auto-generated method stub
		return null;
	}



	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		
	}
}