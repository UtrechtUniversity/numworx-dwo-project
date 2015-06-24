package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;

public class VermenigvuldigingVak extends OperatorVak {

	public VermenigvuldigingVak(FormuleElement holder) {
		super(holder, 'v', '*');
	}
	public String toMathML() {
		// INVISIBLE TIMES \u2062 see http://www.unicode.org/reports/tr25/
				return "<mrow>" + getChild(0).toMathML() + "<mo>\u2062</mo>" + getChild(1).toMathML() + "</mrow>";
			}

}
