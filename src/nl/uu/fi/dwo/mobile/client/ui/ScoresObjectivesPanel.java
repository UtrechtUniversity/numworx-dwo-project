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
	private String[] categoryString;
	private String scoreText = Text.constants.scoreKopLabel();
	private String categoryText = Text.constants.categorieLabel(); 

	private String[][] objectivesForDiagram;
	private String[] categoryStringForDiagram;
	private double[][] totalScoresForDiagram;
	private double[][] totalMaxForDiagram;
	private boolean[] categoryFoldedOut;
	
	boolean pilot = false;
	int numberOfDiagrams;
	
	int straal = 100;
	//int marge = 5;
	int[] mpX;
	int[] mpY;
	//tbv nieuwe weergave:
	int textColumnWidth, margin, lineHeight, indent;
	
	double[][] totalScoreObjectives;
	double[][] totalMaxObjectives;
	double[] categoryScoreObjectives;
	double[] categoryMaxObjectives;
	double[][] scoresPercObjectives;
	double[] categoryScoresPercObjectives;
	int[] totalMax;
	double[][] angle, cumAngle, labelAngle; 
	//double[][] hoekGraden, cumHoekGraden;
	int[][] endPointX, endPointY, labelEndPointX, labelEndPointY;
	int[][] straalRij;
	
	TextArea[][] objectivesTextAreas;
	Label[] categoryLabels;
	
	Canvas canvas;
	Context2d ctx;
	String fontString = "12px Arial";
	String boldFontString = "bold 12px Arial";
//	Font theFont;
//	FontMetrics theFM;
//	Font theBoldFont;
//	FontMetrics theBoldFM;
	
	CssColor[][] colorArray;
	CssColor[] categoryColorArray;
	
	private boolean colorNeutral;
	private CssColor cssColorNeutral = CssColor.make(202, 222, 255);
	
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
		totalScoreObjectives = new double[totaalScoreList.size()][];
		for(int i = 0; i < totaalScoreList.size(); i++)
			totalScoreObjectives[i] = totaalScoreList.getDoubleArray(i);
		ObjectList totaalMaxList = h.getObjectList("totaalMaxObjectives");
		totalMaxObjectives = new double[totaalMaxList.size()][];
		for(int i = 0; i < totaalMaxList.size(); i++)
			totalMaxObjectives[i] = totaalMaxList.getDoubleArray(i);
		categoryString = h.getStringArray("categorieString");
		if(pilot)
		{
			categoryScoreObjectives = h.getDoubleArray("categorieScoreObjectives");
			categoryMaxObjectives = h.getDoubleArray("categorieMaxObjectives");
		}
		
