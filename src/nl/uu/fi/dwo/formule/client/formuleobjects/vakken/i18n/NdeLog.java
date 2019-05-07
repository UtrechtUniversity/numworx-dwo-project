package nl.uu.fi.dwo.formule.client.formuleobjects.vakken.i18n;

import com.google.gwt.i18n.shared.Localizable;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.NdeLogVak;
import nl.uu.fi.dwo.interaction.client.FormuleFont;

public class NdeLog implements Localizable {

	public void position(NdeLogVak p) {
		FormuleFont fm = p.getFont();
		FormuleRegel child1 = p.getChild(1);
		FormuleRegel child0 = p.getChild(0);
//		p.setSize(2*fm.getAscent()/3 + child1.width + p.getfStr() + child0.width + fm.getAscent()/2, 
//				  child1.height + child0.height);
		p.setAsHoogte(child0.getAsHoogte());
        /* FIXME
        if(WiskOpdr.language.toString().equals("en"))
        {
        	kind1.setLocation(k2w + fStr + 3*asc/4, 0);
        	kind2.setLocation(5+fStr, ashoogte + (-kind2.ashoogte + 2*asc/3));
        }
        */

		child0.setPosition( p.getfStr() + child1.width + 3*fm.getAscent()/4, 0);
        child1.setPosition( 5+p.getfStr(), p.getAsHoogte()-(child1.height/2));
//		if (child1.y<0) {
//		  child0.y -= child1.y;
//		  p.setAsHoogte(p.getAsHoogte()-child1.y);
//          child1.y = 0;
//		}
        
        
        
        p.setSize(2*fm.getAscent()/3 + child1.width + p.getfStr() + child0.width + fm.getAscent()/2 , Math.max(child1.y + child1.height, child0.y + child0.height));
	}
	// if language is 'default' grondtal als subscript
	
	public String toMathML(NdeLogVak parent) {
		return "<mrow><msub><mi>log</mi>" + parent.getChild(1).toMathML() + "</msub><mfenced>" + parent.getChild(0).toMathML() + "</mfenced></mrow>";
	}

	public float getLogX(NdeLogVak p) {
		return 4;
	}

	public int getLogY(NdeLogVak p) {
		return p.getChild(0).y;
	}

	public int getAsHoogte(NdeLogVak p) {
		return p.getChild(0).getAsHoogte();
	}
}
