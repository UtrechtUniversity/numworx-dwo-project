package nl.uu.fi.dwo.lms.gwtclient.gwt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.LayoutPanel;

import fi.dwo.gwt.lib.rest.util.DomStudentModelStructureCodec;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.StudentModelGraph;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;

public class StudentModelController implements ClickHandler {
	
	String lang = "nl";

	@Inject StudentModelController() { }
	@Inject StudentModelGraph graph;
	
	private DomStudentModelStructure mock() {
		DomStudentModelStructure result = new DomStudentModelStructure();
		result.setActiveMethod(null);
		DomStudentModelContextInfo info = new DomStudentModelContextInfo(Collections.singletonMap(lang, "titel1"), Collections.singletonMap(lang, ""));
		result.setInfo(info);
		DomStudentModelCategory category = new DomStudentModelCategory();
		category.setInfo(info);
		result.setCategories(Collections.singletonList(category));
		
		DomStudentModelObj obj1, obj2;
		obj1 = new DomStudentModelObj();
		obj2 = new DomStudentModelObj();
		category.setObjectives(Arrays.asList(obj1, obj2));
		info = new DomStudentModelContextInfo(Collections.singletonMap(lang, "obj1"), Collections.singletonMap(lang, ""));
		info.setId("obj1");
		info.setX(10);info.setY(20);
		info.setMethods(Collections.emptyMap());
		obj1.setInfo(info);
		
		info = new DomStudentModelContextInfo(Collections.singletonMap(lang, "obj2"), Collections.singletonMap(lang, ""));
		info.setId("obj2");
		info.setVoorkennis(Collections.singletonList("obj1"));
		info.setX(100); info.setY(100);
		info.setMethods(Collections.emptyMap());
		obj2.setInfo(info);
		return result;
	}
	
	private native String GetValue0(String name) /*-{
		return $wnd.doGetValue(name)
	}-*/;
	
	public String GetValue(String name) {
	  try {
	    return GetValue0(name);
	  } catch(Exception e) {
	    return "";
	  }
	}
	
	
	private native String SetValue0(String name, String value) /*-{
		return $wnd.doSetValue(name, value)
	}-*/;
	
	public String SetValue(String name, String value) {
	  try { 
	    return SetValue0(name, value);
	  } catch(Exception e) {
	    return "false";
	  }
	}

	public void go(LayoutPanel root) {
		
		root.add(graph);
		root.setWidgetTopBottom(graph, 0, Unit.PX, 1, Unit.EM);
		Button b = new Button("doorgaan");
		b.addClickHandler(this);
		b.addStyleName("doorgaan");
		root.add(b);
		root.setWidgetBottomHeight(b, 0, Unit.PX, 1, Unit.EM);
		
		DomStudentModelContext4Student item = new DomStudentModelContext4Student();
		String sm = GetValue("dme.studentmodelstructure");
		DomStudentModelStructure modelStructure = DomStudentModelStructureCodec.CODEC.decode(sm);
		item.setModelStructure(modelStructure);

		DomMethod method = new DomMethod();
		method.books = Collections.emptyList();
		method.chapters = Collections.emptyList();
		method.edges = Collections.emptyList();
		method.standard = true;
		method.method = "Geen methode";
		
		graph.setModel(item, method);
		JSONArray value = JSONParser.parseStrict(GetValue("dme.studentmodelitems")).isArray();
		List<String> ids = new ArrayList<>(value.size());
		for(int i = 0; i < value.size(); i++) ids.add(value.get(i).isString().stringValue());

		List<String> set = Collections.emptyList();
		try {
			value = JSONParser.parseStrict(GetValue("dme.studentmodelset")).isArray();
			set = new ArrayList<>(value.size());
			for(int i = 0; i < value.size(); i++) set.add(value.get(i).isString().stringValue());
		} catch(Exception e) { } 
		
		graph.setGoals(ids, set);
	}

	@Override
	public void onClick(ClickEvent event) {
		List<String> set = graph.getGoals();
		JSONArray array = new JSONArray();
		for(int i = 0; i < set.size(); i++) {
			array.set(i, new JSONString(set.get(i)));
		}
		SetValue("dme.studentmodelset", array.toString()); 
	}
}
