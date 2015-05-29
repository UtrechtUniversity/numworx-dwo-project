package fi.dwo.dwojapplet.domain.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class
 *
 * @author Velth101
 *
 */
public class Util {

    private static final Logger LOG = Logger.getLogger(Util.class.getName());

    static final String UTF8 = "UTF-8";

    private Util() {
    }

    static void encode(String key, char value, StringBuffer sb) {
        encode(key, String.valueOf(value), sb);
    }

    static void encode(String key, int value, StringBuffer sb) {
        encode(key, String.valueOf(value), sb);
    }

    static void encode(String key, String value, StringBuffer sb) {
        try {
            sb.append(URLEncoder.encode(key, UTF8));
            sb.append("=");
            sb.append(URLEncoder.encode(value, UTF8));
        } catch (UnsupportedEncodingException e) {
            LOG.log(Level.SEVERE, null, e);

        }
    }

    public static class ResultException extends IOException {

        ResultException() {
            super();
        }

        ResultException(String s) {
            super(s);
        }

    }

    public static class NoException extends Exception {

        public NoException(String message) {
            super(message);
        }
    }

    /**
     * @param sb
     * @return
     * @throws MalformedURLException
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    static String call(StringBuffer sb) throws MalformedURLException, IOException, UnsupportedEncodingException {
        URL u = new URL(sb.toString());
        InputStream in = u.openStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in, UTF8));
        String line = br.readLine();
        char c = line.charAt(0);
        br.close();
        if (c != '2') {
            throw new ResultException(line);
        }
        return line;
    }

}
