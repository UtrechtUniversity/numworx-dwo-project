package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.stelselsvergelijkingen;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.user.client.ui.FlowPanel;



public class StelselPijl {
	
	Canvas pijlCanvas;
	Context2d g;
	int xBegin, xEind;
	int hoogte = 20;
	
	public StelselPijl(int x1, int x2)
	{
		xBegin = x1;
		xEind = x2;
		pijlCanvas = Canvas.createIfSupported();
		g = pijlCanvas.getContext2d();
		pijlCanvas.setCoordinateSpaceHeight(hoogte);
		pijlCanvas.setCoordinateSpaceWidth(Math.max(5, Math.abs(xBegin - xEind)));
		
		//setPixelSize(Math.max(5, Math.abs(xBegin - xEind)), hoogte);
	}
	
	public void paint()
	{
		g.setStrokeStyle(CssColor.make("gray"));
		g.setLineWidth(2.0);
		g.beginPath();
		g.stroke();
		if(xBegin < xEind)
		{
			g.moveTo(0, 0);
			g.lineTo(xEind - xBegin, hoogte);
			
		}
		else
		{	g.moveTo(xBegin - xEind, 0);
			g.lineTo(0, hoogte);
		}
		g.stroke();
		
	}
	
	public void zetBeginX(int x)
	{
		xBegin = x;
	}
	
	public Canvas getCanvas()
	{
		return pijlCanvas;
	}
	
	public void zetEindX(int x)
	{
		xEind = x;
		pijlCanvas.setCoordinateSpaceWidth(Math.max(5, Math.abs(xBegin - xEind)));
		paint();
	}

}
