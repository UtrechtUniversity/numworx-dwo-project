package nl.uu.fi.dwo.rest.util;

import static org.junit.Assert.*;

import java.nio.charset.Charset;
import java.util.Random;

import org.junit.Test;

public class LZWTest {

	static Charset UTF8 = Charset.forName("UTF-8");
	
	private void check(String in) {
		String out = LZW.compress(in);
		float l1 = in.getBytes(UTF8).length;
		float l2 = out.getBytes(UTF8).length;
		float l3 = out.length();
		System.out.println("in= "+ l1 + ", out= " + l3 + ",bytes="+ l2 + ", rate = " + (l2/l1));
		String re = LZW.uncompress(out);
		assertEquals("check", in, re);
	}
	
	@Test
	public void test() {
		check("");
		check("aap");
		check("noot");
		check("aap noot aap noot mies");
		check("aap noot aap noot mies aap noot mies aap noot mies");
	}

	@Test public void testLang() { 
		char[] buf = new char[200000];
		buf[199999] = 'u';
		check(new String(buf));
		Random r = new Random();
		for(int i = 0; i < buf.length; i++) 
			buf[i] = (char) r.nextInt(128);
		check(new String(buf));
	}
	
	@Test public void testASCII() {
		try { 
			check("á\u03C0");
			fail("should fail");
		} catch( IllegalArgumentException ok) {
			// detected.
		}
	}
	
	@Test public void testEncode() { 
		
	}
	
}
