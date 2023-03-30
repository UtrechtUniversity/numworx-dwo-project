package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.ScrollPane;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicMenuBarUI;
import javax.swing.plaf.basic.BasicMenuUI;

import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JComboBox;
import fi.beans.numworxlf.JScrollPane;
import fi.beans.numworxlf.JTextField;

import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdr;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdrEditPanel;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdrPanel;


public class LeerdomeinMockupPanel extends JPanel implements ActionListener {
	private Locale locale;
	private JPanel topPanel;
	private JPanel topEditPanel;
	private JButton editButton;
	private JButton stopEditButton;
	private JLabel titleLabel;
	private JLabel titleEditLabel;
	
	
	private JPanel mainPanel;
	private JPanel mainEditPanel;
	private JPanel treePanel, wiskOpdrPanel;
	private JPanel treeEditPanel, wiskOpdrEditPanel;
	private Image treeImage;
	private Image treeEditImage;
	private JScrollPane treeScrollPane;
	private JScrollPane treeEditScrollPane;
	private JButton filterButton;
	private JMenuBar treeMenuBar;
	private JComboBox languageComboBox;
	
	private JScrollPane wiskOpdrScrollPane;
	private JScrollPane wiskOpdrEditScrollPane;
	private JButton voorkennisButton;
	private JButton voorkennisEditButton;
	private JButton grButton, mwButton, numworxButton;
	private JButton grEditButton, mwEditButton, numworxEditButton;
	private JLabel initLabel, learnLabel, slipLabel;
	private JTextField initTF, learnTF, slipTF;
	private JLabel koppelingLabel, parametersLabel;
	
	private Image wiskOpdrImage;
	private Image wiskOpdrEditImage;
	private JLabel leerdoelTitelLabel ;
	private JTextField leerdoelTitelEditor ;
	
	
	
	private JPanel bottomPanel;
	
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

	public LeerdomeinMockupPanel() {
		setLayout(new BorderLayout());
		Locale locale = Locale.forLanguageTag("nl");
		treeImage = loadImage("resources/tree-gray.png");
		wiskOpdrImage = loadImage("resources/wiskOpdrPanel.png");
		treeEditImage = loadImage("resources/tree.png");
		wiskOpdrEditImage = loadImage("resources/wiskopdreditor2.png");
		makeGUI();
		//makeEditGUI();
	}
	
	public void makeGUI() {
		topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(colorBlue1);
		topPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

		mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(colorGray3);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		//topPanel
		editButton = new JButton("Bewerk");
		editButton.addActionListener(this);
		
		titleLabel = new JLabel("SLO leerdoelen onderbouw (numworx versie)");
		titleLabel.setForeground(colorGray3);
		titleLabel.setFont(font.deriveFont(Font.PLAIN, 24));
		
		//mainPanel
		treePanel = new JPanel();
		treePanel.setBackground(colorGray3);
		treePanel.setSize(new Dimension(treeImage.getWidth(null),treeImage.getHeight(null)));
		treePanel.add(new JLabel(new ImageIcon(treeImage)));
		
		treeScrollPane = new JScrollPane(treePanel);
		treeScrollPane.setPreferredSize(new Dimension(treeImage.getWidth(null)+40,treeImage.getHeight(null)+20));
		treeScrollPane.setMaximumSize(new Dimension(3000,1200));
		treeScrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		filterButton = new JButton("Filter leerdoelen");
		filterButton.setPreferredSize(new Dimension(140, 24));
			
		wiskOpdrPanel = new JPanel(null);
		wiskOpdrPanel.setBackground(Color.white);
		wiskOpdrPanel.setPreferredSize(new Dimension(wiskOpdrImage.getWidth(null)+10,wiskOpdrImage.getHeight(null)));
		JLabel wiskOpdrImageLabel = new JLabel(new ImageIcon(wiskOpdrImage));
		wiskOpdrImageLabel.setBounds(5,0,wiskOpdrImage.getWidth(null), wiskOpdrImage.getHeight(null));
		wiskOpdrPanel.add(wiskOpdrImageLabel);
		
		wiskOpdrScrollPane = new JScrollPane(wiskOpdrPanel);
		wiskOpdrScrollPane.setPreferredSize(new Dimension(wiskOpdrImage.getWidth(null)+40,wiskOpdrImage.getHeight(null)));
		wiskOpdrScrollPane.setMinimumSize(new Dimension(wiskOpdrImage.getWidth(null)+40,100));
		wiskOpdrScrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		leerdoelTitelLabel = new JLabel("Handig haakjes wegwerken bij merkwaardige producten");
		leerdoelTitelLabel.setForeground(Color.WHITE);
		leerdoelTitelLabel.setBorder(BorderFactory.createEmptyBorder(4, 20, 4, 20));
		leerdoelTitelLabel.setFont(font.deriveFont(Font.BOLD, 14));
		
		grButton = new JButton("Getal&Ruimte");
		grButton.setFont(font);
		grButton.setPreferredSize(new Dimension(140, 24));
		
		mwButton = new JButton("Moderne Wiskunde");
		mwButton.setFont(font);
		mwButton.setPreferredSize(new Dimension(140, 24));
		
		numworxButton = new JButton("Numworx");
		numworxButton.setFont(font);
		numworxButton.setPreferredSize(new Dimension(120, 24));
		
		voorkennisButton = new JButton("Voorkennis");
		voorkennisButton.setFont(font);
		voorkennisButton.setPreferredSize(new Dimension(120, 24));
	
		plaatsGUI();
	}
	
