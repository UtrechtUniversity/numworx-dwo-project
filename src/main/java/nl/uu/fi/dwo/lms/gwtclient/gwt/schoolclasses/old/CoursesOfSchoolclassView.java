package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.old;

import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.old.CourseItem;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.gui.PasswordTextCell;
import nl.uu.fi.dwo.lms.gwtclient.gwt.icons.DwoResources;
import nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses.ClassCourseItem;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.locale.DwoLocalesForGWT;

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
    SimplePager pager;
    @UiField(provided = true)
    Tree tree;
    @UiField
    Button backBtn;
    @UiField
    DwoLocalesForGWT rb = DwoLocalesForGWT.instance;
    
    private static final DwoResources resources = GWT.create(DwoResources.class);
    Image loadingImage = new Image(resources.loadingIcon());
    Image emptyImage = new Image(resources.emptyIcon());

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
                sb.appendHtmlConstant("<div title=\"I'm a tooltip\">");
                sb.appendEscaped(value);
                sb.appendHtmlConstant("</div>");
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
                if (value.booleanValue() != true) {
                    //attach a classcourse
                    coursesOfSchoolclassPresenter.attachItemToSchoolClass((ClassCourseItem) context.getKey());
                } else {
                    //detach a classcourse
                    coursesOfSchoolclassPresenter.detachItemFromSchoolClass((ClassCourseItem) context.getKey());
                }
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
//        final SingleSelectionModel<CourseCellNode> selectionModel = new SingleSelectionModel<CourseCellNode>(CourseCellNode.KEY_PROVIDER);
//        selectionModel.addSelectionChangeHandler(
//                new SelectionChangeEvent.Handler() {
//            public void onSelectionChange(SelectionChangeEvent event) {
//                StringBuilder sb = new StringBuilder();
//                boolean first = true;
//                List<CourseCellNode> selected = new ArrayList<CourseCellNode>(
//                        selectionModel.getSelectedSet());
////            Collections.sort(selected);
//                for (CourseCellNode value : selected) {
//                    if (first) {
//                        first = false;
//                    } else {
//                        sb.append(", ");
//                    }
//                    sb.append(value.getName());
//                }
//                //selectedLabel.setText(sb.toString());
//            }
//        });
//        CellTree.Resources res = GWT.create(CellTree.BasicResources.class);
//        tree = new CellTree(new CoursesOfSchoolclassTreeModel(coursesOfSchoolclassPresenter), MyTreeCell(coursesOfSchoolclassPresenter.getTree()));
//        tree = new CellTree(new CoursesOfSchoolclassTreeModel(coursesOfSchoolclassPresenter), new CourseItem (null,"root"));
        CourseItem item = new CourseItem(null, "root");
        //CoursesOfSchoolclassTreeModel model = new CoursesOfSchoolclassTreeModel(coursesOfSchoolclassPresenter);
        tree = new Tree();
        tree.setAnimationEnabled(true);
        //tree.setSelectionMode(SelectionMode.SINGLE);

        tree.addSelectionHandler(new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection(SelectionEvent event) {
                if (event.getSelectedItem() instanceof TreeItem) {
                    TreeItem treeItem = (TreeItem) event.getSelectedItem();
                    if (treeItem.getUserObject() instanceof CourseItem) {
                        ClassCourseItem item = (ClassCourseItem) treeItem.getUserObject();
                        coursesOfSchoolclassPresenter.setSelectedItem(item);
                    }
                }
            }
        });

        final CoursesOfSchoolclassView.MyCell cell = new CoursesOfSchoolclassView.MyCell();
        final CoursesOfSchoolclassView.MyClickCell clickCell = new CoursesOfSchoolclassView.MyClickCell();

        //name
        Column<ClassCourseItem, String> value = new Column<ClassCourseItem, String>(cell) {
            @Override
            public String getValue(ClassCourseItem object) {
                try {
                    return object.getName();
                } catch (Exception e) {
                    return "";

                }
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
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant("<div title=\"Click to sort\">")
                .appendHtmlConstant(rb.GUI_Table_CourseName())
                .appendHtmlConstant("</div>");
        dataGrid.addColumn(value, builder.toSafeHtml());

        //hasData
        checkBox = new MyCheckBoxCell(true, true);
        Column<ClassCourseItem, Boolean> bValue = new Column<ClassCourseItem, Boolean>(checkBox) {
            @Override
            public Boolean getValue(ClassCourseItem object) {
                return object.getHasStudentData();
            }
        };

        bValue.setSortable(false);
        builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant("<div title=\"selected means available to class\">")
                .appendHtmlConstant(rb.GUI_Table_AssignedtoClass())
                .appendHtmlConstant("</div>");
        dataGrid.addColumn(bValue, builder.toSafeHtml());

        //hasType    
        List<String> categoryNames = new ArrayList<String>();
        for (CourseType ct : CourseType.values()) {
            categoryNames.add(ct.name());
        }
        SelectionCell categoryCell = new SelectionCell(categoryNames);
        value = new Column<ClassCourseItem, String>(categoryCell) {
            @Override
            public String getValue(ClassCourseItem object) {
                try {

                    return object.getType();
                } catch (Exception e) {
                    return "";

                }
            }
        };
        value.setFieldUpdater(new FieldUpdater<ClassCourseItem, String>() {

            @Override
            public void update(int index, ClassCourseItem t, String value) {
                coursesOfSchoolclassPresenter.setCourseType(t.getKey(), value);
                dataProvider.refresh();
            }
        });
        dataGrid.addColumn(value, rb.GUI_Table_CourseType());

        //from
        EditTextCell textCell = new EditTextCell();

        Column<ClassCourseItem, String> dateColumn = new Column<ClassCourseItem, String>(textCell) {
//use //https://chmln.github.io/flatpickr/examples/ to pick stuff and then parse

            @Override
            public String getValue(ClassCourseItem object) {
                return (object.getFrom() == null) ? "" : DateTimeFormat.getFormat("yyyy-MM-dd HH:mm").format(object.getFrom()); //object.getFrom();
            }

        };
        dateColumn.setFieldUpdater(new FieldUpdater<ClassCourseItem, String>() {

            @Override
            public void update(int index, ClassCourseItem t, String value) {
                coursesOfSchoolclassPresenter.setFromDate(t.getKey(), value);
                dataProvider.refresh();
            }
        });

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
                if (o1 != null && o1.getFrom() != null && o2 != null) {
                    return (o1.getFrom().before(o2.getFrom())) ? -1 : 1;
                }
                return -1;
            }
        });
        dataGrid.addColumnSortHandler(columnSortHandler);
        builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant("<div title=\"Enter YYYY-MM-DD HH:SS or empty to reset.\">")
                .appendHtmlConstant(rb.GUI_Table_FromDate())
                .appendHtmlConstant("</div>");

        dataGrid.addColumn(dateColumn, builder.toSafeHtml());

        //to
        textCell = new EditTextCell();
        dateColumn = new Column<ClassCourseItem, String>(textCell) {
//use //https://chmln.github.io/flatpickr/examples/ to pick stuff and then parse

            @Override
            public String getValue(ClassCourseItem object) {
                return (object.getTo() == null) ? "" : DateTimeFormat.getFormat("yyyy-MM-dd HH:mm").format(object.getTo()); //object.getFrom();
            }

        };
