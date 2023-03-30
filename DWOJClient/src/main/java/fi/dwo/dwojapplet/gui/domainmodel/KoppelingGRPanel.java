package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JCheckBox;
import fi.dwo.commons.system.TextMapper;

public class KoppelingGRPanel extends JPanel implements Constants {

    private JPanel topPanel;
	private JPanel mainPanel;
	private JPanel bottomPanel;
	
	private JLabel titleLabel;
	
	private JButton okButton, cancelButton;
	
	private Font font = new Font("SansSerif", Font.PLAIN, 12);
	private final Color colorBlue1 = COLOR15; // new Color(49, 71, 112);
//	private final Color colorBlue2 = new Color(38, 115, 182);
//	private final Color colorBlue3 = COLOR13; // new Color(120, 150, 202);
//	private final Color colorBlue4 = new Color(180,195,228);
//	private final Color colorBlue5 = new Color(211,229,244);
//	private final Color colorBlue6 = new Color(231,242,250);
//	private final Color colorGray1 = new Color(206, 207, 208);
	private final Color colorGray2 = COLOR21; // new Color(221, 223, 225);
	private final Color colorGray3 = COLOR20; // new Color(237, 239, 241);
	

	// from resource
    private String KOPPELING_LEERDOEL = "Koppeling leerdoel aan Getal&Ruimte";
	private String[] grJaarlagen = {
			"Leerjaar 1HV",
			"Leerjaar 1V",
			"Leerjaar 2HV",
			"Leerjaar 2V",
			"Leerjaar 3H",
			"Leerjaar 3V"
	};
	private int aantalHoofdstukken[]= {10, 10, 10, 10, 10, 10};
    private int maxAantalHoofdstukken = 10;
    private int aantalJaarlagen=6;

    
	private JCheckBox[][] cb;
	private JComponent[] hf ;
	private JComponent[] jl ;
	private JComponent   all;
	
	KoppelingGRPanel(String koppeling, String[] jaarlagen, int[] aantal, boolean filter) {
		super(new BorderLayout());
		this.filter = filter;
		KOPPELING_LEERDOEL = koppeling;
		aantalJaarlagen = jaarlagen.length;
		grJaarlagen = jaarlagen;
		aantalHoofdstukken = aantal;
		maxAantalHoofdstukken = 0;
		for(int i = 0; i < aantalJaarlagen; i++) {
		  if (aantal[i] > maxAantalHoofdstukken)
		    maxAantalHoofdstukken = aantal[i];
		}
		makeGUI();		
	}
	
	public KoppelingGRPanel() {
	  super(new BorderLayout());
	  makeGUI();
	}
	
	boolean filter = true;
	private void makeGUI() {
		topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(colorBlue1);
		topPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

		mainPanel = new JPanel(null);
		mainPanel.setBackground(colorGray3);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		mainPanel.setPreferredSize(new Dimension(150+50*maxAantalHoofdstukken, 60+30*aantalJaarlagen+60));

		bottomPanel = new JPanel();
		bottomPanel.setBackground(colorGray2);
		bottomPanel.setBorder(BorderFactory.createLineBorder(colorGray2, 2));

		//topPanel
		titleLabel = new JLabel(KOPPELING_LEERDOEL);
		titleLabel.setForeground(colorGray3);
		titleLabel.setFont(font.deriveFont(Font.PLAIN, 24));

		//bottomPanel
		okButton = new JButton(TextMapper.getText(TextMapper.BTN_OK));
		okButton.setPreferredSize(new Dimension(100, 24));
		okButton.setBackground(colorBlue1);
		okButton.setForeground(colorGray3);

		cancelButton = new JButton(TextMapper.getText(TextMapper.BTN_CANCEL));
		cancelButton.setPreferredSize(new Dimension(100, 24));
		cancelButton.setBackground(colorBlue1);
		cancelButton.setForeground(colorGray3);
		
		cb = new JCheckBox[aantalJaarlagen][maxAantalHoofdstukken];
		for(int i=0 ; i<aantalJaarlagen ; i++) {
			for(int j=0 ; j<aantalHoofdstukken[i] ; j++) {
				cb[i][j] = new JCheckBox("");
				cb[i][j].setBounds(120+50*j,60+30*i,50,20);
				mainPanel.add(cb[i][j]);
			}
		}
		
		hf = new JComponent[maxAantalHoofdstukken];
		for(int i=0 ; i<maxAantalHoofdstukken ; i++) {
			hf[i] = new JLabel("hfst "+(i+1));
			hf[i].setBounds(120+50*i,30,50,20);
			hf[i].setForeground(colorBlue1);
			mainPanel.add(hf[i]);
		}
		
		jl = new JComponent[aantalJaarlagen];
		for(int i=0 ; i<aantalJaarlagen ; i++) {
			jl[i] = createjl(grJaarlagen[i], i);
			jl[i].setBounds(20,60+30*i,100,20);
			jl[i].setForeground(colorBlue1);
			mainPanel.add(jl[i]);
		}

		if (filter) {
		  JCheckBox all = new JCheckBox();
		  all.addActionListener(ev -> { 
		    boolean on = all.isSelected();
		    for(JComponent c: jl) {
		      JCheckBox cb = (JCheckBox) c;
		      cb.setSelected(on);
		      for(ActionListener l : cb.getActionListeners()) { l.actionPerformed(ev); }
		      mainPanel.repaint(); //????
		    }		    
		  });
		  this.all = all;
		} else {
		  all = new JLabel();
		}
        all.setBounds(20,30,100,20);
        all.setForeground(colorBlue1);
        mainPanel.add(all);

        plaatsGUI();	
	}
	

	
	
