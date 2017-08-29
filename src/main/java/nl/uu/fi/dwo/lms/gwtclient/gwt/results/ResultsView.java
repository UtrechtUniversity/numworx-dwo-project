package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.icons.DwoResources;

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

    private ListDataProvider<ResultsPresenter.ResultItem> dataProvider = new ListDataProvider<ResultsPresenter.ResultItem>();

    public class ResultData {

        int width;
        int height;
        String[][] data; //height, width
    }

    public ResultsView(ResultsPresenter rp) {
        resultsPresenter = rp;
        rp.setView(this);
        resultsPresenter.setView(this);
        dataGrid = new DataGrid<String>();
        dataProvider.addDataDisplay(dataGrid);
        dataGrid.setSkipRowHoverCheck(true);
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
        for (int i = 0; i < dataGrid.getColumnCount(); i++) {
            dataGrid.removeColumn(0);
        }
        clear();
        resultsPresenter.plotResultsEvent();
    }

    public void plot(ResultsPresenter.ResultPlot data) {
        for (int i = 0; i < dataGrid.getColumnCount(); i++) {
            dataGrid.removeColumn(0);
        }
        for (int i = 0; i < data.gethIndex().length; i++) {
            //create column
            TextCell cell = new TextCell();
            //givenName
            Column<ResultsPresenter.ResultItem, String> value = new Column<ResultsPresenter.ResultItem, String>(cell) {
                @Override
                public String getValue(ResultsPresenter.ResultItem object) {
                    return object.label;
                }
            };
//            value.setSortable(true);
//            List<ResultsPresenter.ResultItem> colList = new ArrayList<ResultsPresenter.ResultItem>(data.getvIndex().length);
//            for (int j = 0; j < data.getvIndex().length; j++) {
//                colList.add(data.getMarks()[i][j]);
//            }
//            ListHandler<ResultsPresenter.ResultItem> columnSortHandler = new ListHandler<ResultsPresenter.ResultItem>(colList);
//            columnSortHandler.setComparator(value,
//                    new Comparator<ResultsPresenter.ResultItem>() {
//                public int compare(ResultsPresenter.ResultItem o1, ResultsPresenter.ResultItem o2) {
//                    if (o1 == o2) {
//                        return 0;
//                    }
//
//                    // Compare the name columns.
//                    if (o1 != null) {
//                        return (o2 != null) ? o1.label.compareTo(o2.label) : 1;
//                    }
//                    return -1;
//                }
//            });
//            dataGrid.addColumnSortHandler(columnSortHandler);
            dataGrid.addColumn(value, data.gethIndex()[i].label);
        }
    }
    

    public void setEmptyTableMessage(){
        dataGrid.setEmptyTableWidget(emptyImage);
    }

    public void setLoadingTableMessage(){
        dataGrid.setEmptyTableWidget(loadingImage);
    }        
}
