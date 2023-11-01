package nl.numworx.uploadwidgetgwt.client;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

import gwtupload.client.Utils;
import nl.numworx.uploadwidget.shared.AtomEntry;
import nl.numworx.uploadwidgetgwt.shared.Constants;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

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
		      addFileInputModel();
		      
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
		    	  flow.add(BR());
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

	private ObjectList fileInputModel = JSONUtilities.wrapList(Collections.emptyList());
		
	FeedPanel() {
		flow = new FlowPanel(); 
		initWidget(flow);
	}

	public void setComRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
		this.registration = comRoot.getContext().getString("registration");
		this.uuid = comRoot.getUUID();
		this.learnerId = comRoot.getLearnerId();
		addFileInputModel();
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

	public ObjectList getFileInputModel() {
		return fileInputModel;
	}

	public void setFileInputModel(ObjectList fileInputModel) {
		this.fileInputModel = fileInputModel;
	}

	private void addFileInputModel() {
		int modelSize = fileInputModel.size();
		String tail = uuid + "/instance/";
		String head = "/dwo/dav/upload/dir/sec:" + learnerId + "/";
		String base = head + tail;

		  for(int i=0; i < modelSize; i++) {
			  ObjectMap file = fileInputModel.getObjectMap(i);
			  String name = file.getString("name");
			  String url = base + name; // FIXME gebruik UrlBuilder o.i.d.
			  if (file.containsKey("url"))
			  {
				  url = head + URL.decodePathSegment(file.getString("url"));
			  }
			  UrlBuilder builder = Window.Location.createUrlBuilder();
			  Set<String> p = Window.Location.getParameterMap().keySet();
			  builder.setPath(url);
			  builder.setHash(null);
			  p.forEach(key -> builder.removeParameter(key));
			  url = builder.buildString();
			  SafeHtml html = new SafeHtmlBuilder().appendEscaped(name).toSafeHtml();
			  Anchor a = new Anchor(html, url);
			  a.setTarget("_blank");
			  flow.add(a);
			  flow.add(BR());

		  }
	}

	private Widget BR() {
		return new HTMLPanel("br", "");
	}
	
	
}
