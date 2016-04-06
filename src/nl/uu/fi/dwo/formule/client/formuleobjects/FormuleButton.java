package nl.uu.fi.dwo.formule.client.formuleobjects;



import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;

import fi.wiskopdr.text.Text;

public class FormuleButton extends TouchButton{

	protected String code;
	protected boolean focus = false;
	protected boolean actief = false;
	protected boolean focusable = true;
	protected boolean toggle = false;
	boolean toggleAan = false;
	
	protected CssColor randDonker = CssColor.make(178,105,105);
	protected CssColor randLicht = CssColor.make(255, 214, 214);
	
	Context2d ctx;
	
	
	public static int BEWERKINGSKNOP = 1;
	private int soort = 0;

	public FormuleButton()
	{
		this("",0);
	}
	
	public FormuleButton(String s)
	{
		this(s,0);
	}
	public FormuleButton(String s, int soort)
	{	code = s;
		this.soort = soort;
		this.getElement().getStyle().setMargin(1, Style.Unit.PX);
		if(soort==BEWERKINGSKNOP)
		{
			Canvas canvas = Canvas.createIfSupported();
			canvas.setCoordinateSpaceHeight(20);
			canvas.setCoordinateSpaceWidth(20);
			ctx = canvas.getContext2d();
			this.add(canvas);
			
			paintComponent();
			
//			this.getElement().getStyle().setFontSize(10, Style.Unit.PX);
//			this.setText(code);
//			Image achtergrond = new Image(DWOplayer.DWO_BUNDLE.formuleachtergrondknop().getSafeUri());
//			this.getElement().getStyle().setBackgroundImage(achtergrond.toString());
//			//this.add(achtergrond);
			
			
		}
		
	}
	
