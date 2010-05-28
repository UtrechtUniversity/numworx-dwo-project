package fi.dwo.client.gui;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;

import fi.dwo.client.domain.*;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.RegisterException;
import fi.dwo.client.system.TextMapper;
import fi.beans.stringutils.*;

public class RegisterClassListButton extends JButton implements ActionListener
{
	JFrame frame;
	
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
	
	
	
	public RegisterClassListButton(SchoolClass schoolClass)
	{
		super("Voeg nieuwe accouts toe");
		addActionListener(this);
		
		this.schoolClass = schoolClass;
		
		try
		{	systemClipboard = getToolkit().getSystemClipboard ();
		}
		catch(Exception e)
		{	systemClipboard = null;
		}
		
		importClipboardButton = new JButton("Import from clipboard");
		importClipboardButton.addActionListener(this);
		
	    makeAccountsButton = new JButton("Maak accounts");
	    makeAccountsButton.addActionListener(this);
	    
	    addRowButton = new JButton("Extra rij");
	    addRowButton.addActionListener(this);
	    
	    addTableModel = new DefaultTableModel();
	    addTableModel.setDataVector(rowData, columnNames);
	     
	    addTable = new JTable(addTableModel);
	    addTable.setPreferredScrollableViewportSize(new Dimension(800, 400));
	    addTable.setFillsViewportHeight(true);
	    JScrollPane scrollPane = new JScrollPane(addTable);
	    
	    
		bottomPanel = new JPanel();
        bottomPanel.add(importClipboardButton);
        bottomPanel.add(makeAccountsButton);
        bottomPanel.add(addRowButton);
		
        frame = new JFrame();
        //frame.setPreferredSize(new Dimension(800,400));
		
        frame.getContentPane().setLayout(new BorderLayout());
        //frame.getContentPane().add(addTable.getTableHeader(), BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane);
        frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

    }
	
	public boolean pasteFromSystemClipboard()
    {	if(systemClipboard==null)return false;
		Transferable clipboardContent = systemClipboard.getContents(this);
		 	
		 if ((clipboardContent != null) && (clipboardContent.isDataFlavorSupported (DataFlavor.stringFlavor))) 
		 {
		 	try 
		 	{ 	String tempString;
		 		tempString = (String) clipboardContent.getTransferData(DataFlavor.stringFlavor);
		 		System.out.println(tempString);
		 		String[] rowStrings = StringUtils.split(tempString, "\n");
		 		String[][] celStrings = new String[rowStrings.length][];
		 		for(int i=0 ; i<rowStrings.length ; i++)
		    	{	celStrings[i] = StringUtils.split(rowStrings[i], "\t");
		 			System.out.println(rowStrings[i]);
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
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().equals(this))
		{	addTable = new JTable(addTableModel);
			frame.pack();
        	frame.setVisible(true);
        	
		}
		if(e.getSource().equals(addRowButton))
		{	Object[] row = {"","","","","",""};
			addTableModel.addRow(row);
			frame.pack();
		}
		if(e.getSource().equals(importClipboardButton))
		{	
			pasteFromSystemClipboard();
			frame.pack();
		}
		
        if(e.getSource().equals(makeAccountsButton))
		{	
        	for(int i=0 ; i<addTableModel.getRowCount() ; i++)
	    	{
        		String firstname = (String)addTableModel.getValueAt(i, 0);
        		String middlename = (String)addTableModel.getValueAt(i, 1);
        		String lastname = (String)addTableModel.getValueAt(i, 2);
        		String username = (String)addTableModel.getValueAt(i, 3);
        		String password = (String)addTableModel.getValueAt(i, 4);
        		String email = (String)addTableModel.getValueAt(i, 5);
        		String schoollogin = GuiCreator.instance().getUser().getSchool().getSchoolLogin();
        		String schoolpassword = GuiCreator.instance().getUser().getSchool().getPasswd(1);
        		
        		Group g = new Group();
        		g.setGroupID(1);
	    	
        		boolean gemaakt;
	        	try {
	                GuiCreator.instance().dwo.register(username, password, password, firstname, middlename, lastname, email, schoollogin, g, schoolpassword);
	                gemaakt = true;
	        	} catch (RegisterException exc) {
	                JOptionPane.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
	                gemaakt = false;
	        	}
	        	if(gemaakt) {
		            try {
		            	User newUser = PersistenceFacade.instance().login(username, password);
		            	PersistenceFacade.instance().changeAccount(newUser, password, password, firstname, middlename, lastname, email, schoolClass);
		            }	catch (Exception exc) {
			                JOptionPane.showMessageDialog(this, exc.getMessage(), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
		            }
	        	}
	            
	    	}
		}
	}
}
