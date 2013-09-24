package fi.dwo.client.persistence.cache;

public class Bucket {
	Bucket(int uid, int scoid, String key, String value) {
		super();
		this.uid = uid;
		this.scoid = scoid;
		this.key = key;
		this.value = value;
	}
	private int uid, scoid;
	private String key, value;
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + scoid;
		result = prime * result + uid;
		return result;
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
		if (!(obj instanceof Bucket)) {
			return false;
		}
		Bucket other = (Bucket) obj;
		if (key == null) {
			if (other.key != null) {
				return false;
			}
		} else if (!key.equals(other.key)) {
			return false;
		}
		if (scoid != other.scoid) {
			return false;
		}
		if (uid != other.uid) {
			return false;
		}
		return true;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the uid
	 */
	public int getUid() {
		return uid;
	}
	/**
	 * @return the scoid
	 */
	public int getScoid() {
		return scoid;
	}
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Bucket [uid=" + uid + ", scoid=" + scoid + ", key=" + key
				+ ", value=" + trim(value) + "]";
	}
	String trim(String v) {
		if(v == null)
			return null;
		if(v.length() < 10) return v;
		return v.substring(0,9) + "... (" + v.length() + ")";
	}
	
	
	
	
}
