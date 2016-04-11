package nl.uu.fi.dwo.mobile.client.ui;

import nl.uu.fi.dwo.interaction.client.OpdrNavIF;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public interface ScoreNavIF {

	public interface Checker {
		void checkOpdracht(ScoreNavIF source);
	}

	interface GotoOpdracht extends OpdrNavIF {
		void gotoOpdracht(int i, ScoreNavIF source);
		void reloadOpdracht(int i, ScoreNavIF source);
		void setButtonEnabled(int opdrNr, boolean b);
		int getCurrentOpdracht();
	}

	public interface NextPrevHandler {
		void gotoNext(ScoreNavIF source);
		void gotoPrev(ScoreNavIF source);
	}
	
	public interface ObjectivesHandler {
		void openObjectivesPanel(ScoreNavIF source);//source nodig?
	}
	
	void setStatusBar(StatusBarIF bar);
	
	void setBeantwoord(int aantalBeantwoord);

	void setOpdracht(int currentOpdracht);

	void setTotaalScore(int score);

	void setItemScore(int oldOpdr, int i);

	void setKijkNaEnabled(boolean enable);
	void setVolgendeEnabled(boolean enable);
	void setVorigeEnabled(boolean enable);

	void setAantalOpdrachten(int aantalOpdrachten, int[] maxScores, int current);

	void setItemScores(int[] itemScores);

	void setGotoOpdracht(GotoOpdracht gotoOpdracht);

	void setItemOpnieuw(boolean boolean1);

	void setOpnieuw(boolean b);
	
	void setScoresObjectivesKnop(boolean b);

	void setKijkNa(Checker checker);
	
	void setNextPrevHandler(NextPrevHandler nextprev);
	
	Widget getNextButton();
	Widget getPrevButton();
	Widget getEndButton();
	
	void setObjectivesHandler(ObjectivesHandler objectivesHandler);

	PushButton getKijkNaButton();
	Label getTotaalScoreLabel();
	Label getKeerNagekekenLabel();
	void setTotaalScoreLabel(int score);
	void setKeerNagekekenLabel(int aantal);

	void setVolgendeVisible(boolean visible);
	void setVorigeVisible(boolean visible);

	void setButtonEnabled(int opdrNr, boolean b);
	SlidingPopup getPopup();

	void showScore();

	void setAuthELOcheck(boolean b);
	void setAuthELOhelp(boolean b);

	void started();
	void stopped();
	
}
