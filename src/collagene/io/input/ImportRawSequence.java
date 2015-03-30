package collagene.io.input;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import collagene.io.SequenceImporter;
import collagene.seq.AnnotatedSequence;
import collagene.sequtil.NucleotideUtil;


/**
 * Reader of raw sequence strings
 * 
 * @author Johan Henriksson
 */
public class ImportRawSequence implements SequenceImporter
	{
	/**
	 * Load data from stream
	 */
	public List<AnnotatedSequence> load(InputStream is) throws IOException
		{
		BufferedReader input = new BufferedReader( new InputStreamReader(is));

		StringBuilder sb=new StringBuilder();
		String line = null;
		while (( line = input.readLine()) != null)
			{
			line=line.toUpperCase();
			for(char c:line.toCharArray())
				if(!Character.isWhitespace(c) && !Character.isDigit(c))
					{
					if(NucleotideUtil.isValidNucLetter(c))
						sb.append(c);
					else
						throw new IOException("Invalid nucleotide "+c);
					}
				}
		AnnotatedSequence a=new AnnotatedSequence();
		a.setSequence(sb.toString());
		a.name="unnamed";
		return Arrays.asList(a);
		}


	/**
	 * Check if data is FASTA
	 */
	public boolean isType(InputStream is) throws IOException
		{
		try
			{
			load(is);
			return true;
			}
		catch (Exception e)
			{
			System.out.println("not raw sequence");
			//e.printStackTrace();
			return false;
			}
		}


	
	
	}
