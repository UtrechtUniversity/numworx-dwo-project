package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JCheckBox;

public class KoppelingGRPanel extends JPanel{

	private JPanel topPanel;
	private JPanel mainPanel;
	private JPanel bottomPanel;
	
	private JLabel titleLabel;
	
	private JButton okButton, cancelButton;
	
	private Font font = new Font("SansSerif", Font.PLAIN, 12);
	private Color colorBlue1 = new Color(49, 71, 112);
	private Color colorBlue2 = new Color(38, 115, 182);
	private Color colorBlue3 = new Color(120, 150, 202);
	private Color colorBlue4 = new Color(180,195,228);
	private Color colorBlue5 = new Color(211,229,244);
	private Color colorBlue6 = new Color(231,242,250);
	private Color colorGray1 = new Color(206, 207, 208);
	private Color colorGray2 = new Color(221, 223, 225);
	private Color colorGray3 = new Color(237, 239, 241);
	
	private String[] grJaarlagen = {
			"Leerjaar 1HV",
			"Leerjaar 1V",
			"Leerjaar 2HV",
			"Leerjaar 2V",
			"Leerjaar 3H",
			"Leerjaar 3V"
	};
	private int aantalJaarlagen=6;
	private int aantalHoofdstukken=10;
	
	private JCheckBox[][] cb;
	private JLabel[] hf ;
	private JLabel[] jl ;
	
	public KoppelingGRPanel() {
		setLayout(new BorderLayout());
		Locale locale = Locale.forLanguageTag("nl");
		makeGUI();
		
	}
	
	private void makeGUI() {
		topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(colorBlue1);
		topPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

		mainPanel = new JPanel(null);
		mainPanel.setBackground(colorGray3);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		mainPanel.setSize(600,400);
		mainPanel.setPreferredSize(new Dimension(650, 300));

		bottomPanel = new JPanel();
		bottomPanel.setBackground(colorGray2);
		bottomPanel.setBorder(BorderFactory.createLineBorder(colorGray2, 2));

		//topPanel
		titleLabel = new JLabel("Koppeling leerdoel aan Getal&Ruimte");
		titleLabel.setForeground(colorGray3);
		titleLabel.setFont(font.deriveFont(Font.PLAIN, 24));

		//bottomPanel
		okButton = new JButton("Opslaan");
		okButton.setPreferredSize(new Dimension(90, 24));
		okButton.setBackground(colorBlue1);
		okButton.setForeground(colorGray3);

		cancelButton = new JButton("Cancel");
		cancelButton.setPreferredSize(new Dimension(90, 24));
		cancelButton.setBackground(colorBlue1);
		cancelButton.setForeground(colorGray3);
		
		cb = new JCheckBox[aantalJaarlagen][aantalHoofdstukken];
		for(int i=0 ; i<aantalJaarlagen ; i++) {
			for(int j=0 ; j<aantalHoofdstukken ; j++) {
				cb[i][j] = new JCheckBox("");
				cb[i][j].setBounds(120+50*j,60+30*i,50,20);
				mainPanel.add(cb[i][j]);
				//cb[i][j].setPreferredSize(new Dimension(80,20));
				//[i][j].setMinimumSize(new Dimension(80,20));
			}
		}
		
		hf = new JLabel[aantalHoofdstukken];
		for(int i=0 ; i<aantalHoofdstukken ; i++) {
			hf[i] = new JLabel("hfst "+(i+1));
			hf[i].setBounds(120+50*i,30,50,20);
			hf[i].setForeground(colorBlue1);
			mainPanel.add(hf[i]);
			//hf[i].setPreferredSize(new Dimension(80,20));
			//hf[i].setMinimumSize(new Dimension(80,20));
		}
		
		jl = new JLabel[aantalJaarlagen];
		for(int i=0 ; i<aantalJaarlagen ; i++) {
			jl[i] = new JLabel(grJaarlagen[i]);
			jl[i].setBounds(20,60+30*i,100,20);
			jl[i].setForeground(colorBlue1);
			mainPanel.add(jl[i]);
			//jl[i].setPreferredSize(new Dimension(280,20));
			//jl[i].setMinimumSize(new Dimension(280,20));
		}
		
		plaatsGUI();	
	}
	
