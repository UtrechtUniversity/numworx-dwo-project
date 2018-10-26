package fi.dwo.dwojapplet.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;

import javax.swing.*;

import fi.dwo.commons.system.TextMapper;

public class DomainModelEditor extends JPanel implements ActionListener
{
	private JTextField[][] objectiveTextFields;
	private JLabel[] objectiveLabels;
	private JTextField[] categoryTextFields;
	
	private String[] categorieString;
	
	private int maxObjectives  = 20;
	private int maxCategories = 10; // Sietske heeft er 7 in haar nieuwe domainmodel
	int aantalRijen = 4;
	int aantalKolommen = 1;
	PlusMinKnop aantalRijenKnop;
	PlusMinKnop aantalKolommenKnop;
	private String[][] objectives;
	private JButton okButton; 
	private JButton cancelButton;
	
	JPanel objectivesPanel = this;
	JPanel bottomPanel = new JPanel();
	
	private String rowLabel;
	private String columnLabel;

	DomainModelEditor() {
	  this(TextMapper.getText("OBJ_leerdoel"), TextMapper.getText("OBJ_categorie"));
	  makeTextFields();
	  makeGUI(aantalRijen, aantalKolommen);
	}
	
	DomainModelEditor(String rowLabel, String columnLabel){	
		super();
		setOpaque(true);
		setBackground(Color.white);
		this.rowLabel = rowLabel;
		this.columnLabel = columnLabel;
	}
	
	public void setObjectives(String[][] objectives){   
		this.objectives = objectives;
	}
	
	public void setCategories(String[] categorieString){   
		this.categorieString = categorieString;
	}
		
	private void makeObjects(){   
    	objectives = null;
    	String[] newObjects = null;
    	for(int j=0 ; j<maxCategories ; j++)
    	{	String checkObject = objectiveTextFields[j][0].getText();
   			if(checkObject==null || "".equals(checkObject.trim()))
   			{	objectives = new String[j][];
   				break;
   			}
   			if(objectives == null)
   				objectives = new String[maxCategories][];
    	}
    	for(int j=0 ; j<objectives.length ; j++)
    	{	newObjects = new String[maxObjectives];
	        for(int i=0 ; i<maxObjectives ; i++){   
	        	String checkObject = objectiveTextFields[j][i].getText();
	       		if(checkObject!=null && !"".equals(checkObject.trim())){	
	       			newObjects[i] = checkObject;
	            }
	            else{   
	            	objectives[j] = new String[i];
	            	break;
	            }
	        }
	        if(objectives[j]==null){
	        	objectives[j] = new String[maxObjectives];
	        }
	        for(int i=0 ; i<objectives[j].length ; i++){
	        	objectives[j][i] = newObjects[i];
	        }
    	}
    	categorieString = null;
    	categorieString = new String[objectives.length];
    	for(int i = 0; i < objectives.length; i++)
    	{	categorieString[i] = categoryTextFields[i].getText();
    		if(categorieString[i].equals(columnLabel + " "  + (i+1)))
    			categorieString[i] = "";
    	}	
    }
    
    
    public String[][] getObjectives(){   
    	return objectives;
    }
    
    public String[] getCategories(){
    	return categorieString;
    }
        
    public void makeTextFields()
    {	objectiveTextFields = new JTextField[maxCategories][maxObjectives];
	    objectiveLabels = new JLabel[maxObjectives];
	    categoryTextFields = new JTextField[maxCategories];
	    for(int j = 0; j < maxCategories; j++)
	    {	categoryTextFields[j] = new JTextField(columnLabel + " "  + (j+1));
	    	categoryTextFields[j].setPreferredSize(new Dimension(180,20));
	    }
	    for(int i=0 ; i<maxObjectives ; i++)
	    {	objectiveLabels[i] = new JLabel(rowLabel + " " +(i+1));
	   		objectiveLabels[i].setPreferredSize(new Dimension(100,20));
	   		for(int j = 0; j<maxCategories; j++)
	        {	objectiveTextFields[j][i] = new JTextField("");
	        	objectiveTextFields[j][i].setPreferredSize(new Dimension(180,20));
	        }
	    }
    }
    
