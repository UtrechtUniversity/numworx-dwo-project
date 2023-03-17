package nl.numworx.gwtpatch.client;

class Add extends DiffItem {

	public Add(String pointer, Object object) {
		setOp("add");
		setValue(object);
		setPath(pointer);
	}

}
