package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.PlaceTokenizer;

public class ViewCoursePlace extends ViewModulePlace {

  public ViewCoursePlace(Object token) {
    super(token);
  }

  public ViewCoursePlace(Object token, String location) {
    super(token, location);
  }

  public ViewCoursePlace(int id) {
    super(id);
  }

  public ViewCoursePlace(ViewModulePlace place) {
    super(place.getToken());
  }

  public static class Tokenizer implements PlaceTokenizer<ViewCoursePlace>
  {

      @Override
      public ViewCoursePlace getPlace(String token)
      {
          return new ViewCoursePlace(token);
      }

      @Override
      public String getToken(ViewCoursePlace place)
      {
          return place.getToken();
      }

  }

}
