package nl.uu.fi.dwo.rest.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
import nl.uu.fi.dwo.rest.dom.xapi.Extensions;
import nl.uu.fi.dwo.rest.dom.xapi.Score;
import nl.uu.fi.dwo.rest.dom.xapi.Statement;
import nl.uu.fi.dwo.rest.dom.xapi.StatementsResult;

/**
 * Parent class voor Xapi services.
 * Gedeelde berekeningen voor dwo-commons en gwtclient.
 */

public abstract class StudentModelScoreUtil {
	public static final String ATTEMPTED = "http://www.dwo.nl/verbs/attempted";
	public static final String CORRECTED = "http://www.dwo.nl/verbs/corrected";
	private static final Logger LOG = Logger.getLogger(StudentModelScoreUtil.class.getName());

	protected static List<String> strip(List<String> ids) {
	    return ids.stream().map(s -> s.split("/")[0]).collect(Collectors.toList());
	  }

	public static List<String> metVoorkennis(List<String> ids, Map<String, DomStudentModelContextInfo> infos) {
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

	protected static boolean himark(Score score) {
	    if (score == null) return false;
	    Double scaled = score.scaled;
	    if (scaled == null) return false;
	    boolean b = scaled.doubleValue() >= 0.75;
	    if (b) 
	      LOG.info("himark " + scaled.doubleValue());
	    return b;
	  }

	protected static boolean lomark(Score score) {
	    if (score == null) return false;
	    Double scaled = score.scaled;
	    if (scaled == null) return false;
	    boolean b = scaled.doubleValue() <= 0.25 && scaled.doubleValue() > 0;
	    if (b) 
	      LOG.info("lomark " + scaled.doubleValue());
	    return b;
	  }

	protected DomStudentModelDataScore eerstestap(DomStudentModelContext context) {
		    DomStudentModelStructure structure = context.getModelStructure();
		    return eerstestap(context, structure);
		  }
		  
	protected DomStudentModelDataScore eerstestap(DomStudentModelContext4Student context) {
			  return eerstestap(context, context.getModelStructure());
		  }

	private DomStudentModelDataScore eerstestap(DomStudentModelContextId context, DomStudentModelStructure structure) {
			DomStudentModelDataScore result = new DomStudentModelDataScore();
		    DomStudentModelStructureScore score = structure.generateStudentModelStructureScore();
		    result.setDomStudentModelStructureScore(score);
		    result.setModelId(context);
		    return result;
		}

	protected void fill(DomStudentModelStructureScore score, DomStudentModelStructure structure, Map<String, DomStudentModelScore> model, Map<String, DomStudentModelContextInfo> infos) {
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
	private void fill(DomStudentModelCategoryScore score, DomStudentModelCategory structure, Map<String, DomStudentModelScore> model, Map<String, DomStudentModelContextInfo> infos) {
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
	private void fill(DomStudentModelObjectiveScore score, DomStudentModelObj structure, Map<String, DomStudentModelScore> model, Map<String, DomStudentModelContextInfo> infos) {
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

	public void combine(StatementsResult q, StatementsResult c) {
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
	      } else {
	    	  qq.add(s);
	      }
	    }
	  }

	@SuppressWarnings("rawtypes")
	protected void stappen0(DomStudentModelDataScore scores, DomStudentModelStructure domStudentModelStructure, List<Statement> statements) {
	    // converteer scores naar een map<String, Score>
	    
	    Map<String, DomStudentModelScore> model = new HashMap<>();
	    // converteer context naar een map<String, DomStudentModelContextInfo>
	    Map<String, DomStudentModelContextInfo> infos = new HashMap<>();
	
	    fill( scores.getDomStudentModelStructureScore(), domStudentModelStructure, model, infos);
	    
	    
	    for (Statement statement: statements) {
	        Boolean success = statement.result.success;
	        Score   score   = statement.result.score;
	
	        if (success == null && score != null) {
	        	Double scaled = score.scaled;
	        	
	        	if (scaled != null) {
	        		double asDouble = scaled.doubleValue();
					if (asDouble >= 0.75) success = Boolean.TRUE;
	        		else if (asDouble <= 0.25 && asDouble > 0.00) success = Boolean.FALSE; // no failure, no points -> intermediate
	        	}       	
	        }
	       
	       String className = statement.context.contextActivities.parent.get(0).definition.type;
	       Extensions extensions = statement.context.contextActivities.parent.get(0).definition.extensions;
	       double guess = 0.1;
	        if(className.contains("AntwoordKeuzeVak")
	        	//	||className.contains("CheckUnitPanel")
	        )
	        {
	            String nrOfChoicesString = className.substring(className.lastIndexOf('/')+1);
	            int nrOfChoices = 10;
	            try{
	                nrOfChoices = Integer.parseInt(nrOfChoicesString);
	            }
	            catch(Exception e){}
	            guess = 1.0/nrOfChoices;
	        }
	        if (extensions.guess != null) {
	        	guess = extensions.guess.doubleValue();
	        }
	        
	        List<String> ids = extensions.objectives;
	        
	        ids = strip(ids);
	        
	        if (Boolean.FALSE.equals(success))
	        {
//	          if (statement.result.extensions != null && statement.result.extensions.objectives != null) {
//	            ids.removeAll(statement.result.extensions.objectives);
//	          }
	          
	          
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
	        else if (Boolean.TRUE.equals(success))
	        {
	          Collection<String> voorkennis = extensions.foreknowledge;
	          if (voorkennis != null) {
	            voorkennis = new TreeSet<>(voorkennis);
//	            if (false & statement.result.extensions != null && statement.result.extensions.objectives != null) {
//	              ids = new ArrayList<>(statement.result.extensions.objectives);
//	              ids = strip(ids);
//	              voorkennis.retainAll(metVoorkennis(ids, infos));
//	            }
	            voorkennis.addAll(ids);
	            ids = new ArrayList<>(voorkennis);
	          } else 
	          {
//	            if (false && statement.result.extensions != null && statement.result.extensions.objectives != null) {
//	              ids = new ArrayList<>(statement.result.extensions.objectives);
//	            }
	            ids = metVoorkennis(ids, infos);          
	          }
	          
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

}
