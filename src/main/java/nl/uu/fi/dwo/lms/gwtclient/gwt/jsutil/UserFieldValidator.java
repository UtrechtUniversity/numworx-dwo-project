package nl.uu.fi.dwo.lms.gwtclient.gwt.jsutil;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import nl.uu.fi.dwo.rest.dom.entities.ValidUserFieldsChecker;
import nl.uu.fi.dwo.rest.locale.Dwo2LocaleMessageCode;

/**
 *
 * @author Gert van der Plas
 */
@JsType(name = "jsUserFieldValidator", namespace = JsPackage.GLOBAL)
public class UserFieldValidator {

    @JsMethod
    public boolean checkUsername(String username) {
        return ValidUserFieldsChecker.isValidUserName(username);
    }

    @JsMethod
    public String invalidUsernameMsg(){
        return new DwoMessageTranslator().translate(Dwo2LocaleMessageCode.GUI_AnIncorrectPasswordWasGiven.name());
    }
    
    @JsMethod
    public boolean checkEmail (String email) {
        return ValidUserFieldsChecker.isValidEmail(email);
    }
//
//    @JsMethod
//    public String invalidEmailMsg(){
//        return new DwoMessageTranslator().translate(Dwo2LocaleMessageCode.GUI_AnIncorrectPasswordWasGiven.name());
//    }

    @JsMethod
    public boolean checkPassword(String password) {
        return ValidUserFieldsChecker.isValidUserName(password);
    }

    @JsMethod
    public String invalidEmailMsg(){
        return new DwoMessageTranslator().translate(Dwo2LocaleMessageCode.GUI_AnIncorrectPasswordWasGiven.name());
    }
}
