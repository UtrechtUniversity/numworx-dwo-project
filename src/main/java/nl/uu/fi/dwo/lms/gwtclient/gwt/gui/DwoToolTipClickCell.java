package nl.uu.fi.dwo.lms.gwtclient.gwt.gui;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 *
 * @author G.A.J. van der Plas
 */
public class DwoToolTipClickCell extends DwoToolTipCell {

    final String toolTip;

    public DwoToolTipClickCell(){
        super();
        toolTip = null;
    }
    
    public DwoToolTipClickCell(String toolTipText) {
        super();
        toolTip = toolTipText;
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
        if (value != null) {
            if (toolTip != null) {
                sb.appendHtmlConstant("<div title=\"" + toolTip + "\">");
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

}
