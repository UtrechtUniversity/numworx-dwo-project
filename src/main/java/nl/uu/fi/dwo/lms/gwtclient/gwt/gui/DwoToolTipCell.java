package nl.uu.fi.dwo.lms.gwtclient.gwt.gui;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 *
 * @author G.A.J. van der Plas
 */
public class DwoToolTipCell extends AbstractCell<String> {

    final String toolTip;

    public DwoToolTipCell(String toolTipText) {
        super("click", "keydown");
        toolTip = toolTipText;
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
        if (value != null) {
            if (toolTip != null) {
                sb.appendHtmlConstant("<div title=\"" + toolTip + "\">");
                //sb.appendHtmlConstant("<a href='javascript:;'>");
                sb.appendEscaped(value);
                sb.appendHtmlConstant("</div>");
            } else {
                sb.appendEscaped(value);
            }
        }
    }

}
