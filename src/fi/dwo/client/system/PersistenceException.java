/*
 * Created on Feb 25, 2005
 *
 */
package fi.dwo.client.system;

import org.apache.xmlrpc.applet.XmlRpcException;

/**
 * @author thijsk
 *  
 */
public class PersistenceException extends Exception {

    public final static int EX_XML_RPC = -3;

    public final static int EX_DB = -2;

    public final static int EX_IO = -1;

    public final static int EX_UNKNOWN_ERROR = 0;

    public PersistenceException(int exception) {
        super(getMesgFromInt(exception));
        code = exception;
    }

    private int code; 
    
    public int getCode() { 
    	return code;
    }

    public PersistenceException(int exXmlRpc, Exception e) {
		this(exXmlRpc);
		initCause(e);
	}

	private static String getMesgFromInt(int exception) {
        String result = null;
        switch (exception) {
        default:
            result = TextMapper.getText(TextMapper.EX_UNKNOWN_ERROR);
            break;
        }

        return result;

    }
	
	public String getMessage() { 
		if(getCause() == null)
			return super.getMessage();
		return super.getMessage() + "\n" + getCause().getMessage();
	}
	
}