package nl.uu.fi.dwo.rest.dom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseOfClass;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacherv2;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacherv2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 *
 * Client side class, not meant to be transported.
 *
 * @author G.A.J. van der Plas email: G.A.J.vanderPlas@uu.nl
 */
public class DomCoursesOfSchoolclassTree {

    public static final String SCHOOL_ROOT = "LOCAL;PersistentCourse;schoolRoot";

    public static final String PUBLIC_ROOT = "LOCAL;PersistentCourse;publicRoot";

    private static final Logger LOG = Logger.getLogger(DomCoursesOfSchoolclassTree.class.getName());

    private DomTree<DomCourseOfClass> courseTree;
    private Map<String, DomTree> cocMap;

    public DomCoursesOfSchoolclassTree(DomSchool school, DomCoursesOfSchoolClass4Teacher resultData) {
        LOG.log(Level.INFO, "Initializing a DomCourseTree.");
        courseTree = buildCourseTree(school, resultData);
        //reCalculateResults();
    }
    
    public DomCoursesOfSchoolclassTree(DomSchool school, DomResultsPerTeacher resultData) {
      LOG.log(Level.INFO, "Initializing a DomCourseTree.");
      courseTree = buildCourseTree(school, resultData);
    }

    public DomCoursesOfSchoolclassTree(DomSchool school, DomCoursesOfSchoolClass4Teacherv2 resultData) {
        LOG.log(Level.INFO, "Initializing a DomCourseTree.");
        courseTree = buildCourseTree(school, resultData);
	}

	private DomTree<DomCourseOfClass> buildCourseTree(DomSchool school,
        DomResultsPerTeacher resultData) {
      return buildCourseTree(school, resultData.getCourses(), resultData.getClassCourses());
    }
	private DomTree<DomCourseOfClass> buildCourseTree(DomSchool school,
	        DomResultsPerTeacherv2 resultData) {
	      return buildCourseTreev2(school, resultData.getCourses(), resultData.getClassCourses());
	    }

    private DomTree<DomCourseOfClass> buildCourseTreev2(DomSchool school, List<DomCourse> courses,
			List<DomClassCourse4Teacher> classCourses) {
        cocMap = new HashMap<String, DomTree>(courses.size());
        Map<PersistenceId, DomClassCourse4Teacher> classCourseMap = new HashMap<>(classCourses.size());

          for (DomCourse courseEntry : courses) {

              cocMap.put(courseEntry.getId().getIdString(), new DomTree<DomCourseOfClass>(new DomCourseOfClass(courseEntry)));
          }

          for (DomClassCourse4Teacher ccEntry : classCourses) {
              classCourseMap.put(ccEntry.getCourseId(), ccEntry);
          }

          return buildCourseTreeTail(school, classCourseMap);
	}

