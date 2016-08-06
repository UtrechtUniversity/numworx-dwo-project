package fi.beans.private_base64code;

import java.io.*;
import java.util.zip.*;

public class StringCodeObject {

    String codeString;
    Object object;

    public StringCodeObject(String s) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes("ASCII"));
            Base64InputStream b64is = new Base64InputStream(bais);
            GZIPInputStream zis = new GZIPInputStream(b64is);
            ObjectInputStream invoer = new ObjectInputStream(zis);
            object = invoer.readObject();
            codeString = s;
        }
        catch (Exception io) {	
        	System.out.println(io.toString());
        }
    }

    public StringCodeObject(Object o) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Base64OutputStream b64os = new Base64OutputStream(baos);
            GZIPOutputStream zos = new GZIPOutputStream(b64os);
            ObjectOutputStream uitvoer = new ObjectOutputStream(zos);

            uitvoer.writeObject(o);
            zos.finish();
            uitvoer.close();
            codeString = baos.toString("ASCII");
            object = o;
        }
        catch (IOException io) {	//System.out.println(io.toString());
        }
    }

    public static Object decodeStringToObject(String s) {
        Object o = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes("ASCII"));
            Base64InputStream b64is = new Base64InputStream(bais);
            GZIPInputStream zis = new GZIPInputStream(b64is);
            ObjectInputStream invoer = new ObjectInputStream(zis);
            o = invoer.readObject();
        }
        catch (Exception io) {	//
        	System.out.println(io.toString());
        }
        return o;
    }

    public static String encodeObjectToString(Object o) {
        String s = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Base64OutputStream b64os = new Base64OutputStream(baos);
            GZIPOutputStream zos = new GZIPOutputStream(b64os);
            ObjectOutputStream uitvoer = new ObjectOutputStream(zos);

            uitvoer.writeObject(o);
            zos.finish();
            uitvoer.close();
            s = baos.toString("ASCII");
        }
        catch (IOException io) {	//System.out.println(io.toString());
        }
        return s;
    }

    public Object toObject() {
        return object;
    }

    @Override
    public String toString() {
        return codeString;
    }
}
