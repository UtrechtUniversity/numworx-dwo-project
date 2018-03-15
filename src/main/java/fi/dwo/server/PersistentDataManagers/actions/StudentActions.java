/**
 * Copyrighted Mar 7, 2018
 */
package fi.dwo.server.PersistentDataManagers.actions;

import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelData;
import fi.dwo.server.PersistentDataManagers.access.StudentDomainAuthorizer;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelData;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 *
 * @author Gert van der Plas
 */
public interface StudentActions {
    /** Sets student model data */
    public void setStudentModelData(StudentDomainAuthorizer.Context ctx, PersistentStudentModelData data) throws Dwo2Exception;
    public PersistentStudentModelData getStudentModelData(StudentDomainAuthorizer.Context ctx, PersistentScoContext pScoContext) throws Dwo2Exception;
    /** Gets student model data for a specific */
    public DomStudentModelData getStudentModelData(StudentDomainAuthorizer.Context context, PersistentStudentModelContext pStudentModel) throws Dwo2Exception;
}
