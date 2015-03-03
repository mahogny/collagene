package gui.colors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import seq.SeqColor;

/**
 * 
 * Set of colors
 * 
 * @author Johan Henriksson
 *
 */
public class ColorSet
	{
	public LinkedList<SeqColor> colors=new LinkedList<SeqColor>();
	
	public static ColorSet colorset=new ColorSet();
	
	static
		{
		try
			{
			colorset.parseStandardColors();
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}
		}

	
	
	public void parseStandardColors() throws IOException
		{
		BufferedReader br=new BufferedReader(new InputStreamReader(ColorSet.class.getResourceAsStream("commoncolors.txt")));

		String line;
		while((line=br.readLine())!=null)
			{
			SeqColor c=new SeqColor();
			c.r=Integer.parseInt(line.substring(1,1+2),16);
			c.g=Integer.parseInt(line.substring(3,3+2),16);
			c.b=Integer.parseInt(line.substring(5,5+2),16);
			colors.add(c);
			}
		
		br.close();
		}



	public SeqColor get(int curcol)
		{
		return colors.get(curcol);
		}



	public int size()
		{
		return colors.size();
		}
	
	}
