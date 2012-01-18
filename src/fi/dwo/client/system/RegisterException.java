// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\system\\RegisterException.java

package fi.dwo.client.system;

import java.text.MessageFormat;

public class RegisterException extends Exception {
    public final static int EX_XML_RPC = -3;

    public final static int EX_DB = -2;

    public final static int EX_IO = -1;

    public final static int EX_UNKNOWN_ERROR = 0;

    public final static int RE_USER_EXISTS = 1;

    public final static int RE_WRONG_USERNAME_PASSWORD = 2;

    public final static int RE_WRONG_SECOND_PASSWORD = 3;

    public final static int RE_UNKNOWN_SCHOOLGROUP = 4;

    public final static int RE_MANDATORY = 5;
    
    public final static int RE_WRONG_FORMAT = 6;
    
    public final static int RE_WRONG_EMAILFORMAT = 7;
    /**

     */
    public RegisterException(int exception) {
        super(getMesgFromInt(exception));
    }
    
    public RegisterException(int exception, String msg) {
        super(getMesgFromInt(exception, msg));
    }

    public RegisterException(int exception, String[] msg) {
        super(getMesgFromInt(exception, msg));
    }

    private static String getMesgFromInt(int exception, String msg) {
        String[] arguments = {msg};
        return getMesgFromInt(exception, arguments);
    }
    private static String getMesgFromInt(int exception, String[] arguments) {
        String result = null;
        switch (exception) {
        case (RE_MANDATORY):
            result = TextMapper.getText(TextMapper.EXR_MANDATORY);
        	result = MessageFormat.format(result, arguments);
            break;
        case (RE_WRONG_FORMAT):
            result = TextMapper.getText(TextMapper.EXR_WRONG_FORMAT);
    		result = MessageFormat.format(result, arguments);
    		break;
        case RE_USER_EXISTS:
        	result = TextMapper.getText(TextMapper.EXR_USER_EXISTS2);
        	result = MessageFormat.format(result, arguments);
        	break;
        case (RE_WRONG_EMAILFORMAT):
            result = TextMapper.getText(TextMapper.EXR_WRONG_EMAILFORMAT);
    		result = MessageFormat.format(result, arguments);
        break;
        
        default:
            result = getMesgFromInt(exception);
            break;
        }

        return result;
        
    }
    private static String getMesgFromInt(int exception) {
        String result = null;
        switch (exception) {
        case (RE_USER_EXISTS):
            result = TextMapper.getText(TextMapper.EXR_USER_EXISTS);
            break;
        case (RE_WRONG_USERNAME_PASSWORD):
            result = TextMapper.getText(TextMapper.EXR_WRONG_USERNAME_PASSWORD);
            break;
        case (RE_WRONG_SECOND_PASSWORD):
            result = TextMapper.getText(TextMapper.EXR_WRONG_SECOND_PASSWORD);
            break;
        case (RE_UNKNOWN_SCHOOLGROUP):
            result = TextMapper.getText(TextMapper.EXR_UNKNOWN_SCHOOLGROUP);
            break;
        default:
            result = TextMapper.getText(TextMapper.EX_UNKNOWN_ERROR);
            break;
        }

        return result;

    }
}