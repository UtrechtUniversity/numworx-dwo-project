package nl.uu.fi.dwo.lms.gwtclient.gwt.jsutil;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import nl.uu.fi.dwo.rest.dom.entities.SimpleValidUserFieldsChecker;
import nl.uu.fi.dwo.rest.locale.Dwo2LocaleMessageCode;

/**
 *
 * @author Gert van der Plas
 */
@JsType(name = "jsUserFieldValidator", namespace="dwoAPI")
public class UserFieldValidator {

    @JsMethod
    public boolean checkUsername(String username) {
        return SimpleValidUserFieldsChecker.isValidUserName(username);
    }

    @JsMethod
    public String invalidUsernameMsg(){
        return new DwoMessageTranslator().translate(Dwo2LocaleMessageCode.GUI_AnIncorrectPasswordWasGiven.name());
    }
    
    @JsMethod
    public boolean checkEmail (String email) {
        return SimpleValidUserFieldsChecker.isValidEmail(email);
//        return true;
    }
//
//    @JsMethod
//    public String invalidEmailMsg(){
//        return new DwoMessageTranslator().translate(Dwo2LocaleMessageCode.GUI_AnIncorrectPasswordWasGiven.name());
//    }

    @JsMethod
    public boolean checkPassword(String password) {
        return SimpleValidUserFieldsChecker.isValidPassword(password);
    }

    @JsMethod
    public String invalidEmailMsg(){
        return new DwoMessageTranslator().translate(Dwo2LocaleMessageCode.GUI_AnIncorrectPasswordWasGiven.name());
    }
}
