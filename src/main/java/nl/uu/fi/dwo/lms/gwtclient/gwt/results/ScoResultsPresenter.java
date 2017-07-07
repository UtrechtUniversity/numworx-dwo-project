package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Widget;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentScoDataManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherScormValuesManager;
import fi.dwo.gwt.lib.rest.CallManagers.StudentScoDataManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DialogEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.results.ScoResultsPresenter.StudentItem;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolClassListBoxItem;
import nl.uu.fi.dwo.rest.dom.DomResultPlotMatrix;
import nl.uu.fi.dwo.rest.dom.DomResultTree;
import nl.uu.fi.dwo.rest.dom.ResultTreeCalculator;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContextFull;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacherScormValues;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class ScoResultsPresenter {

    private static final String COMPLETION_STATUS = "cmi.completion_status";
	private static final SecuredTeacherScormValuesManager SECURED_TEACHER_SCORM_VALUES_MANAGER = new SecuredTeacherScormValuesManager();
	private static final Logger LOG = Logger.getLogger(ScoResultsPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private List<DomStudentScoContext> resultScoData;
    private DomResultTree resultTree;
    private DomResultPlotMatrix resultMatrix;
    private DomResultStudent selectedStudent;
    private DomResultScoContext scoContext;

    private String[] tableHeaders = {"student name", "total score", "verzegeld"};
    private DomSchoolClass schoolClass;
    private Map<String, DomStudent> studentMap;
    private Map<String, StudentItem> studentItems;
    private Map<String, DomSchoolClass> schoolClassMap;
    private List<SchoolClassListBoxItem> schoolClassItems;
    private Display view;
    private int requests = 0;
// Voor het Frame:
	private Promise<String> launchData;
	private Promise<Map<String, String>> scormVars;

	final Failure failure = new Failure() {
        @Override
        public void fail(Promise<?> resolved) throws Exception {
            Throwable fail = resolved.getFailure();
            if (fail instanceof Dwo2Exception) {
                LOG.log(Level.SEVERE, fail.getMessage());
                eventBus.fireEvent(new DialogEvent((Dwo2Exception) fail));
            } else {
                LOG.log(Level.SEVERE, fail.getMessage());
                eventBus.fireEvent(new DialogEvent(fail.getMessage()));
                //throw directly
            }
        }
    };
	
    final Success identity = new Success() {

		@Override
		public Promise call(Promise resolved) throws Exception {
			return resolved;
		}};
	private Map<String, DomResultStudentScoContext> sscMap;
	private DomContext context;
	private static final Collection<String> keys = Arrays.asList(
			"cmi.suspend_data",
			"cmi.location",
			"cmi.score.raw",
			COMPLETION_STATUS,
			"cmi.comments_from_lms.0.comment"       		
			);
	private StudentItem selectedItem;
	private PersistenceId schoolGroupID;
		
	final <T> Success<T, T> identity() { return identity; }
	
    final class StudentScoUpdater implements
			Success<DomStudentScoContext, Void> {
		private final DomResultStudentScoContext forsuccess;

		StudentScoUpdater(DomResultStudentScoContext forsuccess) {
			this.forsuccess = forsuccess;
		}

		@Override
		public Promise<Void> call(Promise<DomStudentScoContext> resolved)
				throws Exception {
			LOG.info("set " + forsuccess.getLabel());
			forsuccess.setStudentSco(resolved.getValue());
// TODO als de score verandert, dan de bijbehorende studentItem ook aanpassen. 
			return null;
		}
	}

	public interface Display {

        Widget asWidget();

        void clear();

        void init();

        void updateView(StudentItem selectedItem, Map<String, StudentItem> data);
        
        void updateFrame(DomScoContext context);
    }

    public class StudentItem {

        public String key; //unique
        public String givenName;
        public String insertion;
        public String familyName;
        public String usercode;
        public Double score;
 // later pas ingevuld!
        public Double[] subScores;
        public Boolean sealed;

        public StudentItem(String aKey, String aFirstName, String anInsertion, String aFamilyName, String aUsercode, Double aScore, Double[] aSubScores) {
            key = aKey;
            givenName = aFirstName;
            insertion = anInsertion;
            familyName = aFamilyName;
            usercode = aUsercode;
            score = aScore;
            subScores = aSubScores;
        }
    }

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    public String[] getTableHeaders() {
        return tableHeaders;
    }

    void goBackToResults() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.ACTIVERESULTS));
    }

    public ScoResultsPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

    /**
     *
     * @param aResultTree
     * @param aScoContext A ScoContext for a schoolClass and Student
     * @param aSelectedStudent A studentSco inside the resultTree object
     */
    public void init(DomResultTree aResultTree, DomResultScoContext aScoContext, DomResultStudent aStudent) { //DomScoContext aSelectedScoContext, DomSchoolClass aSelectedSchoolClass,         
        resultTree = aResultTree;
        scoContext = aScoContext;
// FIXME WRONG MANAGER
        StudentScoDataManager manager = new SecuredStudentScoDataManager();
        context = new DomContext();
        context.setDomHasRole(dwoGlobalVars.getActiveSchoolRoleAndClass().getHasRole());
		DomDwoProfile profile = dwoGlobalVars.getProfile().getValue();
		launchData = manager.getJSONLaunchDataBytes(scoContext.getScoContext(), profile, context).map(new Function<JSONValue, String>() {

			@Override
			public String apply(JSONValue t) {
				return t.toString();
			}
		});
		launchData = launchData.then(identity(), failure);
        
        
        sscMap = new HashMap<String, DomResultStudentScoContext>(scoContext.getChildren().size());
        studentMap = new HashMap<String, DomStudent>();
        for (DomResultStudentScoContext ss : scoContext.getChildren().values()) {
            sscMap.put(ss.getStudentSco().getUserID().getIdString(), ss);
        }
        selectedStudent = aStudent;
        resultMatrix = ResultTreeCalculator.GetScoreOfActivitiesByStudentsInSco(resultTree, aScoContext);
        studentItems = new HashMap<String, StudentItem>(resultMatrix.getvSize());
        selectedItem = null;
        //TODO Wim, make the promise to fetch the first scoData and StudentScoData then when resolved execute the code block below
        for (int i = 0; i < resultMatrix.getvSize(); i++) {
            DomResultStudent s = (DomResultStudent) resultMatrix.getvIndex(i);
            // fetch and insert score here.
            Double score = null;
            DomStudent student = s.getStudent();
			String studentID = student.getId().getIdString();
			if (sscMap.containsKey(studentID)) {
                score = sscMap.get(studentID).getStudentSco().getScore();
            }
            StudentItem si = new StudentItem(studentID, student.getGivenName(),
                    student.getInsertion(), student.getFamilyName(), student.getUserName(), score, null);
            if(selectedStudent.getStudent().getId().getIdString().equals(studentID) ){
                selectedItem = si;
            }
            studentItems.put(studentID, si);
            studentMap.put(studentID, student);
        }
        LOG.log(Level.FINE, "nr students:" + resultMatrix.getvSize());
  // TODO Gert, at least 1 student met resultaat en alle studenten hebben dezelfde schoolgroupid
        schoolGroupID = sscMap.values().iterator().next().getStudentSco().getSchoolGroupID();
        view.updateView(selectedItem,studentItems);
// fetch ScormValues of student (for each student)
        setStudentFrame(selectedItem);
		getAllSeals();
        
    }

	private void setStudentFrame(StudentItem selectedItem) {
		LOG.info("set Student Frame");
		DomResultStudentScoContext rssc = sscMap.get(selectedItem.key);
		if( rssc == null) {
// als null, insert dummy studentscocontext voor "SEAL"
			scormVars = Promises.resolved(new HashMap<>());
			
		} else {
			DomStudentScoContext ssc = rssc.getStudentSco();
	        scormVars = SECURED_TEACHER_SCORM_VALUES_MANAGER.getValues(ssc, context, keys );			
		}
        // if both, updateFrame for this sco.
		Promises.all(launchData, scormVars.then(
				new Success<Map<String,String>, Void>() {

					@Override
					public Promise<Void> call(
							Promise<Map<String, String>> resolved)
							throws Exception {
						selectedItem.sealed = 
								"completed".equals( resolved.getValue().get(COMPLETION_STATUS));
						view.updateView(selectedItem, studentItems);
						return null;
					}
				}
				
				
				, failure)).onResolve(new Runnable() {

			@Override
			public void run() {
				view.updateFrame(scoContext.getScoContext());				
			}});
	}

