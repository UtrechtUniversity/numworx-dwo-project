package fi.dwo.dwojapplet.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import fi.dwo.dwojapplet.domain.DWO;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureDwoAdminStudentModelManager;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class DwoProfileModelPanel extends JPanel {
	
	List<DomStudentModelContext> contexts = new ArrayList<>();
	Boolean[] enabled = new Boolean[100];
	String language = "nl"; // TODO
	private SecureDwoAdminStudentModelManager manager;
	private DomDwoProfile profile;
	
	public DwoProfileModelPanel(DomDwoProfile p) {
		super();
		try {
			profile = p;
			manager = new SecureDwoAdminStudentModelManager();
			contexts = manager.getReducedList(null);
			enabled = new Boolean[contexts.size()];
			List<DomStudentModelContext> selected = manager.getReducedList(profile);
			List<PersistenceId> ids = selected.stream().map(DomStudentModelContext::getId).collect(Collectors.toList());
			for (int i = 0; i < enabled.length; i++) {
				enabled[i] = ids.contains(contexts.get(i).getId());
			}
		} catch(Exception oops) {
			return;
		}
		JTable tabel = new JTable(new Model<DomStudentModelContext>(contexts, enabled, this::toString ));
		add(tabel);
		
	}

    private String toString(DomStudentModelContext item) {
    	return item.getModelStructure().getInfo().getTitle().get(language);
    }

    static class Model<T> extends AbstractTableModel {

		final List<T> contexts;
		final Boolean[] enabled;
		final Function<T, String> toString;
		
		public Model(List<T> contexts, Boolean[] enabled, Function<T, String> toString) {
			this.contexts = contexts;
			this.enabled = enabled;
			this.toString = toString;
		}

		@Override
		public int getRowCount() {
			return contexts.size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch(columnIndex) {
			case 1: return enabled[rowIndex];
			case 0:
				T item = contexts.get(rowIndex);
				return toString.apply(item);
				// item.getModelStructure().getInfo().getTitle().get(language);
			}
			return null;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch(columnIndex) {
			case 0: return String.class;
			case 1: return Boolean.class;
			}
			return super.getColumnClass(columnIndex);
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 1;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (columnIndex == 1)
				enabled[rowIndex] = (Boolean) aValue;			
		}
		
	}

	public void commit() {
		for(int i = 0; i < enabled.length; i++) {
			DomStudentModelContext model = contexts.get(i);
			if (enabled[i]) {
				System.out.println("enable " + i);
				try {
					manager.addProfile(model, profile);
				} catch (Dwo2Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("disable " + i);
				try {
					manager.removeProfile(model, profile);
				} catch (Dwo2Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
}
