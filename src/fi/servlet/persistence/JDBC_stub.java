package fi.servlet.persistence;
import java.util.Vector;
import java.io.IOException;
import org.apache.xmlrpc.applet.XmlRpcException;
public class JDBC_stub extends PersistenceClient implements fi.servlet.persistence.JDBC
{
  public JDBC_stub(java.net.URL u)
  {
     super(u);
  }

// public abstract java.util.Vector fi.servlet.persistence.JDBC.executeQuery(java.lang.String,java.util.Vector) throws java.lang.Exception
 public java.util.Vector executeQuery (
   java.lang.String a,
   java.util.Vector b
   )
 throws IOException, XmlRpcException
 {
   Vector params = new Vector(2);
   params.addElement(a);
   params.addElement(b);
   java.util.Vector  result;
   Object o = 
       invoke("executeQuery", params);
   result = (java.util.Vector) o;
   return result;
 }
// public abstract int fi.servlet.persistence.JDBC.executeUpdate(java.lang.String,java.util.Vector) throws java.lang.Exception
 public int executeUpdate (
   java.lang.String a,
   java.util.Vector b
   )
 throws IOException, XmlRpcException
 {
   Vector params = new Vector(2);
   params.addElement(a);
   params.addElement(b);
   int  result;
   Object o = 
       invoke("executeUpdate", params);
   result = ((Integer)o).intValue();
   return result;
 }
}
