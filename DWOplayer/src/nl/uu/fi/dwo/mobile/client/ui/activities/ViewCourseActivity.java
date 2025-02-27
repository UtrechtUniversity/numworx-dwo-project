package nl.uu.fi.dwo.mobile.client.ui.activities;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import dagger.MembersInjector;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.c;

public class ViewCourseActivity extends ViewModuleActivity {
	
  public static Place bookmark;	
	

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

  
//FIXME Even voor de test van schoolyear op chromebook

@Override
public String mayStop() {
	
	if (started && isSEB && lastExam.get().restorable()) {
		started = false;
		bookmark = placeController.getWhere();
	}
	return super.mayStop();
}

@Override
public void start(AcceptsOneWidget panel, EventBus eventBus) {
	bookmark = null;
	super.start(panel, eventBus);
}

}
