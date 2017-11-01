package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.builder.shared.DivBuilder;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.AbstractCellTable.Style;
import com.google.gwt.user.cellview.client.AbstractHeaderOrFooterBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.gui.DwoClickCell;
import nl.uu.fi.dwo.lms.gwtclient.gwt.gui.DwoScoreClickCell;
import nl.uu.fi.dwo.lms.gwtclient.gwt.gui.SelectedCellHandler;
import nl.uu.fi.dwo.lms.gwtclient.gwt.icons.DwoResources;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

/**
 * Shows the students results of activities individually or grouped by school
 * class or leave course.
 *
 * @author G.A.J. van der Plas
 */
public class ResultsView extends Composite implements ClickHandler, SelectedCellHandler, ResultsPresenter.Display {

    private static final Logger LOG = Logger.getLogger(ResultsView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, ResultsView> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField(provided = true)
    CellTable dataGrid;
//    @UiField(provided = true)            
//    CellList dataGrid;
    @UiField(provided = true)
    SimplePager pager;
    @UiField
    Button exportBtn;
    DwoLocalesForGWT rb = DwoLocalesForGWT.instance;

    private static final DwoResources resources = GWT.create(DwoResources.class);
    Image emptyImage = new Image(resources.emptyIcon());
    Image loadingImage = new Image(resources.loadingIcon());
    Image drillUpImage = new Image(resources.drillUpIcon());
    Image drillDownImage = new Image(resources.drillDownIcon());

//    
//    public interface Style extends CssResource {
//
//        String panel();
//        String tableCelleven();
//        String tableCellodd();
//    }
    private int timer = 0;
    private boolean zoomedClass = false;
    private boolean zoomedCourse = false;

    private ResultsPresenter resultsPresenter;

    private ListDataProvider<List<ResultsPresenter.ResultItem>> dataProvider = new ListDataProvider<List<ResultsPresenter.ResultItem>>();

    public class ResultData {

        int width;
        int height;
        String[][] data; //height, widthis
    }

    public ResultsView(ResultsPresenter rp) {
        resultsPresenter = rp;
        resultsPresenter.setView(this);
        dataGrid = new CellTable<List<ResultsPresenter.ResultItem>>();
        dataProvider.addDataDisplay(dataGrid);
//        dataGrid.setSkipRowHoverCheck(true);
        final SingleSelectionModel<List<ResultsPresenter.ResultItem>> selectionModel = new SingleSelectionModel<List<ResultsPresenter.ResultItem>>();
        dataGrid.setSelectionModel(selectionModel);
        dataGrid.setKeyboardSelectionPolicy(com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);

//        Timer t = new Timer() {
//            @Override
//            public void run() {
//                timer += 1;
//                try {
//                    if (parent.getAutoUpdateResults().getValue()) {
//                        //handler.updateServerResults();
//                        parent.setStatus("Server OK, updating results every 60 seconds.");
//                    } else {
//                        parent.setStatus("Refresh paused.");
//                    }
//                } catch (Exception e) {
//                    parent.setStatus("Server Offline");
//                }
//            }
//        };
//        t.scheduleRepeating(60000);
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);
        pager.setPageSize(dataGrid.getPageSize());
        initWidget(uiBinder.createAndBindUi(this));
        exportBtn.addClickHandler(this);

        TextHeader header;
        clear();

    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == exportBtn) {
            copyTextToClipboard(resultsPresenter.getExportString());
            resultsPresenter.finnishedExport();
        }
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

