package fi.servlet.persistence;
import fi.beans.xmlrpc.*;

import java.util.*;
import java.io.*;
import java.sql.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
//import javax.servlet.*;
//import javax.servlet.http.*;
//import org.apache.xmlrpc.*;

public abstract class PersistenceServlet extends Servlet
{

	private final static String URL_PREFIX = "jdbc:freetds:sqlserver://vector.nt.fi.uu.nl/";
	private final static String PASSWORD_PREFIX = "!";

    private String url;
    private String user;
    private String password;
    private String driver = "com.internetcds.jdbc.tds.Driver";
	
	private Connection c;
	private String database;
	
	protected PersistenceServlet(String database) 
	{
		
		setDatabase(database);
		
	}

	protected void setDatabase(String database) {
		this.database = database;
		url = URL_PREFIX + database;
		user = database;
		password = PASSWORD_PREFIX + database;
		destroy();
	}

	protected PersistenceServlet()
	{
		this("fidb");	
	}


	protected PreparedStatement getStatement(String sql)
	throws SQLException
	{
		return getConnection().prepareStatement(sql);
	}

	protected ResultSet executeQuery(String sql)
	throws SQLException
	{
		Statement s = getConnection().createStatement();
		ResultSet rs = s.executeQuery(sql);
		//s.close(); // hier of later? nee later!
		return rs;
	}

    public void init(ServletConfig config) throws ServletException
    {
    	super.init(config);
		try
		{
	    	Class.forName(driver). newInstance();
		} catch(Exception e) {} 		// hope for the best.
    }

    protected Connection getConnection() throws SQLException
    {
    	if(c == null)
		{
			Properties p = new Properties();
			p.put("user", user);
			p.put("password", password);
			p.put("TDS", "7.0");
			c = DriverManager.getConnection(url, p);
		}
		return c;
    }

    public void destroy()
    {
      	try {
    		if(c != null) c.close();
		} catch(Exception e) {}
		c = null;
    }


}
