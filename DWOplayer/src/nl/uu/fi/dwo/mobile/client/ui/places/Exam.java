package nl.uu.fi.dwo.mobile.client.ui.places;

import java.util.Objects;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class Exam extends Place {
    private final Object token;
    
    public Exam(Object token2) {
      this.token = token2;
    }
  
  
  public static class Tokenizer implements PlaceTokenizer<Exam>
  {

      @Override
      public Exam getPlace(String token) {
          return new Exam(token);
      }

      @Override
      public String getToken(Exam place) {
          return place.getToken();
      }

  }


  public String getToken() {
    return Objects.toString(token, "");
  }

}
