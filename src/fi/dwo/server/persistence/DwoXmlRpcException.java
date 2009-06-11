/*
 * Created on Mar 21, 2005
 *
 */
package fi.dwo.server.persistence;

import org.apache.xmlrpc.XmlRpcException;

import fi.dwo.client.system.ClassException;
import fi.dwo.client.system.SchoolException;
import fi.dwo.client.system.CourseException;
import fi.dwo.client.system.LoginException;
import fi.dwo.client.system.RegisterException;
import fi.dwo.client.system.ScoException;

/**
 * @author M.J.B. Kupers
 *
 */
public class DwoXmlRpcException extends XmlRpcException {

    /* Local error codes */
    public final static int EXC_CLASS_EXISTS = 1001;
    public final static int EXC_UNKNOWN_SCHOOLGROUP = 1002;
    public final static int EXC_WRONG_USERNAME_PASSWORD = 1003;
    public final static int EXC_UNKNOWN_USER = 1004;
    public final static int EXC_USER_EXISTS = 1005;
    public final static int EXC_COURSE_EXISTS = 1006;
    public final static int EXC_SCO_EXISTS = 1007;
    public final static int EXC_NO_APPLET = 1008;
    public final static int EXC_SCHOOL_EXISTS = 1009;
    public final static int EXC_SCHOOL_UNSUPPORTED = 1010;

    public DwoXmlRpcException(int errorID) {
        super(getErrorCode(errorID), getErrorClass(errorID));
    }
    
    private static String getErrorClass(int errorID) {
        String errorClass = "";
        switch(errorID) {
        	case EXC_CLASS_EXISTS:
        	    errorClass = ClassException.class.getName();
        	    break;
        	case EXC_SCHOOL_UNSUPPORTED:
       		case EXC_SCHOOL_EXISTS:
        	    errorClass = SchoolException.class.getName();
        	    break;
        	case EXC_UNKNOWN_SCHOOLGROUP:
        	    errorClass = RegisterException.class.getName();
        	    break;
        	case EXC_UNKNOWN_USER:
        	    errorClass = LoginException.class.getName();
        	    break;
        	case EXC_USER_EXISTS:
        	    errorClass = RegisterException.class.getName();
        	    break;
        	case EXC_WRONG_USERNAME_PASSWORD:
        	    errorClass = RegisterException.class.getName();
        	    break;
        	case EXC_COURSE_EXISTS:
        	    errorClass = CourseException.class.getName();
        	    break;
        	case EXC_SCO_EXISTS:
        	    errorClass = ScoException.class.getName();
        	    break;
        	case EXC_NO_APPLET:
        	    errorClass = ScoException.class.getName();
        	    break;
        }
        
        return errorClass;
        
    }
    
    private static int getErrorCode(int errorID) {
        int errorCode = 0;
        switch(errorID) {
        	case EXC_CLASS_EXISTS:
        	    errorCode = ClassException.CE_CLASS_EXISTS;
        	    break;
        	case EXC_SCHOOL_EXISTS:
        	    errorCode = SchoolException.SE_SCHOOL_EXISTS;
        	    break;
        	case EXC_SCHOOL_UNSUPPORTED:
        		errorCode = SchoolException.SE_SCHOOL_UNSUPPORTED;
        		break;
        	case EXC_UNKNOWN_SCHOOLGROUP:
        	    errorCode = RegisterException.RE_UNKNOWN_SCHOOLGROUP;
        	    break;
        	case EXC_UNKNOWN_USER:
        	    errorCode = LoginException.LE_UNKNOWN_USER;
        	    break;
        	case EXC_USER_EXISTS:
        	    errorCode = RegisterException.RE_USER_EXISTS;
        	    break;
        	case EXC_WRONG_USERNAME_PASSWORD:
        	    errorCode = RegisterException.RE_WRONG_USERNAME_PASSWORD;
        	    break;
        	case EXC_COURSE_EXISTS:
        	    errorCode = CourseException.CE_COURSE_EXISTS;
        	    break;
        	case EXC_SCO_EXISTS:
        	    errorCode = ScoException.SE_SCO_EXISTS;
        	    break;
        	case EXC_NO_APPLET:
        	    errorCode = ScoException.SE_NO_APPLET;
        	    break;
        }
        
        return errorCode;
        
    }
    
    public String toString() {
        return getMessage();
    }

    
}
