package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolClassListBoxItem;
import nl.uu.fi.dwo.rest.dom.DomResultPlotMatrix;
import nl.uu.fi.dwo.rest.dom.DomResultTree;
import nl.uu.fi.dwo.rest.dom.ResultTreeCalculator;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudentSco;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;

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

    public interface Display {

        Widget asWidget();

        void clear();

        void init();

        void updateView(Map<String, StudentItem> data);
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
        Map<String, DomResultStudentSco> sscMap = new HashMap<String, DomResultStudentSco>(scoContext.getChildren().size());
        for (DomResultStudentSco ss : scoContext.getChildren().values()) {
            sscMap.put(ss.getStudentSco().getUserID().getIdString(), ss);
        }
        selectedStudent = aStudent;
        resultMatrix = ResultTreeCalculator.GetScoreOfActivitiesByStudentsInSco(resultTree, aScoContext);
        studentItems = new HashMap<String, StudentItem>(resultMatrix.getvSize());
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
            studentItems.put(s.getStudent().getId().getIdString(), si);
        }
        LOG.log(Level.FINE, "nr students:" + resultMatrix.getvSize());
        view.updateView(studentItems);

    }

    public void select(StudentItem item) {
        //selected item in single select table
        //show the new student's studenscodata in the window api.
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
        return "";
    }

    /**
     *
     * @param key een CMI variable
     * @param value zijn nieuwe waarde
     * @return
     */
    public String setScormAPIValue(String key, String value) {
        return "";
    }
}
