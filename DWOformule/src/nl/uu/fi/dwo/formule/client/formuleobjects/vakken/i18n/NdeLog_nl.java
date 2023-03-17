package nl.uu.fi.dwo.formule.client.formuleobjects.vakken.i18n;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleRegel;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.NdeLogVak;
import nl.uu.fi.dwo.interaction.client.FormuleFont;

public class NdeLog_nl extends NdeLog {

	public NdeLog_nl() {
		//java.util.logging.Logger.getLogger("NdeLog_nl").info("creating NL ndelog variant");
	}

	
	public String toMathML(NdeLogVak parent) {
		return "<mrow><mmultiscripts><mi>log</mi><mprescripts /><none />" + parent.getChild(1).toMathML() + "</mmultiscripts><mfenced>" + parent.getChild(0).toMathML() + "</mfenced></mrow>";
	}


	@Override
	public void position(NdeLogVak p) {
		FormuleFont fm = p.getFont();
		FormuleRegel child1 = p.getChild(1);
		FormuleRegel child0 = p.getChild(0);
		p.setSize(2*fm.getAscent()/3 + child1.width + p.getfStr() + child0.width + fm.getAscent()/2, 
				  child1.height/2 + child0.height);
		p.setAsHoogte(child0.getAsHoogte() + child1.getHeight()/2);

		child0.setPosition(child1.width + p.getfStr() + 3*fm.getAscent()/4, child1.height/2);
        child1.setPosition(fm.getAscent()/3, p.getAsHoogte()-(child1.height/2 + fm.getAscent()));
	}

	public float getLogX(NdeLogVak p) {
		return 5 + p.getChild(1).width;
	}

	public int getLogY(NdeLogVak p) {
		return p.getChild(1).height/2;
	}

	public int getAsHoogte(NdeLogVak p) {
		return p.getChild(0).getAsHoogte() + p.getChild(1).height/2;
	}

}
