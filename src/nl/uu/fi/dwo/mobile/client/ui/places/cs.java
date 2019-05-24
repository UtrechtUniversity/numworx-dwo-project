package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.PlaceTokenizer;

public class cs extends s {

	cs(String token) {
		super(token);
	}
	public static class Tokenizer implements PlaceTokenizer<cs>
	{

		@Override
		public cs getPlace(String token)
		{
			return new cs(token);
		}

		@Override
		public String getToken(cs place)
		{
			return place.getToken();
		}
	}

	@Override
	public Type getType() {
		return Type.cs;
	}
	
}
