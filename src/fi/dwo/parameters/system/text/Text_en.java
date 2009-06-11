//Source file:
//N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\system\\Text_en.java

package fi.dwo.parameters.system.text;

import java.util.ListResourceBundle;

import fi.dwo.parameters.system.TextMapper;

public class Text_en extends ListResourceBundle {
 private final Object[][] contents = {
        { TextMapper.BOOLEAN_TRUE, "Yes"},
        { TextMapper.BOOLEAN_FALSE, "No"},
        { TextMapper.TLTP_HELP, "More info about this parameter"},
        { TextMapper.TLTP_DELETE_ITEM, "Delete this parameter"},
        { TextMapper.TITLE_HELP, "Help"},
        { TextMapper.BTN_CLOSE, "Close"},
        { TextMapper.BTN_TREE_NR_ITEMS, "Show Items ({0})"},
        { TextMapper.BTN_TREE_ADD_ITEM, "New {0}"},
        { TextMapper.BTN_TREE_DELETE_ITEM, "Delete {0}"},
        { TextMapper.LBL_NO_ITEMS, "There are no items to show"},
        { TextMapper.MSG_TO_MANY_TREE_ITEMS, "Only a maximum of {0} {1} is allowed."}
 		};

 public Text_en() {

 }

 /**
  * @return Object[][]
  */
 public Object[][] getContents() {
     return contents;
 }
}