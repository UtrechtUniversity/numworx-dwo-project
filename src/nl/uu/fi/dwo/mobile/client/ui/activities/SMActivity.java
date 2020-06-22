package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HTML;

import fi.dwo.gwt.lib.rest.CallManagers.XapiManager;
import fi.dwo.gwt.lib.rest.util.DomStudentModelStructureScoreCodec;
import nl.uu.fi.dwo.mobile.client.sco.SMLogger;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.places.SMPlace;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.dom.xapi.Activity;
import nl.uu.fi.dwo.rest.dom.xapi.StateDocument;
import nl.uu.fi.dwo.rest.dom.xapi.Statement;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsQuery;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsResult;

public class SMActivity extends AbstractActivity {

	final RPCHandler rpc;
	final Long token;
	private Promise<List<DomStudentModelContext>> models;
	private Promise<XapiManager> manager;
	private DomStudentModelContext context;
	private DomStudentModelDataScore scores;
	private StateDocument state;
	
	public SMActivity( SMPlace where, RPCHandler rpc) {
		
		this.rpc = rpc;
		String token = where.getToken();
		if (token.isEmpty()) token = "0";
		this.token = Long.parseLong(token);
		models = rpc.getStudentModels();
		manager = rpc.getLRS();
	}
		
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		
		Promises.all(models,manager).then(
		p -> {
		// context ophalen
		context = models.getValue().get(token.intValue());
		XapiManager xapi = manager.getValue();
		scores = null;
		StatementsQuery query = new StatementsQuery();
		query.agent = xapi.getAgent();
		query.verbID = SMLogger.ATTEMPTED;
		query.activityID = "pid:" + context.getId();
		Activity a = new Activity(); a.id = query.activityID;
		query.ascending = Boolean.TRUE;
		// statements ophalen vanaf tijdstip n
		Promise<StateDocument> p2 = xapi.getState("StudentModelData", a, xapi.getAgent(), null);
		p2 = p2.recover(oops -> new StateDocument());
		Promise<StatementsResult> p1 = 
				p2.then( p0 -> {
					StateDocument d = p0.getValue();
					state = d;
					if (d.content != null) {
						scores = new DomStudentModelDataScore();
						DomStudentModelStructureScore s = DomStudentModelStructureScoreCodec.CODEC.decode(d.content);
						scores.setDomStudentModelStructureScore(s);
					}
					query.since = d.timestamp;
				return xapi.queryStatements(query);
				});
		return p1;
		}).
		then ( p -> {
		StatementsResult result = p.getValue();
		List<Statement> list = result.statements;
		if (scores == null) scores = eerstestap(context);
		if (result.statements.isEmpty()) {
// Geen updates.
			HTML vak = new HTML();
			String text = DomStudentModelStructureScoreCodec.CODEC.encode(scores.getDomStudentModelStructureScore()).toString();
			vak.setText(text);
			panel.setWidget(vak);
			return null;
		}
		// Sorteren???
		int last = result.statements.size()-1;
		String lastTimestamp = result.statements.get(last).timestamp;
		Long stamp = SMLogger.FORMAT_8601.parse(lastTimestamp).getTime();
		scores.setFetchTimeStamp(stamp);
		stappen(scores, context, list);

		HTML vak = new HTML();
		String text = DomStudentModelStructureScoreCodec.CODEC.encode(scores.getDomStudentModelStructureScore()).toString();
		state.content = text;
		state.contentType = "application/json";
		Activity a = new Activity(); a.id = "pid:" + context.getId();
		state.activity = a;
		state.id = "StudentModelData";
		state.agent = manager.getValue().getAgent();
		state.registration = null;		
		vak.setText(text);
		panel.setWidget(vak);
		return manager.getValue().saveState(state);
		});
	}

	public DomStudentModelDataScore eerstestap(DomStudentModelContext context) {
		DomStudentModelDataScore result = new DomStudentModelDataScore();
		DomStudentModelStructure structure = context.getModelStructure();
		DomStudentModelStructureScore score = structure.generateStudentModelStructureScore();
		result.setDomStudentModelStructureScore(score);
		result.setModelId(context);
		return result;
	}
	
	public void stappen( DomStudentModelDataScore scores, DomStudentModelContext context, List<Statement> statements) {
		// converteer scores naar een map<String, Score>
		
		Map<String, DomStudentModelScore> model = new HashMap<>();
		// converteer context naar een map<String, DomStudentModelContextInfo>
		Map<String, DomStudentModelContextInfo> infos = new HashMap<>();

		fill( scores.getDomStudentModelStructureScore(), context.getModelStructure(), model, infos);
		
		
		for (Statement statement: statements) {
			Boolean success = statement.result.success;
			
			String className = statement.context.contextActivities.parent.get(0).definition.type;
			double guess = 0.1;
			if(className.contains("AntwoordKeuzeVak"))
			{
				String nrOfChoicesString = className.substring(className.lastIndexOf('/')+1);
				int nrOfChoices = 10;
				try{
					nrOfChoices = Integer.parseInt(nrOfChoicesString);
				}
				catch(Exception e){}
				guess = 1/nrOfChoices;
			}
			
			List<String> ids = statement.context.contextActivities.parent.get(0).definition.extensions.objectives;
			if(Boolean.FALSE.equals(success))
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
			else if(Boolean.TRUE.equals(success))
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

	private void fill(DomStudentModelStructureScore score,
			DomStudentModelStructure structure, Map<String, DomStudentModelScore> model,
			Map<String, DomStudentModelContextInfo> infos) {
		DomStudentModelContextInfo info = structure.getInfo();
		String id = info.getId();
		infos.put(id, info);
		model.put(id, score);
		List<DomStudentModelCategory> cat = structure.getCategories();
		List<DomStudentModelCategoryScore> catS = score.getCategories();
		if (cat == null || catS == null) return;
		int size = Math.min(cat.size(), catS.size());
		for (int i = 0; i < size; i++) {
			fill(catS.get(i), cat.get(i), model, infos);
		}
	}

	private void fill(DomStudentModelCategoryScore score,
			DomStudentModelCategory structure, Map<String, DomStudentModelScore> model,
			Map<String, DomStudentModelContextInfo> infos) {
		DomStudentModelContextInfo info = structure.getInfo();
		String id = info.getId();
		infos.put(id, info);
		model.put(id, score);
		List<DomStudentModelObj> obj = structure.getObjectives();
		List<DomStudentModelObjectiveScore> objS = score.getObjectives();
		if (obj == null || objS == null) return;
		int size = Math.min(obj.size(), objS.size());
		for (int i = 0; i < size; i++) {
			fill(objS.get(i), obj.get(i), model, infos);
		}
	}

	private void fill(DomStudentModelObjectiveScore score,
			DomStudentModelObj structure, Map<String, DomStudentModelScore> model,
			Map<String, DomStudentModelContextInfo> infos) {
		DomStudentModelContextInfo info = structure.getInfo();
		String id = info.getId();
		if (id != null) {
// set defaults
		if (info.getInit() == null) info.setInit(0.5);
		if (info.getLearn() == null) info.setLearn(0.2);
		if (info.getSlip() == null) info.setSlip(0.05);
		if (score.getCount() == 0) {
			score.setScore(info.getInit());
//			score.setCount(1);
		}
			infos.put(id, info);
			model.put(id, score);
		}
		List<DomStudentModelObj> obj = structure.getObjectives();
		List<DomStudentModelObjectiveScore> objS = score.getChildren();
		if (obj == null || objS == null) return;
		int size = Math.min(obj.size(), objS.size());
		for (int i = 0; i < size; i++) {
			fill(objS.get(i), obj.get(i), model, infos);
		}

	}
}
