package fi.dwo.rest.locale;

import com.google.gwt.core.client.GWT;

/**
 * <code>DwoLocalesForGW</code> are to GUI-client-side localized texts, labels, 
 * text fields and such. Exception types and their localized messages explaining 
 * the exception type thrown are to be <code>Dwo2Exceptions</code>.
 * 
 * @author G.A.J. van der Plas
 */
public interface DwoLocalesForGWT extends DwoLocaleMessages{
    DwoLocalesForGWT instance = GWT.create(DwoLocalesForGWT.class);
    
}
