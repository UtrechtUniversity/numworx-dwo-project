package fi.dwo.dwojapplet.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import javax.swing.*;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.util.GensonMapConverter;

public class DomainModelEditor extends JPanel implements ActionListener
{
	private static final Dimension TEXTFIELD_SIZE = new Dimension(180,20);
    private JTextField[][] objectiveTextFields;
	private JLabel[] objectiveLabels;
	private JTextField[] categoryTextFields;
	private JTextField modelTextField;
	private JComboBox<String> localeBox;
	
	//private String[] categorieString;
	
	private int maxObjectives  = 20;
	private int maxCategories = 10; // Sietske heeft er 7 in haar nieuwe domainmodel
	int aantalRijen = 4;
	int aantalKolommen = 1;
	PlusMinKnop aantalRijenKnop;
	PlusMinKnop aantalKolommenKnop;
	//private String[][] objectives;
	private JButton okButton; 
	private JButton cancelButton;
	
	JPanel objectivesPanel = this;
	JPanel bottomPanel = new JPanel();
	
	private String rowLabel;
	private String columnLabel;

	private class ToolTipEditor extends MouseAdapter {

	    private JTextField field;
	  
    private ToolTipEditor(JTextField field) {
        this.field = field;
        field.addMouseListener(this);
      }

    @Override
    public void mousePressed(MouseEvent e) {
      if(e.isPopupTrigger()) {
        popup();
      }
    }

    private void popup() {
      String t = JOptionPane.showInputDialog(field, field.getText(), field.getToolTipText());
      if(t != null) 
        field.setToolTipText(t);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      if (e.isPopupTrigger())
        popup();
    }
	  
	}
	
	
	
	
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
			
//	private void makeObjects(){   
//    	objectives = null;
//    	String[] newObjects = null;
//    	for(int j=0 ; j<maxCategories ; j++)
//    	{	String checkObject = objectiveTextFields[j][0].getText();
//   			if(checkObject==null || "".equals(checkObject.trim()))
//   			{	objectives = new String[j][];
//   				break;
//   			}
//   			if(objectives == null)
//   				objectives = new String[maxCategories][];
//    	}
//    	for(int j=0 ; j<objectives.length ; j++)
//    	{	newObjects = new String[maxObjectives];
//	        for(int i=0 ; i<maxObjectives ; i++){   
//	        	String checkObject = objectiveTextFields[j][i].getText();
//	       		if(checkObject!=null && !"".equals(checkObject.trim())){	
//	       			newObjects[i] = checkObject;
//	            }
//	            else{   
//	            	objectives[j] = new String[i];
//	            	break;
//	            }
//	        }
//	        if(objectives[j]==null){
//	        	objectives[j] = new String[maxObjectives];
//	        }
//	        for(int i=0 ; i<objectives[j].length ; i++){
//	        	objectives[j][i] = newObjects[i];
//	        }
//    	}
//    	categorieString = null;
//    	categorieString = new String[objectives.length];
//    	for(int i = 0; i < objectives.length; i++)
//    	{	categorieString[i] = categoryTextFields[i].getText();
//    		if(categorieString[i].equals(columnLabel + " "  + (i+1)))
//    			categorieString[i] = "";
//    	}	
//    }
            
    public void makeTextFields()
    {	objectiveTextFields = new JTextField[maxCategories][maxObjectives];
	    objectiveLabels = new JLabel[maxObjectives];
	    categoryTextFields = new JTextField[maxCategories];
	    for(int j = 0; j < maxCategories; j++)
	    {	categoryTextFields[j] = new JTextField(columnLabel + " "  + (j+1));
	    	categoryTextFields[j].setPreferredSize(TEXTFIELD_SIZE);
	    }
	    for(int i=0 ; i<maxObjectives ; i++)
	    {	objectiveLabels[i] = new JLabel(rowLabel + " " +(i+1));
	   		objectiveLabels[i].setPreferredSize(new Dimension(100,20));
	   		for(int j = 0; j<maxCategories; j++)
	        {	objectiveTextFields[j][i] = new JTextField("");
	        	objectiveTextFields[j][i].setPreferredSize(TEXTFIELD_SIZE);
	        }
	    }
	    modelTextField = new JTextField();
	    modelTextField.setPreferredSize(TEXTFIELD_SIZE);
	    modelTextField.setMaximumSize(new Dimension(360,20));
	    new ToolTipEditor(modelTextField);
	    localeBox = new JComboBox<>(new String[] { "nl", "en", "fr", "de" });
	    localeBox.setSelectedItem(locale);
	    localeBox.setPreferredSize(TEXTFIELD_SIZE);
	    localeBox.setMaximumSize(TEXTFIELD_SIZE);
	    localeBox.setEditable(true);
	    localeBox.addActionListener(this);
    }
    
