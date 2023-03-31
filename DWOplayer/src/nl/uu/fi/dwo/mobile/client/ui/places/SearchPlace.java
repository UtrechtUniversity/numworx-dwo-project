package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class SearchPlace extends Place {

	final long id;

	public SearchPlace(Object object) {
		this.id =(Long) object;
	}
	
	public long getId() {
		return id;
	}

	public static class Tokenizer implements PlaceTokenizer<SearchPlace>
	{

		@Override
		public SearchPlace getPlace(String token) {
			return new SearchPlace(Long.parseLong(token));
		}

		@Override
		public String getToken(SearchPlace place) {
			return String.valueOf(place.getId());
		}
	}

}