    public void plot(ResultsPresenter.ResultPlot data, boolean schoolClass, boolean course) {
        zoomedClass = schoolClass;
        zoomedCourse = course;
        for (int i = dataGrid.getColumnCount() - 1; i >= 0; i--) {
            dataGrid.removeColumn(0);
        }
        dataProvider.getList().clear();
        dataProvider.setList(data.getMarks());

        //create schoolclass/student column
        DwoClickCell cell = new DwoClickCell();
        cell.addSelectedCellHandler(this);
        DwoScoreClickCell scoreCell = new DwoScoreClickCell();
        scoreCell.addSelectedCellHandler(this);
        ColumnSortEvent.ListHandler<List<ResultsPresenter.ResultItem>> columnSortHandler = new ColumnSortEvent.ListHandler<>(
                dataProvider.getList());
        Column<List<ResultsPresenter.ResultItem>, String> dynValue;
        for (int i = 0; i < data.gethIndex().length; i++) {
            int colVal = i;
            if (i == 0) {
                dynValue = new Column<List<ResultsPresenter.ResultItem>, String>(cell) {
                    @Override
                    public String getValue(List<ResultsPresenter.ResultItem> object) {
                        if(object.size()!=0){
                        int r = object.get(colVal).row;
                            return data.getvIndex()[r].label;
                        }else{
                            return "";
                        }
                    }                    
                };
                } else {
                dynValue = new Column<List<ResultsPresenter.ResultItem>, String>(scoreCell) {
                    @Override
                    public String getValue(List<ResultsPresenter.ResultItem> object) {
                        if (object.get(colVal) != null && object.get(colVal).score != null) {
                            String formattedScore = NumberFormat.getFormat("0").format(object.get(colVal).score);
                            return formattedScore;
                        } else {
                            return "0";
                        }
                    }
                };
            }
                dynValue.setHorizontalAlignment((colVal == 0) ? HasHorizontalAlignment.ALIGN_LOCALE_START : HasHorizontalAlignment.ALIGN_LOCALE_END);
                dynValue.setSortable(true);
                dynValue.setCellStyleNames("flexTableHeader");
                Comparator<List<ResultsPresenter.ResultItem>> comp = new Comparator<List<ResultsPresenter.ResultItem>>() {
                    public int compare(List<ResultsPresenter.ResultItem> o1, List<ResultsPresenter.ResultItem> o2) {
                        if (o1.get(colVal) == o2.get(colVal)) {
                            return 0;
                        }

                        // Compare the name columns.
                        if (o1.get(colVal) != null) {
                            if (colVal == 0) {
                                return (o2.get(colVal) != null) ? o1.get(colVal).label.compareTo(o2.get(colVal).label) : 1;
                            } else {
                                return (o2.get(colVal) != null) ? Double.compare(o1.get(colVal).score.doubleValue(), o2.get(colVal).score.doubleValue()) : 1;
                            }
                        }
                        return -1;
                    }
                };
                columnSortHandler.setComparator(dynValue, comp);
                dataGrid.setColumnWidth(dynValue, "200PX");
                dynValue.setDataStoreName(data.gethIndex()[i].label);

                if (i == 0) {
                    dataGrid.addColumn(dynValue, rb.GUI_Table_Schoolclasses());
                    dataGrid.getColumnSortList().push(dynValue);
                } else {
                    dataGrid.addColumn(dynValue, data.gethIndex()[i].label);
                }
                LOG.log(Level.INFO, "adding column " + data.gethIndex()[i].label);
                dataGrid.addColumnSortHandler(columnSortHandler);
            }
            dataGrid.setHeaderBuilder(new CustomResultHeaderBuilder(dataGrid, false));
            Element elem = this.getElement();//DOM.getElementById("resultCol1");
            DOM.sinkEvents(elem, Event.ONCLICK | Event.ONMOUSEOUT | Event.ONMOUSEOVER);
            DOM.setEventListener(elem, new EventListener() {
                @Override
                public void onBrowserEvent(Event event) {
                    if (Event.ONCLICK == event.getTypeInt()) {
                        LOG.log(Level.INFO, " " + Element.as(event.getEventTarget()).getId());
                        int col;
                        String colId = Element.as(event.getEventTarget()).getId();
                        if (!colId.isEmpty()) {
                            col = Integer.parseInt(colId.substring("resultCol".length()));
                            resultsPresenter.selectColumnZoom(col);
                        }
                    }
                }
            });
            ColumnSortEvent.fire(dataGrid, dataGrid.getColumnSortList());
            dataGrid.redraw();

        }

    

    public void setEmptyTableMessage() {
        dataGrid.setEmptyTableWidget(emptyImage);
    }

    public void setLoadingTableMessage() {
        dataGrid.setEmptyTableWidget(loadingImage);
    }

    private void cellSelected(int row, int column) {
        if (column == 0 || (zoomedClass && zoomedCourse)) {
            LOG.log(Level.FINE, "Clicked row x col " + row + "x" + column + " " + dataProvider.getList().get(row).get(0).label + "," + dataGrid.getHeader(column).getValue());
            resultsPresenter.selectRowAndCol(dataProvider.getList().get(row).get(0).row, column);
        }
    }

    public void onSelectedCell(Cell.Context context, String value) {
        cellSelected(context.getIndex(), context.getColumn());
    }

