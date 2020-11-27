package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
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
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.HandlerRegistrations;

import dagger.Lazy;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.LoggingFailure;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results.JsStudentResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.AbstractResultsPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
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
	@Inject Lazy<StudentResultsGraph> graph;
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
		showGraph = false;
		service.getModels().then(this::getModels, FAILURE);
	}
	
	private static final DomStudentModelScore NULLSCORE = new DomStudentModelScore();
	{
		NULLSCORE.setScore(0, 0, 0, 0);
	}
	class ModelChange implements ChangeHandler, ClickHandler {
		
		List<DomStudentModelContext> list;
		DomStudentModelContext current;
		
		@Override
		public void onChange(ChangeEvent event) {
			int selection = widget.get().models.getSelectedIndex();
			LOG.info("selection = " + selection);
			if(selection == 0) {
				widget.get().tree.removeItems();
				widget.get().title.setText("");
				widget.get().description.clear();
				widget.get().setPerc(NULLSCORE);
				current = null;
				return;
			}
			DomStudentModelContext item = list.get(selection-1);
			service.getModel(item).then(p -> {
				current = p.getValue();
				insertTree(item);
				return p;
			}, FAILURE);
		}

		private ModelChange(List<DomStudentModelContext> list) {
			this.list = list;
		}

		
		private void insertTree(DomStudentModelContext item) {
			Tree tree = widget.get().tree;
			tree.removeItems();
			DomStudentModelStructure structure = item.getModelStructure();
			String title = structure.getInfo().getTitle().getOrDefault(lang, "");
			SafeHtml html = Util.treeItem(title, NULLSCORE ,0);
            TreeItem ti = tree.addItem(html);
			ti.setUserObject(item);
			service.getScore(item).then(s -> {
              DomStudentModelStructureScore score = s.getValue().getDomStudentModelStructureScore();
              ti.setHTML(Util.treeItem(title, score ,0));
              ti.setSelected(true);
              addToTree(ti, item);
			  return s;
			});

		}

		@Override
		public void onClick(ClickEvent event) {
			if (current != null) showHideGraph(current);			
		}
		
	}
	
	boolean showGraph;
	void showHideGraph(DomStudentModelContext item) {
		showGraph = !showGraph;
		if(showGraph) {
			widget.get().description.setWidget(graph.get());
			graph.get().setModelScore(item, service.getScore(item));
		} else {
			setDescription(item.getModelStructure().getInfo());
		}
	}
	
	Promise<?> getModels(Promise<List<DomStudentModelContext>> p) {
		StudentResultsWidget w = widget.get();
		List<DomStudentModelContext> list = p.getValue();
		ModelChange changes = new ModelChange(list);
		Tree tree = w.tree;
		w.description.clear();
		w.title.setText("");
		tree.removeItems();
		String first = w.models.getItemText(0);
		w.models.clear();
		w.models.addItem(first);
		for (DomStudentModelContext item : list) {
			DomStudentModelStructure structure = item.getModelStructure();
			String title = structure.getInfo().getTitle().getOrDefault(lang, "");
			w.models.addItem(title);
		}

		ref = HandlerRegistrations.compose(
				eventBus.addHandlerToSource(ChangeEvent.getType(), w, changes),
				eventBus.addHandlerToSource(ClickEvent.getType(), w, changes),				
				tree.addSelectionHandler(this)
		);
		
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
			addToTree(item, model);
			
		} else if (userObject instanceof Integer) {
			DomStudentModelContext model = (DomStudentModelContext) item.getParentItem().getUserObject();
			DomStudentModelStructure structure = model.getModelStructure();
			DomStudentModelCategory o = structure.getCategories().get(((Integer) userObject).intValue());
			setDescription(o.getInfo());
            String text = o.getInfo().getTitle().get(lang);
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
						TreeItem tt = item.addItem(Util.treeItem(oo.getInfo().getTitle().getOrDefault(lang, ""), s,3));
						tt.setUserObject(new int[] { cat, obj } );
						obj++;
					}
				}
				return p; },  p -> item.removeItems() );			
		} else if (userObject instanceof int[]) {
			int[] elems = (int[]) userObject;
			int cat = elems[0], obj = elems[1];
			TreeItem top = item;
			for (int i = 0; i < elems.length; i++ ) top = top.getParentItem();
			DomStudentModelContext model = (DomStudentModelContext) top.getUserObject();
			DomStudentModelStructure structure = model.getModelStructure();
			DomStudentModelCategory o = structure.getCategories().get(cat);

			DomStudentModelObj o0 = o.getObjectives().get(obj);
			for (int i = 2; i < elems.length; i++ ) {
				o0 = o0.getObjectives().get(elems[i]);
			}
			final DomStudentModelObj oo = o0;
			String text;
			setDescription(oo.getInfo());
	        text = oo.getInfo().getTitle().get(lang);
            widget.get().title.setText(text);

            service.getScore(model).then( p -> { 
				DomStudentModelObjectiveScore score = p.getValue().getDomStudentModelStructureScore().getCategories().get(cat).getObjectives().get(obj);
				for (int i = 2; i < elems.length; i++ ) score = score.getChildren().get(elems[i]);
				setPerc(score);
				return p; }, FAILURE)
			.then (p -> {
				if (oo.getObjectives() != null && oo.getObjectives().size() != item.getChildCount()) {
					DomStudentModelObjectiveScore score = p.getValue().getDomStudentModelStructureScore().getCategories().get(cat).getObjectives().get(obj);
					for (int i = 2; i < elems.length; i++ ) score = score.getChildren().get(elems[i]);
					int oobj = 0;
					for (DomStudentModelObj ooo: oo.getObjectives()) {
						DomStudentModelObjectiveScore s = score.getChildren().get(oobj);
						TreeItem tt = item.addItem(Util.treeItem(ooo.getInfo().getTitle().getOrDefault(lang, ""), s, 4));
						int[] oelems = new int[elems.length+1];
						System.arraycopy(elems, 0, oelems, 0, elems.length);
						oelems[elems.length] = oobj;
						tt.setUserObject(oelems);
					}
				}
				return p;
			}, p-> item.removeItems());
		}
		
	}

	private void addToTree(TreeItem item, DomStudentModelContext model) {
		DomStudentModelStructure structure = model.getModelStructure();
		String text;
		setDescription(structure.getInfo());
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
					  Util.treeItem(o.getInfo().getTitle().getOrDefault(lang, ""), (score),2));
					tt.setUserObject(cat);
					cat++;
				}
			}
			return p; })
		.then(null, FAILURE);
	}

	private void setDescription(DomStudentModelContextInfo info) {
		showGraph = false;
		String text = info.getDescription().get(lang);
		String json = info.getDescription().get(lang +"@JSON");
		Widget description = createDescription(text, json);
		widget.get().description.setWidget(description);
	}

	private void setPerc(DomStudentModelScore<?> score) {
		widget.get().setPerc(score);
		
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
