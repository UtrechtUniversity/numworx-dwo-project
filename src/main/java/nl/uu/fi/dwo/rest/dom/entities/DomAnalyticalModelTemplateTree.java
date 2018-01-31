/** Copyrighted Jan 31, 2018 */
package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * AnalyticalModelTree contains a model tree for PersistentModelTree.
 *
 * @author plas0006
 */
@XmlRootElement
public class DomAnalyticalModelTemplateTree extends DomJsonModelTemplateNode {

    private int id;

    public DomAnalyticalModelTemplateTree() {
    }

    public DomAnalyticalModelTemplateTree(String aLocale, String aTitle) {
        super(aLocale, aTitle);
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
}
