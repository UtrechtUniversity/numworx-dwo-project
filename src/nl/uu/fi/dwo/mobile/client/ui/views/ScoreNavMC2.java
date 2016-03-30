package nl.uu.fi.dwo.mobile.client.ui.views;

import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class ScoreNavMC2 extends ScoreNavFacade {

	private Widget help, check;
	
	public ScoreNavMC2() {
	}

	@Override
	public void setAuthELOcheck(boolean b) {
		if(b && check == null)
		{		Button btn = new Button(Text.constants.authELOcheck());
				btn.getElement().setId("authELOcheck");
				btn.getElement().setLang(StubView.getLocale());
				btn.addStyleName("authELOcheck");
				sb.addKnop(btn, true);
				check = btn;
		}
		if(check != null)
			check.setVisible(b);
	}

	@Override
	public void setAuthELOhelp(boolean b) {
		if(b && help == null)
		{		Button btn = new Button(Text.constants.authELOhelp());
				btn.getElement().setId("authELOhelp");
				btn.getElement().setLang(StubView.getLocale());
				btn.addStyleName("authELOhelp");
				sb.addKnop(btn, true);
				help = btn;
		}
		if(help != null)
			help.setVisible(b);
	}

}
