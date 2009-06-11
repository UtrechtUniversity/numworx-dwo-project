package fi.servlet.persistence;
import java.util.Vector;

public interface JDBC
{

	public Vector executeQuery(String query, Vector params) throws Exception;
	public int executeUpdate(String update, Vector params) throws Exception;
}

