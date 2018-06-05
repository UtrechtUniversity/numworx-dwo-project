package nl.uu.fi.dwo.lms.gwtclient.gwt.gui.old;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Image;

/**
 * Basic gwt cell component.
 *
 * @author G.A.J. van der Plas
 */
public class DwoImageToolTipClickCell extends DwoImageToolTipCell {

    SelectedCellHandler handler;

    public DwoImageToolTipClickCell(Image anImage, String aToolTip) {
        super(anImage, aToolTip);
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
        if (value != null) {
            if (toolTip != null) {
                sb.appendHtmlConstant("<div title=\"" + toolTip + "\">");
            }
            sb.appendHtmlConstant("<a href='javascript:;'>");
            sb.appendHtmlConstant("<img src=\'" + super.image.getUrl() + "\'></img>");
            sb.appendHtmlConstant("</a>");
        }
        if (toolTip != null) {
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
            if (handler != null) {
                handler.onSelectedCell(context, value);
            }
        }
    }

    public void addSelectedCellHandler(SelectedCellHandler aHandler) {
        handler = aHandler;
    }

}