	private DomTree<DomCourseOfClass> buildCourseTreeTail(DomSchool school,
			Map<PersistenceId, DomClassCourse4Teacher> classCourseMap) {
		//build DomTree<DomCourseOfClass> tree add every course
          DomTree<DomCourseOfClass> root = new DomTree<DomCourseOfClass>(new DomCourseOfClass());
          DomCourse rootCourse = new DomCourse();
          rootCourse.setName("root");
          root.setParent(null);
          root.setObject(new DomCourseOfClass());

          DomTree<DomCourseOfClass> publicRoot = new DomTree<DomCourseOfClass>(new DomCourseOfClass());
          DomCourse publicRootCourse = new DomCourse();
          publicRootCourse.setId(new PersistenceId(PUBLIC_ROOT));
          publicRootCourse.setName("public");
          publicRootCourse.setWithChildren(true);
          publicRootCourse.setSchoolId(null);
          publicRoot.setParent(root);
          root.getChildren().put(publicRootCourse.getId().getIdString(), publicRoot);
          publicRoot.setObject(new DomCourseOfClass(publicRootCourse));

          DomTree<DomCourseOfClass> schoolRoot = new DomTree<DomCourseOfClass>(new DomCourseOfClass());
          DomCourse schoolRootCourse = new DomCourse();
          schoolRootCourse.setName(school.getSchoolName());
          schoolRootCourse.setId(new PersistenceId(SCHOOL_ROOT));
          schoolRootCourse.setWithChildren(true);
          schoolRootCourse.setSchoolId(school.getId());
          schoolRoot.setParent(root);
          root.getChildren().put(schoolRootCourse.getId().getIdString(), schoolRoot);
          schoolRoot.setObject(new DomCourseOfClass(schoolRootCourse));

          for (DomTree<DomCourseOfClass> n : cocMap.values()) {
          	if (n.getObject().getCourse() == null) continue;
              //attach classCourse to DomTree<DomCourseOfClass> n if it exists
              //LOG.log(Level.FINE, " id, parent id " + n.getObject().getCourse().getId() + ", " + n.getObject().getCourse().getParentID());
              if (classCourseMap.containsKey(n.getObject().getCourse().getId())
//                 && classCourseMap.get(n.getObject().getCourse().getId().getIdString()).getViewState()!=ViewState.invisible
                      ){
                  n.getObject().setClassCourse(classCourseMap.get(n.getObject().getCourse().getId()));
              }
              //build tree in O(n) time, link parents and kids
              PersistenceId pId = n.getObject().getCourse().getParentID();
              if (pId == null) {// ref to root course
                  //add to root node if not root node
                  if (!root.getChildren().containsKey(n.getObject().getCourse().getId().getIdString())
                          //&& n.getObject().getCourse().getWithChildren()
                          ) {
                      DomCourseOfClass coc = n.getObject();
                      DomCourse c = coc.getCourse();
                      //proxy the new maps with fake persistence id's, ugly but effective.
                      if (n.getObject().getCourse().getSchoolId() == null) {
                          n.setParent(publicRoot);
                          n.getObject().getCourse().setParentID(publicRootCourse.getId());
                          publicRoot.getChildren().put(c.getId().getIdString(), n);
                      } else {
                          n.setParent(schoolRoot);
                          n.getObject().getCourse().setParentID(schoolRootCourse.getId());
                          schoolRoot.getChildren().put(c.getId().getIdString(), n);
                      }
                  }
//                  cocMap.get("root").getChildren().put(n.getObject().getCourse().getId().getIdString(), n);
              } else//add to parent in DomTree<DomCourseOfClass> tree
              //get course parent                
              if (cocMap.containsKey(pId.getIdString())) {
                  Map<String, DomTree<DomCourseOfClass>> children = cocMap.get(pId.getIdString()).getChildren();
                  children.put(n.getObject().getCourse().getId().getIdString(), n);
              }
          }
          // add root node in cocMap
          cocMap.put(schoolRootCourse.getId().getIdString(), schoolRoot);
          cocMap.put(publicRootCourse.getId().getIdString(), publicRoot);
          cocMap.put(null, root);
          LOG.log(Level.FINE, "cocMap entry for null: " + cocMap.get(null));
          //dump tree to logging
          setCourseTree(root);
//          LOG.log(Level.FINE, "Dumping DomCourseTree (depth, name).");
          //DFSTreePrint(root);
          return root;
	}

	private DomTree<DomCourseOfClass> buildCourseTree(DomSchool school, DomCoursesOfSchoolClass4Teacher resultData) {
        return buildCourseTree(school, resultData.getCourses(), resultData.getClassCourses());
    }
	private DomTree<DomCourseOfClass> buildCourseTree(DomSchool school, DomCoursesOfSchoolClass4Teacherv2 resultData) {
        return buildCourseTreev2(school, resultData.getCourses(), resultData.getClassCourses());
    }

    private DomTree<DomCourseOfClass> buildCourseTree(DomSchool school,
        final List<DomMapEntry<PersistenceId, DomCourse>> courses,
        final List<DomMapEntry<PersistenceId, DomClassCourse4Teacher>> classCourses) {
      cocMap = new HashMap<String, DomTree>(courses.size());
      Map<PersistenceId, DomClassCourse4Teacher> classCourseMap = new HashMap<>(classCourses.size());

        for (DomMapEntry<PersistenceId, DomCourse> courseEntry : courses) {

            cocMap.put(courseEntry.getKey().getIdString(), new DomTree<DomCourseOfClass>(new DomCourseOfClass(courseEntry.getValue())));
        }

        for (DomMapEntry<PersistenceId, DomClassCourse4Teacher> ccEntry : classCourses) {
            classCourseMap.put(ccEntry.getValue().getCourseId(), ccEntry.getValue());
        }

        return buildCourseTreeTail(school, classCourseMap);
    }

    private void DFSTreePrint(DomTree<DomCourseOfClass> node) {
        DFSTreePrint(node, 0);
    }

    private void DFSTreePrint(DomTree<DomCourseOfClass> node, int depth) {
        // do depth first search       
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            depth++;
            for (DomTree<DomCourseOfClass> coc : node.getChildren().values()) {
                LOG.log(Level.FINE, "(" + depth + "," + coc.getObject().getCourse().getName() + ")");
                if (coc.getChildren() != null && !coc.getChildren().isEmpty()) {
                    for (DomTree<DomCourseOfClass> child : node.getChildren().values()) {
                        DFSTreePrint(child, depth);
                    }
                }
            }
            depth--;
        }

    }

    public DomTree<DomCourseOfClass> getNode(String key) {
        return cocMap.get(key);
    }

    /**
     * @return the courseTree
     */
    public DomTree<DomCourseOfClass> getCourseTree() {
        return courseTree;
    }

    /**
     * @param courseTree the courseTree to set
     */
    public void setCourseTree(DomTree<DomCourseOfClass> courseTree) {
        this.courseTree = courseTree;
    }
}
