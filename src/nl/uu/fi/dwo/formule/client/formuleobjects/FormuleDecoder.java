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
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.StelselVak;
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
	private FormuleDecoder()
	{
	}

	public static FormuleElement getElementFromCharacter(char ch1, FormuleElement holder)
	{
		switch(ch1) {
		case 'o': return new OptelVak(holder);
		case 'a': return new AftrekVak(holder);
		case 'v': return new VermenigvuldigingVak(holder);
		case 'b': return new BreukVak(holder);
		case 'p': return new PowerVak(holder);
		case 'w': return new WortelVak(holder);
		case 'W': return new NdeWortelVak(holder);
		case 'L': return new NdeLogVak(holder);
		case 'd': return new DiffVak(holder);
		case 'D': return new DiffPartialVak(holder);
		case 'P': return new PrimitieveVak(holder);
		case 'T': return new LimietVak(holder);
		case 'S': return new SigmaVak(holder);
		case 'i': return new IntegraalVak(holder);
		case 'q': return new PrvVak(holder);
		case 'm': return new Machtvak(holder);
		case 's': return new SubscriptVak(holder);
		case 'h': return new Haakjesvak(holder);
		case 'r': return new AbsVak(holder);
		case 'c': return new ConjugVak(holder);
		case 'y': return new BinVak(holder);
		case 'Q': return new StelselVak(holder);
		default: return null;
		}			
		
	}
}
