package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;
import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGTextElement;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.text.Text;
import nl.uu.fi.dwo.ideas.client.RuleIF;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.DWOLogger;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.ActivityInterface;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.SVGButton;
import nl.uu.fi.dwo.mobile.client.ui.SVGButton.ButtonListener;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;
import nl.uu.fi.dwo.mobile.utils.LogBuilder;
import nl.uu.fi.dwo.mobile.utils.Logging;
import nl.uu.fi.dwo.mobile.client.ui.views.MessageDialog;

public class CheckButton implements InteractionStub, CBookEventListener
{
   private final CBookEvent NEXT_PAGE_EVENT = new CBookEvent(ACTION_NEXT_PAGE);
   private final CBookEvent ITEM_OPNIEUW_EVENT = new CBookEvent("action.reset");
   class ActionNextPage implements ButtonListener {

    @Override
    public void onClick(Object sender) {
      logger.warning("CheckButton " + ACTION_NEXT_PAGE);
      Promise<Void> defer = 
    		  activity.agent().barrier().then( p ->
      	{activity.getEventBus().fireEvent(NEXT_PAGE_EVENT);return null; }
      );
      //DWOplayer.clientfactory.addBarrier(defer);
    }
  }
   class ActieItemOpnieuw implements ButtonListener {

	@Override
	public void onClick(Object sender) {
		if(!editable) return;
		logger.warning("CheckButton action.reset");	  
	    activity.agent().barrier().onResolve( () ->	activity.getEventBus().fireEvent(ITEM_OPNIEUW_EVENT));
	}
	   
   }
   
   
   
  boolean editable = true;
	
  final class NakijkenVak implements ButtonListener {
      @Override
	  public void onClick(Object sender) {
      if (!editable) return;
      //e.stopPropagation();
      logger.warning("CheckButton nakijkenVak");
      comRoot.pause();
      TekstVakPanel ideasStatistiekPanel =
          findParentRegel().getTekstVak().getTekstVakParent().isInIdeasStatistiek();
      if (ideasStatistiekPanel != null) {
        // log answers from all FormuleEditorWithAnswer boxes
        for (int i = 0; i < lijst.size(); i++) {
          Object object = lijst.get(i);
          if (object instanceof FormuleEditorWithAnswer) {
            ((FormuleEditorWithAnswer) object).logAttempt();
          }
        }
        ideasStatistiekPanel.kijkNaIdeasStatistiek().then(
          (Promise<RuleIF> resolved) -> {
            if (logging != null) {
              logging.log(buildLog(resolved.getValue()));
            }
            return resolved;
          });
      } else {
        for (int i = 0; i < lijst.size(); i++) {
          Object object = lijst.get(i);
          if (object instanceof InteractionView) {
            InteractionView view = (InteractionView) object;
            view.kijkNa();
          }
        }
      }
      comRoot.unpause();
      logger.warning("CheckButton click end");
    }
  }

	public static final String CHECK = "check";
	public static final String AFRONDEN = "action.seal";
	private static final String READONLY = "action.setNotEditable";
	public static final CBookEvent CHECK_EVENT = new CBookEvent(CHECK);
	public static final CBookEvent SEAL_EVENT = new CBookEvent(AFRONDEN);
	public static final String ACTION_NEXT_PAGE = "actionNextPage";
	
	final class NakijkenPagina implements ButtonListener {
		@Override
		public void onClick(Object sender) {
			if(!editable) return;
			//event.stopPropagation();
			logger.warning("CheckButton nakijkenPagina");
			activity.getEventBus().fireEvent(CHECK_EVENT); // ASYNCHROON EVENT!!!
		}
	}

	final class NakijkenXWidget implements ButtonListener {
		@Override
		public void onClick(Object sender) {
			if(!editable) return;
			//event.stopPropagation();
			logger.warning("CheckButton nakijkenXWidget");
// Welke van de twee?
			comRoot.fireEvent(CHECK_EVENT);
		}
	}
	
	final class ActieAfronden implements ButtonListener {

