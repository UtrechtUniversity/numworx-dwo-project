package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent.SelectedView;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelMethodInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelVariant;

public class DescriptionPresenter {
	private static final Logger LOG = Logger.getLogger(DescriptionPresenter.class.getName());

	private Optional<EventBus> bus;
	private String test = "app";
	
	@Inject public DescriptionPresenter(Optional<EventBus> bus, @Named("test") boolean test, DescriptionService service) { 
		this.bus = bus;
		this.service = service;
		if (test) this.test = "test";
	}
	
	  private native static void setAPI(DescriptionPresenter view) /*-{
      var api = {
          "LMSGetValue" : function(key) {
              return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionPresenter::getValue(Ljava/lang/String;)(key)
          },
          "LMSInitialize" : function(dummy) {
              return "true"
          },
          "LMSGetLastError" : function() {
              return "0"
          },
          "LMSGetDiagnostic" : function(dummy) {
              return ""
          },
          "LMSGetErrorString" : function(code) {
              return ""
          },
          "LMSCommit" : function(dummy) {
              return "true"
          },
          "LMSFinish" : function(dummy) {
              return "true"
          },
          "LMSSetValue" : function(key, value) {
              return "true"
          },
          "Initialize" : function(dummy) {
              return "true"
          },
          "GetValue" : function(key) {
              return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionPresenter::getValue(Ljava/lang/String;)(key)
          },
          "SetValue" : function(key, value) {
              return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionPresenter::setValue(Ljava/lang/String;Ljava/lang/String;)(key, value)
          },
          "GetLastError" : function() {
              return "0"
          },
          "GetDiagnostic" : function(dummy) {
              return ""
          },
          "GetErrorString" : function(code) {
              return ""
          },
          "Commit" : function(dummy) {
              return "true"
          },
          "Terminate" : function(dummy) {
              return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionPresenter::terminate(Ljava/lang/String;)(dummy)
          },
      // TODO more to follow...           
      };
      $wnd.API = api;
      $wnd.API_1484_11 = api;
	}-*/;

	  static final String WISKOPDR_SIG = "H4sIAAAAAA";
	  static final String GOTO_URL = "dme.goto_url";
	  private String launch_data;
	  private SwitchViewEvent event;

		private String getValue(String key) {
			  LOG.info("GetValue " + key);
			  String value = null;
			  if ("cmi.launch_data".equals(key)) value = launch_data;
			  else if ("cmi.mode".equals(key)) value="browse";
			  if(value == null) value = "";
			  String shortValue = value.length() > 10 ? value.substring(0, 10) + "..." : value;
			  LOG.info("result GetValue: " + shortValue);
			  return value;
			}

		private String setValue(String key, String value) {
			LOG.info("SetValue "+ key);
			if (GOTO_URL.equals(key) && value.startsWith("#") && bus.isPresent()) {
				event = new SwitchViewEvent(SelectedView.GOTO_URL, Collections.singletonMap("message", "GOTO:" + value.substring(1)));
			}
			return "true";
		}
		
		private String terminate(String dummy) {
			if (event != null) {
				SwitchViewEvent e = event; event = null;
				bus.get().fireEvent(e);
			}
			return "true";
		}
		
		
		Widget createDescription(String text, String json) {
		    if (text != null && text.startsWith(WISKOPDR_SIG))
		    {
		      Frame wiskopdr;
		      String random = String.valueOf(System.currentTimeMillis());
		      LOG.info("Frame = "+random);
		      String locale = LocaleInfo.getCurrentLocale().getLocaleName();
		      if ("default".equals(locale) ) locale =  "nl";
		      String profile = Location.getParameter("profile");
		      if(profile == null || profile.isEmpty()) profile = "77";

		      String url = "/dwo/apps/player.html?footer=none&locale="
		          + locale
		          + "&profile="
		          + profile
		          + "&env="
		          + test
		          + "&t=" + random + "#cmi.launch_data:0";
		      LOG.info("openUrl " + url);
		      wiskopdr = new Frame(url);
		      wiskopdr.setStylePrimaryName("score-frame");
		      launch_data = json;
		      event = null;
		      setAPI(this);
		      return wiskopdr;
		    }
		  
		  
			Widget description;
			SafeHtmlBuilder builder = new SafeHtmlBuilder();
			if (text == null) text = "";
			builder.appendEscapedLines(text);
			description = new HTML(builder.toSafeHtml());
			description.setStylePrimaryName("score-html");
			return description;
		}

