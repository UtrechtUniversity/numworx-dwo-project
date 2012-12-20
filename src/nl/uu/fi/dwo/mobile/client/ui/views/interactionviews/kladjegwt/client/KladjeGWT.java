package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.kladjegwt.client;

import java.util.HashMap;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class KladjeGWT implements InteractionView
{

	static final String holderId = "dockholder";
	static final String upgradeMessage = "Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

	// UI
	DockLayoutPanel dlp;
	LayoutPanel bottomPanel;
	KladjeGWTVeld kladjeGWTVeld;
	Canvas kladjeGWTCanvas;
	ToggleButton tekenButton, gumButton, tekenLijnButton, tekenRechthoekButton,
			tekenCirkelButton, tekenTekstButton, selecterenButton;

	int breedte = 500;
	int hoogte = 450;
	int bottomHeight = 32;
	int leftOffset = 5;
	int topOffset = 5;
	int toggleSize = 22;
	int buttonWidth = 40;
	int buttonHeight = 22;

	private HashMap<String, Object> launchState;
	String[] randomVarNamen = null;
	HashMap<String, Object> randomVarWaarden = null;

	FlowPanel panel = new FlowPanel();

	// images
	KladjeGWTClientBundle kladjeGWTClientBundle;
	ImageResource tekenKnopUpResource, tekenKnopDownResource,
			gumKnopUpResource, gumKnopDownResource, tekenLijnUpResource,
			tekenLijnDownResource, tekenRechthoekUpResource,
			tekenRechthoekDownResource, tekenCirkelUpResource,
			tekenCirkelDownResource, tekenTekstUpResource,
			tekenTekstDownResource, selecterenUpResource,
			selecterenDownResource;
	Image tekenKnopUpImage, tekenKnopDownImage, gumKnopUpImage,
			gumKnopDownImage, tekenLijnUpImage, tekenLijnDownImage,
			tekenRechthoekUpImage, tekenRechthoekDownImage, tekenCirkelUpImage,
			tekenCirkelDownImage, tekenTekstUpImage, tekenTekstDownImage,
			selecterenUpImage, selecterenDownImage;

	PushButton terugButton, wisButton;

	public KladjeGWT(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		if (h != null && h.get("breedte") != null)
			breedte = (Integer) h.get("breedte");
		if (h != null && h.get("hoogte") != null)
			hoogte = (Integer) h.get("hoogte");
		if (h != null && h.get("interactiePanelLaunchState") != null)
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");

		panel.getElement().getStyle().setWidth(breedte, Unit.PX);
		panel.getElement().getStyle().setHeight(hoogte, Unit.PX);
		panel.getElement().getStyle().setProperty("textAlign", "right");

		kladjeGWTClientBundle = GWT.create(KladjeGWTClientBundle.class);
		ImageResource tekenKnopUpResource = kladjeGWTClientBundle.tekenKnopUpResource();
		ImageResource tekenKnopDownResource = kladjeGWTClientBundle.tekenKnopDownResource();
		Image tekenKnopUpImage = new Image(tekenKnopUpResource);
		Image tekenKnopDownImage = new Image(tekenKnopDownResource);
		tekenKnopUpImage.addStyleName("upimage");
		tekenKnopDownImage.addStyleName("downimage");

		gumKnopUpResource = kladjeGWTClientBundle.gumKnopUpResource();
		gumKnopDownResource = kladjeGWTClientBundle.gumKnopDownResource();
		gumKnopUpImage = new Image(gumKnopUpResource);
		gumKnopDownImage = new Image(gumKnopDownResource);
		gumKnopUpImage.addStyleName("upimage");
		gumKnopDownImage.addStyleName("downimage");

		tekenLijnUpResource = kladjeGWTClientBundle.tekenLijnUpResource();
		tekenLijnDownResource = kladjeGWTClientBundle.tekenLijnDownResource();
		tekenLijnUpImage = new Image(tekenLijnUpResource);
		tekenLijnDownImage = new Image(tekenLijnDownResource);
		tekenLijnUpImage.addStyleName("upimage");
		tekenLijnDownImage.addStyleName("downimage");

		tekenRechthoekUpResource = kladjeGWTClientBundle.tekenRechthoekUpResource();
		tekenRechthoekDownResource = kladjeGWTClientBundle.tekenRechthoekDownResource();
		tekenRechthoekUpImage = new Image(tekenRechthoekUpResource);
		tekenRechthoekDownImage = new Image(tekenRechthoekDownResource);
		tekenRechthoekUpImage.addStyleName("upimage");
		tekenRechthoekDownImage.addStyleName("downimage");

		tekenCirkelUpResource = kladjeGWTClientBundle.tekenCirkelUpResource();
		tekenCirkelDownResource = kladjeGWTClientBundle.tekenCirkelDownResource();
		tekenCirkelUpImage = new Image(tekenCirkelUpResource);
		tekenCirkelDownImage = new Image(tekenCirkelDownResource);
		tekenCirkelUpImage.addStyleName("upimage");
		tekenCirkelDownImage.addStyleName("downimage");

		tekenTekstUpResource = kladjeGWTClientBundle.tekenTekstUpResource();
		tekenTekstDownResource = kladjeGWTClientBundle.tekenTekstDownResource();
		tekenTekstUpImage = new Image(tekenTekstUpResource);
		tekenTekstDownImage = new Image(tekenTekstDownResource);
		tekenTekstUpImage.addStyleName("upimage");
		tekenTekstDownImage.addStyleName("downimage");

		selecterenUpResource = kladjeGWTClientBundle.selecterenUpResource();
		selecterenDownResource = kladjeGWTClientBundle.selecterenDownResource();
		selecterenUpImage = new Image(selecterenUpResource);
		selecterenDownImage = new Image(selecterenDownResource);
		selecterenUpImage.addStyleName("upimage");
		selecterenDownImage.addStyleName("downimage");

		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName("dock");
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		//RootPanel.get(holderId).add(dlp);
		//RootPanel.get(holderId).addStyleName("root");

		panel.add(dlp);
		panel.addStyleName("root");

		bottomPanel = new LayoutPanel();
		bottomPanel.addStyleName("bottom");

		dlp.addSouth(bottomPanel, bottomHeight);

		kladjeGWTVeld = new KladjeGWTVeld(breedte, hoogte - bottomHeight);

		kladjeGWTCanvas = kladjeGWTVeld.getCanvas();
		//if (kladjeGWTCanvas == null) {
		//  RootPanel.get(holderId).add(new Label(upgradeMessage));
		//  return;
		// }

		//kladjeGWTCanvas.addStyleName("canvas");
		kladjeGWTVeld.initContext2d();

		dlp.add(kladjeGWTVeld.getAsPanel());

		int currentX = leftOffset;
		int currentY = topOffset;
		//tekenButton = new ToggleButton("t", "T", new ToggleClickHandler());
		tekenButton = new ToggleButton(tekenKnopUpImage, tekenKnopDownImage);
		tekenButton.addStyleName("togglebutton");
		bottomPanel.add(tekenButton);
		bottomPanel.setWidgetLeftWidth(tekenButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(tekenButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		tekenButton.addTouchStartHandler(new ToggleTouchStartHandler());
		tekenButton.addTouchEndHandler(new ToggleTouchStartHandler());

		currentX += toggleSize;

		//gumButton = new ToggleButton("g", "G", new ToggleClickHandler());
		gumButton = new ToggleButton(gumKnopUpImage, gumKnopDownImage, new ToggleClickHandler());
		gumButton.addStyleName("togglebutton");
		bottomPanel.add(gumButton);
		bottomPanel.setWidgetLeftWidth(gumButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(gumButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		//gumButton.addTouchStartHandler(new ToggleTouchStartHandler());

		currentX += toggleSize;

		tekenLijnButton = new ToggleButton(tekenLijnUpImage, tekenLijnDownImage, new ToggleClickHandler());
		tekenLijnButton.addStyleName("togglebutton");
		bottomPanel.add(tekenLijnButton);
		bottomPanel.setWidgetLeftWidth(tekenLijnButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(tekenLijnButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		//tekenLijnButton.addTouchStartHandler(new ToggleTouchStartHandler());

		currentX += toggleSize;

		tekenRechthoekButton = new ToggleButton(tekenRechthoekUpImage, tekenRechthoekDownImage, new ToggleClickHandler());
		tekenRechthoekButton.addStyleName("togglebutton");
		bottomPanel.add(tekenRechthoekButton);
		bottomPanel.setWidgetLeftWidth(tekenRechthoekButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(tekenRechthoekButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		//tekenRechthoekButton.addTouchStartHandler(new ToggleTouchStartHandler());

		currentX += toggleSize;

		tekenCirkelButton = new ToggleButton(tekenCirkelUpImage, tekenCirkelDownImage, new ToggleClickHandler());
		tekenCirkelButton.addStyleName("togglebutton");
		bottomPanel.add(tekenCirkelButton);
		bottomPanel.setWidgetLeftWidth(tekenCirkelButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(tekenCirkelButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		tekenCirkelButton.addTouchStartHandler(new ToggleTouchStartHandler());

		currentX += toggleSize;

		/*		
				tekenTekstButton = new ToggleButton(tekenTekstUpImage, tekenTekstDownImage, 
						 							new ToggleClickHandler());
				tekenTekstButton.addStyleName("togglebutton");
				bottomPanel.add(tekenTekstButton);
				bottomPanel.setWidgetLeftWidth(tekenTekstButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
				bottomPanel.setWidgetTopHeight(tekenTekstButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
				tekenTekstButton.addTouchStartHandler(new ToggleTouchStartHandler());

				currentX += toggleSize;
		*/

		selecterenButton = new ToggleButton(selecterenUpImage, selecterenDownImage, new ToggleClickHandler());
		selecterenButton.addStyleName("togglebutton");
		bottomPanel.add(selecterenButton);
		bottomPanel.setWidgetLeftWidth(selecterenButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(selecterenButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		selecterenButton.addTouchStartHandler(new ToggleTouchStartHandler());

		currentX += toggleSize + 2 * leftOffset;

		/*		
				terugButton = new PushButton("terug", new PushClickHandler());
				terugButton.addStyleName("pushbutton");
				bottomPanel.add(terugButton);
				bottomPanel.setWidgetLeftWidth(terugButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
				bottomPanel.setWidgetTopHeight(terugButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
				terugButton.addTouchStartHandler(new PushTouchStartHandler());
				
				currentX += buttonWidth + 2 * leftOffset;		
		*/

		wisButton = new PushButton("wis", new PushClickHandler());
		wisButton.addStyleName("pushbutton");
		bottomPanel.add(wisButton);
		bottomPanel.setWidgetLeftWidth(wisButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(wisButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		wisButton.addTouchStartHandler(new PushTouchStartHandler());

		currentX += buttonWidth + 2 * leftOffset;

		kladjeGWTVeld.paint();

	}

	public Panel getAsPanel()
	{
		return panel;
	}

	public Widget asWidget()
	{
		return getAsPanel();
	}

	//public void onModuleLoad() 
	//{
	//
	//} // onModuleLoad

	void buttonsUp(ToggleButton tb)
	{
		if (!tekenButton.equals(tb))
			tekenButton.setDown(false);
		if (!gumButton.equals(tb))
			gumButton.setDown(false);
		if (!tekenLijnButton.equals(tb))
			tekenLijnButton.setDown(false);
		if (!tekenRechthoekButton.equals(tb))
			tekenRechthoekButton.setDown(false);
		if (!tekenCirkelButton.equals(tb))
			tekenCirkelButton.setDown(false);
		// 		if (!tekenTekstButton.equals(tb))
		// 			tekenTekstButton.setDown(false);
		if (!selecterenButton.equals(tb))
			selecterenButton.setDown(false);
	}

	class ToggleClickHandler implements ClickHandler
	{
		public void onClick(ClickEvent e)
		{
			e.preventDefault();
			e.stopPropagation();

			if (e.getSource() == tekenButton)
			{
				if (tekenButton.isDown())
				{
					buttonsUp(tekenButton);
					kladjeGWTVeld.mouseMode = kladjeGWTVeld.tekenen;
					kladjeGWTVeld.selecteerRechthoek = null;
					kladjeGWTVeld.paint();
				}
			}
			else if (e.getSource() == gumButton)
			{
				if (gumButton.isDown())
				{
					buttonsUp(gumButton);
					kladjeGWTVeld.mouseMode = kladjeGWTVeld.gummen;
					kladjeGWTVeld.selecteerRechthoek = null;
					kladjeGWTVeld.paint();

				}
			}
			else if (e.getSource() == tekenLijnButton)
			{
				if (tekenLijnButton.isDown())
				{
					buttonsUp(tekenLijnButton);
					kladjeGWTVeld.mouseMode = kladjeGWTVeld.lijnTekenen;
					kladjeGWTVeld.selecteerRechthoek = null;
					kladjeGWTVeld.paint();

				}
			}
			else if (e.getSource() == tekenRechthoekButton)
			{
				if (tekenRechthoekButton.isDown())
				{
					buttonsUp(tekenRechthoekButton);
					kladjeGWTVeld.mouseMode = kladjeGWTVeld.rechthoekTekenen;
					kladjeGWTVeld.selecteerRechthoek = null;
					kladjeGWTVeld.paint();

				}
			}
			else if (e.getSource() == tekenCirkelButton)
			{
				if (tekenCirkelButton.isDown())
				{
					buttonsUp(tekenCirkelButton);
					kladjeGWTVeld.mouseMode = kladjeGWTVeld.cirkelTekenen;
					kladjeGWTVeld.selecteerRechthoek = null;
					kladjeGWTVeld.paint();

				}
			}
			/*    		
			    		else if (e.getSource() == tekenTekstButton)
			    		{
			    			if (tekenTekstButton.isDown())
			    			{
			    				buttonsUp(tekenTekstButton);
			    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.tekstTekenen;
			    				kladjeGWTVeld.selecteerRechthoek = null;
			    				kladjeGWTVeld.paint();
			    				
			    			}
			    		}
			*/
			else if (e.getSource() == selecterenButton)
			{
				if (selecterenButton.isDown())
				{
					buttonsUp(selecterenButton);
					kladjeGWTVeld.mouseMode = kladjeGWTVeld.selecteren;
					kladjeGWTVeld.selecteerRechthoek = null;
					kladjeGWTVeld.paint();

				}
			}
			e.preventDefault();
			e.stopPropagation();

		}
	} // ToggleClickHandler

	class ToggleTouchStartHandler implements TouchStartHandler, TouchEndHandler
	{
		public void onTouchEnd(TouchEndEvent e)
		{
			//e.preventDefault();
			//e.stopPropagation();
		}

		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();

			if (e.getSource() == tekenButton)
			{
				if (tekenButton.isDown())
				{
					buttonsUp(tekenButton);
					kladjeGWTVeld.mouseMode = kladjeGWTVeld.tekenen;
					kladjeGWTVeld.selecteerRechthoek = null;
					kladjeGWTVeld.paint();

				}
			}
			else if (e.getSource() == gumButton)
			{
				if (gumButton.isDown())
				{
					buttonsUp(gumButton);
					kladjeGWTVeld.mouseMode = kladjeGWTVeld.gummen;
					kladjeGWTVeld.selecteerRechthoek = null;
					kladjeGWTVeld.paint();

				}
			}
			else if (e.getSource() == tekenLijnButton)
			{
				if (tekenLijnButton.isDown())
				{
					buttonsUp(tekenLijnButton);
					kladjeGWTVeld.mouseMode = kladjeGWTVeld.lijnTekenen;
					kladjeGWTVeld.selecteerRechthoek = null;
					kladjeGWTVeld.paint();

				}
			}
			else if (e.getSource() == tekenRechthoekButton)
			{
				if (tekenRechthoekButton.isDown())
				{
					buttonsUp(tekenRechthoekButton);
					kladjeGWTVeld.mouseMode = kladjeGWTVeld.rechthoekTekenen;
					kladjeGWTVeld.selecteerRechthoek = null;
					kladjeGWTVeld.paint();

				}
			}
			else if (e.getSource() == tekenCirkelButton)
			{
				if (tekenCirkelButton.isDown())
				{
					buttonsUp(tekenCirkelButton);
					kladjeGWTVeld.mouseMode = kladjeGWTVeld.cirkelTekenen;
					kladjeGWTVeld.selecteerRechthoek = null;
					kladjeGWTVeld.paint();

				}
			}
			/*    		
			    		else if (e.getSource() == tekenTekstButton)
			    		{
			    			if (tekenTekstButton.isDown())
			    			{
			    				buttonsUp(tekenTekstButton);
			    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.tekstTekenen;
			    				kladjeGWTVeld.selecteerRechthoek = null;
			    				kladjeGWTVeld.paint();
			    				
			    			}
			    		}
			*/
			else if (e.getSource() == selecterenButton)
			{
				if (selecterenButton.isDown())
				{
					buttonsUp(selecterenButton);
					kladjeGWTVeld.mouseMode = kladjeGWTVeld.selecteren;
					kladjeGWTVeld.selecteerRechthoek = null;
					kladjeGWTVeld.paint();

				}
			}

			e.preventDefault();
			e.stopPropagation();

		}
	} // ToggleTouchStartHandler

	class PushClickHandler implements ClickHandler
	{

		public void onClick(ClickEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			if (e.getSource() == terugButton)
			{
				kladjeGWTVeld.undo();

			}
			else if (e.getSource() == wisButton)
			{
				kladjeGWTVeld.wis();
			}

		}
	}

	class PushTouchStartHandler implements TouchStartHandler
	{
		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();

			if (e.getSource() == terugButton)
			{
				kladjeGWTVeld.undo();

			}
			else if (e.getSource() == wisButton)
			{
				kladjeGWTVeld.wis();
			}

		}
	}

	@Override
	public HashMap<String, Object> getState()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getScore()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isCorrect()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		// TODO Auto-generated method stub

	}

}