	private void plaatsGUI() {
		// topPanel
		Component[] compTop = {  hgl(), titleLabel, hgl() };
		topPanel.add(hb(compTop));
		
		//bottomPanel
		Component[] compBottom = { okButton, hst(20), cancelButton, hst(20), hgl() };
		bottomPanel.add(hb(compBottom));
		
//		Component[] r00 = {ra(280,0),hf[0],hf[1],hf[2],hf[3],hf[4],hf[5],hf[6],hf[7],hf[8],hf[9]};
//		Component[] r01 = {jl[0],cb[0][0],cb[0][1],cb[0][2],cb[0][3],cb[0][4],cb[0][5],cb[0][6],cb[0][7],cb[0][8],cb[0][9]};
//		Component[] r02 = {jl[1],cb[1][0],cb[1][1],cb[1][2],cb[1][3],cb[1][4],cb[1][5],cb[1][6],cb[1][7],cb[1][8],cb[1][9]};
//		Component[] r03 = {jl[2],cb[2][0],cb[2][1],cb[2][2],cb[2][3],cb[2][4],cb[2][5],cb[2][6],cb[2][7],cb[2][8],cb[2][9]};
//		Component[] r04 = {jl[3],cb[3][0],cb[3][1],cb[3][2],cb[3][3],cb[3][4],cb[3][5],cb[3][6],cb[3][7],cb[3][8],cb[3][9]};
//		Component[] r05 = {jl[4],cb[4][0],cb[4][1],cb[4][2],cb[4][3],cb[4][4],cb[4][5],cb[4][6],cb[4][7],cb[4][8],cb[4][9]};
//		Component[] r06 = {jl[5],cb[5][0],cb[5][1],cb[5][2],cb[5][3],cb[5][4],cb[5][5],cb[5][6],cb[5][7],cb[5][8],cb[5][9]};
//		
//		int d = 10;
//		Component[] mainBoxComp = {hb(r00),ra(0,d),hb(r01),ra(0,d),hb(r02),ra(0,d),hb(r03),ra(0,d),hb(r04),ra(0,d),hb(r05),ra(0,d),hb(r06),ra(0,d)};
//		//Box mainBox = vb(mainBoxComp);
		//mainPanel.add(vb(mainBoxComp));
		add(topPanel, BorderLayout.NORTH);
		add(bottomPanel, BorderLayout.SOUTH);
		add(mainPanel, BorderLayout.CENTER);
	}
	
	private Box hb(Component[] c) {
		Box box = Box.createHorizontalBox();
		for (int i = 0; c != null && i < c.length; i++)
			box.add(c[i]);
		return box;
	}

	private Box vb(Component[] c) {
		Box box = Box.createVerticalBox();
		for (int i = 0; c != null && i < c.length; i++)
			box.add(c[i]);
		return box;
	}

	private Component hgl() {
		return Box.createHorizontalGlue();
	}

	private Component vgl() {
		return Box.createVerticalGlue();
	}

	private Component hst(int n) {
		return Box.createHorizontalStrut(n);
	}

	private Component vst(int n) {
		return Box.createVerticalStrut(n);
	}

	private Component ra(int w, int h) {
		return Box.createRigidArea(new Dimension(w, h));
	}
	
	private Component ln(int w, int h) {
		  Component c =  ln(h);
		  c.setPreferredSize(new Dimension(w,h));
		  c.setMinimumSize(new Dimension(w,h));
		  c.setMaximumSize(new Dimension(w,h));
		  return c;
	}
	
	private Component ln(int h) {
		  JPanel p = new JPanel() {
		      public void paintComponent(Graphics g) {
		        g.setColor(colorBlue4);  
		        g.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
		        //g.drawLine(0, getHeight()/2+1, getWidth(), getHeight()/2+1);
		      }
		  };
		  p.setPreferredSize(new Dimension(1,h));
		  p.setMaximumSize(new Dimension(1000,h));
		  return p;
	}
	
}
