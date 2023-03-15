package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import nl.uu.fi.dwo.rest.dom.entities.DomScormValues;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 * General interface for exchanging StudentSco data/context AKA ScormValues
 * 
 * @author velth101
 *
 */
public interface StudentScoDataManager {
  DomScormValues getValues(DomScormValues dom) throws Dwo2Exception;

  Boolean setValues(DomScormValues dom) throws Dwo2Exception;
}
