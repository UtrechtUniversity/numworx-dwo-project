package nl.numworx.uploadwidget.rm;

import org.cbook.cbookif.CBookContext;
import org.cbook.cbookif.rm.ResourceContainer;
import org.cbook.cbookif.rm.ResourceManager;

public class UploadResourceManager implements ResourceManager {

	private CBookContext context;

	public UploadResourceManager(CBookContext context) {
		this.context = context;
	}

	@Override
	public ResourceContainer getInstanceContainer() {
		return new UploadInstanceContainer(context);
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
