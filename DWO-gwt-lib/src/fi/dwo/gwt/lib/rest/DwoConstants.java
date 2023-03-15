package fi.dwo.gwt.lib.rest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

/**
 *
 * @author Gert van der Plas
 */
public interface DwoConstants extends Constants {
    DwoConstants constants = GWT.create(DwoConstants.class);
    public String server();
}
