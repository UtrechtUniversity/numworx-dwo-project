package nl.uu.fi.dwo.rest.dom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomResultCourseInClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoPage;
import nl.uu.fi.dwo.rest.dom.entities.DomResultTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentOfClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoPage;
import nl.uu.fi.dwo.rest.dom.entities.util.SumOfSubTreeVisitor;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 *
 * Client side class, not meant to be transported.
 *
 * The information in the DomResultsPerTeacher class is inserted client-side
 * into this simplified kd-range tree. The kd-tree has a search range of 1 and
 * has a node type from resultTree to leave a sequence of: DomTeacher,
 * DomSchoolClass, DomClassCourse referred DomCourse,DomCourse, ..., DomCourse.
 * A leave of the kd-tree is by definition a course-leave.
 *
 * @author G.A.J. van der Plas email: G.A.J.vanderPlas@uu.nl
 */
public class DomResultTree {

    private static final Logger LOG = Logger.getLogger(DomResultTree.class.getName());

    private DomResultTeacher<DomResultCourseInClass> resultTree;
    private DomResultTeacher<DomResultStudent> studentTree;
    //private int newNodeId = 0;
    private List<DomResultScore> nodeList = new ArrayList<DomResultScore>();

    private void addToNodeMap(DomResultScore score) {
        score.setNodeId(nodeList.size());
        nodeList.add(score);
    }

    public DomResultTree(DomMappedResultsPerTeacher resultData) {
      LOG.log(Level.INFO, "Initializing a DomResultTree.");
      buildResultTree(resultData);
    }
    
    public DomResultTree(DomResultsPerTeacher resultData) {
      this(new DomMappedResultsPerTeacher(resultData));
    }

