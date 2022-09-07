package nl.uu.fi.dwo.mobile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.ActivityInterface;
import nl.uu.fi.dwo.mobile.client.ui.TrafficAgent;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.SymboolPanel;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVak;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVakPanel;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TextEditor;
import nl.uu.fi.dwo.mobile.utils.LogBuilder;
import nl.uu.fi.dwo.mobile.utils.Logging;

public class WidgetPlayer implements EntryPoint, InteractionStub, ActivityInterface {

    InteractionStub delegate;
   
    static class TextEditorWidget extends TextEditor {

		TextEditorWidget(ActivityInterface a) {
			super(a);
		}

		@Override
		public void setCommunicationRoot(OpdrNavIF comRoot) {
			super.setCommunicationRoot(comRoot);
			comRoot.getKeyboard().setEditor(this);
			FormuleHolder.installKeyboard(comRoot.getKeyboard());
			FocusOnTouch.installKeyboard(comRoot.getKeyboard(), comRoot.getFormuleClipboard());
		}

		@Override
		protected void addExecuteBtn(OpdrNavIF comRoot) {
		}
    	
    }
    
    static class TekstVakWidget extends TekstVakPanel {

		TekstVakWidget(ActivityComponent a, int breedte, int hoogte, String[] randomVarNamen,
				HashMap<String, Number> randomVarWaarden) {
			super(a, breedte, hoogte, randomVarNamen, randomVarWaarden);
			// TODO Auto-generated constructor stub
		}
    	
    }
    
    
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
		  case  4: delegate = new TextEditorWidget(this);   break;
		  case 55: delegate = new SymboolPanel(); break;
		  //case 9: delegate = new TekstVakWidget(this); break; // TekstVakWidget extends TekstVakPanel
		}

		RootLayoutPanel.get().add(FocusOnTouch.wrap(delegate.asWidget()));
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

@Override
public LogBuilder logBuilder() {
	return new LogBuilder(null) {

		@Override
		public Logging build() {
			return null;
		} };
}

@Override
public boolean isPremium() {
	return false;
}

@Override
public boolean isReview() {
	return false;
}

@Override
public boolean isEindtoetsVerzegeld() {
	return false;
}

@Override
public Scorm2004IF api() {
	return new SCORM_guest();
}

@Override
public boolean isTest() {
	return false;
}

@Override
public String getResource(String string) {
	return GWT.getModuleBaseURL() + "../" + string;
}

@Override
public LessonMode getLessonMode() {
	return LessonMode.normal;
}

@Override
public boolean isNoordhoff() {
	return false;
}

@Override
public TrafficAgent agent() {
	return new TrafficAgent();
}

@Override
public Optional<DwoGlobalVars> vars() {
	return Optional.empty();
}

}
