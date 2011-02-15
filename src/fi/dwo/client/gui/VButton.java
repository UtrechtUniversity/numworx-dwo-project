package fi.dwo.client.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.awt.image.WritableRaster;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

public class VButton extends JButton {

	public int getHeight() {
		return hor ? super.getWidth(): super.getHeight();
	}

	public int getWidth() {
		return hor ? super.getHeight(): super.getWidth();
	}
	
	public Dimension getSize() 
	{
		Dimension size = super.getSize();
		if(hor)
		{
			size.width = super.getHeight();
			size.height = super.getWidth();
		}
		return size;
	}
	
	public static class RotateFilter extends ImageFilter {

	    private static ColorModel defaultRGB = ColorModel.getRGBdefault();

	    private double angle;
	    private double sin;
	    private double cos;
	    private double coord[] = new double[2];

	    private int raster[];
	    private int xoffset, yoffset;
	    private int srcW, srcH;
	    private int dstW, dstH;

	    public RotateFilter(double angle) {
	        this.angle = angle;
	        sin = Math.sin(angle);
	        cos = Math.cos(angle);
	    }

	    public void transform(double x, double y, double[] retcoord) {
	        // Remember that the coordinate system is upside down so apply
	        // the transform as if the angle were negated.
	        // cos(-angle) =  cos(angle)
	        // sin(-angle) = -sin(angle)
	        retcoord[0] = cos * x + sin * y;
	        retcoord[1] = cos * y - sin * x;
	    }

	    public void itransform(double x, double y, double[] retcoord) {
	        // Remember that the coordinate system is upside down so apply
	        // the transform as if the angle were negated.  Since inverting
	        // the transform is also the same as negating the angle, itransform
	        // is calculated the way you would expect to calculate transform.
	        retcoord[0] = cos * x - sin * y;
	        retcoord[1] = cos * y + sin * x;
	    }

	    public void transformBBox(Rectangle rect) {
	        double minx = Double.POSITIVE_INFINITY;
	        double miny = Double.POSITIVE_INFINITY;
	        double maxx = Double.NEGATIVE_INFINITY;
	        double maxy = Double.NEGATIVE_INFINITY;
	        for (int y = 0; y <= 1; y++) {
	            for (int x = 0; x <= 1; x++) {
	                transform(rect.x + x * rect.width,
	                          rect.y + y * rect.height,
	                          coord);
	                minx = Math.min(minx, coord[0]);
	                miny = Math.min(miny, coord[1]);
	                maxx = Math.max(maxx, coord[0]);
	                maxy = Math.max(maxy, coord[1]);
	            }
	        }
	        rect.x = (int) Math.floor(minx);
	        rect.y = (int) Math.floor(miny);
	        rect.width = (int) Math.ceil(maxx) - rect.x + 1;
	        rect.height = (int) Math.ceil(maxy) - rect.y + 1;
	    }

	    public void setDimensions(int width, int height) {
	        Rectangle rect = new Rectangle(0, 0, width, height);
	        transformBBox(rect);
	        xoffset = -rect.x;
	        yoffset = -rect.y;
	        srcW = width;
	        srcH = height;
	        dstW = rect.width;
	        dstH = rect.height;
	        raster = new int[srcW * srcH];
	        consumer.setDimensions(dstW, dstH);
	    }

	    public void setColorModel(ColorModel model) {
	        consumer.setColorModel(defaultRGB);
	    }

	    public void setHints(int hintflags) {
	        consumer.setHints(TOPDOWNLEFTRIGHT
	                          | COMPLETESCANLINES
	                          | SINGLEPASS
	                          | (hintflags & SINGLEFRAME));
	    }

	    public void setPixels(int x, int y, int w, int h, ColorModel model,
	                          byte pixels[], int off, int scansize) {
	        int srcoff = off;
	        int dstoff = y * srcW + x;
	        for (int yc = 0; yc < h; yc++) {
	            for (int xc = 0; xc < w; xc++) {
	                raster[dstoff++] = model.getRGB(pixels[srcoff++] & 0xff);
	            }
	            srcoff += (scansize - w);
	            dstoff += (srcW - w);
	        }
	    }

	    public void setPixels(int x, int y, int w, int h, ColorModel model,
	                          int pixels[], int off, int scansize) {
	        int srcoff = off;
	        int dstoff = y * srcW + x;
	        if (model == defaultRGB) {
	            for (int yc = 0; yc < h; yc++) {
	                System.arraycopy(pixels, srcoff, raster, dstoff, w);
	                srcoff += scansize;
	                dstoff += srcW;
	            }
	        } else {
	            for (int yc = 0; yc < h; yc++) {
	                for (int xc = 0; xc < w; xc++) {
	                    raster[dstoff++] = model.getRGB(pixels[srcoff++]);
	                }
	                srcoff += (scansize - w);
	                dstoff += (srcW - w);
	            }
	        }
	    }

