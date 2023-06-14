package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class up extends Place {

	public static final up UP_PLACE = new up();
	
	private up() {
	}

	public static class Tokenizer implements PlaceTokenizer<up>
	{

		@Override
		public up getPlace(String token) {
			return UP_PLACE;
		}

		@Override
		public String getToken(up place) {
			return "";
		}

	}

}
