package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
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
public class StudentsInSchoolclassView extends Composite implements ClickHandler, StudentsInSchoolclassPresenter.Display {

    private static final Logger LOG = Logger.getLogger(StudentsInSchoolclassView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, StudentsInSchoolclassView> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField(provided = true)
    CellTable dataGrid;
//    @UiField(provided = true)            
//    CellList dataGrid;
    @UiField(provided = true)
    SimplePager pager;
//    @UiField
    Button addBtn;

    private StudentsInSchoolclassPresenter studentsInSchoolclassPresenter;
    StudentsInSchoolclassPresenter.StudentItem selected;
    ListDataProvider<StudentsInSchoolclassPresenter.StudentItem> dataProvider = new ListDataProvider<StudentsInSchoolclassPresenter.StudentItem>();

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

    public StudentsInSchoolclassView(StudentsInSchoolclassPresenter sp) {
        studentsInSchoolclassPresenter = sp;
        studentsInSchoolclassPresenter.setView(this);
        String[] tableHeaders = sp.getTableHeaders();
        dataGrid = new CellTable<String>();

        dataProvider.addDataDisplay(dataGrid);
        dataGrid.setSkipRowHoverCheck(true);
        dataGrid.setKeyboardSelectionPolicy(com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);

        List<StudentsInSchoolclassPresenter.StudentItem> data = dataProvider.getList();
        final StudentsInSchoolclassView.MyCell cell = new StudentsInSchoolclassView.MyCell();

        // Connect the table to the data provider.
        //dataProvider.addDataDisplay(dataGrid);

        //givenName
        Column<StudentsInSchoolclassPresenter.StudentItem, String> value = new Column<StudentsInSchoolclassPresenter.StudentItem, String>(cell) {
            @Override
            public String getValue(StudentsInSchoolclassPresenter.StudentItem object) {
                return object.givenName;
            }
        };
        value.setSortable(true);
        ListHandler<StudentsInSchoolclassPresenter.StudentItem> columnSortHandler = new ListHandler<StudentsInSchoolclassPresenter.StudentItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<StudentsInSchoolclassPresenter.StudentItem>() {
            public int compare(StudentsInSchoolclassPresenter.StudentItem o1, StudentsInSchoolclassPresenter.StudentItem o2) {
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
        dataGrid.addColumnSortHandler(columnSortHandler);
        dataGrid.addColumn(value, tableHeaders[0]);

        //insertion
            value = new Column<StudentsInSchoolclassPresenter.StudentItem, String>(cell) {
            @Override
            public String getValue(StudentsInSchoolclassPresenter.StudentItem object) {
                return object.insertion;
            }
        };
        value.setSortable(true);
        columnSortHandler = new ListHandler<StudentsInSchoolclassPresenter.StudentItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<StudentsInSchoolclassPresenter.StudentItem>() {
            public int compare(StudentsInSchoolclassPresenter.StudentItem o1, StudentsInSchoolclassPresenter.StudentItem o2) {
                if (o1 == o2) {
                    return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                    return (o2 != null) ? o1.insertion.compareTo(o2.insertion) : 1;
                }
                return -1;
            }
        });
        dataGrid.addColumnSortHandler(columnSortHandler);
        dataGrid.addColumn(value, tableHeaders[1]);

        //familyName
            value = new Column<StudentsInSchoolclassPresenter.StudentItem, String>(cell) {
            @Override
            public String getValue(StudentsInSchoolclassPresenter.StudentItem object) {
                return object.familyName;
            }
        };
        value.setSortable(true);
        columnSortHandler = new ListHandler<StudentsInSchoolclassPresenter.StudentItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<StudentsInSchoolclassPresenter.StudentItem>() {
            public int compare(StudentsInSchoolclassPresenter.StudentItem o1, StudentsInSchoolclassPresenter.StudentItem o2) {
                if (o1 == o2) {
                    return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                    return (o2 != null) ? o1.familyName.compareTo(o2.familyName) : 1;
                }
                return -1;
            }
        });
        dataGrid.addColumnSortHandler(columnSortHandler);
        dataGrid.addColumn(value, tableHeaders[2]);

        //usercode
            value = new Column<StudentsInSchoolclassPresenter.StudentItem, String>(cell) {
            @Override
            public String getValue(StudentsInSchoolclassPresenter.StudentItem object) {
                return object.usercode;
            }
        };
        value.setSortable(true);
        columnSortHandler = new ListHandler<StudentsInSchoolclassPresenter.StudentItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<StudentsInSchoolclassPresenter.StudentItem>() {
            public int compare(StudentsInSchoolclassPresenter.StudentItem o1, StudentsInSchoolclassPresenter.StudentItem o2) {
                if (o1 == o2) {
                    return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                    return (o2 != null) ? o1.usercode.compareTo(o2.usercode) : 1;
                }
                return -1;
            }
        });
        dataGrid.addColumnSortHandler(columnSortHandler);
        dataGrid.addColumn(value, tableHeaders[3]);
        
        //usercode col
        value = new Column<StudentsInSchoolclassPresenter.StudentItem, String>(cell) {
            @Override
            public String getValue(StudentsInSchoolclassPresenter.StudentItem object) {
                return tableHeaders[4];
            }
        };
        value.setSortable(false);
        dataGrid.addColumn(value, tableHeaders[4]);

        dataGrid.setRowData(0, data);
        dataGrid.setRowCount(data.size(), true);
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);
        pager.setPageSize(dataGrid.getPageSize());

        initWidget(uiBinder.createAndBindUi(this));
        //controller must be before clicks occur
//        addBtn.addClickHandler(this);
        final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
        dataGrid.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
                String selected = selectionModel.getSelectedObject();
                LOG.log(Level.INFO, "selection key: " + selectionModel.getKey(selected));
                if (selected != null) {
                    Window.alert("You selected: " + selected + ".");
                }
            }
        });

    }

    @Override
    public void init() {
//        addBtn.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == addBtn) {
            //      schoolclassesPresenter.addSchoolClass();
        }
    }

    public void updateView(Map<String, StudentsInSchoolclassPresenter.StudentItem> data) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(data.values());
        dataProvider.refresh();
    }

    private void cellSelected(int row, int column) {
        LOG.log(Level.FINE, "Clicked row x col " + row + "x" + column + " " + dataGrid.getVisibleItem(row) + " " + dataGrid.getHeader(column).getValue());
        dataGrid.getHeader(column);
        studentsInSchoolclassPresenter.selectItem((StudentsInSchoolclassPresenter.StudentItem) dataGrid.getVisibleItem(row), column);
    }

}
