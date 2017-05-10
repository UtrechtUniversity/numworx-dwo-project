package nl.uu.fi.dwo.account.client.boot.Results;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.boot.BootPanel;
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
    int timer =0;

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
        Timer t = new Timer() {
            @Override
            public void run() {
                timer+=10;
                try{
                handler.updateServerResults();
                parent.setStatus("Server OK, updated results for "+ timer+" seconds, updating every 10 seconds.");
                }catch(Exception e){
                parent.setStatus("Server Ofline");
                }
            }
        };
        t.scheduleRepeating(5000);

//                    resultTable.addClickHandler(new ClickHandler() {
//                @Override
//                public void onClick(ClickEvent event) {
//                    if (resultTable.getCellForEvent(event) != null) {
//                        int col = resultTable.getCellForEvent(event).getCellIndex();
//                        int row = resultTable.getCellForEvent(event).getRowIndex();
//                        LOG.log(Level.INFO, "row " + row + ", col " + col + " clicked.");
//                        if (col > 0) {
//                            DomResultScore score = handler.getResultMatrix().gethIndex(col - 1);
//                            if (row == 0 && col > 0 && score instanceof DomResultCourse) {
//                                LOG.log(Level.INFO, "Column label" + col + " clicked.");
//                                DomResultCourse sc = (DomResultCourse) score;
//                                handler.setCourse(sc);
//                                handler.updateResults();
//                                updateView();
//                            }
//                        }
//                    }
//                }
//                    });
//                resultTable.addClickHandler(new ClickHandler() {
//                    @Override
//                    public void onClick(ClickEvent event) {
//                        if (resultTable.getCellForEvent(event) != null) {
//                            int col = resultTable.getCellForEvent(event).getCellIndex();
//                            int row = resultTable.getCellForEvent(event).getRowIndex();
//                            LOG.log(Level.INFO, "row " + row + ", col " + col + " clicked.");
//                            if (row > 0) {
//                                DomResultScore score = handler.getResultMatrix().getvIndex(row - 1);
//                                if (col == 0 && row > 0 && score instanceof DomResultSchoolClass) {
//                                    LOG.log(Level.INFO, "Row label" + row + " clicked.");
//                                    DomResultSchoolClass sc = (DomResultSchoolClass) score;
//                                    handler.setSchoolClass(sc);
//                                    handler.updateResults();
//                                    updateView();
//                                }
//                            }
//                        }
//                    }
//                });

//        tablePanel.setWidget(resultGrid);
    initWidget(uiBinder.createAndBindUi(this));
        handler  = new ResultsPanelHandler(this);
//        resultGrid.setVisible(true);
}

public void init() {
        handler.init();
    }

    public void updateView() {
        plot();
    }

    public void plot() {

        int i = 0;
        int j = 0;
        // column labels
        resultTable.removeAllRows();
        if(handler.getCourse()!=null || handler.getSchoolClass()!=null){
        resultTable.setWidget(0, 0, new Label("[-]\\[-]"));
        }
        for (i = 0; i < handler.getResultMatrix().gethSize(); i++) {
            resultTable.setWidget(0, i + 1, new Label("[+] "+handler.getResultMatrix().gethIndex(i).getLabel()));
            resultTable.getCellFormatter().addStyleName(0, i + 1, "flexTableHeader");
        }
        // row labels
        for (i = 0; i < handler.getResultMatrix().getvSize(); i++) {
            resultTable.setWidget(i + 1, 0, new Label("[+] "+handler.getResultMatrix().getvIndex(i).getLabel()));
            resultTable.getCellFormatter().addStyleName(i + 1, 0, "flexTableHeader");
        }
        // add clickhandler to labels
        resultTable.addClickHandler(new ClickHandler() {
            @Override
        public void onClick(ClickEvent event) {
                if (resultTable.getCellForEvent(event) != null) {
                    int col = resultTable.getCellForEvent(event).getCellIndex();
                    int row = resultTable.getCellForEvent(event).getRowIndex();
                    LOG.log(Level.INFO, "row " + row + ", col " + col + " clicked.");
                    if (col == 0 && row == 0) {
                        LOG.log(Level.INFO, "Reset label (0,0) clicked.");
                        handler.setCourse(null);
                        handler.setSchoolClass(null);
                        handler.updateResults();
                        updateView();

                    } else if (col > 0) {
                        DomResultScore score = handler.getResultMatrix().gethIndex(col - 1);
                        if (row == 0 && col > 0 && score instanceof DomResultCourse) {
                            LOG.log(Level.INFO, "Column label" + col + " clicked.");
                            DomResultCourse sc = (DomResultCourse) score;
                            handler.setCourse(sc);
                            handler.updateResults();
                            updateView();
                        }
                    } else {
                        if (row > 0) {
                            DomResultScore score = handler.getResultMatrix().getvIndex(row - 1);
                            if (col == 0 && row > 0 && score instanceof DomResultSchoolClass) {
                                LOG.log(Level.INFO, "Row label" + row + " clicked.");
                                DomResultSchoolClass sc = (DomResultSchoolClass) score;
                                handler.setSchoolClass(sc);
                                handler.updateResults();
                                updateView();
                            }
                        }
                    }

                }
            }
        }
        );

        for (j = 0; j < handler.getResultMatrix().gethSize(); j++) {
            for (i = 0; i < handler.getResultMatrix().getvSize(); i++) {
                double score = 0.0;
                if (handler.getResultMatrix().getMark(i, j) != null && handler.getResultMatrix().getMark(i, j).getScore() != null) {
                    if (handler.getResultMatrix().getMark(i, j).getScoCount() > 0.0) {
                        score = handler.getResultMatrix().getMark(i, j).getScore() / handler.getResultMatrix().getMark(i, j).getScoCount();
                    } else if (handler.getResultMatrix().getMark(i, j).getStudentScoCount() > 0.0) {
                        score = handler.getResultMatrix().getMark(i, j).getScore();
                    } else {
                        score = 0.0;
                    }
                } else {
                    score = 0.0;
                }
                resultTable.setWidget(i + 1, j + 1, new Label(" " + Double.toString(score)));

            }
        }
        resultTable.setVisible(true);
    }
}
