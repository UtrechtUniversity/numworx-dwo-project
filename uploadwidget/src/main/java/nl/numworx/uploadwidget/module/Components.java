package nl.numworx.uploadwidget.module;

import javax.inject.Singleton;

import org.cbook.cbookif.CBookContext;
import org.cbook.cbookif.CBookWidgetEditIF;
import org.cbook.cbookif.CBookWidgetInstanceIF;

import dagger.BindsInstance;
import dagger.Component;
import fi.beans.wiskopdrbeans.InteractiePanel;


@Singleton
@Component(modules=Modules.class)
public interface Components {

	
	@Component.Builder
	interface Builder {
		@BindsInstance Builder context(CBookContext context);
		Components build();
	}

	CBookWidgetEditIF editor();
	CBookWidgetInstanceIF instance();
	InteractiePanel interactiePanel();

	
}
