package nl.numworx.async;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executor;

import nl.numworx.async.Async;

import org.osgi.util.promise.Promise;

import junit.framework.TestCase;

public class AsyncTest extends TestCase {

	Async async;
	
	protected void setUp() throws Exception {
		async = new Async();
		if(false)
		async.executor = new Executor() {
			
			public void execute(Runnable command) {
				command.run();
			}
		};
	}

	protected void tearDown() throws Exception {
		
	}

	public void testNormaal() throws Exception 
	{
		TestIF t1 = new TestIF() {

			public int getInt(int n) throws Exception {
				return n;
			}
		};
		
		TestIF t2 = async.mediate(t1, TestIF.class);
		
		int n;
		Promise<Integer> p = async.call(n = t2.getInt(4));
		assertEquals("n=", 0, n);
		assertEquals("p=", 4, p.getValue().intValue());		
	}
	
	public void testFailed() throws Exception
	{
		TestIF t1 = new TestIF() {

			public int getInt(int n) throws Exception {
				throw new Exception("n="+n);
			}
		};
		
		TestIF t2 = async.mediate(t1, TestIF.class);
		
		int n;
		Promise<Integer> p = async.call(n = t2.getInt(4));
		assertEquals("n=", 0, n);
		try {
			assertEquals("p=", 4, p.getValue().intValue());	
			fail("should fail");
		} catch(InvocationTargetException e) {
		}
		assertEquals("n=4", p.getFailure().getMessage());
	}
		
	
	
	
}
