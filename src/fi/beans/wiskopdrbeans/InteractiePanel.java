package fi.beans.wiskopdrbeans;

import java.awt.AWTEventMulticaster;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

public interface InteractiePanel 
{
	public void zetOpdracht(Hashtable b, String[] randomVars, Hashtable randomValues);
	
	public void setState(Hashtable b);
	
	public void setEditState(Hashtable b);
	
	public Hashtable getState();
	
	public Hashtable getEditState();
	
	public InteractieEditPanel getEditPanel();
		
	public void setBounds(int x, int y, int b, int h);
	
	public void wis();
	
	public void zetMaat();
	
	public int geefAsHoogte();
	
	public int getIpId();
	
	//public String getIpExpString();
	
	public int getScore();
	
	public int[] getScoreObjectives();
	
	public int getScoreMax();
	
	public boolean isCorrect();
	
	public boolean isFout();
	
	public void zetMode(int mode);
	
	public void zetNagekeken(boolean b);
	
    public void stop();
    
    public void start();
    
    public void destroy();
    
    public void opnieuw();
    
    public void kijkNa();
    
    public void kijkNa(int stapNr);
    
    public void addActionListener(ActionListener al);
    
	//public void actionPerformed(ActionEvent e);
	
}
