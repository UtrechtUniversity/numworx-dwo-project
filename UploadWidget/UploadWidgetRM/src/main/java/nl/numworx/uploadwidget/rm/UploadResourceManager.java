package nl.numworx.uploadwidget.rm;

import org.cbook.cbookif.rm.ResourceContainer;
import org.cbook.cbookif.rm.ResourceManager;

public class UploadResourceManager implements ResourceManager {

	public UploadResourceManager() {
	}

	@Override
	public ResourceContainer getInstanceContainer() {
		return new UploadInstanceContainer();
	}

	@Override
	public ResourceContainer getStudentContainer() {
		return null;
	}

	@Override
	public ResourceContainer getUnitContainer() {
		return null;
	}

	@Override
	public ResourceContainer getWidgetContainer() {
		return null;
	}

}
