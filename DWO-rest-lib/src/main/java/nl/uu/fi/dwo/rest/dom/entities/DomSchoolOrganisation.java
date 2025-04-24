package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;

import nl.uu.fi.dwo.rest.dom.entities.util.OrderType;
import nl.uu.fi.dwo.rest.dom.entities.util.SortType;

public class DomSchoolOrganisation {
	
	private Long skip, limit;
	private RoleType role;
	private OrderType order = OrderType.asc;
	private SortType  sort = SortType.familyName;
	
	private List<DomUser> users;
    private List<DomMemberOfClass> usersOfClasses;
	private List<DomSchoolClass> schoolClasses;
	
 
    public Long getSkip() {
		return skip;
	}
	public void setSkip(Long skip) {
		this.skip = skip;
	}
	public Long getLimit() {
		return limit;
	}
	public void setLimit(Long limit) {
		this.limit = limit;
	}
	public List<DomUser> getUsers() {
		return users;
	}
	public void setUsers(List<DomUser> students) {
		this.users = students;
	}
	public List<DomMemberOfClass> getUsersOfClasses() {
		return usersOfClasses;
	}
	public void setUsersOfClasses(List<DomMemberOfClass> studentsOfClasses) {
		this.usersOfClasses = studentsOfClasses;
	}
	public List<DomSchoolClass> getSchoolClasses() {
		return schoolClasses;
	}
	public void setSchoolClasses(List<DomSchoolClass> schoolClasses) {
		this.schoolClasses = schoolClasses;
	}
	public RoleType getRole() {
		return role;
	}
	public void setRole(RoleType role) {
		this.role = role;
	}
	public OrderType getOrder() {
		return order;
	}
	public void setOrder(OrderType order) {
		this.order = order;
	}
	public SortType getSort() {
		return sort;
	}
	public void setSort(SortType sort) {
		this.sort = sort;
	}
}
