package nl.numworx.uploadwidgetgwt.client;



import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

import gwtupload.client.Utils;
import nl.numworx.uploadwidget.shared.AtomEntry;
import nl.numworx.uploadwidgetgwt.shared.Constants;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;

public class FeedPanel extends Composite implements Constants, RequestCallback {

	
	static final String ENTRY = "entry";
	private  RequestCallback response = new RequestCallback() {

		private void doClick(AtomEntry entry) {
			RequestBuilder req = new RequestBuilder(RequestBuilder.DELETE, entry.url);
			req.setIncludeCredentials(true);
			req.setHeader(AUTHORIZATION, comRoot.getContext().getString(AUTHORIZATION));
			req.setCallback(FeedPanel.this);
			try {
				Request request = req.send();
			} catch (RequestException e) {
				GWT.log("click response", e);
			}
		}
		
		
		@Override
		public void onResponseReceived(Request request, Response response) {
		      String text = response.getText();
		      GWT.log(text);
		      Document document = XMLParser.parse(text);
		      GWT.log(String.valueOf(document));
		      NodeList list = document.getElementsByTagName(ENTRY);
		      flow.clear();
		      ArrayList<AtomEntry> entries = new ArrayList<>(list.getLength());
		      for(int i = 0; i < list.getLength(); i++) {
		    	  AtomEntry entry = new AtomEntry();
		    	  Node item = list.item(i);
		    	  XMLParser.removeWhitespace(item);
		    	  Node node = item.getFirstChild();
		    	  entry.title = Utils.getXmlNodeValue(node);
		    	  node = node.getNextSibling();
		    	  NamedNodeMap attributes = node.getAttributes();
		    	  entry.url = attributes.getNamedItem("href").getNodeValue();
		    	  entry.type = attributes.getNamedItem("type").getNodeValue();
		    	  String length = attributes.getNamedItem("length").getNodeValue();
		    	  entry.length = Long.parseLong(length);
		    	  node = node.getNextSibling();
		    	  entry.id = Utils.getXmlNodeValue(node);
		    	  GWT.log("found: " + entry.title + " " + entry.url + " " + entry.type + " " + entry.length);
		    	  SafeHtml html = new SafeHtmlBuilder().appendEscaped(entry.title).toSafeHtml();
		    	  Anchor a = new Anchor(html, entry.url);
		    	  a.setTarget("_blank");
		    	  flow.add(a);
		    	  InlineHTML x = new InlineHTML(" <i class=\"fa fa-trash-o\"></i> ");
		    	  x.addClickHandler(ev -> doClick(entry));
		    	  flow.add(x);
		    	  entries.add(entry);
		      }
//		      if (entries.size() > itemsMax) {
//		    	  doClick(entries.get(0));
//		      }
		      
		}

		@Override
		public void onError(Request request, Throwable exception) {
				GWT.log("feedpanel response", exception);
		}
		
	};
	private FlowPanel flow;
	
	private String registration = "123-321-323324";
	private OpdrNavIF comRoot;
	private String uuid;
	private String learnerId;
	private int itemsMax = Short.MAX_VALUE;
	
	void doRequest() {
		try {
			UrlBuilder builder = null;
			builder = Location.createUrlBuilder();
			builder.setPath("/dwo/dav/upload/dir/sec:" + learnerId + "/"  + uuid + "/"+ registration + "/");
			builder.setHash(null);
			RequestBuilder req = new RequestBuilder(RequestBuilder.GET, builder.buildString());			
			req.setIncludeCredentials(true);
			req.setHeader(AUTHORIZATION, comRoot.getContext().getString(AUTHORIZATION));
			req.setCallback(response);
			Request request = req.send();
		} catch (RequestException e) {
			GWT.log("doRequest", e);
		}
	}

	FeedPanel() {
		flow = new FlowPanel(); 
		initWidget(flow);
	}

	public void setComRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
		this.registration = comRoot.getContext().getString("registration");
		this.uuid = comRoot.getUUID();
		this.learnerId = comRoot.getLearnerId();
		doRequest();
	}

	@Override
	public void onResponseReceived(Request request, Response response) {
		int code = response.getStatusCode();
		if (code >= 400) {
			GWT.log("onResponseReceived " + request.toString() + " code " + code);
			return;
		}
		doRequest();		
	}

	@Override
	public void onError(Request request, Throwable exception) {
		GWT.log("onError " + request.toString(), exception);
		if (false) doRequest(); // FIXME 
	}

	public void setItemsMax(int max) {
		itemsMax = max;
	}
	
	
}
