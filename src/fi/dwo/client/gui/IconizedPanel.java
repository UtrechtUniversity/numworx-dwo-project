package fi.dwo.client.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class IconizedPanel extends JPanel implements ActionListener {

	private boolean iconized;
	private JButton icon;
	private CardLayout layout;
	private Box box;
	private Component window;
	
	public IconizedPanel(String text) {
		super(new CardLayout());
		setOpaque(false);
		layout = (CardLayout) getLayout();
		box = Box.createVerticalBox();
		box.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		icon = new VButton(text);
		box.add(icon);
		box.add(Box.createVerticalGlue());
		add(box, "icon");	
		icon.addActionListener(this);
	}

	public IconizedPanel() {
		this("");	
	}
	
	public Component add(Component c)
	{
		if(window != null)
			remove(window);
		window = c;
		add(c, "window");
		layout.last(this);
		return c;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(f.EXIT_ON_CLOSE);
		final IconizedPanel ip = new IconizedPanel();
		ip.setText("terug");
		JButton b = new JButton("hoopla");
		b.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
					ip.setIconized(true);
			}});

		ip.add(b);
		f.getContentPane().setBackground(Color.GREEN);
		f.getContentPane().add(ip, BorderLayout.WEST);
		JPanel panel = new JPanel();
		panel.setBackground(Color.PINK);
		f.getContentPane().add(panel, BorderLayout.CENTER);
		System.out.println(ip.getPreferredSize());
		f.setSize(200,200);
		f.validate();
		f.setVisible(true);
	}

	public void setText(String string) {
		getIcon().setText(string);	
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == icon)
		{
			setIconized(false);
		}
		
	}

	JButton getIcon() {
		return icon;
	}

	
	
	void setIcon(JButton icon) {
		this.icon = icon;
	}

	boolean isIconized() {
		return iconized;
	}
	
	public Dimension getPreferredSize() {
		if(isIconized())
			return box.getPreferredSize();
		else
			return window.getPreferredSize();
	}

	void setIconized(boolean iconized) {
		this.iconized = iconized;
		if(iconized)
			layout.first(this);
		else
			layout.last(this);	
	}

	Component getWindow() {
		return window;
	}

	void setWindow(Component window) {
		add(window);
	}

}
