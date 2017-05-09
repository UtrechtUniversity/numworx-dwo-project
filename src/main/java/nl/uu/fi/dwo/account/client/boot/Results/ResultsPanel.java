package nl.uu.fi.dwo.account.client.boot.Results;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.boot.BootPanel;
import nl.uu.fi.dwo.rest.dom.DomResultPlotMatrix;
import nl.uu.fi.dwo.rest.dom.entities.DomResultCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultScore;

/**
 *
 *
 * @author G.A.J. van der Plas
 */
public class ResultsPanel extends Composite {

    private static final Logger LOG = Logger.getLogger(ResultsPanel.class.getName());

    interface MyUiBinder extends UiBinder<Widget, ResultsPanel> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    public interface Style extends CssResource {

        String panel();

        String tableCelleven();

        String tableCellodd();
    }
    private ResultsPanelHandler handler;
    private ResultsTeacherController control;

    //initial gridsize
    final int yInitialGridSize = 12;
    final int xInitialGridSize = 6;
    int xOffset = 0;
    int yOffset = 0;

    @UiField(provided = true)
    FlexTable resultTable = new FlexTable();

    ListDataProvider<DomResultScore> dataProvider = new ListDataProvider<DomResultScore>();

    private BootPanel parent;

    public void setParent(BootPanel aParent) {
        parent = aParent;
    }

    /**
     * @return the parent
     */
    public BootPanel getParent() {
        return parent;
    }

    public ResultsPanel() {
        LOG.log(Level.INFO, "Grid size:" + resultTable.getRowCount() + "x.");
        resultTable.setWidget(0, 0, new Label("class\\course"));
        resultTable.getCellFormatter().addStyleName(0, 0, "flexTableHeader");

//        tablePanel.setWidget(resultGrid);
        initWidget(uiBinder.createAndBindUi(this));
        handler = new ResultsPanelHandler(this);
//        resultGrid.setVisible(true);
    }

    public void init() {
        handler.init();
    }

    public void updateView() {
        handler.init();
    }

    public void plot() {
//        resultTable.setVisible(false);
        final ResultsPanelHandler myHandler = handler;
        
        int i = 0;
        int j = 0;
        // column labels
//        resultTable.removeAllRows();
        for (i = 0; i < myHandler.getResultMatrix().gethSize(); i++) {
            resultTable.setWidget(0, i + 1, new Label(myHandler.getResultMatrix().gethIndex(i).getLabel()));
            resultTable.getCellFormatter().addStyleName(0, i + 1, "flexTableHeader");
            // add clickhandler to labels
            resultTable.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    int col = resultTable.getCellForEvent(event).getCellIndex();
                    int row = resultTable.getCellForEvent(event).getRowIndex();
                        LOG.log(Level.INFO, "row "+row+", col " + col + " clicked.");
                    DomResultScore score = myHandler.getResultMatrix().gethIndex(col);
                    if (row == 0 && col >0 && score instanceof DomResultCourse) {
                        LOG.log(Level.INFO, "Column label" + col + " clicked.");
                        DomResultCourse sc = (DomResultCourse) score;
                        handler.setCourse(sc);
                        handler.updateResults();
                        plot();
                    }
                }
            }
            );
            // row labels
            for (i = 0; i < myHandler.getResultMatrix().getvSize(); i++) {
                resultTable.setWidget(i + 1, 0, new Label(myHandler.getResultMatrix().getvIndex(i).getLabel()));
                resultTable.getCellFormatter().addStyleName(i + 1, 0, "flexTableHeader");
                // add clickhandler to labels
                resultTable.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        int col = resultTable.getCellForEvent(event).getCellIndex();
                        int row = resultTable.getCellForEvent(event).getRowIndex();
                        LOG.log(Level.INFO, "row "+row+", col " + col + " clicked.");
                        DomResultScore score = myHandler.getResultMatrix().getvIndex(row);
                        if (col == 0 && row >0 && score instanceof DomResultSchoolClass) {
                            LOG.log(Level.INFO, "Row label" + row + " clicked.");
                            DomResultSchoolClass sc = (DomResultSchoolClass) score;
                            handler.setSchoolClass(sc);
                            handler.updateResults();
                            plot();
                        }
                    }
                });
            }

            for (j = 0; j < myHandler.getResultMatrix().gethSize(); j++) {
                for (i = 0; i < myHandler.getResultMatrix().getvSize(); i++) {
                    double score = 0.0;
                    if (myHandler.getResultMatrix().getMark(i, j).getScore() != null) {
                        if (myHandler.getResultMatrix().getMark(i, j).getScoCount() > 0.0) {
                            score = myHandler.getResultMatrix().getMark(i, j).getScore() / myHandler.getResultMatrix().getMark(i, j).getScoCount();
                        } else if (myHandler.getResultMatrix().getMark(i, j).getStudentScoCount() > 0.0) {
                            score = myHandler.getResultMatrix().getMark(i, j).getScore();
                        } else {
                            score = 0.0;
                        }
                    } else {
                        score = 0.0;
                    }
                    resultTable.setWidget(i + 1, j + 1, new Label(" " + Double.toString(score)));

                }
            }
        }
        resultTable.setVisible(true);
    }
}
