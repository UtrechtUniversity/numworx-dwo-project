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
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    CellTree tree;
    @UiField
    Button backBtn;

    private CoursesOfSchoolclassPresenter coursesOfSchoolclassPresenter;
    private ListDataProvider<ClassCourseItem> dataProvider = new ListDataProvider<ClassCourseItem>();

    private ClassCourseItem selected;
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
//                cellSelected(context.getIndex(), context.getColumn());
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

    public static class MyTreeCell extends AbstractCell<CourseItem> {

        public MyTreeCell() {
            super("click", "keydown");
        }
        
        @Override
        public void render(com.google.gwt.cell.client.Cell.Context context, CourseItem value, SafeHtmlBuilder sb) {
            if (value != null) {
                sb.appendEscaped(value.getName());
            }
        }

//        @Override
//        public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, ClassCourseItem value, NativeEvent event, ValueUpdater<String> valueUpdater) {
//            if (value == null) {
//                return;
//            }
//            super.onBrowserEvent(context, parent, value, event, valueUpdater);
//            if ("click".equals(event.getType())) {
////                LOG.log(Level.INFO, "key "+context.getKey());
//                cellSelected(context.getIndex(), context.getColumn());
//            }
//        }
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
//                coursesOfSchoolclassPresenter.selectItem((ClassCourseItem) context.getKey(), 5);
                LOG.log(Level.INFO, "key " + context.getKey() + " boolean " + value);
            }
        }
    }

    public CoursesOfSchoolclassView(CoursesOfSchoolclassPresenter sp) {
        coursesOfSchoolclassPresenter = sp;
        coursesOfSchoolclassPresenter.setView(this);
        String[] tableHeaders = sp.getTableHeaders();
        dataGrid = new CellTable<String>();

        dataProvider.addDataDisplay(dataGrid);
        dataGrid.setSkipRowHoverCheck(true);
        dataGrid.setKeyboardSelectionPolicy(com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);
        List<ClassCourseItem> data = dataProvider.getList();

        //Celltree inits
//        final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>(ContactDatabase.ContactInfo.KEY_PROVIDER);
//    selectionModel.addSelectionChangeHandler(
//        new SelectionChangeEvent.Handler() {
//          public void onSelectionChange(SelectionChangeEvent event) {
//            StringBuilder sb = new StringBuilder();
//            boolean first = true;
//            List<ContactInfo> selected = new ArrayList<ContactInfo>(
//                selectionModel.getSelectedSet());
//            Collections.sort(selected);
//            for (ContactInfo value : selected) {
//              if (first) {
//                first = false;
//              } else {
//                sb.append(", ");
//              }
//              sb.append(value.getFullName());
//            }
//            //selectedLabel.setText(sb.toString());
//          }
//        });
        CellTree.Resources res = GWT.create(CellTree.BasicResources.class);
//        tree = new CellTree(new CoursesOfSchoolclassTreeModel(coursesOfSchoolclassPresenter), MyTreeCell(coursesOfSchoolclassPresenter.getTree()));
        tree = new CellTree(new CoursesOfSchoolclassTreeModel(coursesOfSchoolclassPresenter), "root");
        tree.setAnimationEnabled(true);

        final CoursesOfSchoolclassView.MyCell cell = new CoursesOfSchoolclassView.MyCell();
        final CoursesOfSchoolclassView.MyClickCell clickCell = new CoursesOfSchoolclassView.MyClickCell();

        //name
        Column<ClassCourseItem, String> value = new Column<ClassCourseItem, String>(cell) {
            @Override
            public String getValue(ClassCourseItem object) {
                return object.getName();
            }
        };
        value.setSortable(true);
        ListHandler<ClassCourseItem> columnSortHandler = new ListHandler<ClassCourseItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<ClassCourseItem>() {
            public int compare(ClassCourseItem o1, ClassCourseItem o2) {
                if (o1 == o2) {
                    return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                    return (o2 != null) ? o1.getName().compareTo(o2.getName()) : 1;
                }
                return -1;
            }
        });
        dataGrid.addColumnSortHandler(columnSortHandler);
        dataGrid.addColumn(value, tableHeaders[0]);

        //hasData
        value = new Column<ClassCourseItem, String>(cell) {
            @Override
            public String getValue(ClassCourseItem object) {
                return object.getHasStudentData().toString();
            }
        };
        value.setSortable(true);
        columnSortHandler = new ListHandler<ClassCourseItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<ClassCourseItem>() {
            public int compare(ClassCourseItem o1, ClassCourseItem o2) {
                if (o1 == o2) {
                    return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                    return (o2 != null) ? o1.getHasStudentData().compareTo(o2.getHasStudentData()) : 1;
                }
                return -1;
            }
        });
        dataGrid.addColumnSortHandler(columnSortHandler);
        dataGrid.addColumn(value, tableHeaders[1]);

        //hasType
        value = new Column<ClassCourseItem, String>(cell) {
            @Override
            public String getValue(ClassCourseItem object) {
                return object.getType();
            }
        };
        value.setSortable(true);
        columnSortHandler = new ListHandler<ClassCourseItem>(
                data);
        columnSortHandler.setComparator(value,
                new Comparator<ClassCourseItem>() {
            public int compare(ClassCourseItem o1, ClassCourseItem o2) {
                if (o1 == o2) {
                    return 0;
                }

                // Compare the name columns.
                if (o1 != null) {
                    return (o2 != null) ? o1.getType().compareTo(o2.getType()) : 1;
                }
                return -1;
            }
        });
        dataGrid.addColumnSortHandler(columnSortHandler);
        dataGrid.addColumn(value, tableHeaders[2]);

        //from
        value = new Column<ClassCourseItem, String>(cell) {
            @Override
            public String getValue(ClassCourseItem object) {
                return object.getFrom().toString();
            }
        };
        value.setSortable(true);
