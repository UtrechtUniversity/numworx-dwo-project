package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class last extends Place implements Hash {
	
	public static final last LAST_PLACE = new last();
	public static class Tokenizer implements PlaceTokenizer<last>
	{

		@Override
		public last getPlace(String token) {
			return LAST_PLACE;
		}

		@Override
		public String getToken(last place) {
			return "";
		}

	}
	private last() {
	}
	@Override
	public Type getType() {
		return Type.last;
	}
	@Override
	public Place getPlace() {
		return LAST_PLACE;
	}

	@Override
	public String getToken() {
		return "";
	}
	@Override
	public PersistenceId getID() {
		return null;
	}

}
