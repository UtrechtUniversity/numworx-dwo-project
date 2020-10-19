package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import dagger.Lazy;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results.JsStudentResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.AbstractResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;

public class StudentResultsPresenter extends AbstractResultsPresenter implements SelectionHandler<TreeItem> {

	private static final Logger LOG = Logger.getLogger(StudentResultsPresenter.class.getName());

	public interface Display extends BasicDisplay {
		String getId();
	}
	
	private final LoggingFailure FAILURE;
	private Display view;
	private RootPanel root;
	private String lang;
	
	@Inject Lazy<StudentResultsWidget> widget;
	@Inject StudentResults service;
	private HandlerRegistration ref;
	
	@Inject StudentResultsPresenter(EventBus bus, DwoGlobalVars vars) {
		super(bus, vars);
		FAILURE = new LoggingFailure(LOG, bus);
		lang = LocaleInfo.getCurrentLocale().getLocaleName();
	}
	
	@Inject void setView(JsStudentResultsView view) {
		this.view = view;
		attachWidget(view);
	}

	private void attachWidget(Display view) {
		this.view = view;
		root = RootPanel.get(view.getId());
	}
	
	public void init() {
		if (ref != null) {ref.removeHandler(); ref = null;}
		root.clear();
		service.clear();
		view.setHelp(dwoGlobalVars.buildHelpUrl("#studentresults"));
		service.getModels().then(this::getModels, FAILURE);
	}
	
	Promise<?> getModels(Promise<List<DomStudentModelContext>> p) {
		StudentResultsWidget w = widget.get();
		List<DomStudentModelContext> list = p.getValue();
		Tree tree = w.tree;
		tree.removeItems();
		for (DomStudentModelContext item : list) {
			DomStudentModelStructure structure = item.getModelStructure();
			String title = structure.getInfo().getTitle().getOrDefault(lang, "");
			int perc = 50;
			SafeHtml html = Util.treeItem(title, perc,0);
            TreeItem ti = tree.addItem(html);
			ti.setUserObject(item);
			service.getScore(item).then(s -> {
              DomStudentModelStructureScore score = s.getValue().getDomStudentModelStructureScore();
              int percentage = Math.round(percentage(score));
              ti.setHTML(Util.treeItem(title, percentage,1));
			  return s;
			});
		}
		ref = tree.addSelectionHandler(this);
		
		root.add(w);
		return null;
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		TreeItem item = event.getSelectedItem();
		LOG.info("selected " + item);
		Object userObject = item.getUserObject();
		if (userObject instanceof DomStudentModelContext) {
			DomStudentModelContext model = (DomStudentModelContext) userObject;
			DomStudentModelStructure structure = model.getModelStructure();
			String text = structure.getInfo().getDescription().get(lang);
			String json = structure.getInfo().getDescription().get(lang +"@JSON");
			Widget description = createDescription(text, json);
			widget.get().description.setWidget(description);
			text = structure.getInfo().getTitle().get(lang);
			widget.get().title.setText(text);
			service.getScore(model).then ( p -> {
				DomStudentModelStructureScore score = p.getValue().getDomStudentModelStructureScore();
				setPerc(score);
				return p;
			}, FAILURE)
			.then(p -> { 
				if (item.getChildCount() != structure.getCategories().size()) {
					item.removeItems();
					int cat = 0;
					for (DomStudentModelCategory o : structure.getCategories()) {
		                DomStudentModelCategoryScore score = p.getValue().getDomStudentModelStructureScore().getCategories().get(cat);
						TreeItem tt = item.addItem(
						  Util.treeItem(o.getInfo().getTitle().getOrDefault(lang, ""), (percentage(score)),2));
						tt.setUserObject(cat);
						cat++;
					}
				}
				return p; })
			.then(null, FAILURE);
			
		} else if (userObject instanceof Integer) {
			DomStudentModelContext model = (DomStudentModelContext) item.getParentItem().getUserObject();
			DomStudentModelStructure structure = model.getModelStructure();
			DomStudentModelCategory o = structure.getCategories().get(((Integer) userObject).intValue());
			String text = o.getInfo().getDescription().get(lang);
			String json = o.getInfo().getDescription().get(lang + "@JSON");
			Widget description = createDescription(text, json);
			widget.get().description.setWidget(description);
            text = o.getInfo().getTitle().get(lang);
            widget.get().title.setText(text);
			service.getScore(model).then(p -> { 
				DomStudentModelCategoryScore score = p.getValue().getDomStudentModelStructureScore().getCategories().get(((Integer) userObject).intValue());
				setPerc(score);
				return p; }, FAILURE)
			.then(p -> {
                DomStudentModelCategoryScore score = p.getValue().getDomStudentModelStructureScore().getCategories().get(((Integer) userObject).intValue());
				if (item.getChildCount() != o.getObjectives().size()) {
					item.removeItems();
					int cat = ((Integer) userObject).intValue();
					int obj = 0;
					for( DomStudentModelObj oo : o.getObjectives()) {
					    float ppp;
					    DomStudentModelObjectiveScore s = score.getObjectives().get(obj);
					    ppp = (percentage(s));
						TreeItem tt = item.addItem(Util.treeItem(oo.getInfo().getTitle().getOrDefault(lang, ""), ppp,3));
						tt.setUserObject(new int[] { cat, obj } );
						obj++;
					}
				}
				return p; },  p -> item.removeItems() );			
		} else if (userObject instanceof int[]) {
			int[] elems = (int[]) userObject;
			int cat = elems[0], obj = elems[1];
			TreeItem top = item.getParentItem().getParentItem();
			DomStudentModelContext model = (DomStudentModelContext) top.getUserObject();
			DomStudentModelStructure structure = model.getModelStructure();
			DomStudentModelCategory o = structure.getCategories().get(cat);
			DomStudentModelObj oo = o.getObjectives().get(obj);
			String text = oo.getInfo().getDescription().get(lang);
			String json = oo.getInfo().getDescription().get(lang + "@JSON");
	        widget.get().description.setWidget(createDescription(text,json));
	        text = oo.getInfo().getTitle().get(lang);
            widget.get().title.setText(text);
			service.getScore(model).then( p -> { 
				DomStudentModelObjectiveScore score = p.getValue().getDomStudentModelStructureScore().getCategories().get(cat).getObjectives().get(obj);
				setPerc(score);
				return p; }, FAILURE);
		}
		
	}

	private void setPerc(DomStudentModelScore score) {
		float perc = percentage(score);
		widget.get().setPerc(perc);
	}
float percentage(DomStudentModelScore score) {
  float perc;
  		if (score.getCount() == 0) perc = 50;
  		else perc = (float)(score.getScore()*100/score.getCount());
  return perc;
}
  private static final String WISKOPDR_SIG = "H4sIAAAAAA";
  private String launch_data;
    
  
	private Widget createDescription(String text, String json) {
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
	          + "&t=" + random + "#cmi.launch_data:0";
	      LOG.info("openUrl " + url);
	      wiskopdr = new Frame(url);
	      wiskopdr.setStylePrimaryName("score-frame");
	      launch_data = json;
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

	  private native static void setAPI(StudentResultsPresenter view) /*-{
      var api = {
          "LMSGetValue" : function(key) {
              return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsPresenter::getValue(Ljava/lang/String;)(key)
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
              return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsPresenter::getValue(Ljava/lang/String;)(key)
          },
          "SetValue" : function(key, value) {
              return "true"
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
              return "true"
          },
      // TODO more to follow...           
      };
      $wnd.API = api;
      $wnd.API_1484_11 = api;
}-*/;

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

}
