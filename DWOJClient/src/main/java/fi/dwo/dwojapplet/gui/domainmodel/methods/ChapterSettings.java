package fi.dwo.dwojapplet.gui.domainmodel.methods;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JScrollPane;
import fi.beans.numworxlf.JTextField;

class ChapterSettings extends JPanel implements ActionListener
{
	private static final Color colorBlue1 = Constants.colorBlue1;

    //private static final Color colorGray3 = Constants.COLOR10;

    private JTextField[][] chapterTextFields;
	private JLabel[] chapterLabels;
	private JTextField[] bookTextFields;
	private boolean readonly;
	
	private String[] bookString;
	
	private static final int maxObjectives  = 30;
	private static final int maxCategories = 10; // Sietske heeft er 7 in haar nieuwe domainmodel
	int aantalRijen = 4;
	int aantalKolommen = 1;
	PlusMinKnop aantalRijenKnop;
	PlusMinKnop aantalKolommenKnop;
	private String[][] chapters;
	
	JPanel objectivesPanel = new JPanel();
	JScrollPane scrollPane;
		
	final String rowLabel;
	final String columnLabel;
		
			
	public ChapterSettings(String rowLabel, String columnLabel){
	    super(new BorderLayout());
		this.rowLabel = rowLabel;
		this.columnLabel = columnLabel;
	}
	
	public void setChapters(String[][] objectives){   
		this.chapters = objectives;
	}
	
	public void setBooks(String[] categorieString){   
		this.bookString = categorieString;
	}
			
	void makeObjects(){   
    	chapters = null;
    	String[] newObjects = null;
    	for(int j=0 ; j<aantalKolommen ; j++)
    	{	String checkObject = chapterTextFields[j][0].getText();
   			if(checkObject==null || "".equals(checkObject.trim()))
   			{	chapters = new String[j][];
   				break;
   			}
   			if(chapters == null)
   				chapters = new String[aantalKolommen][];
    	}
    	for(int j=0 ; j<chapters.length ; j++)
    	{	newObjects = new String[aantalRijen];
	        for(int i=0 ; i<aantalRijen ; i++){   
	        	String checkObject = chapterTextFields[j][i].getText();
	       		if(checkObject!=null && !"".equals(checkObject.trim())){	
	       			newObjects[i] = checkObject;
	            }
	            else{   
	            	chapters[j] = new String[i];
	            	break;
	            }
	        }
	        if(chapters[j]==null){
	        	chapters[j] = new String[aantalRijen];
	        }
	        for(int i=0 ; i<chapters[j].length ; i++){
	        	chapters[j][i] = newObjects[i];
	        }
    	}
    	bookString = null;
    	bookString = new String[chapters.length];
    	for(int i = 0; i < chapters.length; i++)
    	{	bookString[i] = bookTextFields[i].getText();
//    		if(bookString[i].equals(columnLabel + " "  + (i+1)))
//    			bookString[i] = "";
    	}	
    }
    
    
    public String[][] getChapters(){   
    	return chapters;
    }
    
    public String[] getBooks(){
    	return bookString;
    }
        
    public void makeTextFields()
    {	chapterTextFields = new JTextField[maxCategories][maxObjectives];
	    chapterLabels = new JLabel[maxObjectives];
	    bookTextFields = new JTextField[maxCategories];
	    for(int j = 0; j < maxCategories; j++)
	    {	bookTextFields[j] = new JTextField(columnLabel + " "  + (j+1));
		    	bookTextFields[j].setPreferredSize(new Dimension(180,20));
	    }
	    for(int i=0 ; i<maxObjectives ; i++)
	    {	chapterLabels[i] = new JLabel(rowLabel + " " +(i+1));
	    		chapterLabels[i].setForeground(colorBlue1);
	   		chapterLabels[i].setPreferredSize(new Dimension(100,20));
	   		for(int j = 0; j<maxCategories; j++)
	        {	chapterTextFields[j][i] = new JTextField("");
	    			chapterTextFields[j][i].setPreferredSize(new Dimension(180,20));
	        }
	    }
    }
    
