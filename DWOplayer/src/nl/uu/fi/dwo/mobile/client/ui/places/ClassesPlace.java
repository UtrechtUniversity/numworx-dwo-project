package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class ClassesPlace extends Place {

	public static class Tokenizer implements PlaceTokenizer<ClassesPlace>
	{
      @Override
      public ClassesPlace getPlace(String token) {
          return new ClassesPlace();
      }

      @Override
      public String getToken(ClassesPlace place) {
          return "";
      }
	}
}
