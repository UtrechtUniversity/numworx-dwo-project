/**
 * Copyrighted Mar 7, 2018
 */
package fi.dwo.server.PersistentDataManagers.actions;

import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelData;
import fi.dwo.server.PersistentDataManagers.access.StudentDomainAuthorizer;
import java.util.List;

import javax.ws.rs.core.UriInfo;

import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 *
 * @author Gert van der Plas
 */
public interface StudentActions {
    public List<PersistentStudentModelContext> getStudentModels(StudentDomainAuthorizer.Context context) throws Dwo2Exception;
    public void setStudentModelData(StudentDomainAuthorizer.Context ctx, PersistentStudentModelData data) throws Dwo2Exception;
    public PersistentStudentModelData getStudentModelData(StudentDomainAuthorizer.Context ctx, PersistentScoContext pScoContext) throws Dwo2Exception;
    public DomStudentModelDataScore getStudentModelData(StudentDomainAuthorizer.Context context, PersistentStudentModelContext pStudentModel) throws Dwo2Exception;
    public DomLRS getLRS(StudentDomainAuthorizer.Context context, UriInfo info);
}