		boolean close; 
		public ActieAfronden(boolean actionNextPage) {
			close = actionNextPage;
		}
		@Override
		public void onClick(Object sender) {
			if(!editable) return;
			comRoot.setChanged(false); 
			//event.stopPropagation();
			logger.warning("CheckButton actieAfronden");
			
			Promise<Void> defer = activity.agent().barrier()
					.then(this::confirm)
			.then(new Success<Boolean, Boolean>() {

				@Override
				public Promise<Boolean> call(Promise<Boolean> resolved) throws Exception {
					if (resolved.getValue())
						activity.getEventBus().fireEvent(SEAL_EVENT);
					return resolved;
				}
			}).then(p -> { 
				if (close && p.getValue()) activity.getEventBus().fireEvent(NEXT_PAGE_EVENT);
				return null;
			});
			//DWOplayer.clientfactory.addBarrier(defer);
		}
// FIXME een andere implementatie zie "alles opnieuw"		
		private Promise<Boolean> confirm(Promise<?> p) {
//			return Promises.resolved(Window.confirm(nl.uu.fi.dwo.mobile.client.text.Text.constants.afronden()));
		MessageDialog box = new MessageDialog();
		box.addYes();
		box.addNo();
		Label line = new Label(nl.uu.fi.dwo.mobile.client.text.Text.constants.afronden());
		box.addLine(line);
		return box.showDialog().map(new Function<Integer, Boolean>() {

			@Override
			public Boolean apply(Integer t) {
				return t.intValue() == MessageDialog.YES;
			}
		});		
		}
	}
	
	final class ActieBewaren implements ButtonListener {
		@Override
		public void onClick(Object sender) {
			if(!editable) return;
			//event.stopPropagation();
			logger.warning("CheckButton actieBewaren");
			Promise<Void> defer = 
					activity.agent().barrier().then( p -> {
				comRoot.setChanged(false);
				return null;});
			//DWOplayer.clientfactory.addBarrier(defer);
		}	
	}

	static final String holderId = "dockholder";
	private static Logger logger = Logger.getLogger("CheckButton");
	private Logging logging;
	private Map<String, Object> launchState; 
	
	OpdrNavIF comRoot;
	
	private LayoutPanel basisPanel;
	int breedte = 126;
	int hoogte = 26; 
	int ashoogte = 13;//nog kijken naar zinnige invulling hiervoor. (En hoe is dit in wiskOpdr gedaan?)
	
	private SVGButton checkButton;
	private String knopImageString = "";
	
	ArrayList<Object> lijst;
	
	private int mode; 
// variaties op een thema
	private boolean nakijkenVak=true;
	private boolean nakijkenPagina=false;
	private boolean nakijkenXWidget=false;
	private boolean actieBewaren=false;
	private boolean actieAfronden=false;
    private boolean actionNextPage=false;
    private boolean actieItemOpnieuw=false;
	private final ActivityInterface activity;

	
	public CheckButton(ActivityInterface a, HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		this.activity = a;
		ObjectMap map = JSONUtilities.wrapMap(h);
//		if (h != null && map.containsKey("breedte") )
//			breedte = map.getInt("breedte");
//		if (h != null && map.containsKey("hoogte"))
//			hoogte = map.getInt("hoogte");
		if (h != null && map.containsKey("interactiePanelLaunchState") )
			launchState =  map.getMap("interactiePanelLaunchState");
		
		init(breedte, hoogte, launchState, randomVarWaarden);
		
		initialize(h, randomVarNamen, randomVarWaarden);
	}
	
