package fi.beans.wiskopdrbeans;

import java.awt.AWTEventMulticaster;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

public interface InteractieEditPanel 
{
	public void setEditState(Hashtable b);
	
	public Hashtable getEditState();
		
	public void setBounds(int x, int y, int b, int h);
	
	public void zetBreedte(int b);
	
	public void zetHoogte(int h);
	
	public void wis();
    
	public void zetMode(int mode);
	
    public void stop();
    
    public void start();
    
    public void addActionListener(ActionListener al);
    
	public void actionPerformed(ActionEvent e);
	
}
