package nl.uu.fi.dwo.rest.dom;

import java.util.HashMap;
import java.util.Map;

/**
 * Node of the DomTree. The tree is crawled to calculate a result matrix
 * for the result viewing.
 *
 * @author G.A.J. van der Plas email: G.A.J.vanderPlas@uu.nl
 * @param <T>
 */
public class DomTree<T> {

    private DomTree parent = null;
    T object;
    private Map<String, DomTree<T>> children = new HashMap<String, DomTree<T>>();
    
    public DomTree(T anObject){
        object = anObject;
    }

    public T getObject(){
        return object;
    }
    
    public void setObject(T anObject){
        object = anObject;
    }

    /**
     * @return the parent
     */
    public DomTree getParent() {
        return parent;
    }

    /**
     * @param aParent
     */
    public void setParent(DomTree aParent) {
            parent = aParent;
    }

    /**
     * @return the children
     */
    public Map<String, DomTree<T>> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(Map<String, DomTree<T>> children) {
        this.children = children;
    }

}
