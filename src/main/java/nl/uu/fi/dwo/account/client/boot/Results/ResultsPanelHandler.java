package nl.uu.fi.dwo.account.client.boot.Results;

import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.DomResultPlotMatrix;
import nl.uu.fi.dwo.rest.dom.ResultTree;
import nl.uu.fi.dwo.rest.dom.ResultTreeCalculator;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
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
    private ResultTree rTree;
    private DomResultPlotMatrix matrix;
    
    ResultsPanelHandler(ResultsPanel view) {
        this.view = view;
        init();
    }

    public void init() {
        Promise<DomResultsPerTeacher> promResults;
        promResults= controller.getResultsPerTeacher();
        // onSuccess calculate results and show.
        promResults.then(new Success<DomResultsPerTeacher, Void>() {
                @Override
                public Promise<Void> call(Promise<DomResultsPerTeacher> resolved) throws Exception {
                    //calculate tree and call plotting
                    rTree = new ResultTree(resolved.getValue());
                    matrix  = ResultTreeCalculator.GetScoreOfTeacherClassesByLeafCourses(rTree);
                    view.plot(matrix);
                    return null;
                }
            },
                    new Failure() {
                @Override
                public void fail(Promise<?> resolved) throws Exception {
                    Throwable fail = resolved.getFailure();
                    if(fail instanceof Dwo2Exception){
                        //translate and display in gui
                    }else{
                        //throw directly
                    }
                }
            });        
    }

}
