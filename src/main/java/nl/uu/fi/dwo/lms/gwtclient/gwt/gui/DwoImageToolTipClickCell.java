package nl.uu.fi.dwo.lms.gwtclient.gwt.gui;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;

/**
 * Basic gwt cell component.
 *
 * @author G.A.J. van der Plas
 */
public class DwoImageToolTipClickCell extends DwoImageToolTipCell {

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
}