	public void init(int width, int height, Map<String, Object> h,
			Map<String, Number> values) {
		breedte = width;
		hoogte = height;
		if (h != null)
		{	ObjectMap launchData = JSONUtilities.wrapMap(h);
			
			if(launchData.containsKey("knopImageString") ) 
				knopImageString = launchData.getString("knopImageString");
			boolean nakijken;
			nakijken = launchData.getBoolean("nakijken", true);
			nakijkenPagina = nakijken && launchData.getBoolean("nakijkenPagina", nakijkenPagina);
			nakijkenVak = nakijken && launchData.getBoolean("nakijkenVak", nakijkenVak);
			nakijkenXWidget = nakijken && launchData.getBoolean("nakijkenXWidget", nakijkenXWidget);
			actieBewaren = launchData.getBoolean("actieBewaren", actieBewaren);
			actieAfronden = launchData.getBoolean("actieAfronden", actieAfronden);
			actionNextPage = launchData.getBoolean(ACTION_NEXT_PAGE, actionNextPage);
			actieItemOpnieuw = launchData.getBoolean("actieItemOpnieuw", actieItemOpnieuw);
		}
	    boolean logOption = true;
    	boolean[][] logObjectives = null;
    	String logID = "CheckButton";
    	String logIDLabel = "";

		if (logOption)
	    {	
	    	LogBuilder dwologger = activity.logBuilder().setLogOption(logOption);
	    	dwologger.setMaxScore(0);
			dwologger.setLogID(logID);
	    	dwologger.setClassName("fi.wiskopdr.CheckButton");
			dwologger.setLogObjectives(logObjectives);
			dwologger.setLogIDLabel(logIDLabel);
	    	logging = dwologger.build();
	    }
	}
	
	private List<ButtonListener> registrations = new ArrayList<>(3);
	
	private void initialize(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		basisPanel = new LayoutPanel();
		basisPanel.setStylePrimaryName("checkbutton");
		//basisPanel.setSize("" + breedte + "px", "" + hoogte + "px");
		//ashoogte = hoogte / 2;
		
		int imWidth = breedte;
		int imHeight = hoogte;
		knopImage = null;
		if(knopImageString!=null && !"".equals(knopImageString))
       	{  	ImageView imageView = new ImageView(knopImageString, activity);
       		knopImage = imageView.getImage();
			if(knopImage != null)
			{
				imWidth = imageView.getWidth();
				imHeight = imageView.getHeight();
			
	       		LoadHandler handler = new LoadHandler() {
	
					@Override
					public void onLoad(LoadEvent event) {
						int width = knopImage.getWidth();
						int height = knopImage.getHeight();
						logger.fine("onLoad checkbutton image " + width + "x" + height);
						basisPanel.setWidgetLeftWidth(checkButton, 0, Style.Unit.PX, width, Style.Unit.PX);
						basisPanel.setWidgetTopHeight(checkButton, 5, Style.Unit.PX, height, Style.Unit.PX);
						
					}
				};
				knopImage.addLoadHandler(handler);
			}
		}
		if(knopImage != null)
		{	checkButton = new SVGButton(knopImage);
			checkButton.getElement().getStyle().setPadding(0, Style.Unit.PX);
			checkButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		}
		else
		{	
			String backgroundColorString = (String)DWOplayer.templateConstants.checkButton("background-color");
			String borderColorString = (String)DWOplayer.templateConstants.checkButton("border-color");
			String textColorString = (String)DWOplayer.templateConstants.checkButton("text-color");
			
			checkButton = new SVGButton(Text.constants.klaarKnopLabel()); 
			checkButton.setFontSize(12);			
			checkButton.setBackgroundColor(CssColor.make(backgroundColorString));
			checkButton.setBorderColor(CssColor.make(backgroundColorString));
			checkButton.setBorderColorActive(CssColor.make(borderColorString));
			checkButton.setTextColor(CssColor.make(textColorString));
			checkButton.setSize(breedte, hoogte);
		}
		checkButton.getWidget().getElement().setAttribute("aria-label", Text.constants.klaarKnopLabel());
		breedte = imWidth;
		hoogte = imHeight + 5;
		ashoogte = hoogte / 2 + 7;
		basisPanel.setSize("" + breedte + "px", "" + hoogte + "px");
		basisPanel.add(checkButton);
		if(imWidth > 0 && imHeight > 0)
		{ 
			logger.fine("checkbutton image loaded " + imWidth + "x" + imHeight);
			basisPanel.setWidgetLeftWidth(checkButton, 0, Style.Unit.PX, imWidth, Style.Unit.PX);
			basisPanel.setWidgetTopHeight(checkButton, 5, Style.Unit.PX, imHeight, Style.Unit.PX);
		} else
			logger.fine("await checkbutton loaded " + imWidth + " x " + imHeight);
			
		if(nakijkenVak) registrations.add(checkButton.addButtonListener(new NakijkenVak()));
		if(nakijkenPagina) registrations.add(checkButton.addButtonListener(new NakijkenPagina()));
		if(nakijkenXWidget) registrations.add(checkButton.addButtonListener(new NakijkenXWidget()));
		if(actieBewaren) checkButton.addButtonListener(new ActieBewaren());
		if(actieAfronden) checkButton.addButtonListener(new ActieAfronden(actionNextPage));		
        if(!actieAfronden && actionNextPage) checkButton.addButtonListener(new ActionNextPage());
        if(actieItemOpnieuw) checkButton.addButtonListener(new ActieItemOpnieuw());
	}
	
	
	private Map<String, ?> buildLog(RuleIF value) {
		HashMap<String,Object> map = new HashMap<String, Object>();
		if(value.isReady()) map.put("success", Boolean.TRUE);
		if("notequiv".equals(value.getName())||"buggy".equals(value.getName())) map.put("success", Boolean.FALSE);
		map.put("response", value.getExpr());
		map.put("score", Collections.singletonMap("raw", 0));
		
		Map context = value.getContext();
		String reason = context != null ? (String) context.get("reason") : "";
		
		map.put("feedback", value.getName() + "," + value.getId() + reason);
		map.put("step", "");
		return map;
	}
	boolean fout;
	private Image knopImage;
//	private Boolean correct;

