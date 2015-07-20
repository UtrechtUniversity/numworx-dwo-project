package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem.Type;
import nl.uu.fi.dwo.mobile.client.ui.places.SelectModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.TreeModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ViewModulePlace;
import nl.uu.fi.dwo.mobile.client.ui.views.TreeModuleView;

import com.google.gwt.core.shared.GWT;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
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

public class TreeModuleActivity extends MGWTAbstractActivity implements TreeModuleView.Presenter, Comparator<SelectModuleItem>
{

	ClientFactory clientFactory;
	private List<SelectModuleItem> currentModel;
	private TreeModuleView view;
	private SelectModuleItem item;
	private Map<Object,Integer> ranking;

	public TreeModuleActivity(ClientFactory clientFactory, SelectModuleItem i)
	{
		this.clientFactory = clientFactory;
		this.item = i;
		this.ranking = clientFactory.getRPCHandler().courseSortMap; // FIXME OEF!
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		view = clientFactory.getTreeModuleView();
		view.setSortModel(this);
		boolean select = true;
		if(item.getType() == Type.MODULE && DWOplayer.profiledata != null) {
			Object userID = DWOplayer.profiledata.get("userID");
		if(userID != null) {	
			Object courseID = item.getID();
			AsyncCallback<List<Map<String,Object>>> getUserResultsCallback = new AsyncCallback<List<Map<String,Object>>>() {

				@Override
				public void onFailure(Throwable caught) {
					GWT.log("failure", caught);
					view.selectModule(item);
				}

				@Override
				public void onSuccess(List<Map<String,Object>> result) {
					Map<Object, Number> scoreMap = item.getScoreMap();
					if(scoreMap == null) {
						scoreMap = new HashMap<Object,Number>();
						item.setScoreMap(scoreMap);
					}
					for( Map<String,Object> entry : result) {
						Object id = entry.get("scoID");
						Object score = entry.get("score");
						if(score instanceof Number) {
							scoreMap.put(id, (Number) score);
						} else {
							scoreMap.remove(id);
						}
					}
					GWT.log("succes " + result);
					view.selectModule(item);
				}
			};
			clientFactory.getRPCHandler().getUserResults(courseID, userID, getUserResultsCallback);
			select = false;
		}}

		panel.setWidget(view);
		currentModel = SelectModuleItemHolder.getItems();
		view.setPresenter(this);
		view.render(currentModel);
		if(select)
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

	@Override
	public int compare(SelectModuleItem o1, SelectModuleItem o2) {
		Object c1 = o1.getID(); Integer n1 = ranking.get(c1);
		Object c2 = o2.getID(); Integer n2 = ranking.get(c2);
		if(n2 == null && n1 == null) {
			// unsorted
			return o1.getName().compareTo(o2.getName());
		}
		if( n2 != null && n1 != null) {
			return n1.compareTo(n2);
		}
		if( n1 == null) return +1;		
		return -1;
	}

	

}
