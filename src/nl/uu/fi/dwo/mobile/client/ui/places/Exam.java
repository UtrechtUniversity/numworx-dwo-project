package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class Exam extends Place {
  public static class Tokenizer implements PlaceTokenizer<Exam>
  {

      @Override
      public Exam getPlace(String token) {
          return new Exam();
      }

      @Override
      public String getToken(Exam place) {
          return "";
      }

  }

}
