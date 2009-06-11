package fi.beans.tekstobjects;

import java.util.*;

public class TekstBuffer
{
	private String tekst;
	private TekstVak tekstVak; 
	
	public TekstBuffer(TekstVak tv, String completeString)
	{	tekstVak = tv; 
		tekst = completeString+'\n';
	}
	
	public char charAt(int pos)
	{	return tekst.charAt(pos);
	}
	
	public void insert(int index, char c)
	{	tekst = ""+tekst.substring(0,index)+ c + tekst.substring(index);
	}
	
	public void insert(int index, String s)
	{	tekst = ""+tekst.substring(0,index)+ s + tekst.substring(index);
	}
	
	public String insertAndComplete(int index, String s)
	{	String newString = getSelection(0,index-1) + s + getSelection(index,tekst.length());
		return newString;
	}
	
	public void replace(int index, char c)
	{	deleteCharAt(index);
		insert(index,c);
	}
	
	public void deleteCharAt(int index)
	{	if(index>tekst.length()-2)return;
		tekst = tekst.substring(0,index) + tekst.substring(index+1);
	}
	
	public String toString()
	{	return tekst;
	}
	
	public String toCompleteString()
	{	String completeString;
		completeString = new String(tekst);
		return completeString;
	}
	
	public int length()
	{	return tekst.length();
	}
	
	public void delete(int firstIndex, int lastIndex)
	{	if(lastIndex>tekst.length()-2)lastIndex = tekst.length()-2;
		for(int i=0 ; i<lastIndex-firstIndex+1; i++)
		{	deleteCharAt(firstIndex);
		}
	}
	
	public String getSelection(int firstIndex, int lastIndex)
	{	String s = "";
		for(int i=0 ; i<tekst.length(); i++)
		{	if(i>=firstIndex && i<=lastIndex)
			{	s = s + tekst.charAt(i);
			}
		}
		return s;
	}
}
