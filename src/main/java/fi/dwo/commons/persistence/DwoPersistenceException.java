/*Copyrighted 2015. */
package fi.dwo.commons.persistence;

import fi.dwo.commons.system.TextMapper;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Persistence error message. This exception contains an error type that can be
 * localized for translation. The message part is not localized as it is assumed
 * to contain stack traces and other internal code info. For GUI messaging it is
 * recommended to use the method getLocalizedTypeMessage.
 *
 * @author G.A.J. van der Plas
 */
public class DwoPersistenceException extends Exception {

    public static enum PersistenceErrorType { //enumName maps to DwoPersistenceException.enumName in Exceptions.properties file.
        ObjectNotFound,
        DataModelConstraintViolated,
    }
    private PersistenceErrorType type;

    public DwoPersistenceException(PersistenceErrorType type, String message) {
        super(message);
    }

    public String getLocalizedTypeMessage(Locale locale) {
        ResourceBundle localeLookup = ResourceBundle.getBundle("Exceptions", locale);
        String msg = TextMapper.getText(localeLookup.getString("DwoPersistenceException" + type.name())); 
        return msg;
    }

    @Override
    public String getMessage() {
        return super.getMessage(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the type
     */
    public PersistenceErrorType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(PersistenceErrorType type) {
        this.type = type;
    }

}