    /**
     * Takes the data of a DomResultsPerTeacher object and builds two result
     * trees. The result trees can be crawled and data can be collected for
     * result viewing of studentsco data. The result trees are a mixed object
     * type tree where every tree path has the following object type sequence:
     *
     * resultTree:DomResultTeacher, DomResultSchoolClass, one or more DomCourse,
     * DomSco, DomStudentSco.
     *
     * or
     *
     * studentTree: DomResultTeacher, DomResultSchoolClass, DomResultStudent
     *
     * @param resultData
     */
    private void buildResultTree(DomMappedResultsPerTeacher resultData) {
        /* build tree from results collection
           create the treeroot a DomResultTeacher, Attach the DomResultSchoolclass
           objects, create the subtrees of the DomCourse objects, attach them 
           to the DomResultSchoolClasses, add the Students per schoolclass.
         */

        //set the resultTree teacher
        setResultTree(new DomResultTeacher<>(resultData.getTeacher()));
        setStudentTree(new DomResultTeacher<>(resultData.getTeacher()));
        //set the schoolclasses of the teacher
        Map<PersistenceId, DomResultSchoolClass<DomResultCourseInClass>> schoolClasses = new HashMap<>(resultData.getSchoolClasses().size());
        Map<PersistenceId, DomResultSchoolClass<DomResultStudent>> studentClasses = new HashMap<>(resultData.getSchoolClasses().size());
        getResultTree().setChildren(schoolClasses);
        getStudentTree().setChildren(studentClasses);
        //create schoolClass maps for studentTree and resultTree
        for (PersistenceId key : resultData.getSchoolClasses().keySet()) {
            DomResultSchoolClass<DomResultCourseInClass> resultValue = new DomResultSchoolClass<>(resultData.getSchoolClasses().get(key));
            DomResultSchoolClass<DomResultStudent> classValue = new DomResultSchoolClass<>(resultData.getSchoolClasses().get(key));
            //set child to parent
            resultValue.setParent(getResultTree());
            classValue.setParent(getStudentTree());
            //add to parent
            schoolClasses.put(key, resultValue);
            studentClasses.put(key, classValue);
        }

        //add students to studentClasses
        for (PersistenceId key : resultData.getStudentsOfClasses().keySet()) {
            //for each student in a class
            DomStudentOfClass soc = resultData.getStudentsOfClasses().get(key);
            PersistenceId sId = soc.getStudentId();
            if (studentClasses.containsKey(soc.getClassId()) && !studentClasses.get(soc.getClassId()).getChildren().containsKey(sId)) {
                //if class exists and student does not yet exist, add student to that class.
                //fetch student
                DomStudent student = resultData.getStudents().get(sId);
                //add student to parent.
                studentClasses.get(soc.getClassId()).getChildren().put(sId, new DomResultStudent(student));
            }
        }

        //Built an index for DomCourses with the same parentID. Memory efficient.
        Map<PersistenceId, List<DomScoContext>> scoParentIndex = new HashMap<PersistenceId, List<DomScoContext>>();
        for (DomScoContext sco : resultData.getScoContexts().values()) {
            if (resultData.getCourses().containsKey(sco.getCourseId())) { //if it is an existing course
                if (!scoParentIndex.containsKey(sco.getCourseId())) {
                    scoParentIndex.put(sco.getCourseId(), new ArrayList<DomScoContext>());
                }
                scoParentIndex.get(sco.getCourseId()).add(sco);
            }
        }

        //Built an index for DomStudentScoContext's with the same parentID. Memory efficient.
        Map<PersistenceId, List<DomStudentScoContext>> ssParentIndex = new HashMap<PersistenceId, List<DomStudentScoContext>>();
        for (DomStudentScoContext ss : resultData.getStudentScoContexts().values()) {
            if (resultData.getScoContexts().containsKey(ss.getScoID())) { //ifit is an existing course
                if (!ssParentIndex.containsKey(ss.getScoID())) {
                    ssParentIndex.put(ss.getScoID(), new ArrayList<DomStudentScoContext>());
                }
                ssParentIndex.get(ss.getScoID()).add(ss);
            }
        }

        //scan the classcourses and add a root result course instance to every class
        //assume flat trees, therefore children are sco's.
        for (PersistenceId key : resultData.getClassCourses().keySet()) {
            //build the subtrees
            DomClassCourse4Teacher cc = resultData.getClassCourses().get(key);
            //           if (cc.getViewState() == ViewState.invisible || cc.getViewState() == ViewState.studentsAndTeachers || cc.getViewState() == ViewState.teachers) {
            if (
//            		cc.getViewState() == ViewState.studentsAndTeachers || 
//            		cc.getViewState() == ViewState.students || 
//            		cc.getViewState() == ViewState.invisible
            		
            		cc.getViewState() != ViewState.none &&
            		cc.getViewState() != ViewState.onlyStudents &&
            		cc.getViewState() != ViewState.studentsNorTeachers &&
            		! (cc.getViewState() == ViewState.students && Boolean.FALSE.equals(cc.getResults())) &&
            		! (cc.getViewState() == ViewState.studentsOrTeachers && Boolean.FALSE.equals(cc.getResults()))
            		
            ) {
                DomResultCourseInClass resultCourse = new DomResultCourseInClass(resultData.getCourses().get(cc.getCourseId()), cc.getViewState());
                //attach to class
                schoolClasses.get(cc.getClassId()).getChildren().put(cc.getCourseId(), resultCourse); //add course to parent
                resultCourse.setParent(schoolClasses.get(cc.getClassId())); //add parent to course
                //attach sco-type children to it
                List<DomScoContext> scoList = scoParentIndex.get(resultCourse.getCourse().getId());
                if(scoList != null)
                for (DomScoContext sco : scoList) {
                    DomResultScoContext resultSco = new DomResultScoContext(sco);
                    List<DomStudentScoPage> template = resultData.getStudentScoPages().get(sco.getId());
                    resultSco.setTemplate(template);
                    resultCourse.getChildren().put(sco.getId(), resultSco);//add sco to parent
                    resultSco.setParent(resultCourse);//set parent in sco
                    //find studentsco's to sco if present in the same school class
                    DomResultScore<?> ancestor = resultSco;
                    do {
                        ancestor = ancestor.getParent();
                    } while (!(ancestor instanceof DomResultSchoolClass));

                    DomResultSchoolClass curSchoolClass = (DomResultSchoolClass) ancestor;

                    //add studentSco to Sco in subtree                
                    if (ssParentIndex.containsKey(sco.getId())) {
                        for (DomStudentScoContext ss : ssParentIndex.get(sco.getId())) {
                            DomStudent student = resultData.getStudents().get(ss.getUserID());
                            if (student != null && studentClasses.containsKey(curSchoolClass.getSchoolClass().getId())
                                    && studentClasses.get(curSchoolClass.getSchoolClass().getId()).getChildren().containsKey(student.getId())) {
                                DomResultStudentScoContext value = new DomResultStudentScoContext(ss, student);
                                //value.setParent(resultSco);
								value.setMaxScore(resultSco.getMaxScore());
								value.setScoType(resultSco.getScoContext().getScoType());
                                resultSco.getChildren().put(ss.getId(), value);
// check for studentscopages:                                
                                if (template != null) {
                                	value.setChildren(new HashMap<>());
                                	initResultScoPages(value, template);
                                    List<DomStudentScoPage> pages = resultData.getStudentScoPages().getOrDefault(ss.getId(), Collections.emptyList());
                                    initResultScoPages(value, pages);
                                }
                            }
                        }
                    }
                }
            }
        }
        for (DomResultSchoolClass sc : schoolClasses.values()) {
            sc.calculateSumOfSubtreeScore(studentTree.getChildren().get(sc.getSchoolClass().getId()).getChildren().size());
        }

        assignNodeIds(resultTree);
        assignNodeIds(studentTree);
    }

