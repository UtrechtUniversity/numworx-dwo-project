package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import dagger.Component;
import nl.uu.fi.dwo.lms.gwtclient.gwt.StudentModelController;

@Component(modules = { StudentModelModule.class } )
public interface StudentModelComponent {
	StudentModelController controller();
}
