package io.input;

import io.SequenceImporter;

import java.io.*;
import java.util.LinkedList;

import seq.AnnotatedSequence;


/**
 * Reader of FASTA files, without indexing them
 * 
 * @author Johan Henriksson
 */
public class ImportFASTA implements SequenceImporter
	{
	public static LinkedList<AnnotatedSequence> load(File infile) throws IOException
		{
		Reader input = new FileReader(infile);
		LinkedList<AnnotatedSequence> list=read(input);
		input.close();
		return list;
		}
	
	
	/**
	 * 
	 * @param input2
	 * @return
	 * @throws IOException
	 */
	public static LinkedList<AnnotatedSequence> read(Reader input2) throws IOException
		{
		BufferedReader input = new BufferedReader( input2 );

		LinkedList<AnnotatedSequence> seqs=new LinkedList<AnnotatedSequence>();
		
		StringBuffer out = null;
		String line = null;
		String curName = null;
		while (( line = input.readLine()) != null)
			{
			if(line.startsWith(">"))
				{
				if(out!=null)
					{
					AnnotatedSequence a=new AnnotatedSequence();
					a.name=curName;
					a.setSequence(out.toString());
					seqs.add(a);
					}
				out = new StringBuffer();
				curName=line.substring(1);
				}
			else
				out.append(line.replace(" ", "").replace("\t", ""));
			}
		if(curName!=null)
			{
			AnnotatedSequence a=new AnnotatedSequence();
			a.name=curName;
			a.setSequence(out.toString());
			seqs.add(a);
			}
		return seqs;
		}


	/**
	 * Check if data is FASTA
	 */
	public boolean isType(InputStream is) throws IOException
		{
		BufferedReader br=new BufferedReader(new InputStreamReader(is));
		String line=br.readLine();
		return line!=null && line.startsWith(">");
		}


	/**
	 * Load data from stream
	 */
	@Override
	public LinkedList<AnnotatedSequence> load(InputStream is) throws IOException
		{
		return read(new InputStreamReader(is));
		}
	
	
	}
