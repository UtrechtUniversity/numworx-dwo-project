package fi.dwo.dwojapplet.domain.utils;

import fi.dwo.dwojapplet.domain.utils.Util;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import fi.dwo.dwojapplet.domain.utils.Util.ResultException;
import fi.dwo.dwojapplet.domain.utils.Util.NoException;

public class CheckEmail {

    private String service;
    private Throwable cause;

    public Throwable getCause() {
        return cause;
    }

    static final String PHPrefix = "/scripts/php/fi/checkmail/"; // FIXME
    static final String CHECKEMAIL = PHPrefix + "checkMail.php";
    static final String EMAIL = "emailaddress";

    public CheckEmail() {
        this("http://www.fisme.science.uu.nl" + CHECKEMAIL);
    }

    public CheckEmail(URL base) throws MalformedURLException {
        this(new URL(base, CHECKEMAIL).toExternalForm());
    }

    public CheckEmail(String url) {
        service = url;
    }

    public boolean check(String emailaddress) {
        cause = null;
        StringBuffer sb = new StringBuffer(service);
        sb.append("?");
        Util.encode(EMAIL, emailaddress, sb);
        String result;
        try {
            result = Util.call(sb);
            cause = new NoException(result);
        } catch (ResultException e) {
            cause = e;
            return false;
        } // Deze 3 exceptions ontstaan als de service down is. 
        // Dan moeten we dus helaas 'true' afgeven.
        catch (MalformedURLException e) {
            e.printStackTrace();
            cause = e;
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            cause = e;
            return true;
        } catch (IOException e) {
            cause = e;
            return true;
        }
        return result.startsWith("2");
    }
}
