package nl.numworx.notebookgwt.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;
import org.fusesource.restygwt.client.dispatcher.DispatcherFilter;
import org.osgi.util.promise.Deferred;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import nl.numworx.notebook.common.HubInitializer;
import nl.numworx.notebook.common.Resource;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class NotebookGWT implements EntryPoint, InteractionStub, DispatcherFilter, MethodCallback<String>, LoadHandler {

	static final String SCORE_MAX = "scoreMax";
	static final String CHECK_DOCENT = "checkDocent";
	static final String UPLOAD = "upload";
	static final String PROJECT = "project";
	static final String NOTEBOOK = "notebook";
	
	static final NotebookService service = GWT.create(NotebookService.class);

	
	final Frame frame;
	OpdrNavIF root;
	private int height;
	private int width;
	private boolean volledigeBreedte;
	private String project;
	private String notebook;
	private int scoreMax;
	private ObjectList upload;
	
	private HubInitializer initializer;

	public void onModuleLoad() {
		Defaults.setServiceRoot("/dwo/notebook/");
	    Defaults.setDispatcher(DefaultFilterawareDispatcher.singleton());
    	DefaultFilterawareDispatcher.singleton().addFilter(this);
		RootLayoutPanel.get().add(this);
		Stub.publish(this);
  }

	public NotebookGWT() {
		frame = new Frame("https://hub-dev.dwo.nl/hub/logout"); // uitvogelen met deploy.jsp
		frame.addLoadHandler(this);
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
		if (scoreMax > 0) return null; // docent kijkt na!
		return Boolean.TRUE;
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
		initializer.mode = root.getLessonMode();
		service.create(initializer, root.getLearnerId(), this);
	}

//	void startNotebook() {
//		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "/dwo/oauth2/dwo-redirect");
//		Method get = null;
//		filter(get , builder);
//		RequestCallback callback = this;
//		builder.setCallback(callback);
//		try {
//			builder.send();
//		} catch (RequestException e) {
//			GWT.log("failure", e);
//		}
//	}

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
		ObjectMap map = JSONUtilities.wrapMap(launchData);
		project = map.getString(PROJECT);
		notebook = map.getString(NOTEBOOK);
		if (map.containsKey(SCORE_MAX)) {
			scoreMax = map.getInt(SCORE_MAX);
		}
		if (map.containsKey(UPLOAD))
			upload = map.getObjectList(UPLOAD);
		
		initializer = new HubInitializer();
		initializer.project = project;
		initializer.notebook = notebook;
		if (upload != null) {
			List<Resource> resources = new ArrayList<>(upload.size());
			for (int i = 0; i < upload.size(); i++) {
				ObjectMap m = upload.getObjectMap(i);
				String name = m.getString("name");
				String type = m.getString("type");
				String content = m.getString("content");
				resources.add(new Resource(name, type, content));
			}
			initializer.resources = resources;
		}
	}

//	@Override
//	public void onResponseReceived(Request request, Response response) {
//		int code = response.getStatusCode();
//		if (code == 204) {
//			String hub = serverUrl;
//			String tail = "";
//			if (notebook != null) {
//				tail = notebook;
//				if (project != null) {
//					tail = project + "/" + notebook;
//				}
//				while(tail.startsWith("/")) tail = tail.substring(1);
//				String user = URL.decodePathSegment(root.getLearnerName());
//				hub  += "user/" + user + "/";
//				if (root.getLessonMode() == LessonMode.browse)
//					hub += "nbconvert/html/";
//				else
//					hub  += "notebooks/"; 
//				hub += URL.decodePathSegment(tail);	
//			} else if (project != null) {
//				while(project.startsWith("/")) project = project.substring(1);
//				String user = URL.decodePathSegment(root.getLearnerName());
//				hub  += "user/" + user + "/";
//				hub  += "lab/tree/" + URL.decodePathSegment(project);
//			}
//			frame.setUrl(hub);
//		}		
//	}

	@Override
	public int getConstantHeight() {
		return height;
	}

	@Override
	public boolean filter(Method method, RequestBuilder builder) {
		builder.setHeader("Authorization", root.getContext().getString("Authorization"));
		return true;
	}

	@Override
	public void onFailure(Method method, Throwable exception) {
		startNotebook("notfound.html");
	}

	@Override
	public void onSuccess(Method method, String response) {
		startNotebook(response);		
	}

	private void startNotebook(String response) {
		onLoad.getPromise().onResolve(
				() ->
				frame.setUrl(response)
		);		
	}

	private Deferred<LoadEvent> onLoad = new Deferred<>();
	@Override
	public void onLoad(LoadEvent event) {
		onLoad.resolve(event);
	}
	
	
}