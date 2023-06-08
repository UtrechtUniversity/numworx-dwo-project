package nl.numworx.uploadwidget.module;

import org.cbook.cbookif.CBookWidgetEditIF;
import org.cbook.cbookif.CBookWidgetInstanceIF;

import dagger.Binds;
import dagger.Module;
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
}
