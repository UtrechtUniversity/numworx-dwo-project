package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.icons.DwoResources;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.SchoolclassesPresenter;

/**
 * Shows the students results of activities individually or grouped by school
 * class or leave course.
 *
 * @author G.A.J. van der Plas
 */
public class ResultsView extends Composite implements ResultsPresenter.Display {

    private static final Logger LOG = Logger.getLogger(ResultsView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, ResultsView> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField(provided = true)
    DataGrid dataGrid;
//    @UiField(provided = true)            
//    CellList dataGrid;
    @UiField(provided = true)
    SimplePager pager;
    @UiField
    Button exportBtn;

    private static final DwoResources resources = GWT.create(DwoResources.class);
    Image emptyImage = new Image(resources.emptyIcon());
    Image loadingImage = new Image(resources.loadingIcon());

//    
//    public interface Style extends CssResource {
//
//        String panel();
//        String tableCelleven();
//        String tableCellodd();
//    }
    private int timer = 0;

    private ResultsPresenter resultsPresenter;

    private ListDataProvider<List<ResultsPresenter.ResultItem>> dataProvider = new ListDataProvider<List<ResultsPresenter.ResultItem>>();

    public class ResultData {

        int width;
        int height;
        String[][] data; //height, width
    }

    public ResultsView(ResultsPresenter rp) {
        resultsPresenter = rp;
        resultsPresenter.setView(this);
        dataGrid = new DataGrid<List<ResultsPresenter.ResultItem>>();
        dataProvider.addDataDisplay(dataGrid);
//        dataGrid.setSkipRowHoverCheck(true);
        dataGrid.setKeyboardSelectionPolicy(com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);

        Timer t = new Timer() {
            @Override
            public void run() {
                timer += 1;
//                try {
//                    if (parent.getAutoUpdateResults().getValue()) {
//                        //handler.updateServerResults();
//                        parent.setStatus("Server OK, updating results every 5 seconds.");
//                    } else {
//                        parent.setStatus("Refresh paused.");
//                    }
//                } catch (Exception e) {
//                    parent.setStatus("Server Offline");
//                }
            }
        };
        t.scheduleRepeating(5000);
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);
        pager.setPageSize(dataGrid.getPageSize());
        initWidget(uiBinder.createAndBindUi(this));
        clear();

    }

    @Override
    public void clear() {
        dataGrid.setEmptyTableWidget(loadingImage);
        dataProvider.getList().clear();
    }

    public void init() {
//        for (int i = 0; i < dataGrid.getColumnCount(); i++) {
//            dataGrid.removeColumn(0);
//        }
        clear();
        //resultsPresenter.plotResultsEvent();
    }

    public void plot(ResultsPresenter.ResultPlot data) {
        for (int i = dataGrid.getColumnCount() - 1; i >= 0; i--) {
            dataGrid.removeColumn(0);
        }

        //create schoolclass/student column
        TextCell cell = new TextCell();
        //schoolclass/student
//        if(data.getvIndex().length>0){
//        Column<List<ResultsPresenter.ResultItem>, String> value = new Column<List<ResultsPresenter.ResultItem>, String>(cell) {
//            @Override
//            public String getValue(List<ResultsPresenter.ResultItem> object) {
//                //return "test" ;
//                int r=3+timer;
//                LOG.log(Level.INFO, ""+r);
//                return (object!=null && object.get(0)!=null && object.get(0).label!=null) ? object.get(0).label : "unknown class";
//            }
//        };
//        dataGrid.addColumn(value, "zoom out");
//        LOG.log(Level.INFO, "adding schoolclass/student column");
//        }
        for (int i = 0; i < data.gethIndex().length; i++) {
            //create column
            cell = new TextCell();
            int colVal = i;
            //givenName
            Column<List<ResultsPresenter.ResultItem>, String> dynValue = new Column<List<ResultsPresenter.ResultItem>, String>(cell) {
                @Override
                public String getValue(List<ResultsPresenter.ResultItem> object) {
//                    return "b";
                    if (colVal == 0 && object.get(colVal) != null && object.get(colVal).score != null) {
                        return object.get(colVal).label;
                    } else if (object.get(colVal) != null && object.get(colVal).score != null) {
                        return object.get(colVal).score.toString();
                    } else {
                        return "0";
                    }
                }
            };
            dynValue.setSortable(true);
            ListHandler<List<ResultsPresenter.ResultItem>> columnSortHandler = new ListHandler<List<ResultsPresenter.ResultItem>>(
                    dataProvider.getList());
            columnSortHandler.setComparator(dynValue,
                    new Comparator<List<ResultsPresenter.ResultItem>>() {
                public int compare(List<ResultsPresenter.ResultItem> o1, List<ResultsPresenter.ResultItem> o2) {
                    if (o1.get(colVal) == o2.get(colVal)) {
                        return 0;
                    }

                    // Compare the name columns.
                    if (o1.get(colVal) != null) {
                        return (o2.get(colVal) != null) ? o1.get(colVal).label.compareTo(o2.get(colVal).label) : 1;
                    }
                    return -1;
                }
            });
            dataGrid.addColumnSortHandler(columnSortHandler);
            dataGrid.addColumn(dynValue, data.gethIndex()[i].label);
            LOG.log(Level.INFO, "adding column " + data.gethIndex()[i].label);

        }
        dataProvider.getList().clear();;
        dataProvider.setList(data.getMarks());

    }

    public void setEmptyTableMessage() {
        dataGrid.setEmptyTableWidget(emptyImage);
    }

    public void setLoadingTableMessage() {
        dataGrid.setEmptyTableWidget(loadingImage);
    }
}
