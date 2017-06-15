package nl.uu.fi.dwo.lms.gwtclient.gwt.gui;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;

/**
 * Basic gwt cell component.
 * 
 * @author G.A.J. van der Plas
 */
public class DwoCell extends AbstractCell<String> {

    public DwoCell() {
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

            Window.alert("key, row x col "+context.getKey().toString()+","+context.getIndex()+"x"+context.getColumn());
        }
    }
}
