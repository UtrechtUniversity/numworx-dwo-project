package fi.servlet.persistence;

import java.util.*;
import java.net.*;

public class Main
{
	public static void main(String[] args)
	throws Exception
	{	Vector results;
		JDBC store = new JDBC_stub(new URL("http://localhost:8080/RPC2"));
		if(args.length == 1)
		{
			results = store.executeQuery(args[0], new Vector(0));
			System.out.println(results);
		}
	}
}
