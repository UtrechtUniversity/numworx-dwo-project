package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.EditCoursesInSchoolclassPresenter.CourseItem;

/**
 * GWT Panel that handles switching the role.
 *
 * @author G.A.J. van der Plas
 */
public class CoursesOfSchoolclassView extends Composite implements ClickHandler, ChangeHandler, CoursesOfSchoolclassPresenter.Display {

    private static final Logger LOG = Logger.getLogger(CoursesOfSchoolclassView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, CoursesOfSchoolclassView> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField(provided = true)
    CellTable dataGrid;
    @UiField(provided = true)            
    Tree tree;
    @UiField
    Button backBtn;

    private CoursesOfSchoolclassPresenter coursesOfSchoolclassPresenter;
    private ListDataProvider<CoursesOfSchoolclassPresenter.CourseItem> dataProvider = new ListDataProvider<CoursesOfSchoolclassPresenter.CourseItem>();
    
    private CoursesOfSchoolclassPresenter.CourseItem selected;
    private MyCheckBoxCell checkBox;

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

    public class MyCheckBoxCell extends CheckboxCell {

        boolean state = false;

        public MyCheckBoxCell(boolean a, boolean b) {
            super(a, b);
        }

        @Override
        public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, Boolean value, NativeEvent event, ValueUpdater<Boolean> valueUpdater) {
            if (value == null) {
                return;
            }
            super.onBrowserEvent(context, parent, value, event, valueUpdater);
            if ("change".equals(event.getType())) {
//                coursesOfSchoolclassPresenter.selectItem((CoursesOfSchoolclassPresenter.CourseItem) context.getKey(), 5);
                LOG.log(Level.INFO, "key " + context.getKey() + " boolean " + value);
            }
        }
    }

    public CoursesOfSchoolclassView(CoursesOfSchoolclassPresenter sp) {
        coursesOfSchoolclassPresenter = sp;
        coursesOfSchoolclassPresenter.setView(this);
        String[] tableHeaders = sp.getTableHeaders();
        dataGrid = new CellTable<String>();
        tree = new Tree();
        
       
        dataProvider.addDataDisplay(dataGrid);
        dataGrid.setSkipRowHoverCheck(true);
        dataGrid.setKeyboardSelectionPolicy(com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);

        List<CoursesOfSchoolclassPresenter.CourseItem> data = dataProvider.getList();
        final CoursesOfSchoolclassView.MyCell cell = new CoursesOfSchoolclassView.MyCell();
        final CoursesOfSchoolclassView.MyClickCell clickCell = new CoursesOfSchoolclassView.MyClickCell();

        //name
        Column<CoursesOfSchoolclassPresenter.CourseItem, String> value = new Column<CoursesOfSchoolclassPresenter.CourseItem, String>(cell) {
            @Override
            public String getValue(CoursesOfSchoolclassPresenter.CourseItem object) {
                return object.name;
            }
        };
        value.setSortable(true);
        ListHandler<CoursesOfSchoolclassPresenter.CourseItem> columnSortHandler = new ListHandler<CoursesOfSchoolclassPresenter.CourseItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<CoursesOfSchoolclassPresenter.CourseItem>() {
            public int compare(CoursesOfSchoolclassPresenter.CourseItem o1, CoursesOfSchoolclassPresenter.CourseItem o2) {
                if (o1 == o2) {
                    return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                    return (o2 != null) ? o1.name.compareTo(o2.name) : 1;
                }
                return -1;
            }
        });
        dataGrid.addColumnSortHandler(columnSortHandler);
        dataGrid.addColumn(value, tableHeaders[0]);

