package nl.uu.fi.dwo.lms.gwtclient.gwt.gui.old;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 *
 * @author G.A.J. van der Plas
 */
public class DwoStyledClickCell extends DwoCell{

    SelectedCellHandler handler;
    String style;

    public DwoStyledClickCell(){
        super();
        style = null;
    }
    
    public DwoStyledClickCell(String style) {
        
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
        if (value != null) {
            if (style != null) {
                sb.appendHtmlConstant("<div style=\"" + style + "\">");
                //sb.appendHtmlConstant("<a href='javascript:;'>");
                sb.appendHtmlConstant("<a href='javascript:;'>");
                sb.appendEscaped(value);
                sb.appendHtmlConstant("</a>");
                sb.appendHtmlConstant("</div>");
            } else {
                sb.appendEscaped(value);
            }
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
    
    public void addSelectedCellHandler(SelectedCellHandler aHandler){
        handler = aHandler;
    }
    
}
