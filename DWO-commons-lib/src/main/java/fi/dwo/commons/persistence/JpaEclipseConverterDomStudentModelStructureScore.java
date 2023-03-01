/**
 * Copyrighted Jan 30, 2018
 */
package fi.dwo.commons.persistence;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.dom.entities.util.GensonMapConverter;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

/**
 *
 * @author Gert van der Plas
 */
public class JpaEclipseConverterDomStudentModelStructureScore implements Converter {
    private Genson g = new GensonBuilder().withConverters(new GensonMapConverter()).create();
    
    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {        
        return g.serialize(objectValue);
    }

    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        DomStudentModelStructureScore data;
            data = g.deserialize((String) dataValue, DomStudentModelStructureScore.class);
            return data;
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public void initialize(DatabaseMapping mapping, Session session) {
        
    }

}
