/**
 * Copyrighted Mar 9, 2018
 */
package fi.dwo.server.PersistentDataManagers.actions;

import fi.dwo.server.PersistentDataManagers.access.StudentDomainAuthorizer;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelData;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 *
 * @author Gert van der Plas
 */
public class MySQLStudentActions implements StudentActions {

    @Override
    public void setStudentModelData(StudentDomainAuthorizer.StudentPersistentContext ctx, DomStudentModelData data) throws Dwo2Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
