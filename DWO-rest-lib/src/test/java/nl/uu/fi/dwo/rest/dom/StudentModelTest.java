package nl.uu.fi.dwo.rest.dom;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;

public class StudentModelTest {

  @Test
  public void test() {
    DomStudentModelStructure structure = new DomStudentModelStructure();
    DomStudentModelCategory category = new DomStudentModelCategory();
    List<DomStudentModelCategory> categories = Collections.singletonList(category);
    structure.setCategories(categories);
    DomStudentModelObj objective, child;
    objective = new DomStudentModelObj();
    List<DomStudentModelObj> objectives = Collections.singletonList(objective);
    category.setObjectives(objectives);
    
    DomStudentModelStructureScore score = structure.generateStudentModelStructureScore();
    assertNull("score 2 children", score.getCategories().get(0).getObjectives().get(0).getChildren());
    
    child = new DomStudentModelObj();
    objective.setObjectives(Collections.singletonList(child));
    score = structure.generateStudentModelStructureScore();
    List<DomStudentModelObjectiveScore> children = score.getCategories().get(0).getObjectives().get(0).getChildren();
    assertNotNull("score 3 children", children);
//    children.get(0).setCount(1);
//    children.get(0).setScore(100);
//    score.recalculateAncestors();
//    assertEquals("propagate", 1, score.getCount());
  
  }

}
