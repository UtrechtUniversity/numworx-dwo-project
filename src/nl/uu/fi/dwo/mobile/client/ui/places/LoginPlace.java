package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class LoginPlace extends Place
{
	private Hash token;

	LoginPlace(Hash token)
	{
		this.token = token;
	}
	
	public LoginPlace(Place place) {
		if(place instanceof Hash)
			this.token = (Hash) place;
	}

	public LoginPlace()
	{
	}

	String getToken()
	{
		if(token == null)
			return "";
		return token.getType() + "/" + token.getType().getT().getToken(token.getPlace());
	}
	
	public Place getPlace() {
		if(token == null) 
			return null;
		return token.getPlace();
	}

	public static class Tokenizer implements PlaceTokenizer<LoginPlace>
	{

		@Override
		public LoginPlace getPlace(String token)
		{
			if(token == null) token = "";
			int slash = token.indexOf('/');
			if(slash == -1) return new LoginPlace();
			Hash.Type pfx = Hash.Type.valueOf(token.substring(0, slash));
			Hash hash = pfx.getHash(token.substring(slash+1));			
			return new LoginPlace(hash);
		}

		@Override
		public String getToken(LoginPlace place)
		{
			return place.getToken();
		}

	}
}
