package nl.numworx.notebookgwt.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class NotebookGWT implements EntryPoint, InteractionStub, RequestCallback {

	final Frame frame;
	OpdrNavIF root;
	private int height;
	private int width;
	private boolean volledigeBreedte;

	public void onModuleLoad() {
		RootLayoutPanel.get().add(this);
		Stub.publish(this);
  }

	public NotebookGWT() {
		frame = new Frame("about:blank");
	};
	
	public NotebookGWT(HashMap<String, Object> h, HashMap<String, Number> randomVarWaarden, int volleBreedte) {
		this();
		ObjectMap map = JSONUtilities.wrapMap(h);
		if(map != null)
		{
				width = map.getInt("breedte");
				height = map.getInt("hoogte");
				volledigeBreedte = map.getBoolean("volledigeBreedte", false);
		}
		
		if(volledigeBreedte)
			width = volleBreedte;
		Map<String,Object> launchState = Collections.emptyMap();
		if (h != null && h.get("interactiePanelLaunchState") != null)
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
				
		frame.setStylePrimaryName("StubView");
		frame.addStyleDependentName("borderless");

		
		
		//alle gegevens uit launchData halen: 
		init(width, height, launchState, randomVarWaarden);

	}
	
	
	
	@Override
	public HashMap<String, Object> getState() {
		return new HashMap<>();
	}

	@Override
	public void setState(HashMap<String, Object> h) {
	}

	@Override
	public int getScore() {
		return 0;
	}

	@Override
	public int[][] getScoreObjectives() {
		return null;
	}

	@Override
	public Boolean isCorrect() {
		return null;
	}

	@Override
	public void kijkNa() {
	}

	@Override
	public void zetNagekeken(boolean b) {
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		root = comRoot;	
		String Authorization = root.getContext().getString("Authorization");
		GWT.log(Authorization);
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "/dwo/oauth2/dwo-redirect");
		builder.setHeader("Authorization", Authorization);
		RequestCallback callback = this;
		builder.setCallback(callback);
		try {
			builder.send();
		} catch (RequestException e) {
			GWT.log("failure", e);
		}
	}

	@Override
	public void zetVolledigeBreedte(int breedte) {
		if (volledigeBreedte) 
		{
			this.width = breedte;
			frame.setPixelSize(breedte, -1);
		}
	}

	@Override
	public Widget asWidget() {
		return frame;
	}

	@Override
	public int getAsHoogte() {
		return 0;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
	}

	@Override
	public void init(int width, int height, Map<String, Object> launchData, Map<String, Number> values) {
		this.width = width;
		this.height = height;
		frame.setPixelSize(width, height);
	}

	@Override
	public void onResponseReceived(Request request, Response response) {
		int code = response.getStatusCode();
		if (code == 204)
			frame.setUrl("https://hub-dev.dwo.nl/");		
	}

	@Override
	public void onError(Request request, Throwable exception) {
		frame.setUrl("/notfound.html");	
	}

	@Override
	public int getConstantHeight() {
		return height;
	}
	
	
}