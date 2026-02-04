package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PrintSeparator extends Composite {

	private static PrintSeparatorUiBinder uiBinder = GWT.create(PrintSeparatorUiBinder.class);

	@UiField
	Label page;
	private String cur = "";
	private String title = "";
	private String score = "";
	
	interface PrintSeparatorUiBinder extends UiBinder<Widget, PrintSeparator> {
	}

	public PrintSeparator() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public PrintSeparator(int cur) {
		this();
		page.setText(this.cur = String.valueOf(cur));
	}

	public void setScore(int score, int correction, int max) {
		String value;
		if (score == 0 && correction != 0)
			value = "";
		else
			value = String.valueOf(score);
		if (correction > 0)
		{
			value = value + "+" + correction;
		}
		else if (correction < 0)
		{
			value = value + correction;
		}
		this.score =  " " + value + "/" + max;
		setText();
	}
	
	public void setTitle(String title) {
		if (title == null || title.isEmpty()) 
			this.title = "";
		else
			this.title = " " + title.trim();
		setText();
	}
	
	private void setText() {
		page.setText(cur + title + score);
	}
}
