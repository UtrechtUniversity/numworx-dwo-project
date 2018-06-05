package nl.uu.fi.dwo.lms.gwtclient.gwt.gui.old;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Image;

/**
 * Image cell component.
 * 
 * @author G.A.J. van der Plas
 */
public class DwoImageCell extends AbstractCell<String> {
    Image image;

    public DwoImageCell(Image anImage) {
        super("click", "keydown");
        image = anImage;
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
        if (value != null) {
            sb.appendHtmlConstant("<img src=\'" + image.getUrl() + "\'></img>");
        }
    }

//    @Override
//    public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
//        if (value == null) {
//            return;
//        }
//        super.onBrowserEvent(context, parent, value, event, valueUpdater);
//        if ("click".equals(event.getType())) {
//
//            Window.alert("key, row x col "+context.getKey().toString()+","+context.getIndex()+"x"+context.getColumn());
//        }
//    }
}
