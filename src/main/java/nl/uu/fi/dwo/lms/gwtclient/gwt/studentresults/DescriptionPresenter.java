package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.Collections;
import java.util.Optional;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent.SelectedView;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;

public class DescriptionPresenter {
	private static final Logger LOG = Logger.getLogger(DescriptionPresenter.class.getName());

	private Optional<EventBus> bus;
	private String test = "app";
	
	@Inject DescriptionPresenter(Optional<EventBus> bus, @Named("test") boolean test) { 
		this.bus = bus;
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

		@Inject DescriptionService service;
		DomStudentModelContextId current;

		public Promise<Widget> get(DomStudentModelContextId current, DomStudentModelContextInfo info) {
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



}
