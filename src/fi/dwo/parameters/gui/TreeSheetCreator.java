// Source file: C:\\parameters\\fi\\dwo\\parameters\\gui\\TreeSheetCreator.java

package fi.dwo.parameters.gui;

import java.awt.Color;
import java.awt.Component;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Vector;

import fi.beans.scorm.Parameter;
import fi.beans.scorm.ScormTree;
import fi.beans.scorm.TreeParameter;
import fi.beans.stringutils.StringUtils;
import fi.dwo.client.gui.DwoMessageDialog;
import fi.dwo.parameters.system.TextMapper;

public class TreeSheetCreator implements TabSheetCreatorIF, ItemLaunchdataCallBackIF,
        ParameterComponentIF {

    private TreeParameter parameter;

    private Vector itemLaunchdata;

    private SingleLevelTreeParameterComponent parent;
    
    private TabPane tabPane;

    /**
     * @param parameter
     * @param default
     */
    public TreeSheetCreator(SingleLevelTreeParameterComponent parent,
            TreeParameter parameter, Hashtable defaultValue) {
        this.parent = parent;
        this.parameter = parameter;

        if(defaultValue.containsKey(parameter.getItemCountName())) {
            /* If the parameter is not definied, use 0 as number of items */
            String sNrItems = (String) defaultValue.get(parameter.getItemCountName());
            int nrItems = 0;
            if(sNrItems != null) {
                nrItems = Integer.parseInt(sNrItems);
            }
            itemLaunchdata = new Vector(nrItems);

            /*
             * Split the launchdata (for every tree-item). 
             * Create a new hashtable and add the object.
             */
            Hashtable ht;
            Object item;
            Parameter subParameter;
            int i, j;
            for(i = 1; i <= nrItems; i++) {
                ht = new Hashtable();
                for(j = 0; j < parameter.getSubParameters().length; j++) {
                    subParameter = parameter.getSubParameters()[j];
                    item = defaultValue.get(subParameter.getName() + "_" + i);
                    ht.put(subParameter.getName(), item);                    
                }
                itemLaunchdata.addElement(new ItemLaunchdata(this, ht));
            }
            
        } else {
            itemLaunchdata = new Vector();
        }

    }

    public void addTab() {
        if(parameter.getType() instanceof ScormTree) {
            ScormTree scormTree = (ScormTree) parameter.getType();
            if((scormTree.getMaxItems() != -1) &&(scormTree.getMaxItems() <= getNrItems())) {
                String[] arguments = new String[2];
                arguments[0] = Integer.toString(scormTree.getMaxItems());
                arguments[1] = parameter.getPreLabel();
                String s = TextMapper.getText(TextMapper.MSG_TO_MANY_TREE_ITEMS);

                s = MessageFormat.format(s, arguments);
                DwoMessageDialog.showMessageDialog(null, s);
                return;
            }
        }
        ItemLaunchdata item = new ItemLaunchdata(this, new Hashtable());
        item.setKey(StringUtils.randomstring());
        itemLaunchdata.addElement(item);
        tabPane.addRuntimeTabSheet(parameter.getItemLabel() + " " + getNrItems(), item.getKey());
        tabPane.selectTab(item.getKey());
    }

    /**
     * @param tabSheet
     */
    public void removeTab(SingleLevelTreeTabSheet tabSheet) {
        Object key = tabSheet.getKey();
        int index = itemLaunchdata.indexOf(tabSheet);
        itemLaunchdata.removeElementAt(index);
        ItemLaunchdataIF item;
        for(int i = index; i < itemLaunchdata.size(); i++) {
            item = (ItemLaunchdataIF) itemLaunchdata.elementAt(i);
            tabPane.renameTab(parameter.getItemLabel() + " " + (i + 1), item.getKey());
        }
        
        tabPane.removeTab(key);
        if(getNrItems() == 0) {
            parent.noItems();
        }
    }

    /**
     * @param tabPane
     */
    public void setTabPane(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    /**
     * @return int
     */
    public int getNrItems() {
        return itemLaunchdata.size();
    }

    public void createTabs() {
        if(tabPane != null) {
	        int i;
	        ItemLaunchdataIF item;
	        for(i = 0; i < itemLaunchdata.size(); i++) {
	            item = (ItemLaunchdataIF) itemLaunchdata.elementAt(i);
	            item.setKey(StringUtils.randomstring());
	            tabPane.addRuntimeTabSheet(parameter.getItemLabel() + " " + (i + 1), item.getKey());
	        }
        }

    }

    /**
     * @param key
     * @return TabSheetIF
     */
    public TabSheetIF createTabSheet(Object key) {
        int i = 0;
        while(((ItemLaunchdataIF) itemLaunchdata.elementAt(i)).getKey() != key) {
            i++;
        }
        ItemLaunchdataIF item = (ItemLaunchdataIF) itemLaunchdata.elementAt(i);
        if(item instanceof TabSheetIF) {
            return (TabSheetIF) item;
        } else {
	        SingleLevelTreeTabSheet tabSheet = new SingleLevelTreeTabSheet(this, parameter, item.getLaunchdata());
	        tabSheet.setKey(key);
	        itemLaunchdata.setElementAt(tabSheet, i);
	        return tabSheet;
        }
    }

    public void unFocus() {

    }

    /**
     * @return java.awt.Color
     */
    public Color getColor() {
        return parent.getColor();
    }

    /**
     * @param parameters
     */
    public void addParameters(Hashtable parameters) {
        Hashtable ht = new Hashtable();
        ht.put(((TreeParameter) parameter).getItemCountName(), Integer.toString(getNrItems()));        
        for(int i = 0; i < itemLaunchdata.size(); i++) {
            ((ItemLaunchdataIF) itemLaunchdata.elementAt(i)).addParameters(ht);
        }
        parent.addParameter(parameters, ht);        

    }

    /**
     * @return java.awt.Component
     */
    public Component getComponent() {
        return null;
    }

    /**
     * @param parent
     */
    public void setParentCom(ParameterComponentIF parent) {

    }

    /**
     * @param component
     */
    public void isFocussed(ParameterComponentIF component) {

    }


    /**
     * @param component
     */
    public void registerComponent(ParameterComponentIF component) {

    }

    public void reset() {

    }

    /**
     * @param color
     */
    public void setColor(Color color) {

    }

    /**
     * @param sequence
     */
    public void setSequenceLabel(int sequence) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.dwo.parameters.gui.ParameterComponentIF#getParentCom()
     */
    public ParameterComponentIF getParentCom() {
        // TODO Auto-generated method stub
        return parent;
    }

    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ParameterComponentIF#getSequenceString(fi.dwo.parameters.gui.ParameterComponentIF)
     */
    public String getSequenceString(ParameterComponentIF component) {
        return "_" + (itemLaunchdata.indexOf(component) + 1);
    }

    /* (non-Javadoc)
     * @see fi.dwo.parameters.gui.ItemLaunchdataCallBackIF#getSequenceStr(fi.dwo.parameters.gui.ItemLaunchdataIF)
     */
    public String getSequenceStr(ItemLaunchdataIF component) {
        return "_" + (itemLaunchdata.indexOf(component) + 1);
    }
}