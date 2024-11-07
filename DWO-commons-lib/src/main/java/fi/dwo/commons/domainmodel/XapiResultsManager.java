package fi.dwo.commons.domainmodel;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataStudentScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScorePerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.dom.xapi.Account;
import nl.uu.fi.dwo.rest.dom.xapi.Activity;
import nl.uu.fi.dwo.rest.dom.xapi.Agent;
import nl.uu.fi.dwo.rest.dom.xapi.StateDocument;
import nl.uu.fi.dwo.rest.dom.xapi.Statement;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsQuery;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsResult;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.StudentModelScoreUtil;
import nl.uu.fi.dwo.rest.util.StudentModelUtil;


public class XapiResultsManager extends StudentModelScoreUtil {
  
  private static final Logger LOG = Logger.getLogger(XapiResultsManager.class.getName());
  private static final SimpleDateFormat FORMAT_8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  private static final SimpleDateFormat FORMAT_8601a = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
  static {
    FORMAT_8601.setTimeZone(TimeZone.getTimeZone("UTC"));
    FORMAT_8601a.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  protected XapiManager xapi;
  protected String homePage;
  final Genson genson;

  class ScoreUpdater {
    DomStudentModelContext context;
    DomStudent student;
    DomStudentModelDataStudentScore score;
    Activity activity;
    Agent agent;

    private ScoreUpdater(DomStudentModelContext context, DomStudent student,
        DomStudentModelDataStudentScore score) {
      this.context = context;
      this.student = student;
      this.score = score;
      activity = new Activity();
      activity.id = "pid:" + context.getId();
      agent = new Agent();
      agent.account = new Account();
      agent.account.homePage = XapiResultsManager.this.homePage;
      agent.name = student.getUserName();
      agent.account.name = "pid:" + student.getId();
    }
    
    private DomStudentModelDataStudentScore toDataScore(StatementsResult result, StateDocument state,
                                                 DomStudentModelContext id) {
      DomStudentModelDataScore data = toDataScore0(result, state, id);
      data.getDomStudentModelStructureScore().recalculateAncestors();
      score.setDomStudentModelStructureScore(data.getDomStudentModelStructureScore());
      score.setFetchTimeStamp(data.getFetchTimeStamp());
      return score;
    }
    
    
    private DomStudentModelDataScore toDataScore0(StatementsResult result, StateDocument state,
                                                 DomStudentModelContext context) {
      DomStudentModelDataScore scores = null;
      if (state.content != null) {
        scores = new DomStudentModelDataScore();
        DomStudentModelStructureScore s =
            genson.deserialize(state.content, DomStudentModelStructureScore.class);
        scores.setDomStudentModelStructureScore(s);
      }
      if (scores == null) scores = eerstestap(context);
      // Sorteren??? JA
      
      List<Statement> list = result.statements;
      Collections.sort(list, new Comparator<Statement>() {

        @Override
        public int compare(Statement o1, Statement o2) {
          String t1 = o1.timestamp;
          String t2 = o2.timestamp;
          return t1.compareTo(t2); // String compare, niet helemaal goed voor meerdere tijdzones.
        }});
      int last = list.size() - 1;
      String lastTimestamp = state.timestamp;
      if (last >= 0) lastTimestamp = result.statements.get(last).timestamp;
      Long stamp;
      try {
        SimpleDateFormat fmt = FORMAT_8601a;
        if (lastTimestamp != null && lastTimestamp.endsWith("Z")) fmt = FORMAT_8601;
        stamp = lastTimestamp == null ? 0L : fmt.parse(lastTimestamp).getTime();
        scores.setFetchTimeStamp(stamp);
      } catch (ParseException e) {
        LOG.log(Level.WARNING, "time conversion", e);
      }
      if (last < 0) return scores;

      stappen(scores, context, list);
      String text = genson.serialize(scores.getDomStudentModelStructureScore()).toString(); // zonder correcties

      StudentModelUtil util = new StudentModelUtil();
      util.setStudentModelStructure(context.getModelStructure());
      util.setStudentModelScore(scores.getDomStudentModelStructureScore());
      DomStudentModelStructureScore calculate = util.calculate();
      scores.setDomStudentModelStructureScore(calculate);

      state.content = text;
      state.contentType = "application/json";
      state.activity = activity;
      state.id = "StudentModelData";
      state.agent = agent;
      state.registration = null;
      xapi.saveState(state); // store in background
      return scores;
    }
   
    public Promise<DomStudentModelDataStudentScore> then() {
      final StatementsQuery query = new StatementsQuery();
      query.agent = agent;
      query.verbID = ATTEMPTED;
      query.activityID = activity.id;
      query.ascending = Boolean.TRUE;
      final StatementsQuery query2 = new StatementsQuery();
      query2.agent = agent;
      query2.verbID = CORRECTED;
      query2.activityID = activity.id;
      query2.ascending = Boolean.TRUE;
      

      return
//          xapi.getState("StudentModelData", activity, agent, null)
//          .recover(oops -> new StateDocument())
          Promises.resolved(new StateDocument())

          .then( p0 -> {
            final StateDocument d = p0.getValue();
            query.since = d.timestamp;
            query2.since = d.timestamp;
            Promise<StatementsResult> queryStatements = xapi.queryStatements(query);
            Promise<StatementsResult> correctedStatements = xapi.queryStatements(query2);
            
            return Promises.all(queryStatements, correctedStatements)
                .then(all -> {
                  StatementsResult q = all.getValue().get(0);
                  StatementsResult c = all.getValue().get(1);
                  if (!c.statements.isEmpty())
                    combine(q,c);
                  return queryStatements;
                })
                
                
                
            .map(statements -> {
             DomStudentModelDataStudentScore p = toDataScore( statements, d, context);
             return p;
             }).then(null, p -> 
             LOG.log(Level.SEVERE, "oops", p.getFailure()));
        });    
      }
    
  }  

  public Promise<DomStudentModelScorePerTeacher> fromXAPI(DomStudentModelScorePerTeacher scores) {
    Map<PersistenceId, DomStudent> students = convert(scores.getStudents());
    Map<PersistenceId, DomStudentModelContext> contexts = convert(scores.getStudentModelContexts());
    if (xapi != null) {
      List<Promise<DomStudentModelDataStudentScore>> promises = new ArrayList<>();
      for (DomStudentModelDataStudentScore item: scores.getStudentScores()) {
        DomStudent student = students.get(item.getStudentId());
        DomStudentModelContext context = contexts.get(item.getModelId().getId());
        promises.add( new ScoreUpdater(context, student, item).then());
      }
      return Promises.all(promises).map(p -> scores).recover(p -> scores);
    }
    return Promises.resolved(scores);
  }



  private <T> Map<PersistenceId, T> convert(
      List<DomMapEntry<PersistenceId, T>> list) {
    Map<PersistenceId, T> result = new HashMap<>();
    list.forEach(item -> result.put(item.getKey(), item.getValue()));
    return result;
  }
  @SuppressWarnings("rawtypes")
  protected void stappen( DomStudentModelDataScore scores, DomStudentModelContext context, List<Statement> statements) {
	  stappen0(scores, context.getModelStructure(), statements);
  }
  XapiResultsManager() {
    genson = new GensonBuilder().create();
  }

  public XapiResultsManager(DomLRS lrs, URL serverUrlPath) {
    this();
    try {
      xapi = new XapiManager(lrs, serverUrlPath);
      homePage = lrs.getAgent().account.homePage;
    } catch (MalformedURLException e) {
      LOG.log(Level.SEVERE, "should not happen", e);
    }

  }

}
