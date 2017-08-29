package nl.uu.fi.dwo.lms.gwtclient.gwt.gui;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Image;

/**
 * Image cell with tooltip support.
 *
 * @author G.A.J. van der Plas
 */
public class DwoImageToolTipCell extends DwoImageCell {
    String toolTip;

    public DwoImageToolTipCell(Image anImage) {
        super(anImage);
        toolTip=null;
    }

    
    public DwoImageToolTipCell(Image anImage, String aToolTip) {
        super(anImage);
        toolTip = aToolTip;
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
        if (value != null) {
            if (toolTip != null) {
                sb.appendHtmlConstant("<div title=\"" + toolTip + "\">");
            }
            sb.appendHtmlConstant("<img src=\'" + image.getUrl() + "\'></img>");
            if (toolTip != null) {
                sb.appendHtmlConstant("</div>");
            }
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