	public static void initResultScoPages(DomResultStudentScoContext value, List<DomStudentScoPage> pages) {
		boolean hascorrectie = "completed".equals(value.getStudentSco().getCompletionStatus());
		for(DomStudentScoPage page: pages) {
			String label = page.getLabel(); //String.valueOf(page.getSequencenr().intValue() + 1);
			if (page.getMaxFactor() != null && page.getMaxFactor() != 1.0f) {
				label = label + " (×" + page.getMaxFactor() + ")";
			}
			
			
			DomResultStudentScoPage resultPage = new DomResultStudentScoPage(label);
			resultPage.setNodeId(page.getSequencenr().intValue());
			PersistenceId pid = new PersistenceId("LOCAL;none;" + page.getSequencenr());
			value.getChildren().put(pid, resultPage);
			if (page.getScore() != null) {
				if (hascorrectie && Boolean.TRUE.equals(page.getDocentCorrectie())) {
					resultPage.setScore(-1.0);
					resultPage.setMaxScore(Double.valueOf(page.getMaxScore().doubleValue()));					
				} else {
					resultPage.setScore(Double.valueOf(page.getScore().doubleValue()));
					resultPage.setMaxScore(Double.valueOf(page.getMaxScore().doubleValue()));
				}
			} else {
				resultPage.setScore(0.0);
				resultPage.setMaxScore(null);
			}
			if (hascorrectie && page.getCorrectie() != null) {
				resultPage.setCorrectie(page.getCorrectie().doubleValue());
				resultPage.setMaxScore(Double.valueOf(page.getMaxScore().doubleValue()));
			}
			Float maxFactor = page.getMaxFactor();
			if (Float.valueOf(1.0F).equals(maxFactor)) maxFactor = null; // 1.0 is default maxFactor
			resultPage.setMaxFactor(maxFactor);
		}
	}

    private void assignNodeIds(DomResultScore score) {
        addToNodeMap(score);
        if (score.getChildren() instanceof DomResultScore) {
            for (DomResultScore s : (Collection<DomResultScore>) score.getChildren().values()) {
                assignNodeIds(s);
            }
        }
    }

