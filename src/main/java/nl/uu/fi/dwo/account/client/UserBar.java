package nl.uu.fi.dwo.account.client;


import nl.uu.fi.dwo.account.client.text.Text;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import fi.dwo.rest.dom.entities.DomUserFull;

public class UserBar extends Composite {	
	
	private final MenuBar top;
	private final MenuBar items;
	private final Text rb = Text.constants;
	private final ProfileCommand profileCmd;
	private final SchoolLoginCommand schoolLoginCmd;
        private final SchoolClassStudentCommand schoolClassCmd;
        private DomUserFull user;
	
	public UserBar(DomUserFull user) {
            this.user = user;
		top = new MenuBar();
		initWidget(top);		
		items = new MenuBar(true);
		
		MenuItem item;
		MenuItem item2;
		MenuItem item3;
		final int correctie = 10; // width popup 
		item = new MenuItem("<i class='fa fa-navicon fa-2x'></i>", true, items) {
			@Override
			public int getAbsoluteLeft() {
				int w1 = items.getOffsetWidth();
				int w2 = this.getOffsetWidth();
				return super.getAbsoluteLeft()-w1+w2-correctie;
			}
			
		};
		top.addItem(item);
		
		profileCmd = new ProfileCommand(user);
		schoolLoginCmd = new SchoolLoginCommand(user);
                schoolClassCmd = new SchoolClassStudentCommand(user);
		item = new MenuItem(rb.GUIMNU_MY_PROFILE(), profileCmd);
		items.addItem(item);
		item2 = new MenuItem(rb.GUIMNU_MY_SCHOOLLOGINS(), schoolLoginCmd);
		items.addItem(item2);
		item3 = new MenuItem(rb.GUIMNU_MY_SCHOOLCLASSES(), schoolClassCmd);
		items.addItem(item3);
	}

    /**
     * @return the user
     */
    public DomUserFull getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(DomUserFull user) {
        this.user = user;
    }
}
