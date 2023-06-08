package fi.dwo.dwojapplet.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
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

public class DomainModelEditor extends JPanel implements ActionListener, Scrollable
{
    static class Model {
      JTextField field;
      ToolTipEditor editor;
      String tip; // plain.
      
      Model(String value, String tip) {
        field = new JTextField(value);
        field.setPreferredSize(TEXTFIELD_SIZE);
        field.setOpaque(true);
        this.tip = tip;
        editor = new ToolTipEditor(this);
      }
      public Model(String string) {
        this(string, null);
      }
      void setText(String text) {
        field.setText(text);
      }
      void setToolTip(String tip) {
        field.setToolTipText(wrap(tip));
        this.tip = tip;
      }
      
      String getText() {
        return field.getText();
      }
      String getToolTip() {
        return tip;
      }

      private static String wrap(String string) {      
        // is alleen write-only, niet read-write   
           if (string != null && string.length() > 30) {
              string = string.replace("&", "&amp;").replace("<", "&lt;").replace(">","&gt");
             int space = string.indexOf(" ");
             int start = 0;
             while(space >=0) {
               if (space - start > 30) {
                 string = string.substring(0, space) + "<br>" + string.substring(space + 1);
                 start = space + 3;
               }
               space = string.indexOf(" ", space+1);
             }
             string = "<html>" + string + "</html>";
           }
           return string;
         }
    
    }
  
  
  
  
    private static final Dimension TEXTFIELD_SIZE = new Dimension(180,20);
    private Model[][] objectiveTextFields;
	private JLabel[] objectiveLabels;
	private Model[] categoryTextFields;
	private Model modelTextField;
	private JComboBox<String> localeBox;
	
	private int maxObjectives  = 20;
	private int maxCategories = 10; // Sietske heeft er 7 in haar nieuwe domainmodel
	int aantalRijen = 4;
	int aantalKolommen = 1;
	PlusMinKnop aantalRijenKnop;
	PlusMinKnop aantalKolommenKnop;
	
	JPanel objectivesPanel = this;
	
	private String rowLabel;
	private String columnLabel;

  private static class ToolTipEditor extends MouseAdapter {

    private Model model;
    private JTextArea area;
    private JScrollPane pane;

    private ToolTipEditor(Model model) {
      this.model = model;
      model.field.addMouseListener(this);
      area = new JTextArea(4, 30);
      area.setWrapStyleWord(true);
      area.setLineWrap(true);
      pane = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
          JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      pane.setMaximumSize(new Dimension(320, 200)); // Single size!
      pane.setMinimumSize(pane.getMaximumSize());
      pane.setPreferredSize(pane.getMaximumSize());
    }

    @Override
    public void mousePressed(MouseEvent e) {
      if (e.isPopupTrigger()) {
        popup();
      }
    }

