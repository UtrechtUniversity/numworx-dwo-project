package nl.uu.fi.dwo.mobile.touchtest;


import java.util.HashMap;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.AppPlaceHistoryMapper;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactoryImpl;
import nl.uu.fi.dwo.mobile.client.ui.TabletAnimationMapper;
import nl.uu.fi.dwo.mobile.client.ui.activities.LoginActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.ProfileActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.SelectModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.activities.TreeModuleActivity;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ProfilePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartHandler;
import com.googlecode.mgwt.mvp.client.AnimatableDisplay;
import com.googlecode.mgwt.mvp.client.AnimatingActivityManager;
import com.googlecode.mgwt.mvp.client.Animation;
import com.googlecode.mgwt.mvp.client.AnimationMapper;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort.DENSITY;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Touchtest implements EntryPoint {

	public class NoHistory implements PlaceHistoryMapper {

		@Override
		public Place getPlace(String token) {
			// TODO Auto-generated method stub
			return defaultPlace;
		}

		@Override
		public String getToken(Place place) {
			// TODO Auto-generated method stub
			return "default";
		}

	}

	public class AnimMapper implements AnimationMapper {

		@Override
		public Animation getAnimation(Place oldPlace, Place newPlace) {
			return null;
		}

	}

	public LoginView mainView;
	public Place    defaultPlace = new LoginPlace(); 

	public class MyActivity extends MGWTAbstractActivity implements LoginView.Presenter {

		@Override
		public void start(AcceptsOneWidget panel, EventBus eventBus) {
			mainView.setupModule(this);
			panel.setWidget(mainView);

		}

		@Override
		public void login() {
			Window.alert("login");
			
		}

		@Override
		public void login(String username, String password) {
			Window.alert("login " + username );
			DWOplayer.profiledata = new HashMap();
			DWOplayer.profiledata.put("firstname", username);
			DWOplayer.profiledata.put("middlename", "");
			DWOplayer.profiledata.put("lastname", "");
			
			clientFactory.getPlaceController().goTo(new ProfilePlace("Profile"));
		}

	}

	public class AMapper implements ActivityMapper {

		@Override
		public Activity getActivity(Place place) {
			if(place instanceof LoginPlace)
				return new LoginActivity(clientFactory);
			else if(place instanceof ProfilePlace)
				return new ProfileActivity(clientFactory);
			else if (place instanceof TreeModulePlace)
			{
				//TreeModuleActivity treeModuleActivity = new TreeModuleActivity(clientFactory);
				DummyTreeModuleActivity treeModuleActivity = new DummyTreeModuleActivity(clientFactory);
				return treeModuleActivity;
			}
//			else if (place instanceof SelectModulePlace)
//				return new SelectModuleActivity(clientFactory);
					
			return null;
		}

	}

	private EventBus eventBus;
	private PlaceController placeController;
	private Label label;
	private ClientFactoryImpl clientFactory;
	

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		clientFactory = new ClientFactoryImpl();
		DWOplayer.clientfactory = clientFactory;
		eventBus = clientFactory.getEventBus();
		//eventBus = new SimpleEventBus();
		
		
		
		
		//MGWT Settings//
		ViewPort viewport = new MGWTSettings.ViewPort();
		viewport.setTargetDensity(DENSITY.MEDIUM);
		viewport.setUserScaleAble(false).setMinimumScale(1.0).setMaximumScale(1.0);
		MGWTSettings settings = new MGWTSettings();
		settings.setViewPort(viewport);
		settings.setAddGlosToIcon(true);
		//settings.setFullscreen(true);
		//settings.setPreventScrolling(true);
		MGWT.applySettings(settings);

		//AnimatableDisplay display = GWT.create(AnimatableDisplay.class);
		SimplePanel display = new SimplePanel();
		
//		LoginView lv;
//		mainView = new LoginViewTest().asWidget();
//		mainView = lv = clientFactory.getLoginView();
//		LayoutPanel panel = new LayoutPanel();
//		Button btn = new Button("oops");
//		label = new Label("hits");
//		panel.add(label); panel.add(btn);
//		btn.addTouchStartHandler(new Handler());
//		mainView = panel;
		
		
		ActivityMapper appActivityMapper = new AMapper();
		AnimationMapper animationMapper = new AnimMapper();
		ActivityManager activityMapper = new ActivityManager(appActivityMapper, eventBus);
		activityMapper.setDisplay(display);
		label = new Label("log...");
		RootPanel.get().add(label);
		RootPanel.get().add(display);
		PlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
		final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
		placeController = clientFactory.getPlaceController();
		historyHandler.register(placeController, eventBus, defaultPlace);
		historyHandler.handleCurrentHistory();
		
		NativePreviewHandler handler = new NativePreviewHandler() {
			int cnt;
			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				
				final String type = event.getNativeEvent().getType();
				if("mousemove".equals(type))
					return;
				String s = cnt++ + (type + " " /*+ event.getNativeEvent().getEventTarget()*/);
				label.setText(s);
			}};
		Event.addNativePreviewHandler(handler);
		

	}
}
