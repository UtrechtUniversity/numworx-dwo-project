/**
 * Copyrighted Jan 30, 2018
 */
package fi.dwo.commons.persistence;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Gert van der Plas
 */
public class JpaEclipseConverter4JsonObject implements Converter {

    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        return ((JSONObject) objectValue).toJSONString();        
    }

    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse((String) dataValue);
            return json;
        }
        catch (ParseException ex) {
            Logger.getLogger(JpaEclipseConverter4JsonObject.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public void initialize(DatabaseMapping mapping, Session session) {
        
    }

}
