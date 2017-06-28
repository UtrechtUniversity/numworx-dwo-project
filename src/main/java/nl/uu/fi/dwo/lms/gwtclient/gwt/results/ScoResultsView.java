package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GWT Panel that handles switching the role.
 *
 * @author G.A.J. van der Plas
 */
public class ScoResultsView extends Composite implements ClickHandler, ScoResultsPresenter.Display {

    private static final Logger LOG = Logger.getLogger(ScoResultsView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, ScoResultsView> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private ScoResultsPresenter scoResultsPresenter;
    @UiField(provided = true)
    SimplePager pager;
    @UiField(provided = true)
    CellTable dataTable;
    @UiField
    Button backBtn;
//    @UiField
    Frame frame; // Hier komt /dwo/apps/player.html?locale=nl#cmi.launch_data:scoid

    private native static void setAPI(ScoResultsView view) /*-{
    	var api = {
    			"LMSGetValue" : function(key) {
    				return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.results.ScoResultsView::getValue(Ljava/lang/String;)(key)
    			},
    			"LMSSetValue" : function(key, value) {
    				return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.results.ScoResultsView::setValue(Ljava/lang/String;Ljava/lang/String;)(key, value)
    			},
    			"GetValue" : function(key) {
    				return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.results.ScoResultsView::getValue(Ljava/lang/String;)(key)
    			},
    			"SetValue" : function(key, value) {
    				return view.@nl.uu.fi.dwo.lms.gwtclient.gwt.results.ScoResultsView::setValue(Ljava/lang/String;Ljava/lang/String;)(key, value)
    			},
    // TODO more to follow...			
    		};
    	$wnd.API = api;
    	$wnd.API_1484_11 = api;
    }-*/;

    private String getValue(String key) {
        return scoResultsPresenter.getScormAPIValue(key);
    }

    private String setValue(String key, String value) {
        return scoResultsPresenter.setScormAPIValue(key, value);
    }

//    private ScoResultsPresenter.StudentItem selected;
    private ListDataProvider<ScoResultsPresenter.StudentItem> dataProvider = new ListDataProvider<ScoResultsPresenter.StudentItem>();

    //non-clickable cell
    public class MyCell extends AbstractCell<String> {

        public MyCell() {
            super("click", "keydown");
        }

        @Override
        public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
            if (value != null) {
                sb.appendEscaped(value);
            }
        }

        @Override
        public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
            if (value == null) {
                return;
            }
            super.onBrowserEvent(context, parent, value, event, valueUpdater);
            if ("click".equals(event.getType())) {
//                LOG.log(Level.INFO, "key "+context.getKey());
                cellSelected(context.getIndex(), context.getColumn());
            }
        }
    }

    //clickable cell
    public class MyClickCell extends AbstractCell<String> {

        public MyClickCell() {
            super("click", "keydown");
        }

        @Override
        public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
            if (value != null) {
                sb.appendHtmlConstant("<a href='javascript:;'>");
                sb.appendEscaped(value);
                sb.appendHtmlConstant("</a>");
            }
        }

        @Override
        public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
            if (value == null) {
                return;
            }
            super.onBrowserEvent(context, parent, value, event, valueUpdater);
            if ("click".equals(event.getType())) {
//                LOG.log(Level.INFO, "key "+context.getKey());
                cellSelected(context.getIndex(), context.getColumn());
            }
        }
    }

    ScoResultsPresenter.StudentItem selected;

    public ScoResultsView(ScoResultsPresenter sp) {
        scoResultsPresenter = sp;
        scoResultsPresenter.setView(this);
        String[] tableHeaders = sp.getTableHeaders();
        dataTable = new CellTable<String>();
        dataProvider.addDataDisplay(dataTable);
        dataTable.setSkipRowHoverCheck(true);
        dataTable.setKeyboardSelectionPolicy(com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);

        List<ScoResultsPresenter.StudentItem> data = dataProvider.getList();
        final MyCell cell = new MyCell();
        final MyClickCell clickCell = new MyClickCell();

        //fullname
        Column<ScoResultsPresenter.StudentItem, String> value = new Column<ScoResultsPresenter.StudentItem, String>(cell) {
            @Override
            public String getValue(ScoResultsPresenter.StudentItem object) {
                return object.givenName + " " + object.insertion + " " + object.familyName + " - " + object.usercode;
            }
        };
        value.setSortable(true);
        ListHandler<ScoResultsPresenter.StudentItem> columnSortHandler = new ListHandler<ScoResultsPresenter.StudentItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<ScoResultsPresenter.StudentItem>() {
            public int compare(ScoResultsPresenter.StudentItem o1, ScoResultsPresenter.StudentItem o2) {
                if (o1 == o2) {
                    return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                    return (o2 != null) ? o1.givenName.compareTo(o2.givenName) : 1;
                }
                return -1;
            }
        });
        dataTable.addColumnSortHandler(columnSortHandler);
        dataTable.addColumn(value, tableHeaders[0]);

        //total score
        value = new Column<ScoResultsPresenter.StudentItem, String>(cell) {
            @Override
            public String getValue(ScoResultsPresenter.StudentItem object) {
                return (object.score != null) ? "" + object.score : "";
            }
        };
        value.setSortable(true);
        columnSortHandler = new ListHandler<ScoResultsPresenter.StudentItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<ScoResultsPresenter.StudentItem>() {
            public int compare(ScoResultsPresenter.StudentItem o1, ScoResultsPresenter.StudentItem o2) {
                if (o1 == o2) {
                    return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                    return (o2 != null) ? o1.score.compareTo(o2.score) : 1;
                }
                return -1;
            }
        });
        dataTable.addColumnSortHandler(columnSortHandler);
        dataTable.addColumn(value, tableHeaders[1]);
        final SingleSelectionModel<ScoResultsPresenter.StudentItem> selectionModel = new SingleSelectionModel<ScoResultsPresenter.StudentItem>();

        dataTable.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
                if(((ScoResultsPresenter.StudentItem) selectionModel.getSelectedObject()).score!=null){
                select(selectionModel.getSelectedObject());
                }else{
                    selectionModel.clear();
                }
                LOG.log(Level.INFO, "selection key: " + selectionModel.getSelectedObject().key);
            }
        });

