package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.List;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView.AnchorContext;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;

/**
 * Display module activity
 * 
 * @author Danny Hendrix
 * 
 */
public class ViewModuleActivity extends MGWTAbstractActivity implements AnchorContext
{
	private ClientFactory clientFactory;
	private ViewModuleView view;
	private AnchorContext defaultContext;
	private SelectModuleItem sco;
	public ViewModuleActivity(ClientFactory clientFactory)
	{
		this.clientFactory = clientFactory;

	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		view = clientFactory.getEntryView();
		panel.setWidget(view);

		Place place = clientFactory.getPlaceController().getWhere();

		if (place instanceof ViewModulePlace)
		{
			ViewModulePlace selectedModulePlace = (ViewModulePlace) place;
			final String id = selectedModulePlace.getToken();
			sco = SelectModuleItemHolder.getScoByID(id);
			defaultContext = view.getAnchorContext();
			view.setAnchorContext(this);
			view.setUnitId(id);
			DWOplayer.api.setScoID(id);
			AsyncCallback<Void> callback = new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					view.setupModule(sco.getName(), sco.getFile());
				}

				@Override
				public void onSuccess(Void result) {
					view.setupModule(sco.getName(), sco.getFile());
				}
			};
			DWOplayer.api.Initialize(callback);
			addHandlerRegistration(view.getBackButton().addTapHandler(new TapHandler()
			{

				@Override
				public void onTap(TapEvent event)
				{
					History.back();
				}
			}));
		}
	}

	@Override
	public void onStop() {
		view.setAnchorContext(defaultContext); // unwrap
		view.close();
		super.onStop();
	}

	@Override
	public void gotoUrl(String href) {
		if(href.startsWith("goto:.")) defaultContext.gotoUrl(href);
		else if(href.startsWith("goto:")){
			href = href.substring(5);
			SelectModuleItem parent = sco.getParent();
			List<SelectModuleItem> list = parent.getChildren();
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
				clientFactory.getPlaceController().goTo(new ViewModulePlace(scoid));
		}
	}
	
}
