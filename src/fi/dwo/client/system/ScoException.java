// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\system\\ClassException.java

package fi.dwo.client.system;

public class ScoException extends Exception {

    public final static int EX_XML_RPC = -3;

    public final static int EX_DB = -2;

    public final static int EX_IO = -1;

    public final static int EX_UNKNOWN_ERROR = 0;

    public final static int SE_SCO_EXISTS = 1;
    public final static int SE_NO_APPLET = 2;

    /**

     */
    public ScoException(int exception) {
        super(getMesgFromInt(exception));
    }

    public ScoException(int exception, Exception e) {
    	super(getMesgFromInt(exception),e);	}

	private static String getMesgFromInt(int exception) {
        String result = null;
        switch (exception) {
        case (SE_SCO_EXISTS):
            result = TextMapper.getText(TextMapper.EXS_SCO_EXISTS);
            break;
        case (SE_NO_APPLET):
            result = TextMapper.getText(TextMapper.EXS_NO_APPLET);
            break;
        default:
            result = TextMapper.getText(TextMapper.EX_UNKNOWN_ERROR);
            break;
        }

        return result;

    }
}