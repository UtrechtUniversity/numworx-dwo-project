package nl.uu.fi.dwo.mobile.client;

import com.google.gwt.resources.client.CssResource;

public interface DWOplayerCss extends CssResource {
	String insert_formule_steps();
	
	String StubView_readonly();

	String textEditor();
	String textEditor_readonly();
	String textEditor_empty();
	String textEditor_nowrap();
	String textEditor_cursor();
	String textEditor_select();
	String balk();
	String insert_formule();
	String insert_formule_readonly();
	String insert_calculator();
	String insert_result();
	String insert_button();
	
	String WaitScreen();
	
	@ClassName("numworx-popup")
	String numworx_popup();
	
	String myPushButton();
	@ClassName("myPushButton-disabled")
	String myPushButton_disabled();
	
	String tekstvak();
	
	@ClassName("cbg-RP")
	String cbg_RP();
	
	String disabled();
	
	String kijknapanel();
	
	String goed();
	
	String half();
	
	String fout();
	
	String navigatiebalk();

	String navigatiebalkLabel();
	
	String opdrachtbollen();
	
	String DWOkeyboard();
	
	String review();
	
	String spaceShiftLabel();
	String shiftBtn();
	String spaceLabel();
	String score();
	
	// Score buttons:
	String v();
	String nr();
	String scoreBtn();
// dependent names
	@ClassName("scoreBtn-max0")
	String scoreBtn_max0();
	@ClassName("scoreBtn-disabled")
	String scoreBtn_disabled();
	@ClassName("scoreBtn-correct")
	String scoreBtn_correct();
	@ClassName("scoreBtn-selectedcorrect")
	String scoreBtn_selectedcorrect();
	@ClassName("scoreBtn-cursor")
	String scoreBtn_cursor();
	@ClassName("scoreBtn-popupTime")
	String scoreBtn_popupTime();
	
	String correctie();
	String corrected();
	@ClassName("correctie-tail")
	String correctieTail();
	
	String tablet();
	String tablet_active();
	String desktop_active();

	String removeBtn();
	String strategieKeuzeVak();
	String strategieKeuze();
	String strategieLabel();
}