    /**
     * @return the resultTree
     */
    public DomResultTeacher<DomResultCourseInClass> getResultTree() {
        return resultTree;
    }

    /**
     * @param resultTree the resultTree to set
     */
    public void setResultTree(DomResultTeacher<DomResultCourseInClass> resultTree) {
        this.resultTree = resultTree;
    }

    /**
     * @return the studentTree
     */
    public DomResultTeacher<DomResultStudent> getStudentTree() {
        return studentTree;
    }

    /**
     * @param studentTree the studentTree to set
     */
    public void setStudentTree(DomResultTeacher<DomResultStudent> studentTree) {
        this.studentTree = studentTree;
    }

    /**
     * Debug test code for logging a ResultTree.
     *
     * @return
     */
    public String getPlottedResultTree() {
        StringBuilder sb = new StringBuilder();

//       plotSubTree(studentTree);
//        sb.append("\n");
//        LOG.log(Level.INFO, sb.toString());
        plotSubTree(sb,resultTree, 0);
        sb.append("\n");
//        LOG.log(Level.INFO, sb.toString());

        return sb.toString();
    }

    /**
     * used by getPlottedResultTree().
     *
     * @param rt
     * @param depth
     */
    private void plotSubTree(StringBuilder sb, DomResultScore rt, int depth) {

        sb.append(rt.getNodeId());
        sb.append(":");
        sb.append(depth);
        sb.append(":");
        sb.append(rt.getLabel());
        sb.append(":");
        sb.append(rt.getScore());
        sb.append("\n");
//        LOG.log(Level.INFO, tabs.substring(0, depth) + sb.toString());

        for (DomResultScore s : (Collection<DomResultScore>) rt.getChildren().values()) {
            if (rt.getChildren().size() > 0) {
                plotSubTree(sb, s, depth + 1);
            }
        }

    }

    /**
     * @return the nodeMap
     */
    public DomResultCourseInClass getDomCourseInClassFromId(int nodeId) {
        if (nodeList.get(nodeId) instanceof DomResultCourseInClass) {
            return (DomResultCourseInClass) nodeList.get(nodeId);
        } else {
            return null;
        }
    }

    public void updateResultStudentSco(Collection<DomStudentScoContext> set) {
        Map<PersistenceId, DomStudentScoContext> map = new HashMap<>();
        set.stream().forEach(item -> map.put(item.getId(), item));
        findAndUpdateResultStudentSco(resultTree, map);
        insertStudentCourses();
        //resultTree.calculateSumOfSubtreeScore();
        visitSumOfSubtree();
    }

	private void visitSumOfSubtree() {
		SumOfSubTreeVisitor v = new SumOfSubTreeVisitor();
		resultTree.visit(v);
		studentTree.visit(v);
	}
    
   

    private boolean findAndUpdateResultStudentSco(DomResultScore<? extends DomResultScore> item,
        Map<PersistenceId, DomStudentScoContext> map) {
      if(item instanceof DomResultStudentScoContext) {
        DomResultStudentScoContext rssc = (DomResultStudentScoContext) item;
        PersistenceId pid = rssc.getStudentSco().getId(); // if null
        if (pid == null) {
        	PersistenceId uid = rssc.getStudentSco().getUserID();
        	PersistenceId sid = rssc.getStudentSco().getScoID();
        	
        	// find by userid/scoid
        	for (DomStudentScoContext ssc : map.values()) {
        		if ( Objects.equals(uid, ssc.getUserID()) && Objects.equals(sid, ssc.getScoID())) {
        			pid = ssc.getId();
        			break;
        		}
        	}
        }
        DomStudentScoContext studentSco = map.remove(pid);
        if(studentSco != null)
        {
          LOG.info("size is " + rssc.getChildren().size());
          rssc.getChildren().clear(); // invalidate pages
          rssc.setStudentSco(studentSco);
          rssc.setStudentScoCount(1);
          return true;
        } else { 
          return false;
        }
      }
      item.getChildren().values().stream().forEach(child -> findAndUpdateResultStudentSco(child, map));
      if(item instanceof DomResultScoContext) {
        DomResultScoContext rsc = (DomResultScoContext) item;
        Iterator<DomStudentScoContext> it = map.values().iterator();
        while (it.hasNext()) {
          DomStudentScoContext ssc = it.next();
          if(ssc.getScoID().equals(rsc.getScoContext().getId()))
          {
              DomStudent student = new DomStudent();
              student.setId(ssc.getUserID());
              DomResultStudentScoContext value = new DomResultStudentScoContext(ssc, student);
              value.setMaxScore(rsc.getMaxScore());
			  rsc.getChildren().put(ssc.getId(), value);
          }          
        };
      }
      return false;
    }
    
