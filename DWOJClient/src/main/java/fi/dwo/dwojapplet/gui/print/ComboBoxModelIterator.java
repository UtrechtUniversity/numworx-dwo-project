package fi.dwo.dwojapplet.gui.print;

import java.awt.print.Printable;
import java.util.Iterator;

import javax.swing.ComboBoxModel;

public class ComboBoxModelIterator<T> implements Iterable<Printable> {

	private ComboBoxModel<T> model;
	private Printable component;
	
	class Impl implements Iterator<Printable> {

		int item = -1;
		
		@Override
		public boolean hasNext() {
			return (item+1) < model.getSize();
		}

		@Override
		public Printable next() {
			item ++;
			T element = model.getElementAt(item);
			model.setSelectedItem(element);
			return component;
		}

	}

	public ComboBoxModelIterator(ComboBoxModel<T> model, Printable component) {
		this.model = model;
		this.component = component;
	}

	@Override
	public Iterator<Printable> iterator() {
		return new Impl();
	}

}