//        columnSortHandler = new ListHandler<ClassCourseItem>(
//                data);
//        columnSortHandler.setComparator(value,
//                new Comparator<ClassCourseItem>() {
//            public int compare(ClassCourseItem o1, ClassCourseItem o2) {
//                if (o1 == o2) {
//                    return 0;
//                }
//
//                // Compare the name columns.
//                if (o1 != null) {
//                    return (o2 != null) ? o1.getFrom()(0.compareTo(o2.getFrom())) : 1;
//                }
//                return -1;
//            }
//        });
//        dataGrid.addColumnSortHandler(columnSortHandler);
        dataGrid.addColumn(value, tableHeaders[3]);

        //to
        value = new Column<ClassCourseItem, String>(cell) {
            @Override
            public String getValue(ClassCourseItem object) {
                return object.getTo().toString();
            }
        };
        value.setSortable(true);
//        columnSortHandler = new ListHandler<ClassCourseItem>(
//                data);
//        columnSortHandler.setComparator(value,
//                new Comparator<ClassCourseItem>() {
//            public int compare(ClassCourseItem o1, ClassCourseItem o2) {
//                if (o1 == o2) {
//                    return 0;
//                }
//
//                // Compare the name columns.
//                if (o1 != null) {
//                    return (o2 != null) ? o1.getTo().compareTo(o2.getTo()) : 1;
//                }
//                return -1;
//            }
//        });
//        dataGrid.addColumnSortHandler(columnSortHandler);
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

    public void refreshView() {

    }

    private void cellSelected(int row, int column) {
//        LOG.log(Level.FINE, "Clicked row x col " + row + "x" + column + " " + dataProvider.getList().get(row).usercode + " " + dataGrid.getHeader(column).getValue());
//        dataGrid.getHeader(column);
//        coursesOfSchoolclassPresenter.selectItem((ClassCourseItem) dataProvider.getList().get(row), column);
    }

}
