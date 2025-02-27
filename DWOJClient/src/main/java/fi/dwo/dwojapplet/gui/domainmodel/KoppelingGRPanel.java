package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.ItemSelectable;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JCheckBox;
import fi.beans.numworxlf.JScrollPane;
import fi.dwo.commons.system.TextMapper;

@SuppressWarnings("serial")
public class KoppelingGRPanel extends JPanel implements Constants, ItemListener {

    private static final int CB_WIDTH = 80;
	private JPanel topPanel;
	private JPanel mainPanel;
	private JPanel bottomPanel;
	
	private JLabel titleLabel;
	
	private JButton okButton, cancelButton;
	
	private Font font = new Font("SansSerif", Font.PLAIN, 12);
	private final Color colorBlue1 = COLOR15; // new Color(49, 71, 112);
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
	private String variant;
	
	private void makeGUI() {
		topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(colorBlue1);
		topPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

		mainPanel = new JPanel(null);
		mainPanel.setBackground(colorGray3);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		mainPanel.setPreferredSize(new Dimension(150+CB_WIDTH*maxAantalHoofdstukken, 60+30*aantalJaarlagen+60));

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
				JCheckBox c = 
				cb[i][j] = new JCheckBox("");
				c.addItemListener(this);				
				c.setBounds(120+CB_WIDTH*j,60+30*i,CB_WIDTH,20);
				mainPanel.add(c);
			}
		}
		
		hf = new JComponent[maxAantalHoofdstukken];
		for(int i=0 ; i<maxAantalHoofdstukken ; i++) {
			hf[i] = new JLabel("hfst "+(i+1));
			hf[i].setBounds(120+CB_WIDTH*i,30,CB_WIDTH,20);
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
		add(new JScrollPane(mainPanel), BorderLayout.CENTER);
	}
	
	private static Box hb(Component... c) {
		Box box = Box.createHorizontalBox();
		for (int i = 0; c != null && i < c.length; i++)
			box.add(c[i]);
		return box;
	}

	private static Component hgl() {
		return Box.createHorizontalGlue();
	}

	private Component hst(int n) {
		return Box.createHorizontalStrut(n);
	}

	
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
  
  public void setCurrentVariant(String v) { // switch to if checkbox set...
	  this.variant = v;
	  if (v != null && !v.isEmpty()) {
		  titleLabel.setText(KOPPELING_LEERDOEL + " " + v);
	  } else {
		  titleLabel.setText(KOPPELING_LEERDOEL);
	  }
  }
  
  public void setVariants(String[][] variants) {
	    for(int i = 0; i < variants.length; i++) {
	        JCheckBox[] cbi = cb[i];
	        String[] statei = variants[i];
	        for (int j= 0; j < statei.length; j++) 
	        {  cbi[j].setToolTipText(statei[j]);
	           cbi[j].setText(statei[j]);
	        }
	      }	  
  }

  public String[][] getVariants() {
	String state[][] = new String[cb.length][];
    for(int i = 0; i < cb.length; i++) {
    	JCheckBox[] cbi = cb[i];
    	String[] statei = new String[aantalHoofdstukken[i]];
    	for(int j = 0; j < statei.length; j++)
    		statei[j] = cbi[j].getToolTipText();
    	state[i] = statei;
    }
    return state;	  
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

	@Override
	public void itemStateChanged(ItemEvent e) {
		ItemSelectable cbi = e.getItemSelectable();
		if (e.getStateChange() == ItemEvent.SELECTED) {
			((JComponent) cbi).setToolTipText(variant);
			((JCheckBox) cbi).setText(variant);
			
		} else {
			((JComponent) cbi).setToolTipText(null);
			((JCheckBox) cbi).setText("");
		}
	}
	
}
