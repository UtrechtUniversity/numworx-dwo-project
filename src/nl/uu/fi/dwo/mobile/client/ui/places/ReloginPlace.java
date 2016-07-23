package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class ReloginPlace extends Place
{
	private Hash token;

	ReloginPlace(Hash token)
	{
		this.token = token;
	}
	
	public ReloginPlace(Place place) {
		if(place instanceof Hash)
			this.token = (Hash) place;
	}

	public ReloginPlace()
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

	public static class Tokenizer implements PlaceTokenizer<ReloginPlace>
	{

		@Override
		public ReloginPlace getPlace(String token)
		{
			if(token == null) token = "";
			int slash = token.indexOf('/');
			if(slash == -1) return new ReloginPlace();
			Hash.Type pfx = Hash.Type.valueOf(token.substring(0, slash));
			Hash hash = pfx.getHash(token.substring(slash+1));			
			return new ReloginPlace(hash);
		}

		@Override
		public String getToken(ReloginPlace place)
		{
			return place.getToken();
		}

	}
}
