package nl.uu.fi.dwo.mobile.client.ui;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import lx.interaction.dollar.Dollar;
import lx.interaction.dollar.DollarListener;
import lx.interaction.dollar.Point;
import lx.interaction.touch.MGWTTouchHandler;
import lx.interaction.touch.TouchListener;
import nl.uu.fi.dwo.mobile.client.ui.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.mobile.client.ui.formuleobjects.FormuleTeken;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.googlecode.mgwt.dom.client.event.touch.TouchHandler;
import com.googlecode.mgwt.ui.client.widget.touch.TouchDelegate;

public class DigitsPanel_oud extends HorizontalPanel implements TouchListener, DollarListener
{

	private static final Object DELETE = "delete";
	private Canvas canvas;
	private TouchDelegate delegate;
	private Dollar dollar = new Dollar(Dollar.GESTURES_DWOPLAYER);
	private TextBox box;
	private int width;
	private int height;
	private List<Point> current = new ArrayList<Point>();
	private FormuleKeyboard kb;

	public DigitsPanel_oud(FormuleKeyboard formuleKeyboard)
	{
		super();
		kb = formuleKeyboard;
		getElement().getStyle().setBackgroundColor("lightgray");
		setSize("100%", "250px");
		initialize(400, 240);
		add(canvas);
		box = new TextBox();
		box.setText("nothing");
		add(box);
		dollar.setListener(this);
		dollar.setActive(true);
		draw();
	}

	private void initialize(int width, int height)
	{
		this.width = width;
		this.height = height;
		canvas = Canvas.createIfSupported();
		canvas.setWidth(width + "px");
		canvas.setHeight(height + "px");
		canvas.setCoordinateSpaceWidth(width);
		canvas.setCoordinateSpaceHeight(height);
		delegate = new TouchDelegate(canvas);

		TouchHandler handler = new MGWTTouchHandler(this, canvas);
		delegate.addTouchHandler(handler);
	}

	@Override
	public void pointerDragged(int x, int y)
	{
		addPoint(x, y);
		dollar.pointerDragged(x, y);
	}

	@Override
	public void pointerPressed(int x, int y)
	{
		dollar.pointerPressed(x, y);
		current.clear();
		addPoint(x, y);

	}

	@Override
	public void pointerReleased(int x, int y)
	{
		addPoint(x, y);

		dumpPoints();

		try
		{
			dollar.pointerReleased(x, y);
		}
		catch (Exception e)
		{
			box.setText(e.toString());
		}
	}

	private void dumpPoints()
	{
		final PrintStream out = System.out;
		for (Point p : current)
		{
			out.println("\t" + Math.round(p.X) + ", " + Math.round(p.Y) + ",");
		}
		out.println();
		out.println();

	}

	@Override
	public void dollarDetected(Dollar dollar)
	{
		box.setText("detected " + dollar.getName() + " " + dollar.getScore());
		if (dollar.getIndex() >= 0)
		{
			String text = dollar.getName();
			FormuleEditor editor = kb.getEditor();
			if (text.length() == 1 && editor != null)
			{
				char ch = text.charAt(0);
				editor.addElement(new FormuleTeken(editor.getCurrentRegel(), ch));
			}
			else if (DELETE.equals(text))
			{
				editor.removeCurrentElement();
			}
		}
	}

	public void drawStroke(Context2d g, List<Point> list)
	{
		if (list.isEmpty())
			return;
		g.beginPath();
		Point p = list.get(0);
		g.moveTo(p.X, p.Y);
		for (Point next : list)
		{
			g.lineTo(next.X, next.Y);
		}
		g.stroke();
	}

	protected Point addPoint(int x, int y)
	{
		Point p = new Point(x, y);
		current.add(p);
		draw();
		return p;
	}

	/**
	 * 
	 */
	protected void draw()
	{
		// clear 
		Context2d context2d = canvas.getContext2d();
		context2d.setFillStyle("white");
		context2d.fillRect(0, 0, width, height);
		// draw
		drawStroke(context2d, current);
	}

}
