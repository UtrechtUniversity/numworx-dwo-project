package nl.uu.fi.dwo.account.client.boot.Results;

import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.DomResultPlotMatrix;
import nl.uu.fi.dwo.rest.dom.DomResultTree;
import nl.uu.fi.dwo.rest.dom.ResultTreeCalculator;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomResultCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Controller for ResultsPanel actions.
 *
 * @author Gert van der Plas
 */
class ResultsPanelHandler {

    private static final Logger LOG = Logger.getLogger(ResultsPanelHandler.class.getName());

    private ResultsPanel view;
    private ResultsTeacherController controller = new ResultsTeacherController();
    //model
    private DomResultTree rTree;
    private DomResultPlotMatrix matrix;

    protected enum mode {
        CoursesClasses, //All Courses over all Classes
        CoursesClass, //All Courses over a Class
        CourseClasses, //A Single Course over Class
        CourseClass     //A Course for a Class
    }

    private DomResultCourse course; //null means all courses.
    private DomResultSchoolClass schoolClass; //null means all classes.

    ResultsPanelHandler(ResultsPanel view) {
        this.view = view;
    }

    public void init() {
        LOG.log(Level.INFO, "DwoGlobalVarsState = " + DwoGlobalVars.instance().getState().name());
        course = null;
        schoolClass = null;
        Promise<DomResultsPerTeacher> promResults;
        promResults = controller.getResultsPerTeacher();
        // onSuccess calculate results and show.
        promResults.then(new Success<DomResultsPerTeacher, Void>() {
            @Override
            public Promise<Void> call(Promise<DomResultsPerTeacher> resolved) throws Exception {
                //calculate tree and call plotting
                LOG.log(Level.INFO, "DomResults returned.");
                rTree = new DomResultTree(resolved.getValue());
                LOG.log(Level.INFO, "ResultTree obtained.");
                matrix = ResultTreeCalculator.GetScoreOfTeacherClassesByLeafCourses(rTree);
                LOG.log(Level.INFO, "ResultMatrix obtained.");
                view.plot(matrix);
                LOG.log(Level.INFO, "plotted ResultMatrix.");
                return null;
            }
        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                if (fail instanceof Dwo2Exception) {
                    //translate and display in gui
                } else {
                    //throw directly
                }
            }
        });
    }

    public DomResultPlotMatrix getResults(){
        return getResults(course, schoolClass);
    }
    private DomResultPlotMatrix getResults(DomResultCourse aCourse, DomResultSchoolClass aClass) {
        if (rTree != null) {
            if (aCourse == null && aClass == null) {
                return ResultTreeCalculator.GetScoreOfTeacherClassesByLeafCourses(rTree);
            } else if (aCourse == null && aClass != null) {
                return ResultTreeCalculator.GetScoreOfLeafCoursesByStudentsInClass(rTree, aClass);
            } else if (aCourse != null && aClass == null) {
                return ResultTreeCalculator.GetScoreOfTeacherClassesByLeafCourses(rTree);

            } else if (aCourse != null && aClass != null) {
                return ResultTreeCalculator.GetScoreOfTeacherClassesByLeafCourses(rTree);
            }
        }
        return null; // THough in Java 8 return Optional.
    }
}
