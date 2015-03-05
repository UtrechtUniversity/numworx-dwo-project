package fi.dwo.dwojapplet.gui.fullscreen;

import fi.dwo.dwojapplet.domain.ClassCourse;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.gui.CenterPanel;
import fi.dwo.dwojapplet.gui.CenterSubPanel;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;

public class FramedScoPanel extends JPanel implements CenterSubPanel, ActionListener {

    private CenterSubPanel csp;
    private CenterPanel center;
    private JButton btn;
    private Sco sco;
    private ClassCourse link;
    private Timer timer;
    protected FullScreenDWO screen;

    public FramedScoPanel(CenterSubPanel csp, Sco sco) {
        super();
        this.csp = csp;
        this.sco = sco;
        this.link = sco.getCourse().link;
        btn = new JButton("Start toets");
        btn.addActionListener(this);
        add(btn);
        Date notAfter = link.getNotAfter();
        if (notAfter != null) {
            System.out.println("stop na " + notAfter);
            long delay = notAfter.getTime() - System.currentTimeMillis() - User.getCurrentUser().getTimeZone();
            delay = Math.min(Integer.MAX_VALUE, Math.max(0L, delay));
            timer = new Timer((int) delay, this);
            timer.setRepeats(false);
            timer.start();
        }

    }

    public void end() {
        if (timer != null) {
            timer.stop();
        }
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

    @Override
    public void actionPerformed(ActionEvent e) {
        btn.setEnabled(false); // one shot?
        if (e.getSource() == timer) {
            if (screen != null) {
                screen.tearDown();
            }
            return;
        }
        final Frame f = JOptionPane.getFrameForComponent((Component) e.getSource());
        final JComponent component = csp.getComponent();
        component.setSize(getSize());
        component.setLocation(getLocationOnScreen());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                screen = FullScreenDWO.showInFrame(f, component);
                screen.setVisible(true); // modal dialog
                center.select(sco.getCourse());
            }
        });
    }

}
