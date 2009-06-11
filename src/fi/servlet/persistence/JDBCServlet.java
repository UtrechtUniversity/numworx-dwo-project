package fi.servlet.persistence;
import java.sql.*;
import java.util.*;
import org.apache.xmlrpc.WebServer;

public class JDBCServlet extends PersistenceServlet
implements JDBC
{

/** Constructor zet default database op "fidb".
 */	
	public JDBCServlet() {
		super("fidb");	
	}
	
	public Vector executeQuery(String query, Vector params)
	throws Exception
	{
		Vector result = new Vector();
		PreparedStatement p = getStatement(query);
		storeParams(p, params);
		ResultSet rs = p.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		int columns = rsmd.getColumnCount();
		while(rs.next())
		{	Hashtable h = new Hashtable(columns);
			for(int i=1; i <= columns; i++)
			{
				Object o = rs.getObject(i);
				String name = rsmd.getColumnLabel(i);
				h.put(name, o);
			}
			result.add(h);
		}
		rs.close();
		p.close();
		return result;
	}

	private void storeParams(PreparedStatement p, Vector params)
	throws SQLException
	{
		for(int i = 0; i < params.size(); i++)
		{	p.setObject(i+1, params.elementAt(i));
		}

	}
	public int executeUpdate(String update, Vector params)
	throws Exception
	{
		int result;
		PreparedStatement p = getStatement(update);
		storeParams(p,params);
		result = p.executeUpdate();
		p.close();
		return result;
	}
	


}
