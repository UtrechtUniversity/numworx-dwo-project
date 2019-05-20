/**
 * Copyrighted Jan 30, 2018
 */
package fi.dwo.commons.persistence;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import fi.dwo.commons.util.UEscape;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.util.GensonMapConverter;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

/**
 *
 * @author Gert van der Plas
 */
public class JpaEclipseConverterDomStudentModelStructure implements Converter {
    private Genson g = new GensonBuilder().withConverters(new GensonMapConverter()).create();
    
    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {        
        String object = g.serialize(objectValue);
        object = UEscape.convertUEsc(object);
        return object;
    }

    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        DomStudentModelStructure data;
            data = g.deserialize((String) dataValue, DomStudentModelStructure.class);
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
