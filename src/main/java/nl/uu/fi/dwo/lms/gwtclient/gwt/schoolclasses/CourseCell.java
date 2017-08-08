package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

/**
 *
 * @author Gert van der Plas
 */
public class CourseCell extends AbstractCell<CourseItem> {

    ListDataProvider<CourseCellNode> dataProvider; //for refresh

    public CourseCell(ListDataProvider<CourseCellNode> dataProvider) {
        super("click", "keydown");
        this.dataProvider = dataProvider;
    }

    public void refresh() {
        dataProvider.refresh();
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, CourseItem value, SafeHtmlBuilder sb) {
        if (value != null) {
            sb.appendEscaped(value.getName());
        } else {
            sb.appendEscaped("root");
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
