package nl.uu.fi.dwo.formule.client.formuleobjects.vakken;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleElement;

public class OptelVak extends OperatorVak {

	public OptelVak(FormuleElement holder) {
		super(holder,'o', '+');
	}
	public String toMathML() {
		return "<mrow>" + getChild(0).toMathML() + "<mo>+</mo>" + getChild(1).toMathML() + "</mrow>";
	}

}
