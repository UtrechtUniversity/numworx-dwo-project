/**
 * 
 */
package fi.dwo.client.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Random;

import fi.beans.base64code.StringCodeObject;
import fi.beans.licman.LicMan;
import fi.beans.licman.LicenseException;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.domain.User;
import fi.dwo.client.system.TextMapper;

public class ScormParameters {

		private Random rn = new Random();

	    private int rand(int lo, int hi) {
	        int n = hi - lo + 1;
	        int i = rn.nextInt() % n;
	        if (i < 0)
	            i = -i;
	        return lo + i;
	    }

	    private String randomstring(int lo, int hi) {
	        int n = rand(lo, hi);
	        char b[] = new char[n];
	        for (int i = 0; i < n; i++)
	            b[i] = (char) rand('a', 'z');
	        return new String(b);
	    }

	    private String randomstring() {
	        return randomstring(10, 20);
	    }

		
// SCO parameters
		static final int SCO_TITLE = 0;
		static final int SCO_CLASS = 1;
		static final int SCO_JAR   = 2;
		static final int SCO_LAUNCH_DATA = 3;		
		static final int SCO_ID = 4;
		static final int SCO_DESCRIPTION = 5;
// USER parameters
		static final int USER_FIRSTNAME = 6;
		static final int USER_LASTNAME = 7;
		static final int USER_EMAIL = 8;
// VERSION
		static final int VERSION = 9;
		static final int UUID = 10;
		static final int LANG = 11;
		static final int BGCOLOR = 12;
		private static final int PLENGTH = 13;
		private Object[] parameters = new Object[PLENGTH];
		
		public void setSco(Sco sco)
		{
			parameters[SCO_TITLE] = sco.getScoName();
			parameters[SCO_DESCRIPTION] = sco.getDescription();
			parameters[SCO_CLASS] = sco.getAppletData().getClassName();
			parameters[SCO_JAR] = sco.getAppletData().getJarName();
			parameters[SCO_ID] = String.valueOf(sco.getID());
			Hashtable launchData = null;
			Hashtable editLaunchData = sco.getEditLaunchdata();
			if(editLaunchData != null) launchData = editLaunchData;
			else launchData = sco.getLaunchdata();
			Class applet = sco.getApplet().getClass();
			
	// licentie manager, via een parameter
			String licentie = "null";
			try { 
				User u = GuiCreator.instance().getUser();
				licentie = LicMan.getLicense(u.getSchool().getSchoolID(), sco.getCourse().getDwoProfile(), applet);
				launchData.put(LicMan.LICENSE_KEY, licentie);
			} catch (LicenseException e)
			{
				// TODO iets beters dan printstacktrace
				e.printStackTrace();
			}
			parameters[SCO_LAUNCH_DATA] = StringCodeObject.encodeObjectToString(launchData);
			launchData.remove(LicMan.LICENSE_KEY);

		}
		public void setUser(User u)
		{
			parameters[USER_FIRSTNAME] = u.getFirstname();
			parameters[USER_LASTNAME] = (u.getMiddleName() + " " + u.getLastName()).trim();
			parameters[USER_EMAIL] = u.getEmail();
		}
		
		private final DateFormat FMT = new SimpleDateFormat("ddMMyyyy");
		private static final String UTF8 = "UTF-8";
		public ScormParameters()
		{
			Date now = new Date();
			parameters[VERSION] = FMT.format(now);
			parameters[UUID] = randomstring();
			parameters[LANG] = TextMapper.getLanguage();
			parameters[BGCOLOR] =  "#" + Integer.toHexString(GuiConstants.MAIN_BACKGROUND.getRGB()).substring(2);
		}
		
		public void copy(BufferedReader in, PrintWriter out) throws IOException
		{
			String line;
			while( (line = in.readLine()) != null) 
			{
				out.println(MessageFormat.format(line, parameters));
			}
			out.flush();
			in.close();
		}
		
		public void copy(Reader in, Writer out) throws IOException
		{
			BufferedReader bin; 
			PrintWriter pout;
			if( in instanceof BufferedReader)
				bin = (BufferedReader) in;
			else
				bin = new BufferedReader(in);
			if( out instanceof PrintWriter)
				pout = (PrintWriter) out;
			else
				pout = new PrintWriter(out);
			copy(bin, pout);
		}
		
		public void copy(InputStream in, OutputStream out) throws IOException
		{
			copy(new InputStreamReader(in, UTF8), new OutputStreamWriter(out, UTF8));
		}
		byte[] buf = new byte[1024];

		public void rawCopy(InputStream in, OutputStream out) throws IOException {
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
		}
		
	}