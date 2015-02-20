//Source file:
//N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\system\\Text_nl.java

package fi.dwo.dwojapplet.parameters.system.text;

import fi.dwo.dwojapplet.parameters.system.TextMapper;
import java.util.ListResourceBundle;

public class Text_nl extends ListResourceBundle {
 private final Object[][] contents = {
        { TextMapper.BOOLEAN_TRUE, "Ja"}, 
        { TextMapper.BOOLEAN_FALSE, "Nee"}, 
        { TextMapper.TLTP_HELP, "Meer info over deze parameter"},
        { TextMapper.TLTP_DELETE_ITEM, "Verwijder deze parameter"},
        { TextMapper.TITLE_HELP, "Help"},
        { TextMapper.BTN_CLOSE, "Sluiten"},
        { TextMapper.BTN_TREE_NR_ITEMS, "Toon Items ({0})"},
        { TextMapper.BTN_TREE_ADD_ITEM, "Nieuwe {0}"},
        { TextMapper.BTN_TREE_DELETE_ITEM, "Verwijder {0}"},
        { TextMapper.LBL_NO_ITEMS, "Er zijn geen items aanwezig"},
        { TextMapper.MSG_TO_MANY_TREE_ITEMS, "Er kunnen maximaal {0} {1} worden aangemaakt."}
 		};

 public Text_nl() {

 }

 /**
  * @return Object[][]
  */
 @Override
 public Object[][] getContents() {
     return contents;
 }
}