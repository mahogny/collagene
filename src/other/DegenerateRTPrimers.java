package other;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import sequtil.DegenerateBases;


/**
 * Take a list of RT primers, and generate a new set using degenerate primers to use fewer oligos.
 * For not so random primers, this means a reduction of about 7%
 * 
 * @author Johan Henriksson
 *
 */
public class DegenerateRTPrimers
	{

	
	public static void dofile(File f) throws IOException
		{
		BufferedReader br=new BufferedReader(new FileReader(f));
		LinkedList<String> oligos=new LinkedList<String>();
		String line;
		while((line=br.readLine())!=null)
			oligos.add(line);
		br.close();
		
		
		//Find similar oligos by, for each string, put a . in a place, and count in a map
		HashMap<String, LinkedList<String>> cnt=new HashMap<String, LinkedList<String>>();
		for(String o:oligos)
			{
			for(int i=0;i<o.length();i++)
				{
				String no=o.substring(0,i)+"."+o.substring(i+1);
				LinkedList<String> prevcnt=cnt.get(no);
				if(prevcnt==null)
					prevcnt=new LinkedList<String>();
				cnt.put(no,prevcnt);

				prevcnt.add(o);
				}
			}
		
		//Generate candidate degenerate oligos
		HashMap<String, String> mapOligoToDegen=new HashMap<String, String>();
		for(String o:cnt.keySet())
			{
			int ind=o.indexOf(".");
			
			String usedletters="";
			for(String s:cnt.get(o))
				usedletters=usedletters+s.charAt(ind);
			
			String dego=o.substring(0,ind) + DegenerateBases.degenerateBaseFor(usedletters) + o.substring(ind+1);
			
			for(String s:cnt.get(o))
				mapOligoToDegen.put(s, dego);
			}
		
		//Invert map
		HashMap<String,HashSet<String>> mapDegenToOligo=new HashMap<String,HashSet<String>>();
		for(String s:mapOligoToDegen.keySet())
			{
			String dego=mapOligoToDegen.get(s);
			HashSet<String> p=mapDegenToOligo.get(dego);
			if(p==null)
				p=new HashSet<String>();
			mapDegenToOligo.put(dego, p);
			p.add(s);
			}
		
		PrintWriter pw=new PrintWriter(new File(f.getParent(),f.getName()+".degen"));
		
		pw.println("num degenerate oligos: "+mapDegenToOligo.size());
		
		for(int i=1;i<=4;i++)
			{
			pw.println("==== degeneracy "+i);
			for(String dego:mapDegenToOligo.keySet())
				if(mapDegenToOligo.get(dego).size()==i)
					{
					//Find the right degenerate base - some may have lost oligos during previous map inversion
					HashSet<String> theoligos=mapDegenToOligo.get(dego);
					String newdegen=null;
					if(theoligos.size()==1)
						newdegen=theoligos.iterator().next();
					else
						{
						//Find point of difference. There can only be one here
						Iterator<String> it=theoligos.iterator();
						String a=it.next();
						String b=it.next();
						
						int diffIndex;
						for(diffIndex=0;i<a.length()+1;diffIndex++)
							if(a.charAt(diffIndex)!=b.charAt(diffIndex))
								break;
						
						String letters="";
						for(String s:theoligos)
							letters=letters+s.charAt(diffIndex);

						newdegen=dego.substring(0,diffIndex)+DegenerateBases.degenerateBaseFor(letters)+dego.substring(diffIndex+1);
						}
					
					pw.println(newdegen);
					}
			}
		pw.close();
		}

	
	
	public static void main(String[] args)
		{
		try
			{
			dofile(new File("/home/mahogny/Dropbox/ebi/_my protocols/ddtseq/not so random primers/fs.txt"));
			dofile(new File("/home/mahogny/Dropbox/ebi/_my protocols/ddtseq/not so random primers/ss.txt"));
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}
		//only the last 6bp are relevant
		}
	
	
	}
