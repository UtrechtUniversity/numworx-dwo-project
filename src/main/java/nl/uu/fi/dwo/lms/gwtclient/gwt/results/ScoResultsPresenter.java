package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.DomResultPlotMatrix;
import nl.uu.fi.dwo.rest.dom.DomResultTree;
import nl.uu.fi.dwo.rest.dom.ResultTreeCalculator;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudent;
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
    private int selectedIndex = 0;
    private List<DomStudentScoContext> resultScoData;
    private DomResultTree resultTree;
    private DomResultPlotMatrix resultMatrix;
    private DomResultStudent selectedStudent;
    private DomResultScoContext scoContext;
//    private DomSchoolClass selectedSchoolClass;
//    private DomScoContext selectedDomScoContext;

    private Display view;

    /**
     * @param view the view to set
     */
    public void setView(Display view) {
        this.view = view;
    }

    void goBackToResults() {
        eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.RESULTS));
    }

    public interface Display {
        Widget asWidget();
        void clear();
        void init();
        void updateView(int height, int width, String[][] data);
    }

    public ScoResultsPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
    }

    /**
     * 
     * @param aResultTree
     * @param aScoContext A ScoContext for a schoolClass and Student
     * @param aSelectedStudent A studentSco inside the resultTree
     */
    public void init(DomResultTree aResultTree, DomResultScoContext aScoContext, DomResultStudent aStudent) { //DomScoContext aSelectedScoContext, DomSchoolClass aSelectedSchoolClass,         
        resultTree = aResultTree;
        scoContext=aScoContext;
        selectedStudent = aStudent;
        resultMatrix = ResultTreeCalculator.GetScoreOfActivitiesByStudentsInSco(resultTree, aScoContext);
        LOG.log(Level.FINE,"nr students:"+resultMatrix.getvSize());
        //aStudent is the selected student in the ScoContext;
        
    }

    /**
     * @param row the course to set
     */
    public void selectRow(int row) {
        if (row != -1) {
            selectedIndex = row;
            return;
        }
    }

    private String[][] buildPlotData() {
        String[][] data = new String[resultScoData.size()+1][1];
//        data[0][0] = "School";//<div style=\"text-align: left; background-color: #aaaaaa; padding: 2px; overflow auto;\">School</div>";
//        int i = 1;
//        selectedIndex=0;
//        for (DomSchoolRoleAndClassV2 srac : resultScoData) {
//            data[i][0] = srac.getSchool().getSchoolName();
//            if (srac.getHasRole().getId().equals(srac.getHasRole().getId())) {
//                selectedIndex = i;
//            }
//            i++;
//        }
        return data;
    }

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