	public void paintComponent()
	{
		//achtergrond tekenen afhankelijk van toggling
		for(int i=0 ; i<10 ; i++)
		{
			ctx.setFillStyle(CssColor.make(255-i, 150+i*95/10, 150 + i*95/10));
			ctx.fillRect(0, 18-2*i, 20, 2);
		}
		if(actief || toggleAan)ctx.setStrokeStyle(randDonker);
		else ctx.setStrokeStyle(randLicht);
		ctx.beginPath();
		ctx.moveTo(0, 19);
		ctx.lineTo(0, 0);
		ctx.lineTo(19, 0);
		ctx.stroke();
		//g.drawLine(0,0,getSize().width-1,0);
		//g.drawLine(0,0,0,getSize().height-1);
		if(focus)
		{	ctx.beginPath();
			ctx.moveTo(1,18);
			ctx.lineTo(1, 1);
			ctx.lineTo(18,1);
			ctx.stroke();
			//g.drawLine(1,1,getSize().width-2,1);
			//g.drawLine(1,1,1,getSize().height-2);
		}
		if(actief || toggleAan)ctx.setStrokeStyle(randLicht);
		else ctx.setStrokeStyle(randDonker);
		ctx.beginPath();
		ctx.moveTo(0, 20);
		ctx.lineTo(20,20);
		ctx.lineTo(20, 0);
		ctx.stroke();
		
		//g.drawLine(getSize().width-1,0,getSize().width-1,getSize().height-1);
		//g.drawLine(0,getSize().height-1,getSize().width-1,getSize().height-1);
		if(focus)
		{	ctx.beginPath();
			ctx.moveTo(1,19);
			ctx.lineTo(19, 19);
			ctx.lineTo(19, 1);
			ctx.stroke();
			
			//g.drawLine(getSize().width-2,1,getSize().width-2,getSize().height-2);
			//g.drawLine(1,getSize().height-2,getSize().width-2,getSize().height-2);
		}
		
		//tekst/tekening op knop zetten afhankelijk van code
		int b = 20;
		int h = 20;
		ctx.setFillStyle("black");
		ctx.setStrokeStyle("black");
		if(code.equals("abc") || code.equals("sub"))
		{	String i18n = Text.rb.getString(code);
			ctx.setFont("10px Arial");
			ctx.fillText(i18n, 1, 15);
		}
		else if(code.equals("wortelbewerk"))
		{   ctx.beginPath();
			ctx.moveTo(h/4, h-3);
			ctx.lineTo(3, 2*h/4);
			ctx.moveTo(4, 2*h/4);
			ctx.lineTo(h/4+1, h-3);
			ctx.lineTo(h/2, 3);
			ctx.lineTo(b-3, 3);
			ctx.stroke();
//			g.drawLine(3,2*h/4,   h/4,h-3);
//	        g.drawLine(4,2*h/4,   h/4+1,h-3);
//	        g.drawLine(h/4+1,h-3, h/2,3);
//	        g.drawLine(h/2,3,     b-3,3);
	    }
		else if(code.equals("plus"))
		{	ctx.beginPath();
			ctx.moveTo(b/4+2, h/2);
			ctx.lineTo(3*b/4-2, h/2);
			ctx.moveTo(b/2, h/4+2);
			ctx.lineTo(b/2, 3*h/4-2);
			ctx.stroke();
			//g.drawLine(b/4+2,h/2,3*b/4-2,h/2);
			//g.drawLine(b/2,h/4+2,b/2,3*h/4-2);
		}
		else if(code.equals("min"))
		{	ctx.beginPath();
			ctx.moveTo(b/4+2, h/2);
			ctx.lineTo(3*b/4-2, h/2);
			ctx.stroke();
			
			//g.drawLine(b/4+2,h/2,3*b/4-2,h/2);
		}
		else if(code.equals("maal"))
		{	ctx.beginPath();
			ctx.moveTo(b/4+2, h/4+2);
			ctx.lineTo(3*b/4-2, 3*h/4-2);
			ctx.moveTo(b/4+2, 3*h/4-2);
			ctx.lineTo(3*b/4-2,h/4+2);
			ctx.stroke();
			
			//g.drawLine(b/4+2,h/4+2,3*b/4-2,3*h/4-2);
			//g.drawLine(b/4+2,3*h/4-2,3*b/4-2,h/4+2);
		}
		else if(code.equals("deel"))
		{	ctx.fillRect(b/2, h/4+1, 2, 2);
			ctx.fillRect(b/2, 3*h/4-2, 2, 2);
			ctx.beginPath();
			ctx.moveTo(b/4+2, h/2);
			ctx.lineTo(3*b/4-1, h/2);
			ctx.stroke();
			
//			g.fillRect(b/2,h/4+1,2,2);
//			g.fillRect(b/2,3*h/4-2,2,2);
//			g.drawLine(b/4+2,b/2,3*b/4-2,b/2);
		}
		else if(code.equals("haakjesweg"))
		{	ctx.setFont("14px Arial");
			ctx.fillText("(", 5, 15);
			ctx.fillText(")", 11, 15);
			ctx.beginPath();
			ctx.moveTo(b/5, h/5);
			ctx.lineTo(4*b/5, 4*h/5);
			ctx.stroke();
			
//			g.drawString("(",5,15);
//			g.drawString(")",11,15);
//			g.drawLine(b/5,h/5,4*b/5,4*h/5);
		}
		else if(code.equals("ontbind"))
		{	ctx.setFont("14px Arial");
		
			ctx.fillText("(", 11, 15);
			ctx.fillText(")", 5, 15);
		
//			g.drawString("(",11,15);
//			g.drawString(")",5,15);
		}
		
		else if(code.equals("splits"))
		{	
			ctx.beginPath();
			ctx.moveTo(4, 12);
			ctx.lineTo(4, 16);
			ctx.lineTo(8, 16);
			ctx.moveTo(4, 16);
			ctx.lineTo(10, 10);
			ctx.lineTo(10, 4);
			ctx.moveTo(10, 10);
			ctx.lineTo(16, 16);
			ctx.lineTo(16, 12);
			ctx.moveTo(16, 16);
			ctx.lineTo(12, 16);
			ctx.stroke();
			
			
//			g.drawLine(10,10,16,16);
//			g.drawLine(10,10,4,16);
//			g.drawLine(10,4,10,10);
//			g.drawLine(16,16,16,12);
//			g.drawLine(16,16,12,16);
//			g.drawLine(4,16,8,16);
//			g.drawLine(4,16,4,12);
		}
		else if(code.equals("herleid"))
		{	ctx.beginPath();
			ctx.rect(b/5, h/6, b/5, h/2);
			ctx.rect(b/2+1, h/6, b/5, h/2);
			ctx.moveTo(4*b/5, 4*h/5-1);
			ctx.lineTo(4*b/5-1, 4*h/5);
			ctx.lineTo(b/5, 4*h/5);
			ctx.lineTo(b/5-1, 4*h/5-1);
			ctx.stroke();
			
//			g.drawRect(b/5,h/6,b/5,h/2);
//			g.drawRect(b/2+1,h/6,b/5,h/2);
//			g.drawLine(b/5,4*h/5,4*b/5-1,4*h/5);
//			g.drawLine(b/5,4*h/5,b/5-1,4*h/5-1);
//			g.drawLine(4*b/5-1,4*h/5,4*b/5,4*h/5-1);
		}
				
	}
	
	public void setFocusable(boolean b)
	{	focusable = b;
	}
	
	public String getCode()
	{	return code;
	}
	
	public void setCode(String s)
	{	 code = s;
	}
	
	public void setToggle(boolean b)
	{	toggle = b;
	}
	
	public boolean isToggleAan()
	{	return toggleAan;
	}
	
	
}
