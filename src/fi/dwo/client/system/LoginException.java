// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\system\\LoginException.java

package fi.dwo.client.system;

public class LoginException extends Exception {

    public final static int EX_XML_RPC = -3;

    public final static int EX_DB = -2;

    public final static int EX_IO = -1;

    public final static int EX_UNKNOWN_ERROR = 0;

    public final static int LE_UNKNOWN_USER = 1;

    /**

     */
    public LoginException(int exception) {
        super(getMesgFromInt(exception));

    }

    private static String getMesgFromInt(int exception) {
        String result = null;
        switch (exception) {
        case LE_UNKNOWN_USER:
            result = TextMapper.getText(TextMapper.EXL_UNKNOWN_USER);
            break;
        default:
            result = TextMapper.getText(TextMapper.EX_UNKNOWN_ERROR);
            break;
        }

        return result;

    }
}