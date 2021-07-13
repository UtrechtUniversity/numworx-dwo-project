package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Provider;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.s;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorContext;
import nl.uu.fi.dwo.mobile.client.ui.views.GotoController;
import nl.uu.fi.dwo.mobile.client.ui.views.HeaderView;
import nl.uu.fi.dwo.mobile.client.ui.views.NoCourseView;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import dagger.MembersInjector;
import dagger.Reusable;
import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;

public class ScoActivity extends AbstractActivity implements AnchorContext, ViewModuleView.Presenter, GotoController {

	private ViewModuleView view;
	private String name;
	private AnchorContext defaultContext;
	final private LoginPlace next;
	final private String location;
	@Inject PlaceController placeController;
	@Inject nl.uu.fi.dwo.mobile.client.ui.RPCHandler rpcHandler;
	@Inject DWOplayerParameters PARAMETERS;
	private boolean started;

	private DomSchoolClass schoolClass;
    private DomSchool school;
    private RoleType  role;

    @Inject Provider<NoCourseView> noCourseView;
    @Inject HeaderView headerView;
    private final PersistenceId where;
    private Promise<List<DomScoContext>> scoList;
	private SelectModuleItem item;
	private DwoGlobalVars vars;
	private boolean withUser;

	private ScoActivity(s where) {
	    this.where = where.getID();
		next = new LoginPlace(where);
		location = where.getLocation();
	}
		
	@Reusable public static class Factory {
		@Inject MembersInjector<ScoActivity> injector;
		@Inject Provider<ViewModuleView> clientFactory;
		@Inject DwoGlobalVars vars;
		@Inject Factory() {}
		
		public ScoActivity create(SelectModuleItem item, Place place) {
			ScoActivity activity = new ScoActivity(clientFactory, item, (s)place, vars);
			injector.injectMembers(activity);
			return activity;
		}
	}
	
	
	
	private ScoActivity(Provider<ViewModuleView> clientFactory, SelectModuleItem item, s where, DwoGlobalVars vars) {
		this(where);
		this.item = item;
		this.vars = vars;
		schoolClass = vars.getCurrentSchoolClass();
		school = vars.getSchool();
		role = vars.getRoleType();
		view = clientFactory.get();
		withUser = vars.withUser();
	}

	DomScoContext findSco(DomCoursesOfSchoolClass csc) {
	  return  csc.getScoContexts().stream()
	      .filter(entry -> where.equals(entry.getKey()))
	      .findAny().get().getValue(); 
	}
	List<DomScoContext> listScos(DomCoursesOfSchoolClass csc) {
	  return csc.getScoContexts().stream().map(DomMapEntry::getValue).collect(Collectors.toList());
	}
	
