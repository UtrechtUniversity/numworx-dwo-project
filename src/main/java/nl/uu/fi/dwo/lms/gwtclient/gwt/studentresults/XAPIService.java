package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.fusesource.restygwt.client.Defaults;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

import fi.dwo.gwt.lib.rest.CallManagers.MethodManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentStudentModelManager;
import fi.dwo.gwt.lib.rest.CallManagers.XapiManager;
import fi.dwo.gwt.lib.rest.util.DomStudentModelStructureScoreCodec;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.dom.xapi.Activity;
import nl.uu.fi.dwo.rest.dom.xapi.Agent;
import nl.uu.fi.dwo.rest.dom.xapi.Extensions;
import nl.uu.fi.dwo.rest.dom.xapi.StateDocument;
import nl.uu.fi.dwo.rest.dom.xapi.Statement;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsQuery;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsResult;

@RoleScope
public class XAPIService extends StudentResultsService implements StudentResults {
  public static final String ATTEMPTED = "http://www.dwo.nl/verbs/attempted";
  public static final String CORRECTED = "http://www.dwo.nl/verbs/corrected";
  public static final DateTimeFormat FORMAT_8601 = DateTimeFormat.getFormat(PredefinedFormat.ISO_8601);

  private Promise<XapiManager> man;

  @Inject XAPIService(SecuredStudentStudentModelManager manager, DwoGlobalVars vars, MethodManager mm) {
    super(manager, vars, mm);
    if (!vars.isPremium()) {
        man = Promises.failed(new IllegalArgumentException());
    } else {
      this.man = manager.getLRS(context).map(lrs -> {
        Defaults.ignoreJsonNulls();
        Defaults.setAddXHttpMethodOverrideHeader(false);
        XapiManager x = new XapiManager();
        x.setServer(lrs.getEndpoint());
        x.setAuth(lrs.getAuth());
        x.setAgent(lrs.getAgent());
        return x;
      });
    }
  }
  
  @Override
  public void clear() {
    super.clear();
  }

//  @Override
//  public Promise<List<DomStudentModelContext>> getModels() {
//    return super.getModels();
//  }

  @Override
  public Promise<DomStudentModelDataScore> getScore(DomStudentModelContextId id) {
	  Promise<DomStudentModelDataScore> result;
	  result = map.get(id.getId());
	  if (result == null || result.isDone() && result.getFailure() != null) {
		  if (id.getId().getIdString().startsWith("ADVISEME"))
			  result = adviseMe.get().getScore(id);
		  else
			  result = getScore_impl(id);
		  map.put(id.getId(), result);
	  }
	  return result.recoverWith(oops -> super.getScore(id));
  }
  
  public Promise<DomStudentModelDataScore> getScore_impl(DomStudentModelContextId id) {
    return man.then(p -> {
      XapiManager xapi = p.getValue();
      StatementsQuery query = new StatementsQuery();
      query.agent = xapi.getAgent();
      query.verbID = ATTEMPTED;
      query.activityID = "pid:" + id.getId();
      Activity a = new Activity(); a.id = query.activityID;
      query.ascending = Boolean.TRUE;
      final StatementsQuery query2 = new StatementsQuery();
      query2.agent = query.agent;
      query2.verbID = CORRECTED;
      query2.activityID = query.activityID;
      query2.ascending = Boolean.TRUE;

      
      // statements ophalen vanaf tijdstip n
      return 
          //xapi.getState("StudentModelData", a, xapi.getAgent(), null)
    	  Promises.resolved(new StateDocument())
          .recover(oops -> new StateDocument())
          .then( p0 -> {
              StateDocument d = p0.getValue();
              query.since = query2.since = d.timestamp;
	          Promise<StatementsResult> result1 = xapi.queryStatements(query);
	          Promise<StatementsResult> result2 = xapi.queryStatements(query2);
	          return Promises.all(result1,result2)
	        		  .map(list -> {
	        			  				StatementsResult statements = result1.getValue();
	        			  				StatementsResult correctie  = result2.getValue();
	        			  				if (!correctie.statements.isEmpty()) {
	        			  					combine(statements, correctie);
	        			  				}
	        			  				return toDataScore( statements, d, id, xapi);
	        		  				});
          });
      });
  }
  
