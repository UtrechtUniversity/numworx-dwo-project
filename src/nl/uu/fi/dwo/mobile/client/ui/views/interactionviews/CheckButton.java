package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.text.Text_nl;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;

public class CheckButton implements InteractionStub
{
	public static Text_nl rb = new Text_nl();
	static final String holderId = "dockholder";
	
	private HashMap<String, Object> launchState; 
	
	OpdrNavIF comRoot;
	
	private LayoutPanel basisPanel;
	int breedte = 110;
	int hoogte = 24; 
	int ashoogte = hoogte/2;//nog kijken naar zinnige invulling hiervoor. (En hoe is dit in wiskOpdr gedaan?)
	
	private PushButton checkButton;
	private String knopImageString = "";
	
	ArrayList<Object> lijst;
	
	
	public CheckButton(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		
		if (h != null && h.get("breedte") != null)
			breedte = ((Number) h.get("breedte")).intValue();
		if (h != null && h.get("hoogte") != null)
			hoogte = ((Number) h.get("hoogte")).intValue();
		if (h != null && h.get("interactiePanelLaunchState") != null)
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
		
		init(breedte, hoogte, launchState, randomVarWaarden);
		
		initialize(h, randomVarNamen, randomVarWaarden);
	}
	
	public void init(int width, int height, Map<String, Object> launchData,
			Map<String, Number> values) {
		breedte = width;
		hoogte = height;
		if (launchData != null)
		{
			if(launchData.get("knopImageString") != null) 
				knopImageString = (String)launchData.get("knopImageString");
		}
	}
	
	private void initialize(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		basisPanel = new LayoutPanel();
		basisPanel.setSize("" + breedte + "px", "" + hoogte + "px");
		
		int imWidth = breedte - 20;
		int imHeight = 20;
		Image knopImage = null;
		if(knopImageString!=null && !"".equals(knopImageString))
       	{  	knopImage = new ImageView(knopImageString).getImage();
			imWidth = knopImage.getWidth();
			System.out.println("imWidth 1: " + imWidth);
			imHeight = knopImage.getHeight();
			if(imWidth == -1) imWidth = 80;
			System.out.println("imWidth 2: " + imWidth);
			if(imHeight == -1) imHeight = 20;
		}
		if(knopImage != null)
		{	checkButton = new PushButton(knopImage);
			checkButton.getElement().getStyle().setPadding(0, Style.Unit.PX);
			checkButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		}
		else
			checkButton = new PushButton(rb.getString("klaarKnopLabel"));
		basisPanel.add(checkButton);
		basisPanel.setWidgetLeftWidth(checkButton, 0, Style.Unit.PX, imWidth, Style.Unit.PX);
		basisPanel.setWidgetTopHeight(checkButton, 0, Style.Unit.PX, imHeight, Style.Unit.PX);
		checkButton.addClickHandler(new ClickHandler(){

			public void onClick(ClickEvent e)
			{	e.stopPropagation();
				fout = false;
				for (int i = 0; i < lijst.size(); i++)
				{	Object object = lijst.get(i);
					if(object instanceof InteractionView) {
						InteractionView view = (InteractionView) object;
						view.kijkNa();
						//view.zetNagekeken(true); 
						fout |= !view.isCorrect();
					}
				}
			}
		});
	}
	
	boolean fout;

	public void zetNakijkObjecten(ArrayList<Object> lijst)
	{
		this.lijst = lijst;
	}
	
	@Override
	public HashMap<String, Object> getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getScore() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isCorrect() {
		// TODO Auto-generated method stub
		return !fout;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
	}

	@Override
	public Widget asWidget() {
		return basisPanel;
	}

	@Override
	public void kijkNa() {
		// TODO Auto-generated method stub
		
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

	@Override
	public void setAsHoogte(int ashoogte) {
		this.ashoogte = ashoogte;
	}
}
