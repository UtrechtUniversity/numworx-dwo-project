package nl.uu.fi.dwo.lms.gwtclient.gwt.gui.old;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * Basic GWT cell component.
 * 
 * @author G.A.J. van der Plas
 */
public class DwoToolTipCell extends DwoCell {
    String toolTip;

    public DwoToolTipCell() {
        super();
        toolTip = null;
    }
    
    public DwoToolTipCell(String aToolTip) {
        super();
        toolTip = aToolTip;
    }

    public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
        if (value != null) {
            if(toolTip!=null) {
                sb.appendHtmlConstant("<div title=\"" + toolTip + "\">");
            }
                sb.appendEscaped(value);
            if(toolTip!=null) {
                sb.appendHtmlConstant("</div>");
            }
        }
    }
}
