/*
 * Created on Apr 14, 2005
 *
 */
package fi.dwo.dwojapplet.parameters.test;

import java.applet.Applet;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.Panel;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Hashtable;

import fi.beans.scorm.DataType;
import fi.beans.scorm.DataTypeIF;
import fi.beans.scorm.ExtendedParameter;
import fi.beans.scorm.Parameter;
import fi.beans.scorm.ScormAppletIF;
import fi.beans.scorm.ScormBoolean;
import fi.beans.scorm.ScormDouble;
import fi.beans.scorm.ScormEditComponentIF;
import fi.beans.scorm.ScormEnum;
import fi.beans.scorm.ScormFormula;
import fi.beans.scorm.ScormGroup;
import fi.beans.scorm.ScormInteger;
import fi.beans.scorm.ScormString;
import fi.beans.scorm.ScormText;
import fi.beans.scorm.ScormTree;
import fi.beans.scorm.TreeParameter;
//import fi.beans.tooltip.ToolTipManager;
import fi.dwo.dwojapplet.gui.AutoScrollPanel;
import fi.dwo.dwojapplet.gui.GuiConstants;
import fi.dwo.dwojapplet.parameters.gui.MainParameterComponent;
import fi.dwo.dwojapplet.parameters.gui.ParameterComponent;

/**
 * @author M.J.B. Kupers
 *
 */
public class ParametersTest extends Applet implements ComponentListener, ScormAppletIF {
    
    Parameter[] parameters;

