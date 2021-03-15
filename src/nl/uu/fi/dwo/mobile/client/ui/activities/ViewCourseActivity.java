package nl.uu.fi.dwo.mobile.client.ui.activities;

import com.google.gwt.place.shared.Place;

import dagger.MembersInjector;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.c;

public class ViewCourseActivity extends ViewModuleActivity {

  public ViewCourseActivity(MembersInjector<ViewModuleActivity> injector, SelectModuleItem sco,
      ViewModulePlace where) {
    super(injector, sco, where);
  }

  @Override
  public void goTo(Place place) {
    c.Tokenizer tokenizer = new c.Tokenizer();
    if(place instanceof TreeModulePlace) { // home/up
      place = tokenizer.getPlace(sco.getParentID().toString());
    }
    if (place instanceof LoginPlace) {
      place = new LoginPlace(tokenizer.getPlace(sco.getParentID().toString())); // logout/login
    }
    super.goTo(place);
  }

}