//        //for each of the subscores add a column and use a clickable cell
//        value = new Column<ScoResultsPresenter.StudentItem, String>(cell) {
//            @Override
//            public String getValue(ScoResultsPresenter.StudentItem object) {
//                return object.usercode;
//            }
//        };
//        value.setSortable(true);
//        columnSortHandler = new ListHandler<ScoResultsPresenter.StudentItem>(
//                data);
//        columnSortHandler.setComparator(value,
//                new Comparator<ScoResultsPresenter.StudentItem>() {
//            public int compare(ScoResultsPresenter.StudentItem o1, ScoResultsPresenter.StudentItem o2) {
//                if (o1 == o2) {
//                    return 0;
//                }
//
//                // Compare the name columns.
//                if (o1 != null) {
//                    return (o2 != null) ? o1.usercode.compareTo(o2.usercode) : 1;
//                }
//                return -1;
//            }
//        });
//        dataTable.addColumnSortHandler(columnSortHandler);
//        dataTable.addColumn(value, tableHeaders[3]);
        dataTable.setRowData(0, data);
        dataTable.setRowCount(data.size(), true);
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataTable);
        pager.setPageSize(dataTable.getPageSize());

        initWidget(uiBinder.createAndBindUi(this));
        //controller must be before clicks occur
        backBtn.addClickHandler(this);
    }

    @Override
    public void init() {
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == backBtn) {
            scoResultsPresenter.goBackToResults();
        }
    }

    public void updateView(ScoResultsPresenter.StudentItem selectedItem, Map<String, ScoResultsPresenter.StudentItem> data) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(data.values());
        dataProvider.refresh();
        if (selectedItem != null) {
            dataTable.getSelectionModel().setSelected(selectedItem, true);
        }
    }

    private void select(ScoResultsPresenter.StudentItem item) {
        scoResultsPresenter.select(item);
    }

    private void cellSelected(int row, int col) {

    }
}