	public void makeEditGUI() {
		topEditPanel = new JPanel(new BorderLayout());
		topEditPanel.setBackground(colorBlue1);
		topEditPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

		mainEditPanel = new JPanel(new BorderLayout());
		mainEditPanel.setBackground(colorGray3);
		mainEditPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		bottomPanel = new JPanel();
		bottomPanel.setBackground(colorGray2);
		bottomPanel.setBorder(BorderFactory.createLineBorder(colorGray2, 2));

		//topPanel
		stopEditButton = new JButton("Stop bewerken");
		stopEditButton.addActionListener(this);
		
		titleEditLabel = new JLabel("Editor SLO leerdoelen onderbouw (numworx versie)");
		titleEditLabel.setForeground(colorGray3);
		titleEditLabel.setFont(font.deriveFont(Font.PLAIN, 24));

		//bottomPanel
		okButton = new JButton("Opslaan");
		okButton.setPreferredSize(new Dimension(90, 24));
		okButton.setBackground(colorBlue1);
		okButton.setForeground(colorGray3);

		cancelButton = new JButton("Cancel");
		cancelButton.setPreferredSize(new Dimension(90, 24));
		cancelButton.setBackground(colorBlue1);
		cancelButton.setForeground(colorGray3);
		
		//mainPanel
		treeEditPanel = new JPanel();
		treeEditPanel.setBackground(Color.white);
		treeEditPanel.setSize(new Dimension(treeEditImage.getWidth(null),treeEditImage.getHeight(null)));
		treeEditPanel.add(new JLabel(new ImageIcon(treeEditImage)));
		
		treeEditScrollPane = new JScrollPane(treeEditPanel);
		treeEditScrollPane.setPreferredSize(new Dimension(treeEditImage.getWidth(null)+20,treeEditImage.getHeight(null)+20));
		treeEditScrollPane.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, colorBlue4));
		
		treeMenuBar = new JMenuBar();
		treeMenuBar.setUI(new BasicMenuBarUI() {
			public void paintComponent(Graphics g) {
			}
		});
		JMenu bestandMenu = new JMenu("Bestand");
		bestandMenu.setUI(new BasicMenuUI() {
			public void paint(Graphics g) {
			}
		});
		bestandMenu.setOpaque(true);
		bestandMenu.setBackground(colorGray2);
		bestandMenu.setForeground(colorBlue1);
		treeMenuBar.add(bestandMenu);
		
		JMenu bewerkenMenu = new JMenu("Bewerken");
		bewerkenMenu.setUI(new BasicMenuUI() {
			public void paint(Graphics g) {
			}
		});
		bewerkenMenu.setBackground(colorGray2);
		bewerkenMenu.setForeground(colorBlue1);
		treeMenuBar.add(bewerkenMenu);
		treeMenuBar.setOpaque(true);
		treeMenuBar.setPreferredSize(new Dimension(treeEditImage.getWidth(null)+20,26));
		treeMenuBar.setMaximumSize(new Dimension(1000,26));
		treeMenuBar.setBackground(colorGray2);
		treeMenuBar.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, colorBlue4));
		
		languageComboBox = new JComboBox();
		languageComboBox.addItem("nl");
		languageComboBox.setPreferredSize(new Dimension(70,20));
		languageComboBox.setMaximumSize(new Dimension(70,20));
		languageComboBox.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1, colorBlue4));
		
		wiskOpdrEditPanel = new JPanel(null);
		wiskOpdrEditPanel.setBackground(colorGray1);
		wiskOpdrEditPanel.setPreferredSize(new Dimension(wiskOpdrEditImage.getWidth(null)+10,wiskOpdrEditImage.getHeight(null)));
		JLabel wiskOpdrImageLabel = new JLabel(new ImageIcon(wiskOpdrEditImage));
		wiskOpdrImageLabel.setBounds(0,0,wiskOpdrEditImage.getWidth(null), wiskOpdrEditImage.getHeight(null));
		wiskOpdrEditPanel.add(wiskOpdrImageLabel);
		
		wiskOpdrEditScrollPane = new JScrollPane(wiskOpdrEditPanel);
		wiskOpdrEditScrollPane.setPreferredSize(new Dimension(wiskOpdrEditImage.getWidth(null)+15,wiskOpdrEditImage.getHeight(null)));
		wiskOpdrEditScrollPane.setMinimumSize(new Dimension(wiskOpdrEditImage.getWidth(null)+15,100));
		wiskOpdrEditScrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		leerdoelTitelEditor = new JTextField("Handig haakjes wegwerken bij merkwaardige producten");
		leerdoelTitelEditor.setForeground(Color.WHITE);
		leerdoelTitelEditor.setMaximumSize(new Dimension(800,30));
		leerdoelTitelEditor.setBorder(BorderFactory.createEmptyBorder(4, 20, 4, 20));
		leerdoelTitelEditor.setFont(font.deriveFont(Font.BOLD, 14));
		leerdoelTitelEditor.setOpaque(false);
		
		initLabel = new JLabel("Init");
		initLabel.setForeground(colorBlue1);
		initLabel.setFont(font);
		
		learnLabel = new JLabel("Learn");
		learnLabel.setForeground(colorBlue1);
		learnLabel.setFont(font);
		
		slipLabel = new JLabel("Slip");
		slipLabel.setForeground(colorBlue1);
		slipLabel.setFont(font);
		
		initTF = new JTextField("0.5");
		initTF.setForeground(colorBlue1);
		initTF.setFont(font);
		initTF.setPreferredSize(new Dimension(50,20));
		initTF.setMaximumSize(new Dimension(50,24));
		
		learnTF = new JTextField("0.2");
		learnTF.setForeground(colorBlue1);
		learnTF.setFont(font);
		learnTF.setPreferredSize(new Dimension(50,20));
		learnTF.setMaximumSize(new Dimension(50,30));
		
		slipTF = new JTextField("0.05");
		slipTF.setForeground(colorBlue1);
		slipTF.setFont(font);
		slipTF.setPreferredSize(new Dimension(50,20));
		slipTF.setMaximumSize(new Dimension(50,30));
		
		koppelingLabel = new JLabel("Koppeling lesmateriaal:");
		koppelingLabel.setForeground(colorBlue1);
		koppelingLabel.setFont(font);
		
		parametersLabel = new JLabel("Knowledge tracing parameters:");
		parametersLabel.setForeground(colorBlue1);
		parametersLabel.setFont(font);
		
		grEditButton = new JButton("Getal&Ruimte");
		grEditButton.setFont(font);
		grEditButton.setPreferredSize(new Dimension(140, 20));
		grEditButton.addActionListener(this);
		
		mwEditButton = new JButton("Moderne Wiskunde");
		mwEditButton.setFont(font);
		mwEditButton.setPreferredSize(new Dimension(140, 20));
		
		numworxEditButton = new JButton("Numworx");
		numworxEditButton.setFont(font);
		numworxEditButton.setPreferredSize(new Dimension(120, 20));
		
		voorkennisEditButton = new JButton("Voorkennis");
		voorkennisEditButton.setFont(font);
		voorkennisEditButton.setPreferredSize(new Dimension(120, 20));
		
		
		plaatsEditGUI();
	}
	
	private void plaatsGUI() {
		
		// topPanel
		Component[] compTop = { editButton, hgl(), titleLabel, hgl() };
		topPanel.add(hb(compTop));


		//mainPanel
		//
		Component[] r11 = {treeScrollPane};
		Component[] r12 = {hgl(), filterButton, hgl()};
		Component[] compLinks = { hb(r11),ra(0,10), hb(r12),ra(0,10)};
		
		Component[] r21 = {leerdoelTitelLabel, hgl()};
		Component[] r22 = {wiskOpdrScrollPane};
		Component[] r23 = {ra(10,0),voorkennisButton, ra(10,0), hgl(), grButton, ra(10,0), mwButton, ra(10,0)};
		
		
		Box leerdoelTitel = hb(r21);
		leerdoelTitel.setOpaque(true);
		leerdoelTitel.setBackground(colorBlue3);
		
		Component[] compLeerdoelFooter = {ra(0,10), hb(r23),ra(0,8)};
		
		Box leerdoelFooter = vb(compLeerdoelFooter);
		leerdoelFooter.setOpaque(true);
		leerdoelFooter.setBackground(colorGray3);
		//leerdoelFooter.setBorder(BorderFactory.createLineBorder(colorBlue3, 1));
		
		Component[] compLeerdoel = {leerdoelTitel , ra(0,20), hb(r22), ra(0,5), leerdoelFooter};
		
		Box leerdoelBox = vb(compLeerdoel);
		leerdoelBox.setBorder(BorderFactory.createLineBorder(colorBlue3, 1));
		leerdoelBox.setOpaque(true);
		leerdoelBox.setBackground(Color.white);
		
		Component[] compRechtsA = {leerdoelBox};
		
		Component[] compRechts = {hb(compRechtsA)};
		
		Component[] compMain = {vb(compLinks) , hst(20), vb(compRechts)};
		mainPanel.add(hb(compMain));

		
		
		add(topPanel, BorderLayout.NORTH);
		add(mainPanel, BorderLayout.CENTER);

		
	}
	
	private void plaatsEditGUI() {
		
		// topPanel
		Component[] compTop = { stopEditButton, hgl(), titleEditLabel, hgl() };
		topEditPanel.add(hb(compTop));

		
		//bottomPanel
		Component[] compBottom = { okButton, hst(20), cancelButton, hst(20), hgl() };
		bottomPanel.add(hb(compBottom));

		//mainPanel
		
		JLabel instellingenLabel = new JLabel("Instellingen");
		 instellingenLabel.setFont(new Font("SansSerif", Font.BOLD,11));
		 instellingenLabel.setForeground(colorBlue3);
        Component[] instellingenLine = {ra(10,0),ln(10),instellingenLabel,ln(10), ra(10,0)};
        
		Component[] r10 = {treeMenuBar};
		Component[] r11 = {treeEditScrollPane};
		Component[] compLinks = { hb(r10),hb(r11)};
		
		Component[] r21 = {leerdoelTitelEditor, hgl()};
		Component[] r22 = {wiskOpdrEditScrollPane};
		Component[] r23 = {ra(10,0),voorkennisEditButton, ra(10,0), hgl()};
		Component[] r24 = {ra(10,0), parametersLabel, ra(10,0), hgl(), initLabel, ra(5,0), initTF, ra(10,0),learnLabel, ra(5,0), learnTF, ra(10,0), slipLabel, ra(5,0), slipTF, ra(10,0)};
		Component[] r25 = {ra(10,0), koppelingLabel,ra(10,0), hgl(), grEditButton, ra(10,0), mwEditButton, ra(10,0)};
		
		
		Box leerdoelTitel = hb(r21);
		leerdoelTitel.setOpaque(true);
		leerdoelTitel.setBackground(colorBlue3);
		
		Component[] compLeerdoelFooter = {ra(0,10), hb(instellingenLine), ra(0,5),hb(r23), ra(0,10),hb(r24), ra(0,10),hb(r25),ra(0,8)};
		
		Box leerdoelFooter = vb(compLeerdoelFooter);
		leerdoelFooter.setOpaque(true);
		leerdoelFooter.setBackground(colorGray3);
		//leerdoelFooter.setBorder(BorderFactory.createLineBorder(colorBlue3, 1));
		
		Component[] compLeerdoel = {leerdoelTitel , ra(0,0), hb(r22), ra(0,0), leerdoelFooter};
		
		Box leerdoelBox = vb(compLeerdoel);
		leerdoelBox.setBorder(BorderFactory.createLineBorder(colorBlue4, 1));
		
		Component[] compRechtsA = {leerdoelBox};
		
		Component[] compRechts = {hb(compRechtsA)};
		
		Component[] compMain = {vb(compLinks) , hst(20), vb(compRechts)};
		mainEditPanel.add(hb(compMain));

		
		
		add(topEditPanel, BorderLayout.NORTH);
		add(bottomPanel, BorderLayout.SOUTH);
		add(mainEditPanel, BorderLayout.CENTER);

		
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
	
	private Image loadImage(String imagePath) {
		Image image = null;
		try {
			URL url = getClass().getResource(imagePath);
			image = ImageIO.read(url);
		}
		catch(Exception ex) {System.out.println("plaatje niet gevonden h");}
		return image;
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==editButton) {
			remove(mainPanel);
			remove(topPanel);
			if(mainEditPanel==null)
				makeEditGUI();
			else {
				add(mainEditPanel, BorderLayout.CENTER);
				add(bottomPanel, BorderLayout.SOUTH);
				add(topEditPanel, BorderLayout.NORTH);
			}
			((JFrame)SwingUtilities.getAncestorOfClass(JFrame.class,(Component)this)).pack();
		}
		if(e.getSource()==stopEditButton) {
			remove(mainEditPanel);
			remove(bottomPanel);
			remove(topEditPanel);
			if(mainPanel==null)
				makeGUI();
			else {
				add(mainPanel, BorderLayout.CENTER);
				add(topPanel, BorderLayout.NORTH);
			}
			((JFrame)SwingUtilities.getAncestorOfClass(JFrame.class,(Component)this)).pack();
		}
		if(e.getSource()==grEditButton) {
			new SelectKoppelingGRMockup().show();
		}
		
		
	}
}