//    public void select(StudentItem item) {
//        //selected item in single select table
//        //show the new student's studenscodata in the window api if the score is not null
//        //otherwise show message or refuse selection.
//    }

    //function to be called from the view in the future when subscores are supported.
    public void selectItem(StudentItem item, int col) {
        //TODO Wim. Update the sco result view frame.
        switch(col){
            case 0: //Selected studentname (currently an non-click cell)
            case 1: //Selected score (a MyClickCell)
                //display scoresult of student in frame
            	selectedItem = item;
            	setStudentFrame(item);
                break;
            case 2: //selected selection toggle
                  //do some magic stuff   
            break;
            //
            default:
                Dwo2Exception ex = new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Programmer's error, out of cases!");
                ex.fillInStackTrace();
                LOG.log(Level.INFO,""+ex.getDwo2Message()+ex.getStackTrace().toString());
                break;
        }
    }
    /**
     *
     * @param key een CMI variable
     * @return
     */
    public String getScormAPIValue(String key) {
    	if ("cmi.launch_data".equals(key))
    	{
    		return launchData.getValue();
    	}
    	if (COMPLETION_STATUS.equals(key))
    	{
    		return "completed";
    	}
    	if (scormVars.isDone() && scormVars.getFailure() == null) {
    		return scormVars.getValue().getOrDefault(key, "");
    	}
    	
        return "";
    }

    /**
     *
     * @param key een CMI variable
     * @param value zijn nieuwe waarde
     * @return
     */
    public String setScormAPIValue(String key, String value) {
        return "true";
    }

	public Promise<?> setSeal(StudentItem object, Boolean value) {
		LOG.info("setSeal  " + object.usercode + " to " + value);
		object.sealed = value;
// Send to server
		String status = Boolean.TRUE.equals(value) ? "completed": "incomplete";
		
		DomResultStudentScoContext rssc = sscMap.get(object.key);
		DomStudentScoContext sco;
		Success<DomStudentScoContext, Void> succes;
LOG.info("RSSC = " + rssc);
		if(rssc != null) {
			sco = rssc.getStudentSco();
			succes = new StudentScoUpdater(rssc);
		} else {
LOG.info("create scc");
			sco = new DomStudentScoContext();
			sco.setSchoolGroupID(schoolGroupID);
			sco.setScoID(scoContext.getScoContext().getId());
			DomStudent student = studentMap.get(object.key);
LOG.info("Student = " + student);
			sco.setUserID(student.getId());
			rssc = new DomResultStudentScoContext(sco, student);
			sscMap.put(object.key, rssc);
LOG.info(sco.toString());
			succes = new StudentScoUpdater(rssc);
		}
		return SECURED_TEACHER_SCORM_VALUES_MANAGER.setValues(sco, context, Collections.singletonMap(COMPLETION_STATUS, status)).then(succes, failure);
		
	}

	public void sealAllStudents() {
		if(studentItems.isEmpty()) return;
		Collection<StudentItem> items = studentItems.values();		
		final Iterator<StudentItem> iterator = items.iterator();
		final Runnable[] runners = new Runnable[1];
		runners[0] = new Runnable() {

			@Override
			public synchronized void run() {
				while(iterator.hasNext()) {
					StudentItem next = iterator.next();
					if(!Boolean.TRUE.equals(next.sealed))
					{
						LOG.info("Seal "+ next.usercode);
						setSeal(next, Boolean.TRUE).onResolve(runners[0]);
						return;
					}	
				}
				view.updateView(selectedItem, studentItems);
			}};
		int n = 1;
		for(int i = 0; i < n; i++)
			runners[0].run();
	}
	
	private void getAllSeals() {
		if(sscMap.isEmpty()) return;
		Collection<DomResultStudentScoContext> items = sscMap.values();		
		final Iterator<DomResultStudentScoContext> iterator = items.iterator();
		final Runnable[] runners = new Runnable[1];
		runners[0] = new Runnable() {

			@Override
			public synchronized void run() {
				while(iterator.hasNext()) {
					DomResultStudentScoContext next = iterator.next();
					String key = next.getStudentSco().getUserID().getIdString();
					StudentItem item = studentItems.get(key);
					if(item.sealed == null) 
					{
						LOG.info("get Seal "+ item.usercode);
						DomStudentScoContext sco = next.getStudentSco();
						SECURED_TEACHER_SCORM_VALUES_MANAGER.getValues(sco, context, Collections.singleton(COMPLETION_STATUS))
						.then(new Success<Map<String,String>, Void>() {

							@Override
							public Promise<Void> call(
									Promise<Map<String, String>> resolved)
									throws Exception {
								if(item.sealed == null) {
									String value = resolved.getValue().get(COMPLETION_STATUS);
									item.sealed = "completed".equals(value);
								}
								return null;
							}}).onResolve(runners[0]);
						return;
					}	
				}
				view.updateView(selectedItem, studentItems);
			}};
		int n = 1;
		for(int i = 0; i < n; i++)
			runners[0].run();
	}
}
