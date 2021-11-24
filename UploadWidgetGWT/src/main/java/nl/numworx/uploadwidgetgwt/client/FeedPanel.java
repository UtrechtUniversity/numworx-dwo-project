package nl.numworx.uploadwidgetgwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

import gwtupload.client.Utils;

public class FeedPanel extends Composite {

	
	static final String ENTRY = "entry";
	private  RequestCallback response = new RequestCallback() {

		@Override
		public void onResponseReceived(Request request, Response response) {
		      String text = response.getText();
		      GWT.log(text);
		      Document document = XMLParser.parse(text);
		      GWT.log(String.valueOf(document));
		      NodeList list = document.getElementsByTagName(ENTRY);
		      flow.clear();
		      for(int i = 0; i < list.getLength(); i++) {
		    	  Node item = list.item(i);
		    	  XMLParser.removeWhitespace(item);
		    	  Node node = item.getFirstChild();
		    	  String title = Utils.getXmlNodeValue(node);
		    	  node = node.getNextSibling();
		    	  NamedNodeMap attributes = node.getAttributes();
		    	  String url = attributes.getNamedItem("href").getNodeValue();
		    	  String type = attributes.getNamedItem("type").getNodeValue();
		    	  String length = attributes.getNamedItem("length").getNodeValue();
		    	  
		    	  GWT.log("found: " + title + " " + url + " " + type + " " + length);
		    	  SafeHtml html = new SafeHtmlBuilder().appendEscaped(title).toSafeHtml();
		    	  InlineLabel space = new InlineLabel(" ");
		    	  Anchor a = new Anchor(html, url);
		    	  a.setTarget("_blank");
		    	  flow.add(a);
		    	  flow.add(space);
		      }
		}

		@Override
		public void onError(Request request, Throwable exception) {
				GWT.log("feedpanel response", exception);
		}
		
	};
	private FlowPanel flow;
	
	void doRequest() {
		RequestBuilder req = new RequestBuilder(RequestBuilder.GET, "/feed.xml");
		req.setCallback(response);
		try {
			Request request = req.send();
		} catch (RequestException e) {
			GWT.log("doRequest", e);
		}
	}

	FeedPanel() {
		flow = new FlowPanel(); 
		initWidget(flow);
		doRequest();
	}
	
	
}