    public void updateResultStudentScoPages(PersistenceId sscid, Map<PersistenceId, DomResultStudentScoPage> children) {
      findAndUpdateRestultStudentScoPages(resultTree, sscid, children);
      visitSumOfSubtree();
    }

    private void findAndUpdateRestultStudentScoPages(
        DomResultScore<?>item,
        PersistenceId sscid, Map<PersistenceId, DomResultStudentScoPage> children) {
      if(item instanceof DomResultStudentScoContext) {
        DomResultStudentScoContext context = (DomResultStudentScoContext) item;
        if( sscid.equals(context.getStudentSco().getId())) {
          context.setChildren(children);
        }
        return;
      }
      if (item instanceof DomResultScoContext) {
        DomResultScoContext context = (DomResultScoContext) item;
        item = context.getChildren().get(sscid);
        if(item != null)
          findAndUpdateRestultStudentScoPages(item, sscid, children);
        return;
      }
      
      item.getChildren().values().stream().forEach(child -> findAndUpdateRestultStudentScoPages(child, sscid, children));
    }

    public void insertStudentCourses() {
    	// copy studentscocontext from resultTree to studentTree
    	Map<PersistenceId, DomResultSchoolClass<DomResultCourseInClass>> coursemap = resultTree.getChildren();
    	Collection<DomResultSchoolClass<DomResultStudent>> studentclasses = studentTree.getChildren().values();
    	
    	for(DomResultSchoolClass<DomResultStudent> schoolclass: studentclasses) {
    		DomResultSchoolClass<DomResultCourseInClass> resultclass = coursemap.get(schoolclass.getSchoolClass().getId());
    		insertStudentCourses( schoolclass.getChildren().values(), resultclass.getChildren().values());
    	}
    	
    	
    	
    }

	private void insertStudentCourses(Collection<DomResultStudent> students, Collection<DomResultCourseInClass> courses) {
		for( DomResultStudent student : students) {
			PersistenceId sid = student.getStudent().getId();
			student.getChildren().clear();
			for (DomResultCourseInClass course: courses) {
				DomResultCourseInClass copy = new DomResultCourseInClass(course);
				PersistenceId id = copy.getCourse().getId();
				copy.setParent(student);
				student.getChildren().put(id, copy);
				insertStudentScos(sid, copy, course.getChildren().values());
			}
		}
		
	}

// nog even geen copy: deze ssc is het origineel.	
	private void insertStudentScos(PersistenceId sid, DomResultCourseInClass course,
			Collection<DomResultScoContext> scos) {
		for (DomResultScoContext sco: scos) {
			DomResultScoContext copy = new DomResultScoContext(sco);
			copy.setParent(course);
			PersistenceId id = copy.getScoContext().getId();
			course.getChildren().put(id, copy);
			sco.getChildren().values()
				.stream()
				.filter( ssc -> ssc.getStudentSco().getUserID().equals(sid))
				.forEach( ssc -> copy.getChildren().put(sid, ssc));
		}
		
	}
    
    
}