        //hasData
        value = new Column<CoursesOfSchoolclassPresenter.CourseItem, String>(cell) {
            @Override
            public String getValue(CoursesOfSchoolclassPresenter.CourseItem object) {
                return object.hasStudentData;
            }
        };
        value.setSortable(true);
        columnSortHandler = new ListHandler<CoursesOfSchoolclassPresenter.CourseItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<CoursesOfSchoolclassPresenter.CourseItem>() {
            public int compare(CoursesOfSchoolclassPresenter.CourseItem o1, CoursesOfSchoolclassPresenter.CourseItem o2) {
                if (o1 == o2) {
                    return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                    return (o2 != null) ? o1.hasStudentData.compareTo(o2.hasStudentData) : 1;
                }
                return -1;
            }
        });
        dataGrid.addColumnSortHandler(columnSortHandler);
        dataGrid.addColumn(value, tableHeaders[1]);        

        //hasType
        value = new Column<CoursesOfSchoolclassPresenter.CourseItem, String>(cell) {
            @Override
            public String getValue(CoursesOfSchoolclassPresenter.CourseItem object) {
                return object.type;
            }
        };
        value.setSortable(true);
        columnSortHandler = new ListHandler<CoursesOfSchoolclassPresenter.CourseItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<CoursesOfSchoolclassPresenter.CourseItem>() {
            public int compare(CoursesOfSchoolclassPresenter.CourseItem o1, CoursesOfSchoolclassPresenter.CourseItem o2) {
                if (o1 == o2) {
                    return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                    return (o2 != null) ? o1.type.compareTo(o2.type) : 1;
                }
                return -1;
            }
        });
        dataGrid.addColumnSortHandler(columnSortHandler);
        dataGrid.addColumn(value, tableHeaders[2]); 
        
        //from
        value = new Column<CoursesOfSchoolclassPresenter.CourseItem, String>(cell) {
            @Override
            public String getValue(CoursesOfSchoolclassPresenter.CourseItem object) {
                return object.from.toString();
            }
        };
        value.setSortable(true);
        columnSortHandler = new ListHandler<CoursesOfSchoolclassPresenter.CourseItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<CoursesOfSchoolclassPresenter.CourseItem>() {
            public int compare(CoursesOfSchoolclassPresenter.CourseItem o1, CoursesOfSchoolclassPresenter.CourseItem o2) {
                if (o1 == o2) {
                    return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                    return (o2 != null) ? o1.from.compareTo(o2.from) : 1;
                }
                return -1;
            }
        });
        dataGrid.addColumnSortHandler(columnSortHandler);
        dataGrid.addColumn(value, tableHeaders[3]);     
                

        //to
        value = new Column<CoursesOfSchoolclassPresenter.CourseItem, String>(cell) {
            @Override
            public String getValue(CoursesOfSchoolclassPresenter.CourseItem object) {
                return object.to.toString();
            }
        };
        value.setSortable(true);
        columnSortHandler = new ListHandler<CoursesOfSchoolclassPresenter.CourseItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<CoursesOfSchoolclassPresenter.CourseItem>() {
            public int compare(CoursesOfSchoolclassPresenter.CourseItem o1, CoursesOfSchoolclassPresenter.CourseItem o2) {
                if (o1 == o2) {
                    return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                    return (o2 != null) ? o1.to.compareTo(o2.to) : 1;
                }
                return -1;
            }
        });
        dataGrid.addColumnSortHandler(columnSortHandler);
        dataGrid.addColumn(value, tableHeaders[3]);     
                        
        dataGrid.setRowData(0, data);
        dataGrid.setRowCount(data.size(), true);
//        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
//        pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
//        pager.setDisplay(dataGrid);
//        pager.setPageSize(dataGrid.getPageSize());

        initWidget(uiBinder.createAndBindUi(this));
        //controller must be before clicks occur
//        final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
//        dataGrid.setSelectionModel(selectionModel);
//        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
//            public void onSelectionChange(SelectionChangeEvent event) {
//                String selected = selectionModel.getSelectedObject();
//                LOG.log(Level.INFO, "selection key: " + selectionModel.getKey(selected));
//                if (selected != null) {
//                    Window.alert("You selected: " + selected + ".");
//                }
//            }
//        });
        backBtn.addClickHandler(this);
    }

    public void init() {
//        addStudentsBtn.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == backBtn) {
            coursesOfSchoolclassPresenter.goBackToSchoolClasses();
        }
    }

    @Override
    public void onChange(ChangeEvent event) {
        //    LOG.log(Level.INFO, "Listbox event:" + event.getSource().toString());
    }

    public void updateView(Map<String, CoursesOfSchoolclassPresenter.CourseItem> data) {
//        dataProvider.getList().clear();
//        dataProvider.getList().addAll(data.values());
//        dataProvider.refresh();
    }

    private void cellSelected(int row, int column) {
//        LOG.log(Level.FINE, "Clicked row x col " + row + "x" + column + " " + dataProvider.getList().get(row).usercode + " " + dataGrid.getHeader(column).getValue());
//        dataGrid.getHeader(column);
//        coursesOfSchoolclassPresenter.selectItem((CoursesOfSchoolclassPresenter.CourseItem) dataProvider.getList().get(row), column);
    }

}
