package fi.writemathgwt.client;

//import javax.swing.JPanel;
//import javax.swing.JButton;
//import javax.swing.JLabel;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;

public class CorrectiePanel extends LayoutPanel //JPanel 
{
	String teken1 = "";
	String teken2 = "";
	String teken3 = "";
	String teken4 = "";
	
	//JButton t1Button, t2Button, t3Button, t4Button, closeButton;
	PushButton t1Button, t2Button, t3Button, t4Button, closeButton;

	public CorrectiePanel(String t1, String t2, String t3, String t4)
	{
		teken1 = t1;
		teken2 = t2;
		teken3 = t3;
		teken4 = t4;
		
		//t1Button = new JButton(teken1);
		//t2Button = new JButton(teken2);
		//t3Button = new JButton(teken3);
		//t4Button = new JButton(teken4);
		
		t1Button = new PushButton(teken1);
		t2Button = new PushButton(teken2);
		t3Button = new PushButton(teken3);
		t4Button = new PushButton(teken4);
		
		t1Button.addStyleName("pushbutton");
		t2Button.addStyleName("pushbutton");
		t3Button.addStyleName("pushbutton");
		t4Button.addStyleName("pushbutton");

		//setLayout(null);
		
		//t1Button.setBounds(0,0,60,20);
		//t2Button.setBounds(0,20,60,20);
		//t3Button.setBounds(0,40,60,20);
		//t4Button.setBounds(0,60,60,20);
		//closeButton.setBounds(0,80,60,20);
		
		add(t1Button);
		add(t2Button);
		add(t3Button);
		add(t4Button);
		//add(closeButton);
		
		setWidgetLeftWidth(t1Button, 0, Style.Unit.PX, 30, Style.Unit.PX);
		setWidgetTopHeight(t1Button, 0, Style.Unit.PX, 20, Style.Unit.PX);
		setWidgetLeftWidth(t2Button, 0, Style.Unit.PX, 30, Style.Unit.PX);
		setWidgetTopHeight(t2Button, 20, Style.Unit.PX, 20, Style.Unit.PX);
		setWidgetLeftWidth(t3Button, 0, Style.Unit.PX, 30, Style.Unit.PX);
		setWidgetTopHeight(t3Button, 40, Style.Unit.PX, 20, Style.Unit.PX);
		setWidgetLeftWidth(t4Button, 0, Style.Unit.PX, 30, Style.Unit.PX);
		setWidgetTopHeight(t4Button, 60, Style.Unit.PX, 20, Style.Unit.PX);

				
		//setSize(60,80);
		
//System.out.println("cP constructed");		
	}
	
	public void zetTekens(String t1, String t2, String t3, String t4)
	{
		t1Button.setText(t1);
		t2Button.setText(t2);
		t3Button.setText(t3);
		t4Button.setText(t4);
	}
	
	
}
