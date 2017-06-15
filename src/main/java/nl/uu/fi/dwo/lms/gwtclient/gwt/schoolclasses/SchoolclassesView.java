package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
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
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ViewFactory;

/**
 * GWT Panel that handles switching the role.
 *
 * @author G.A.J. van der Plas
 */
public class SchoolclassesView extends Composite implements ClickHandler, SchoolclassesPresenter.Display {

    private static final Logger LOG = Logger.getLogger(SchoolclassesView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, SchoolclassesView> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField(provided = true)
    CellTable dataGrid;
    @UiField(provided = true)
    SimplePager pager;
    @UiField
    Button addBtn;

    private SchoolclassesPresenter schoolclassesPresenter;
    private ViewFactory viewFactory;
    SchoolclassesPresenter.ClassItem selected;
    ListDataProvider<SchoolclassesPresenter.ClassItem> dataProvider = new ListDataProvider<SchoolclassesPresenter.ClassItem>();
    AddSchoolclassView addSchoolclassView;
    final DialogBox dialogBox = new DialogBox();

    public class MyCell extends AbstractCell<String> {

        public MyCell() {
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

    public SchoolclassesView(SchoolclassesPresenter sp, ViewFactory vf) {
        schoolclassesPresenter = sp;
        viewFactory = vf;
        schoolclassesPresenter.setView(this);
//            dialogBox.add(addSchoolclassView.asWidget());
//            dialogBox.setModal(true);
//            dialogBox.setAutoHideEnabled(true);
//            dialogBox.setGlassEnabled(true);
//            dialogBox.setAnimationEnabled(true);
//            dialogBox.center();

        String[] tableHeaders = sp.getTableHeaders();
//        TextCell textCell = new TextCell();
        dataGrid = new CellTable<String>();

//        ListDataProvider<String> dataProvider = new ListDataProvider<String>();
        // Connect the table to the data provider.
        dataProvider.addDataDisplay(dataGrid);
        dataGrid.setSkipRowHoverCheck(true);
        dataGrid.setKeyboardSelectionPolicy(com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);

//        // Add the data to the data provider, which automatically pushes it to the
//        // widget.
        List<SchoolclassesPresenter.ClassItem> data = dataProvider.getList();
//        SchoolclassesPresenter.ClassItem item = schoolclassesPresenter.new ClassItem("" + 0, "" + 1);
//        for (int i = 0; i < 100; i++) {
//            data.add(item);
//        }
        final MyCell cell = new MyCell();

        //classname
        Column<SchoolclassesPresenter.ClassItem, String> value = new Column<SchoolclassesPresenter.ClassItem, String>(cell) {
            @Override
            public String getValue(SchoolclassesPresenter.ClassItem object) {
                return object.schoolclassName;
            }
        };
        value.setSortable(true);
        ListHandler<SchoolclassesPresenter.ClassItem> columnSortHandler = new ListHandler<SchoolclassesPresenter.ClassItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<SchoolclassesPresenter.ClassItem>() {
            public int compare(SchoolclassesPresenter.ClassItem o1, SchoolclassesPresenter.ClassItem o2) {
                if (o1 == o2) {
                    return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                    return (o2 != null) ? o1.schoolclassName.compareTo(o2.schoolclassName) : 1;
                }
                return -1;
            }
        });
        dataGrid.addColumnSortHandler(columnSortHandler);
        dataGrid.addColumn(value, tableHeaders[0]);

        //edit
        value = new Column<SchoolclassesPresenter.ClassItem, String>(cell) {
            @Override
            public String getValue(SchoolclassesPresenter.ClassItem object) {
                return tableHeaders[1];
            }
        ;
        };
            value.setSortable(false);
        dataGrid.addColumn(value, tableHeaders[1]);

        //modules
        value = new Column<SchoolclassesPresenter.ClassItem, String>(cell) {
            @Override
            public String getValue(SchoolclassesPresenter.ClassItem object) {
                return tableHeaders[2];
            }
        ;
        };
            value.setSortable(false);
        dataGrid.addColumn(value, tableHeaders[2]);

        //students col
        value = new Column<SchoolclassesPresenter.ClassItem, String>(cell) {
            @Override
            public String getValue(SchoolclassesPresenter.ClassItem object) {
                return tableHeaders[3];
            }
        };
        value.setSortable(false);
        dataGrid.addColumn(value, tableHeaders[3]);

        //teachers col
        value = new Column<SchoolclassesPresenter.ClassItem, String>(cell) {
            @Override
            public String getValue(SchoolclassesPresenter.ClassItem object) {
                return tableHeaders[4];
            }
        };
        value.setSortable(false);
        dataGrid.addColumn(value, tableHeaders[4]);

        //remove col
        value = new Column<SchoolclassesPresenter.ClassItem, String>(cell) {
            @Override
            public String getValue(SchoolclassesPresenter.ClassItem object) {
                return tableHeaders[5];
            }
        };
        value.setSortable(false);
        dataGrid.addColumn(value, tableHeaders[5]);

        dataGrid.setRowData(0, data);
        dataGrid.setRowCount(data.size());
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);

        pager.setDisplay(dataGrid);

        pager.setPageSize(dataGrid.getPageSize());

        initWidget(uiBinder.createAndBindUi(this));
        addBtn.addClickHandler(this);

    }

    @Override
    public void init() {
        addSchoolclassView = (AddSchoolclassView) viewFactory.getAddSchoolclassView();
        addSchoolclassView.clear();
        if (dialogBox != null) {
            dialogBox.hide();
        }
        addBtn.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == addBtn) {
//            ClickHandler okHandler = new ClickHandler() {
//                public void onClick(ClickEvent event) {
//                    dialogBox.hide();
//                }
//            };
            if (dialogBox.getWidget()==null) {
                addSchoolclassView.clear();
                dialogBox.add(addSchoolclassView.asWidget());
                dialogBox.setModal(true);
                dialogBox.setAutoHideEnabled(true);
                dialogBox.setGlassEnabled(true);
                dialogBox.setAnimationEnabled(true);
                dialogBox.center();
            }
            dialogBox.show();
//            schoolclassesPresenter.addSchoolClass();
        }
    }

    public void updateView(Map<String, SchoolclassesPresenter.ClassItem> data) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(data.values());
        dataProvider.refresh();
    }

    private void cellSelected(int row, int column) {
        LOG.log(Level.FINE, "Clicked row x col " + row + "x" + column + " " + dataGrid.getVisibleItem(row) + " " + dataGrid.getHeader(column).getValue());
        dataGrid.getHeader(column);
        schoolclassesPresenter.selectItem((SchoolclassesPresenter.ClassItem) dataGrid.getVisibleItem(row), column);
    }
}
