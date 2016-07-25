package nl.uu.fi.dwo.mobile.client.ui.views;


import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.StubView;
import nl.uu.fi.dwo.mobile.utils.LaTransport;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

public class ScoreNavMC2 extends ScoreNavFacade {

	private Widget help, check;
	LaTransport logging;
	
	public ScoreNavMC2() {
		logging = (LaTransport) DWOplayer.PARAMETERS.getLogging();
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
		{
			if(check.getParent() == null)
				sb.addKnop(check, true);
			check.setVisible(b);
		}
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
		{	if(help.getParent() == null)
				sb.addKnop(help, true);
			help.setVisible(b);
		}
	}

	@Override
	public void setGotoOpdracht(GotoOpdracht gotoOpdracht) {
		super.setGotoOpdracht(gotoOpdracht);
		logging.setCommunicationRoot(gotoOpdracht);
	}

	@Override
	public void started() {
		if(gotoOpdracht != null)
		{
			logging.startSession();
			currentOpdracht = gotoOpdracht.getCurrentOpdracht();
			logging.setLocation(Integer.toString(currentOpdracht+1));
		}
		super.started();
	}

	@Override
	public void stopped() {
		if(gotoOpdracht != null)
			logging.stopSession();
		super.stopped();
		if(check != null) check.removeFromParent();
		if(help != null) help.removeFromParent();
	}

	@Override
	public void setOpdracht(int currentOpdracht) {
// "1", "2", ...
		if(gotoOpdracht != null)
			logging.setLocation(Integer.toString(currentOpdracht+1));
		super.setOpdracht(currentOpdracht);
	}

}
