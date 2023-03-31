package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.strategievak;

import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVak;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVakPanel;

class EmptyTekstVak extends TekstVak {

	EmptyTekstVak(TekstVakPanel parent, int rij, int kolom) {
		super(parent, rij, kolom);
		aantalRegels = 0;
	}

	@Override
	public void resize() {
		if (getTekstVakParent() != null) {
			getTekstVakParent().resize();
		}
	}

	@Override
	public void setAshoogte(int ashoogte) {
	}

	@Override
	public int getRegelHoogte() {
		// TODO Auto-generated method stub
		return super.getRegelHoogte();
	}

	@Override
	public int getRegelBreedte() {
		// TODO Auto-generated method stub
		return super.getRegelBreedte();
	}

	@Override
	public void reLayout() {
		resize();
	}

	@Override
	public void plaatsRegels(boolean herplaats) {
	}

	@Override
	public void setPasHoogteBreedteAan(boolean pasAanH, boolean pasAanB) {
		// TODO Auto-generated method stub
		super.setPasHoogteBreedteAan(false, false);
	}

	@Override
	public void setTekstVakBreedte(double tekstVakBreedte) {
		// TODO Auto-generated method stub
		super.setTekstVakBreedte(tekstVakBreedte);
	}

	@Override
	public void setMarges(int bovenMarge, int cellMarge) {
		// TODO Auto-generated method stub
		super.setMarges(bovenMarge, cellMarge);
	}

	@Override
	public void setInterlinie(int interlinie) {
		// TODO Auto-generated method stub
		super.setInterlinie(interlinie);
	}

	@Override
	public void setSize(int b, int h) {
		this.breedte = b;
		this.hoogte = h;
		setPixelSize(b, h);
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return super.getHeight();
	}

	@Override
	public int getAsHoogte() {
		return 0;
	}


}
