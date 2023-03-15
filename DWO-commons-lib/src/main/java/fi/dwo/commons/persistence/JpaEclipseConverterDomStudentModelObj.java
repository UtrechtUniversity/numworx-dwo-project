/**
 * Copyrighted Jan 30, 2018
 */
package fi.dwo.commons.persistence;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import fi.dwo.commons.util.UEscape;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.util.GensonMapConverter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

/**
 * Deze class houd rekening met de bug in Mysql jdbc driver 5.1.34
 * @author Gert van der Plas
 */
@SuppressWarnings("serial")
public class JpaEclipseConverterDomStudentModelObj implements Converter {
    private static final Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;
    private static final Charset UTF8 = StandardCharsets.UTF_8;
    private Genson g = new GensonBuilder().withConverters(new GensonMapConverter()).create();
    
    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {        
        String object = g.serialize(objectValue);
        object = UEscape.convertUEsc(object);
        return object;
    }

    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        DomStudentModelObj data;
            String latin1 = (String) dataValue;
            dataValue = new String( latin1.getBytes(ISO_8859_1), UTF8);
            data = g.deserialize((String) dataValue, DomStudentModelObj.class);
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
