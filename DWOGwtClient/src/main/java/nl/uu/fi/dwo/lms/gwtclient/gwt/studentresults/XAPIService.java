package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.List;

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
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.dom.xapi.Activity;
import nl.uu.fi.dwo.rest.dom.xapi.Agent;
import nl.uu.fi.dwo.rest.dom.xapi.StateDocument;
import nl.uu.fi.dwo.rest.dom.xapi.Statement;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsQuery;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsResult;
import nl.uu.fi.dwo.rest.util.StudentModelUtil;

@RoleScope
public class XAPIService extends StudentResultsService implements StudentResults {
  public static final DateTimeFormat FORMAT_8601 = DateTimeFormat.getFormat(PredefinedFormat.ISO_8601);

  private Promise<XapiManager> man;

  @Inject XAPIService(SecuredStudentStudentModelManager manager, DwoGlobalVars vars, MethodManager mm, DomContext ctx) {
    super(manager, vars, mm, ctx);
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
//		  if (id.getId().getIdString().startsWith("ADVISEME"))
//			  result = adviseMe.get().getScore(id);
//		  else
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
      query.limit = 100;
      final StatementsQuery query2 = new StatementsQuery();
      query2.agent = query.agent;
      query2.verbID = CORRECTED;
      query2.activityID = query.activityID;
      query2.ascending = Boolean.TRUE;
      query2.limit = 100;
      
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
  
  DomStudentModelDataScore toDataScore(StatementsResult result, StateDocument state,
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
    String text = DomStudentModelStructureScoreCodec.CODEC.encode(scores.getDomStudentModelStructureScore()).toString(); // zonder correcties

    StudentModelUtil util = new StudentModelUtil();
    util.setStudentModelStructure(context.getModelStructure());
    util.setStudentModelScore(scores.getDomStudentModelStructureScore());
    DomStudentModelStructureScore calculate = util.calculate();
	scores.setDomStudentModelStructureScore(calculate);

    state.content = text;
    state.contentType = "application/json";
    Activity a = new Activity(); a.id = "pid:" + context.getId();
    state.activity = a;
    state.id = "StudentModelData";
    state.agent = xapi.getAgent();
    state.registration = null;      
    xapi.saveState(state); // store in background
        
    return scores;
  }

  @SuppressWarnings("rawtypes")
  protected void stappen( DomStudentModelDataScore scores, DomStudentModelContext4Student context, List<Statement> statements) {
	  stappen0(scores, context.getModelStructure(), statements);
  }

  public Promise<String> saveStatement(Statement s) {
      return man.then(xapi -> xapi.getValue().saveStatement(s));
    }
    
    public Promise<Agent> getAgent() {
      return man.map(XapiManager::getAgent);
    }
}