    public class CustomResultHeaderBuilder extends AbstractHeaderOrFooterBuilder<List<ResultsPresenter.ResultItem>> {

        public CustomResultHeaderBuilder(AbstractCellTable<List<ResultsPresenter.ResultItem>> table,
                boolean isFooter) {
            super(table, isFooter);
            // TODO Auto-generated constructor stub
        }

        @Override
        public boolean buildHeaderOrFooterImpl() {
            int cols = this.getTable().getColumnCount();

            TableRowBuilder row;
            TableCellBuilder th;
            DivBuilder div;
            setSortIconStartOfLine(false);

            Style style = dataGrid.getResources().style();
            ColumnSortList sortList = dataGrid.getColumnSortList();
            ColumnSortInfo sortedInfo = (sortList.size() == 0) ? null : sortList.get(0);
            Column<?, ?> sortedColumn = (sortedInfo == null) ? null : sortedInfo.getColumn();
            boolean isSortAscending = (sortedInfo == null) ? false : sortedInfo.isAscending();
            row = startRow();
            for (int col = 0; col < cols; col++) {
                Column column = dataGrid.getColumn(col);
                th = row.startTH();
                div = th.startDiv();
                //get rid this and add a cell row that has buttons below the header
                String imageUrl;
                if (col == 0) {
                    imageUrl = (zoomedClass) ? drillUpImage.getUrl() : "";
                } else {
                    imageUrl = (zoomedCourse) ? drillUpImage.getUrl() : drillDownImage.getUrl();
                };
                div.startImage().src(imageUrl).attribute("href", "\"javascript:void(0);\"").id("resultCol" + col).endImage();
                div.end();

                th.endTH();
            }
            row.endTR();

            row = startRow();
            for (int col = 0; col < cols; col++) {
                StringBuilder classesBuilder = new StringBuilder(style.sortableHeader());
                if (col == 0) {
                    classesBuilder.append(" " + style.firstColumnHeader());
                }
                if (col == cols - 1) {
                    classesBuilder.append(" " + style.lastColumnHeader());
                }

                Column column = dataGrid.getColumn(col);
                if (column.isSortable()) {
                    classesBuilder.append(" " + style.sortableHeader());
                }
                boolean isSorted = (sortedColumn == column);
                if ((sortedColumn == column)) {
                    classesBuilder.append(" "
                            + (isSortAscending ? style.sortedHeaderAscending() : style.sortedHeaderDescending()));
                }
                th = row.startTH().className(classesBuilder.toString());//.className(classesBuilder.toString());
                enableColumnHandlers(th, dataGrid.getColumn(col));
                Header header = getHeader(col);
                Context context = new Context(0, 2, header.getKey());
                renderSortableHeader(th, context, header, isSorted, isSortAscending);
                th.endTH();
            }
            row.endTR();

            return true;
        }
    }

    /**
     * Function that allows to put a string into the browsers clipboard.
     *
     * @param text
     */
    public static native void copyTextToClipboard(String text) /*-{
        var textArea = document.createElement("textarea");
        //
        // *** This styling is an extra step which is likely not required. ***
        //
        // Why is it here? To ensure:
        // 1. the element is able to have focus and selection.
        // 2. if element was to flash render it has minimal visual impact.
        // 3. less flakyness with selection and copying which **might** occur if
        //    the textarea element is not visible.
        //
        // The likelihood is the element won't even render, not even a flash,
        // so some of these are just precautions. However in IE the element
        // is visible whilst the popup box asking the user for permission for
        // the web page to copy to the clipboard.
        //

        // Place in top-left corner of screen regardless of scroll position.
        textArea.style.position = 'fixed';
        textArea.style.top = 0;
        textArea.style.left = 0;

        // Ensure it has a small width and height. Setting to 1px / 1em
        // doesn't work as this gives a negative w/h on some browsers.
        textArea.style.width = '2em';
        textArea.style.height = '2em';

        // We don't need padding, reducing the size if it does flash render.
        textArea.style.padding = 0;

        // Clean up any borders.
        textArea.style.border = 'none';
        textArea.style.outline = 'none';
        textArea.style.boxShadow = 'none';

        // Avoid flash of white box if rendered for any reason.
        textArea.style.background = 'transparent';


        textArea.value = text;

        document.body.appendChild(textArea);

        textArea.select();

        try {
            var successful = document.execCommand('copy');
        } catch (err) {
            console.log('Unable to copy');
        }
        document.body.removeChild(textArea);
    }-*/;
}
