package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.gui.DialogEvent;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.MainPresenter;
import nl.uu.fi.dwo.lms.gwtclient.gwt.SwitchViewEvent;
import nl.uu.fi.dwo.rest.dom.DomResultPlotMatrix;
import nl.uu.fi.dwo.rest.dom.DomResultTree;
import nl.uu.fi.dwo.rest.dom.ResultTreeCalculator;
import nl.uu.fi.dwo.rest.dom.entities.DomResultCourseInClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;
import nl.uu.fi.dwo.rest.dom.entities.DomResultStudent;
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
public class ResultsPresenter {

    private static final Logger LOG = Logger.getLogger(ResultsPresenter.class.getName());

    private final EventBus eventBus;
    private final DwoGlobalVars dwoGlobalVars;

    private Display view;
    private ResultsService resultService;
    //model
    private DomResultTree resultTree;
    private DomResultPlotMatrix resultMatrix;
    private DomResultCourseInClass course = null; //null means all courses.
    private DomResultSchoolClass schoolClass = null; //null means all classes.

    public interface Display {

        Widget asWidget();

        void clear();

        void plot(ResultPlot data, boolean zoomedClass, boolean zoomedCourse);

        void setEmptyTableMessage();

        void setLoadingTableMessage();

    }

    protected enum mode {
        CoursesClasses, //All Courses over all Classes
        CoursesClass, //All Courses over a Class
        CourseClasses, //A Single Course over Class
        CourseClass     //A Course for a Class
    }

    public class ResultPlot {

        private List<List<ResultItem>> marks = null; //row, col order.
        private ResultItem[] vIndex; //uses label property for display
        private ResultItem[] hIndex; //uses label property for display    

        /**
         * @return the marks
         */
        public List<List<ResultItem>> getMarks() {
            return marks;
        }

        /**
         * @param marks the marks to set
         */
        public void setMarks(List<List<ResultItem>> aMarks) {
            this.marks = aMarks;
        }

        /**
         * @return the vIndex
         */
        public ResultItem[] getvIndex() {
            return vIndex;
        }

        /**
         * @param vIndex the vIndex to set
         */
        public void setvIndex(ResultItem[] vIndex) {
            this.vIndex = vIndex;
        }

        /**
         * @return the hIndex
         */
        public ResultItem[] gethIndex() {
            return hIndex;
        }

        /**
         * @param hIndex the hIndex to set
         */
        public void sethIndex(ResultItem[] hIndex) {
            this.hIndex = hIndex;
        }

    }

    public class ResultItem {
        public int row;
        public String label; //unique
        public String toolTip;
        public Double score;

        public ResultItem(int aRow, String aLabel, Double aScore) {
            row = aRow;
            label = aLabel;
            score = aScore;
        }

        public ResultItem(String aLabel, Double aScore, String aToolTip) {
            label = aLabel;
            score = aScore;
            toolTip = aToolTip;
        }
    }

    public ResultsPresenter(EventBus anEventBus, DwoGlobalVars aDwoGlobalVars) {
        eventBus = anEventBus;
        dwoGlobalVars = aDwoGlobalVars;
        resultService = new ResultsService(dwoGlobalVars);
       
    }