	@Override
	public void start(final AcceptsOneWidget panel, EventBus eventBus)
	{
		defaultContext = view.getAnchorContext();
		view.setPresenter(this);
		view.removeBtns(); // up en home
		
		String scoID = item.getID().toString();
		DWOplayer.insertCSS(scoID);
		view.setUnitId(scoID);
		name = item.getName();
		Promise<String> namePromise;
		if(name == null) {
			name = scoID;
			Promise<DomScoContext> sco;
			Promise<DomCoursesOfSchoolClass> course;
			if (schoolClass != null /*&& item.isFromSchool()*/) { // XXX unsure if isSchool correct
			  //sco = rpcHandler.getSco(item.getID());
			  course = rpcHandler.getScoContextClass(item.getID(), schoolClass)
			  .filter(p -> !p.getScoContexts().isEmpty())
// should not happen. Server should prevent this.
			  .filter(p-> p.getClassCourses().get(0).getValue().getCourseType() != CourseType.assesment);
              sco = course
			  .map(this::findSco);
              scoList = 
              course.then(p-> { 
                SelectModuleItem parent = new SelectModuleItem(p.getValue().getCourses().get(0).getValue(), p.getValue().getClassCourses().get(0).getValue());
                SelectModuleItemHolder.insert(parent);
                item.setParent(parent);
                return p;} )              
// all scos of course
            		  .then(p -> {
            			  Object id = item.getParentID();
            			  return rpcHandler.getCourseClass(id, schoolClass);
            		  })
//
            		  .map(this::listScos);
            		  
            		 // scoList.then(p -> { item.getParent().setChildrenAsync(p);return p; });
			} else {
	            sco = rpcHandler.getSco(item.getID())
	            // temporary		
	            .filter( ssc -> {
	            	PersistenceId schoolID = ssc.getSchoolId();
	            	if(schoolID == null) return true;
	            	if (school == null) return false;
	            	return school.getId().equals(schoolID);
	            });
	            scoList = sco
	                .flatMap(p -> rpcHandler.getCourse(p.getCourseId()))
	                .then( p -> { 
	                    SelectModuleItem parent = new SelectModuleItem(p.getValue(),(DomClassCourse)null);
	                    SelectModuleItemHolder.insert(parent);
	                    item.setParent(parent);
	                    return rpcHandler.getScos(p.getValue());
	                });
			}
			namePromise = 
			  sco.then(new Success<DomScoContext, String>() {

				@Override
				public Promise<String> call(Promise<DomScoContext> resolved) throws Exception {
					name = resolved.getValue().getScoName();
					item.setName(name);
					item.setFromSchool(resolved.getValue().getSchoolId() != null);
					view.setScoType(resolved.getValue().getScoType());
					view.setTitle(name);
					return Promises.resolved(name);
				}
			});
			
		} else {
			namePromise = Promises.resolved(name);
			scoList = rpcHandler.getScos(item.getParentID());
		}
		final Failure failure = new Failure() {
			
			@Override
			public void fail(Promise<?> resolved) throws Exception {
				Throwable t = resolved.getFailure();
				if(t instanceof Dwo2Exception) {
					Dwo2Exception e = (Dwo2Exception) t;
					if( e.getDwo2Code() == Dwo2ExceptionCode.Rest_LoginNeeded && school == null)
					{	item.setFromSchool(true);
						started = false;
						gotoNext();
						return;
					}
				}
				if (t instanceof NoSuchElementException || t instanceof Dwo2Exception)
                {
                    NoCourseView view = noCourseView.get();
            		DomUserFull currentUser = vars.getCurrentUser();
            		headerView.setUserAndRole(currentUser, role);
            		headerView.setPresenter(ScoActivity.this);
                    panel.setWidget(view);
                    view.setHomePlace(next.getPlace());
                    view.render();
                    return;
                }
				Logger.getLogger("ScoActivity").log(Level.SEVERE, "initialize()", t);
				Window.alert(t.getMessage());
				gotoNext();
			}
		};
		namePromise.flatMap(
				new Function<String, Promise<? extends Void>>() {

					@Override
					public Promise<? extends Void> apply(String t) {
						PromiseCallback<Void> callback = new PromiseCallback<Void>();
						view.getApi().Initialize(callback);
						return callback.getPromise();
					}
				}
				).then(new Success<Void, Boolean>() {

					@Override
					public Promise<Boolean> call(Promise<Void> resolved)
							throws Exception {
						started = ! Memento.COMPLETED.equals(view.getApi().GetValue(Memento.COMPLETION_STATUS));
						if(location != null) {
							view.getApi().SetValue(Memento.LOCATION, location);
						}
						Promise<Boolean> p = view.setupModule(name, PARAMETERS.getLaunchData() + item.getID());
						panel.setWidget(view);
	                    view.setAnchorContext(ScoActivity.this);
						return p; // true is fout
					}
				}, failure);
		
	}


	@Override
	public void onStop() {
		started = false;
		view.setAnchorContext(defaultContext);
		view.close();
		super.onStop();
	}
	
	@Override public void onCancel() {
		started = false;
		view.setAnchorContext(defaultContext);
		super.onCancel();
	}

	@Override
	public String mayStop() {
		if (started && withUser)
			return Text.constants.maybe_lost_data();
		return super.mayStop();
	}
	@Override
	public void prepareLeave() {
		started = false;
	}
	
	@Override
	public void gotoUrl(String href) {
		if("goto:0".equals(href))
			gotoNext();
		else if (href.startsWith("goto:") && href.charAt(5) != '.') {
		  gotoHref(href.substring(5));	  
		} else 
			defaultContext.gotoUrl(href);
	}

	protected void gotoHref(String href) {
	    int dot = href.indexOf('.');
	    String label = dot < 0 ? href:href.substring(0, dot);
	    String location;
	    if (dot>=0) 
	    	location = Integer.toString(Integer.parseInt(href.substring(dot+1))-1); // subtract 1
	    else 
	    	location = null;
	    scoList.then(p -> {
	      List<SelectModuleItem> items = new ArrayList<>();
	      for (DomScoContext i:p.getValue()) {
	        if (!i.getId().equals(where)) {
	          SelectModuleItem sm = new SelectModuleItem(i);
              SelectModuleItemHolder.insert(sm);
              items.add(sm);	          
	        } else {
	          items.add(item);
	        }
	      }

	      SelectModuleItemHolder.getItemByID(item.getParentID()).setChildren(items);

	      Object id = null;
	      try { 
    	      int n = Integer.parseInt(label)-1;
    	      DomScoContext sco = p.getValue().get(n);
    	      id = PersistenceIdDecoderInterface.instance.idOf(sco.getId(), PersistenceClassType.PersistentScoContext);
	      } catch (Exception e) {
	        for(DomScoContext sco: p.getValue()) {
	          if (sco.getScoName().startsWith(label)) {
	             id = PersistenceIdDecoderInterface.instance.idOf(sco.getId(), PersistenceClassType.PersistentScoContext);
	             break;
	          }
	        }
	      }
	      if (id == null) return p;
	      Place place = new nl.uu.fi.dwo.mobile.client.ui.places.s(id, location);
	      started = false;
	      placeController.goTo(place);
	      return p;
	    });
	}
	
	void gotoNext() {
      started = false;
      placeController.goTo(next);
	}

	@Override
	public void goTo(Place place) {
		gotoNext();	
	}

}
