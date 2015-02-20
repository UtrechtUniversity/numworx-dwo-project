package fi.dwo.dwojapplet.gui;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;

import fi.dwo.dwojapplet.domain.*;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import fi.dwo.commons.exceptions.RegisterException;
import fi.dwo.commons.system.TextMapper;

public class RegisterClassListButton extends JButton implements ActionListener
{
	JDialog frame;
	JPanel  content;
	boolean modal;
	
	DefaultTableModel addTableModel;
	JTable addTable;
	
	SchoolClass schoolClass;
	JPanel bottomPanel;
	
	JButton importClipboardButton;
    JButton makeAccountsButton;
    JButton addRowButton;
    
    private Clipboard systemClipboard;
    
    
    Object[] columnNames = {
    		TextMapper.getText(TextMapper.GUIR_FIRSTNAME),
    		TextMapper.getText(TextMapper.GUIR_MIDDLENAME),
    		TextMapper.getText(TextMapper.GUIR_LASTNAME),
    		TextMapper.getText(TextMapper.GUIR_USERNAME),
    		TextMapper.getText(TextMapper.GUIR_PASSWORD),
    		TextMapper.getText(TextMapper.GUIR_EMAIL)};
    
	Object[][] rowData = {{ "","","","","",""}};

	private int sg;
	
	/**
	 * Register nieuwe leerlingen. Zet ze meteen in een klas
	 * @param schoolClass
	 * @param p 
	 */
	public RegisterClassListButton(SchoolClass schoolClass)
	{
		super(TextMapper.getText(TextMapper.GUIUMP_ADD_STUDENTS));
		this.schoolClass = schoolClass;
		this.sg = SchoolGroup.STUDENT;
		initialize();
    }
	
	/**
	 * Register nieuwe docenten.
	 */
	public RegisterClassListButton()
	{
		super(TextMapper.getText(TextMapper.GUIUMP_ADD_TEACHERS));
		this.sg = SchoolGroup.TEACHER;
		this.schoolClass = null;
		this.modal = true;
		initialize();
	}

	private void initialize() {
// for languages without middlename
		if( columnNames[1].toString().length()==0)
		{
			Object[] old = columnNames;
			columnNames = new Object[5];
			columnNames[0] = old[0];
			System.arraycopy(old, 2, columnNames, 1, 4);
			rowData = new Object[][] {{"","","","",""}};
		}
		
		
		
		
		
		addActionListener(this);
		try
		{	systemClipboard = getToolkit().getSystemClipboard ();
		}
		catch(Exception e)
		{	systemClipboard = null;
		}
		
		importClipboardButton = new JButton(TextMapper.getText(TextMapper.GUIUMP_IMPORT_CLIPBOARD));
		importClipboardButton.addActionListener(this);
		
	    makeAccountsButton = new JButton(TextMapper.getText(TextMapper.GUIUMP_MAKE_ACCOUNTS));
	    makeAccountsButton.addActionListener(this);
	    
	    addRowButton = new JButton(TextMapper.getText(TextMapper.GUIUMP_EXTRA_ROW));
	    addRowButton.addActionListener(this);
	    
	    addTableModel = new DefaultTableModel();
	    addTableModel.setDataVector(rowData, columnNames);
	     
	    addTable = new JTable(addTableModel);
	    addTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
	    addTable.setPreferredScrollableViewportSize(new Dimension(800, 400));
	    //addTable.setFillsViewportHeight(true);
	    JScrollPane scrollPane = new JScrollPane(addTable);
	    
	    
		bottomPanel = new JPanel();
		if(DwoHelper.isSecure())bottomPanel.add(importClipboardButton);
        bottomPanel.add(makeAccountsButton);
        bottomPanel.add(addRowButton);
		
        content = new JPanel(new BorderLayout());
        //frame.setPreferredSize(new Dimension(800,400));
		
        //content.add(addTable.getTableHeader(), BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);
        content.add(bottomPanel, BorderLayout.SOUTH);
	}
	
