package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.studentmodel;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.fusesource.restygwt.client.JsonEncoderDecoder;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.web.bindery.event.shared.EventBus;

import nl.uu.fi.dwo.lms.gwtclient.gwt.BootPanelController;
import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.schoolclasses.JsModulesOfSchoolclassDisplay;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClassCodec;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.SMClassResultsPresenter;
import nl.uu.fi.dwo.rest.dom.DomTree;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataStudentScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

/**
 * Mapper to allow java interface implementation.
 * 
 * @author Wim van Velthoven
 */
@Singleton
public class JsTeacherSMClassResultsView extends AbstractStudentModelView implements SMClassResultsPresenter.Display {

	@Override
    public void clear() {
		super.clear();
		JsTeacherSMClassResultsDisplay.clear();
    }

    @Override
    public void setHelp(String url) {
    	JsTeacherSMClassResultsDisplay.setHelp(url);
    }
    
    @Override
    public void init() {
    	JsTeacherSMClassResultsDisplay.init();
    }

    @Override
    public void showSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses) {
      JSONObject json = new JSONObject();
      schoolClasses.forEach((k,v) -> {json.put(k, TaggedDomSchoolClassCodec.CODEC.encode(v));});        
      JsTeacherSMClassResultsDisplay.showSchoolclasses(json.getJavaScriptObject());
    }

    @Inject JsTeacherSMClassResultsView(EventBus bus) {
		super(JsTeacherSMClassResultsDisplay.getTreeId(),bus);
    }
		
	@Override
	public void setTitle(String title) {
		JsTeacherSMClassResultsDisplay.setTitle(title);
	}

	public interface DomStudentModelScoreCodec extends JsonEncoderDecoder<DomStudentModelObjectiveScore> {
	}
	DomStudentModelScoreCodec CODEC = GWT.create(DomStudentModelScoreCodec.class);
	
	private JSONObject enc(DomStudentModelObjectiveScore s) {
		return CODEC.encode(s).isObject();
	}
	
	@Override
	public void setScores(Map<String, DomStudentModelObjectiveScore> result) {
		JSONObject obj = new JSONObject();
		DomStudentModelObjectiveScore gemiddelde = new DomStudentModelObjectiveScore();
		long gt = 0, rt = 0;
		double gs = 0, rs = 0;
		for(Map.Entry<String, DomStudentModelObjectiveScore> entry: result.entrySet())
		{
			String k = entry.getKey();
			DomStudentModelObjectiveScore v = entry.getValue();
			obj.put(k, enc(v));
			if (v.getGreenCount() > 0) {
			  gt += v.getGreenCount();
			  gs += v.getGreenScore();
			}
			if (v.getRedCount() > 0) {
				  rt += v.getRedCount();
				  rs += v.getRedScore();
			}
		}
		gemiddelde.setScore(gs, gt, rs, rt);
		JsTeacherSMClassResultsDisplay.setScore(obj.getJavaScriptObject(), enc(gemiddelde).getJavaScriptObject());
	}
	
}
