package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;

public class AftrekVak extends OperatorVak {

	public AftrekVak(FormuleElement holder) {
		super(holder, 'a', '-');
	}
	
	
	
	public String toMathML() {
		// MINUS SIGN \u2212 see http://www.unicode.org/reports/tr25/
				return "<mrow>" + getChild(0).toMathML() + "<mo>\u2212</mo>" + getChild(1).toMathML() + "</mrow>";
			}



	@Override
	public void paintSelection() {
		paintSelection0();
		FormuleRegel a = children.get(0);
		if(!"0".equals(a.toString()))
				a.paintSelection();
		FormuleRegel b = children.get(1);
		b.paintSelection();
	}

}
