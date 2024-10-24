package fi.dwo.dwojapplet.gui.domainmodel;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelVariant;

public class WrappedSet extends AbstractSet<DomStudentModelVariant> implements SortedSet<DomStudentModelVariant> {

	private final List<DomStudentModelVariant> wrap;

	public WrappedSet(List<DomStudentModelVariant> variants) {
		this.wrap = variants;
	}

	@Override
	public synchronized boolean add(DomStudentModelVariant e) {
		if (wrap.contains(e)) return false;
		wrap.add(e); Collections.sort(wrap);
		return true;
	}

	@Override
	public synchronized boolean addAll(Collection<? extends DomStudentModelVariant> c) {
		boolean result = false;
		for(DomStudentModelVariant e: c) {
			result |= add(e);
		}
		return false;
	}

	@Override
	public Iterator<DomStudentModelVariant> iterator() {
		return wrap.iterator();
	}

	@Override
	public int size() {
		return wrap.size();
	}

	@Override
	public void clear() {
		wrap.clear();
	}

	@Override
	public synchronized boolean remove(Object o) {
		return wrap.remove(o);
	}

	@Override
	public Comparator<? super DomStudentModelVariant> comparator() {
		return null; // natural ordening
	}

	@Override
	public synchronized DomStudentModelVariant first() {
		return wrap.get(0);
	}

	@Override
	public SortedSet<DomStudentModelVariant> headSet(DomStudentModelVariant toElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized DomStudentModelVariant last() {
		return wrap.get(wrap.size()-1);
	}

	@Override
	public SortedSet<DomStudentModelVariant> subSet(DomStudentModelVariant fromElement,
			DomStudentModelVariant toElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SortedSet<DomStudentModelVariant> tailSet(DomStudentModelVariant fromElement) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
