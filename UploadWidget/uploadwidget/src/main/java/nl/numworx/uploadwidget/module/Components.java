package nl.numworx.uploadwidget.module;

import java.util.Locale;

import javax.annotation.Nullable;
import javax.inject.Singleton;

import org.cbook.cbookif.CBookContext;
import org.cbook.cbookif.CBookWidgetEditIF;
import org.cbook.cbookif.CBookWidgetInstanceIF;

import dagger.BindsInstance;
import dagger.Component;
import fi.beans.wiskopdrbeans.InteractiePanel;
import nl.numworx.uploadwidget.UploadWidget;


@Singleton
@Component(modules=Modules.class)
public interface Components {

	
	@Component.Builder
	interface Builder {
		@BindsInstance Builder context(CBookContext context);
		@BindsInstance Builder locale(@Nullable Locale locale);
		Components build();
		@BindsInstance Builder widget(UploadWidget widget);
	}

	CBookWidgetEditIF editor();
	CBookWidgetInstanceIF instance();
	InteractiePanel interactiePanel();

	
}
