package fi.dwo.dwojapplet.gui;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class IdleDetect implements AWTEventListener, Runnable  {

  public interface IdleListener {
    void onIdle(IdleEvent ev);
  }
  
  static public class IdleEvent extends EventObject {

    final int cnt;
    public IdleEvent(Object source, int cnt) {
      super(source);
      this.cnt = cnt;
    }
 
    public boolean isSlow() {
      return cnt >= SLOW;
    }

    public int getCnt( ) { return cnt; }

    @Override
    public String toString() {
      return "IdleEvent[cnt=" + cnt + "]";
    }
    
  }
  
  final static int FAST = 2;
  final static int SLOW = 90;
  
  final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
  final List<IdleListener> listeners = new Vector<>();
  
  private IdleDetect() {   
  }
  
  public static final IdleDetect instance = new IdleDetect();
  
  final static long eventMask = AWTEvent.MOUSE_MOTION_EVENT_MASK + AWTEvent.MOUSE_EVENT_MASK + AWTEvent.MOUSE_WHEEL_EVENT_MASK + AWTEvent.INPUT_METHOD_EVENT_MASK + AWTEvent.KEY_EVENT_MASK;
  
  int cnt;
  private ScheduledFuture<?> reg;
  
  public void start() {
    stop();
    Toolkit.getDefaultToolkit().addAWTEventListener(this, eventMask);
    reg = scheduler.scheduleAtFixedRate(this, 10, 10, TimeUnit.SECONDS);
  }

  public void stop() {
    Toolkit.getDefaultToolkit().removeAWTEventListener(this);
    if (reg != null) { reg.cancel(false); reg = null; }
  }
  
  public void reset() {
    cnt = 0;
  }
  
  @Override
  public void eventDispatched(AWTEvent event) {
    reset();    
  }

  @Override
  public void run() {
    cnt ++;
    if (cnt <= FAST) fire();
    else if (cnt >= SLOW) { fire(); reset(); }
  }

  private void fire() {
    IdleEvent ev = new IdleEvent(this, cnt);
    ArrayList<IdleListener> copy;
    synchronized (listeners) {copy = new ArrayList<>(listeners);}
    SwingUtilities.invokeLater( () -> {
      copy.forEach(l -> l.onIdle(ev));
    });   
  }
  
  public void addIdleListener(IdleListener l) {
    if (!listeners.contains(l))
      listeners.add(l);
  }
  
  public void removeIdleListener(IdleListener l) {
    listeners.remove(l);
  }
  
  
  // TESTING 
  
//  public static void main(String...args) {
//    JFrame f = new JFrame("test");
//    f.setContentPane(new JPanel());
//    JButton exit = new JButton("exit"); f.getContentPane().add(exit);
//    JTextField field = new JTextField("test"); f.getContentPane().add(field);
//    exit.addActionListener(ev -> instance.stop());
//    field.addActionListener(ev -> System.out.println(ev.getActionCommand()));
//    instance.addIdleListener(ev -> System.out.println(ev));
//    f.pack();
//    f.show();
//    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    instance.start();
//  }
 
  
}
