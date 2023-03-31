package nl.uu.fi.dwo.mobile.client.ui.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class MaybeLogout extends Place implements HasHash {
  private Hash token;
 
  String getToken()
  {
      if(token == null)
          return "";
      return token.getType() + "/" + token.getType().getT().getToken(token.getPlace());
  }

  MaybeLogout(Hash token)
  {
      this.token = token;
  }

  public Place getPlace() {
    if(token == null) 
        return null;
    return token.getPlace();
}
 
  public MaybeLogout(Place place) {
      if(place instanceof Hash)
          this.token = (Hash) place;
  }

  public MaybeLogout() {
  }

  public static class Tokenizer implements PlaceTokenizer<MaybeLogout>
  {

      @Override
      public MaybeLogout getPlace(String token)
      {
          if(token == null) token = "";
          int slash = token.indexOf('/');
          if(slash == -1) return new MaybeLogout();
          Hash.Type pfx = Hash.Type.valueOf(token.substring(0, slash));
          Hash hash = pfx.getHash(token.substring(slash+1));          
          return new MaybeLogout(hash);
      }

      @Override
      public String getToken(MaybeLogout place)
      {
          return place.getToken();
      }
  }

}
