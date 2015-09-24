package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;

public class AftrekVak extends OperatorVak {

	public AftrekVak(FormuleElement holder) {
		super(holder, 'a', '-');
	}
	
	
	
	public String toMathML() {
		// MINUS SIGN \u2212 see http://www.unicode.org/reports/tr25/
				return "<mrow>" + getChild(0).toMathML() + "<mo>\u2212</mo>" + getChild(1).toMathML() + "</mrow>";
			}

}
