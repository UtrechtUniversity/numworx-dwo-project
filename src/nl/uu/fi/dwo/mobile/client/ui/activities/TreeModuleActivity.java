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

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
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
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.MGWTAbstractActivity;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedHandler;

public class TreeModuleActivity extends MGWTAbstractActivity implements TreeModuleView.Presenter
{

	ClientFactory clientFactory;
	private List<SelectModuleItem> currentModel;
	private TreeModuleView view;
	private SelectModuleItem item;

	public TreeModuleActivity(ClientFactory clientFactory, SelectModuleItem i)
	{
		this.clientFactory = clientFactory;
		this.item = i;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		view = clientFactory.getTreeModuleView();
		panel.setWidget(view);
		currentModel = SelectModuleItemHolder.getItems();
		view.setPresenter(this);
		view.render(currentModel);
		view.selectModule(item);
	}
	@Override
	public void onStop() {
		view.close();
		super.onStop();
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
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}

	

}