	public void zetNakijkObjecten(ArrayList<Object> lijst)
	{
		this.lijst = lijst;
	}
	
	@Override
	public HashMap<String, Object> getState() {
		return null;
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
		return Boolean.TRUE;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
		zetMode(comRoot.getMode());
		if(logging != null) logging.setCommunicationRoot(comRoot);
	}

	@Override
	public Widget asWidget() {
		return basisPanel;
	}

	@Override
	public void kijkNa() {
		
	}
	
	public void zetMode(int mode)
	{
		this.mode = mode;
		boolean zichtbaar = true;
// alleen zichtbaar in nakijken modus als het geen toets is.
// altijd zichtbaar als een actie, maar dan geen kijkna handlers bij toets
		if (nakijkenPagina||nakijkenVak||nakijkenXWidget)
		{  zichtbaar = mode==OpdrNav.OEFENEN || mode==OpdrNav.OEFENEN_STRAFPUNTEN;
		   if (!zichtbaar) {
		     for(int i = 0 ; i<registrations.size() ; i++)
		    	 checkButton.removeButtonListener(registrations.get(i));
		   }
		   zichtbaar = zichtbaar || actionNextPage||actieBewaren||actieAfronden;	
		}
      checkButton.setVisible(zichtbaar);
	}
	
	public void zetNagekeken(boolean b) {
		
	}

	@Override
	public int getAsHoogte() {
		return ashoogte;
	}

	@Override
	public int getHeight() {
		return hoogte;
	}

	@Override
	public int getWidth() {
		return breedte;
	}
	
	public void zetVolledigeBreedte(int breedte){
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		this.ashoogte = ashoogte;
	}
	
	public TekstRegel findParentRegel()
	{
		Widget parent = asWidget();
		while (parent != null && !(parent instanceof TekstVak))
		{
			parent = parent.getParent();
		}
		return ((TekstVak) parent).getRegelVak(0);
	}

	@Override
	public void acceptCBookEvent(CBookEvent event) {
		if(READONLY.equals(event.getCommand())) {
			editable = false;
			basisPanel.setStyleDependentName("readonly", !editable);
		}
		
	}
}
