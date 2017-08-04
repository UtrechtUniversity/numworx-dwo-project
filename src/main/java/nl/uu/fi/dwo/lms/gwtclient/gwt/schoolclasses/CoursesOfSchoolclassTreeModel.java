package nl.uu.fi.dwo.lms.gwtclient.gwt.schoolclasses;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.TreeViewModel;

/**
 * Treeview model
 * 
 * @author Gert van der Plas
 */
public class CoursesOfSchoolclassTreeModel implements TreeViewModel {
    
    
    CoursesOfSchoolclassTreeModel(){
        
    }
    
    @Override
    public <T> NodeInfo<?> getNodeInfo(T value) {
        /*
       * Create some data in a data provider. Use the parent value as a prefix
       * for the next level.
       */
      ListDataProvider<String> dataProvider = new ListDataProvider<String>();
      for (int i = 0; i < 2; i++) {
        dataProvider.getList().add(value + "." + String.valueOf(i));
      }

      // Return a node info that pairs the data with a cell.
      return new DefaultNodeInfo<String>(dataProvider, new TextCell());
    }

    @Override
    public boolean isLeaf(Object value) {
        return value.toString().length() > 20;
    }
    
    
}
