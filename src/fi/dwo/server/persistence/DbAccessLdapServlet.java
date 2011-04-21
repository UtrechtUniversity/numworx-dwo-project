/*
 * Created on Nov 20, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package fi.dwo.server.persistence;

import org.apache.xmlrpc.XmlRpc;

/**
 * Servlet met interface naar de LDAP variant van DbAccess
 * @author Wim
 * @web.servlet
 *   name="DbAccessLdap"
 *   description="Db Access met LDAP extensies"
 * @web.servlet-mapping
 *   url-pattern=/dbaccessldap
 */
public class DbAccessLdapServlet extends DbAccessServlet
{

    private static final long serialVersionUID = 1L;
    /**
     * Null constructor. Attach DbAccessLdap aan de DbAccessServlet.
     * @see DbAccessLdap
     * @see DbAccessServlet
     */
    public DbAccessLdapServlet() {
        super(new DbAccessProxy());
        unLock();
    }

}
