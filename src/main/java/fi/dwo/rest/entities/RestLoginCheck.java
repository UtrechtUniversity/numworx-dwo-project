package fi.dwo.rest.entities;

import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomLoginCheck;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RestLoginCheck{

    private DomContext restContext;
    private DomLoginCheck loginCheck;
    /**
     * @return the restContext
     */
    public DomContext getRestContext() {
        return restContext;
    }

    /**
     * @param restContext the restContext to set
     */
    public void setRestContext(DomContext restContext) {
        this.restContext = restContext;
    }

    /**
     * @return the domNewUser
     */
    public DomLoginCheck getDomLoginCheck() {
        return loginCheck;
    }

    /**
     * @param loginCheck the domNewUser to set
     */
    public void setDomLoginCheck(DomLoginCheck loginCheck) {
        this.loginCheck = loginCheck;
    }

}
