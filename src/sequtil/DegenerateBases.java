package sequtil;

import java.util.HashMap;

/**
 * 
 * @author Johan Henriksson
 *
 */
public class DegenerateBases
	{
	private static HashMap<String, String> map=new HashMap<String, String>();
	static
		{
		map.put("A","A");
		map.put("T","T");
		map.put("C","C");
		map.put("G","G");

		map.put("R","AG");
		map.put("Y","CT");
		map.put("M","AC");
		map.put("K","GT");
		map.put("S","CG");
		map.put("W","AT");
		map.put("B","CGT");
		map.put("D","AGT");
		map.put("H","ACT");
		map.put("V","ACG");
		map.put("N","ACGT");
//		map.put("","");
		//I inosin
		}

	public static String getLettersFor(String s)
		{
		return map.get(s);
		}
	}
