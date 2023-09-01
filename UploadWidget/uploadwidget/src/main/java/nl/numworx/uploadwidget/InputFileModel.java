package nl.numworx.uploadwidget;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;

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
}
