package fi.dwo.client.domain;

public class ContactDocent extends Teacher {

	public ContactDocent() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Een schooladmin kan altijd meer dan een docent.
	 */
	public boolean hasRight(char right) {
		if(right == MODIFY_MODULES_RIGHT || right == CHANGE_CLASS_RIGHT)
			return true;
		return super.hasRight(right);
	}

}
