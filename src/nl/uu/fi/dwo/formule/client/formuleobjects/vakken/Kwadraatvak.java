package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;

/**
 * 
 * @author Danny Hendrix
 * 
 */
public class Kwadraatvak extends Machtvak
{
	public Kwadraatvak(FormuleElement holder)
	{
		super(holder);
		this.getChild().insert("2");
		getChild().setEditable(false);
	}

	@Override
	public FormuleElement getCurrentOnNew()
	{
		return this;
	}
}
