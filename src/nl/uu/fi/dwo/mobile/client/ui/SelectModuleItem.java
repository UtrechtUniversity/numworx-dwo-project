package nl.uu.fi.dwo.mobile.client.ui;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem.Type;

import com.google.gwt.core.client.GWT;
import com.google.gwt.xml.client.Node;

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
		SCO, MODULE, FOLDER, ROOT, SEPARATOR
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

	private Type type = Type.ROOT;
	private List<SelectModuleItem> children;
	private SelectModuleItem parent;
	private Date notBefore, notAfter;
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
				this.parent = SelectModuleItemHolder.getItemByID(parentID);
				if(this.parent != null && this.parent.getType() == Type.ROOT) this.parent = null; // children of root have no parent
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
				this.parent = SelectModuleItemHolder.getItemByID(parentID);
				if(parent != null) {
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

	public List<SelectModuleItem> getChildren()
	{
		return children;
	}

	public void setChildren(List<SelectModuleItem> children)
	{
		this.children = children;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SelectModuleItem getParent() {
		return parent;
	}

	public void setParent(SelectModuleItem parent) {
		this.parent = parent;
	}
	
	public boolean isShowScore() {
		return showScore && getScore() != null;
	}

	private Map<Object, Number> scoreMap;

	public Number getScore() {
		if(parent == null) return null;
		Map<Object,? extends Number> scoreMap = parent.scoreMap;
		if(scoreMap == null) return null;
		Number score = scoreMap.get(id);
		return score;
	}

	public void setScore(Number number) {
		if(parent == null) return;
		Map<Object, Number> scoreMap = parent.scoreMap;
		if(scoreMap == null)
			parent.scoreMap = scoreMap = new HashMap<Object, Number>();
		scoreMap.put(id, number);
	}

	public Map<Object, Number> getScoreMap() {
		return scoreMap;
	}

	public void setScoreMap(Map<Object, Number> scoreMap) {
		this.scoreMap = scoreMap;
	}
	
	public int getSequencenr() {
		return sequencenr;
	}

	public void setSequencenr(int sequencenr) {
		this.sequencenr = sequencenr;
	}
	
}
