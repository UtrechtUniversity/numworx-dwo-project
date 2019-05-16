package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class LogoutPlace extends Place {

	private LogoutPlace() {
	}
	
	public static final LogoutPlace INSTANCE = new LogoutPlace();
	
	public static class Tokenizer implements PlaceTokenizer<LogoutPlace>
	{

		@Override
		public LogoutPlace getPlace(String token) {
			return INSTANCE;
		}

		@Override
		public String getToken(LogoutPlace place) {
			return "";
		}
	}
	
}
