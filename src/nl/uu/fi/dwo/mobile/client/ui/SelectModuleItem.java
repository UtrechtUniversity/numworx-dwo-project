package nl.uu.fi.dwo.mobile.client.ui;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem.Type;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.xml.client.Node;

import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;

/**
 * Item in selection list
 * 
 * @author Danny Hendrix
 * 
 */
public class SelectModuleItem
{
	
	public static final String PREFIX = DWOplayer.PREFIX;
	
	public enum Type
	{
		SCO, MODULE, FOLDER, ROOT, SEPARATOR, SEARCH
	}

	public static final SelectModuleItem ROOT = new SelectModuleItem(null, SelectModuleItem.Type.ROOT);
	static {
		ROOT.setName("Standaard DWO Modules");
		ROOT.setDescription("<html><body><b>DWO-modules</b><br>In de DWO is veel oefenmateriaal beschikbaar.  Naast dit oefenmateriaal zijn er ook diverse volledige lessen en lessenseries beschikbaar, die kunnen worden gebruikt als aanvulling op of zelfs vervanging van het reguliere boek. In de etalage kunt u een indruk krijgen van de mogelijkheden die de DWO biedt voor de wiskundeles.</body></html>");
	}
	
	
	private String name;
	private String file;
	private String description;
	private Object id;
	private boolean showScore, fromSchool;
	private int sequencenr;
	private String image;
	
	private Type type = Type.ROOT;
	private Promise<List<SelectModuleItem>> childrenAsync;
	private Object parent;
	private Date notBefore, notAfter;
	private boolean showChildren = true;

	public Date getNotAfter() {
		return notAfter;
	}

	private Number toetsType;

	public SelectModuleItem(Object id, String name, String file)
	{
		this.id = id;
		this.name = name;
		this.file = file;
		this.type = Type.SCO;
		this.showScore = true; // the default
	}

	public SelectModuleItem(Map<String,Object> map, Type type)
	{
		switch(type) {
		case MODULE:
			if(Boolean.TRUE.equals(map.get("withChildren")))
				this.type = Type.FOLDER;
			else
				this.type = Type.MODULE;
			this.name = map.get("name").toString();
			this.id   = map.get("courseID");
			this.description = (String) map.get("description");
			Object parentID =  map.get("parentID");
			if(parentID != null) {
				this.parent = parentID;
			} 
			{  Object schoolID = map.get("schoolID");
			   this.fromSchool = schoolID != null && ! "".equals(schoolID);
			}
			this.showScore = false;
// Alleen als de "classcourse" data ge-piggybacked is.
			this.notAfter = toDate(map.get("notAfter"));
			this.notBefore = toDate(map.get("notBefore"));
			this.toetsType = (Number) map.get("type");
			break;
		case SCO:
			this.type = type;
			this.name = map.get("sconame").toString();
			this.description = (String) map.get("description");
			this.id =  map.get("scoID");
			this.file = PREFIX + this.id;
			this.showScore = !Boolean.TRUE.equals(map.get("showscore")); // reverse logica
			this.sequencenr = ((Number) map.get("sequencenr")).intValue();
			parentID = map.get("courseID");
			if(parentID != null) {
				this.parent = parentID;
				if(getParent() != null) {
					SelectModuleItem parent = getParent();
					this.notAfter = parent.notAfter;
					this.notBefore = parent.notBefore;
					this.toetsType = parent.toetsType;
				}
			}
			break;
		case SEPARATOR:
			this.type = type;
		break;
// more to follow....			
			
			
		}
	}
	
	private Date toDate(Object object)
	{
		if(object instanceof Date)
			return (Date) object;
		return null;
	}

	public boolean isFromSchool() {
		return fromSchool;
	}
	
	public SelectModuleItem(Object id, Node node)
	{
		this.id = id;
		this.showScore = false;
		for (int i = 0; i < node.getChildNodes().getLength(); i++)
		{

			Node curr = node.getChildNodes().item(i);
			if (curr.getNodeName().equalsIgnoreCase("name"))
				this.name = curr.getChildNodes().toString();
			if (curr.getNodeName().equalsIgnoreCase("file"))
				this.file = curr.getChildNodes().toString();

		}
		Logger.getLogger("SelectModuleItem").log(Level.INFO,this.name + " " + this.file);
	}

	public SelectModuleItem(Object id, Type module) {
		this.type = module;
		this.id = id;
		if(module == Type.SCO)
		{
			this.file = PREFIX + id;
		}
	}

