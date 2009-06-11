// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\system\\ClassException.java

package fi.dwo.client.system;

public class SchoolException extends Exception {

    public final static int EX_XML_RPC = -3;

    public final static int EX_DB = -2;

    public final static int EX_IO = -1;

    public final static int EX_UNKNOWN_ERROR = 0;

    public final static int SE_SCHOOL_EXISTS = 1;
    public final static int SE_SCHOOL_UNSUPPORTED = 2;

    /**

     */
    public SchoolException(int exception) {
        super(getMesgFromInt(exception));
    }

    private static String getMesgFromInt(int exception) {
        String result = null;
        switch (exception) {
        case (SE_SCHOOL_EXISTS):
            result = TextMapper.getText(TextMapper.EXS_SCHOOL_EXISTS);
            break;
        default:
            result = TextMapper.getText(TextMapper.EX_UNKNOWN_ERROR);
            break;
        }

        return result;

    }
}