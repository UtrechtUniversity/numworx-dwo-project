package fi.beans.numworxlf;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;

public class NumworxTreeUI extends BasicTreeUI implements Constants {
    public static ComponentUI createUI( JComponent c )
    {
      return new NumworxTreeUI();
    }

    class ExpandedIcon implements Icon {
    	final boolean expand;
    	
		private ExpandedIcon(boolean expand) {
			this.expand = expand;
		}

		@Override
		public void paintIcon(Component c, Graphics gr, int x, int y) {
			Graphics2D g = (Graphics2D)gr;
		    ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		    ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
			int width = 8;
			int height = 8;
			x += 5;
			y += 6;
			g.setColor(tree.getBackground());
			
			g.fillOval(x, y, width, height);
			g.setColor(getHashColor());
			g.drawOval(x, y, width, height);
			if (expand)
				g.drawLine(x+4, y+8, x+4, y+13);
			else
				g.drawLine(x+8, y+4, x+13,  y+4);
			
		}

		@Override
		public int getIconWidth() {
			return 20;
		}

		@Override
		public int getIconHeight() {
			return 20;
		}
    	
    }
    
    static {
    	UIDefaults defs = UIManager.getDefaults();
    	defs.put("Tree.paintLines", Boolean.TRUE);
    	defs.put("Tree.lineTypeDashed", Boolean.TRUE);    	
    }
    
    
	/* (non-Javadoc)
	 * @see javax.swing.plaf.basic.BasicTreeUI#installDefaults()
	 */
	@Override
	protected void installDefaults() {
		super.installDefaults();
		setHashColor(colorBlue1);
		setExpandedIcon(new ExpandedIcon(true));
		setCollapsedIcon(new ExpandedIcon(false));
	}

}
