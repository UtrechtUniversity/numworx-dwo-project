package fi.writemathgwt.client;


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


public class WriteMathGWT implements EntryPoint, WritePanelHolder {
	
	static final String holderId = "rootPanel";
	
	private DockLayoutPanel dlp;
	private int breedte = 1000;
	private int hoogte = 900;
	private int bottomHeight = 200;
	private int leftOffset = 5;
	private int topOffset = 5;
	private int buttonWidth = 60;
	private int buttonHeight = 22;

	private WritePanel writePanel;
	private Canvas writePanelCanvas;
	
	private LayoutPanel bottomPanel;
	
	private IFormuleViewer formuleViewer;
	private Widget formulePanel;
	
	private PushButton wisButton, printButton, closeButton;
	
	private TextBox testBox;
	
	private boolean standAlone = false;
	
	private int tekenSet = 2;
	
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
		
		formuleViewer = GWT.create(IFormuleViewer.class);
		formulePanel = formuleViewer.asWidget();
		
		bottomPanel = new LayoutPanel();
		formulePanel = formuleViewer.asWidget();
		bottomPanel.add(formulePanel);
		bottomPanel.setWidgetLeftWidth(formulePanel, 0, Style.Unit.PX, 600, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(formulePanel, 0, Style.Unit.PX, 150, Style.Unit.PX);
				
		standAlone = true;
		
		testBox = new TextBox();
	
		int currentX = 50;
		int currentY = 150;
		
		currentX += buttonWidth + 50;
		wisButton = new PushButton("wis");
		//wisButton.addStyleName("pushbutton");
		
		if (standAlone)	{	
			bottomPanel.add(wisButton);
			bottomPanel.setWidgetLeftWidth(wisButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(wisButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			wisButton.addClickHandler(new PushClickHandler());
		}	

		currentX += buttonWidth + 50;
		printButton = new PushButton("print");
		//printButton.addStyleName("printbutton");
		if (standAlone)	{	
			bottomPanel.add(printButton);
			bottomPanel.setWidgetLeftWidth(printButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(printButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			printButton.addClickHandler(new PushClickHandler());
		}	
		
		currentX += buttonWidth + 50;
		closeButton = new PushButton("sluit tekstpopup");
		//printButton.addStyleName("printbutton");
		if (standAlone)	{	
			bottomPanel.add(closeButton);
			bottomPanel.setWidgetLeftWidth(closeButton, currentX, Style.Unit.PX, 2 * buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(closeButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			closeButton.addClickHandler(new PushClickHandler());
		}		
		
		dlp.addNorth(bottomPanel, bottomHeight);
		dlp.add(writePanel);
		
		writePanel.paint();
	}
	
	public void zetTekenSet(int num) {
		if ((num < 1) && (num > 2))
			num = 1;
		tekenSet = num;
		
		if (writePanel != null)
			writePanel.setTekenSet(num);
	}

	
	public void writePanelChanged() {
		bottomPanel.remove(formulePanel);
		formuleViewer = GWT.create(IFormuleViewer.class);
		formuleViewer.setFormule(writePanel.parseFormule());
		formulePanel = formuleViewer.asWidget();
		bottomPanel.add(formulePanel);
		bottomPanel.setWidgetLeftWidth(formulePanel, 0, Style.Unit.PX, 600, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(formulePanel, 0, Style.Unit.PX, 150, Style.Unit.PX);
	}
	
	public void writeFormulaObject(String s) {
		formuleViewer.setFormule(s);
	}
	
	public void wisFormulePanel() {
		bottomPanel.remove(formulePanel);
		formuleViewer = GWT.create(IFormuleViewer.class);
		formuleViewer.setFormule("");
		formulePanel = formuleViewer.asWidget();
		bottomPanel.add(formulePanel);
		bottomPanel.setWidgetLeftWidth(formulePanel, 0, Style.Unit.PX, 600, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(formulePanel, 0, Style.Unit.PX, 150, Style.Unit.PX);
	}
	
	
	class PushClickHandler implements ClickHandler {
		
		public void onClick(ClickEvent e) {
			e.stopPropagation();
			
			if (e.getSource() == wisButton)	{
				writePanel.wis();
				wisFormulePanel();
			}
			else if (e.getSource() == printButton) {
				
			}
			else if (e.getSource() == closeButton)	{
				
			}
		}
	}
	
	class TextBoxKeyDownHandler implements KeyDownHandler{
		
		public void onKeyDown(KeyDownEvent e) {
			if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)	{
				String fString = testBox.getText();
				writePanel.readFormula(fString);
			}
		}
	}
	class TextBoxClickHandler implements ClickHandler {
		
		public void onClick(ClickEvent e) {
			testBox.setFocus(true);
		}
	}
}
