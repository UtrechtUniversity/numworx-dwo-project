package nl.uu.fi.dwo.mobile.client.ui;



import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.text.Text;

public class ScoresObjectivesPanel extends LayoutPanel{

	private String[][] objectives;
	private String[] categorieString;
	private String scoreText = Text.constants.scoreKopLabel();
	private String categorieText = Text.constants.categorieLabel(); 

	private String[][] objectivesForDiagram;
	private String[] categorieStringForDiagram;
	private double[][] totaalScoresForDiagram;
	private double[][] totaalMaxForDiagram;
	private boolean[] categorieUitgeklapt;
	
	boolean pilot = false;
	int aantalDiagrammen;
	
	int straal = 100;
	//int marge = 5;
	int[] mpX;
	int[] mpY;
	//tbv nieuwe weergave:
	int tekstKolomBreedte, marge, regelHoogte, indent;
	
	double[][] totaalScoreObjectives;
	double[][] totaalMaxObjectives;
	double[] categorieScoreObjectives;
	double[] categorieMaxObjectives;
	double[][] scoresPercObjectives;
	double[] categorieScoresPercObjectives;
	int[] totaalMax;
	double[][] hoek, cumHoek, labelHoek; 
	//double[][] hoekGraden, cumHoekGraden;
	int[][] eindPuntX, eindPuntY, labelEindPuntX, labelEindPuntY;
	int[][] straalRij;
	
	TextArea[][] objectivesTextAreas;
	Label[] categorieLabels;
	
	Canvas canvas;
	Context2d ctx;
	String fontString = "12px Arial";
	String boldFontString = "bold 12px Arial";
//	Font theFont;
//	FontMetrics theFM;
//	Font theBoldFont;
//	FontMetrics theBoldFM;
	
	CssColor[][] kleurRij;
	CssColor[] categorieKleurRij;
	
	private boolean kleurNeutraal;
	private CssColor cssColorNeutraal = CssColor.make(202, 222, 255);
	
	public ScoresObjectivesPanel(HashMap<String, Object> map, boolean pilot)
	{
		this.pilot = pilot;
		canvas = Canvas.createIfSupported();
		ctx = canvas.getContext2d();
		//setLayout(null);
		
//		theFont = new Font("Dialog", Font.PLAIN, 12);
//		theFM = getFontMetrics(theFont);
//		theBoldFont = new Font("Dialog", Font.BOLD, 12);
//		theBoldFM = getFontMetrics(theBoldFont);
		
		ObjectMap h = JSONUtilities.wrapMap(map);
		ObjectList objectivesList = h.getObjectList("objectives");
		objectives = new String[objectivesList.size()][];
		for(int i = 0; i < objectivesList.size(); i++)
			objectives[i] = objectivesList.getStringArray(i);
		ObjectList totaalScoreList = h.getObjectList("totaalScoreObjectives");
		totaalScoreObjectives = new double[totaalScoreList.size()][];
		for(int i = 0; i < totaalScoreList.size(); i++)
			totaalScoreObjectives[i] = totaalScoreList.getDoubleArray(i);
		ObjectList totaalMaxList = h.getObjectList("totaalMaxObjectives");
		totaalMaxObjectives = new double[totaalMaxList.size()][];
		for(int i = 0; i < totaalMaxList.size(); i++)
			totaalMaxObjectives[i] = totaalMaxList.getDoubleArray(i);
		categorieString = h.getStringArray("categorieString");
		if(pilot)
		{
			categorieScoreObjectives = h.getDoubleArray("categorieScoreObjectives");
			categorieMaxObjectives = h.getDoubleArray("categorieMaxObjectives");
		}
		
//		objectives = (String[][]) map.get("objectives");
//		totaalScoreObjectives = (double[][]) map.get("totaalScoreObjectives");
//		totaalMaxObjectives = (int[][]) map.get("totaalMaxObjectives");
//		categorieString = (String[]) map.get("categorieString");
		
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
		totaalScoresForDiagram = new double[aantalDiagrammen][];
		totaalMaxForDiagram = new double[aantalDiagrammen][];
		categorieUitgeklapt = new boolean[aantalDiagrammen];
		
		aantalDiagrammen = 0;
		for(int j = 0; j < objectives.length; j++)
			if(totaalMax[j] != 0)
			{	objectivesForDiagram[aantalDiagrammen] = objectives[j];
				categorieStringForDiagram[aantalDiagrammen] = categorieString[j];
				totaalScoresForDiagram[aantalDiagrammen] = totaalScoreObjectives[j];
				totaalMaxForDiagram[aantalDiagrammen] = totaalMaxObjectives[j];
				totaalMax[aantalDiagrammen] = totaalMax[j];
				categorieUitgeklapt[aantalDiagrammen] = false;
				aantalDiagrammen++;
			}
		
		scoresPercObjectives = new double[aantalDiagrammen][];
		categorieScoresPercObjectives = new double[aantalDiagrammen];
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
			int somScoresPerc = 0;
			for(int k=0 ; k<objectivesForDiagram[j].length; k++)
		   	{	if(totaalMaxForDiagram[j][k]==0) scoresPercObjectives[j][k] = 0;
		   		else scoresPercObjectives[j][k] = Math.round(100.0*totaalScoresForDiagram[j][k]/totaalMaxForDiagram[j][k]);
		   		somScoresPerc += scoresPercObjectives[j][k];
		   	}
			if(pilot)
				categorieScoresPercObjectives[j] = Math.round(100.0 * categorieScoreObjectives[j]/categorieMaxObjectives[j]);
			else
				categorieScoresPercObjectives[j] = Math.round(somScoresPerc/objectivesForDiagram[j].length);
		}
		
		
		int canvasWidth = 1200;
		int canvasHeight = 700;
		if(pilot)
		{
			canvasWidth = 500;
			canvasHeight = 700; // TODO: zinvollere breedte en hoogte geven
		}
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
		
		
		
