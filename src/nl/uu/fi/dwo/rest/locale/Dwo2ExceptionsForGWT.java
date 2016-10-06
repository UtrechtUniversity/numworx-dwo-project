package nl.uu.fi.dwo.rest.locale;

import nl.uu.fi.dwo.rest.locale.Dwo2ExceptionMessages;
import com.google.gwt.core.client.GWT;

/**
  * <code>Dwo2ExceptionsForGWT</code> are Typed Exceptions with localized messages
  * explaining the exception. GUI-client-side localized texts, labels, 
 * text fields and such are to be <code>DwoLocalesForGWT</code>.
*
 * @author G.A.J. van der Plas
 */
public interface Dwo2ExceptionsForGWT extends Dwo2ExceptionMessages{
    Dwo2ExceptionsForGWT instance = GWT.create(Dwo2ExceptionsForGWT.class);
}
