package fi.writemathgwt.client;

import com.google.gwt.user.client.ui.Label;

public class DummyFormuleViewer extends Label implements IFormuleViewer 
{
	@Override
	public void setFormule(String formule) 
	{
		setText(formule);
	}

}
