package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class s extends ViewModulePlace implements Hash {

	public s(Object token, String location ) {
	  super(token, location);
    }
	  	
	s(String token) {
		super(token);
	}

	public static class Tokenizer implements PlaceTokenizer<s>
	{

		@Override
		public s getPlace(String token)
		{
			return new s(token);
		}

		@Override
		public String getToken(s place)
		{
			return place.getToken();
		}
	}

	@Override
	public Type getType() {
		return Type.s;
	}

	@Override
	public Place getPlace() {
		return this;
	}

}
