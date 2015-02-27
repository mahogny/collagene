package ladder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * One DNA ladder
 * 
 * @author Johan Henriksson
 *
 */
public class DNALadder
	{
	public String name;
	public TreeMap<Double,Double> sizes=new TreeMap<Double,Double>();

	
	public void add(double bp, double weight)
		{
		sizes.put(bp,weight);
		}
	
	
	public static DNALadder parse(String name) throws IOException
		{
		BufferedReader br=new BufferedReader(new InputStreamReader(DNALadder.class.getResourceAsStream(name)));
		DNALadder lad=parse(br);
		br.close();
		return lad;
		}
	
	/**
	 * 
	 * @param f
	 * @throws IOException
	 */
	public static DNALadder parse(BufferedReader br) throws IOException
		{
		DNALadder ladder=new DNALadder();

		ladder.name=br.readLine();
		String line;
		while((line=br.readLine())!=null)
			if(!line.startsWith("#") && !line.equals(""))
				{
				StringTokenizer stok=new StringTokenizer(line,"\t");
				double bp=Double.parseDouble(stok.nextToken());
				double w=1;
				if(stok.hasMoreTokens())
					w=Double.parseDouble(stok.nextToken());
				ladder.add(bp,w);
				}
		br.close();
		return ladder;
		}
	}