		//tbv nieuwe weergave
		if(pilot)
		{
			categorieLabels = new Label[objectivesForDiagram.length];
			marge = 10;
			
			
			//int categorieX = marge + 5;
			//int categorieY = regelHoogte + 5;
			for(int j = 0; j < objectivesForDiagram.length; j++)
			{
				//categorieY += regelHoogte;
				categorieLabels[j] = new Label(categorieStringForDiagram[j]);
				categorieLabels[j].getElement().getStyle().setFontSize(12, Unit.PX);
				
				//categorieLabels[j].setFont(theFont);
				//maatzetting pas regelen in paint.
				//this.setWidgetLeftWidth(categorieLabels[j], categorieX, Unit.PX, tekstKolomBreedte, Unit.PX);
				//this.setWidgetTopHeight(categorieLabels[j], categorieY, Unit.PX, regelHoogte, Unit.PX);
				add(categorieLabels[j]);
				
				categorieLabels[j].addClickHandler(new ClickHandler(){
					
					@Override
					public void onClick(ClickEvent e) 
					{
						e.stopPropagation();
						e.preventDefault();
						for(int j = 0; j < objectivesForDiagram.length; j++)
						{
							if(e.getSource() == categorieLabels[j])
							{
								categorieUitgeklapt[j] = !categorieUitgeklapt[j];
								paint();
								break;
							}
						}
					}
				});
				
			}
		}
		// einde nieuwe weergave
		
		else
		{
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
		}
	
//        	scoresObjectivesPanel.setBounds(0, 0, 400 * aantalDiagrammen, 350);
//        else 
//        	scoresObjectivesPanel.setBounds(0, 0, 1200, 700);
		
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
	
	public void zetKleurNeutraal()
	{
		kleurNeutraal = true;
		paint();
	}
	
	
	
	public void zetKleuren()
	{	categorieKleurRij = new CssColor[objectivesForDiagram.length];
		kleurRij = new CssColor[aantalDiagrammen][];
		for(int j = 0; j < aantalDiagrammen; j++)
		{	int red = 255; 
			int green = 255;
			if(categorieScoresPercObjectives[j] < 50)
				green = (int) (green * categorieScoresPercObjectives[j] / 50);
			else 
				red -= (int) (red * (categorieScoresPercObjectives[j] - 50)/50);
			categorieKleurRij[j] = CssColor.make(red, green, 0);
		
			kleurRij[j] = new CssColor[objectivesForDiagram[j].length];
			for(int i = 0; i < objectivesForDiagram[j].length; i++)
			{	red = 255; 
				green = 255;
				if(scoresPercObjectives[j][i] < 50)
					green = (int) (green * scoresPercObjectives[j][i] / 50);
				else 
					red -= (int) (red * (scoresPercObjectives[j][i] - 50)/50);
				kleurRij[j][i] = CssColor.make(red, green, 0);
			}	
		}
	}
	
