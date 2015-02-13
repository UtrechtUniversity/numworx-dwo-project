package fi.beans.mathkit;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Shape;

import javax.swing.JToolTip;
import javax.swing.text.BadLocationException;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.Position.Bias;

public class TopView extends View 
{
	ViewFactory f;
	View view;
	Container tip;
	
	public TopView(View view, ViewFactory f, Container tip) {
		super(view.getElement());
		this.view = view;
		this.f = f;
		this.tip = tip;
		view.setParent(this);
		view.setSize(100,100);
		view.setSize(getPreferredSpan(X_AXIS), getPreferredSpan(Y_AXIS));
	}
	
	public float getPreferredSpan(int axis) {
		return view.getPreferredSpan(axis);
	}
	public Shape modelToView(int pos, Shape a, Bias b)
			throws BadLocationException {
		return view.modelToView(pos, a, b);
	}
	public void paint(Graphics g, Shape allocation) {
		view.paint(g, allocation);
	}
	public int viewToModel(float x, float y, Shape a, Bias[] biasReturn) {
		return view.viewToModel(x, y, a, biasReturn);
	}
// the chain ends here...
	public ViewFactory getViewFactory() {
		return f;
	}

	public Container getContainer() {
		return tip;
	}
	
	
}