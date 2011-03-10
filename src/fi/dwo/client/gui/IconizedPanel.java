package fi.dwo.client.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class IconizedPanel extends JPanel implements ActionListener {

	private final CloseAction CLOSE_ACTION = new CloseAction();
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

	public void setIconBorder(Border b)
	{
		box.setBorder(b);
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
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final IconizedPanel ip = new IconizedPanel();
		Image img = ip.getToolkit().createImage(ip.getClass().getResource("/resources/iconized-bgimage.png"));
		Border border = new DWOBorder(img, null, 0, 1, 1, 80, 490, 500);
		Image menuimg = ip.getToolkit().createImage(ip.getClass().getResource("/resources/menu-bgimage.png"));
		Image imgs = ip.getToolkit().createImage(ip.getClass().getResource("/resources/sco-bgimage.png"));
		Border borderm = new DWOBorder(menuimg, new Insets(20,20,20,20), 20, 140, 159, 80, 490, 500);
		Border borders = new DWOBorder(imgs, new Insets(20,20,20,20), 59, 600, 643, 80, 490, 500);
		ip.setIconBorder(border);
		ip.setText("terug");
		JButton b = new JButton(ip.getCloseAction());
		JPanel p = new JPanel();
		p.setBorder(borderm);
		p.add(b);
		p.add(new JButton("äsdsadasdasd"));
		ip.setWindow(p);
		f.getContentPane().setBackground(Color.GREEN);
		f.getContentPane().add(ip, BorderLayout.WEST);
		JPanel panel = new JPanel();
		panel.setBackground(Color.PINK);
		panel.setBorder(borders);
		f.getContentPane().add(panel, BorderLayout.CENTER);
		f.setSize(400,200);
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
		{	layout.first(this);			
		}
		else
			layout.last(this);	
	}

	Component getWindow() {
		return window;
	}

	void setWindow(Component window) {
		add(window);
	}

	class CloseAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			setIconized(true);			
		}

		CloseAction() {
			super("<"); // super(string, icon ) 
		}
	}
	
	public Action getCloseAction() {
		return CLOSE_ACTION;
	}

}


