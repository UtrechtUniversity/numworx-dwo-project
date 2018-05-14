package nl.uu.fi.dwo.mobile.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Provider;

import org.osgi.util.promise.Failure;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import nl.uu.fi.dwo.account.client.DialogFailure;
import nl.uu.fi.dwo.account.client.StudentModelView;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;

public class StudentModelPanel extends nl.uu.fi.dwo.account.client.StudentModelPanel implements StudentModelView {

	public static final Provider<StudentModelView> BUILDER = new Builder();
	private static class Builder implements Provider<StudentModelView> {

		@Override
		public StudentModelView get() {
			return new StudentModelPanel(new DialogFailure(DWOplayer.clientfactory.getEventBus()));
		}
		
	}
		
	SimplePanel simple;
	ScoresObjectivesPanel main;
	DockLayoutPanel dock;
	@Override
	protected void createAndBindUi() {
		selectBox = new ListBox();
		FlowPanel flow = new FlowPanel();
		flow.add(new InlineHTML("StudentModel "));
		flow.add(selectBox);
		button = new Button("X");
		//flow.add(button);
		dock = new DockLayoutPanel(Unit.EM);
		dock.addNorth(flow, 3);
		simple = new ScrollPanel();
		dock.add(simple);
		initWidget(dock);	
		setPixelSize(600,400);
		button.addClickHandler(this);
		selectBox.addChangeHandler(this);
	}

	StudentModelPanel(Failure failure) {
		super(failure);
	}

	@Override
	protected void deselect() {
		simple.remove(main);
	}

	@Override
	public void updateStructure(DomStudentModelStructure modelStructure,
			DomStudentModelStructureScore modelStructureScore) {

		HashMap<String,Object> map = new HashMap<>();
		// vullen 
		List<DomStudentModelCategory> categories = modelStructure.getCategories();
		int size = categories.size();
		String[][] objectives = new String[size][];
		map.put("objectives", objectives);
		String[] categorieString = new String[size];
		map.put("categorieString", categorieString);
		double[] categoryScoreObjectives = new double[size];
		map.put("categorieScoreObjectives",categoryScoreObjectives);
		double[] categorieMaxObjectives = new double[size];
		map.put("categorieMaxObjectives",categorieMaxObjectives);
		
		double[][] scores = new double[size][];
		double[][] max   = new double[size][];
		
		map.put("totaalScoreObjectives", scores);
		map.put("totaalMaxObjectives", max);
		
		for(int i = 0;i < size; i++) {
			DomStudentModelCategory child = categories.get(i);
			DomStudentModelCategoryScore cScore = modelStructureScore.getCategories().get(i);
			categorieString[i] = child.getInfo().getTitle().get(locale);
			categoryScoreObjectives[i] = cScore.getScore();
			categorieMaxObjectives[i] = cScore.getCount();
			List<DomStudentModelObj> list = child.getObjectives();
			String[] o = new String[list.size()];
			double[] s = new double[o.length];
			double[] m = new double[o.length];
			for(int j = 0; j < list.size(); j++) {
				o[j] = list.get(j).getInfo().getTitle().get(locale);
				s[j] = cScore.getObjectives().get(j).getScore();
				m[j] = cScore.getObjectives().get(j).getCount();
//				s[j] = s[j]/m[j];
//				m[j] = 1;
			}
			objectives[i] =o;
			scores[i] = s;
			max[i] = m;
		}
		
		
		try {
			if(main != null) simple.remove(main);
			main = new ScoresObjectivesPanel(map, true, true);
			simple.add(main);
		} catch (Exception e) {			
			Logger.getGlobal().log(Level.SEVERE, "scoreobjectives", e);
		}
		
		
	}

	@Override
	public void updateModels(Collection<String> keySet) {
		super.updateModels(keySet);
		if(!keySet.isEmpty() && initialSelection == null) {
			selectBox.setSelectedIndex(selectBox.getItemCount()-1); // pick last to start
			onChange(null);
		}
	}
	

}
