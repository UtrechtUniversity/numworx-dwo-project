package nl.uu.fi.dwo.mobile;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.SymboolPanel;

public class WidgetPlayer implements EntryPoint, InteractionStub {

    InteractionStub delegate;
   
	@Override
	public void onModuleLoad() {
	    delegate = this;
		String id = Window.Location.getParameter("id");
		int nr = 0;
		try {
          nr = Integer.parseInt(id);
        } catch (NumberFormatException e) {
        }
		switch(nr) {
		  case 55: delegate = new SymboolPanel(); break;
		}

		RootPanel.get().add(delegate);
        Stub.publish(delegate);
	}

  @Override
  public HashMap<String, Object> getState() {
    return new HashMap<>();
  }

  @Override
  public void setState(HashMap<String, Object> h) {
  }

  @Override
  public int getScore() {
    return 0;
  }

  @Override
  public int[][] getScoreObjectives() {
    return null;
  }

  @Override
  public Boolean isCorrect() {
    return null;
  }

  @Override
  public void kijkNa() {
  }

  @Override
  public void zetNagekeken(boolean b) {
  }

  @Override
  public void setCommunicationRoot(OpdrNavIF comRoot) {
  }

  @Override
  public void zetVolledigeBreedte(int breedte) {
  }

  @Override
  public Widget asWidget() {
    return new Label("Unknown widget");
  }

  @Override
  public int getAsHoogte() {
    return 0;
  }

  @Override
  public int getHeight() {
    return 0;
  }

  @Override
  public int getWidth() {
    return 0;
  }

  @Override
  public void setAsHoogte(int ashoogte) {
  }

  @Override
  public void init(int width, int height, Map<String, Object> launchData,
      Map<String, Number> values) {
  }

}
