package nl.numworx.gwtpatch.client;

import java.util.ArrayList;
import java.util.List;

import nl.numworx.gwtpatch.client.GWTPatch.Builder;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;

class DiffFactory {

	private List<DiffItem> diffs = new ArrayList<DiffItem>();
	private Builder builder;
	
	public DiffFactory(GWTPatch.Builder builder) {
		this.builder = builder;
	}

	ObjectList getPatches() {
		List<Object> patches = builder.createList(diffs.size());
		for(DiffItem item : diffs) {
			patches.add(item.toObjectMap(builder));
		}
		return builder.toObjectList(patches);
	}

	void addModify(String pointer, Object now) {
		DiffItem item = new Modify(pointer, now);
		diffs.add(item);
	}

	 void addRemove(String pointer) {
		DiffItem item = new Remove(pointer);
		diffs.add(item);
	}

	 void addAdd(String pointer, Object object) {
		 DiffItem item = new Add(pointer, object);
		 diffs.add(item);
	}
	
}
