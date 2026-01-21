package nl.uu.fi.dwo.rest.dom.entities;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@SuppressWarnings("serial")
public class DomDwoProfileId extends DomId {

	public DomDwoProfileId(PersistenceId id) {
		super(id);
	}

	public DomDwoProfileId(PersistenceId id, Long longid) {
		this(id);
		this.profile = longid;
	}

	@Override
	public void setId(PersistenceId id) {
		profile = null;
		super.setId(id);
	}

	public void setProfile(PersistenceId id, Long profile) {
		this.profile = profile;
		super.setId(id);
	}


	private transient Long profile; // cache...

	public Long asLong() {
		if  (profile != null) return profile;
		PersistenceId id = getId();
		if (id != null) {
			String str = id.getIdString();
			if (str != null) {
				int i = str.lastIndexOf(';');
				str = str.substring(i+1);
				profile = Long.valueOf(str);
				return profile;
			}
		}
		return null;
	}
	
	
	public DomDwoProfileId() {
	}

	public DomDwoProfileId duplicate() {
		return new DomDwoProfileId(getId());
	}

}
