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
	String balk();
	String insert_formule();
	String insert_calculator();
	
	String WaitScreen();
	
	@ClassName("numworx-popup")
	String numworx_popup();
	
	String myPushButton();
	@ClassName("myPushButton-disabled")
	String myPushButton_disabled();
	
	String tekstvak();
	
	@ClassName("cbg-RP")
	String cbg_RP();
	
}
