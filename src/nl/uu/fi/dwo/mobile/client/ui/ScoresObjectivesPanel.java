package nl.uu.fi.dwo.mobile.client.ui;


import java.util.HashMap;
import java.util.Map;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class ScoresObjectivesPanel extends LayoutPanel{

	private String[][] objectives;
	private String[] categorieString;
	
	private String[][] objectivesForDiagram;
	private String[] categorieStringForDiagram;
	private int[][] totaalScoresForDiagram;
	private int[][] totaalMaxForDiagram;
	
	int aantalDiagrammen;
	
	int straal = 100;
	int marge = 5;
	int[] mpX;
	int[] mpY;
	
	int[][] totaalScoreObjectives;
	int[][] totaalMaxObjectives;
	double[][] scoresPercObjectives;
	int[] totaalMax;
	double[][] hoek, cumHoek, labelHoek; 
	//double[][] hoekGraden, cumHoekGraden;
	int[][] eindPuntX, eindPuntY, labelEindPuntX, labelEindPuntY;
	int[][] straalRij;
	
	TextArea[][] objectivesTextAreas;
	
	Canvas canvas;
	Context2d ctx;
//	Font theFont;
//	FontMetrics theFM;
//	Font theBoldFont;
//	FontMetrics theBoldFM;
	
	CssColor[][] kleurRij;
	
	public ScoresObjectivesPanel(HashMap<String, Object> map)
	{
		canvas = Canvas.createIfSupported();
		ctx = canvas.getContext2d();
		//setLayout(null);
		
//		theFont = new Font("Dialog", Font.PLAIN, 12);
//		theFM = getFontMetrics(theFont);
//		theBoldFont = new Font("Dialog", Font.BOLD, 12);
//		theBoldFM = getFontMetrics(theBoldFont);
		
		objectives = (String[][]) map.get("objectives");
		totaalScoreObjectives = (int[][]) map.get("totaalScoreObjectives");
		totaalMaxObjectives = (int[][]) map.get("totaalMaxObjectives");
		categorieString = (String[]) map.get("categorieString");
		
		/* Makkelijk voor testen (deel hierboven afschermen)
		objectives = new String[] {"Procenten/ verhoudingen", "Meten/ meetkunde", "Verbanden", "Getallen"};
		totaalScoreObjectives = new int[] {60, 40, 80, 10};;
		totaalMaxObjectives = new int[] {110, 70, 90, 15};
		*/
		
		totaalMax = new int[objectives.length];
		for(int j = 0; j<objectives.length; j++)
		{	totaalMax[j] = 0;
			for(int i = 0; i<objectives[j].length; i++)
				totaalMax[j] += totaalMaxObjectives[j][i];
		}	
		aantalDiagrammen = 0;
		for(int j = 0; j < totaalMax.length; j++)
		{	
			if(totaalMax[j] != 0)
				aantalDiagrammen++;
		}
		
		objectivesForDiagram = new String[aantalDiagrammen][];
		categorieStringForDiagram = new String[aantalDiagrammen];
		totaalScoresForDiagram = new int[aantalDiagrammen][];
		totaalMaxForDiagram = new int[aantalDiagrammen][];
		
		aantalDiagrammen = 0;
		for(int j = 0; j < objectives.length; j++)
			if(totaalMax[j] != 0)
			{	objectivesForDiagram[aantalDiagrammen] = objectives[j];
				categorieStringForDiagram[aantalDiagrammen] = categorieString[j];
				totaalScoresForDiagram[aantalDiagrammen] = totaalScoreObjectives[j];
				totaalMaxForDiagram[aantalDiagrammen] = totaalMaxObjectives[j];
				totaalMax[aantalDiagrammen] = totaalMax[j];
				aantalDiagrammen++;
			}
		
		scoresPercObjectives = new double[aantalDiagrammen][];
		hoek = new double[aantalDiagrammen][];
		cumHoek = new double[aantalDiagrammen][];
		//hoekGraden = new double[aantalDiagrammen][];
		//cumHoekGraden = new double[aantalDiagrammen][];
		labelHoek = new double[aantalDiagrammen][];
		
		eindPuntX = new int[aantalDiagrammen][];
		eindPuntY = new int[aantalDiagrammen][];
		labelEindPuntX = new int[aantalDiagrammen][];
		labelEindPuntY = new int[aantalDiagrammen][];
		mpX = new int[aantalDiagrammen];
		mpY = new int[aantalDiagrammen];
		
		for(int i = 0; i<aantalDiagrammen; i++)
		{
			scoresPercObjectives[i] = new double[objectivesForDiagram[i].length];
			hoek[i] = new double[objectivesForDiagram[i].length];
			cumHoek[i] = new double[objectivesForDiagram[i].length];
			//hoekGraden[i] = new double[objectivesForDiagram[i].length];
			//cumHoekGraden[i] = new double[objectivesForDiagram[i].length];
			labelHoek[i] = new double[objectivesForDiagram[i].length];
			
			eindPuntX[i] = new int[objectivesForDiagram[i].length];
			eindPuntY[i] = new int[objectivesForDiagram[i].length];
			labelEindPuntX[i] = new int[objectivesForDiagram[i].length];
			labelEindPuntY[i] = new int[objectivesForDiagram[i].length];
			if(i<3)
			{	mpX[i] = 200 + i*400;
				mpY[i] = 175;
			}
			else 
			{	mpX[i] = 200 + (i-3)*400;
				mpY[i] = 525;
			}
			
		}
		
		for(int j = 0; j < aantalDiagrammen; j++)
		{	for(int i = 0; i<objectivesForDiagram[j].length; i++)
			{	hoek[j][i] = totaalMaxForDiagram[j][i] * 2 * Math.PI / totaalMax[j];
				//hoekGraden[j][i] = totaalMaxForDiagram[j][i] * 360 / totaalMax[j];
			}
		
			cumHoek[j][0] = hoek[j][0];
			//cumHoekGraden[j][0] = hoekGraden[j][0];
			for(int i = 1; i<objectivesForDiagram[j].length; i++)
			{	cumHoek[j][i] = cumHoek[j][i-1] + hoek[j][i];
				//cumHoekGraden[j][i] = (cumHoekGraden[j][i-1] + hoekGraden[j][i]);
			}
			
			for(int i = 0; i<objectivesForDiagram[j].length; i++)
			{	
				eindPuntX[j][i] = (int) Math.round(straal * Math.sin(cumHoek[j][i]) + mpX[j]);
				eindPuntY[j][i] = (int) Math.round(- straal * Math.cos(cumHoek[j][i]) + mpY[j]);
			}
			for(int i = 0; i<objectivesForDiagram[j].length; i++)
			{
				labelHoek[j][i] = cumHoek[j][i] - hoek[j][i]/2;
				labelEindPuntX[j][i] = (int) ((straal + marge) * Math.sin(labelHoek[j][i]) + mpX[j]);
				labelEindPuntY[j][i] = (int) (- (straal + marge) * Math.cos(labelHoek[j][i]) + mpY[j]);
			}
			for(int k=0 ; k<objectivesForDiagram[j].length; k++)
		   	{	if(totaalMaxForDiagram[j][k]==0) scoresPercObjectives[j][k] = 0;
		   		else scoresPercObjectives[j][k] = Math.round(100.0*totaalScoresForDiagram[j][k]/totaalMaxForDiagram[j][k]);
		   	}
		}
		
		straalRij = new int[aantalDiagrammen][];
		objectivesTextAreas = new TextArea[aantalDiagrammen][];
		
		for(int j = 0; j<aantalDiagrammen; j++)
		{	straalRij[j] = new int[objectivesForDiagram[j].length];
			objectivesTextAreas[j] = new TextArea[objectivesForDiagram[j].length];
			for(int i = 0; i<objectivesForDiagram[j].length; i++)
				straalRij[j][i] = (int) Math.round(straal * scoresPercObjectives[j][i]/100);
			for(int i = 0; i < objectivesForDiagram[j].length; i++)
			{	objectivesTextAreas[j][i] = new TextArea();
				objectivesTextAreas[j][i].setWidth(breedteLabel(i,j) + "px");
				objectivesTextAreas[j][i].getElement().getStyle().setFontSize(12, Unit.PX);
				//objectivesTextAreas[j][i].setReadOnly(true);
				objectivesTextAreas[j][i].getElement().getStyle().setBorderStyle(BorderStyle.NONE);
				objectivesTextAreas[j][i].getElement().getStyle().setMargin(0, Unit.PX);
				objectivesTextAreas[j][i].getElement().getStyle().setPadding(0, Unit.PX);
							
				double links = 0; 
				double top = 0;
				ctx.setFont("12 px Arial");
				
				if(labelEindPuntX[j][i] >= mpX[j]) //label staat 'rechts' van cirkel
				{	if(labelEindPuntY[j][i] > mpY[j] + straal)
					{	links = labelEindPuntX[j][i] - breedteLabel(i,j)/2;
						top = labelEindPuntY[j][i];
					}
					else if(labelEindPuntY[j][i] >= mpY[j])
					{
						links = labelEindPuntX[j][i];
						top = labelEindPuntY[j][i];
					}
					else if(labelEindPuntY[j][i] <= mpY[j] - straal)
					{
						links = labelEindPuntX[j][i] - breedteLabel(i,j)/2;
						top = labelEindPuntY[j][i] - hoogteLabel(i,j);
					}
					else
					{
						links = labelEindPuntX[j][i];
						top = labelEindPuntY[j][i] - hoogteLabel(i, j);
					}
				}
				else //label staat 'links' van cirkel
				{	if(labelEindPuntY[j][i] > mpY[j] + straal)
					{
						links = labelEindPuntX[j][i] - breedteLabel(i,j)/2;
						top = labelEindPuntY[j][i];
					}
					else if(labelEindPuntY[j][i] >= mpY[j])
					{
						links = labelEindPuntX[j][i] - breedteLabel(i,j);
						top = labelEindPuntY[j][i];
					}
					else if(labelEindPuntY[j][i] <= mpY[j] - straal)
					{
						links = labelEindPuntX[j][i] - breedteLabel(i,j)/2;
						top = labelEindPuntY[j][i] - hoogteLabel(i,j);
					}
					else
					{
						links = labelEindPuntX[j][i] - breedteLabel(i,j);
						top =  labelEindPuntY[j][i] - hoogteLabel(i,j);
					}
				}	
				objectivesTextAreas[j][i].setText(objectivesForDiagram[j][i] + ": " + (int) scoresPercObjectives[j][i]+"%");
				this.add(objectivesTextAreas[j][i]);
				setWidgetLeftWidth(objectivesTextAreas[j][i], links, Unit.PX, breedteLabel(i, j), Unit.PX);
				setWidgetTopHeight(objectivesTextAreas[j][i], top, Unit.PX, hoogteLabel(i, j), Unit.PX);
			}
		}
		
		
//        	scoresObjectivesPanel.setBounds(0, 0, 400 * aantalDiagrammen, 350);
//        else 
//        	scoresObjectivesPanel.setBounds(0, 0, 1200, 700);
		int canvasWidth = 1200;
		int canvasHeight = 700;
		if(aantalDiagrammen < 4)
		{	
			canvasWidth = 400 * aantalDiagrammen;
			canvasHeight = 350;
		}
		//canvas.setWidth(width + "px");
		//canvas.setHeight(height + "px");
		canvas.setCoordinateSpaceWidth(canvasWidth);
		canvas.setCoordinateSpaceHeight(canvasHeight);
			
		this.setWidth(canvasWidth + "px");
		this.setHeight(canvasHeight + "px");
		this.add(canvas);
		setWidgetLeftWidth(canvas, 0, Unit.PX, canvasWidth, Unit.PX);
		setWidgetTopHeight(canvas, 0, Unit.PX, canvasHeight, Unit.PX);
		paint();
	}
	
	public double breedteLabel(int i, int j)
	{
		double breedte = Math.min(mpX[0] - straal - marge, 
			ctx.measureText(objectivesForDiagram[j][i] + ": " + (int) scoresPercObjectives[j][i]+"%").getWidth() + marge);
		return breedte;	
	}
	
	public double hoogteLabel(int i, int j)
	{
		double hoogte = 0;
		if(ctx.measureText(objectivesForDiagram[j][i] + ": " + (int) scoresPercObjectives[j][i] + "%").getWidth() > mpX[0] - straal - marge)
			hoogte = 35;
		else 
			hoogte = 17;			
		
		return hoogte;
	}
	
	public void zetKleuren()
	{	kleurRij = new CssColor[aantalDiagrammen][];
		for(int j = 0; j < aantalDiagrammen; j++)
		{	kleurRij[j] = new CssColor[objectivesForDiagram[j].length];
			for(int i = 0; i < objectivesForDiagram[j].length; i++)
			{	int red = 255; 
				int green = 255;
				if(scoresPercObjectives[j][i] < 50)
					green = (int) (green * scoresPercObjectives[j][i] / 50);
				else 
					red -= (int) (red * (scoresPercObjectives[j][i] - 50)/50);
				kleurRij[j][i] = CssColor.make(red, green, 0);
			}	
		}
	}
	
	public void paint()
	{	
		//super.paintComponent(g);
		ctx.setFillStyle("white");
		ctx.fillRect(0, 0, canvas.getOffsetWidth(), canvas.getOffsetHeight());
		
		//Arc2D.Double arc;
		//Ellipse2D.Double ellipse;
		//Line2D.Double line;
		
//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//		        RenderingHints.VALUE_ANTIALIAS_ON);	
		
		zetKleuren();
		ctx.setStrokeStyle("black");
		for(int j = 0; j < aantalDiagrammen; j++)
		{	ctx.setFillStyle(kleurRij[j][0]);
			ctx.beginPath();
			ctx.moveTo(mpX[j], mpY[j]);
			//ctx.lineTo(mpX[j], mpY[j] - straalRij[j][0]);
			ctx.arc(mpX[j], mpY[j], straalRij[j][0], -Math.PI / 2, -Math.PI / 2 + hoek[j][0]);
			ctx.closePath();
			ctx.fill();
			ctx.stroke();
//			arc = new Arc2D.Double(mpX[j] - straalRij[j][0], mpY[j] - straalRij[j][0], 2 * straalRij[j][0], 2 * straalRij[j][0], 90, (int) - hoekGraden[j][0],Arc2D.PIE);
//			g2.setColor(kleurRij[j][0]);
//			g2.fill(arc);
//			g2.setColor(Color.BLACK);
//			g2.draw(arc);
			
			for(int i = 1; i < objectivesForDiagram[j].length; i++)
			{
				ctx.setFillStyle(kleurRij[j][i]);
				ctx.beginPath();
				ctx.moveTo(mpX[j], mpY[j]);
//				ctx.lineTo(mpX[j], mpY[j] - straalRij[j][i]);
				ctx.arc(mpX[j], mpY[j], straalRij[j][i], - Math.PI / 2 + cumHoek[j][i-1], - Math.PI / 2 + cumHoek[j][i]);
				//ctx.lineTo(mpX[j], mpY[j]);
				ctx.closePath();
				ctx.fill();
				ctx.stroke();
//				arc = new Arc2D.Double(mpX[j] - straalRij[j][i], mpY[j] - straalRij[j][i], 2 * straalRij[j][i], 2 * straalRij[j][i], 90 - cumHoekGraden[j][i-1], (int) - hoekGraden[j][i], Arc2D.PIE);
//				g2.setColor(kleurRij[j][i]);
//				g2.fill(arc);
//				g2.setColor(Color.BLACK);
//				g2.draw(arc);
				
			}
			ctx.beginPath();
			ctx.arc(mpX[j], mpY[j], straal, 0, 360);
			ctx.closePath();
			ctx.stroke(); //nodig?
//			ellipse = new Ellipse2D.Double(mpX[j] - straal, mpY[j]-straal, 2 * straal, 2 * straal);
//			g2.setColor(Color.BLACK);
//			g2.draw(ellipse);
			for(int i = 0; i < objectivesForDiagram[j].length; i++)
			{	ctx.beginPath();
				ctx.moveTo(mpX[j], mpY[j]);
				ctx.lineTo(eindPuntX[j][i], eindPuntY[j][i]);
				ctx.closePath();
				ctx.stroke();
				
				//line = new Line2D.Double(mpX[j], mpY[j], eindPuntX[j][i], eindPuntY[j][i]);
				//g2.draw(line);
			}
			ctx.setFont("bold 12px Arial");
			ctx.setFillStyle("black");
			double stringWidth = ctx.measureText(categorieStringForDiagram[j]).getWidth();
			//g.setFont(theBoldFont);
			if(j<3)
				ctx.fillText(categorieStringForDiagram[j], mpX[j] - stringWidth/2, 20);
			else
				ctx.fillText(categorieStringForDiagram[j], mpX[j] - stringWidth/2, 370);
		}
	}	
	
	
}