    private void clearFields() {
      for (int j = 0; j < maxCategories; j++) bookTextFields[j].setText(columnLabel + " " + (j+1)); 
      for (int i = 0; i < maxObjectives; i++) 
        for (int j = 0; j < maxCategories; j++)
          chapterTextFields[j][i].setText("");   
    }
    
    
    public void makeGUI(int aantalRijen, int aantalKolommen){
    		objectivesPanel = new JPanel();
    		//objectivesPanel.setBackground(colorGray3);
    		objectivesPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 50, 30));
    		
    		//mainPanel.setBackground(colorGray3);
    		//mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    		removeAll();
		
        
		Box boxh1 = Box.createHorizontalBox();
		Box boxv = Box.createVerticalBox();
        Box boxh = Box.createHorizontalBox();
        
        JLabel leegLabel = new JLabel("");
        leegLabel.setPreferredSize(new Dimension(100,20));
        boxh.add(leegLabel);
        
       
        for(int j = 0; j < aantalKolommen; j++)
        {
          boxh.add(bookTextFields[j]);
        }
        
        boxv.add(boxh);
        
        
        
        for(int i = 0; i < aantalRijen; i++)
        {	boxh = Box.createHorizontalBox();
        	boxh.add(chapterLabels[i]);
        	for(int j = 0; j < aantalKolommen; j++)
        		boxh.add(chapterTextFields[j][i]);
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
        objectivesPanel.setSize(objectivesPanel.getPreferredSize());       
		scrollPane = new JScrollPane(objectivesPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		//scrollPane.setBackground(colorGray3);
		scrollPane.setVisible(true);
		
	    
	    add(scrollPane);
    }
    
    
        public void zetTextFieldsZichtbaar(boolean b){
    	
    	for(int i = 1; i < maxCategories; i++)
    	{	bookTextFields[i].setVisible(b);
    		for(int j = 0; j < maxObjectives; j++)
    		{	chapterTextFields[i][j].setText("");
    			chapterTextFields[i][j].setVisible(b);
    		}
    	}
    }
    
        
    void resize() {
        invalidate();
        Window w = SwingUtilities.windowForComponent(this);
        if (w == null) return; // if not showing
        w.setSize(w.getPreferredSize());
        w.validate();
        w.pack();
        repaint();
        
    }
	
	public void actionPerformed(ActionEvent e)
	{	if(readonly) return;
	    if(e.getSource().equals(aantalRijenKnop))
		{	if(e.getActionCommand().equals("plus") && aantalRijen > 0)
			{	makeGUI(aantalRijen - 1, aantalKolommen);
				aantalRijen--;
				for(int j = 0; j < aantalKolommen; j++)
					chapterTextFields[j][aantalRijen].setText("");
				resize();
			}
			if(e.getActionCommand().equals("min") && aantalRijen < maxObjectives)
			{	makeGUI(aantalRijen + 1, aantalKolommen);
				aantalRijen++;
				resize();
			}
		}
		else if(e.getSource().equals(aantalKolommenKnop))
		{	if(e.getActionCommand().equals("min") && aantalKolommen > 0)
			{	makeGUI(aantalRijen, aantalKolommen - 1);
				aantalKolommen--;
				for(int i = 0; i < aantalRijen; i++)
					chapterTextFields[aantalKolommen][i].setText("");
                resize();
			}
			if(e.getActionCommand().equals("plus") && aantalKolommen < maxCategories)
			{	makeGUI(aantalRijen, aantalKolommen + 1);
				aantalKolommen++;
                resize();
			}
		}
		
	}

  public void makeGUI() {
    if (chapters != null)
    {
      aantalKolommen = chapters.length;
      aantalRijen = 0;
      for (int i = 0; i < chapters.length; i++)
        if (chapters[i] != null && chapters[i].length > aantalRijen)
          aantalRijen = chapters[i].length;
    }
    clearFields();
    makeGUI(aantalRijen, aantalKolommen);
    for (int j = 0 ; chapters!=null &&  j < chapters.length; j++)
    { int i;
      for (i = 0 ; chapters[j]!=null && i < chapters[j].length; i++)
        chapterTextFields[j][i].setText(chapters[j][i]);
      for (; i < chapterTextFields[j].length; i++)
        chapterTextFields[j][i].setText("");        
      bookTextFields[j].setText(bookString[j]);
    }
    resize();
  }

  /**
   * @return the readonly
   */
  public boolean isReadonly() {
    return readonly;
  }

  /**
   * @param readonly the readonly to set
   */
  public void setReadonly(boolean readonly) {
    this.readonly = readonly;
    for(JTextField f: bookTextFields) f.setEnabled(!readonly);
    for(JTextField[] fa: chapterTextFields)
      for(JTextField f: fa)
        f.setEnabled(!readonly);
  }

}