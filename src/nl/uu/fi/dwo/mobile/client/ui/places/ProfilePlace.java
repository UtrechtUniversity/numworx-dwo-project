package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class ProfilePlace extends Place
{
	private String token;

	public ProfilePlace(String token)
	{
		this.token = token;
	}

	public String getToken()
	{
		return this.token;
	}

	public static class Tokenizer implements PlaceTokenizer<ProfilePlace>
	{

		@Override
		public ProfilePlace getPlace(String token)
		{
			return new ProfilePlace(token);
		}

		@Override
		public String getToken(ProfilePlace place)
		{
			return place.getToken();
		}

	}
}
