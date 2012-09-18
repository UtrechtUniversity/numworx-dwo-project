package nl.uu.fi.dwo.mobile.utils;

import java.util.HashMap;
import java.util.Vector;

/**
 * 
 * copied from DWO
 * 
 */
public class VariableCollection
{
	Vector variables;

	public VariableCollection()
	{
		variables = new Vector();
	}

	public boolean setVariables(String s)
	{
		//bv a1 wordt a?(1)
		String sNieuw = "";
		for (int i = 0; i < s.length() - 1; i++)
		{
			char char1 = s.charAt(i);
			char char2 = s.charAt(i + 1);
			if (Character.isLetter(char1) && Character.isDigit(char2))
			{
				sNieuw = sNieuw + char1 + "?(" + char2 + ")";
				i++;
			}
			else if (i == s.length() - 2)
				sNieuw = sNieuw + char1 + char2;
			else
				sNieuw = sNieuw + char1;
			//System.out.println(sNieuw);
		}
		s = sNieuw;

		String[] tokenizer = s.split("[;\n]+");
		for (int i = 0; i < tokenizer.length; i++)
		{
			String tok = tokenizer[i];
			try
			{
				int index = s.indexOf("=");
				if (index > 0)
				{
					String name = s.substring(0, index);
					if (Character.isLetter(name.charAt(0)))
					{
						setVariable(tok);
					}
					else
						return true;
				}
				else
					return false;
			}
			catch (Exception e)
			{
				return false;
			}
		}
		return true;
	}

	public void setVariable(String s)
	{
		s = s.trim();
		int index = s.indexOf("=");
		String name = s.substring(0, index);
		name = name.trim();
		//String[] nameParts = StringUtils.split(name, "_");
		//if(nameParts.length==2) name = nameParts[0] + "?(" + nameParts[1] + ")";

		for (int i = 0; i < variables.size(); i++)
		{
			Variable v = (Variable) variables.elementAt(i);
			if (v.getName().equals(name))
			{
				variables.removeElementAt(i);
				break;
			}
		}
		String valueString = s.substring(index + 1);
		valueString = valueString.trim();
		Variable var = new Variable(name);
		var.setValues(valueString);
		variables.addElement(var);
	}

	public Variable[] getVariables()
	{
		Variable[] vars = new Variable[variables.size()];
		for (int i = 0; i < variables.size(); i++)
		{
			vars[i] = (Variable) variables.elementAt(i);
		}
		return vars;
	}

	public Variable getVariable(String name)
	{
		Variable[] vars = getVariables();
		for (int i = 0; i < vars.length; i++)
		{
			if (vars[i].getName().equals(name))
			{
				return vars[i];
			}
		}
		return null;
	}

	public String[] getVariableNames()
	{
		String[] names = new String[variables.size()];
		for (int i = 0; i < variables.size(); i++)
		{
			names[i] = ((Variable) variables.elementAt(i)).getName();
		}
		return names;
	}

	public HashMap getRandomValues()
	{
		HashMap h = new HashMap();
		for (int i = 0; i < variables.size(); i++)
		{
			Variable var = (Variable) variables.elementAt(i);
			String varName = var.getName();
			int[] values = var.getValues();
			int randNr = (int) (Math.random() * values.length);
			int value = values[randNr];
			for (int j = i + 1; j < variables.size(); j++)
			{
				((Variable) variables.elementAt(j)).substitueer(value, varName);
			}
			h.put(varName, new Integer(value));
		}
		return h;
	}
}
