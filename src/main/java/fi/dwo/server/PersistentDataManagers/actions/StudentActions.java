/**
 * Copyrighted Mar 7, 2018
 */
package fi.dwo.server.PersistentDataManagers.actions;

import fi.dwo.commons.persistence.entities.PersistentStudentModelData;
import fi.dwo.server.PersistentDataManagers.access.StudentDomainAuthorizer;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 *
 * @author Gert van der Plas
 */
public interface StudentActions {
    public void setStudentModelData(StudentDomainAuthorizer.StudentPersistentContext ctx, PersistentStudentModelData data) throws Dwo2Exception;
}
