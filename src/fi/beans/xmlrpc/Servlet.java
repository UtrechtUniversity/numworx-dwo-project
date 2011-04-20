// $Id: Servlet.java,v 1.3 2004/03/16 12:00:43 wim Exp wim $
package fi.beans.xmlrpc;
/*
 * $Log: Servlet.java,v $
 * Revision 1.3  2004/03/16 12:00:43  wim
 * handler is nu de handler van xmlrpc niet this.
 *
 * Revision 1.2  2004/03/01 15:29:15  wim
 * add constructor en handler om een losse xml-handler te kunnen gebruiken
 * voor JDBC servlets volgens de regels van 1-03-04
 *
 */

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.xmlrpc.XmlRpcServer;

import fi.beans.jdbc.DbConnect;

/**
 * Generieke XML-RPC servlet. Voor gebruik bij 
 * @author wim
 * @see org.apache.xmlrpc.applet.XmlRpcClient
 */
public abstract class Servlet extends HttpServlet {


    protected XmlRpcServer xmlrpc;
    private Object handler;
    private Object lock;
    
/**
 * Standaard init. Moet aangeroepen worden in subclasses!
 */    
    public void init(ServletConfig config) throws ServletException
    {
    	super.init(config);
		xmlrpc = new XmlRpcServer ();
        xmlrpc.addHandler ("$default", handler);
    }
    
/**
 * Handel XML RPC request af. Alle public methods kunnen aangeroepen worden d.m.v reflection.
 * Noot: als de handler een 'DbConnect' object is, wordt daarvan close aangeroepen.
 * @see fi.beans.jdbc.DbConnect#close()
 */    
	final public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException
    {
	    
	    byte[] result;
    	ServletInputStream in = request.getInputStream ();
    	if(lock != null)
    		synchronized (lock)
    		{
    			result = execute(in);
    		}
    	else 
    		result = execute(in);
        response.setContentType ("text/xml");
        response.setContentLength (result.length);
        OutputStream out = response.getOutputStream();
        out.write (result);
        out.flush ();
    }

private byte[] execute(ServletInputStream in) {
	byte[] result;
	result = xmlrpc.execute (in);
	if(handler instanceof DbConnect)
	{
// disconnect database, if fi design pattern used.          
	    ((DbConnect)handler).close();
	}
	return result;
}

    protected Servlet() 
    {
        handler = this;
        lock = this;
    }

    protected Servlet(Object h)
    {
        handler = h;
        lock = this;;
    }
    /**
     * @return Returns the handler.
     */
    protected Object getHandler()
    {
        return handler;
    }
    /**
     * @param handler The handler to set.
     */
    protected void setHandler(Object handler)
    {
        this.handler = handler;
        getXmlrpc().addHandler("$default", handler);
        
    }
    /**
     * @return Returns the xmlrpc.
     */
    protected XmlRpcServer getXmlrpc()
    {
        return xmlrpc;
    }
    /**
     * @param xmlrpc The xmlrpc to set.
     */
    protected void setXmlrpc(XmlRpcServer xmlrpc)
    {
        this.xmlrpc = xmlrpc;
    }
    /**
     * @return Returns the lock.
     */
    protected Object getLock()
    {
        return lock;
    }
    /**
     * @param lock The lock to set.
     */
    protected void setLock(Object lock)
    {
        if(lock == null)
            this.lock = this;
        else
            this.lock = lock;
    }
    
    // no lock at all
    protected void unLock()
    {
    	this.lock = null;
    }
}