  void combine(StatementsResult q, StatementsResult c) {
	    List<Statement> qq = q.statements;
	    for(Statement s: c.statements) {    
	      ListIterator<Statement> iterator = qq.listIterator();
	      String id = s.context.contextActivities.parent.get(0).id;
	      String stamp = s.timestamp;
	      Statement last = null;
	      while(iterator.hasNext()) {
	        Statement cur = iterator.next();
	        if (id .equals(cur.context.contextActivities.parent.get(0).id)) {
	          String curstamp = cur.timestamp;
	          if (curstamp.compareTo(stamp) == +1) break;
	          last = cur;
	        }
	      }
	      if (last != null) {
	        last.result = s.result;
	      }
	    }
	  }

  
  private DomStudentModelDataScore toDataScore(StatementsResult result, StateDocument state,
      DomStudentModelContextId id, XapiManager xapi) {
    DomStudentModelContext4Student context = (DomStudentModelContext4Student) id; // if not, search from models...
    DomStudentModelDataScore scores = null;
    if (state.content != null) {
      scores = new DomStudentModelDataScore();
      DomStudentModelStructureScore s = DomStudentModelStructureScoreCodec.CODEC.decode(state.content);
      scores.setDomStudentModelStructureScore(s);
      // FIXME validate s against context
    }
    if (scores == null) scores = eerstestap(context);
    // Sorteren???
    List<Statement> list = result.statements;
    int last = list.size()-1;
    String lastTimestamp = state.timestamp;
    if (last>=0)
    		lastTimestamp = result.statements.get(last).timestamp;
    Long stamp = lastTimestamp == null ? 0L : FORMAT_8601.parse(lastTimestamp).getTime();
    scores.setFetchTimeStamp(stamp);
    if (last < 0) {
        scores.getDomStudentModelStructureScore().recalculateAncestors(); 	
    	return scores;
    }
    
    stappen(scores, context, list);

    String text = DomStudentModelStructureScoreCodec.CODEC.encode(scores.getDomStudentModelStructureScore()).toString();
    state.content = text;
    state.contentType = "application/json";
    Activity a = new Activity(); a.id = "pid:" + context.getId();
    state.activity = a;
    state.id = "StudentModelData";
    state.agent = xapi.getAgent();
    state.registration = null;      
    xapi.saveState(state); // store in background

    scores.getDomStudentModelStructureScore().recalculateAncestors();
    return scores;
  }

  private DomStudentModelDataScore eerstestap(DomStudentModelContext context) {
    DomStudentModelStructure structure = context.getModelStructure();
    return eerstestap(context, structure);
  }
  
