package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class c extends Place implements Hash {

	private String token;
	
	c(String token) {
		this.token = token;
	}
	public static class Tokenizer implements PlaceTokenizer<c>
	{

		@Override
		public c getPlace(String token)
		{
			return new c(token);
		}

		@Override
		public String getToken(c place)
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
		return Type.c;
	}

	@Override
	public Place getPlace() {
		return this;
	}

}
