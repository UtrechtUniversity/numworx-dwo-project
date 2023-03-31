package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.strategievak;

import java.util.ArrayList;
import java.util.HashMap;

import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.ActivityInterface;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorContext;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVak;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVakPanel;

class StrategieTekstVakPanel extends TekstVakPanel {

	public StrategieTekstVakPanel(ActivityInterface a, int breedte, int hoogte, String[] randomVarNamen,
			HashMap<String, Number> randomVarWaarden) {
		super(a, breedte, hoogte, randomVarNamen, randomVarWaarden);
		// TODO Auto-generated constructor stub
	}

	public StrategieTekstVakPanel(ActivityInterface a, HashMap<String, Object> hh, String[] randomVarNamen,
			HashMap<String, Number> randomVarWaarden, AnchorContext context, int breedte) {
		super(a, hh, randomVarNamen, randomVarWaarden, context, breedte);
		// TODO Auto-generated constructor stub
	}

	public StrategieTekstVakPanel(ActivityInterface a, HashMap<String, Object> hh, String[] randomVarNamen,
			HashMap<String, Number> randomVarWaarden, int breedte) {
		super(a, hh, randomVarNamen, randomVarWaarden, breedte);
		// TODO Auto-generated constructor stub
	}

	public StrategieTekstVakPanel(ActivityInterface a, int breedte, int hoogte, String[] randomVarNamen,
			HashMap randomVarWaarden, AnchorContext anchorContext) {
		super(a, breedte, hoogte, randomVarNamen, randomVarWaarden, anchorContext);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected TekstVak createTekstVak(int i, int j) {
		if (i % 2 != 0)
			return super.createTekstVak(i, j);
		return new EmptyTekstVak(this, i, j);
	}

	@Override
	public HashMap<String, Object> getState() {
// This is the right order.
		interactionViewObjects.clear();
		int size = hoogtes.size();
		for(int i = 0; i < size; i++) {
			ArrayList<Object> list = tekstVakken[i][kolom].getOpdrachtObjects();
			interactionViewObjects.addAll(list);
		}
		return super.getState();
	}

	
}
