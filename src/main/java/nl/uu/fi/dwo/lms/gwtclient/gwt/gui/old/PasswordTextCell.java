package nl.uu.fi.dwo.lms.gwtclient.gwt.gui.old;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 *
 * @author Gert van der Plas
 */
public class PasswordTextCell extends EditTextCell {

    @Override
    public void render(Context context, String value, SafeHtmlBuilder sb) {
        super.render(context, "*********", sb);
    }
}