    /**
     * @throws java.awt.HeadlessException
     */
    public ParametersTest() throws HeadlessException {
        super();
        int getal = 5;
        if(getal == 1) {
	        parameters = new Parameter[3];
	        DataType dt;
	        /* Het textveld met de begeleidende text */
	        dt = new ScormString();
	        DataType type = new ScormInteger();
	        type.setSize(5); // Stel de grote van het invoerveld in
	        parameters[0] = new Parameter("distance", "Afstand", type);
	        parameters[0].setPostLabel("meters"); // Zet eeen labeltje achter het invoerveld
	        parameters[0].setHelpText("Vul hier de totale afstand in meters in"); // Een helptekst kan de gebruiker meer informatie geven
	
	         
	
	
	        
	        /* De Activiteiten met daarbinnen de opdrachten */
	        ScormTree scormTree = new ScormTree();
	        scormTree.setMaxItems(6);
	        TreeParameter tp = new TreeParameter("activiteiten", "Activiteiten", scormTree);
	        tp.setItemCountName("aantalActiviteiten");
	        tp.setItemLabel("Activiteit");
	        tp.addSubParameter(new Parameter("activiteit", "Naam", new ScormString()));
	        
	        ScormTree scormTree2 = new ScormTree();
	        scormTree2.setMaxItems(15);
	        
	        TreeParameter tp2 = new TreeParameter("opdrachten", "Opdrachten", scormTree2);
	        tp2.setItemCountName("aantalOpdrachten");
	        tp2.setItemLabel("Opdracht");
	        tp2.addSubParameter(new Parameter("opdracht", "Opdracht", new ScormString()));
	        
	        tp.addSubParameter(tp2);
	        
	        parameters[1] = tp;
	        
	        parameters[2] = new Parameter("vb", "Voorbeeld", new ScormDouble());
        } else if(getal == 2) {      
	        parameters = new Parameter[1]; // We moeten uiteindelijk een parametersarray teruggeven
	
	        ScormTree treeType = new ScormTree();
	        treeType.setMaxItems(5);
	
	        TreeParameter treeParam = new TreeParameter("niveaus", "Niveaus", treeType);
	        treeParam.setItemCountName("nrNiveaus");
	        treeParam.setItemLabel("Niveau");
	
	        DataType type = new ScormText();
	        Parameter param = new Parameter("description", "Omschrijving", type);
	        treeParam.addSubParameter(param);
	
	        /* Maak een nieuwe tree die als subparameter van treeParam zal worden */
	        ScormTree treeType2 = new ScormTree();
	        treeType2.setMaxItems(15);
	        TreeParameter subtree = new TreeParameter("exercises", "Opgaven", treeType2);
	        subtree.setItemCountName("nrExercises");
	        subtree.setItemLabel("Opgave");
	
	        type = new ScormString();
	        param = new Parameter("exercise", "Opdracht", type);
	        subtree.addSubParameter(param);
	
	        treeParam.addSubParameter(subtree); // Voeg de subtree toe aan de hoofdtree
	
	
	        parameters[0] = treeParam;
        } else if(getal == 3) {


//         Op dit moment is er nog geen speciaal formule invoerveld. Tot die tijd wordt een stringparameter weergegeven
	      parameters = new Parameter[8];
	      
	      DataTypeIF dt = new ScormString();
	      dt.setSize(10);
	      parameters[0] = new Parameter("testParam1", "Doe je ding", dt);
	      parameters[0].setHelpText("Whelp!! oso sfdgkj sfdgjh sdfoij sodhg  fsdigoj doifgj oeiuj oiujt udiu jhnfudhgnuhrui[nx;krijn  rior mnoirun fgim  iorpmt \n\n\n\n sdifj difj ");
	      parameters[0].setPostLabel("m/s");
	      
	      dt = new ScormInteger();
	      parameters[1] = new Parameter("testParam2", "Doe je ding", dt);
	      parameters[1].setHelpText("Whelp!!");
	      
	      dt = new ScormBoolean();
	      parameters[2] = new Parameter("plaat3", "Is dat echt", dt);
	      parameters[2].setHelpText("Whelp!!");
	
	      dt = new ScormText();
	      parameters[3] = new Parameter("plaat4", "Is dat echt", dt);
	      parameters[3].setHelpText("Whelp!!");
	
	      dt = new ScormGroup();
	      ExtendedParameter ex = new ExtendedParameter("plaat5", "Is dat echt", dt);
	      dt = new ScormInteger();
	      dt.setSize(15);
	      Parameter param = new Parameter("x", "X", dt);
	      ex.addSubParameter(param);
	      dt = new ScormInteger();
	      dt.setSize(4);
	      param = new Parameter("y", "Y", dt);
	      ex.addSubParameter(param);
	      
	      dt = new ScormInteger();
	      dt.setSize(4);
	      param = new Parameter("z", "Z", dt);
	      ex.addSubParameter(param);
	
	      parameters[4] = ex;
	
	      dt = new ScormTree();
	      ((ScormTree) dt).setMaxItems(6);
	      TreeParameter tp = new TreeParameter("plaat", "Is dat echt", dt);
	      parameters[5] = tp;
	      parameters[5].setHelpText("Whelp!!");
	      tp.setItemCountName("nrPlaat");
	      tp.setItemLabel("Itemtjes");
	      tp.addSubParameter(new Parameter("name", "Hello", new ScormInteger()));
	      
	      dt = new ScormTree();
	      ((ScormTree) dt).setMaxItems(5);
	      tp = new TreeParameter("appel", "Is dat echt", dt);
	      parameters[6] = tp;
	      parameters[6].setHelpText("Whelp!!");
	      tp.setItemCountName("nrAppel");
	      tp.setItemLabel("Itemtjes");
	      tp.addSubParameter(new Parameter("gmail", "Hello", new ScormInteger()));
	      tp.addSubParameter(new Parameter("blaat", "appel", new ScormText()));
	      
	      TreeParameter tp2 = new TreeParameter("help", "opdrachten", new ScormTree());
	      tp2.setItemCountName("nrOpdrachten");
	      tp2.setItemLabel("Opdracht");
	      tp2.addSubParameter(new Parameter("waarde", "waarde", new ScormText()));
	      tp.addSubParameter(tp2);
	      
	      dt = new ScormDouble();
	      parameters[7] = new Parameter("plaat8", "Is dat echt", dt);
	      parameters[7].setHelpText("Whelp!!");
        } else if (getal == 4) {
            DataType type = new ScormString();
            parameters = new Parameter[2];
            parameters[0] = new Parameter("name", "Naam", type);
            
            TreeParameter tree = new TreeParameter("niveaus", "Niveaus", new ScormTree());
            tree.setItemCountName("nrNiveaus");
            tree.setItemLabel("Niveau");
            tree.addSubParameter(new Parameter("name", "Naam", new ScormString()));
            tree.addSubParameter(new Parameter("mandatory", "Verplicht", new ScormBoolean()));
            
            TreeParameter subTree = new TreeParameter("opgaven", "Opgaven", new ScormTree());
            subTree.setItemCountName("nrOpgaven");
            subTree.setItemLabel("Opgave");
            Parameter tmp;
            tmp = new Parameter("description", "Omschrijving", new ScormText());
            tmp.setHelpText("Help");
            subTree.addSubParameter(tmp);
            
            subTree.addSubParameter(new Parameter("mandatory", "Verplicht", new ScormBoolean()));
            subTree.addSubParameter(new Parameter("opgave", "Opgave", new ScormString()));
            tmp = new Parameter("score", "Score", new ScormInteger());
            tmp.setPostLabel("(0 - 100)");
            tmp.getType().setSize(5);
            subTree.addSubParameter(tmp);
            tree.addSubParameter(subTree);
            
            parameters[1] = tree;
            
        } else if (getal == 5) {
            parameters = new Parameter[11]; // De methode moet uiteindelijk een array van parameters teruggeven
            
            DataType type = new ScormString();
            Parameter param = new Parameter("description", "Omschrijving", type);
            parameters[0] = param;

            type = new ScormInteger();
            type.setSize(5); // Stel de grote van het invoerveld in
            param = new Parameter("distance", "Afstand", type);
            param.setPostLabel("meters"); // Zet eeen labeltje achter het invoerveld
            param.setHelpText("Vul hier de totale afstand in meters in"); // Een helptekst kan de gebruiker meer informatie geven
            parameters[1] = param; // Voeg de parameter toe aan de array

            /* Maak een Boolean */
            type = new ScormBoolean();
            param = new Parameter("mandatory", "Is verplicht", type);
            parameters[2] = param;

            /* Maak een Double */
            type = new ScormDouble();
            type.setSize(8);
            param = new Parameter("temperature", "Hoe warm is het", type);
            param.setPostLabel("� C");
            parameters[3] = param;

            /* Maak een Text */
            type = new ScormText();
            param = new Parameter("story", "Een verhaaltje", type);
            parameters[4] = param;

            /* Maak een Formula */
            type = new ScormFormula();
            param = new Parameter("formula", "De formule", type);
            parameters[5] = param;
            
            type = new ScormGroup();
            ExtendedParameter extendedParam = new ExtendedParameter("", "Locatie", type);
            extendedParam.setHelpText("De locatie van het voorwerp");

            type = new ScormInteger();
            type.setSize(4);
            param = new Parameter("x", "X", type);
            param.setHelpText("de waarde van de x-as");
            extendedParam.addSubParameter(param);

            type = new ScormInteger();
            type.setSize(4);
            param = new Parameter("y", "Y", type);
            param.setHelpText("de waarde van de y-as");
            extendedParam.addSubParameter(param);

            type = new ScormInteger();
            type.setSize(4);
            param = new Parameter("z", "Z", type);
            param.setHelpText("de waarde van de z-as");
            extendedParam.addSubParameter(param);
            parameters[6] = extendedParam;
            
            ScormTree treeType = new ScormTree();
            treeType.setMaxItems(5); // Het maximaal aantal items in de tree

            TreeParameter treeParam = new TreeParameter("tasks", "Opdrachten", treeType); // De naam van de tree is meestal meervoud
            treeParam.setItemCountName("nrTasks"); // Onder deze naam is het aantal items uit de launchdata op te vragen
            treeParam.setItemLabel("Opdracht"); // Geeft de naam aan van een enkel item. Meestal is dit enkelvoud

            type = new ScormString();
            param = new Parameter("text", "Tekst", type);
            treeParam.addSubParameter(param);
            
            parameters[7] = treeParam;
            
            treeType = new ScormTree();
            treeParam = new TreeParameter("tasks2", "Opdrachten", treeType);
            treeParam.setItemCountName("nrTasks2"); // Onder deze naam is het aantal items uit de launchdata op te vragen
            treeParam.setItemLabel("Opdracht"); // Geeft de naam aan van een enkel item. Meestal is dit enkelvoud

            type = new ScormString();
            param = new Parameter("text", "Tekst", type);
            treeParam.addSubParameter(param);

            type = new ScormText();
            param = new Parameter("comment", "Opmerking", type);
            treeParam.addSubParameter(param);
            
            parameters[8] = treeParam;
            
            treeType = new ScormTree();
            treeType.setMaxItems(5);

            treeParam = new TreeParameter("niveaus", "Niveaus", treeType);
            treeParam.setItemCountName("nrNiveaus");
            treeParam.setItemLabel("Niveau");

            type = new ScormText();
            param = new Parameter("description", "Omschrijving", type);
            treeParam.addSubParameter(param);

            /* Maak een nieuwe tree die als subparameter van treeParam zal worden */
            ScormTree treeType2 = new ScormTree();
            treeType2.setMaxItems(15);
            TreeParameter subtree = new TreeParameter("exercises", "Opgaven", treeType2);
            subtree.setItemCountName("nrExercises");
            subtree.setItemLabel("Opgave");

            type = new ScormString();
            param = new Parameter("exercise", "Opdracht", type);
            subtree.addSubParameter(param);

            treeParam.addSubParameter(subtree); // Voeg de subtree toe aan de hoofdtree
            
            parameters[9] = treeParam;
            
            type = new ScormEnum(new String[] {"rood", "groen", "blauw"});
            parameters[10] = new Parameter("enumstest","Opsomming", type);
            
//             Op dit moment is er nog geen speciaal formule invoerveld. Tot die tijd wordt een stringparameter weergegeven
        }
//      
//      Hashtable ht3 = new Hashtable();
//      ht3.put("nrOpdrachten", new Integer(2));
//      ht3.put("waarde_0", "blaat1");
//      ht3.put("waarde_1", "blaat2");
//      ht3.put("waarde_2", "blaat3");
//      
//      Hashtable ht = new Hashtable();
//      ht.put("testParam1", "appelscha test textsst");
//      ht.put("plaat8", new Double(3.1415926535192));
//      ht.put("plaat3", new Boolean(false));
//      Hashtable ht2 = new Hashtable();
//      ht2.put("nrPlaat", new Integer(5));
//      ht2.put("name_0", new Integer(0));
//      ht2.put("name_1", new Integer(1));
//      ht2.put("name_2", new Integer(2));
//      ht2.put("name_3", new Integer(3));
//      ht2.put("name_4", new Integer(4));
//      
//      ht.put("plaat", ht2);
//
//      ht2 = new Hashtable();
//      ht2.put("nrAppel", new Integer(4));
//      ht2.put("gmail_0", new Integer(6));
//      ht2.put("blaat_0", new String("foo1"));
//      ht2.put("gmail_1", new Integer(7));
//      ht2.put("blaat_1", new String("foo2"));
//      ht2.put("gmail_2", new Integer(8));
//      ht2.put("blaat_2", new String("foo3"));
//      ht2.put("gmail_3", new Integer(9));
//      ht2.put("blaat_3", new String("foo4"));
//      ht2.put("help_0", ht3);
//      ht2.put("help_1", ht3);
//      ht2.put("help_2", ht3);
//      ht2.put("help_3", ht3);
//      ht.put("appel", ht2);
    }

