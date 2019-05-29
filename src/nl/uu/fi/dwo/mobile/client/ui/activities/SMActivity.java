package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelData;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;
import nl.uu.fi.dwo.rest.dom.xapi.Statement;

public class SMActivity extends AbstractActivity {

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		
		// context ophalen
		DomStudentModelContext context = new DomStudentModelContext();
		// oude modeldata ophalen 
		DomStudentModelData scores = null;
		// statements ophalen vanaf tijdstip n
				
	    Statement statement = new Statement();
		// vullen
		
		List<Statement> list = Collections.singletonList(statement);
	
		
		if (scores == null) scores = eerstestap(context);
		
		stappen(scores, context, list);
		
		Widget vak = new Label("asdadsa");
		panel.setWidget(vak);
	}

	public DomStudentModelData eerstestap(DomStudentModelContext context) {
		DomStudentModelData result = new DomStudentModelData();
		// ...
		
		return result;
	}
	
	public void stappen( DomStudentModelData scores, DomStudentModelContext context, List<Statement> statements) {
		// converteer scores naar een map<String, Score>
		
		Map<String, DomStudentModelScore> model = new HashMap<>();
		// converteer context naar een map<String, DomStudentModelContextInfo>
		Map<String, DomStudentModelContextInfo> infos = new HashMap<>();
		
		for (Statement statement: statements) {
			Boolean success = statement.result.success;
			
			String className = statement.context.contextActivities.parent.get(0).definition.type;
			double guess = 0.1;
			if(className.contains("AntwoordKeuzeVak"))
			{
				String nrOfChoicesString = className.substring(className.indexOf('/'));
				int nrOfChoices = 10;
				try{
					nrOfChoices = Integer.parseInt(nrOfChoicesString);
				}
				catch(Exception e){}
				guess = 1/nrOfChoices;
			}
			
			List<String> ids = statement.context.contextActivities.parent.get(0).definition.extensions.objectives;
			if(success.equals(false))
			{
				//Calculate prodCorrect based on current scores
				double prodCorrect = 1;
				for(String id: ids)
				{
					double current = model.get(id).getScore();
					DomStudentModelContextInfo info = infos.get(id);
					prodCorrect = prodCorrect * ((1 - info.getSlip()) * current + guess * (1 - current));
				}
				
				//Now that prodCorrect has been calculated, use it to calculate all new scores
				for(String id: ids)
				{	double current = model.get(id).getScore();
					DomStudentModelContextInfo info = infos.get(id);
					double newScore = (1 - (1 - info.getSlip()) * prodCorrect / ((1 - info.getSlip()) * current + guess * (1 - current))) *
							current / (1 - prodCorrect);
					newScore = newScore + (1 - newScore) * info.getLearn();
					model.get(id).setScore(newScore);
				}
			}
			else if(success.equals(true))
			{
				//Immediately calculate new scores for all ids
				for(String id: ids)
				{	double current = model.get(id).getScore();
					DomStudentModelContextInfo info = infos.get(id);
					double newScore = current * (1 - info.getSlip()) / (current * (1 - info.getSlip()) + (1 - current) * guess);
					newScore = newScore + (1 - newScore) * info.getLearn();
					model.get(id).setScore(newScore);
				}
			}
		}
		
		//TODO: en dan gegevens uit het model weer terugzetten naar de tree? Of is dat niet nodig?
	}
}
