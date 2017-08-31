package nl.uu.fi.dwo.lms.gwtclient.gwt.gui;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * Basic GWT cell component.
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
            sb.appendEscaped(value);
        }
    }
}
