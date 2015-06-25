package nl.uu.fi.dwo.formule.client.formuleobjects;

import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.AbsVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.AftrekVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.BinVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.BreukVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.ConjugVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.DiffPartialVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.DiffVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.Haakjesvak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.IntegraalVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.LimietVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.Machtvak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.NdeLogVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.NdeWortelVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.OptelVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.PowerVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.PrimitieveVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.PrvVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.SigmaVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.SubscriptVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.VermenigvuldigingVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.WortelVak;

/**
 * Covert string to formule objects
 * 
 * @author Danny Hendrix
 * 
 */
public class FormuleDecoder
{
	public FormuleDecoder()
	{
	}

	public static FormuleElement getElementFromCharacter(char ch1, FormuleElement holder)
	{
		
		if(ch1=='o')
		{	return new OptelVak(holder);
		}
		else if(ch1=='a')
		{	return new AftrekVak(holder);
		}
		else if(ch1=='v')
		{	return new VermenigvuldigingVak(holder);
		}
		else if (ch1 == 'b')
		{
			return new BreukVak(holder);
			//bv.vulVak(s.substring(2, eind));
			//regel.addElement(bv);
			//insert(bv);
			//s = s.substring(eind);
		}
		else if(ch1=='p')
		{	return new PowerVak(holder);
		}
		else if (ch1 == 'w')
		{
			return new WortelVak(holder);
		}
		else if (ch1 == 'W')
		{
			return new NdeWortelVak(holder);
		}
		else if(ch1=='L')
		{	return new NdeLogVak(holder);
		}
		else if(ch1=='d')
		{	return new DiffVak(holder);
		}
		else if(ch1=='D')
		{	return new DiffPartialVak(holder);
		}
		else if(ch1=='P')
		{	return new PrimitieveVak(holder);
		}
		else if(ch1=='T')
		{	return new LimietVak(holder);
		}
		else if(ch1=='S')
		{   return new SigmaVak(holder);
		}
		else if (ch1 == 'i')
		{
			return new IntegraalVak(holder);
		}
		else if(ch1=='q')
		{	return new PrvVak(holder);
			
		}
		else if (ch1 == 'm')
		{
			Machtvak mv = new Machtvak(holder);
			return mv;
		}
		else if(ch1=='s')
		{   return new SubscriptVak(holder);
			//SubscriptVak sv = new SubscriptVak(formuleVak);
			//   insert(sv);
			//   sv.vulVak(s.substring(2,eind));
			//   s = s.substring(eind);
		}
		else if (ch1 == 'h')
		{
			return new Haakjesvak(holder);
		}
		else if(ch1=='r')
		{	return new AbsVak(holder);
			//AbsVak av = new AbsVak(formuleVak);
			//av.setFGColor(fgColor);
			//av.vulVak(s.substring(2,eind));
			//insert(av);
			//s = s.substring(eind);
		}
		else if(ch1=='c')
		{   return new ConjugVak(holder);
			//ConjugVak av = new ConjugVak(formuleVak);
			//  av.vulVak(s.substring(2,eind));
			//  insert(av);
			//  s = s.substring(eind);
		}
		else if(ch1=='y')
		{	return new BinVak(holder);
			//BinVak iv = new BinVak(formuleVak);
			//iv.vulVak(s.substring(2,eind));
			//insert(iv);
			//s = s.substring(eind);
		}
			
		return null;
	}
}
