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
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
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
public class TeachersInSchoolclassView extends Composite implements ClickHandler, ChangeHandler, TeachersInSchoolclassPresenter.Display {

    private static final Logger LOG = Logger.getLogger(TeachersInSchoolclassView.class.getName());

    interface MyUiBinder extends UiBinder<Widget, TeachersInSchoolclassView> {
    }
    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField(provided = true)
    CellTable dataGrid;
//    @UiField(provided = true)            
//    CellList dataGrid;
    @UiField(provided = true)
    SimplePager pager;
    @UiField
    Button backBtn;
    @UiField
    Button addToSchoolClass;
    @UiField
    Button deleteSelectedBtn;
    @UiField
    ListBox teacherListBox;

    private TeachersInSchoolclassPresenter teachersInSchoolclassPresenter;
    private ListDataProvider<TeachersInSchoolclassPresenter.TeacherItem> dataProvider = new ListDataProvider<TeachersInSchoolclassPresenter.TeacherItem>();
    private List<TeacherListBoxItem> addTeacherList;
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
                teachersInSchoolclassPresenter.selectItem((TeachersInSchoolclassPresenter.TeacherItem) context.getKey(), 4);
                LOG.log(Level.INFO, "key " + context.getKey() + " boolean " + value);
            }
        }
    }

    public TeachersInSchoolclassView(TeachersInSchoolclassPresenter sp) {
        teachersInSchoolclassPresenter = sp;
        teachersInSchoolclassPresenter.setView(this);
        String[] tableHeaders = sp.getTableHeaders();
        dataGrid = new CellTable<String>();
//        teacherListBox = new ValueListBox<SchoolClassItem>(new Renderer<SchoolClassItem>() {
//
//            public String render(SchoolClassListBoxItem item) {                
//                return item.getSchoolclassName();
//            }
//
//            public void render(SchoolClassListBoxItem user, Appendable appendable) throws IOException {
//                String s = render(user);
//                appendable.append(s);
//            }
//        });

        dataProvider.addDataDisplay(dataGrid);
        dataGrid.setSkipRowHoverCheck(true);
        dataGrid.setKeyboardSelectionPolicy(com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);

        List<TeachersInSchoolclassPresenter.TeacherItem> data = dataProvider.getList();
        final TeachersInSchoolclassView.MyCell cell = new TeachersInSchoolclassView.MyCell();
        final TeachersInSchoolclassView.MyClickCell clickCell = new TeachersInSchoolclassView.MyClickCell();

        //givenName
        Column<TeachersInSchoolclassPresenter.TeacherItem, String> value = new Column<TeachersInSchoolclassPresenter.TeacherItem, String>(cell) {
            @Override
            public String getValue(TeachersInSchoolclassPresenter.TeacherItem object) {
                return object.givenName;
            }
        };
        value.setSortable(true);
        ListHandler<TeachersInSchoolclassPresenter.TeacherItem> columnSortHandler = new ListHandler<TeachersInSchoolclassPresenter.TeacherItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<TeachersInSchoolclassPresenter.TeacherItem>() {
            public int compare(TeachersInSchoolclassPresenter.TeacherItem o1, TeachersInSchoolclassPresenter.TeacherItem o2) {
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
        value = new Column<TeachersInSchoolclassPresenter.TeacherItem, String>(cell) {
            @Override
            public String getValue(TeachersInSchoolclassPresenter.TeacherItem object) {
                return object.insertion;
            }
        };
        value.setSortable(true);
        columnSortHandler = new ListHandler<TeachersInSchoolclassPresenter.TeacherItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<TeachersInSchoolclassPresenter.TeacherItem>() {
            public int compare(TeachersInSchoolclassPresenter.TeacherItem o1, TeachersInSchoolclassPresenter.TeacherItem o2) {
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
        value = new Column<TeachersInSchoolclassPresenter.TeacherItem, String>(cell) {
            @Override
            public String getValue(TeachersInSchoolclassPresenter.TeacherItem object) {
                return object.familyName;
            }
        };
        value.setSortable(true);
        columnSortHandler = new ListHandler<TeachersInSchoolclassPresenter.TeacherItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<TeachersInSchoolclassPresenter.TeacherItem>() {
            public int compare(TeachersInSchoolclassPresenter.TeacherItem o1, TeachersInSchoolclassPresenter.TeacherItem o2) {
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
        value = new Column<TeachersInSchoolclassPresenter.TeacherItem, String>(cell) {
            @Override
            public String getValue(TeachersInSchoolclassPresenter.TeacherItem object) {
                return object.usercode;
            }
        };
        value.setSortable(true);
        columnSortHandler = new ListHandler<TeachersInSchoolclassPresenter.TeacherItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<TeachersInSchoolclassPresenter.TeacherItem>() {
            public int compare(TeachersInSchoolclassPresenter.TeacherItem o1, TeachersInSchoolclassPresenter.TeacherItem o2) {
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

//        //edit student
//        value = new Column<TeachersInSchoolclassPresenter.SchoolClassListBoxItem, String>(clickCell) {
//            @Override
//            public String getValue(TeachersInSchoolclassPresenter.SchoolClassListBoxItem object) {
//                if (object.singleSchool == true) {
//                    return tableHeaders[4];
//                } else {
//                    return "";
//                }
//            }
//        };
//        value.setSortable(false);
//        dataGrid.addColumn(value, tableHeaders[3]);

        //select student
        checkBox = new MyCheckBoxCell(true, true);
        Column<TeachersInSchoolclassPresenter.TeacherItem, Boolean> bValue = new Column<TeachersInSchoolclassPresenter.TeacherItem, Boolean>(checkBox) {
            @Override
            public Boolean getValue(TeachersInSchoolclassPresenter.TeacherItem object) {
                return object.selected;
            }
        };

        bValue.setSortable(false);
        dataGrid.addColumn(bValue, tableHeaders[4]);

        dataGrid.setRowData(0, data);
        dataGrid.setRowCount(data.size(), true);
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);
        pager.setPageSize(dataGrid.getPageSize());

        initWidget(uiBinder.createAndBindUi(this));
        //controller must be before clicks occur
//        newTeacherBtn.addClickHandler(this);
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
        deleteSelectedBtn.addClickHandler(this);
        addToSchoolClass.addClickHandler(this);
        teacherListBox.addChangeHandler(this);
    }

    public void init() {
//        newTeacherBtn.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == backBtn) {
            teachersInSchoolclassPresenter.goBackToSchoolClasses();
        } else if (event.getSource() == deleteSelectedBtn) {
            teachersInSchoolclassPresenter.removeSelectedFromSchoolClass();
        } else if (event.getSource() == addToSchoolClass) {
            teachersInSchoolclassPresenter.addTeacherToSchoolClass(addTeacherList.get(teacherListBox.getSelectedIndex()).getKey());
//        } else if (event.getSource() == teacherListBox) {
//            studentsInSchoolclassPresenter.updateTeacherList();
//        } else if (event.getSource() == checkBox) {
//
        }
    }

    @Override
    public void onChange(ChangeEvent event) {
        //    LOG.log(Level.INFO, "Listbox event:" + event.getSource().toString());
    }

    public void updateView(Map<String, TeachersInSchoolclassPresenter.TeacherItem> data) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(data.values());
        dataProvider.refresh();
    }

    public void updateTeacherList(List<TeacherListBoxItem> data) {
        addTeacherList = data;
        teacherListBox.clear();
        for (TeacherListBoxItem item : data) {
            teacherListBox.addItem(item.getTeacherName());
        }
    }

    private void cellSelected(int row, int column) {
        LOG.log(Level.FINE, "Clicked row x col " + row + "x" + column + " " + dataProvider.getList().get(row).usercode + " " + dataGrid.getHeader(column).getValue());
        dataGrid.getHeader(column);
        teachersInSchoolclassPresenter.selectItem((TeachersInSchoolclassPresenter.TeacherItem) dataProvider.getList().get(row), column);
    }

}
