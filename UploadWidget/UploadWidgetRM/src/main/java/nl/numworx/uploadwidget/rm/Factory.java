package nl.numworx.uploadwidget.rm;

import org.cbook.cbookif.CBookContext;
import org.cbook.cbookif.rm.ResourceManager;

import fi.beans.wiskopdrbeans.ResourceManagerClient.ResourceManagerFactory;

public class Factory implements ResourceManagerFactory {
	
	private CBookContext context;

	public Factory() {
	}

	@Override
	public ResourceManager getResourceManager() {
		return new UploadResourceManager(context);
	}

	/**
	 * @return the context
	 */
	public CBookContext getContext() {
		return context;
	}

	/**
	 * @param context the context to set.
	 * Part of ResourceManagerFactorySPI
	 */
	public void setContext(CBookContext context) {
		this.context = context;
	}

}
