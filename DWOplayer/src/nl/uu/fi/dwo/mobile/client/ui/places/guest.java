package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class guest extends Place {

	public guest() {
	}
	
	public static class Tokenizer implements PlaceTokenizer<guest>
	{

		@Override
		public guest getPlace(String token) {
			return new guest();
		}

		@Override
		public String getToken(guest place) {
			return "";
		}

	}

}
