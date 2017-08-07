package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import java.util.HashMap;
import java.util.Map;

/**
 * Node of the TreeNode. The tree is crawled to calculate a result matrix
 * for the result viewing.
 *
 * @author G.A.J. van der Plas email: G.A.J.vanderPlas@uu.nl
 * @param <T>
 */
public class TreeNode<T> {

    private TreeNode parent = null;
    T object;
    private Map<String, TreeNode<T>> children = new HashMap<String, TreeNode<T>>();
    
    public TreeNode(T anObject){
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
    public TreeNode getParent() {
        return parent;
    }

    /**
     * @param aParent
     */
    public void setParent(TreeNode aParent) {
            parent = aParent;
    }

    /**
     * @return the children
     */
    public Map<String, TreeNode<T>> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(Map<String, TreeNode<T>> children) {
        this.children = children;
    }

}
