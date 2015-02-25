package fi.dwo.dwojapplet.domain;

public class ContactDocent extends Teacher {

    public ContactDocent() {
        
    }

    /**
     * Een schooladmin kan altijd meer dan een docent.
     * @return 
     */
    @Override
    public boolean hasRight(char right) {
        switch (right) {
            case MODIFY_MODULES_RIGHT:
            case CHANGE_CLASS_RIGHT:
            case CHANGE_CLASS_RIGHT_TEACHER:
                return true;
            default:
                return super.hasRight(right);

        }
    }

}
