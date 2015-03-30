package collagene.sequtil;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * 
 * @author Johan Henriksson
 *
 */
public class DegenerateBases
	{
	private static HashMap<String, String> mapToLetters=new HashMap<String, String>();
	private static HashMap<String, String> mapToDegen=new HashMap<String, String>();
	static
		{
		put("A","A");
		put("T","T");
		put("C","C");
		put("G","G");

		put("R","AG");
		put("Y","CT");
		put("M","AC");
		put("K","GT");
		put("S","CG");
		put("W","AT");
		put("B","CGT");
		put("D","AGT");
		put("H","ACT");
		put("V","ACG");
		put("N","ACGT");
		//I inosin
		}

	public static void put(String degen, String letters)
		{
		mapToLetters.put(degen,letters);
		
		LinkedList<String> list=new LinkedList<String>();
		permutate(list, "", letters);
		for(String s:list)
			mapToDegen.put(s, degen);
		}
	
	public static String getLettersFor(String s)
		{
		return mapToLetters.get(s);
		}
	
	
	private static void permutate(LinkedList<String> list, String pre, String sleft)
		{
		if(sleft.length()==0)
			list.add(pre);
		else
			{
			for(int i=0;i<sleft.length();i++)
				permutate(list, pre+sleft.charAt(i), sleft.substring(0,i)+sleft.substring(i+1));
			}
		}
	
	
	public static String degenerateBaseFor(String s)
		{
		return mapToDegen.get(s);
		}
	}
