package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Widget;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentScoDataManager;
import fi.dwo.gwt.lib.rest.CallManagers.StudentScoDataManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolClassListBoxItem;
import nl.uu.fi.dwo.rest.dom.DomResultPlotMatrix;
import nl.uu.fi.dwo.rest.dom.DomResultTree;
import nl.uu.fi.dwo.rest.dom.ResultTreeCalculator;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentSco;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContextFull;

/**
 * Handler for for Login actions.
 *
 * @author Gert van der Plas
 */
public class ScoResultsPresenter {

    private static final Logger LOG = Logger.getLogger(ScoResultsPresenter.class.getName());
    private DwoGlobalVars dwoGlobalVars;
    private EventBus eventBus;
    private List<DomStudentScoContext> resultScoData;
    private DomResultTree resultTree;
    private DomResultPlotMatrix resultMatrix;
    private DomResultStudent selectedStudent;
    private DomResultScoContext scoContext;

    private String[] tableHeaders = {"student name", "total score"};
    private DomSchoolClass schoolClass;
    private Map<String, DomStudent> studentMap;
    private Map<String, StudentItem> studentItems;
    private Map<String, DomSchoolClass> schoolClassMap;
    private List<SchoolClassListBoxItem> schoolClassItems;
    private Display view;
    private int requests = 0;
// Voor het Frame:
	private Promise<String> launchData;
	private Promise<DomStudentScoContextFull> scormVars1;
	// private Promise<DomStudenScoDataFull> scormVars2;

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
        public double[] subScores;

        public StudentItem(String aKey, String aFirstName, String anInsertion, String aFamilyName, String aUsercode, Double aScore, double[] aSubScores) {
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
// fetch JSONValue of scocontext (once)
        final DomScoContext sco = scoContext.getScoContext();
// FIXME WRONG MANAGER
        StudentScoDataManager manager = new SecuredStudentScoDataManager();
        DomContext context = new DomContext();
        context.setDomHasRole(dwoGlobalVars.getActiveSchoolRoleAndClass().getHasRole());
		DomDwoProfile profile = dwoGlobalVars.getProfile().getValue();
		launchData = manager.getJSONLaunchDataBytes(sco, profile, context).map(new Function<JSONValue, String>() {

			@Override
			public String apply(JSONValue t) {
				return t.toString();
			}
		});

        
        
        Map<String, DomResultStudentSco> sscMap = new HashMap<String, DomResultStudentSco>(scoContext.getChildren().size());
        for (DomResultStudentSco ss : scoContext.getChildren().values()) {
            sscMap.put(ss.getStudentSco().getUserID().getIdString(), ss);
        }
        selectedStudent = aStudent;
        resultMatrix = ResultTreeCalculator.GetScoreOfActivitiesByStudentsInSco(resultTree, aScoContext);
        studentItems = new HashMap<String, StudentItem>(resultMatrix.getvSize());
        StudentItem selectedItem=null;
        //TODO Wim, make the promise to fetch the first scoData and StudentScoData then when resolved execute the code block below
        for (int i = 0; i < resultMatrix.getvSize(); i++) {
            DomResultStudent s = (DomResultStudent) resultMatrix.getvIndex(i);
            // fetch and insert score here.
            Double score = null;
            if (sscMap.containsKey(s.getStudent().getId().getIdString())) {
                score = sscMap.get(s.getStudent().getId().getIdString()).getStudentSco().getScore();
            }
            StudentItem si = new StudentItem(s.getStudent().getId().getIdString(), s.getStudent().getGivenName(),
                    s.getStudent().getInsertion(), s.getStudent().getFamilyName(), s.getStudent().getUserName(), score, new double[0]);
            if(selectedStudent.getStudent().getId().getIdString().equals(si.key) && si.score!=null){
                selectedItem = si;
            }
            studentItems.put(s.getStudent().getId().getIdString(), si);
        }
        LOG.log(Level.FINE, "nr students:" + resultMatrix.getvSize());
        view.updateView(selectedItem,studentItems);
// fetch ScormValues of student (for each student)
        DomStudentScoContext ssc = sscMap.get(selectedItem.key).getStudentSco();
        DomStudentScoContextFull full = new DomStudentScoContextFull();
        full.setId(ssc.getId());
        full.setScoID(ssc.getScoID());
        full.setUserID(ssc.getUserID());
        full.setSchoolGroupID(ssc.getSchoolGroupID());
        full.setScore(ssc.getScore());
        full.setLocation("0");
// etc...
        scormVars1 = Promises.resolved(full); // TODO better implementation
// if both, updateFrame for this sco.
		Promises.all(launchData, scormVars1).onResolve(new Runnable() {

			@Override
			public void run() {
				view.updateFrame(sco);				
			}});
		
        
    }

    public void select(StudentItem item) {
        //selected item in single select table
        //show the new student's studenscodata in the window api if the score is not null
        //otherwise show message or refuse selection.
    }

    //function to be called from the view in the future when subscores are supported.
//    public void selected(int row, int col) {
//        //selected item in single select table
//        //show the new student's studenscodata in the window api.
//    }
    /**
     *
     * @param key een CMI variable
     * @return
     */
    public String getScormAPIValue(String key) {
    	if("cmi.launch_data".equals(key))
    		return launchData.getValue();
    	if("cmi.completion_status".equals(key)) {
    		return "completed";
    	}
    	if( "cmi.score.raw".equals(key)) {
    		return String.valueOf(scormVars1.getValue().getScore());
    	}
    	if ("cmi.location".equals(key)) {
    		return scormVars1.getValue().getLocation();
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
}
