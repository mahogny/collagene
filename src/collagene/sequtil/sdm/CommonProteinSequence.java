package collagene.sequtil.sdm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

/**
 * 
 * Common protein sequences
 * 
 * @author Johan Henriksson
 *
 */
public class CommonProteinSequence
	{
	public String name;
	public String protseq;
	public String dnaseq;
	
	public CommonProteinSequence(String name, String protseq, String dnaseq)
		{
		this.name = name;
		this.protseq = protseq;
		this.dnaseq = dnaseq;
		}

	public static LinkedList<CommonProteinSequence> get()
		{
		LinkedList<CommonProteinSequence> list=new LinkedList<CommonProteinSequence>();
		try
			{
			BufferedReader r=new BufferedReader(new InputStreamReader(CommonProteinSequence.class.getResourceAsStream("commonseqs.txt")));
			String line;
			while((line=r.readLine())!=null)
				{
				String name=r.readLine();
				String prot=r.readLine();
				r.readLine();
				list.add(new CommonProteinSequence(name,
						prot,
						line));
				
				}
			r.close();
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}
		return list;
		}
	}