    private void popup() {
      area.setText(model.getToolTip());
      int r = JOptionPane.showConfirmDialog(model.field, pane, model.getText(),
          JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
      if (r == JOptionPane.OK_OPTION) {
        model.setToolTip(area.getText());
      }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      if (e.isPopupTrigger()) popup();
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

    public void makeTextFields()
    {	objectiveTextFields = new Model[maxCategories][maxObjectives];
	    objectiveLabels = new JLabel[maxObjectives];
	    categoryTextFields = new Model[maxCategories];
	    for(int j = 0; j < maxCategories; j++)
	    {	categoryTextFields[j] = new Model(columnLabel + " "  + (j+1));
	    }
	    for(int i=0 ; i<maxObjectives ; i++)
	    {	objectiveLabels[i] = new JLabel(rowLabel + " " +(i+1));
	   		objectiveLabels[i].setPreferredSize(new Dimension(100,20));
	   		for(int j = 0; j<maxCategories; j++)
	        {	objectiveTextFields[j][i] = new Model("");
	        }
	    }
	    modelTextField = new Model("");
	    
	    modelTextField.field.setMaximumSize(new Dimension(360,20));
	    localeBox = new JComboBox<>(new String[] { "nl", "en", "fr", "de" });
	    localeBox.setSelectedItem(locale);
	    localeBox.setPreferredSize(TEXTFIELD_SIZE);
	    localeBox.setMaximumSize(TEXTFIELD_SIZE);
	    localeBox.setEditable(true);
	    localeBox.addActionListener(this);
	    localeBox.setOpaque(true);
	    setOpaque(true);
    }
    
    public void makeGUI(int aantalRijen, int aantalKolommen){
    	objectivesPanel = this;
    	objectivesPanel.removeAll();
//		bottomPanel = new JPanel();
        
		Box boxh1 = Box.createHorizontalBox();
		Box boxv = Box.createVerticalBox();
 
        boxh1.add(modelTextField.field);
        boxh1.add(Box.createHorizontalStrut(10));
        boxh1.add(localeBox);
        boxh1.add(Box.createHorizontalGlue());      
        boxv.add(boxh1);
        
        Box boxh = Box.createHorizontalBox();
        JLabel leegLabel = new JLabel("");
        leegLabel.setPreferredSize(new Dimension(100,20));
        boxh.add(leegLabel);
        
        for(int j = 0; j < aantalKolommen; j++)
        {
          boxh.add(categoryTextFields[j].field);
          categoryTextFields[j].field.repaint();
        }
        
        boxv.add(boxh);
        
        for(int i = 0; i < aantalRijen; i++)
        {	boxh = Box.createHorizontalBox();
        	boxh.add(objectiveLabels[i]);
        	for(int j = 0; j < aantalKolommen; j++)
        	{
        	  boxh.add(objectiveTextFields[j][i].field);
        	  objectiveTextFields[j][i].field.repaint();
        	}
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
        
//        okButton = new JButton("Ok");
//        okButton.addActionListener(this);
//        bottomPanel.add(okButton);
//        
//        cancelButton = new JButton("Cancel");
//        cancelButton.addActionListener(this);
//        bottomPanel.add(cancelButton);
        invalidate();
        setSize(getPreferredSize());
    }
    
    
        public void zetTextFieldsZichtbaar(boolean b){
    	
    	for(int i = 1; i < maxCategories; i++)
    	{	categoryTextFields[i].field.setVisible(b);
    		for(int j = 0; j < maxObjectives; j++)
    		{	objectiveTextFields[i][j].setText("");
    			objectiveTextFields[i][j].field.setVisible(b);
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
                repaint();
				getParent().validate();
                getParent().repaint();
			}
			if(e.getActionCommand().equals("min") && aantalRijen < maxObjectives)
			{	makeGUI(aantalRijen + 1, aantalKolommen);
				aantalRijen++;
				invalidate();
	            repaint();
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
				repaint();
				
                getParent().getParent().validate();
                getParent().getParent().repaint();
			}
			if(e.getActionCommand().equals("plus") && aantalKolommen < maxCategories)
			{	makeGUI(aantalRijen, aantalKolommen + 1);
				aantalKolommen++;
				invalidate();
				repaint();
                getParent().getParent().validate();
                getParent().getParent().repaint();
			}
		}
//		else if(e.getSource().equals(okButton)) {   
//			//makeObjects();
//			
//		}
//		else if(e.getSource().equals(cancelButton)) {   
//        }
		else if(e.getSource() == localeBox) {
		   switchModel(localeBox.getSelectedItem().toString());
		}
	}   
	
	private void switchModel(String string) {
	  String title;
	   setInfo(model.getInfo(), modelTextField.getText(), modelTextField.getToolTip());
       List<DomStudentModelCategory> categories = model.getCategories();
	   for( int i = 0; i < aantalKolommen; i++ ) {
         if (i >= categories.size()) categories.add(newDomStudentModelCategory());
         DomStudentModelCategory cat = categories.get(i);
         String text = categoryTextFields[i].getText(); // XXX empty tekst
         setInfo(cat.getInfo(), text, categoryTextFields[i].getToolTip());
         List<DomStudentModelObj> objectives = cat.getObjectives();
         for (int j = 0; j < aantalRijen; j++) {
           if (j >= objectives.size()) objectives.add(newDomStudentModelObj());
           DomStudentModelObj obj = objectives.get(j);
           DomStudentModelContextInfo info = obj.getInfo();
           setInfo(info, objectiveTextFields[i][j].getText(), objectiveTextFields[i][j].getToolTip());
           title = info.getTitle().get(string);
           if (title != null) objectiveTextFields[i][j].setText(title);
           if (info.getDescription().containsKey(string))
             objectiveTextFields[i][j].setToolTip((info.getDescription().get(string)));
         }
         DomStudentModelContextInfo info = cat.getInfo();
         title = info.getTitle().get(string);
         if (title != null) categoryTextFields[i].setText(title);
         if (info.getDescription().containsKey(string))
           categoryTextFields[i].setToolTip((info.getDescription().get(string)));
       }

	   DomStudentModelContextInfo info = model.getInfo();
       title = info.getTitle().get(string);
       if (title != null) modelTextField.setText(title);
       if (info.getDescription().containsKey(string))
         modelTextField.setToolTip((info.getDescription().get(string)));     
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
    modelTextField.field.setEditable(b);
    for(Model item: categoryTextFields) item.field.setEditable(b);
    for(Model[] items: objectiveTextFields) {
      for (Model item: items)
        item.field.setEditable(b);
    }
  }

  public boolean isEditable() {
    return isEnabled();
  }

  private String locale = DwoHelper.getLocale().getLocale();
  
  private final static Genson GENSON = new GensonBuilder().withConverters(new GensonMapConverter()).useIndentation(true).create();
  private static final int NEW_CATEGORIES = 3;
  private static final int NEW_OBJECTIVES = 5;

  public void setModel(DomStudentModelStructure modelStructure) {
    if ( modelStructure == null) {
      modelStructure = new DomStudentModelStructure();
      modelStructure.setInfo(new DomStudentModelContextInfo(new TreeMap<>(), new TreeMap<>()));
      modelStructure.setCategories(new LinkedList<>());
      for(int i = 0; i < NEW_CATEGORIES; i++ ) {
        modelStructure.getCategories().add(newDomStudentModelCategory()); // start with a single category
        for (int j = 0; j < NEW_OBJECTIVES; j++ ) {
          modelStructure.getCategories().get(i).getObjectives().add(newDomStudentModelObj()); // and a single objective
        }
      }
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
    modelTextField.setToolTip((descr));
    
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
      categoryTextFields[i].setToolTip((descr));
      int rijen = cat.getObjectives().size();
      Model[] objectiveTextField = objectiveTextFields[i];
      List<DomStudentModelObj> objectives = cat.getObjectives();
      for (int j = 0; j < rijen; j++) {
        DomStudentModelObj obj = objectives.get(j);
        String label = obj.getInfo().getTitle().getOrDefault(locale, "");
        objectiveTextField[j].setText(label);
        descr = obj.getInfo().getDescription().get(locale);
        objectiveTextField[j].setToolTip((descr));
      }
      for (int j = rijen; j < objectiveTextField.length; j++) {
        objectiveTextField[j].setText("");
        objectiveTextField[j].setToolTip(null);
      }
    }
    for (int i = aantalKolommen; i < categoryTextFields.length; i++) {
      categoryTextFields[i].setText("");
      categoryTextFields[i].setToolTip(null);
      Model[] objectiveTextField = objectiveTextFields[i];
      for (int j = 0; j < objectiveTextField.length; j++) {
        objectiveTextField[j].setText("");
        objectiveTextField[j].setToolTip(null);
      }
    }
    invalidate();
    getParent().validate();
    repaint();
  }

  @Override
  public Dimension getPreferredScrollableViewportSize() {
    return new Dimension( 180*5, 20 * 9);
  }

  @Override
  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
    if(orientation == SwingConstants.VERTICAL) return 20;
    return 180;
  }

  @Override
  public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
    if (orientation == SwingConstants.VERTICAL)
      return visibleRect.height;
    return visibleRect.width;
  }

  @Override
  public boolean getScrollableTracksViewportWidth() {
    return false;
  }

  @Override
  public boolean getScrollableTracksViewportHeight() {
    return false;
  }


  
}