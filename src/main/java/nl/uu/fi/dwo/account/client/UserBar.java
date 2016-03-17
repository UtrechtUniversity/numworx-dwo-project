package nl.uu.fi.dwo.account.client;

import java.util.Map;

import nl.uu.fi.dwo.account.client.text.Text;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class UserBar extends Composite {	
	
	private final MenuBar top;
	private final MenuBar items;
	private final Text rb = Text.constants;
	private final ProfileCommand profileCmd;
	
	public UserBar() {
		top = new MenuBar();
		initWidget(top);		
		items = new MenuBar(true);
		
		MenuItem item;
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
		
		profileCmd = new ProfileCommand();
		item = new MenuItem(rb.GUIMNU_MY_PROFILE(), profileCmd);
		items.addItem(item);
	}

	public void setProfile(Map<String,Object> map) {
		profileCmd.setProfile(map);
	}
}
