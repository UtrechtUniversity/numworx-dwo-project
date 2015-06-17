package nl.uu.fi.dwo.mobile.client.ui;


import java.util.HashMap;
import java.util.Map;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.user.client.ui.TextArea;

public class ScoresObjectivesPanel {

	private String[][] objectives;
	private String[] categorieString;
	
	private String[][] objectivesForDiagram;
	private String[] categorieStringForDiagram;
	private int[][] totaalScoresForDiagram;
	private int[][] totaalMaxForDiagram;
	
	int straal = 100;
	int marge = 5;
	int[] mpX;
	int[] mpY;
	
	int[][] totaalScoreObjectives;
	int[][] totaalMaxObjectives;
	double[][] scoresPercObjectives;
	int[] totaalMax;
	double[][] hoek, cumHoek, labelHoek; 
	double[][] hoekGraden, cumHoekGraden;
	int[][] eindPuntX, eindPuntY, labelEindPuntX, labelEindPuntY;
	int[][] straalRij;
	
	TextArea[][] objectivesTextAreas;
//	Font theFont;
//	FontMetrics theFM;
//	Font theBoldFont;
//	FontMetrics theBoldFM;
	
	CssColor[][] kleurRij;
	
	public ScoresObjectivesPanel(HashMap<String, Object> map)
	{
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
		int nietNulTeller = 0;
		for(int j = 0; j < totaalMax.length; j++)
		{	
			if(totaalMax[j] != 0)
				nietNulTeller++;
		}
		
		objectivesForDiagram = new String[nietNulTeller][];
		categorieStringForDiagram = new String[nietNulTeller];
		totaalScoresForDiagram = new int[nietNulTeller][];
		totaalMaxForDiagram = new int[nietNulTeller][];
		
		nietNulTeller = 0;
		for(int j = 0; j < objectives.length; j++)
			if(totaalMax[j] != 0)
			{	objectivesForDiagram[nietNulTeller] = objectives[j];
				categorieStringForDiagram[nietNulTeller] = categorieString[j];
				totaalScoresForDiagram[nietNulTeller] = totaalScoreObjectives[j];
				totaalMaxForDiagram[nietNulTeller] = totaalMaxObjectives[j];
				totaalMax[nietNulTeller] = totaalMax[j];
				nietNulTeller++;
			}
		
		scoresPercObjectives = new double[objectivesForDiagram.length][];
		hoek = new double[objectivesForDiagram.length][];
		cumHoek = new double[objectivesForDiagram.length][];
		hoekGraden = new double[objectivesForDiagram.length][];
		cumHoekGraden = new double[objectivesForDiagram.length][];
		labelHoek = new double[objectivesForDiagram.length][];
		
		eindPuntX = new int[objectivesForDiagram.length][];
		eindPuntY = new int[objectivesForDiagram.length][];
		labelEindPuntX = new int[objectivesForDiagram.length][];
		labelEindPuntY = new int[objectivesForDiagram.length][];
		mpX = new int[objectivesForDiagram.length];
		mpY = new int[objectivesForDiagram.length];
		
		for(int i = 0; i<objectivesForDiagram.length; i++)
		{
			scoresPercObjectives[i] = new double[objectivesForDiagram[i].length];
			hoek[i] = new double[objectivesForDiagram[i].length];
			cumHoek[i] = new double[objectivesForDiagram[i].length];
			hoekGraden[i] = new double[objectivesForDiagram[i].length];
			cumHoekGraden[i] = new double[objectivesForDiagram[i].length];
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
		
		for(int j = 0; j < objectivesForDiagram.length; j++)
		{	for(int i = 0; i<objectivesForDiagram[j].length; i++)
			{	hoek[j][i] = totaalMaxForDiagram[j][i] * 2 * Math.PI / totaalMax[j];
				hoekGraden[j][i] = totaalMaxForDiagram[j][i] * 360 / totaalMax[j];
			}
		
			cumHoek[j][0] = hoek[j][0];
			cumHoekGraden[j][0] = hoekGraden[j][0];
			for(int i = 1; i<objectivesForDiagram[j].length; i++)
			{	cumHoek[j][i] = cumHoek[j][i-1] + hoek[j][i];
				cumHoekGraden[j][i] = (cumHoekGraden[j][i-1] + hoekGraden[j][i]);
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
		
		straalRij = new int[objectivesForDiagram.length][];
		objectivesTextAreas = new TextArea[objectivesForDiagram.length][];
		
		for(int j = 0; j<objectivesForDiagram.length; j++)
		{	straalRij[j] = new int[objectivesForDiagram[j].length];
			objectivesTextAreas[j] = new TextArea[objectivesForDiagram[j].length];
			for(int i = 0; i<objectivesForDiagram[j].length; i++)
				straalRij[j][i] = (int) Math.round(straal * scoresPercObjectives[j][i]/100);
			for(int i = 0; i < objectivesForDiagram[j].length; i++)
			{	objectivesTextAreas[j][i] = new TextArea();
				//TODO: omzetten naar css-rules
				//objectivesTextAreas[j][i].setFont();
				//objectivesTextAreas[j][i].setLineWrap(true);
				//objectivesTextAreas[j][i].setWrapStyleWord(true);
				
				//TODO: plaatsing en toevoegen teksten anders (gwt-style) regelen.
			
			/*
				if(labelEindPuntX[j][i] >= mpX[j]) //label staat 'rechts' van cirkel
				{	if(labelEindPuntY[j][i] > mpY[j] + straal)
						objectivesTextAreas[j][i].setBounds(labelEindPuntX[j][i] - breedteLabel(i,j)/2, labelEindPuntY[j][i], breedteLabel(i,j), hoogteLabel(i,j));
					else if(labelEindPuntY[j][i] >= mpY[j])
						objectivesTextAreas[j][i].setBounds(labelEindPuntX[j][i], labelEindPuntY[j][i], breedteLabel(i,j), hoogteLabel(i,j));
					else if(labelEindPuntY[j][i] <= mpY[j] - straal)
						objectivesTextAreas[j][i].setBounds(labelEindPuntX[j][i] - breedteLabel(i,j)/2, labelEindPuntY[j][i] - hoogteLabel(i,j), breedteLabel(i,j), hoogteLabel(i,j));
					else
						objectivesTextAreas[j][i].setBounds(labelEindPuntX[j][i], labelEindPuntY[j][i] - hoogteLabel(i,j), breedteLabel(i,j), hoogteLabel(i,j));
				}
				else //label staat 'links' van cirkel
				{	if(labelEindPuntY[j][i] > mpY[j] + straal)
						objectivesTextAreas[j][i].setBounds(labelEindPuntX[j][i] - breedteLabel(i,j)/2, labelEindPuntY[j][i], breedteLabel(i,j), hoogteLabel(i,j));
					else if(labelEindPuntY[j][i] >= mpY[j])
						objectivesTextAreas[j][i].setBounds(labelEindPuntX[j][i] - breedteLabel(i,j), labelEindPuntY[j][i], breedteLabel(i,j), hoogteLabel(i,j));
					else if(labelEindPuntY[j][i] <= mpY[j] - straal)
						objectivesTextAreas[j][i].setBounds(labelEindPuntX[j][i] - breedteLabel(i,j)/2, labelEindPuntY[j][i] - hoogteLabel(i,j), breedteLabel(i,j), hoogteLabel(i,j));
					else
						objectivesTextAreas[j][i].setBounds(labelEindPuntX[j][i] - breedteLabel(i,j), labelEindPuntY[j][i] - hoogteLabel(i,j), breedteLabel(i,j), hoogteLabel(i,j));
				}	
				objectivesTextAreas[j][i].setText(objectivesForDiagram[j][i] + ": " + (int) scoresPercObjectives[j][i]+"%");
				add(objectivesTextAreas[j][i]);
				*/
			}
		}
	}
	
	public int breedteLabel(int i, int j)
	{
		int breedte = 0;
		
		//TODO: breedte meten met behulp van measureText.
		//int breedte = Math.min(mpX[0] - straal - marge, 
		//		theFM.stringWidth(objectivesForDiagram[j][i] + ": " + (int) scoresPercObjectives[j][i]+"%"));
		return breedte;	
	}
	
	public int hoogteLabel(int i, int j)
	{
		int hoogte = 0;
		
		//TODO: hoogte op handige manier meten.
//		if(theFM.stringWidth(objectivesForDiagram[j][i] + ": " + (int) scoresPercObjectives[j][i] + "%") > mpX[0] - straal - marge)
//			hoogte = 2 * theFM.getHeight();
//		else 
//			hoogte = theFM.getHeight();			
		
		return hoogte;
	}
	
	public void zetKleuren()
	{	kleurRij = new CssColor[objectivesForDiagram.length][];
		for(int j = 0; j < objectivesForDiagram.length; j++)
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
	
	public void paintComponent(Context2d g)
	{	
		//TODO: omzetten.
		/*
		super.paintComponent(g);
		this.setBackground(Color.WHITE);
		
		Graphics2D g2 = (Graphics2D) g;
		Arc2D.Double arc;
		Ellipse2D.Double ellipse;
		Line2D.Double line;
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		        RenderingHints.VALUE_ANTIALIAS_ON);	
		
		zetKleuren();
		for(int j = 0; j < objectivesForDiagram.length; j++)
		{	arc = new Arc2D.Double(mpX[j] - straalRij[j][0], mpY[j] - straalRij[j][0], 2 * straalRij[j][0], 2 * straalRij[j][0], 90, (int) - hoekGraden[j][0],Arc2D.PIE);
			g2.setColor(kleurRij[j][0]);
			g2.fill(arc);
			g2.setColor(Color.BLACK);
			g2.draw(arc);
			
			for(int i = 1; i < objectivesForDiagram[j].length; i++)
			{
				arc = new Arc2D.Double(mpX[j] - straalRij[j][i], mpY[j] - straalRij[j][i], 2 * straalRij[j][i], 2 * straalRij[j][i], 90 - cumHoekGraden[j][i-1], (int) - hoekGraden[j][i], Arc2D.PIE);
				g2.setColor(kleurRij[j][i]);
				g2.fill(arc);
				g2.setColor(Color.BLACK);
				g2.draw(arc);
				
			}
			ellipse = new Ellipse2D.Double(mpX[j] - straal, mpY[j]-straal, 2 * straal, 2 * straal);
			g2.setColor(Color.BLACK);
			g2.draw(ellipse);
			for(int i = 0; i < objectivesForDiagram[j].length; i++)
			{	line = new Line2D.Double(mpX[j], mpY[j], eindPuntX[j][i], eindPuntY[j][i]);
				g2.draw(line);
			}
			g.setFont(theBoldFont);
			if(j<3)
				g.drawString(categorieStringForDiagram[j], mpX[j] - theBoldFM.stringWidth(categorieStringForDiagram[j])/2, 20);
			else
				g.drawString(categorieStringForDiagram[j], mpX[j] - theBoldFM.stringWidth(categorieStringForDiagram[j])/2, 370);
		}
		*/
	}	
}
