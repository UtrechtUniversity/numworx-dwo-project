package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.List;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleView;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleView.Presenter;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;

public class TreeModuleActivity extends MGWTAbstractActivity implements Presenter
{

	ClientFactory clientFactory;

	public TreeModuleActivity(ClientFactory clientFactory)
	{
		this.clientFactory = clientFactory;
		//getItems();
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		TreeModuleView view = clientFactory.getTreeModuleView();
		panel.setWidget(view);
		List<SelectModuleItem> currentModel = SelectModuleItemHolder.getItems();

		view.render(currentModel);
		Place place = clientFactory.getPlaceController().getWhere();

		if (place instanceof TreeModulePlace)
		{
			TreeModulePlace selectedModulePlace = (TreeModulePlace) place;
			int id = Integer.parseInt(selectedModulePlace.getToken());

			SelectModuleItem item = SelectModuleItemHolder.getItemByID(id);
			view.selectModule(item);
			view.setPresenter(this);
		}

	}

	private void getItems()
	{
		RequestBuilder.Method method = RequestBuilder.GET;
		String url = "activiteiten.xml";
		RequestBuilder rb = new RequestBuilder(method, url);
		try
		{
			rb.sendRequest(null, new RequestCallback()
			{

				@Override
				public void onResponseReceived(Request request, Response response)
				{
					String responseText = response.getText();
					if (!responseText.isEmpty())
					{
						Document dom = XMLParser.parse(responseText);
						Node main = dom.getElementsByTagName("activiteiten").item(0);
						NodeList children = main.getChildNodes();
						int j = 0;
						for (int i = 0; i < children.getLength(); i++)
						{
							if (children.item(i).hasChildNodes() == true)
								SelectModuleItemHolder.insert(j++, children.item(i));
						}
					}
					//list = new CellList<SelectModuleItem>(new SelectModuleCell());
					clientFactory.getTreeModuleView().render(SelectModuleItemHolder.getItems());
				}

				@Override
				public void onError(Request request, Throwable exception)
				{
					Window.alert("error loading activiteiten.xml");
				}
			});

		}
		catch (RequestException e)
		{
			Window.alert("error loading activiteiten.xml");
		}

	}

	@Override
	public void back() {
		History.back();
	}

	@Override
	public void selectItem(SelectModuleItem o) {
		Place place;
		switch(o.getType()) {
		default:
		case ROOT:
			place = new TreeModulePlace("0");
			break;
		case SCO:
			place = new ViewModulePlace(o.getID());
			break;
		case MODULE:
			place = new SelectModulePlace(o.getID());
			break;
		case FOLDER:
			place = new TreeModulePlace(o.getID());
			break;
		}
		clientFactory.getPlaceController().goTo(place);
	}

}
