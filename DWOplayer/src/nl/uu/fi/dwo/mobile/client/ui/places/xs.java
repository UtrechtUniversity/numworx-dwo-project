package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.PlaceTokenizer;

import nl.uu.fi.dwo.mobile.client.ui.places.Hash.Type;

public class xs extends s {

	public xs(Object token, String location) {
		super(token, location);
	}

	public xs(String token) {
		super(token);
	}

	public static class Tokenizer implements PlaceTokenizer<xs>
	{

		@Override
		public xs getPlace(String token)
		{
			return new xs(token);
		}

		@Override
		public String getToken(xs place)
		{
			return place.getToken();
		}
	}

	@Override
	public Type getType() {
		return Type.xs;
	}

}
