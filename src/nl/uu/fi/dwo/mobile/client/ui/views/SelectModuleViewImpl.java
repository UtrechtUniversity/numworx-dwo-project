package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.List;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleCell;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItemHolder;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ProfilePlace;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.widget.CellList;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.HasCellSelectedHandler;

/**
 * 
 * @author Danny Hendrix
 * 
 */
public class SelectModuleViewImpl implements SelectModuleView
{
	private LayoutPanel main;
	private CellList<SelectModuleItem> list;
	private HeaderButton backbutton;

	public SelectModuleViewImpl()
	{
		main = new LayoutPanel();

		HeaderPanel header = new HeaderPanel();
		header.setCenter("Selecteer module");
		main.add(header);

		backbutton = new HeaderButton();
		backbutton.setBackButton(true);
		if (DWOplayer.profiledata != null)
			backbutton.setText("Profiel");
		else
			backbutton.setText("Login");

		backbutton.addTapHandler(new TapHandler()
		{

			@Override
			public void onTap(TapEvent event)
			{
				if (DWOplayer.profiledata != null)
					DWOplayer.clientfactory.getPlaceController().goTo(new ProfilePlace("Profile"));
				else
					DWOplayer.clientfactory.getPlaceController().goTo(new LoginPlace("Login"));
			}
		});

		header.setLeftWidget(backbutton);

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
					list.render(SelectModuleItemHolder.getItems());
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

		list = new CellList<SelectModuleItem>(new SelectModuleCell());
		main.add(list);
	}

	@Override
	public Widget asWidget()
	{
		return main;
	}

	@Override
	public void render(List<SelectModuleItem> items)
	{
		if (DWOplayer.profiledata != null)
			backbutton.setText("Profiel");
		else
			backbutton.setText("Login");
		list.render(items);
	}

	@Override
	public HasCellSelectedHandler getList()
	{
		return list;
	}

}
