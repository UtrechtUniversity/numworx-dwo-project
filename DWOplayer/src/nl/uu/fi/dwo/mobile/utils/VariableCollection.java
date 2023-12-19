package nl.uu.fi.dwo.mobile.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import fi.wiskopdr.Letter;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

/**
 * 
 * copied from DWO
 * 
 */
public class VariableCollection
{
	Vector<Variable> variables;

	public VariableCollection()
	{
		variables = new Vector<>();
	}

	public boolean setVariables(String s)
	{
		//bv a1 wordt a?(1)
		String sNieuw = "";
		for (int i = 0; i < s.length() - 1; i++)
		{
			char char1 = s.charAt(i);
			char char2 = s.charAt(i + 1);
			if (Letter.isLetter(char1) && Character.isDigit(char2))
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
		boolean result = true;
		for (int i = 0; i < tokenizer.length; i++)
		{
			String tok = tokenizer[i];
			try
			{	int index = Math.max(tok.indexOf("="), tok.indexOf('~'));
				if (index > 0)
				{
					String name = tok.substring(0, index);
					if (Letter.isLetter(name.charAt(0)))
					{
						setVariable(tok);
					} else 
						result = false;
				} else
					result = false;
			}
			catch (Exception e)
			{
				return false;
			}
		}
		return result;
	}

	public void setVariable(String s)
	{
		s = s.trim();
		int index = Math.max(s.indexOf("="),s.indexOf('~'));
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
		var.setDraw(s.charAt(index)=='~');
		variables.addElement(var);
	}

	public Variable[] getVariables()
	{
		Variable[] vars = new Variable[variables.size()];
		for (int i = 0; i < variables.size(); i++)
		{
			vars[i] = variables.elementAt(i);
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
			names[i] = variables.elementAt(i).getName();
		}
		return names;
	}

	public HashMap getRandomValues()
	{
		HashMap<String, Integer> h = new HashMap<>();
		for (int i = 0; i < variables.size(); i++)
		{
			Variable var = variables.elementAt(i);
			String varName = var.getName();
			int[] values = var.getValues();
			int randNr = (int) (Math.random() * values.length);
			int value = values[randNr];
			var.draw(value);
			for (int j = i + 1; j < variables.size(); j++)
			{
				variables.elementAt(j).substitueer(value, varName);
			}
			h.put(varName, new Integer(value));
		}
		return h;
	}
	
	public Map<String, Collection<Integer>> getState() {
		Map<String, Collection<Integer>> state = new HashMap<>();		
		for(Variable var: variables) {
			if (var.isDraw()) {
				state.put(var.getName(), new ArrayList<>(var.drawSet));
			}
		}
		if (state.isEmpty()) return null;
		return state;
	}
	
	public void setState(ObjectMap state) {
		if (state == null) return;
		Set<String> names = state.keySet();
		for (String name : names) {
			Variable var = getVariable(name);
			if (var.isDraw()) {
				List<Integer> r = state.getIntegerList(name);
				var.drawSet.clear();
				var.drawSet.addAll(r);
			}
		}
	}
}