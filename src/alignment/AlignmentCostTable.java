package alignment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * 
 * Costs for alignment
 * 
 * @author Johan Henriksson
 *
 */
public class AlignmentCostTable
	{
	public double cost[][];
	public String letters;

	public AlignmentCostTable()
		{
		setDNA(5, -2);
		}
	
	public AlignmentCostTable(InputStream is) throws IOException
		{
		readFrom(is);
		}

	/**
	 * Turn characters into indices
	 */
	public int[] indexOfChars(String s)
		{
		int[] arr=new int[s.length()];
		for(int i=0;i<s.length();i++)
			{
			char c=s.charAt(i);
			arr[i]=letters.indexOf(c);
			if(arr[i]==-1)
				throw new RuntimeException("Does not exist in table: "+c);
			}
		return arr;
		}
	
	/**
	 * Read matrix from stream
	 */
	public void readFrom(InputStream is) throws IOException
		{
		BufferedReader br=new BufferedReader(new InputStreamReader(is));
		String line=nextLine(br).trim();
		
		LinkedList<String> letters=new LinkedList<String>();
		StringTokenizer tok=new StringTokenizer(line," ");
		while(tok.hasMoreTokens())
			letters.add(tok.nextToken());
		this.letters="";
		for(String s:letters)
			this.letters=this.letters+s;
		allocate(this.letters);
		for(int i=0;i<letters.size();i++)
			{
			line=nextLine(br);
			tok=new StringTokenizer(line," ");
			tok.nextToken(); //Skip first element, the row name. Assuming same order as columns
			for(int j=0;j<letters.size();j++)
				cost[i][j]=Double.parseDouble(tok.nextToken());
			}
		br.close();
		}
	private static String nextLine(BufferedReader br) throws IOException
		{
		for(;;)
			{
			String line=br.readLine();
			if(line==null)
				return null;
			if(!line.startsWith("#"))
				return line;
			}
		}
	
	private void allocate(String letters)
		{
		this.letters=letters;
		cost=new double[letters.length()][letters.length()];
		}

	
	private void setDNA(double match, double mismatch)
		{
		allocate("ATCG");
		for(int i=0;i<3;i++)
			for(int j=0;j<3;j++)
				cost[i][j]=mismatch;
		for(int i=0;i<3;i++)
			cost[i][i]=match;
		}
	
	}