	    public void imageComplete(int status) {
	        if (status == IMAGEERROR || status == IMAGEABORTED) {
	            consumer.imageComplete(status);
	            return;
	        }
	        int pixels[] = new int[dstW];
	        for (int dy = 0; dy < dstH; dy++) {
	            itransform(0 - xoffset, dy - yoffset, coord);
	            double x1 = coord[0];
	            double y1 = coord[1];
	            itransform(dstW - xoffset, dy - yoffset, coord);
	            double x2 = coord[0];
	            double y2 = coord[1];
	            double xinc = (x2 - x1) / dstW;
	            double yinc = (y2 - y1) / dstW;
	            for (int dx = 0; dx < dstW; dx++) {
	                int sx = (int) Math.round(x1);
	                int sy = (int) Math.round(y1);
	                if (sx < 0 || sy < 0 || sx >= srcW || sy >= srcH) {
	                    pixels[dx] = 0;
	                } else {
	                    pixels[dx] = raster[sy * srcW + sx];
	                }
	                x1 += xinc;
	                y1 += yinc;
	            }
	            consumer.setPixels(0, dy, dstW, 1, defaultRGB, pixels, 0, dstW);
	        }
	        consumer.imageComplete(status);
	    }
	}

	static Dimension swap(Dimension size) {
		int tmp = size.width;
		size.width = size.height;
		size.height = tmp;
		return size;
	}
	static final ImageFilter filter = new RotateFilter(Math.PI/2);

	boolean hor; 		// Hack, schadow button is horizontal
	
	
	public static class UI extends ButtonUI {

		static final Color UIFOREGROUND = new Color(0,0,200);
		private ButtonUI ui;

		public UI(ButtonUI ui) {
			this.ui = ui;
		}

		public void installUI(JComponent c) {
			ui.installUI(c);
			installDefaults((AbstractButton) c);
		}

		protected void installDefaults(AbstractButton b) {
			
			b.setHorizontalTextPosition(SwingConstants.LEADING);
			//b.setForeground(UIFOREGROUND);
			//b.setDoubleBuffered(false);
			b.setIconTextGap(15);
			b.setFocusPainted(false);
		}

		public Dimension getMaximumSize(JComponent c) {
			return swap(ui.getMaximumSize(c));
		}

		public Dimension getMinimumSize(JComponent c) {
			return swap(ui.getMinimumSize(c));
		}

		public Dimension getPreferredSize(JComponent c) {
// c.getSize() relevant?
			return swap(ui.getPreferredSize(c));
		}

		public void paint(Graphics g, JComponent c) {
			VButton v = (VButton) c;
			Image img = c.createImage(c.getHeight(), c.getWidth());			
			boolean h = v.hor;//c.setSize(swap(c.getSize()));
			v.hor = true;
			ui.paint(img.getGraphics(), c);
			v.hor = h; // c.setSize(swap(c.getSize()));
	        ImageProducer producer = new FilteredImageSource(
                    img.getSource(),
                    filter);
	        img = c.createImage(producer);
			g.drawImage(img, 0, 0, null);
		}


	}

	public void updateUI() {
		super.updateUI();
		setUI(new UI(getUI()));
	}
	
	
	public static class V implements Icon {

		static final int SIZE = 8;
		static final BasicStroke STROKE = new BasicStroke(2.0f);

		public int getIconHeight() {
			return SIZE;
		}

		public int getIconWidth() {
			return SIZE;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			if(g instanceof Graphics2D)
			{
				Graphics2D g2 = (Graphics2D)g;
				g2.setStroke(STROKE);
			}
			g.setColor(c.getForeground());
			g.drawLine(x, y, x+SIZE/2, y+SIZE/2);
			g.drawLine(x+SIZE, y, x+SIZE/2, y+SIZE/2);
		}
	}

	
	
	public VButton() {
	}

	public VButton(Icon icon) {
		super(icon);
	}

	public VButton(String text) {
		super(text);
	}

	public VButton(Action a) {
		super(a);
	}

	public VButton(String text, Icon icon) {
		super(text, icon);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		javax.swing.JFrame frame = new JFrame("test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		VButton button = new VButton("Knoppen", new VButton.V() );
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(e);			
			} });
		frame.getContentPane().add(button);
		frame.getContentPane().setLayout(new FlowLayout());
		frame.pack();
		frame.show();
	}

}