    public void init() {
        view.clear();
        LOG.log(Level.INFO, "DwoGlobalVarsState = " + dwoGlobalVars.getState().name());
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
                resultTree = new DomResultTree(resolved.getValue());
                LOG.log(Level.INFO, "ResultTree obtained.");
                resultMatrix = ResultTreeCalculator.GetScoreOfTeacherClassesByLeafCourses(resultTree);
                LOG.log(Level.INFO, "ResultMatrix obtained.");
                view.setEmptyTableMessage();
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
                    eventBus.fireEvent(new DialogEvent((Dwo2Exception) fail));
                } else {
                    LOG.log(Level.SEVERE, fail.getMessage());
                    eventBus.fireEvent(new DialogEvent(fail.getMessage()));
                    //throw directly
                }
            }
        });
    }

    public void setView(Display aView) {
        view = aView;
    }

    /**
     * @param row the course to set
     */
    public void selectColumnZoom(int col) {
        switch (col) {
            case 0:
                schoolClass = null;
                break;
            default:
                if (course == null) {
                    course = (DomResultCourseInClass) resultMatrix.gethIndex(col - 1);
                } else {
                    course = null;
                }
        }
        resultMatrix = calculateResults(course, schoolClass);
        ResultPlot plotData = buildPlotMatrix(resultMatrix);
        view.plot(plotData, (schoolClass != null), (course != null));
    }

    /**
     * @param row the course to set
     */
    /**
     * @param row the course to set
     */
    public void selectRowAndCol(int row, int col) {
        //col = 0 indicates clicked in student/class column
        if (col == 0 && schoolClass == null && resultMatrix.getvIndex(row) instanceof DomResultSchoolClass) {
            //if(col==0 && schoolClass ==null select schoolclass
            schoolClass = (DomResultSchoolClass) resultMatrix.getvIndex(row);
        } else if (col == 0 && schoolClass != null) {
            //if(col==0 && schoolClass ==null set schoolclass = null
            schoolClass = null;
        } else if (col != 0 && schoolClass != null && course != null) {
            //open sco
            LOG.log(Level.INFO, "selected a student sco for " + resultMatrix.getMark(row, col - 1).getLabel() + " with score " + resultMatrix.getMark(row, col - 1).getScore());
            //send event to show studentSco Context en Data.
            DomResultStudent rs = (DomResultStudent) resultMatrix.getvIndex(row);
            DomResultScoContext ssc = (DomResultScoContext) resultMatrix.gethIndex(col - 1);
            eventBus.fireEvent(new SwitchViewEvent(SwitchViewEvent.SelectedView.SCORESULTS, resultTree, ssc, rs, schoolClass.getSchoolClass()));
            return;
//        }else{
//        //if(col!=0 && select schoolclass and course
//            course = (DomResultCourseInClass) resultMatrix.gethIndex(col - 1);
//            schoolClass = (DomResultSchoolClass) resultMatrix.getvIndex(row);
        }
        resultMatrix = calculateResults(course, schoolClass);
        ResultPlot plotData = buildPlotMatrix(resultMatrix);
        view.plot(plotData, (schoolClass != null), (course != null));
    }

    /**
     * @return the resultMatrix
     */
    public DomResultPlotMatrix getResultMatrix() {
        return resultMatrix;
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
                resultTree = new DomResultTree(resolved.getValue());
                LOG.log(Level.INFO, "ResultTree obtained.");
                resultMatrix = ResultTreeCalculator.GetScoreOfTeacherClassesByLeafCourses(resultTree);
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
        resultMatrix = calculateResults(course, schoolClass);
        ResultPlot plotData = buildPlotMatrix(resultMatrix);
        view.plot(plotData, (schoolClass != null), (course != null));
    }

    public void updateResults() {
        resultMatrix = calculateResults(course, schoolClass);
    }

    public DomResultPlotMatrix getResults() {
        return resultMatrix;
    }

    private DomResultPlotMatrix calculateResults(DomResultCourseInClass aCourse, DomResultSchoolClass aClass) {
        DomResultPlotMatrix result = null;
        if (resultTree != null) {
            if (aCourse == null && aClass == null) {
                result = ResultTreeCalculator.GetScoreOfTeacherClassesByLeafCourses(resultTree);
            } else if (aCourse == null && aClass != null) {
                result = ResultTreeCalculator.GetScoreOfLeafCoursesByStudentsInClass(resultTree, aClass);
            } else if (aCourse != null && aClass == null) {
                result = ResultTreeCalculator.GetScoreOfTeacherClassesByActivitiesOfCourse(resultTree, aCourse);
            } else if (aCourse != null && aClass != null) {
                result = ResultTreeCalculator.GetScoreOfActivitiesOfCourseByStudentsInClass(resultTree, aCourse, aClass);
            }
        }
        return result; // Though in Java 8 return Optional.
    }

    public ResultPlot buildPlotMatrix(DomResultPlotMatrix matrix) {
        ResultPlot data = new ResultPlot();

        //set column headers
        ResultItem[] hHeaders = new ResultItem[matrix.gethSize() + 1];
        List<ResultItem> colHeaders = new ArrayList<ResultItem>(matrix.gethSize() + 1);
        colHeaders.add(null);
        hHeaders[0] = (schoolClass == null) ? new ResultItem(-1,"schoolclasses", null) : new ResultItem(-1,schoolClass.getLabel(), null);
        for (int i = 0; i < matrix.gethSize(); i++) {
            DomResultScore score = matrix.gethIndex(i);
            hHeaders[i + 1] = new ResultItem(-1,score.getLabel(), score.getScore());
            colHeaders.add(new ResultItem(-1, score.getLabel(), score.getScore()));
        }
        data.sethIndex(hHeaders);

        ResultItem[] vHeaders = new ResultItem[matrix.getvSize()];
        List<ResultItem> rowHeaders = new ArrayList<ResultItem>(matrix.getvSize() + 1);
        rowHeaders.add(null);
        for (int i = 0; i < matrix.getvSize(); i++) {
            DomResultScore score = matrix.getvIndex(i);
            ResultItem item = new ResultItem(i,score.getLabel(), score.getScore());
            if (score instanceof DomResultStudent) {
                String toolTip = ((DomResultStudent) score).getStudent().getUserName();
                item.toolTip = toolTip;
            }
            vHeaders[i] = item;
            rowHeaders.add(item);
        }
        data.setvIndex(vHeaders);

        // built row, col order.
        List<List<ResultItem>> marks = new ArrayList<List<ResultItem>>(matrix.getvSize());
        //    marks.add(colHeaders);
        for (int i = 0; i < matrix.getvSize(); i++) {
            marks.add(new ArrayList<ResultItem>(matrix.gethSize()));
            for (int j = 0; j < matrix.gethSize(); j++) {
                if (j == 0) {
                    marks.get(i).add(rowHeaders.get(i + 1));
                }
                DomResultScore score = matrix.getMark(i, j); //row, col
                if (score == null) {
                    marks.get(i).add(null);
                } else {
                    marks.get(i).add(new ResultItem(i, score.getLabel(), score.getScore()));
                }
            }
        }
        int s = marks.size();
        data.setMarks(marks);
        return data;
    }

    String getExportString() {
        ResultPlot plot = buildPlotMatrix(resultMatrix);
        StringBuilder sb = new StringBuilder();
        sb.append("index");
        for (ResultItem hItem : plot.hIndex) {
        sb.append('\t');
            sb.append(hItem.label);
        }
        sb.append('\n');
        for (int r = 0; r < plot.vIndex.length; r++) {
            sb.append(plot.vIndex[r].label);
            for (int c = 0; c < plot.hIndex.length; c++) {
            sb.append('\t');
                ResultItem item = plot.marks.get(r).get(c);
                if (item != null && item.score != null) {
                    sb.append(item.score);
                } else {
                    sb.append("0");
                }

            }
            sb.append('\n');
        }

        return sb.toString();
    }

    void finnishedExport(){
        eventBus.fireEvent(new DialogEvent("Exported tab separated values to clipboard."));
    }
}
