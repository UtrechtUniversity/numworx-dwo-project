package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class s extends Place implements Hash {

	private String token;
	
	s(String token) {
		this.token = token;
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

	public String getToken()
	{
		return token;
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
