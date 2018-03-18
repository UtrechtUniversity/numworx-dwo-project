package fi.writemathgwt.client;


import java.util.ArrayList;

import com.google.gwt.canvas.client.Canvas;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;


import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ListBox;



public class WriteMathGWT implements EntryPoint, WritePanelHolder 
{
	
	static final String holderId = "rootPanel";
	DockLayoutPanel dlp;
	int breedte = 1000;
	int hoogte = 900;
	int bottomHeight = 200;
	int leftOffset = 5;
	int topOffset = 5;
	int buttonWidth = 60;
	int buttonHeight = 22;

	
	WritePanel writePanel;
	Canvas writePanelCanvas;
	
	LayoutPanel bottomPanel;
	
	private IFormuleViewer formuleViewer;
	private Widget formulePanel;
	
	ListBox refSampleComboBox;
	PushButton wisButton, printButton, closeButton;
	TekstPopup tekstPopup;
	
	TextBox testBox;
	
	boolean standAlone = false;
	
	int tekenSet = 2;
	
	public void onModuleLoad() {
		
		/* Eliminate context menu (on right click */
	    Element body = Document.get().getBody();
	    body.setAttribute("oncontextmenu", "return false;");
		/* Eliminate context menu (on right click */
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName("dock");
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		RootPanel.get(holderId).add(dlp);
		RootPanel.get(holderId).addStyleName("root");
		
		writePanel = new WritePanel(1000,800, this, tekenSet); 
		//writePanelCanvas = writePanel.getCanvas();
		//writePanel.initContext2d();	
		
		formuleViewer = GWT.create(IFormuleViewer.class);
		formulePanel = formuleViewer.asWidget();
		//formulePanel.setWidth(600 + "px");
		//formulePanel.setHeight(150 + "px");
		
		bottomPanel = new LayoutPanel();
		formulePanel = formuleViewer.asWidget();
		bottomPanel.add(formulePanel);
		bottomPanel.setWidgetLeftWidth(formulePanel, 0, Style.Unit.PX, 600, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(formulePanel, 0, Style.Unit.PX, 150, Style.Unit.PX);
				
		standAlone = true;
		
		int testX = 50;
		int testY = 100;
		testBox = new TextBox();
// testBox herkrijgt focus niet na tekenen


/*if (standAlone)
{	bottomPanel.add(testBox);
	bottomPanel.setWidgetLeftWidth(testBox, testX, Style.Unit.PX, 500, Style.Unit.PX);
	bottomPanel.setWidgetTopHeight(testBox, testY, Style.Unit.PX, 40, Style.Unit.PX);
	testBox.addKeyDownHandler(new TextBoxKeyDownHandler());
	testBox.addClickHandler(new TextBoxClickHandler());	
}
*/		
		int currentX = 50;
		int currentY = 150;
		
		refSampleComboBox = new ListBox();
		refSampleComboBox.addItem("Load");
		for(int i=0 ; i<OneStrokeMatcher.tekens.length ; i++) {
			refSampleComboBox.addItem(OneStrokeMatcher.tekens[i]);
		}
		
		
		if (standAlone)
		{	bottomPanel.add(refSampleComboBox);
			bottomPanel.setWidgetLeftWidth(refSampleComboBox, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(refSampleComboBox, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			refSampleComboBox.addChangeHandler(new ListChangeHandler());
		}
		currentX += buttonWidth + 50;
		wisButton = new PushButton("wis");
		//wisButton.addStyleName("pushbutton");
		
		if (standAlone)
		{	bottomPanel.add(wisButton);
			bottomPanel.setWidgetLeftWidth(wisButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(wisButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			wisButton.addClickHandler(new PushClickHandler());
		}	

		currentX += buttonWidth + 50;
		printButton = new PushButton("print");
		//printButton.addStyleName("printbutton");
		if (standAlone)
		{	bottomPanel.add(printButton);
			bottomPanel.setWidgetLeftWidth(printButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(printButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			printButton.addClickHandler(new PushClickHandler());
		}	
		
		currentX += buttonWidth + 50;
		closeButton = new PushButton("sluit tekstpopup");
		//printButton.addStyleName("printbutton");
		if (standAlone)
		{	bottomPanel.add(closeButton);
			bottomPanel.setWidgetLeftWidth(closeButton, currentX, Style.Unit.PX, 2 * buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(closeButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			closeButton.addClickHandler(new PushClickHandler());
		}		
		
		dlp.addNorth(bottomPanel, bottomHeight);
		
		dlp.add(writePanel);
		
		writePanel.paint();
	}
	
	public void zetTekenSet(int num)
	{
		if ((num < 1) && (num > 2))
			num = 1;
		tekenSet = num;
		
		if (writePanel != null)
			writePanel.setTekenSet(num);
	}

	
	public void writePanelChanged() 
	{
		bottomPanel.remove(formulePanel);
		formuleViewer = GWT.create(IFormuleViewer.class);
		formuleViewer.setFormule(writePanel.parseFormule());
		formulePanel = formuleViewer.asWidget();
		bottomPanel.add(formulePanel);
		bottomPanel.setWidgetLeftWidth(formulePanel, 0, Style.Unit.PX, 600, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(formulePanel, 0, Style.Unit.PX, 150, Style.Unit.PX);
		
	}
	
	public void writeFormulaObject(String s) 
	{
		formuleViewer.setFormule(s);
	}
	
	public void wisFormulePanel() 
	{
		bottomPanel.remove(formulePanel);
		formuleViewer = GWT.create(IFormuleViewer.class);
		formuleViewer.setFormule("");
		formulePanel = formuleViewer.asWidget();
		bottomPanel.add(formulePanel);
		bottomPanel.setWidgetLeftWidth(formulePanel, 0, Style.Unit.PX, 600, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(formulePanel, 0, Style.Unit.PX, 150, Style.Unit.PX);
		
	}
	
	
	class PushClickHandler implements ClickHandler
	{
		public void onClick(ClickEvent e)
	    {
			e.stopPropagation();
			
			if (e.getSource() == wisButton)
			{
				writePanel.wis();
				wisFormulePanel();
			}
			else if (e.getSource() == printButton)
			{
				//showTekstPopup(true);
				//if (writePanel.lastObject != null)
				//	tekstPopup.setText(writePanel.lastObject.printStroke());
				String selectedString = refSampleComboBox.getSelectedValue();
				writePanel.logWriteObjects(selectedString);
			}
			else if (e.getSource() == closeButton)
			{
				showTekstPopup(false);
			}
			
	    }
	}
	
	public void showTekstPopup(boolean b)
	{
		if (!b)
		{
			if (tekstPopup != null)
			{
				dlp.remove(tekstPopup);
				tekstPopup = null;
				return;
			}
		}
		
		int popupX = dlp.getAbsoluteLeft();
		int popupY = dlp.getAbsoluteTop() + 100;
		if (tekstPopup == null)
		{	tekstPopup = new TekstPopup(this);
			//tf.setText(tfString);
			//tf.setWidth("35px");
			//tf.setHeight("20px");
			tekstPopup.setPopupPosition(popupX, popupY);
			tekstPopup.show();
			//tf.textBox.setFocus(true);
		}
		else
			tekstPopup.show();
	}

		
	class TextBoxKeyDownHandler implements KeyDownHandler
	{
		public void onKeyDown(KeyDownEvent e)
		{
			if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
				String fString = testBox.getText();
				writePanel.readFormula(fString);
				
			}
		}
	}
	class TextBoxClickHandler implements ClickHandler
	{
		public void onClick(ClickEvent e)
		{
			testBox.setFocus(true);
		}
	}
	class ListChangeHandler implements ChangeHandler
	{
		@Override
		public void onChange(ChangeEvent e)
		{
			if (e.getSource() == refSampleComboBox)
			{
				String selectedString = refSampleComboBox.getSelectedValue();
				if (selectedString != null)
				{
					writePanel.loadRefSamples(selectedString);
					writePanel.setNoParse(!selectedString.equals("Load"));
				}

			}
			
		}
	}
}