	private JComponent createjl(String string, final int row) {
    if (filter) {
      JCheckBox b = new JCheckBox(string);
      b.addActionListener(e -> {
          JCheckBox bci[] = cb[row];
          for( JCheckBox i: bci) if (i != null) i.setSelected(b.isSelected());      
      });
      return b;
    }
    return new JLabel(string);
  }

  private void plaatsGUI() {
		// topPanel
		topPanel.add(hb( hgl(), titleLabel, hgl()));
		
		//bottomPanel
		bottomPanel.add(hb( okButton, hst(20), cancelButton, hst(20), hgl()));
		
		add(topPanel, BorderLayout.NORTH);
		add(bottomPanel, BorderLayout.SOUTH);
		add(mainPanel, BorderLayout.CENTER);
	}
	
	private static Box hb(Component... c) {
		Box box = Box.createHorizontalBox();
		for (int i = 0; c != null && i < c.length; i++)
			box.add(c[i]);
		return box;
	}

//	private Box vb(Component... c) {
//		Box box = Box.createVerticalBox();
//		for (int i = 0; c != null && i < c.length; i++)
//			box.add(c[i]);
//		return box;
//	}

	private static Component hgl() {
		return Box.createHorizontalGlue();
	}

//	private Component vgl() {
//		return Box.createVerticalGlue();
//	}

	private Component hst(int n) {
		return Box.createHorizontalStrut(n);
	}

//	private Component vst(int n) {
//		return Box.createVerticalStrut(n);
//	}
//
//	private Component ra(int w, int h) {
//		return Box.createRigidArea(new Dimension(w, h));
//	}
	
//	private Component ln(int w, int h) {
//		  Component c =  ln(h);
//		  c.setPreferredSize(new Dimension(w,h));
//		  c.setMinimumSize(new Dimension(w,h));
//		  c.setMaximumSize(new Dimension(w,h));
//		  return c;
//	}
	
//	private Component ln(int h) {
//		  JPanel p = new JPanel() {
//		      public void paintComponent(Graphics g) {
//		        g.setColor(colorBlue4);  
//		        g.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
//		        //g.drawLine(0, getHeight()/2+1, getWidth(), getHeight()/2+1);
//		      }
//		  };
//		  p.setPreferredSize(new Dimension(1,h));
//		  p.setMaximumSize(new Dimension(1000,h));
//		  return p;
//	}
	
	public JButton ok() { return okButton; }
	public JButton cancel() { return cancelButton; }

  public void setState(boolean[][] state) {
    for(int i = 0; i < state.length; i++) {
      JCheckBox[] cbi = cb[i];
      boolean[] statei = state[i];
      for (int j= 0; j < statei.length; j++) 
        cbi[j].setSelected(statei[j]);
    }
  }

  public boolean[][] getState() {
    boolean state[][] = new boolean[cb.length][];
    for(int i = 0; i < cb.length; i++) {
      JCheckBox[] cbi = cb[i];
      boolean[] statei = new boolean[aantalHoofdstukken[i]];
      for(int j = 0; j < statei.length; j++)
        statei[j] = cbi[j].isSelected();
      state[i] = statei;
    }
    return state;
  }

  public JPanel getTopPanel() {
    return topPanel;
  }

  public JPanel getMainPanel() {
    return mainPanel;
  }

  public JPanel getBottomPanel() {
    return bottomPanel;
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    for( JCheckBox cbi[]: cb) {
      if (cbi != null)
      for(JCheckBox cbij: cbi) {
        if (cbij != null)
          cbij.setEnabled(enabled);
      }
    }
  }
	
}