//        value.setSortable(true);
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

        dateColumn.setFieldUpdater(new FieldUpdater<ClassCourseItem, String>() {

            @Override
            public void update(int index, ClassCourseItem t, String value) {
                coursesOfSchoolclassPresenter.setToDate(t.getKey(), value);
                dataProvider.refresh();
            }
        });
        builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant("<div title=\"Enter 'YYYY-MM-DD HH:SS' or empty to reset.\">")
                .appendHtmlConstant(rb.GUI_Table_ToDate())
                .appendHtmlConstant("</div>");

        dataGrid.addColumn(dateColumn, builder.toSafeHtml());

        // accessKey
        
        builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant("<div title=\"Accesskey for assessments.\">")
                .appendHtmlConstant(rb.GUI_Label_Password())
                .appendHtmlConstant("</div>");
        textCell = new PasswordTextCell();
        Column<ClassCourseItem, String> accessKeyColumn = new Column<ClassCourseItem, String>(textCell) {

            @Override
            public String getValue(ClassCourseItem object) {
            	String key = object.getAccessKey();
                return (key == null) ? "" : key;
            }

        };
        accessKeyColumn.setFieldUpdater(new FieldUpdater<ClassCourseItem, String>() {

            @Override
            public void update(int index, ClassCourseItem t, String value) {
            	
                coursesOfSchoolclassPresenter.setAccessKey(t.getKey(), value);
                dataProvider.refresh();
            }
        });
        
        
        
        dataGrid.addColumn(accessKeyColumn, builder.toSafeHtml());
        
        dataGrid.setEmptyTableWidget(new Label("empty"));
        dataGrid.setRowData(0, data);
        dataGrid.setRowCount(data.size(), true);
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);
        pager.setPageSize(dataGrid.getPageSize());

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

    @Override
    public void updateTable(List<ClassCourseItem> items) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(items);
        dataProvider.refresh();
    }

    @Override
    public void setTree(ClassCourseItem item) {
        TreeItem selItem = tree.getSelectedItem();
        for (int i = tree.getItemCount() - 1; i >= 0; i--) {
            tree.removeItem(tree.getItem(i));
        }
        for (ClassCourseItem child : coursesOfSchoolclassPresenter.getNodeChildren(item.getKey())) {
            TreeItem treeItem = new TreeItem();
            String name = (child.getName() != null ? child.getName() : "null");
            treeItem.setText(name);
            treeItem.setUserObject(child);
            tree.addItem(treeItem);
//            tree.setSelectedItem(treeItem);
            fillTreeNode(treeItem, selItem);
            treeItem.setState(true);//do after adding to tree.
        }
//        coursesOfSchoolclassPresenter.setSelectedItem(item);
    }

    private void fillTreeNode(TreeItem treeItem, TreeItem selItem) {
        ClassCourseItem item = (ClassCourseItem) treeItem.getUserObject();
        for (ClassCourseItem i : coursesOfSchoolclassPresenter.getNodeChildren(item.getKey())) {
            if (!i.getIsLeaf()) {
                TreeItem childItem = new TreeItem();
                String name;
                name = i.getName();
                if (i.getHasStudentData()) {
                    childItem.getElement().getStyle().setFontStyle(Style.FontStyle.ITALIC);
                    childItem.getElement().getStyle().setColor("blue");
                } else {
                    childItem.getElement().getStyle().setFontStyle(Style.FontStyle.NORMAL);
                    childItem.getElement().getStyle().setColor("black");
                }
                childItem.setText(name);
                childItem.setUserObject(i);
                treeItem.addItem(childItem);
                //treeItem.setState(true);
                fillTreeNode(childItem, selItem);
                if (i.getHasStudentData() || treeItem.isSelected()) {
                    childItem.setState(true);
                }
            }
//            else if (item.getKey().equals(((ClassCourseItem) selItem.getUserObject()).getKey())) {
//                treeItem.setSelected(true);
//            }

        }
        if (selItem != null && ((ClassCourseItem) treeItem.getUserObject()).getKey().equals(((ClassCourseItem) selItem.getUserObject()).getKey())) {
            treeItem.setSelected(true);
            coursesOfSchoolclassPresenter.setSelectedItem(((ClassCourseItem) treeItem.getUserObject()));
        }

    }

    private void cellSelected(int row, int column) {
//        LOG.log(Level.FINE, "Clicked row x col " + row + "x" + column + " " + dataProvider.getList().get(row).usercode + " " + dataGrid.getHeader(column).getValue());
//        dataGrid.getHeader(column);
//        coursesOfSchoolclassPresenter.selectItem((ClassCourseItem) dataProvider.getList().get(row), column);
    }

    public void setEmptyTableMessage() {
        dataGrid.setEmptyTableWidget(emptyImage);
    }

    public void setLoadingTableMessage() {
        dataGrid.setEmptyTableWidget(loadingImage);
    }
}