    public void makeGUI(int aantalRijen, int aantalKolommen){
    	objectivesPanel = this;
    	objectivesPanel.removeAll();
		bottomPanel = new JPanel();
        
		Box boxh1 = Box.createHorizontalBox();
		Box boxv = Box.createVerticalBox();
 
        boxh1.add(modelTextField);
        boxh1.add(Box.createHorizontalStrut(10));
        boxh1.add(localeBox);
        boxh1.add(Box.createHorizontalGlue());      
        boxv.add(boxh1);
        
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
        aantalRijenKnop.addActionListener(this);
        boxh.add(aantalRijenKnop);
        
        boxv.add(boxh);
        
       // boxh1.add(boxv);
        aantalKolommenKnop = new PlusMinKnop(0, 0, 20, 16, PlusMinKnop.HORIZONTAAL);
        aantalKolommenKnop.setPreferredSize(new Dimension(20, 16));
        aantalKolommenKnop.setMaximumSize(aantalKolommenKnop.getPreferredSize());
        aantalKolommenKnop.addActionListener(this);
        boxh1.add(aantalKolommenKnop);
        
        objectivesPanel.add(boxv);
        
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
			//makeObjects();
			
		}
		else if(e.getSource().equals(cancelButton)) {   
        }
		else if(e.getSource() == localeBox) {
		   switchModel(localeBox.getSelectedItem().toString());
		}
	}   
	
	private void switchModel(String string) {
	  String title;
	   setInfo(model.getInfo(), modelTextField.getText(), modelTextField.getToolTipText());
       List<DomStudentModelCategory> categories = model.getCategories();
	   for( int i = 0; i < aantalKolommen; i++ ) {
         if (i >= categories.size()) categories.add(newDomStudentModelCategory());
         DomStudentModelCategory cat = categories.get(i);
         String text = categoryTextFields[i].getText(); // XXX empty tekst
         setInfo(cat.getInfo(), text, categoryTextFields[i].getToolTipText());
         List<DomStudentModelObj> objectives = cat.getObjectives();
         for (int j = 0; j < aantalRijen; j++) {
           if (j >= objectives.size()) objectives.add(newDomStudentModelObj());
           DomStudentModelObj obj = objectives.get(j);
           DomStudentModelContextInfo info = obj.getInfo();
           setInfo(info, objectiveTextFields[i][j].getText(), objectiveTextFields[i][j].getToolTipText());
           title = info.getTitle().get(string);
           if (title != null) objectiveTextFields[i][j].setText(title);
           if (info.getDescription().containsKey(string))
             objectiveTextFields[i][j].setToolTipText(info.getDescription().get(string));
         }
         DomStudentModelContextInfo info = cat.getInfo();
         title = info.getTitle().get(string);
         if (title != null) categoryTextFields[i].setText(title);
         if (info.getDescription().containsKey(string))
           categoryTextFields[i].setToolTipText(info.getDescription().get(string));
       }

	   DomStudentModelContextInfo info = model.getInfo();
       title = info.getTitle().get(string);
       if (title != null) modelTextField.setText(title);
       if (info.getDescription().containsKey(string))
         modelTextField.setToolTipText(info.getDescription().get(string));     
       locale = string;
       repaint();
  }

  private DomStudentModelObj newDomStudentModelObj() {
    DomStudentModelObj obj = new DomStudentModelObj();
    obj.setInfo(new DomStudentModelContextInfo(new TreeMap<>(), new TreeMap<>()));
    return obj;
  }

  private DomStudentModelCategory newDomStudentModelCategory() {
    DomStudentModelCategory category = new DomStudentModelCategory();
    category.setInfo(new DomStudentModelContextInfo(new TreeMap<String, String>(), new TreeMap<String, String>()));
    category.setObjectives(new ArrayList<>());
    return category;
  }

  private void setInfo(DomStudentModelContextInfo info, String title, String description) {
    if(title == null) title = "";
    title = title.trim();
    if (description != null) description = description.trim();
    if (description != null && description.isEmpty()) description = null;
    info.getTitle().put(locale, title);
    info.getDescription().put(locale, description);
  }

  private String text = "";
    private DomStudentModelStructure model;
	
	public void setText(String json) {
	  this.text = json;
	  this.model = GENSON.deserialize(json, DomStudentModelStructure.class);
	  extractModel(locale);
	}
	
	public DomStudentModelStructure getModel() {
	  switchModel(locale);
	  return model;
	}
	
	public String getText() {
	  switchModel(locale);
	  text = GENSON.serialize(model);
	  return text;
	}

  public void setEditable(boolean b) {
    setEnabled(b);
    modelTextField.setEditable(b);
    for(JTextField item: categoryTextFields) item.setEditable(b);
    for(JTextField[] items: objectiveTextFields) {
      for (JTextField item: items)
        item.setEditable(b);
    }
  }

  public boolean isEditable() {
    return isEnabled();
  }

  private String locale = DwoHelper.getLocale().getLocale();
  
  private final static Genson GENSON = new GensonBuilder().withConverters(new GensonMapConverter()).useIndentation(true).create();

  public void setModel(DomStudentModelStructure modelStructure) {
    if ( modelStructure == null) {
      modelStructure = new DomStudentModelStructure();
      modelStructure.setInfo(new DomStudentModelContextInfo(new TreeMap<>(), new TreeMap<>()));
      modelStructure.setCategories(new LinkedList<>());
      modelStructure.getCategories().add(newDomStudentModelCategory()); // start with a single category
      modelStructure.getCategories().get(0).getObjectives().add(newDomStudentModelObj()); // and a single objective
    }
    model = modelStructure;
    text = GENSON.serialize(modelStructure);
    extractModel(locale);
  }

  private void extractModel(String locale) {
    String title,descr;
    title = model.getInfo().getTitle().getOrDefault(locale, "");
    descr = model.getInfo().getDescription().get(locale);
    modelTextField.setText(title);
    modelTextField.setToolTipText(descr);
    
    List<DomStudentModelCategory> list = model.getCategories();
    aantalKolommen = list.size();
    aantalRijen = 0;
    for(DomStudentModelCategory cat : list) {
      int rijen = cat.getObjectives().size();
      if (rijen > aantalRijen) aantalRijen = rijen;
    }
    makeGUI(aantalRijen, aantalKolommen);
    for (int i = 0; i < aantalKolommen; i++) {
      DomStudentModelCategory cat = list.get(i);
      title = cat.getInfo().getTitle().getOrDefault(locale, columnLabel + " "  + (i+1));
      categoryTextFields[i].setText(title);
      descr = cat.getInfo().getDescription().get(locale);
      categoryTextFields[i].setToolTipText(descr);
      int rijen = cat.getObjectives().size();
      JTextField[] objectiveTextField = objectiveTextFields[i];
      List<DomStudentModelObj> objectives = cat.getObjectives();
      for (int j = 0; j < rijen; j++) {
        DomStudentModelObj obj = objectives.get(j);
        String label = obj.getInfo().getTitle().getOrDefault(locale, "");
        objectiveTextField[j].setText(label);
        descr = obj.getInfo().getDescription().get(locale);
        objectiveTextField[j].setToolTipText(descr);
      }
      for (int j = rijen; j < objectiveTextField.length; j++) {
        objectiveTextField[j].setText("");
        objectiveTextField[j].setToolTipText(null);
      }
    }
    for (int i = aantalKolommen; i < categoryTextFields.length; i++) {
      categoryTextFields[i].setText("");
      categoryTextFields[i].setToolTipText(null);
      JTextField[] objectiveTextField = objectiveTextFields[i];
      for (int j = 0; j < objectiveTextField.length; j++) {
        objectiveTextField[j].setText("");
        objectiveTextField[j].setToolTipText(null);
      }
    }
    invalidate();
    getParent().validate();
    repaint();
  }


  
}