//		objectives = (String[][]) map.get("objectives");
//		totaalScoreObjectives = (double[][]) map.get("totaalScoreObjectives");
//		totaalMaxObjectives = (int[][]) map.get("totaalMaxObjectives");
//		categorieString = (String[]) map.get("categorieString");
		
		totalMax = new int[objectives.length];
		for(int j = 0; j<objectives.length; j++)
		{	totalMax[j] = 0;
			for(int i = 0; i<objectives[j].length; i++)
				totalMax[j] += totalMaxObjectives[j][i];
		}	
		numberOfDiagrams = 0;
		for(int j = 0; j < totalMax.length; j++)
		{	
			if(totalMax[j] != 0)
				numberOfDiagrams++;
		}
		
		objectivesForDiagram = new String[numberOfDiagrams][];
		categoryStringForDiagram = new String[numberOfDiagrams];
		totalScoresForDiagram = new double[numberOfDiagrams][];
		totalMaxForDiagram = new double[numberOfDiagrams][];
		categoryFoldedOut = new boolean[numberOfDiagrams];
		
		numberOfDiagrams = 0;
		for(int j = 0; j < objectives.length; j++)
			if(totalMax[j] != 0)
			{	objectivesForDiagram[numberOfDiagrams] = objectives[j];
				categoryStringForDiagram[numberOfDiagrams] = categoryString[j];
				totalScoresForDiagram[numberOfDiagrams] = totalScoreObjectives[j];
				totalMaxForDiagram[numberOfDiagrams] = totalMaxObjectives[j];
				totalMax[numberOfDiagrams] = totalMax[j];
				categoryFoldedOut[numberOfDiagrams] = false;
				numberOfDiagrams++;
			}
		
		scoresPercObjectives = new double[numberOfDiagrams][];
		categoryScoresPercObjectives = new double[numberOfDiagrams];
		angle = new double[numberOfDiagrams][];
		cumAngle = new double[numberOfDiagrams][];
		//hoekGraden = new double[aantalDiagrammen][];
		//cumHoekGraden = new double[aantalDiagrammen][];
		labelAngle = new double[numberOfDiagrams][];
		
		endPointX = new int[numberOfDiagrams][];
		endPointY = new int[numberOfDiagrams][];
		labelEndPointX = new int[numberOfDiagrams][];
		labelEndPointY = new int[numberOfDiagrams][];
		mpX = new int[numberOfDiagrams];
		mpY = new int[numberOfDiagrams];
		
		for(int i = 0; i<numberOfDiagrams; i++)
		{
			scoresPercObjectives[i] = new double[objectivesForDiagram[i].length];
			angle[i] = new double[objectivesForDiagram[i].length];
			cumAngle[i] = new double[objectivesForDiagram[i].length];
			//hoekGraden[i] = new double[objectivesForDiagram[i].length];
			//cumHoekGraden[i] = new double[objectivesForDiagram[i].length];
			labelAngle[i] = new double[objectivesForDiagram[i].length];
			
			endPointX[i] = new int[objectivesForDiagram[i].length];
			endPointY[i] = new int[objectivesForDiagram[i].length];
			labelEndPointX[i] = new int[objectivesForDiagram[i].length];
			labelEndPointY[i] = new int[objectivesForDiagram[i].length];
			if(i<3)
			{	mpX[i] = 200 + i*400;
				mpY[i] = 175;
			}
			else 
			{	mpX[i] = 200 + (i-3)*400;
				mpY[i] = 525;
			}
			
		}
		
		for(int j = 0; j < numberOfDiagrams; j++)
		{	for(int i = 0; i<objectivesForDiagram[j].length; i++)
			{	angle[j][i] = totalMaxForDiagram[j][i] * 2 * Math.PI / totalMax[j];
				//hoekGraden[j][i] = totaalMaxForDiagram[j][i] * 360 / totaalMax[j];
			}
		
			cumAngle[j][0] = angle[j][0];
			//cumHoekGraden[j][0] = hoekGraden[j][0];
			for(int i = 1; i<objectivesForDiagram[j].length; i++)
			{	cumAngle[j][i] = cumAngle[j][i-1] + angle[j][i];
				//cumHoekGraden[j][i] = (cumHoekGraden[j][i-1] + hoekGraden[j][i]);
			}
			
			for(int i = 0; i<objectivesForDiagram[j].length; i++)
			{	
				endPointX[j][i] = (int) Math.round(straal * Math.sin(cumAngle[j][i]) + mpX[j]);
				endPointY[j][i] = (int) Math.round(- straal * Math.cos(cumAngle[j][i]) + mpY[j]);
			}
			for(int i = 0; i<objectivesForDiagram[j].length; i++)
			{
				labelAngle[j][i] = cumAngle[j][i] - angle[j][i]/2;
				labelEndPointX[j][i] = (int) ((straal + margin) * Math.sin(labelAngle[j][i]) + mpX[j]);
				labelEndPointY[j][i] = (int) (- (straal + margin) * Math.cos(labelAngle[j][i]) + mpY[j]);
			}
			int somScoresPerc = 0;
			for(int k=0 ; k<objectivesForDiagram[j].length; k++)
		   	{	if(totalMaxForDiagram[j][k]==0) scoresPercObjectives[j][k] = 0;
		   		else scoresPercObjectives[j][k] = Math.round(100.0*totalScoresForDiagram[j][k]/totalMaxForDiagram[j][k]);
		   		somScoresPerc += scoresPercObjectives[j][k];
		   	}
			if(pilot)
				categoryScoresPercObjectives[j] = Math.round(100.0 * categoryScoreObjectives[j]/categoryMaxObjectives[j]);
			else
				categoryScoresPercObjectives[j] = Math.round(somScoresPerc/objectivesForDiagram[j].length);
		}
		
		
		int canvasWidth = 1200;
		int canvasHeight = 700;
		if(pilot)
		{
			canvasWidth = 500;
			canvasHeight = 700; // TODO: zinvollere breedte en hoogte geven
		}
		if(numberOfDiagrams < 4)
		{	
			canvasWidth = 400 * numberOfDiagrams;
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
			categoryLabels = new Label[objectivesForDiagram.length];
			margin = 10;
			
			
			//int categorieX = marge + 5;
			//int categorieY = regelHoogte + 5;
			for(int j = 0; j < objectivesForDiagram.length; j++)
			{
				//categorieY += regelHoogte;
				categoryLabels[j] = new Label(categoryStringForDiagram[j]);
				categoryLabels[j].getElement().getStyle().setFontSize(12, Unit.PX);
				
				//categorieLabels[j].setFont(theFont);
				//maatzetting pas regelen in paint.
				//this.setWidgetLeftWidth(categorieLabels[j], categorieX, Unit.PX, tekstKolomBreedte, Unit.PX);
				//this.setWidgetTopHeight(categorieLabels[j], categorieY, Unit.PX, regelHoogte, Unit.PX);
				add(categoryLabels[j]);
				
				categoryLabels[j].addClickHandler(new ClickHandler(){
					
					@Override
					public void onClick(ClickEvent e) 
					{
						e.stopPropagation();
						e.preventDefault();
						for(int j = 0; j < objectivesForDiagram.length; j++)
						{
							if(e.getSource() == categoryLabels[j])
							{
								categoryFoldedOut[j] = !categoryFoldedOut[j];
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
			straalRij = new int[numberOfDiagrams][];
			objectivesTextAreas = new TextArea[numberOfDiagrams][];
			
			for(int j = 0; j<numberOfDiagrams; j++)
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
					
					if(labelEndPointX[j][i] >= mpX[j]) //label staat 'rechts' van cirkel
					{	if(labelEndPointY[j][i] > mpY[j] + straal)
						{	links = labelEndPointX[j][i] - breedteLabel(i,j)/2;
							top = labelEndPointY[j][i];
						}
						else if(labelEndPointY[j][i] >= mpY[j])
						{
							links = labelEndPointX[j][i];
							top = labelEndPointY[j][i];
						}
						else if(labelEndPointY[j][i] <= mpY[j] - straal)
						{
							links = labelEndPointX[j][i] - breedteLabel(i,j)/2;
							top = labelEndPointY[j][i] - hoogteLabel(i,j);
						}
						else
						{
							links = labelEndPointX[j][i];
							top = labelEndPointY[j][i] - hoogteLabel(i, j);
						}
					}
					else //label staat 'links' van cirkel
					{	if(labelEndPointY[j][i] > mpY[j] + straal)
						{
							links = labelEndPointX[j][i] - breedteLabel(i,j)/2;
							top = labelEndPointY[j][i];
						}
						else if(labelEndPointY[j][i] >= mpY[j])
						{
							links = labelEndPointX[j][i] - breedteLabel(i,j);
							top = labelEndPointY[j][i];
						}
						else if(labelEndPointY[j][i] <= mpY[j] - straal)
						{
							links = labelEndPointX[j][i] - breedteLabel(i,j)/2;
							top = labelEndPointY[j][i] - hoogteLabel(i,j);
						}
						else
						{
							links = labelEndPointX[j][i] - breedteLabel(i,j);
							top =  labelEndPointY[j][i] - hoogteLabel(i,j);
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
		double breedte = Math.min(mpX[0] - straal - margin, 
			ctx.measureText(objectivesForDiagram[j][i] + ": " + (int) scoresPercObjectives[j][i]+"%").getWidth() + margin);
		return breedte;	
	}
	
	public double hoogteLabel(int i, int j)
	{
		double hoogte = 0;
		if(ctx.measureText(objectivesForDiagram[j][i] + ": " + (int) scoresPercObjectives[j][i] + "%").getWidth() > mpX[0] - straal - margin)
			hoogte = 35;
		else 
			hoogte = 17;			
		
		return hoogte;
	}
	
	public void zetKleurNeutraal()
	{
		colorNeutral = true;
		paint();
	}
	
	
	
	public void zetKleuren()
	{	categoryColorArray = new CssColor[objectivesForDiagram.length];
		colorArray = new CssColor[numberOfDiagrams][];
		for(int j = 0; j < numberOfDiagrams; j++)
		{	int red = 255; 
			int green = 255;
			if(categoryScoresPercObjectives[j] < 50)
				green = (int) (green * categoryScoresPercObjectives[j] / 50);
			else 
				red -= (int) (red * (categoryScoresPercObjectives[j] - 50)/50);
			categoryColorArray[j] = CssColor.make(red, green, 0);
		
			colorArray[j] = new CssColor[objectivesForDiagram[j].length];
			for(int i = 0; i < objectivesForDiagram[j].length; i++)
			{	red = 255; 
				green = 255;
				if(scoresPercObjectives[j][i] < 50)
					green = (int) (green * scoresPercObjectives[j][i] / 50);
				else 
					red -= (int) (red * (scoresPercObjectives[j][i] - 50)/50);
				colorArray[j][i] = CssColor.make(red, green, 0);
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
		int scoreWidth = (int) ctx.measureText(scoreText).getWidth() + margin;
		
		//iets met ctx doen zodat ik mooie scherpe lijnen krijg... maar setLineWidth alleen helpt niet genoeg; ergens heb ik hiervoor iets slims gedaan. 
		
		lineHeight = 15 + margin; //TODO nog iets zinvollers van 15 maken; meten mbv canvas?
		textColumnWidth = 0;
		indent = 20;
		for(int j = 0; j < objectivesForDiagram.length; j++)
		{
			textColumnWidth = Math.max((int) ctx.measureText(categoryStringForDiagram[j]).getWidth() + margin, textColumnWidth);
			for(int i = 0; i < objectivesForDiagram[j].length; i++ )
			{
				textColumnWidth = Math.max((int) ctx.measureText(objectivesForDiagram[j][i]).getWidth() + margin + indent, textColumnWidth);
			}
		}
		
		if(numberOfDiagrams == 1)
		{
			int labelX = margin + 5;
			int scoreX = labelX + textColumnWidth;
			int colorX = margin + textColumnWidth;
			int colorY = margin;
			int textY = lineHeight + 2;
			
			ctx.setFont(boldFontString);
			ctx.fillText(categoryStringForDiagram[0], labelX, textY);
			ctx.fillText(scoreText, scoreX, textY);
			ctx.setFont(fontString);
			
			for(int i = 0; i < objectivesForDiagram[0].length; i++)
			{
				textY += lineHeight;
				colorY += lineHeight;
				ctx.setFillStyle(colorArray[0][i]);
				ctx.fillRect(colorX, colorY, scoreWidth, lineHeight);
				ctx.setFillStyle("black");
				ctx.fillText(objectivesForDiagram[0][i], labelX, textY);
				ctx.fillText((int) scoresPercObjectives[0][i]+"%", scoreX, textY);
			}
			int columnHeight = margin;
			for(int i = 0; i < objectivesForDiagram[0].length + 2; i++)
			{	ctx.beginPath();
				ctx.moveTo(margin, columnHeight);
				ctx.lineTo(textColumnWidth + scoreWidth + margin, columnHeight);
				ctx.closePath();
				ctx.stroke();
				columnHeight += lineHeight;
			}
			columnHeight -= lineHeight;
			ctx.beginPath();
			ctx.moveTo(margin, margin);
			ctx.lineTo(margin, columnHeight);
			ctx.moveTo(margin + textColumnWidth, margin);
			ctx.lineTo(margin + textColumnWidth, columnHeight);
			ctx.moveTo(margin + textColumnWidth + scoreWidth, margin);
			ctx.lineTo(margin + textColumnWidth + scoreWidth, columnHeight);
			ctx.closePath();
			ctx.stroke();
			writePilotText(columnHeight + lineHeight);
		}
		else
		{
			int categoryX = margin + 5;
			int labelX = margin + indent + 5;
			int scoreX = categoryX + textColumnWidth;
			int colorX = margin + textColumnWidth;
			int columnHeight = margin;
			
			//int tekstY = regelHoogte + 2;
			int textDifference = lineHeight - margin + 2;
			int interspace = 5;
			ctx.setFont(boldFontString);
			ctx.fillText(categoryText, categoryX, columnHeight + textDifference);
			ctx.fillText(scoreText, scoreX, columnHeight + textDifference);
			ctx.setFont(fontString);
			textDifference += 3;
			ctx.beginPath();
			ctx.moveTo(margin, columnHeight);
			ctx.lineTo(textColumnWidth + scoreWidth + margin, columnHeight);
			ctx.closePath();
			ctx.stroke();
			columnHeight += lineHeight;			
			
			for(int j = 0; j < objectivesForDiagram.length; j++)
			{
				//dubble line above each category.
				ctx.beginPath();
				ctx.moveTo(margin, columnHeight);
				ctx.lineTo(textColumnWidth + scoreWidth + margin, columnHeight);
				ctx.closePath();
				ctx.stroke();
				columnHeight += interspace;
				ctx.setFillStyle(categoryColorArray[j]);
				ctx.fillRect(colorX, columnHeight, scoreWidth, lineHeight);
				ctx.beginPath();
				ctx.moveTo(margin, columnHeight);
				ctx.lineTo(textColumnWidth + scoreWidth + margin, columnHeight);
				ctx.closePath();
				ctx.stroke();
				
				//fill in category name and score
				textDifference += 12 - lineHeight;
				ctx.setFillStyle("black");
				this.setWidgetLeftWidth(categoryLabels[j], categoryX, Unit.PX, textColumnWidth, Unit.PX);
				this.setWidgetTopHeight(categoryLabels[j], columnHeight + textDifference, Unit.PX, lineHeight, Unit.PX);
				
				textDifference += lineHeight - 15;  //label-location and drawString need different y
				ctx.fillText((int) categoryScoresPercObjectives[j]+"%", scoreX, columnHeight + textDifference);
			
				if(categoryFoldedOut[j])
				{
					for(int i = 0; i < objectivesForDiagram[j].length; i++)
					{
						columnHeight += lineHeight;
						ctx.setFillStyle(colorArray[j][i]);
						ctx.fillRect(colorX, columnHeight, scoreWidth, lineHeight);
						ctx.setFillStyle("black");
						ctx.fillText(objectivesForDiagram[j][i], labelX, columnHeight + textDifference);
						ctx.fillText((int) scoresPercObjectives[j][i]+"%", scoreX, columnHeight + textDifference);
						ctx.beginPath();
						ctx.moveTo(margin, columnHeight);
						ctx.lineTo(textColumnWidth + scoreWidth + margin, columnHeight);
						ctx.closePath();
						ctx.stroke();
					}
				}
				textDifference += 3;
				columnHeight += lineHeight;
			}
			ctx.beginPath();
			ctx.moveTo(margin, columnHeight);
			ctx.lineTo(textColumnWidth + scoreWidth + margin, columnHeight);
			
			//draw table borders
			ctx.moveTo(margin, margin);
			ctx.lineTo(margin, columnHeight);
			ctx.moveTo(margin + textColumnWidth, margin);
			ctx.lineTo(margin + textColumnWidth, columnHeight);
			ctx.moveTo(margin + textColumnWidth + scoreWidth, margin);
			ctx.lineTo(margin + textColumnWidth + scoreWidth, columnHeight);
			
			ctx.closePath();
			ctx.stroke();
			writePilotText(columnHeight + lineHeight);
		}
		
	}
	
	public void writePilotText(int y)
	{
		
		ctx.fillText(Text.constants.pilotTextPart1(), margin, y);
		y += lineHeight - margin;
		ctx.fillText(Text.constants.pilotTextPart2(), margin, y);
	}
	
	public void paint()
	{	

		if(pilot)
		{	paintPilot();
			return;
		}
		ctx.setFillStyle("white");
		ctx.fillRect(0, 0, canvas.getOffsetWidth(), canvas.getOffsetHeight());
		
		zetKleuren();
		ctx.setStrokeStyle("black");
		for(int j = 0; j < numberOfDiagrams; j++)
		{	if(!colorNeutral)
				ctx.setFillStyle(colorArray[j][0]);
			else
				ctx.setFillStyle(cssColorNeutral);
			ctx.beginPath();
			ctx.moveTo(mpX[j], mpY[j]);
			ctx.arc(mpX[j], mpY[j], straalRij[j][0], -Math.PI / 2, -Math.PI / 2 + angle[j][0]);
			ctx.closePath();
			ctx.fill();
			ctx.stroke();
			for(int i = 1; i < objectivesForDiagram[j].length; i++)
			{
				if(!colorNeutral)
					ctx.setFillStyle(colorArray[j][i]);
				else
					ctx.setFillStyle(cssColorNeutral);
				ctx.beginPath();
				ctx.moveTo(mpX[j], mpY[j]);
				ctx.arc(mpX[j], mpY[j], straalRij[j][i], - Math.PI / 2 + cumAngle[j][i-1], - Math.PI / 2 + cumAngle[j][i]);
				ctx.closePath();
				ctx.fill();
				ctx.stroke();
			}
			ctx.beginPath();
			ctx.arc(mpX[j], mpY[j], straal, 0, 360);
			ctx.closePath();
			ctx.stroke(); //nodig?
			for(int i = 0; i < objectivesForDiagram[j].length; i++)
			{	ctx.beginPath();
				ctx.moveTo(mpX[j], mpY[j]);
				ctx.lineTo(endPointX[j][i], endPointY[j][i]);
				ctx.closePath();
				ctx.stroke();
				
			}
			ctx.setFont("bold 12px Arial");
			ctx.setFillStyle("black");
			double stringWidth = ctx.measureText(categoryStringForDiagram[j]).getWidth();
			if(j<3)
				ctx.fillText(categoryStringForDiagram[j], mpX[j] - stringWidth/2, 20);
			else
				ctx.fillText(categoryStringForDiagram[j], mpX[j] - stringWidth/2, 370);
		}
	}	
	
	
}
