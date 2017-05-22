package nl.uu.fi.dwo.account.client.boot.Results;

import com.google.gwt.i18n.client.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.DomResultPlotMatrix;
import nl.uu.fi.dwo.rest.dom.DomResultTree;
import nl.uu.fi.dwo.rest.dom.ResultTreeCalculator;
import nl.uu.fi.dwo.rest.dom.entities.DomResultCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/**
 * Controller for ResultsView actions.
 *
 * @author Gert van der Plas
 */
class ResultsPresenter {

    private static final Logger LOG = Logger.getLogger(ResultsPresenter.class.getName());

    private ResultsView view;
    private ResultsService resultService = new ResultsService();
    //model
    private DomResultTree rTree;
    private DomResultPlotMatrix resultMatrix;

    /**
     * @param row the course to set
     */
    public void selectRowAndCol(int row, int col) {
        if (row != 0 && col != 0 && schoolClass != null && course != null) {
            LOG.log(Level.INFO,"selected a student sco I hope "+resultMatrix.getMark(row, col).getLabel());
        }
        if (row == 0 && col == 0 && (schoolClass != null || course != null)) {
            //zoom all out
            schoolClass = null;
            course = null;
            return;
        }
        if (row != 0 && schoolClass == null && resultMatrix.getvIndex(row - 1) instanceof DomResultSchoolClass) {
            schoolClass = (DomResultSchoolClass) resultMatrix.getvIndex(row - 1);
        } else if (row != 0 && schoolClass != null) {
            schoolClass = null;
        }

        if (col != 0 && course == null && resultMatrix.gethIndex(col - 1) instanceof DomResultCourse) {
            course = (DomResultCourse) resultMatrix.gethIndex(col - 1);
        } else if (col != 0 && course != null) {
            course = null;
        }
    }

    /**
     * @return the resultMatrix
     */
    public DomResultPlotMatrix getResultMatrix() {
        return resultMatrix;
    }

    protected enum mode {
        CoursesClasses, //All Courses over all Classes
        CoursesClass, //All Courses over a Class
        CourseClasses, //A Single Course over Class
        CourseClass     //A Course for a Class
    }

    private DomResultCourse course = null; //null means all courses.
    private DomResultSchoolClass schoolClass = null; //null means all classes.

    ResultsPresenter(ResultsView view) {
        this.view = view;
    }

    public void init() {
        LOG.log(Level.INFO, "DwoGlobalVarsState = " + DwoGlobalVars.instance().getState().name());
        course = null;
        schoolClass = null;
        Promise<DomResultsPerTeacher> promResults;
        promResults = resultService.getResultsPerTeacher();
        // onSuccess calculate results and show.
        promResults.then(new Success<DomResultsPerTeacher, Void>() {
            @Override
            public Promise<Void> call(Promise<DomResultsPerTeacher> resolved) throws Exception {
                //calculate tree and call plotting
                LOG.log(Level.INFO, "DomResults returned.");
                rTree = new DomResultTree(resolved.getValue());
                LOG.log(Level.INFO, "ResultTree obtained.");
                resultMatrix = ResultTreeCalculator.GetScoreOfTeacherClassesByLeafCourses(rTree);
                LOG.log(Level.INFO, "ResultMatrix obtained.");
                plotResultsEvent();
                LOG.log(Level.INFO, "plotted ResultMatrix.");
                return null;
            }
        },
                new Failure() {
            @Override
            public void fail(Promise<?> resolved) throws Exception {
                Throwable fail = resolved.getFailure();
                if (fail instanceof Dwo2Exception) {
                    LOG.log(Level.SEVERE, fail.getMessage());
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    //throw directly
                }
            }
        });
    }