		private final DescriptionService service;
		//DomStudentModelContextId current;

		public Promise<Widget> get(DomStudentModelContext current, DomStudentModelContextInfo info, String pid) {
			if (pid != null) {
				info = findInfo(current, info);
				List<DomStudentModelMethodInfo> methods = info.getMethodInfo();
				if (methods != null) {
					for(DomStudentModelMethodInfo x:methods) {
						if (pid.equals(x.key()))
							return get(current, info, x);
					}
				}
			}
			return service.getDescription(current, info)
			   .then(p -> {
				String text = p.getValue();
				String json = null;
				if (text.startsWith("{")) {
					json = text;
					text = DescriptionPresenter.WISKOPDR_SIG;
				}
				Widget description = this.createDescription(text, json);
				return Promises.resolved(description);
			});
			
		}
		
		private DomStudentModelStructure structurecache;
		private Map<String, DomStudentModelContextInfo> infocache = new HashMap<>();
		
		private DomStudentModelContextInfo findInfo(DomStudentModelContext current, DomStudentModelContextInfo info) {
			if (info.getTitle() != null) return info;
			String id = info.getId();
			DomStudentModelContextInfo found = null;
			DomStudentModelStructure structure = current.getModelStructure();
			if (structure == structurecache)
			{	found = infocache.get(id);
				if (found != null) return found;
			} else {
				structurecache = structure;
				infocache.clear();
			}
			if (id.equals(structure.getInfo().getId())) return structure.getInfo();
			List<DomStudentModelCategory> children = structure.getCategories();
			for(DomStudentModelCategory item : children) {
				found = findInfo(item, id);
				if (found != null) {
					return found;
				}
			}
			return info;
		}

		private DomStudentModelContextInfo findInfo(DomStudentModelCategory item, String id) {
			DomStudentModelContextInfo info = item.getInfo();
			infocache.put(info.getId(), info);
			if (id.equals(info.getId())) return info;
			List<DomStudentModelObj> children = item.getObjectives();
			for(DomStudentModelObj obj : children) {
				DomStudentModelContextInfo found = findInfo(obj, id);
				if (found != null) {
					return found;
				}
			}			
			return null;
		}

		private DomStudentModelContextInfo findInfo(DomStudentModelObj item, String id) {
			DomStudentModelContextInfo info = item.getInfo();
			infocache.put(info.getId(), info);
			if (id.equals(info.getId())) return info;
			List<DomStudentModelObj> children = item.getObjectives();
			if (children != null)
			for(DomStudentModelObj obj : children) {
				DomStudentModelContextInfo found = findInfo(obj, id);
				if (found != null) {
					return found;
				}
			}			
			return null;
		}

		public Promise<Widget> get(DomStudentModelContextId current, DomStudentModelContextInfo info, DomStudentModelMethodInfo method) {
			return service.getDescription(current, info)
			   .then(p -> {
				String text = p.getValue();
				String json = null;
				if (text.startsWith("{")) {
					json = text;
					text = DescriptionPresenter.WISKOPDR_SIG;
				}
				json = selectVariant(json, info, method);
				Widget description = this.createDescription(text, json);
				return Promises.resolved(description);
			});
			
		}

		protected String selectVariant(String json, DomStudentModelContextInfo info, DomStudentModelMethodInfo method) {
			if (json != null && info != null && info.getVariants() != null && method != null) {
				String string = method.getVariant();
				Optional<DomStudentModelVariant> opt = info.getVariants().stream().filter(t -> Objects.equals(t.getName(),string)).findAny();
				if (opt.isPresent()) {
					Map<String, Boolean> layers = opt.get().getLayers();
					JSONValue v = JSONParser.parseStrict(json);
					JSONObject instellingen = v.isObject().get("instellingen").isObject();
					JSONBoolean hasLayers = instellingen.get("hasLayers").isBoolean();
					if (hasLayers.booleanValue()) {
						JSONArray names = instellingen.get("layerNames").isArray();
						JSONArray values = instellingen.get("layerVisible").isArray();
						for(int i = 0; i < names.size(); i++) {
							String n = names.get(i).isString().stringValue();
							Boolean visible = layers.getOrDefault(n, Boolean.FALSE);
							values.set(i, JSONBoolean.getInstance(visible));
						}
						json = v.toString();
					}
				}
			}
			return json;
		}



}