  private DomStudentModelDataScore eerstestap(DomStudentModelContext4Student context) {
	  return eerstestap(context, context.getModelStructure());
  }

private DomStudentModelDataScore eerstestap(DomStudentModelContextId context, DomStudentModelStructure structure) {
	DomStudentModelDataScore result = new DomStudentModelDataScore();
    DomStudentModelStructureScore score = structure.generateStudentModelStructureScore();
    result.setDomStudentModelStructureScore(score);
    result.setModelId(context);
    return result;
}

  
  
  
  @SuppressWarnings("rawtypes")
  private void stappen( DomStudentModelDataScore scores, DomStudentModelContext4Student context, List<Statement> statements) {
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
        
        Extensions extensions = statement.context.contextActivities.parent.get(0).definition.extensions;
		List<String> ids = extensions.objectives;
        ids = strip(ids);
        if(Boolean.FALSE.equals(success))
        {
            //Calculate prodCorrect based on current scores
            double prodCorrect = 1;
            for(String id: ids)
            {
                DomStudentModelScore modelScore = model.get(id);
                if (modelScore == null) continue;
				double current = modelScore.getScore();
                DomStudentModelContextInfo info = infos.get(id);
                if (info == null) continue;
                prodCorrect = prodCorrect * ((1 - info.getSlip()) * current + guess * (1 - current));
            }
            
            //Now that prodCorrect has been calculated, use it to calculate all new scores
            for(String id: ids)
            {   DomStudentModelScore modelScore = model.get(id);
                if (modelScore == null) continue;
				double current = modelScore.getScore();
                DomStudentModelContextInfo info = infos.get(id);
                if (info == null) continue;
                double newScore = (1 - (1 - info.getSlip()) * prodCorrect / ((1 - info.getSlip()) * current + guess * (1 - current))) *
                        current / (1 - prodCorrect);
                newScore = newScore + (1 - newScore) * info.getLearn();
                modelScore.setScore(newScore);
            }
        }
        else if(Boolean.TRUE.equals(success))
        {
            Collection<String> voorkennis = extensions.foreknowledge;
            if (voorkennis != null) {
              voorkennis = new TreeSet<>(voorkennis);
              voorkennis.addAll(ids);
              ids = new ArrayList<>(voorkennis);
            } else 
            	ids = metVoorkennis(ids, infos);
            //Immediately calculate new scores for all ids
            for(String id: ids)
            {   DomStudentModelScore modelScore = model.get(id);
            	if (modelScore == null) continue;
				double current = modelScore.getScore();
                DomStudentModelContextInfo info = infos.get(id);
                if (info == null) continue;
                double newScore = current * (1 - info.getSlip()) / (current * (1 - info.getSlip()) + (1 - current) * guess);
                newScore = newScore + (1 - newScore) * info.getLearn();
                modelScore.setScore(newScore);
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

@SuppressWarnings("rawtypes")
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

@SuppressWarnings("rawtypes")
private void fill(DomStudentModelObjectiveScore score,
        DomStudentModelObj structure, Map<String, DomStudentModelScore> model,
        Map<String, DomStudentModelContextInfo> infos) {
    DomStudentModelContextInfo info = structure.getInfo();
    String id = info.getId();
    if (id != null) {
//set defaults
    if (info.getInit() == null) info.setInit(0.5);
    if (info.getLearn() == null) info.setLearn(0.2);
    if (info.getSlip() == null) info.setSlip(0.05);
    if (score.getCount() == 0) {
        score.setScore(info.getInit());
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

List<String> metVoorkennis(List<String> ids,
	      Map<String, DomStudentModelContextInfo> infos) {
	    Set<String> all = new TreeSet<String>(ids);
	    Set<String> extra = new TreeSet<>();
	    Set<String> work = new TreeSet<>(all);
	    while( ! work.isEmpty()) {
	      // extra is empty, work is nonempty, work all in "all"
	      for (String id: work) {
	        DomStudentModelContextInfo info = infos.get(id);
	        if (info == null) continue;
	        List<String> voorkennis = info.getVoorkennis();
	        if (voorkennis == null) continue;
	        voorkennis = strip(voorkennis);
	        extra.addAll(voorkennis);
	      }
	      extra.removeAll(all);
	      work.clear();
	      work.addAll(extra);
	      all.addAll(extra);
	      extra.clear();
	    }
	    return new ArrayList<String>(all);
	  }

  
    public Promise<String> saveStatement(Statement s) {
      return man.then(xapi -> xapi.getValue().saveStatement(s));
    }
    
    public Promise<Agent> getAgent() {
      return man.map(XapiManager::getAgent);
    }
    
    private static List<String> strip(Collection<String> list) {
    	return list.stream().map(s -> s.split("/")[0]).collect(Collectors.toList());
    }
}
