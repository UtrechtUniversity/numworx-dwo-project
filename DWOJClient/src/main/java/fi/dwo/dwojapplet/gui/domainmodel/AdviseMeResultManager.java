package fi.dwo.dwojapplet.gui.domainmodel;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.JComponent;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import fi.beans.ideas.AbstractRule;
import fi.beans.ideas.IdeasClient;
import fi.beans.ideas.IdeasIF;
import fi.beans.ideas.RuleIF;
import fi.beans.ideas.Usermodel;
import fi.beans.ideas.Usermodel.Competence;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScorePerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class AdviseMeResultManager {
  public static final String KEY = "AdviseMe:";
  static private final Logger LOG = Logger.getLogger(AdviseMeResultManager.class.getName());
  private static final String ADVISEME = "adviseme-usermodel";
  final Map<String,String> context = new HashMap<>();

  IdeasIF ideas;
  String locale;
  
  AdviseMeResultManager() {
    try {
		ideas = new IdeasClient(DwoHelper.getApplet().getCodeBase(), IdeasClient.IDEAS);
	} catch (MalformedURLException e) {
	} 
    locale = JComponent.getDefaultLocale().getLanguage();
    context.put("language", locale);
  }

  Promise<DomStudentModelScorePerTeacher> fromAdviseMe(DomStudentModelScorePerTeacher scores) {
    try {
      Long id = MySQLPersistenceId.getNativeId(scores.getTeacher());
      context.put("userid", id.toString());
    } catch (Dwo2Exception e) {
      return Promises.failed(e);
    }

    List<DomMapEntry<PersistenceId, DomStudentModelContext>> contexts = scores.getStudentModelContexts();
    DomStudentModelContext adviseme = null;
    for (DomMapEntry<PersistenceId, DomStudentModelContext> entry : contexts) {
      DomStudentModelContext v = entry.getValue();
      String title = v.getModelStructure().getInfo().getTitle().get(locale);
      if (KEY.equals(title)) {
        adviseme = v;
        break;
      }
      String id = v.getModelStructure().getInfo().getId();
      if (id != null && id.startsWith(KEY)) {
        adviseme = v;
        break;
      }
    }
    if (adviseme != null) {
      AbstractRule input = new AbstractRule() {
        @Override
        public Map getContext() {               
            return context;
        }       
    };
    RuleIF[] inputs = new RuleIF[] { input };
    if ( scores.getStudents() != null && !scores.getStudents().isEmpty()) {
      inputs = new RuleIF[scores.getStudents().size()];
      for (int i = 0; i < inputs.length; i++) {
        try {
          Map context = new HashMap<>(this.context);
          context.put("userid", MySQLPersistenceId.getNativeId(scores.getStudents().get(i).getValue()).toString());
          inputs[i] = new AbstractRule() {
            @Override
            public Map getContext() {               
                return context;
            }       
          };
       } catch (Dwo2Exception e) {
          return Promises.failed(e);
        }
      }
    }
    Usermodel[] usermodel = ideas.adviseMeUsermodel(inputs, ADVISEME);
      if (usermodel != null && usermodel.length > 0) {
        DomStudentModelStructure structure = toStructure(usermodel[0]);
        adviseme.setModelStructure(structure);
        structure.getInfo().setId(KEY + structure.getInfo().getId());
      }
    
    if (scores.getStudentScores() != null) {
      for (int i = 0; i < usermodel.length; i++) {
        DomStudentModelDataScore score = toScore(usermodel[i]);
        scores.getStudentScores().get(i).setDomStudentModelStructureScore(score.getDomStudentModelStructureScore());
      }
    }
    
    }
    return Promises.resolved(scores);
  }

  private DomStudentModelStructure toStructure(Usermodel u) {
    DomStudentModelStructure structure = new DomStudentModelStructure();
    structure.setCategories(toCategories(u.getCompetence().getChildren()));
    structure.setInfo(toInfo(u.getCompetence()));
    return structure;

  }
  private List<DomStudentModelCategory> toCategories(List<Competence> children) {
    return children.stream().map(item -> { 
        DomStudentModelCategory result = new DomStudentModelCategory();
        result.setInfo(toInfo(item));
        result.setObjectives(toObjectives(item.getChildren()));         
        return result;
    }).collect(Collectors.toList());
}

private List<DomStudentModelObj> toObjectives(List<Competence> children) {
    if (children == null) return null;
    return children.stream().map(item -> {
        DomStudentModelObj result = new DomStudentModelObj();
        result.setInfo(toInfo(item));
        result.setObjectives(toObjectives(item.getChildren()));
        return result;
    }).collect(Collectors.toList());
    
}

private DomStudentModelContextInfo toInfo(Competence item) {
    DomStudentModelContextInfo info = new DomStudentModelContextInfo(new HashMap<>(), new HashMap<>());
    info.setId(item.getId());
    info.getTitle().put(locale, item.getLabel());
    String description = item.getDescription();
    if (description == null) description = "";
    String example = item.getExample();
    if (example != null && !example.isEmpty()) {
      String EXAMPLE = TextMapper.dwo2Message().NUM_LBL_ADVISEME_EXAMPLE();
      description += "\n\n" + EXAMPLE + "\n\n" + example;
    }
    info.getDescription().put(locale, description); // XXX wat komt hier?
    return info;
}

public static DomStudentModelStructure restructure(DomStudentModelStructure model, String locale, DomStudentModelContext context) {
  if (KEY.equals(model.getInfo().getTitle().get(locale)))
  {
      DomStudentModelScorePerTeacher scores = new DomStudentModelScorePerTeacher();
      scores.setTeacher(new DomTeacher(DwoHelper.getCurrentUser()));
      scores.setStudentModelContexts(Collections.singletonList(new DomMapEntry<>(context.getId(), context)));
      try {
        return new AdviseMeResultManager().fromAdviseMe(scores).getValue().getStudentModelContexts().get(0).getValue().getModelStructure();
      } catch (InvocationTargetException e) {
        LOG.log(Level.SEVERE, "restructure", e.getTargetException());
      } catch (InterruptedException e) {
      }
  }
  return model;
}
DomStudentModelDataScore toScore(Usermodel u) {
  DomStudentModelDataScore result = new DomStudentModelDataScore();
  DomStudentModelStructureScore model = new DomStudentModelStructureScore();
  model.setScore(u.getCompetence().getValue().doubleValue());
  model.setCategories(toCategoriesScore(u.getCompetence().getChildren()));
  result.setDomStudentModelStructureScore(model);
  return result;
}

private List<DomStudentModelCategoryScore> toCategoriesScore(List<Competence> children) {
  return children.stream().map(item -> { 
      DomStudentModelCategoryScore score = new DomStudentModelCategoryScore();
      score.setScore(item.getValue().doubleValue());
      score.setObjectives(toObjectivesScore(item.getChildren()));
      return score;
  }).collect(Collectors.toList());
}

private List<DomStudentModelObjectiveScore> toObjectivesScore(List<Competence> children) {
  if (children == null)
      return null;
  return children.stream().map(item -> {
      DomStudentModelObjectiveScore score = new DomStudentModelObjectiveScore();
      score.setScore(item.getValue().doubleValue());
      score.setChildren(toObjectivesScore(item.getChildren()));
      return score;
  }).collect(Collectors.toList());
}

}
