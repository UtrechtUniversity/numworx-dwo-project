package fi.beans.mathkit;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.font.LineMetrics;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.SizeRequirements;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.CompositeView;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.Segment;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.Utilities;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.WrappedPlainView;
import javax.swing.text.Position.Bias;

class FormuleContext implements ViewFactory {

	static class MultiScriptsView extends SubSupView {

		public MultiScriptsView(Element elem) {
			super(elem);
		}
// FIXME Dit is compleet fout, maar werkt in het log(x,n) geval
		
                @Override
		protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets,
				int[] spans) {
			int span = (int)getView(0).getMinimumSpan(axis);
			spans[0] = span;
			targetSpan -= span;
			offsets[0] = targetSpan;
			int n = getViewCount();
			for(int i = 1; i < n; i++)
			{
				spans[i] = Math.min((int)getView(i).getMinimumSpan(axis), targetSpan);
				offsets[i] = targetSpan - spans[i];
			}
		}

	}

	public static class RowView extends FormuleView {

		public RowView(Element elem) {
			super(elem);
		}

                @Override
		protected SizeRequirements calculateMinorAxisRequirements(int axis,
				SizeRequirements r) {
			return baselineRequirements(axis, r);
		}

                @Override
		protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets,
				int[] spans) {
			baselineLayout(targetSpan, axis, offsets, spans);
		}

	}

	public static class SkipView extends NoneView { 
		public SkipView(Element elem) { super(elem); }
	}
	public static class NoneView extends View {

		public NoneView(Element elem) {
			super(elem);
		}

                @Override
		public float getPreferredSpan(int axis) {
			return 0;
		}

                @Override
		public Shape modelToView(int pos, Shape a, Bias b)
				throws BadLocationException {
			return new Rectangle();
		}

                @Override
		public void paint(Graphics g, Shape allocation) {
		}

                @Override
		public int viewToModel(float x, float y, Shape a, Bias[] biasReturn) {
			return 0;
		}

	}

	public static class RootView extends SupView {

		public RootView(Element elem) {
			super(elem);
		}

                @Override
		protected SizeRequirements calculateMajorAxisRequirements(int axis,
				SizeRequirements r) {
			r = super.calculateMajorAxisRequirements(axis, r);
			r.minimum += 5;
			r.preferred += 5;
			r.maximum += 5;
			return r;
		}

                @Override
		protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets,
				int[] spans) {
			int span0 = (int) getView(0).getMinimumSpan(axis);
			int span1 = (int) getView(1).getMinimumSpan(axis);
			offsets[0] = span1 + 5;
			offsets[1] = 0;
			spans[0] = span0;
			spans[1] = span1;
		
		}

                @Override
		public void paint(Graphics g, Shape allocation) {
			super.paint(g, allocation);
			g.setColor(getForeground());
			Rectangle b = allocation.getBounds();
			float f = getView(1).getMinimumSpan(BoxView.X_AXIS)-5;
			b.x += f;
			b.width -= f;
			int w = 10;
			int x1 = b.x;
			int y1 = b.y + b.height/2;
			int x2 = b.x + w/2;
			int y2 = b.y + b.height;
			g.drawLine(x1, y1, x2, y2);
			x1 = x2; y1 = y2;
			x2 = b.x + w;
			y2 = b.y;
			g.drawLine(x1, y1, x2, y2);
			x1 = x2; y1 = y2;
			x2 = b.x + b.width;
			g.drawLine(x1, y1, x2, y2);
			
			// TODO insert the mark at the end
		}


	}

	public static class SpaceView extends FormuleView {

		SpaceView(Element elem) {
			super(elem);
		}

	}
	
	public static class SubSupView extends SupView {

		public SubSupView(Element elem) {
			super(elem);
		}

                @Override
		protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets,
				int[] spans) {
			int n = getViewCount();
			float myalign = getAlignment(axis);
			float base = targetSpan * myalign;
			for (int i = 0; i < n; i++) {
			    View v = getView(i);
			    int min = (int) v.getMinimumSpan(axis);
			    if (min < targetSpan) {
				// can't make the child this wide, align it
			    	float align = v.getAlignment(axis);
			    	if(i == 0)
			    		offsets[i] = (int)(base - (min*align));
			    	else if(i>1)
			    		offsets[i] = (int) (base/2 - min*align);
			    	else 
			    		offsets[i] = (int) (((base) ));
			    	spans[i] = min;
			    } else {
				// make it the target width, or as small as it can get.
		               min = (int)v.getMinimumSpan(axis);
		               offsets[i] = 0;
		               spans[i] = Math.max(min, targetSpan);
			    }
			}
		    
		}

                @Override
		protected SizeRequirements calculateMajorAxisRequirements(int axis,
				SizeRequirements r) {
			// calculate tiled request
			float min = 0;
			int n = getViewCount();
			for (int i = 1; i < n; i++) {
			    View v = getView(i);
			    min = Math.max(min, v.getMinimumSpan(axis));
			}
			if(n > 0)
				min += getView(0).getMinimumSpan(axis);
			if (r == null) {
			    r = new SizeRequirements();
			}
			r.alignment = 0.5f;
			r.minimum = (int) min;
			r.preferred = (int) min;
			r.maximum = (int) min;
			return r;
		}

                @Override
		protected SizeRequirements calculateMinorAxisRequirements(int axis,
				SizeRequirements r) {
			View v = getView(0);
			float align = v.getAlignment(axis);
			float pref = 0;
			float pref1 = v.getPreferredSpan(axis);
			float asc = pref1 * align;
			float desc = pref1 - asc;
			
			int n = getViewCount();

			v = getView(1);

			if(n > 1)
				desc = Math.max(v.getPreferredSpan(axis), desc);
			
			for (int i = 2; i < n; i++) {
			    v = getView(i);
			    pref = Math.max((int) v.getPreferredSpan(axis), pref);
			}
			pref += asc/2 + desc;
			
			if (r == null) {
			    r = new SizeRequirements();
			}
		    r.alignment = (pref-desc)/pref;
			r.preferred = (int) pref;
			r.minimum = r.preferred;
			r.maximum = r.preferred;
			return r;
		}
                @Override
		protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets,
				int[] spans) {
			int span = (int)getView(0).getMinimumSpan(axis);
			offsets[0] = 0;
			spans[0] = span;
			targetSpan -= span;
			int n = getViewCount();
			for(int i = 1; i < n; i++)
			{
				offsets[i]= span;
				spans[i] = Math.min((int)getView(i).getMinimumSpan(axis), targetSpan);
			}
		}
		

	}

	private ViewFactory delegate;

	/**
	 * Constructs a set of styles to represent java lexical tokens. By default
	 * there are no colors or fonts specified.
	 */
	public FormuleContext() {
		super();
	}



	// --- ViewFactory methods -------------------------------------

	public FormuleContext(ViewFactory viewFactory) {
		this.delegate = viewFactory;
	}



        @Override
	public View create(Element elem) {
	    String kind = elem.getName();
	    if("none".equals(kind)||"mprescripts".equals(kind))
	    {
	    	return new NoneView(elem);
	    }
	    
	    
	    if ("math".equals(kind) ||
	    	"mtr".equals(kind)
	    )
	    {
	    	return new RowView(elem);
	    }
	    if ("mtd".equals(kind))
	    	return new TableDataView(elem);
	    if("munder".equals(kind))
	    	return new UnderView(elem);
	    if("mtable".equals(kind))
	    	return new TableView(elem);
	    if("mover".equals(kind))
	    	return new UnderOverView(elem,1);
	    if("munderover".equals(kind))
	    	return new UnderOverView(elem,2);
	    if("mfrac".equals(kind))
	    	return new FracView(elem);
	    if("mroot".equals(kind))
	    	return new RootView(elem);
	    if("mrow".equals(kind))
	    	return new RowView(elem);
	    if("msqrt".equals(kind))
	    	return new SqrtView(elem);
	    if("msup".equals(kind))
	    	return new SupView(elem);
	    if("msubsup".equals(kind)||"msub".equals(kind))
	    	return new SubSupView(elem);
	    if("mfenced".equals(kind))
	    	return new MfencedView(elem);
	    if("mmultiscripts".equals(kind))
	    	return new MultiScriptsView(elem);
	    if("mspace".equals(kind))
	    {
	    	return new SpaceView(elem);
	    }
	    
	    
		return delegate.create(elem);
	}

	static class UnderView extends FormuleView {

		public UnderView(Element elem) {
			super(elem, BoxView.Y_AXIS);
		}
                @Override
		protected SizeRequirements calculateMajorAxisRequirements(int axis,
				SizeRequirements r) {
			r = super.calculateMajorAxisRequirements(axis, r);
			View root = getView(0);
			float align = root.getAlignment(axis);
			float span  = root.getPreferredSpan(axis);
			r.alignment = span * align / r.preferred;
			return r;
		}
	}
	static class TableView extends FormuleView {

		private LineMetrics lm;

		public TableView(Element elem) {
			super(elem, BoxView.Y_AXIS);
		}

                @Override
		protected SizeRequirements calculateMajorAxisRequirements(int axis,
				SizeRequirements r) {
			r = super.calculateMajorAxisRequirements(axis, r);
//r.alignment: center at height of '-': strikethrough offset
			Container c = getContainer();
			if(c != null)
			{
				Font f = getFont();
				FontMetrics fm = c.getFontMetrics(f);
				Graphics g = c.getGraphics();
				if(g != null) {
					lm = fm.getLineMetrics("-", g);
					g.dispose();
				}
				if(lm  != null) {
					r.alignment = (r.preferred/2.0f - lm.getStrikethroughOffset()) / r.preferred;
				}
			}			
			return r;
		}
	}
	
	static class TableDataView extends RowView {

		public TableDataView(Element elem) {
			super(elem);
		}

                @Override
		protected SizeRequirements calculateMajorAxisRequirements(int axis,
				SizeRequirements r) {
			r = minimumRequirements(axis, r);
			try {
				
				View parent = this.getParent();
				int myCol = 0;
				for(int i = 0; i < parent.getViewCount(); i++)
					if(parent.getView(i)==this)
						myCol = i;
				
				parent = parent.getParent();
				int rows = parent.getViewCount();
				for(int row = 0; row < rows; row++)
				{
					View rowView = parent.getView(row);
					int cols = rowView.getViewCount();
					int col = myCol;
					//for(int col = 0; col < cols; col++)
					{
						View v = rowView.getView(col);
						if(v instanceof TableDataView)
						{
							SizeRequirements rr = ((TableDataView) v).minimumRequirements(axis, new SizeRequirements());
							r.minimum = Math.max(r.minimum, rr.minimum);
							r.maximum = Math.max(r.maximum, rr.maximum);
						}
					}
				}
				r.preferred = r.minimum;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return r;
		}

		SizeRequirements minimumRequirements(int axis, SizeRequirements r) {
			return super.calculateMajorAxisRequirements(axis, r);
		}
		
	}
	
	
	static class FracView extends TableView {
		FracView(Element elem)
		{
			super(elem);
			setInsets((short)0, (short)1, (short)0, (short)1);
		}

                @Override
		public void paint(Graphics g, Shape allocation) {
			super.paint(g, allocation);
			Shape shape = getChildAllocation(0, allocation);
			if(shape == null)
				return;
			String thickness = (String) getElement().getAttributes().getAttribute("linethickness");
			if("0".equals(thickness))
				return;
			g.setColor(getForeground());
			int y = shape.getBounds().y + shape.getBounds().height;
			int x1 = allocation.getBounds().x;
			int x2 = allocation.getBounds().width + x1;
			g.drawLine(x1, y, x2, y);			
		}

		/* (non-Javadoc)
		 * @see javax.swing.text.View#append(javax.swing.text.View)
		 */
                @Override
		public void append(View v) {
			if(getViewCount()<2)
				super.append(v);
		}

		/* (non-Javadoc)
		 * @see javax.swing.text.CompositeView#loadChildren(javax.swing.text.ViewFactory)
		 */
                @Override
		protected void loadChildren(ViewFactory f) {
		    	if (f == null) {
		    	    // No factory. This most likely indicates the parent view
		    	    // has changed out from under us, bail!
		    	    return;
		    	}
		    	Element e = getElement();
		    	int n = e.getElementCount();
		    	if (n > 0) {
		    	    View[] added = new View[n];
		    	    for (int i = 0; i < n; i++) {
		    	    if(i >= 2)
		    	    	added[i] = new SkipView(e.getElement(i));
		    	    else
		    	    	added[i] = f.create(e.getElement(i));
		    	    }
		    	    replace(0, 0, added);
		    	}
		}

		
		
	}
	static class UnderOverView extends FormuleView {
		private int over;

		UnderOverView(Element elem, int over)
		{
			super(elem, BoxView.Y_AXIS);
			this.over = over;
		}

                @Override
		protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets,
				int[] spans) {
			super.layoutMajorAxis(targetSpan, axis, offsets, spans);
			// put 'over' on top
			int o0 = offsets[0];
			int sp1 = spans[over];
			for(int i = 0; i < over; i++)
				offsets[i] = offsets[i] + sp1;
			offsets[over] = o0;
		}

                @Override
		protected SizeRequirements calculateMajorAxisRequirements(int axis,
				SizeRequirements r) {

			View root = getView(0);
			float align = root.getAlignment(axis);
			float span  = root.getPreferredSpan(axis);
			float top = getView(over).getPreferredSpan(axis);
			
			r = super.calculateMajorAxisRequirements(axis, r);
			float max = r.preferred;
			r.alignment = (top + span * align) / max;
			return r;
		}
		
	}
	
	public static class SupView extends FormuleView {
		
		SupView(Element elem)
		{
			super(elem);
		}

                @Override
		protected SizeRequirements calculateMinorAxisRequirements(int axis,
				SizeRequirements r) {
			View v = getView(0);
			float align = v.getAlignment(axis);
			float pref = 0;
			float pref1 = v.getPreferredSpan(axis);
			float asc = pref1 * align;
			float desc = pref1 - asc;
			
			int n = getViewCount();
			for (int i = 1; i < n; i++) {
			    v = getView(i);
			    pref = Math.max((int) v.getPreferredSpan(axis), pref);
			}
			pref = pref + asc/2 + desc;
			
			if (r == null) {
			    r = new SizeRequirements();
			}
		    r.alignment = (pref-desc)/pref;
			r.preferred = (int) pref;
			r.minimum = r.preferred;
			r.maximum = r.preferred;
			return r;
		}

                @Override
		protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets,
				int[] spans) {
			int n = getViewCount();
			float myalign = getAlignment(axis);
			float base = targetSpan * myalign;
			for (int i = 0; i < n; i++) {
			    View v = getView(i);
			    int min = (int) v.getMinimumSpan(axis);
			    if (min < targetSpan) {
				// can't make the child this wide, align it
			    	float align = v.getAlignment(axis);
			    	if(i == 0)
			    		offsets[i] = (int) (base - min*align);
			    	else
			    		offsets[i] = (int) (base/2 - min*align);
			    	spans[i] = min;
			    } else {
				// make it the target width, or as small as it can get.
		               min = (int)v.getMinimumSpan(axis);
		               offsets[i] = 0;
		               spans[i] = Math.max(min, targetSpan);
			    }
			}
		    
		}

                @Override
		protected void loadChildren(ViewFactory f) {
					if (f == null) {
					    // No factory. This most likely indicates the parent view
					    // has changed out from under us, bail!
					    return;
					}
					Element e = getElement();
					int n = e.getElementCount();
					if (n > 0) {
						ArrayList v = new ArrayList();
					    for (int i = 0; i < n; i++) {
							Element j = e.getElement(i);
		// only MATH content, or MATH block elements
							if(j.getName().equals("content"))
							{
								if(! j.getAttributes().isDefined(FormuleDocument.MATH)) 
								{	v.add(new SkipView(j)); // extra dummies nodig...
									continue;
								}
							}
							v.add(f.create(j));
					    }
					    View[] added = (View[]) v.toArray(new View[v.size()]);
					    replace(0, 0, added);
					}
				}
		
		
	}
	
	static class SqrtView extends RowView {
		SqrtView(Element elem)
		{
			super(elem);
			short top = getTopInset();
			short left = getLeftInset();
			short bottom = getBottomInset();
			short right = getRightInset();
			left += getSqrtWidth();
			top  += 2;
			setInsets(top, left, bottom, right);
		}

		private short getSqrtWidth() {	
			return 10;
		}

                @Override
		public void paint(Graphics g, Shape allocation) {			
			super.paint(g, allocation);
			g.setColor(getForeground());
			Rectangle b = allocation.getBounds();
			int w = getSqrtWidth();
			int x1 = b.x;
			int y1 = b.y + b.height/2;
			int x2 = b.x + w/2;
			int y2 = b.y + b.height;
			g.drawLine(x1, y1, x2, y2);
			x1 = x2; y1 = y2;
			x2 = b.x + w;
			y2 = b.y;
			g.drawLine(x1, y1, x2, y2);
			x1 = x2; y1 = y2;
			x2 = b.x + b.width;
			g.drawLine(x1, y1, x2, y2);
			
		}
		
	}
	

	/**
	 */
	static class FormuleView extends BoxView {

		/**
		 * Construct a simple colorized view of java text.
		 */
		FormuleView(Element elem) {
			super(elem, BoxView.X_AXIS);
		}

		FormuleView(Element elem, int axis) {
			super(elem, axis);
		}

                @Override
		protected SizeRequirements calculateMinorAxisRequirements(int axis,
				SizeRequirements r) {
			r = super.calculateMinorAxisRequirements(axis, r);
			r.maximum = r.preferred;
			return r;
		}

		/**
		 * Renders using the given rendering surface and area on that surface.
		 * This is implemented to invalidate the lexical scanner after rendering
		 * so that the next request to drawUnselectedText will set a new range
		 * for the scanner.
		 * 
		 * @param g
		 *            the rendering surface to use
		 * @param a
		 *            the allocated region to render into
		 * 
		 * @see View#paint
		 */
		public void paint0(Graphics g, Shape a) {
			super.paint(g, a);
			Rectangle r = a.getBounds();
			g.setColor(Color.RED);
			g.drawRect(r.x, r.y, r.width, r.height);
		}
	    /**
	     * Fetch the font that the glyphs should be based
	     * upon.  This is implemented to call
	     * <code>StyledDocument.getFont</code> if the associated
	     * document is a StyledDocument.  If the associated document
	     * is not a StyledDocument, the associated components font
	     * is used.  If there is no associated component, null 
	     * is returned.
	     */
	    public Font getFont() {
	    	Document doc = getDocument();
	    	if (doc instanceof StyledDocument) {
	    		AttributeSet attr = getAttributes();
	   			return ((StyledDocument)doc).getFont(attr);
	    	}
	    	Container c = getContainer();
	    	if (c != null) {
	    		return c.getFont();
	    	}
	    	return null;
	    }
	    /**
	     * Fetch the foreground color to use to render the
	     * glyphs.  If there is no foreground color, null should
	     * be returned.  This is implemented to call
	     * <code>StyledDocument.getBackground</code> if the associated
	     * document is a StyledDocument.  If the associated document
	     * is not a StyledDocument, the associated components foreground
	     * color is used.  If there is no associated component, null 
	     * is returned.
	     */
	    public Color getForeground() {
		Document doc = getDocument();
		if (doc instanceof StyledDocument) {
		    AttributeSet attr = getAttributes();
		    return ((StyledDocument)doc).getForeground(attr);
		}
		Container c = getContainer();
		if (c != null) {
		    return c.getForeground();
		}
		return null;
	    }

	}

	static class MfencedView extends RowView {
		
		String open, close;
		MfencedView(Element elem) {
			super(elem);
			short top = getTopInset();
			short left = getLeftInset();
			short bottom = getBottomInset();
			short right = getRightInset();
			left += 10;
			right += 10;
			setInsets(top, left, bottom, right);
			String str;
			AttributeSet attributes = getElement().getAttributes();
			if(!attributes.isDefined("open")) str = "(";
			else str = (String)attributes.getAttribute("open");
			open = str;
			if(!attributes.isDefined("close")) str = ")";
			else str = (String)attributes.getAttribute("close");
			close = str;
		}

		
                @Override
		protected SizeRequirements calculateMinorAxisRequirements(int axis,
				SizeRequirements r) {
			// TODO Auto-generated method stub
			r =  super.calculateMinorAxisRequirements(axis, r);
			Container c = getContainer();
			if(c != null)
			{
				Font f = getFont();
				int h = getFontMetrics(f).getHeight();
				int s = f.getSize();
//				AffineTransform trans;
//				trans = new AffineTransform(1, 0, 0, (r.preferred) / (float)h, 0, 0);
//				f = f.deriveFont(trans);
				f = (new Font( f.getFamily(), 0, s * (r.preferred) / h));
				FontMetrics fm = getFontMetrics(f);
				short left = (short) fm.stringWidth(open);
				short right = (short) fm.stringWidth(close);
				setInsets((short)0, left, (short)0, right);
			}
			return r;
		}


		private FontMetrics getFontMetrics(Font font)
		{
			FontMetrics result;
			Container c = getContainer();
			if(c != null)
				result = c.getFontMetrics(font);
			else
				result = java.awt.Toolkit.getDefaultToolkit().getFontMetrics(font);
			return result;
		}
		
                @Override
		public void paint(Graphics g, Shape a) {
			super.paint(g, a);
			g.setColor(getForeground());
			Rectangle r = a.getBounds();
			Font f = getFont();
			int x = r.x;
			
			int h = g.getFontMetrics().getHeight();
			int s = f.getSize();
//			AffineTransform trans;
//			trans = new AffineTransform(1, 0, 0, (r.height) / (float)h, 0, 0);
//			f = f.deriveFont(trans);
			f = new Font( f.getFamily(), 0, s * (r.height) / h);
			g.setFont(f);
			int y = (int) (r.y + g.getFontMetrics().getAscent());
			String str;
			AttributeSet attributes = getElement().getAttributes();
			h = g.getFontMetrics().getAscent();
			if(!attributes.isDefined("open")) str = "(";
			else str = (String)attributes.getAttribute("open");
			g.drawString(str, x, y);
			x += r.width-getRightInset();
			if(!attributes.isDefined("close")) str = ")";
			else str = (String)attributes.getAttribute("close");
			g.drawString(str, x, y);
		}
		
	}
	
}
