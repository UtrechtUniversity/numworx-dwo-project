package fi.beans.mathkit;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Reader;
import java.io.StringReader;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JToolTip;
import javax.swing.plaf.ToolTipUI;
import javax.swing.plaf.basic.BasicToolTipUI;
import javax.swing.text.Document;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;


public class JMathToolTip extends JToolTip {

	static final private ToolTipUI MATH_UI = new MathToolTipUI();

	private static class MathToolTipUI extends BasicToolTipUI implements PropertyChangeListener
	{
		JToolTip  tip;
		TopView view;

                @Override
		public void installUI(JComponent c) {
	    	tip = (JToolTip) c;
	    	installDefaults(c);
	    	installListeners(c);
	    }

                @Override
		public void propertyChange(PropertyChangeEvent e) {
			if("tiptext".equals(e.getPropertyName()))
			{ 
				copy(e.getSource());
			}
		}

                @Override
		protected void installListeners(JComponent c)
		{
			c.addPropertyChangeListener(this);
		}

                @Override
		public Dimension getMaximumSize(JComponent c) {
			return getPreferredSize(c);
		}

                @Override
		public Dimension getPreferredSize(JComponent c) {
			copy(c);
			Dimension result = new Dimension();
			result.width = Math.round( view.getPreferredSpan(View.X_AXIS));
			result.height = Math.round(view.getPreferredSpan(View.Y_AXIS));
			Insets inset = c.getInsets();
			result.width += inset.left + inset.right;
			result.height+= inset.top + inset.bottom;
			return result;
		}

                @Override
		public Dimension getMinimumSize(JComponent c) {
			return getPreferredSize(c);
		}

                @Override
		public void paint(Graphics g, JComponent c) {
			copy(c);
			Rectangle r = c.getBounds();
			Insets inset = c.getInsets();
			r.x = inset.left; r.y = inset.top;
			r.width -= inset.left + inset.right;
			r.height-= inset.top + inset.bottom;
			view.paint(g, r);
		}

		static final MathKit kit = new MathKit();
		
		private void copy(Object c) {
			tip = (JToolTip) c;
			Document doc = kit.createDefaultDocument();
			Reader r = new StringReader(String.valueOf(tip.getTipText()));
			try {
			    kit.read(r, doc, 0);
			} catch (Throwable e) {
			}
			ViewFactory f = kit.getViewFactory();
			View hview = f.create(doc.getDefaultRootElement());
			view = new TopView(hview, f, tip);

		}
		
	}
	
	
	public JMathToolTip() {
		super();
	}

        @Override
	public void updateUI() {
		setUI(new MathToolTipUI());
	}

	
	public static void main(String[] args)
	{
		JMathPane panel = new JMathPane() {

                        @Override
			public JToolTip createToolTip() {
				return new JMathToolTip();
			}
			
		};
		JFrame frame = new JFrame("test frame");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		panel.setText("<html><p>base<math><mfenced open='{' close='' ><mtable><mtr><mtd>ja</mtd><mtd> if a>2</mtd></mtr><mtr><mtd>nee </mtd><mtd> </mtd></mtr></mtable></mfenced></math>" +
				"<p>base<math><mover><mi>AB</mi><mo>\u2192</mo></mover></math>line <math><munder><mi>y</mi><mi>x</mi></munder></math> en <math><munderover><mi>base</mi><mi>under</mi><mi>over</mi></munderover></math>" +
				"<p>lang wrap en meer en <math><mfrac><mrow>aap</mrow><mrow>noot</mrow></mfrac>||<mtable><mrow>aap</mrow><mrow>noot</mrow></mtable></math>meer en meer en meer en zo door..." +
				"<p>base<math><mo>-</mo><mfrac><mn>1</mn><mn>2</mn></mfrac></math>" +
				"<p><math><msub><mi>e</mi><mrow><mi>\u03c0</mi>" +
				"<mo>-</mo><mfrac><mn>1</mn><mn>2</mn></mfrac></mrow></msub></html>");

//		panel.setText("<html><p>--<math><mfrac><mtext>aap</mtext><mtext>no-ot</mtext></mfrac></math>-<math><mfrac><mrow>aap</mrow><mrow>no-ot</mrow></mfrac></math>-</html>");
		panel.setToolTipText(panel.getText());
		frame.setSize(100,100);
		frame.setVisible(true);
		LineMetrics lm = frame.getFontMetrics(frame.getFont()).getLineMetrics("x", frame.getGraphics());
		System.out.println(lm.getStrikethroughOffset());
		System.out.println(lm.getAscent());
		Rectangle2D r = frame.getFontMetrics(frame.getFont()).getStringBounds("-",frame.getGraphics());
		System.out.println(r);
	}
	
}
