package nl.numworx.uploadwidget.rm;

import org.cbook.cbookif.rm.ResourceManager;

import fi.beans.wiskopdrbeans.ResourceManagerClient.ResourceManagerFactory;

public class Factory implements ResourceManagerFactory {

	public Factory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ResourceManager getResourceManager() {
		return new UploadResourceManager();
	}

}