	public void paintPilot()
	{
		ctx.setFillStyle("white");
		ctx.fillRect(0, 0, canvas.getOffsetWidth(), canvas.getOffsetHeight());
		zetKleuren();
		ctx.setFont(fontString);
		ctx.setFillStyle("black");
		ctx.setStrokeStyle("black");
		int scoreBreedte = (int) ctx.measureText(scoreText).getWidth() + marge;
		
		//iets met ctx doen zodat ik mooie scherpe lijnen krijg... maar setLineWidth alleen helpt niet genoeg; ergens heb ik hiervoor iets slims gedaan. 
		
		regelHoogte = 15 + marge; //TODO nog iets zinvollers van 15 maken; meten mbv canvas?
		tekstKolomBreedte = 0;
		indent = 20;
		for(int j = 0; j < objectivesForDiagram.length; j++)
		{
			tekstKolomBreedte = Math.max((int) ctx.measureText(categorieStringForDiagram[j]).getWidth() + marge, tekstKolomBreedte);
			for(int i = 0; i < objectivesForDiagram[j].length; i++ )
			{
				tekstKolomBreedte = Math.max((int) ctx.measureText(objectivesForDiagram[j][i]).getWidth() + marge + indent, tekstKolomBreedte);
			}
			
		}
		
		if(aantalDiagrammen == 1)
		{
			int labelX = marge + 5;
			int scoreX = labelX + tekstKolomBreedte;
			int kleurX = marge + tekstKolomBreedte;
			int kleurY = marge;
			int tekstY = regelHoogte + 2;
			
			ctx.setFont(boldFontString);
			ctx.fillText(categorieStringForDiagram[0], labelX, tekstY);
			ctx.fillText(scoreText, scoreX, tekstY);
			ctx.setFont(fontString);
			
			for(int i = 0; i < objectivesForDiagram[0].length; i++)
			{
				tekstY += regelHoogte;
				kleurY += regelHoogte;
				ctx.setFillStyle(kleurRij[0][i]);
				ctx.fillRect(kleurX, kleurY, scoreBreedte, regelHoogte);
				ctx.setFillStyle("black");
				ctx.fillText(objectivesForDiagram[0][i], labelX, tekstY);
				ctx.fillText((int) scoresPercObjectives[0][i]+"%", scoreX, tekstY);
			}
			int lijnHoogte = marge;
			for(int i = 0; i < objectivesForDiagram[0].length + 2; i++)
			{	ctx.beginPath();
				ctx.moveTo(marge, lijnHoogte);
				ctx.lineTo(tekstKolomBreedte + scoreBreedte + marge, lijnHoogte);
				ctx.closePath();
				ctx.stroke();
				lijnHoogte += regelHoogte;
			}
			lijnHoogte -= regelHoogte;
			ctx.beginPath();
			ctx.moveTo(marge, marge);
			ctx.lineTo(marge, lijnHoogte);
			ctx.moveTo(marge + tekstKolomBreedte, marge);
			ctx.lineTo(marge + tekstKolomBreedte, lijnHoogte);
			ctx.moveTo(marge + tekstKolomBreedte + scoreBreedte, marge);
			ctx.lineTo(marge + tekstKolomBreedte + scoreBreedte, lijnHoogte);
			ctx.closePath();
			ctx.stroke();
			schrijfPilotText(lijnHoogte + regelHoogte);
		}
		else
		{
			int categorieX = marge + 5;
			int labelX = marge + indent + 5;
			int scoreX = categorieX + tekstKolomBreedte;
			int kleurX = marge + tekstKolomBreedte;
			int lijnHoogte = marge;
			
			//int tekstY = regelHoogte + 2;
			int tekstVerschil = regelHoogte - marge + 2;
			int tussenRuimte = 5;
			ctx.setFont(boldFontString);
			ctx.fillText(categorieText, categorieX, lijnHoogte + tekstVerschil);
			ctx.fillText(scoreText, scoreX, lijnHoogte + tekstVerschil);
			ctx.setFont(fontString);
			tekstVerschil += 3;
			ctx.beginPath();
			ctx.moveTo(marge, lijnHoogte);
			ctx.lineTo(tekstKolomBreedte + scoreBreedte + marge, lijnHoogte);
			ctx.closePath();
			ctx.stroke();
			lijnHoogte += regelHoogte;			
			
			for(int j = 0; j < objectivesForDiagram.length; j++)
			{
				//dubbel lijntje boven elke categorie.
				ctx.beginPath();
				ctx.moveTo(marge, lijnHoogte);
				ctx.lineTo(tekstKolomBreedte + scoreBreedte + marge, lijnHoogte);
				ctx.closePath();
				ctx.stroke();
				lijnHoogte += tussenRuimte;
				ctx.setFillStyle(categorieKleurRij[j]);
				ctx.fillRect(kleurX, lijnHoogte, scoreBreedte, regelHoogte);
				ctx.beginPath();
				ctx.moveTo(marge, lijnHoogte);
				ctx.lineTo(tekstKolomBreedte + scoreBreedte + marge, lijnHoogte);
				ctx.closePath();
				ctx.stroke();
				
				//categorienaam en score invullen
				tekstVerschil += 12 - regelHoogte;
				ctx.setFillStyle("black");
				this.setWidgetLeftWidth(categorieLabels[j], categorieX, Unit.PX, tekstKolomBreedte, Unit.PX);
				this.setWidgetTopHeight(categorieLabels[j], lijnHoogte + tekstVerschil, Unit.PX, regelHoogte, Unit.PX);
				
				tekstVerschil += regelHoogte - 15;  //label-location en drawString hebben verschillende y nodig.
				ctx.fillText((int) categorieScoresPercObjectives[j]+"%", scoreX, lijnHoogte + tekstVerschil);
			
				if(categorieUitgeklapt[j])
				{
					for(int i = 0; i < objectivesForDiagram[j].length; i++)
					{
						lijnHoogte += regelHoogte;
						ctx.setFillStyle(kleurRij[j][i]);
						ctx.fillRect(kleurX, lijnHoogte, scoreBreedte, regelHoogte);
						ctx.setFillStyle("black");
						ctx.fillText(objectivesForDiagram[j][i], labelX, lijnHoogte + tekstVerschil);
						ctx.fillText((int) scoresPercObjectives[j][i]+"%", scoreX, lijnHoogte + tekstVerschil);
						ctx.beginPath();
						ctx.moveTo(marge, lijnHoogte);
						ctx.lineTo(tekstKolomBreedte + scoreBreedte + marge, lijnHoogte);
						ctx.closePath();
						ctx.stroke();
					}
				}
				tekstVerschil += 3;
				lijnHoogte += regelHoogte;
			}
			ctx.beginPath();
			ctx.moveTo(marge, lijnHoogte);
			ctx.lineTo(tekstKolomBreedte + scoreBreedte + marge, lijnHoogte);
			
			//tabellijnen tekenen
			ctx.moveTo(marge, marge);
			ctx.lineTo(marge, lijnHoogte);
			ctx.moveTo(marge + tekstKolomBreedte, marge);
			ctx.lineTo(marge + tekstKolomBreedte, lijnHoogte);
			ctx.moveTo(marge + tekstKolomBreedte + scoreBreedte, marge);
			ctx.lineTo(marge + tekstKolomBreedte + scoreBreedte, lijnHoogte);
			
			ctx.closePath();
			ctx.stroke();
			schrijfPilotText(lijnHoogte + regelHoogte);
		}
		
	}
	
	public void schrijfPilotText(int y)
	{
		
		ctx.fillText(Text.constants.pilotTextPart1(), marge, y);
		y += regelHoogte - marge;
		ctx.fillText(Text.constants.pilotTextPart2(), marge, y);
		y += regelHoogte - marge;
		ctx.fillText(Text.constants.pilotTextPart3(), marge, y);
		y += regelHoogte - marge;
		ctx.fillText(Text.constants.pilotTextPart4(), marge, y);
	}
	
	public void paint()
	{	

		if(pilot)
		{	paintPilot();
			return;
		}
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
		{	if(!kleurNeutraal)
				ctx.setFillStyle(kleurRij[j][0]);
			else
				ctx.setFillStyle(cssColorNeutraal);
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
				if(!kleurNeutraal)
					ctx.setFillStyle(kleurRij[j][i]);
				else
					ctx.setFillStyle(cssColorNeutraal);
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