	public SelectModuleItem(DomCourseStudent course, DomClassCourse domClassCourse) {
		type = course.getWithChildren() ? Type.FOLDER : Type.MODULE;
		description = course.getDescription();
		fromSchool = course.getSchoolId() != null;
		id = PersistenceIdDecoderInterface.instance.idOf(course.getId(), PersistenceClassType.PersistentCourse);
		parent = PersistenceIdDecoderInterface.instance.idOf(course.getParentID(), PersistenceClassType.PersistentCourse);
		name = course.getName();
		Long sequence = course.getSequenceNr();
		sequencenr = sequence != null ? sequence.intValue() : Integer.MAX_VALUE;
		showScore = false;
		showChildren(!course.isNotVisible());
		if (domClassCourse!=null) {
			notAfter = domClassCourse.getNotAfter();
			notBefore = domClassCourse.getNotBefore();
			toetsType = domClassCourse.getType();
		}
		image = course.getImage();
		if("".equals(image)) image = null;
	}

	public SelectModuleItem(DomScoContext sco) {
		type = Type.SCO;
		description = "";
		id = PersistenceIdDecoderInterface.instance.idOf(sco.getId(), PersistenceClassType.PersistentScoContext);
		parent = PersistenceIdDecoderInterface.instance.idOf(sco.getCourseId(), PersistenceClassType.PersistentCourse);
		name = sco.getScoName();
		Long sequence = sco.getSequencenr();
		sequencenr = sequence != null ? sequence.intValue() : Integer.MAX_VALUE;
		showScore = !Boolean.TRUE.equals(sco.getShowScore());
		this.file = PREFIX + this.id;
		if(parent != null) {
			if(getParent() != null) {
				SelectModuleItem parent = getParent();
				this.notAfter = parent.notAfter;
				this.notBefore = parent.notBefore;
				this.toetsType = parent.toetsType;
			}
		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getFile()
	{
		return file;
	}

	public void setFile(String file)
	{
		this.file = file;
	}

	public Object getID()
	{
		return this.id;
	}

	public void setID(Object id)
	{
		this.id = id;
	}

	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	/**
	 * Niet async safe.
	 * @return children
	 * @deprecated gebruik getChildrenAsync()
	 */
	public List<SelectModuleItem> getChildren()
	{
		if(childrenAsync != null && childrenAsync.isDone())
			return childrenAsync.getValue();
		return null;
	}

	/**
	 * gebruik setChildrenAsync()
	 * @param children
	 * @deprecated 
	 */
	public void setChildren(List<SelectModuleItem> children)
	{
		childrenAsync = Promises.resolved(children);
	}
	
	public void setChildrenAsync(Promise<List<SelectModuleItem>> promise) {
		childrenAsync = promise;
	}
	
	public Promise<List<SelectModuleItem>> getChildrenAsync() {
		return childrenAsync;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SelectModuleItem getParent() {
		if(this.parent == null) return null;
		SelectModuleItem parent = SelectModuleItemHolder.getItemByID(this.parent);
		if(parent == ROOT) parent = null;
		return parent;
	}
	
	public Object getParentID() {
		return this.parent;
	}

	public void setParent(SelectModuleItem parent) {
		if(parent == null) this.parent = null;
		else this.parent = parent.id;
	}
	
	public boolean isShowScore() {
		return showScore && getScore() != null;
	}

	private Promise<Map<Object, Number>> promisedScoreMap;

	public Promise<Map<Object, Number>> getPromisedScoreMap() {
		return promisedScoreMap;
	}

	public void setPromisedScoreMap(Promise<Map<Object, Number>> promisedScoreMap) {
		this.promisedScoreMap = promisedScoreMap;
	}

	public Number getScore() {
		if(getParent() == null) return null;
		Map<Object,? extends Number> scoreMap = getParent().getScoreMap();
		if(scoreMap == null) return null;
		Number score = scoreMap.get(id);
		return score;
	}

	public void setScore(Number number) {
		if(getParent() == null) return;
		Map<Object, Number> scoreMap = getParent().getScoreMap();
		if(scoreMap == null)
			getParent().setScoreMap( scoreMap = new HashMap<Object, Number>() );
		scoreMap.put(id, number);
	}

	public Map<Object, Number> getScoreMap() {
		if(promisedScoreMap != null && promisedScoreMap.isDone()) return promisedScoreMap.getValue();
		return null;
	}

	public void setScoreMap(Map<Object, Number> scoreMap) {
		promisedScoreMap = Promises.resolved(scoreMap);
	}
	
	public int getSequencenr() {
		return sequencenr;
	}

	public void setSequencenr(int sequencenr) {
		this.sequencenr = sequencenr;
	}

	public boolean showChildren() {
		return showChildren;
	}

	public void showChildren(boolean showChildren) {
		this.showChildren = showChildren;
	}

	public String getImage() {
		return image;
	}
	
}