    public void updateServerResults() {
        Promise<DomResultsPerTeacher> promResults;
        promResults = resultService.getResultsPerTeacher();
        // onSuccess calculate results and show.
        promResults.then(new Success<DomResultsPerTeacher, Void>() {
            @Override
            public Promise<Void> call(Promise<DomResultsPerTeacher> resolved) throws Exception {
                //calculate tree and call plotting
                LOG.log(Level.INFO, "DomResults returned.");
                rTree = new DomResultTree(resolved.getValue());
                LOG.log(Level.INFO, "ResultTree obtained.");
                resultMatrix = ResultTreeCalculator.GetScoreOfTeacherClassesByLeafCourses(rTree);
                LOG.log(Level.INFO, "ResultMatrix obtained.");
                plotResultsEvent();
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

    public void plotResultsEvent() {
        resultMatrix = getResults(course, schoolClass);
        String[][] data = buildPlotMatrix();
        view.plot(data.length, data[0].length, data);
    }

    public void updateResults() {
        resultMatrix = getResults(course, schoolClass);
    }

    public DomResultPlotMatrix getResults() {
        return resultMatrix;
    }

    private DomResultPlotMatrix getResults(DomResultCourse aCourse, DomResultSchoolClass aClass) {
        DomResultPlotMatrix result = null;
        if (rTree != null) {
            if (aCourse == null && aClass == null) {
                result = ResultTreeCalculator.GetScoreOfTeacherClassesByLeafCourses(rTree);
            } else if (aCourse == null && aClass != null) {
                result = ResultTreeCalculator.GetScoreOfLeafCoursesByStudentsInClass(rTree, aClass);
            } else if (aCourse != null && aClass == null) {
                result = ResultTreeCalculator.GetScoreOfTeacherClassesByActivitiesOfCourse(rTree, aCourse);
            } else if (aCourse != null && aClass != null) {
                result = ResultTreeCalculator.GetScoreOfActivitiesOfCourseByStudentsInClass(rTree, aCourse, aClass);
            }
        }
        return result; // Though in Java 8 return Optional.
    }

    public String[][] buildPlotMatrix() {

        int i = this.getResultMatrix().getvSize();
        int j = this.getResultMatrix().gethSize();
        String[][] data = new String[i + 1][j + 1];

        // column labels
        String nulLabel = "";
        if (schoolClass != null || course != null) {
            nulLabel += "[-]\\[-] ";
        }
        if (schoolClass != null) {
            nulLabel += schoolClass.getLabel();
        } else {
            nulLabel += "classes";
        }
        nulLabel += "\\";
        if (course != null) {
            nulLabel += course.getLabel();
        } else {
            nulLabel += "courses";
        }
        data[0][0] = nulLabel;

        for (i = 0; i < getResultMatrix().gethSize(); i++) {
            String action = "[+] ";
            if (course != null) {
                action = "[-] ";
            }
            data[0][i + 1] = "" + action + getResultMatrix().gethIndex(i).getLabel();
        }

        // row labels
        for (i = 0; i < getResultMatrix().getvSize(); i++) {
            String action = "[+] ";
            if (schoolClass != null) {
                action = "[-] ";
            }
            data[i + 1][0] = "" + action + getResultMatrix().getvIndex(i).getLabel();
        }

        for (j = 0;
                j < getResultMatrix()
                .gethSize(); j++) {
            for (i = 0; i < getResultMatrix().getvSize(); i++) {
                double score = 0.0;
                if (getResultMatrix().getMark(i, j) != null && getResultMatrix().getMark(i, j).getScore() != null) {
                    if (getResultMatrix().getMark(i, j).getScoCount() > 0.0) {
                        score = getResultMatrix().getMark(i, j).getScore();
                    } else if (getResultMatrix().getMark(i, j).getStudentScoCount() > 0.0) {
                        score = getResultMatrix().getMark(i, j).getScore();
                    } else {
                        score = 0.0;
                    }
                } else {
                    score = 0.0;
                }
                String color = "red";
                if (score > 10.0 && score < 60.0) {
                    color = "orange";
                } else if (score >= 60) {
                    color = "green";
                }
                String prefix;
                if (score > 0) {
                    int r, g, b;
                    b = 0;
                    g = (int) (255 * (score / 50));
                    r = (int) (255 * (1 - (score - 50) / 50));
                    prefix = "<div style=\"text-align: right; background:rgb(" + r + "," + g + "," + b + ");\">";
                } else {
                    prefix = "<div style=\"text-align: right; overflow auto;\">"; // use default of style
                }
                String formattedScore = NumberFormat.getFormat("0.0").format(score);
                data[i + 1][j + 1] = prefix + formattedScore + "</div>";

            }
        }
        return data;
    }
}
