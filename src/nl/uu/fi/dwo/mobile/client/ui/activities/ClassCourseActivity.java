package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import dagger.Lazy;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.CoursesOfClasToSelectItems;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.SecureMode;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.SCO_TO_MODULEITEM;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.TrafficAgent;
import nl.uu.fi.dwo.mobile.client.ui.places.Hash;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.c;
import nl.uu.fi.dwo.mobile.client.ui.views.ExamModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.ExamModuleView.Presenter;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.NavigationView;
import nl.uu.fi.dwo.mobile.client.ui.views.UnSafeModuleView;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class ClassCourseActivity extends AbstractActivity {

  @Inject ClassCourseActivity() {}
  @Inject PlaceController placeController;
  @Inject RPCHandler rpc;
  @Inject Lazy<UnSafeModuleView> unsafeModuleView;
  @Inject TrafficAgent barrier;
  @Inject HeaderView header;
  @Inject NavigationView navigation;
  @Inject DwoGlobalVars instance;
  private Promise<DomCoursesOfSchoolClass> promise;
  private SelectModuleItem item;

  @Override
  public void start(AcceptsOneWidget panel, EventBus eventBus) {
    
      Place where = placeController.getWhere();
      DomUserFull currentUser = instance.getCurrentUser();
      if (currentUser == null) {
        Place newPlace = new LoginPlace(where);
        goToAST(newPlace);
        return;
      }
// vanaf hier ingelogd.
      Hash hash = (Hash) where;
      PersistenceId id = hash.getID();
      promise = rpc.getClassCourse(id);
      promise.then(this::succes)
      .map(new CoursesOfClasToSelectItems())
      .then(l -> {
        item = l.getValue().get(0);
        SelectModuleItemHolder.insert(item);
        header.show(); navigation.hide();
        if (!item.isExam())
         item.setChildrenAsync(
          promise.map(v -> {
                        List<DomMapEntry<PersistenceId, DomScoContext>> list = v.getScoContexts();
                        ArrayList<DomScoContext> scos = new ArrayList<>();
                        for(DomMapEntry<PersistenceId, DomScoContext> entry: list) scos.add(entry.getValue());
                        return scos;
                    }).map(new SCO_TO_MODULEITEM(item)));
        
        Place cp = Hash.Type.c.getT().getPlace(item.getID().toString());
        goToAST(cp);
        return null;
      })
      
      .then(null, f -> placeController.goTo(new LoginPlace(where)));
      
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

//  @Override
//  public void onKO() {
//    Place where = placeController.getWhere();
//    goToAST(new LoginPlace(where));
//  }
//
//  @Override
//  public void onOk(String password, ExamModuleView view) {
//    Promise<List<SelectModuleItem>> scos = 
//    barrier.barrier().then(p -> promise)
//    .then(p -> rpc.startExam(p.getValue().getClassCourses().get(0).getKey().getIdString(), password))
//    .then(p -> rpc.getScos(promise.getValue().getCourses().get(0).getValue()), f -> view.showFailure(f.getFailure()))
//    .map(new SCO_TO_MODULEITEM(item));
// 
//    item.setChildrenAsync(scos);
//
//    
//    scos.then (p -> {
//      Place cp = Hash.Type.c.getT().getPlace(item.getID().toString());
//      goToAST(cp);
//      return null;
//    }, 
//      f -> view.showFailure(f.getFailure())
//    );
//
//    
//    
//  }

}
