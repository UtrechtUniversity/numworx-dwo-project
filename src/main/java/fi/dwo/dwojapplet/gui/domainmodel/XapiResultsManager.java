package fi.dwo.dwojapplet.gui.domainmodel;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import fi.dwo.dwojapplet.domain.DwoHelper;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.XapiManager;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataStudentScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScorePerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.dom.xapi.Account;
import nl.uu.fi.dwo.rest.dom.xapi.Activity;
import nl.uu.fi.dwo.rest.dom.xapi.Agent;
import nl.uu.fi.dwo.rest.dom.xapi.StateDocument;
import nl.uu.fi.dwo.rest.dom.xapi.Statement;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsQuery;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsResult;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class XapiResultsManager {

  static private final Logger LOG = Logger.getLogger(XapiResultsManager.class.getName());
  public static final String ATTEMPTED = "http://www.dwo.nl/verbs/attempted";
 
  XapiManager xapi;
  String homePage;
  Genson genson;
  
  public XapiResultsManager(DomLRS lrs) {
    
    try {
      xapi = new XapiManager(lrs, DwoHelper.getServerUrlPath());
      homePage = lrs.getAgent().account.homePage;
      genson = new GensonBuilder().create();
    } catch (MalformedURLException e) {
      LOG.log(Level.SEVERE, "should not happen", e);
    }
    
  }

  private static final SimpleDateFormat FORMAT_8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  private static final SimpleDateFormat FORMAT_8601a = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
  static {
    FORMAT_8601.setTimeZone(TimeZone.getTimeZone("UTC"));
    FORMAT_8601a.setTimeZone(TimeZone.getTimeZone("UTC"));

  }

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
    
//    private void calculateInterior(DomStudentModelStructureScore data) {
//      data.recalculateAncestors(); if(true) return;
//        long count = 0;
//        double value = 0.0;
//        for (DomStudentModelCategoryScore item: data.getCategories()) {
//          calculateInterior(item);
//          if (item.getCount() > 0) {
//            value += item.getScore()/item.getCount();
//            count += 1;
//          } else {
//            value += 0.5;
//            count += 1;
//          }
//        }
//        data.setScore(value/count); // FIXME 
//    }

//    private void calculateInterior(DomStudentModelCategoryScore data) {
//      long count = 0L;
//      double value = 0.0;
//      for (DomStudentModelObjectiveScore item: data.getObjectives()) {
//        calculateInterior(item);
//        if (item.getCount() > 0) {
//          value += item.getScore() / item.getCount();
//          count += 1;
//        } else {
//          value += 0.5;
//          count += 1;
//        }
//      }
//      data.setScore(value/count);      
//    }

//    private void calculateInterior(DomStudentModelObjectiveScore data) {
//      if (data.getChildren() != null && !data.getChildren().isEmpty()) {
//        long count = 0L;
//        double value = 0.0;
//        for (DomStudentModelObjectiveScore item: data.getChildren()) {
//          calculateInterior(item);
//          if (item.getCount() > 0) {
//            value += item.getScore() / item.getCount();
//            value += 1;
//          }
//        }
//        data.setScore(value/count); // FIXME
//      }
//      
//    }

    private DomStudentModelDataScore eerstestap(DomStudentModelContext context) {
      DomStudentModelDataScore result = new DomStudentModelDataScore();
      DomStudentModelStructure structure = context.getModelStructure();
      DomStudentModelStructureScore score = structure.generateStudentModelStructureScore();
      result.setDomStudentModelStructureScore(score);
      result.setModelId(context);
      return result;
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

      String text = genson.serialize(scores.getDomStudentModelStructureScore()).toString();
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
      StatementsQuery query = new StatementsQuery();
      query.agent = agent;
      query.verbID = ATTEMPTED;
      query.activityID = activity.id;

      return
//          xapi.getState("StudentModelData", activity, agent, null)
//          .recover(oops -> new StateDocument())
          Promises.resolved(new StateDocument())

          .then( p0 -> {
            final StateDocument d = p0.getValue();
            query.since = d.timestamp;
            Promise<StatementsResult> queryStatements = xapi.queryStatements(query);
            return queryStatements
            .map(statements -> {
             DomStudentModelDataStudentScore p = toDataScore( statements, d, context);
             return p;
             });
        });    
      }
    
  }  
  
  
  Promise<DomStudentModelScorePerTeacher> fromXAPI(DomStudentModelScorePerTeacher scores) {
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
  private void stappen( DomStudentModelDataScore scores, DomStudentModelContext context, List<Statement> statements) {
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
            guess = 1.0/nrOfChoices;
        }
        
        List<String> ids = statement.context.contextActivities.parent.get(0).definition.extensions.objectives;
        
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

  private List<String> strip(List<String> ids) {
    return ids.stream().map(s -> s.split("/")[0]).collect(Collectors.toList());
  }

  private Set<String> strip(Set<String> ids) {
    return ids.stream().map(s -> s.split("/")[0]).collect(Collectors.toSet());
  }



List<String> metVoorkennis(List<String> ids,
      Map<String, DomStudentModelContextInfo> infos) {
    ids = strip(ids);
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
  

}
