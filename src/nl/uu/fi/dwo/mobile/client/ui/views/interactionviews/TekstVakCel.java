package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.LayoutPanel;

public class TekstVakCel extends LayoutPanel{
	
	private TekstVakPanel parent;
	private int rij;
	private int kolom;
	private ArrayList<Object> opdrachtObjects;
	
	
	public TekstVakCel(TekstVakPanel parent, int rij, int kolom)
	{
		super();
		this.parent = parent;
		this.rij = rij;
		this.kolom = kolom;
	}

	public TekstVakPanel getTekstVakParent()
	{
		return parent;
	}
	
	public int getRij()
	{
		return rij;
	}
	
	public int getKolom()
	{
		return kolom;
	}
	
	public void zetOpdrachtObjects(ArrayList<Object> objects)
	{
		this.opdrachtObjects = objects;
	}
	
	public ArrayList<Object> getOpdrachtObjects()
	{
		return opdrachtObjects;
	}
}
