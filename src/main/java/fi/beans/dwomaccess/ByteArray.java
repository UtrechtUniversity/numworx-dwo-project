package fi.beans.dwomaccess;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.XMLEncoder;
import java.io.Serializable;
import java.util.Arrays;

/**
 *
 * Implementatie van een bytearray die XMLEncoder vriendelijk is.
 *
 * @author wim
 *
 */
public class ByteArray implements Serializable {

    /**
     * voor Serializable
     */
    private static final long serialVersionUID = -2268476311965180061L;
    private byte[] bytes;
    transient private String cache;

    private ByteArray(byte[] bytes) {
        this.bytes = bytes;
    }

    public ByteArray() {
    }

    static public ByteArray newInstance(byte[] bytes) {
        return new ByteArray(bytes);
    }

    public ByteArray(String s) {
        setString(s);
    }

    /**
     * @param s
     */
    public void setString(String s) {
        cache = s;
        if (s != null) {
            bytes = new hplb.misc.BASE64Decoder().decodeBuffer(s);
        } else {
            bytes = null;
        }
    }

    public String getString() {
        if (bytes == null) {
            return null;
        }
        if (cache != null) {
            return cache;
        }
        return new hplb.misc.BASE64Encoder(false).encodeBuffer(bytes);
    }

    public byte[] getBytes() {
        return bytes;
    }


    /* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ByteArray)) {
            return false;
        }
        ByteArray other = (ByteArray) obj;
        if (!Arrays.equals(bytes, other.bytes)) {
            return false;
        }
        return true;
    }

    /**
     * @param encoder
     */
    public static void installDelegate(Encoder encoder) {
        encoder.setPersistenceDelegate(ByteArray.class,
                new DefaultPersistenceDelegate(
                        new String[]{"string"}));
    }

}
