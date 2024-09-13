package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.osgi.util.promise.Promise;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import dagger.Lazy;
import dagger.MembersInjector;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.CoursesOfClasToSelectItems;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.SCO_TO_MODULEITEM;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.TrafficAgent;
import nl.uu.fi.dwo.mobile.client.ui.places.Hash;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.NavigationView;
import nl.uu.fi.dwo.mobile.client.ui.views.NoCourseView;
import nl.uu.fi.dwo.mobile.client.ui.views.UnSafeModuleView;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class ClassCourseActivity implements Activity {

  @Inject ClassCourseActivity() {}
  @Inject PlaceController placeController;
  @Inject RPCHandler rpc;
  @Inject Lazy<UnSafeModuleView> unsafeModuleView;
  @Inject Lazy<NoCourseView> noCourseView;
  @Inject TrafficAgent barrier;
  @Inject HeaderView header;
  @Inject NavigationView navigation;
  @Inject DwoGlobalVars instance;
  @Inject Lazy<CoursesOfClasToSelectItems> coursesToItems;
  @Inject CourseActivity2.Factory caFactory;
  @Inject MembersInjector<ExamModuleActivity> exInjector;
  @Inject LastExamActivity lastExam;
  
  private Promise<DomCoursesOfSchoolClass> promise;
  private SelectModuleItem item;
  private Activity delegate;

  @Override
  public void start(AcceptsOneWidget panel, EventBus eventBus) {
    
      Place where = placeController.getWhere();
      DomUserFull currentUser = instance.getCurrentUser();
      if (currentUser == null) {
    	  lastExam.setActivity(() -> this, where).start(panel, eventBus);
    	  
    	  
//        Place newPlace = new LoginPlace(where);
//        goToAST(newPlace);
        return;
      }
// vanaf hier ingelogd.
      lastExam.suspend();
      Hash hash = (Hash) where;
      PersistenceId id = hash.getID();
      lastExam.setClassCourseId(id);
      promise = rpc.getClassCourse(id);
      promise.then(this::succes)
      .map(coursesToItems.get())
      .then(l -> {
        item = l.getValue().get(0);
        item.setPlace(where);
        SelectModuleItemHolder.insert(item);
        header.setHomePlace(where);
        header.setUpPlace(where);
        header.show(); navigation.hide();
        if (!item.isExam())
         item.setChildrenAsync(
          promise.map(v -> {
                        List<DomMapEntry<PersistenceId, DomScoContext>> list = v.getScoContexts();
                        ArrayList<DomScoContext> scos = new ArrayList<>();
                        for(DomMapEntry<PersistenceId, DomScoContext> entry: list) scos.add(entry.getValue());
                        return scos;
                    }).map(new SCO_TO_MODULEITEM(item)));
        
        Provider<Activity> activity = () -> caFactory.create(item, where);
        if (item.isExam()) {
          delegate = new ExamModuleActivity(item, activity, exInjector);
        } else 
          delegate = activity.get();
        delegate.start(panel, eventBus);
        return null;
      })
      
      .then(null, f -> {
        GWT.log("failure in promise", f.getFailure());
        NoCourseView w = noCourseView.get();
        w.setHomePlace(where);
        w.render();
        panel.setWidget(w);
      });
      
  }

  public void goToAST(Place newPlace) {
    Scheduler.get().scheduleDeferred(() -> placeController.goTo(newPlace));
  }
  
  Promise<DomCoursesOfSchoolClass> succes(Promise<DomCoursesOfSchoolClass> p) {
    DomCoursesOfSchoolClass r = p.getValue();
    DomSchoolClass currentClass = r.getSchoolClass();
    instance.setCurrentSchoolClass(currentClass);
    
    return p;
  }

  @Override
  public String mayStop() {
    if (delegate != null) return delegate.mayStop();
    return null;
  }

  @Override
  public void onCancel() {
    if (delegate != null) delegate.onCancel();
  }

  @Override
  public void onStop() {
    if (delegate != null) delegate.onStop();
    
  }

}
