package nl.uu.fi.dwo.mobile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Role;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.ActivityInterface;
import nl.uu.fi.dwo.mobile.client.ui.TrafficAgent;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorContext;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.SymboolPanel;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVakPanel;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TextEditor;
import nl.uu.fi.dwo.mobile.utils.LogBuilder;
import nl.uu.fi.dwo.mobile.utils.Logging;

public class WidgetPlayer implements EntryPoint, InteractionStub, ActivityInterface {

    InteractionStub delegate;
   
    static class TextEditorWidget extends TextEditor {

		TextEditorWidget(ActivityInterface a) {
			super(a);
			DWOplayer.DWO_BUNDLE.dwoplayercss().ensureInjected();
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
    
    static class TekstVakWidget extends SimpleLayoutPanel implements  InteractionStub, AnchorContext, OpdrNavIF, ResizeHandler {
    	private TekstVakPanel delegate;
    	private ActivityInterface activity;
    	private OpdrNavIF comRoot;
		private HashMap<String, Object> initMap;

		public TekstVakWidget(WidgetPlayer widgetPlayer) {
			addStyleName("tekstvakwidget");
			activity = widgetPlayer;
		}

		public void setCommunicationRoot(OpdrNavIF comRoot) {
			this.comRoot = comRoot;
			delegate.setCommunicationRoot(this);
			//delegate.setHoofdPanel(false);
			ObjectMap configuration = comRoot.getConfiguration();
			if (configuration == null) configuration = JSONUtilities.wrapMap(new JSONObject());
			delegate.zetInstellingen(configuration); // never null. 
			//delegate.setKeyboard(comRoot.getKeyboard()); // zal ook wel!
			delegate.zetOpdracht(initMap);
			setWidget(delegate);
			setPixelSize(delegate.getWidth(), delegate.getHeight());
			if (DWOplayer.RESPONSIVE)
				delegate.addResizeHandler(this);
		}

		public HashMap<String, Object> getState() {
			return delegate.getState();
		}

		public void setState(HashMap<String, Object> h) {
			delegate.setState(h);
		}

		public int getScore() {
			return delegate.getScore();
		}

		public Boolean isCorrect() {
			return delegate.isCorrect();
		}

		public void kijkNa() {
			delegate.kijkNa();
		}

		public int getHeight() {
			return delegate.getHeight();
		}

		public int getWidth() {
			return delegate.getWidth();
		}

		public int getAsHoogte() {
			return delegate.getAsHoogte();
		}

		public void zetVolledigeBreedte(int breedte) {
			delegate.zetVolledigeBreedte(breedte);
		}

		public void setAsHoogte(int ashoogte) {
			delegate.setAsHoogte(ashoogte);
		}

		public int[][] getScoreObjectives() {
			return delegate.getScoreObjectives();
		}

		public void zetNagekeken(boolean b) {
			delegate.zetNagekeken(b);
		}

		@Override
		public void init(int width, int height, Map<String, Object> launchData, Map<String, Number> values) {
			Map<String, Object> images = Collections.emptyMap();
			//images = (Map<String, Object>) launchData.getOrDefault("$IMAGE$MAP$", images);
			if (launchData.containsKey("$IMAGE$MAP$"))
				images = (Map<String, Object>) launchData.get("$IMAGE$MAP$");
			ImageView.setMap(images); // voorkom NPE
			String[] randomVarNamen = values.keySet().toArray(new String[values.size()]);
			HashMap<String, Number> randomVarWaarden = new HashMap<>(values);
			HashMap<String, Object> launch = new HashMap<>();
			launch.put("breedte", width);
			launch.put("hoogte", height);
			launch.put("volledigeBreedte", true);
			launch.put("popup", false);
			launch.put("interactiePanelLaunchState", launchData);
			delegate = new TekstVakPanel(activity, launch, randomVarNamen, randomVarWaarden, this, width);
			initMap = new HashMap<>(launchData);
		}

		@Override
		public void gotoUrl(String href) {
			GWT.log("goto url: " + href);
			CBookEvent ev = new CBookEvent(this, "gotoPlace", href);
			comRoot.fireEvent(ev);
		}

		@Override
		public void gotoPlace(String token) {
			GWT.log("goto place: " + token);			
			CBookEvent ev = new CBookEvent(this, "gotoPlace", token);
			comRoot.fireEvent(ev);
		}

		public void setChanged(boolean fout) {
			comRoot.setChanged(fout);
		}

		public FormuleKeyboardIF getKeyboard() {
			return comRoot.getKeyboard();
		}

		public FormuleClipboardIF getFormuleClipboard() {
			return comRoot.getFormuleClipboard();
		}

		public int getMode() {
			return comRoot.getMode();
		}

		public String getLearnerId() {
			return comRoot.getLearnerId();
		}

		public String getLearnerName() {
			return comRoot.getLearnerName();
		}

		public CssColor getBackground() {
			return comRoot.getBackground();
		}

		public String getUUID() {
			return comRoot.getUUID();
		}

		public LessonMode getLessonMode() {
			return comRoot.getLessonMode();
		}

		public Role getRole() {
			return comRoot.getRole();
		}

		public HandlerRegistration addCBookEventListener(String command, CBookEventListener listener) {
			return comRoot.addCBookEventListener(command, listener);
		}

		public void fireEvent(CBookEvent event) {
			comRoot.fireEvent(event);
		}

		public boolean hasListeners(String command) {
			return false;
		}

		public void pause() {
			comRoot.pause();
		}

		public void unpause() {
			comRoot.unpause();
		}

		public ObjectMap getConfiguration() {
			return comRoot.getConfiguration();
		}

		public ObjectMap getContext() {
			return comRoot.getContext();
		}

		public void setVisited() {
			comRoot.setVisited();
		}

		@Override
		public void onResize(ResizeEvent event) {
			if (delegate.getWidth() != Window.getClientWidth()) {
				delegate.zetVolledigeBreedte(Window.getClientWidth());
				setPixelSize(delegate.getWidth(), delegate.getHeight());
			}			
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
		  case 9: delegate = new TekstVakWidget(this); break; // TekstVakWidget extends TekstVakPanel
		}

		FocusPanel wrap = FocusOnTouch.wrap(delegate.asWidget());
		wrap.addStyleName("focus");
// We doen ons best om de maat correct te krijgen, maar als het niet lukt, dan maar scrollen.
		wrap.getElement().getStyle().setOverflow(Style.Overflow.AUTO);
		RootLayoutPanel.get().add(wrap);
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

private final EventBus bus = new SimpleEventBus();
@Override
public EventBus getEventBus() {
	return bus;
}

@Override
public void tickle() {
	// TODO Auto-generated method stub
	
}

@Override
public String getStubView() {
	// TODO Auto-generated method stub
	return "";
}

}
