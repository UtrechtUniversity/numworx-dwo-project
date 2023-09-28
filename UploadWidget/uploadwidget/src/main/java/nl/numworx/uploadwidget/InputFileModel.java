package nl.numworx.uploadwidget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.AbstractListModel;

import org.cbook.cbookif.rm.ResourceContainer;

import fi.beans.wiskopdrbeans.ResourceManagerClient.ResourceManagerFactory;

@SuppressWarnings("serial")
public class InputFileModel extends AbstractListModel<InputFile> {

	List<InputFile> inputs = new ArrayList<>();
		
	public InputFileModel() {
	}

	@Override
	public int getSize() {
		return inputs.size();
	}

	@Override
	public InputFile getElementAt(int index) {
		return inputs.get(index);
	}

	public boolean add(InputFile in) {
		int pos = getSize(); // at end for now
		boolean add = inputs.add(in);
		if (add) {
			fireIntervalAdded(this, pos, pos);
		}
		return add;
	}
	
	public InputFile remove(InputFile in) {
		int i = inputs.indexOf(in);
		if (i < 0) return null;
		InputFile remove = inputs.remove(i);
		fireIntervalRemoved(this, i, i);
		return remove;
	}
	
	public List<Map<String,Object>> getLaunchData(ResourceManagerFactory rmf) {
		if (getSize() == 0)
			return Collections.emptyList();
		ResourceContainer rc = rmf.getResourceManager().getInstanceContainer();
		List<InputFile> inputs = new ArrayList<>(this.inputs);
		Iterator<InputFile> i = inputs.iterator();
		while(i.hasNext()) {
			try {
				i.next().persist(rc);
			} catch(Exception ioe) {
				i.remove();
			}
		}
		return inputs.stream().map(InputFile::toMap).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	public void setLaunchData(Object state) { // memento pattern
		setLaunchData( (List<Map<String,Object>>) state); 
	}
	
	public void setLaunchData(List<Map<String,Object>> state) {
		int s = inputs.size();
		inputs.clear();
		if (s > 0) fireIntervalRemoved(this, 0, s-1);
		if (state.isEmpty()) return;
		state.forEach(item -> inputs.add(new InputFile(item)));
		fireIntervalAdded(this, 0, getSize()-1);
	}
}