    public void makeGUI(int aantalRijen, int aantalKolommen){
    	objectivesPanel = this;
    	objectivesPanel.removeAll();
		bottomPanel = new JPanel();
        
		Box boxh1 = Box.createHorizontalBox();
		Box boxv = Box.createVerticalBox();
        Box boxh = Box.createHorizontalBox();
        
        JLabel leegLabel = new JLabel("");
        leegLabel.setPreferredSize(new Dimension(100,20));
        boxh.add(leegLabel);
        
        for(int j = 0; j < aantalKolommen; j++)
        	boxh.add(categoryTextFields[j]);
        
        boxv.add(boxh);
        
        for(int i = 0; i < aantalRijen; i++)
        {	boxh = Box.createHorizontalBox();
        	boxh.add(objectiveLabels[i]);
        	for(int j = 0; j < aantalKolommen; j++)
        		boxh.add(objectiveTextFields[j][i]);
        	boxv.add(boxh);
        }
        
        boxh = Box.createHorizontalBox();
        boxh.add(Box.createHorizontalStrut(20));
        
        aantalRijenKnop = new PlusMinKnop(0, 0, 16, 20, PlusMinKnop.VERTIKAAL);
        aantalRijenKnop.setPreferredSize(new Dimension(16, 20));
        aantalRijenKnop.setSize(getPreferredSize());
        aantalRijenKnop.addActionListener(this);
        boxh.add(aantalRijenKnop);
        
        boxv.add(boxh);
        
        boxh1.add(boxv);
        aantalKolommenKnop = new PlusMinKnop(0, 0, 20, 16, PlusMinKnop.HORIZONTAAL);
        aantalKolommenKnop.setPreferredSize(new Dimension(20, 16));
        aantalKolommenKnop.setSize(getPreferredSize());
        aantalKolommenKnop.addActionListener(this);
        boxh1.add(aantalKolommenKnop);
        
        objectivesPanel.add(boxh1);
        
        okButton = new JButton("Ok");
        okButton.addActionListener(this);
        bottomPanel.add(okButton);
        
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        bottomPanel.add(cancelButton);
    }
    
    
        public void zetTextFieldsZichtbaar(boolean b){
    	
    	for(int i = 1; i < maxCategories; i++)
    	{	categoryTextFields[i].setVisible(b);
    		for(int j = 0; j < maxObjectives; j++)
    		{	objectiveTextFields[i][j].setText("");
    			objectiveTextFields[i][j].setVisible(b);
    		}
    	}
    }
    
	
	public void actionPerformed(ActionEvent e)
	{
	    if(e.getSource().equals(aantalRijenKnop))
		{	if(e.getActionCommand().equals("plus") && aantalRijen > 0)
			{	makeGUI(aantalRijen - 1, aantalKolommen);
				aantalRijen--;
				for(int j = 0; j < aantalKolommen; j++)
					objectiveTextFields[j][aantalRijen].setText("");
				invalidate();
				getParent().validate();
                getParent().repaint();
			}
			if(e.getActionCommand().equals("min") && aantalRijen < maxObjectives)
			{	makeGUI(aantalRijen + 1, aantalKolommen);
				aantalRijen++;
				invalidate();
                getParent().validate();
				getParent().repaint();
			}
		}
		else if(e.getSource().equals(aantalKolommenKnop))
		{	if(e.getActionCommand().equals("min") && aantalKolommen > 0)
			{	makeGUI(aantalRijen, aantalKolommen - 1);
				aantalKolommen--;
				for(int i = 0; i < aantalRijen; i++)
					objectiveTextFields[aantalKolommen][i].setText("");
				invalidate();
                getParent().getParent().validate();
                getParent().getParent().repaint();
			}
			if(e.getActionCommand().equals("plus") && aantalKolommen < maxCategories)
			{	makeGUI(aantalRijen, aantalKolommen + 1);
				aantalKolommen++;
				invalidate();
                getParent().getParent().validate();
                getParent().getParent().repaint();
			}
		}
		else if(e.getSource().equals(okButton)) {   
			makeObjects();
			
		}
		else if(e.getSource().equals(cancelButton)) {   
        }
	}   
	
	private String text = "";
	
	public void setText(String json) {
	  this.text = json;
	}
	
	public String getText() {
	  return text;
	}

  public void setEditable(boolean b) {
    setEnabled(b);
  }

  public boolean isEditable() {
    return isEnabled();
  }

}