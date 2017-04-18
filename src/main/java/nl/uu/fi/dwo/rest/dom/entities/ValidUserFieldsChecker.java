package nl.uu.fi.dwo.rest.dom.entities;

/**
 * ValidUserFieldsChecker checks the class for valid user fields. Currently it
 * checks the username characters and size, and the email characters.
 *
 * @author Gert van der Plas
 */
public final class ValidUserFieldsChecker extends SimpleValidUserFieldsChecker {

    /**
     * Tests for RFC 5322 addresses, but not RFC 6530.
     *
     * @param email
     * @return
     */
    public static boolean isValidEmail(String email) {
// JavaMail package approach
//        boolean result = true;
//        try {
//            InternetAddress emailAddr = new InternetAddress(email);
//            emailAddr.validate();
//        }
//        catch (AddressException ex) {
//            result = false;
//        }
//        return result;
//from http://stackoverflow.com/questions/624581/what-is-the-best-java-email-address-validation-method
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean isValidUserName(String username) {
        //nieuwe valid username is alphanumeriek met '-', '_' en '@'.
        return username.matches("[A-Za-z0-9_.-]+");
//        if (username == null || username.isEmpty()) {
//            return false;
//        }
//        if (!username.trim().equals(username)) {
//            return false;
//        }
//        char[] chars = username.toCharArray();
//        for (int i = 0; i < chars.length; i++) {
//            char c = chars[i];
//            if (c < 0x20 || c >= 0x7F // ascii, no space?, no delete?
//                    || c == '(' // aselect verbiedt =*?
//                    || c == ')' // maar ook , \ ( en ) mogen niet
//                    || c == '*' || c == '?' || c == '=' || c == '\\' || c == ',' || c == ';' // beter
//                    // van
//                    // niet
//                    // in
//                    // LDAP
//                    || c == '+' || c == '#' // nieuw, werkt niet in PHP
//                    ) {
//                return false;
//            }
//        }
//        return true;
    }
}
