package fi.dwo.server.PersistentDataManagers.util;

import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelData;
import fi.dwo.server.PersistentDataManagers.actions.MySQLStudentActions;
import fi.dwo.server.PersistentDataManagers.core.StudentModelDataManager;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

public class StudentModelDataUtilManager {

  private StudentModelDataUtilManager() {
  }

  public static DomStudentModelStructureScore calculateStudentModelScore(
          PersistentStudentModelContext pStudentModel, PersistentHasRole hasRole)
          throws Dwo2Exception {
        DomStudentModelStructureScore score = pStudentModel.buildDomStudentModelContext().getModelStructure().generateStudentModelStructureScore();
//        List<PersistentStudentModelData> list = StudentModelDataManager.findEntities(pStudentModel, hasRole);
//  //        //aggregate data over list
//        try {
//            for (PersistentStudentModelData data : list) {
//                //update leaves
//                DomStudentModelStructureScore dataPoint = data.getModelData();
//                for (int c = 0; c < dataPoint.getCategories().size(); c++) {
//                    for (int o = 0; o < dataPoint.getCategories().get(c).getObjectives().size(); o++) {
//                        DomStudentModelObjectiveScore obj = dataPoint.getCategories().get(c).getObjectives().get(o);
//                        DomStudentModelObjectiveScore sum = score.getCategories().get(c).getObjectives().get(o);
//                       // sum.setCount(sum.getCount() + obj.getCount());
//                        sum.setScore(sum.getScore() + obj.getScore());
//                    }
//                }
//            }
//        } catch (RuntimeException e) {
//            String msg = MessageFormat.format("Something went wrong aggregating scores over student model {0}", pStudentModel.getModelID());
//            MySQLStudentActions.LOG.log(Level.WARNING, msg, e);
//            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, msg);
//        }
        //recalculate categories 
        score.recalculateAncestors();
        //prep return 
        return score;
      }

public static DomStudentModelStructureScore calculateStudentModelScore(DomStudentModelContext t, PersistentHasRole hr) throws Dwo2Exception {

	DomStudentModelStructureScore score = t.getModelStructure().generateStudentModelStructureScore();
// fetch ....
	
	score.recalculateAncestors();

    return score;
}

}
