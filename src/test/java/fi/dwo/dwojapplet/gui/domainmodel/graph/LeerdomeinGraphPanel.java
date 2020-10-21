package fi.dwo.dwojapplet.gui.domainmodel.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import fi.beans.numworxlf.JButton;

public class LeerdomeinGraphPanel extends JPanel implements ActionListener{
	
	public static Color colorBlue1 = new Color(49, 71, 112);
	public static Color colorBlue2 = new Color(38, 115, 182);
	public static Color colorBlue3 = new Color(120, 150, 202);
	public static Color colorBlue4 = new Color(180,195,228);
	public static Color colorBlue5 = new Color(211,229,244);
	public static Color colorBlue6 = new Color(231,242,250);
	public static Color colorGray1 = new Color(206, 207, 208);
	public static Color colorGray2 = new Color(221, 223, 225);
	public static Color colorGray3 = new Color(237, 239, 241);
	
	private int width = 1200;
	private int height = 800;
	
	private Font font = new Font("SansSerif", Font.PLAIN, 12);
	
	private boolean editMode = false;
	
	private Graph graphPanel;
	private EditGraph graphEditPanel;
	private JPanel topPanel;
	private JPanel topEditPanel;
	private JPanel bottomPanel;
	
	private JButton editButton;
	private JButton stopEditButton;
	private JLabel titleLabel;
	private JLabel titleEditLabel;
	
	private JButton okButton, cancelButton;
	
	public LeerdomeinGraphPanel() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(width, height));
		//setBackground(colorGray3);
		
		makeGUI();
	}
	
	private void makeGUI() {
		topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(colorBlue1);
		topPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

		graphPanel = new Graph();
		
		editButton = new JButton("Bewerk");
		editButton.addActionListener(this);
		
		titleLabel = new JLabel("Voorkennisgraaf");
		titleLabel.setForeground(colorGray3);
		titleLabel.setFont(font.deriveFont(Font.PLAIN, 24));
		
		plaatsGUI();
	}
	
	private void plaatsGUI() {
		// topPanel
		Component[] compTop = { editButton, hgl(), titleLabel, hgl() };
		topPanel.add(hb(compTop));
				
		add(topPanel, BorderLayout.NORTH);
		add(graphPanel, BorderLayout.CENTER);
		graphPanel.repaint();
	}
	
	private void makeEditGUI() {
		topEditPanel = new JPanel(new BorderLayout());
		topEditPanel.setBackground(colorBlue1);
		topEditPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

		graphEditPanel = new EditGraph(graphPanel);

		bottomPanel = new JPanel();
		bottomPanel.setBackground(colorGray2);
		bottomPanel.setBorder(BorderFactory.createLineBorder(colorGray2, 2));

		//topPanel
		stopEditButton = new JButton("Stop bewerken");
		stopEditButton.addActionListener(this);
		
		titleEditLabel = new JLabel("Editor Voorkennisgraaf");
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
		
		plaatsEditGUI();
	}
	
	private void plaatsEditGUI() {
		// topPanel
		Component[] compTop = { stopEditButton, hgl(), titleEditLabel, hgl() };
		topEditPanel.add(hb(compTop));

		
		//bottomPanel
		Component[] compBottom = { okButton, hst(20), hgl() };
		bottomPanel.add(hb(compBottom));
				
		add(topEditPanel, BorderLayout.NORTH);
		add(graphEditPanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
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
	
	
	
	public void setEditMode(boolean b) {
		editMode = b;
	}
	
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==editButton) {
			
			remove(graphPanel);
			remove(topPanel);
			if(graphEditPanel==null)
				makeEditGUI();
			else {
				add(graphEditPanel, BorderLayout.CENTER);
				add(bottomPanel, BorderLayout.SOUTH);
				add(topEditPanel, BorderLayout.NORTH);
			}
			editMode = true;
		}
		if(e.getSource()==stopEditButton) {
			remove(graphEditPanel);
			remove(bottomPanel);
			remove(topEditPanel);
			if(graphPanel==null)
				makeGUI();
			else {
				graphPanel.setGraphNodes(graphEditPanel.getGraphNodes());
				graphPanel.setGraphEdges(graphEditPanel.getGraphEdges());
				add(graphPanel, BorderLayout.CENTER);
				add(topPanel, BorderLayout.NORTH);
			}
			editMode = false;
		}
		doLayout();
		repaint();
		((JFrame)SwingUtilities.getAncestorOfClass(JFrame.class,(Component)this)).pack();
	}

}
