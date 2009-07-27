/*
 * Created on Mar 24, 2005
 *
 */
package fi.dwo.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.Hashtable;

import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.Teacher;
import fi.dwo.client.system.SchoolException;
import fi.dwo.client.system.TextMapper;

/**
 * The panel where a School can be managed.
 * @author M.J.B. Kupers
 * @author Wim van Velthoven
 *
 */
public class SchoolPanel extends JPanel implements CenterSubPanel, ActionListener {

    public class ImageButtonEditor extends AbstractCellEditor implements
			TableCellEditor, ActionListener {

    	Object value;
    	SchoolModel model;
    	int row;
    	
		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean arg2, int row, int col) {
			this.value = value;
			JButton button = new JButton(new ImageIcon((Image)value));
			button.addActionListener(this);
			this.row = row;
			model = (SchoolModel) table.getModel();
			return button;
		}

		public Object getCellEditorValue() {
			return value;
		}

		public void actionPerformed(ActionEvent event) {
			if(value == editImage)
			{
				try {
					School s = AddSchoolDialog.editSchool(SchoolPanel.this, model.school[row]);
					if(s != null)
						model.fireTableRowsUpdated(row, row);
				} catch (SchoolException e) {
					e.printStackTrace();
				}
			} else if (value == removeImage)
			{
                /* Delete the school */
                School sc = model.school[row];
                if (JOptionPane.showConfirmDialog(SchoolPanel.this, TextMapper.getText(TextMapper.GUIS_MSG_DELETE_SCHOOL)
                       + "?", TextMapper.getText(TextMapper.GUIS_DELETE_SCHOOL), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    if ( GuiCreator.instance().deleteSchool(sc) ) {
                        center.loadMenu();
        	            model.deleteRow(row);
                    }   
                }
			}
			fireEditingStopped();
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
			case 4:	String s = TextMapper.getText(TextMapper.GUIS_TLTP_USERS_SCHOOL);
	    			setToolTipText(MessageFormat.format(s, arguments));
	    			break;
			case 5: setToolTipText(TextMapper.getText(TextMapper.GUIS_TLTP_EDIT_SCHOOL));
				break;
			case 6: String format = TextMapper.getText(TextMapper.GUIS_TLTP_DELETE_SCHOOL);
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

	class SchoolModel extends AbstractTableModel implements TableModel {

		private School[] school;

		public SchoolModel(School[] school) {
			this.school = school;
		}

		public int getColumnCount() {
			return 7;
		}

		public Class getColumnClass(int col) {
			if(col >= 4)
				return Image.class;
			return super.getColumnClass(col);
		}

		public boolean isCellEditable(int row, int col) {
			if(col >= 4)
				return true;
			return super.isCellEditable(row, col);
		}

		public int getRowCount() {
			return school.length;
		}

		public Object getValueAt(int row, int col) {
			School s = school[row];
			switch(col) {
			case 0: return s.getName();
			case 1: return s.getSchoolLogin();
			case 2: try {
						return s.getSchoolGroupList()[0].getPasswd();
					} catch (RuntimeException e) {
					} 
					break;
			case 3: try {
						return s.getSchoolGroupList()[1].getPasswd();
					} catch (RuntimeException e) {
					}
					break;
			case 4: return usersImage;
			case 5: return editImage;
			case 6: return removeImage;
			}
 			
			return "";
		}

		public String getColumnName(int col) {
			switch(col) {
			case 0: return "School";
			case 1: return "Login";
			case 2: return "Leerling";
			case 3: return "Docent";
			}
			return "";
		}

		void deleteRow(int row) {
			School[] newSchool = new School[school.length-1];
			System.arraycopy(school, 0, newSchool, 0, row);
			System.arraycopy(school, row+1, newSchool, row, newSchool.length-row);
			school = newSchool;
			fireTableRowsDeleted(row, row);
		}
	}

	private CenterPanel center;

    private JButton addSchoolButton, copyButton;
    
    private Image removeImage, editImage, usersImage, assignImage;

    /**
     * Creates a new SchoolPanel witch shows a list of schools.
     * 
     */
    public SchoolPanel() {
        super(null);
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setSize(627, 485);
        setPreferredSize(getSize());

        /* Add Remove-school image */
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

        int w; 
        addSchoolButton = new JButton(TextMapper.getText(TextMapper.GUIS_ADD_SCHOOL));
        addSchoolButton.setSize(addSchoolButton.getPreferredSize());
        w = addSchoolButton.getWidth();
        addSchoolButton.addActionListener(this);
        addSchoolButton.setLocation(30, 10);
        this.add(addSchoolButton);
        
        copyButton = new JButton(/*FIXME*/ "Copy");
        copyButton.setSize(copyButton.getPreferredSize());
        copyButton.addActionListener(this);
        copyButton.setLocation(30 + w + 10, 10);
        add(copyButton);
        
        buildJTable();

    }

    /**
     * Indicate that another panel is loaded and the connections of this panel
     * must be closed.
     */
    public void end() {

    }
    
    private JScrollPane jtbl;
    private void buildJTable() {
    	if (jtbl != null)
    	{
    		remove(jtbl);
    		jtbl = null;
    	}
    	JTable table = new JTable();
    	
    	table.setModel(new SchoolModel(GuiCreator.instance().getSchool()));
    	TableUtil.setDefaults(table, true, new ImageRenderer(), new ImageButtonEditor());
// de volgende regel heeft geen effect.
// je moet dan zelf een TableCellRenderer installeren.
        //table.getTableHeader().setBackground(GuiConstants.CELL_BACKGROUND);
        TableUtil.setJTableSizes(table);
        //table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumnModel m = table.getColumnModel();
        for(int i = 4; i < table.getColumnCount(); i++)
        {
        	m.getColumn(i).setMinWidth(m.getColumn(i).getPreferredWidth());
        	m.getColumn(i).setMaxWidth(m.getColumn(i).getPreferredWidth());
        }
        table.setSize(table.getPreferredSize());
    	//JPanel panel = new JPanel(new BorderLayout());
    	//panel.add(table.getTableHeader(),BorderLayout.NORTH);
    	//panel.add(table, BorderLayout.CENTER);
    	//panel.setPreferredSize(table.getPreferredSize());
    	jtbl = new JScrollPane(table);
        TableUtil.setBorder(jtbl);
    	int h = addSchoolButton.getSize().height + addSchoolButton.getLocation().y + 5;
        jtbl.setBounds(5, h, 627 - 5 , 492 - h - 5);
        jtbl.validate();
        this.add(jtbl);
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
    public Component getHeaderPanel() {
    	return new HeaderPanel(TextMapper.getText(TextMapper.GUIS_SCHOOL_MANAGEMENT));
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     */
    public void actionPerformed(ActionEvent e) {
    	if ( e.getSource() == copyButton)
    	{
    		ClipboardExport.instance().export(GuiCreator.instance().getSchool());
    		return;
    	}
        if(e.getSource() == addSchoolButton) {
            try {
            	School s = AddSchoolDialog.addSchool(this);
            	if(s != null) {
	            	buildJTable(); 
	            }               
            } catch (SchoolException ex) {
            	JOptionPane.showMessageDialog(this, ex.getMessage());
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