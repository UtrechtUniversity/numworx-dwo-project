package fi.dwo.client.gui;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;

import fi.dwo.client.domain.Sco;

public class FramedScoPanel extends JPanel implements CenterSubPanel, ActionListener {

	private CenterSubPanel csp;
	private CenterPanel center;
	private JButton btn;
	private Sco sco;
	
	public FramedScoPanel(CenterSubPanel csp, Sco sco) {
		super();
		this.csp = csp;
		this.sco = sco;
		btn = new JButton("Start toets");
		btn.addActionListener(this);
		add(btn);
	}

	public void end() {
		csp.end();
	}

	public JComponent getComponent() {
		return this;
	}

	public Component getHeaderPanel() {
		return csp.getHeaderPanel();
	}

	public Object getUserObject() {
		return csp.getUserObject();
	}

	public void setCenterPanel(CenterPanel centerPanel) {
		this.center = centerPanel;
		csp.setCenterPanel(centerPanel); // is dit wel goed?
	}

	public void stateChanged(ChangeEvent e) {
		csp.stateChanged(e);
	}

	public void actionPerformed(ActionEvent e) {
		//btn.setEnabled(false); // one shot?
		Frame f = JOptionPane.getFrameForComponent((Component) e.getSource());
		JDialog d = new JDialog(f, true);
		d.setContentPane(csp.getComponent());
		d.setSize(800,600);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.show();
		center.select(sco.getCourse());
	}

}
