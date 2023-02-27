/** Copyrighted Jan 31, 2018 */
package nl.uu.fi.dwo.rest.dom.entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author plas0006
 */
@XmlRootElement
public class DomJsonModelTemplateNode {

    private Map<String, String> title = new HashMap<>();
    private Map<String, String> description = new HashMap<>();
    private Set<DomJsonModelTemplateNode> children = new HashSet<>();

    public DomJsonModelTemplateNode() {

    }

    public DomJsonModelTemplateNode(String aLocale, String aTitle) {
        title.put(aLocale, aTitle);
        title.put("en", aTitle);
    }

    /**
     * @return the title
     */
    public Map<String, String> getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    /**
     * @return the description
     */
    public Map<String, String> getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(Map<String, String> description) {
        this.description = description;
    }

    /**
     * @return the children
     */
    public Set<DomJsonModelTemplateNode> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(Set<DomJsonModelTemplateNode> children) {
        this.children = children;
    }

}
