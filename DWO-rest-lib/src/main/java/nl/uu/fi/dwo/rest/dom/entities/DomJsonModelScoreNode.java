/** Copyrighted Jan 31, 2018 */
package nl.uu.fi.dwo.rest.dom.entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JsonModelScoreNode
 *
 * @author plas0006
 */
@XmlRootElement
public class DomJsonModelScoreNode {

    private Double score;
    private Map<String, String> title = new HashMap<>();
    private Map<String, String> description = new HashMap<>();
    private Set<DomJsonModelScoreNode> children = new HashSet<>();

    public DomJsonModelScoreNode() {

    }

    public DomJsonModelScoreNode(String aLocale, String aTitle) {
        title.put(aLocale, aTitle);
        title.put("en", aTitle);
        score = 0.0;
    }

    public DomJsonModelScoreNode(String aLocale, String aTitle, double aScore) {
        title.put(aLocale, aTitle);
        title.put("en", aTitle);
        score = aScore;
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
     * @return the score
     */
    public Double getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(Double score) {
        this.score = score;
    }

    /**
     * @return the children
     */
    public Set<DomJsonModelScoreNode> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(Set<DomJsonModelScoreNode> children) {
        this.children = children;
    }
}