	public boolean pasteFromSystemClipboard()
    {	if(systemClipboard==null)return false;
		Transferable clipboardContent = systemClipboard.getContents(this);
		 	
		 if ((clipboardContent != null) && (clipboardContent.isDataFlavorSupported (DataFlavor.stringFlavor))) 
		 {
		 	try 
		 	{ 	String tempString;
		 		tempString = (String) clipboardContent.getTransferData(DataFlavor.stringFlavor);
// Wim: Remove ending \n, niet nodig als je String.split (1.4.2) gebruikt.
//		 		if(tempString.endsWith("\n"))
//		 		{
//		 			tempString = tempString.substring(0, tempString.length()-1); 
//		 		}
		 		//System.out.println(tempString);
		 		String[] rowStrings = tempString.split("\n"); // was: StringUtils.split(tempString, "\n");
		 		String[][] celStrings = new String[rowStrings.length][];
		 		for(int i=0 ; i<rowStrings.length ; i++)
		    	{	celStrings[i] = rowStrings[i].split("\t", columnNames.length); // was: StringUtils.split(rowStrings[i], "\t");
		 			//System.out.println(rowStrings[i]);
		    	}
		    	addTableModel.setDataVector(celStrings, columnNames);
				return true;
		    }
		    catch (Exception e) 
		    {  	 e.printStackTrace ();
		    		return false;
		    }
		 }
		 else return false;
    }
	
	private static final String CLOSING = "closing";
	class OnClose extends WindowAdapter {

                @Override
		public void windowClosing(WindowEvent e) {
			ActionEvent ae = new ActionEvent(frame, ActionEvent.ACTION_PERFORMED, CLOSING);
			fireActionPerformed(ae);
		}
	}
	
	
        @Override
	public void actionPerformed(ActionEvent e)
	{
		if(CLOSING.equals(e.getActionCommand()))
			return;
		
		if(e.getSource().equals(this))
		{
			frame = new JDialog(JOptionPane.getFrameForComponent(this), getText(), modal);
			frame.addWindowListener(new OnClose());
			frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
			frame.setContentPane(content);
			frame.pack();
        	frame.setVisible(true);
		}
		if(e.getSource().equals(addRowButton))
		{	Object[] row = new Object[columnNames.length];
			for (int i = 0; i < row.length; i++) {
				row[i] = "";
			}
			addTableModel.addRow(row);
			frame.pack();
		}
		if(e.getSource().equals(importClipboardButton))
		{	
			pasteFromSystemClipboard();
			frame.pack();
		}
		
        if(e.getSource().equals(makeAccountsButton))
		{	boolean error = false;
        	for(int i=0 ; i<addTableModel.getRowCount() ; i++)
	    	{
        		int v = 6 - columnNames.length;
        		
        		
        		String firstname = (String)addTableModel.getValueAt(i, 0);
        		String middlename = 
        			v == 0?
        			(String)addTableModel.getValueAt(i, 1)
        			: "";
        		String lastname = (String)addTableModel.getValueAt(i, 2-v);
        		String username = (String)addTableModel.getValueAt(i, 3-v);
        		String password = (String)addTableModel.getValueAt(i, 4-v);
        		String email = (String)addTableModel.getValueAt(i, 5-v);

        		String schoollogin = GuiCreator.instance().getUser().getSchool().getSchoolLogin();
        		String schoolpassword = GuiCreator.instance().getUser().getSchool().getPasswd(sg);
        		
        		Group g = new Group();
        		g.setGroupID(sg);
	    	
        		boolean gemaakt;
	        	try {
	                GuiCreator.instance().dwo.register(username, password, password, firstname, middlename, lastname, email, schoollogin, g, schoolpassword);
	                gemaakt = true;
	        	} catch (RegisterException exc) {
	                JOptionPane.showMessageDialog(frame, exc.getMessage(), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
	                gemaakt = false;
	                error = true;
	        	}
	        	// zet in de class
	        	if(gemaakt && schoolClass != null) {
		            try {
		            	User newUser = PersistenceFacade.instance().login(username, password);
		            	PersistenceFacade.instance().changeAccount(newUser, password, password, firstname, middlename, lastname, email, schoolClass);
		            	i = reduceTable(i);
		            
		            }	catch (Exception exc) {
		            		error = true;
			                JOptionPane.showMessageDialog(frame, exc.getMessage(), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
		            }
	        	} else if (gemaakt) {
	            	i = reduceTable(i);
	        	}
	            
	    	}
        	if(schoolClass != null)
        		GuiCreator.instance().getMainPanel().getCenter().updateClass(schoolClass);
        	else
        		GuiCreator.instance().getMainPanel().getCenter().updateSchool(GuiCreator.instance().getUser().getSchool());
        	if (!error && frame.isModal())
        	{  //System.out.println("frame hide");
        		frame.hide();
        	}
 		}
	}

	/**
	 * @param i
	 * @return
	 */
	private int reduceTable(int i) {
		if(addTableModel.getRowCount() > 1)
		{
			addTableModel.removeRow(i);i--;
		}
		else {
			int len = addTableModel.getColumnCount();
			for(int col = 0 ; col < len ; col ++) addTableModel.setValueAt("", i, col);
		}
		return i;
	}
}
