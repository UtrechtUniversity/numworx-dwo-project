package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView.AnchorContext;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;

/**
 * Display module activity
 * 
 * @author Danny Hendrix
 * 
 */
public class ViewModuleActivity extends MGWTAbstractActivity implements AnchorContext, ViewModuleView.Presenter
{
	private ClientFactory clientFactory;
	private ViewModuleView view;
	private AnchorContext defaultContext;
	private SelectModuleItem sco;
	private Timer tm;
	private boolean started;
	
	
	
	@Override
	public String mayStop() {
		if (started && clientFactory.withUser())
			return Text.constants.maybe_lost_data();
		return super.mayStop();
	}

	public ViewModuleActivity(ClientFactory clientFactory, SelectModuleItem sco)
	{
		this.clientFactory = clientFactory;
		this.sco = sco;

	}

	@Override
	public void start(final AcceptsOneWidget panel, EventBus eventBus)
	{
// Eindtijd
// History.back is terug naar waar je vandaan komt
		Date notAfter = sco.getNotAfter();
		if(notAfter != null && notAfter.getTime() < System.currentTimeMillis() + DWOplayer.timezone)
		{
			started = false;
			panel.setWidget(new Label("Activiteit verlopen"));
			History.back();
			view = null;
			return;
		} else if (notAfter != null) {
			long timeToGo = notAfter.getTime()-System.currentTimeMillis() - DWOplayer.timezone;
			timeToGo = Math.min(timeToGo, Integer.MAX_VALUE);
			timeToGo = Math.max(timeToGo,1);
			tm = new Timer() {

				@Override
				public void run() {
					tm = null;
					started = false;
					History.back();
				}};
			tm.schedule((int)timeToGo); 
		}			
// All systems go
		
		
		
		started = true;
		clientFactory.getHeaderView().hide();
		view = clientFactory.getEntryView();
		panel.setWidget(view); // terug naar af. problemen met gekke scrolls
		{
			final String id = sco.getID().toString();
			List<SelectModuleItem> trail = new ArrayList<SelectModuleItem>();
			SelectModuleItem parent = sco.getParent();
			while(parent != null) {
				trail.add(parent);
				parent = parent.getParent();
			}
			view.setTrail(trail);
			view.setTitle(sco.getName());
			view.setScoType(sco.getScoType());
			view.setPresenter(this);
			
			defaultContext = view.getAnchorContext();
			view.setAnchorContext(this);
			view.setUnitId(id);
		
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Logger.getLogger("ViewModuleActivity").log(Level.SEVERE, "initialize()", caught);
				Window.alert(caught.getMessage());
				started = false;
				History.back();
				//view.setupModule(sco.getName(), sco.getFile());
				view = null;
			}

			@Override
			public void onSuccess(Void result) {
				view.setupModule(sco.getName(), sco.getFile());
				//panel.setWidget(view);
			}
		};
		view.getApi().Initialize(callback);
			
			addHandlerRegistration(view.getBackButton().addTapHandler(new TapHandler()
			{

				@Override
				public void onTap(TapEvent event)
				{
					started = false;
					History.back();
				}
			}));
		}
	}

	@Override
	public void onStop() {
		if (tm != null) {
			tm.cancel();
			tm = null;
		}
		if(view != null) {
			view.setAnchorContext(defaultContext); // unwrap
			view.close();
			sco.setScore(view.getScoreRaw());
		}
		super.onStop();
	}
	
	@Override
	public void onCancel() {
		started = false;
		if(tm!=null) {
			tm.cancel();
			tm=null;
		}
		if(view != null) {
			view.setAnchorContext(defaultContext); // unwrap
		}
		super.onCancel();
	}

	@Override
	public void gotoUrl(String href) {
		if("goto:0".equals(href)) {
			started = false;
			History.back();
		}
		else if(href.startsWith("goto:.")) defaultContext.gotoUrl(href);
		else if(href.startsWith("goto:")){
			href = href.substring(5);
			SelectModuleItem parent = sco.getParent();
			List<SelectModuleItem> list = parent.getChildren();
			int page = href.lastIndexOf('.');
			if(page >= 0) href = href.substring(0, page);
			int sconr = -1;
			try {
				sconr = Integer.parseInt(href)-1;
			} catch(Exception _) {}
			if(sconr <= -1 || sconr >= list.size())
			{
				for(sconr = 0; sconr < list.size(); sconr ++) {
					if(list.get(sconr).getName().startsWith(href))
						break; // found by prefix
				}
			}
			if(sconr == list.size()) {
				sconr = Integer.parseInt(href)-1;
			}
			SelectModuleItem item = list.get(sconr);
			Object scoid = item.getID();
			if(item != sco )
			{	
				goTo(new ViewModulePlace(scoid));
			}
		}
	}

	@Override
	public void goTo(Place place) {
		started = false;
		clientFactory.getPlaceController().goTo(place);
	}
	
}
