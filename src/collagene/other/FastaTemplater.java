package collagene.other;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.TreeMap;

import collagene.io.input.ImportFASTA;
import collagene.io.output.ExportFASTA;
import collagene.seq.AnnotatedSequence;
import collagene.sequtil.NucleotideUtil;

/**
 * 
 * Take a FASTA file with <>-regions, and insert templates
 * 
 * @author Johan Henriksson
 *
 */
public class FastaTemplater
	{
	private TreeMap<String, String> mapNameSeq=new TreeMap<String, String>();
	private TreeMap<String, String> mapNameSeqUPR=new TreeMap<String, String>();
	
	
	
	public static void main(String[] args)
		{
		File f=new File("/home/mahogny/Dropbox/ebi/_my protocols/droplets/oligodesign.fa");
		File fo=new File("/home/mahogny/Dropbox/ebi/_my protocols/droplets/oligodesign.fa.processed");
		
		try
			{
			FastaTemplater t=new FastaTemplater();
			t.addAll(ImportFASTA.load(f));
			t.process();
			t.out(fo);
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}
		}


	/**
	 * Output to file
	 */
	private void out(File fo) throws IOException
		{
		LinkedList<AnnotatedSequence> seqs=new LinkedList<AnnotatedSequence>();
		for(String n:mapNameSeq.keySet())
			{
			System.out.println(n);
			AnnotatedSequence seq=new AnnotatedSequence();
			seq.setSequence(mapNameSeq.get(n));
			seq.name=n;
			seqs.add(seq);
			}
		
		FileOutputStream os=new FileOutputStream(fo);
		new ExportFASTA().save(os, seqs);
		
		os.close();
		}


	/**
	 * Process the templates
	 */
	private void process()
		{
		for(String n:new LinkedList<String>(mapNameSeq.keySet()))
			{
			String seq=mapNameSeq.get(n);
			for(;;)
				{
				int ind=seq.indexOf("<");
				if(ind==-1)
					break;
				else
					{
					int ind2=seq.indexOf(">",ind);
					String sub=seq.substring(ind+1,ind2);
	//				System.out.println(sub);
					
					String repl;
					if(sub.startsWith("R:"))
						repl=NucleotideUtil.reverse(mapNameSeqUPR.get(sub.substring(2)));
					else if(sub.startsWith("RC:"))
							repl=NucleotideUtil.revcomplement(mapNameSeqUPR.get(sub.substring(3)));
					else if(sub.startsWith("C:"))
						repl=NucleotideUtil.complement(mapNameSeqUPR.get(sub.substring(2)));
					else 
						repl=mapNameSeqUPR.get(sub);
						
					seq=seq.substring(0,ind) + repl + seq.substring(ind2+1);
					}
				}
			mapNameSeq.put(n, seq);
			}
		}


	
	private void addAll(LinkedList<AnnotatedSequence> load)
		{
		for(AnnotatedSequence seq:load)
			{
			mapNameSeq.put(seq.name, seq.getSequence());
			mapNameSeqUPR.put(seq.name.toUpperCase(), seq.getSequence());
			}
		}
	}
