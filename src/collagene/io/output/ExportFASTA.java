package collagene.io.output;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import collagene.io.SequenceExporter;
import collagene.seq.AnnotatedSequence;

/**
 * Exporter of FASTA files
 * 
 * @author Johan Henriksson
 *
 */
public class ExportFASTA implements SequenceExporter
	{

	@Override
	public boolean isType(File f)
		{
		return(f.getName().endsWith(".fa") || f.getName().endsWith(".fasta"));
		}

	@Override
	public void save(OutputStream is, List<AnnotatedSequence> list)
			throws IOException
		{
		PrintWriter pw=new PrintWriter(is);
		for(AnnotatedSequence s:list)
			{
			pw.println(">"+s.name);
			pw.println(s.getSequence());
			}
		pw.flush();
		}


	
	
	}
