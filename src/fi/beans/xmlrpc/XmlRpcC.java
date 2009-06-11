// $Id: XmlRpcC.java,v 1.3 2005/02/16 12:23:03 wim Exp wim $
package fi.beans.xmlrpc;
// $Log: XmlRpcC.java,v $
// Revision 1.3  2005/02/16 12:23:03  wim
// foutmeldingen toegevoegd.
//
// Revision 1.2  2004/03/01 15:27:22  wim
// suffix is nu IF in plaats van ServletIF
//

import java.lang.reflect.Method;

/**
 * @author wim
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class XmlRpcC {
	private static String SUFFIX = "IF";
    static String getTypeName(Class type) {
	if (type.isArray()) {
	    try {
		Class cl = type;
		int dimensions = 0;
		while (cl.isArray()) {
		    dimensions++;
		    cl = cl.getComponentType();
		}
		StringBuffer sb = new StringBuffer();
		sb.append(cl.getName());
		for (int i = 0; i < dimensions; i++) {
		    sb.append("[]");
		}
		return sb.toString();
	    } catch (Throwable e) { /*FALLTHRU*/ }
	}
	return type.getName();
    }
	private static void println(Object o) 
	{
		System.out.println(o);
	}
	private static String local(String name)
	{
		int dot = name.lastIndexOf('.');
		if(dot >= 0) 
			return name.substring(dot+1);
		return name;
	}

	private static String packageOf(String name)
	{
		int dot = name.lastIndexOf('.');
		if( dot >= 0)
			return name.substring(0,dot);
		return null;
	}


	public static void main(String[] args)
	throws Exception
	{
		if(args.length != 1)
		{
			System.err.println("Geef een interfacenaam op");
			System.err.println("Gebruik: jxmlrpcc <interface>");
			System.exit(1);
		}
			
		String classNameIF = args[0];
		String className; 
		if (classNameIF.endsWith(SUFFIX)) 
		{
			int len = classNameIF.length();
			className = classNameIF.substring(0,len-SUFFIX.length());
		} else {
			className = classNameIF;
			System.err.println("Warning: " + classNameIF + " does not end with " + SUFFIX);
		}
		Class clazz = null;
		try {
			clazz = Class.forName(classNameIF);
		} catch (ClassNotFoundException e) {
			System.err.println("interface "+ classNameIF + " niet gevonden");
			System.exit(1);
		}
		if(!clazz.isInterface()) {
			System.err.println("class "+ classNameIF + " is geen interface");
			System.exit(1);			
		}
		String p = packageOf(className);
		if(p != null) println("package " + p + ";");
		println("import java.util.Vector;");
		println("import java.io.IOException;");
		println("import org.apache.xmlrpc.applet.XmlRpcException;");
		println("import fi.beans.xmlrpc.Client;");
		println("public class " + local(className) + "Client extends Client implements " + classNameIF);
		println("{");
		println("  public " + local(className) + "Client(java.net.URL u)");
		println("  {");
		println("     super(u);");
		println("  }");
		println("");
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length; i++)
		{	Method m = methods[i];
			println("// " + m);
			Class returnType = m.getReturnType();
			println(" public " + returnType.getName() + " " + m.getName() + " (");
			Class[] parameterTypes = m.getParameterTypes();
			for(int j = 0; j < parameterTypes.length; j++)
			{	boolean last = j == parameterTypes.length-1;
				println("   " + parameterTypes[j].getName() + " " + (char)(j+'a') + (last?"":","));
			}
			println("   )");
			println(" throws IOException, XmlRpcException");
			println(" {");
			println("   Vector params = new Vector(" + parameterTypes.length + ");");
			for(int j = 0; j < parameterTypes.length; j++)
			{
				Class c = parameterTypes[j];
				if(c.isPrimitive())
				{
					println("   params.addElement( new " + getAname(c) + "( " + (char)(j+'a') + "));");
				} else
					println("   params.addElement(" + (char)(j+'a') + ");");

			}
			if(returnType != Void.TYPE) {
				println("   " + returnType.getName() + "  result;");
				println("   Object o = ");
			}

			println("       invoke(\"" + m.getName() + "\", params);" );
			
			if(returnType != Void.TYPE) {
				if(returnType.isPrimitive())
				{
					String name = returnType.getName();
					String Aname = getAname(returnType);
					println("   result = ((" + Aname + ")o)." + name + "Value();");
				} else
				println("   result = (" + returnType.getName() + ") o;");
				println("   return result;");
			}	
			
			println(" }"); 			
		
		}
		println("}");
				
		
	
	}
	
	private static String getAname(Class returnType)
	{
		String Aname;	 
		if(returnType == Integer.TYPE) Aname = "Integer";
		else if(returnType == Character.TYPE) Aname = "Character";
		else Aname = Character.toUpperCase(returnType.getName().charAt(0)) + returnType.getName().substring(1);
		return Aname;
	}

}