    @Override
    public void init() {
        super.init();
        int aantal = 11;
        String par = getParameter("aantal");
        System.out.println(par);
        if(par != null) {
            aantal = Integer.parseInt(par);
        }
        
        this.setSize(800, 600);
        this.setLayout(null);
       // ToolTipManager ttm = new ToolTipManager(this);
                
        
        Hashtable ht = new Hashtable();
        

        Panel scrollPanel = new AutoScrollPanel(null);
        scrollPanel.setSize(800, 600);
        scrollPanel.setLocation(0, 0);
        this.add(scrollPanel);
        
        Parameter[] params = new Parameter[aantal];
        
        System.arraycopy(parameters, 0, params, 0, aantal);
        
        
        ParameterComponent pc = new MainParameterComponent(params, ht);
        pc.setLocation(0, 0);
        pc.addComponentListener(this);
        scrollPanel.add(pc);
        
        pc.validate();
        setBackground(GuiConstants.MAIN_BACKGROUND);
//        this.add(pc);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
     */
    @Override
    public void componentHidden(ComponentEvent e) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
     */
    @Override
    public void componentMoved(ComponentEvent e) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    @Override
    public void componentResized(ComponentEvent e) {
//        validate();
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
     */
    @Override
    public void componentShown(ComponentEvent e) {
        // TODO Auto-generated method stub
        
    }
    
    public static Color randomColor() {
        int r, g, b;
        r = (int) (Math.random() * 155 + 100);
        g = (int) (Math.random() * 155 + 100);
        b = (int) (Math.random() * 155 + 100);
        return new Color(r, g, b);
    }

    /* (non-Javadoc)
     * @see fi.beans.scorm.ScormAppletIF#getState()
     */
    @Override
    public String getState() {
        // TODO Auto-generated method stub
        return "";
    }

    /* (non-Javadoc)
     * @see fi.beans.scorm.ScormAppletIF#setState(java.lang.String)
     */
    @Override
    public void setState(String state) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see fi.beans.scorm.ScormAppletIF#stopSco()
     */
    @Override
    public void stopSco() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see fi.beans.scorm.ScormAppletIF#hasEditMode()
     */
    @Override
    public boolean hasEditMode() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see fi.beans.scorm.ScormAppletIF#getEditComponent(java.lang.String)
     */
    @Override
    public ScormEditComponentIF getEditComponent(Hashtable launchdata) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see fi.beans.scorm.ScormAppletIF#getParameters()
     */
    @Override
    public Parameter[] getEditableParameters() {
        return parameters;
    }
    
    @Override
    public Parameter[] getAllParameters() {
        return null;
    }

    /**
	De testsuite. 
     * @return 
    */
    public static junit.framework.Test suite()
    {
	return new junit.framework.TestSuite();
    }
}
