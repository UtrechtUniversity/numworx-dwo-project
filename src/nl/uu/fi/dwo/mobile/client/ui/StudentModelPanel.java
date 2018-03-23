package nl.uu.fi.dwo.mobile.client.ui;

import javax.inject.Provider;

import nl.uu.fi.dwo.account.client.StudentModelView;

public class StudentModelPanel extends nl.uu.fi.dwo.account.client.StudentModelPanel implements StudentModelView {

	public static final Provider<StudentModelView> BUILDER = new Builder();
	private static class Builder implements Provider<StudentModelView> {

		@Override
		public StudentModelView get() {
			return new StudentModelPanel();
		}
		
	}
	
	
	public StudentModelPanel() {
		// TODO Auto-generated constructor stub
	}

}
