package nl.numworx.uploadwidget.module;

import org.cbook.cbookif.CBookContext;
import org.cbook.cbookif.CBookWidgetEditIF;
import org.cbook.cbookif.CBookWidgetInstanceIF;
import org.cbook.cbookif.Constants;
import org.cbook.cbookif.rm.ResourceManager;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import fi.beans.wiskopdrbeans.InteractieEditPanel;
import fi.beans.wiskopdrbeans.InteractiePanel;
import nl.numworx.uploadwidget.Editor;
import nl.numworx.uploadwidget.Upload;
import nl.numworx.uploadwidget.UploadInteractieEditPanel;
import nl.numworx.uploadwidget.UploadInteractiePanel;

@Module
public abstract class Modules {
	@Binds abstract CBookWidgetInstanceIF upload(Upload upload);
	@Binds abstract CBookWidgetEditIF editor(Editor editor);
	@Binds abstract InteractiePanel interactiePanel(UploadInteractiePanel panel);
	@Binds abstract InteractieEditPanel interactieEditPanel(UploadInteractieEditPanel panel);
	@Provides static ResourceManager rm(CBookContext context) {
		return (ResourceManager) context.getProperty(Constants.RESOURCE_MANAGER);
	}
}
