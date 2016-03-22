package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.text.Text;
import fi.wiskopdr.text.TextConstants;
import fi.wiskopdr.text.Text_nl;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;

public class CheckButton implements InteractionStub
{
	public static TextConstants rb = Text.constants;
	static final String holderId = "dockholder";
	private static Logger logger = Logger.getLogger("CheckButton");
	
	private Map<String, Object> launchState; 
	
	OpdrNavIF comRoot;
	
	private LayoutPanel basisPanel;
	int breedte = 110;
	int hoogte = 24; 
	int ashoogte = 12;//nog kijken naar zinnige invulling hiervoor. (En hoe is dit in wiskOpdr gedaan?)
	
	private PushButton checkButton;
	private String knopImageString = "";
	
	ArrayList<Object> lijst;
	
	private int mode; 
	
	
	public CheckButton(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		ObjectMap map = JSONUtilities.wrapMap(h);
		if (h != null && map.containsKey("breedte") )
			breedte = map.getInt("breedte");
		if (h != null && map.containsKey("hoogte"))
			hoogte = map.getInt("hoogte");
		if (h != null && map.containsKey("interactiePanelLaunchState") )
			launchState =  map.getMap("interactiePanelLaunchState");
		
		init(breedte, hoogte, launchState, randomVarWaarden);
		
		initialize(h, randomVarNamen, randomVarWaarden);
	}
	
	public void init(int width, int height, Map<String, Object> launchData,
			Map<String, Number> values) {
		breedte = width;
		hoogte = height;
		if (launchData != null)
		{
			if(launchData.containsKey("knopImageString") ) 
				knopImageString = (String)launchData.get("knopImageString");
		}
	}
	
	private void initialize(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		basisPanel = new LayoutPanel();
		basisPanel.setStylePrimaryName("checkbutton");
		//basisPanel.setSize("" + breedte + "px", "" + hoogte + "px");
		//ashoogte = hoogte / 2;
		
		int imWidth = breedte;
		int imHeight = 20;
		knopImage = null;
		if(knopImageString!=null && !"".equals(knopImageString))
       	{  	ImageView imageView = new ImageView(knopImageString);
       		knopImage = imageView.getImage();
			if(knopImage != null)
			{
				imWidth = imageView.getWidth();
				imHeight = imageView.getHeight();
			}
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
		if(knopImage != null)
		{	checkButton = new PushButton(knopImage);
			checkButton.getElement().getStyle().setPadding(0, Style.Unit.PX);
			checkButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		}
		else
		{	checkButton = new PushButton(rb.klaarKnopLabel());
			checkButton.getElement().getStyle().setFontSize(12, Style.Unit.PX);
			checkButton.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		}
		
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
			
		checkButton.addClickHandler(new ClickHandler(){

			public void onClick(ClickEvent e)
			{	e.stopPropagation();
//				correct = Boolean.TRUE;
				logger.warning("CheckButton click start");
				comRoot.pause();
				for (int i = 0; i < lijst.size(); i++)
				{	Object object = lijst.get(i);
					if(object instanceof InteractionView) {
						InteractionView view = (InteractionView) object;
						view.kijkNa();
						//view.zetNagekeken(true); 
	//						Boolean check = view.isCorrect();
	//						if(check == null) correct = null;
	//						if(check == Boolean.FALSE) {
	//							correct = check; 
	//							return; // early out.
	//						}
					}
				}
				comRoot.unpause();				
				logger.warning("CheckButton click end");
			}
		});
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
		checkButton.setVisible(mode==0 || mode==1);
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
}
