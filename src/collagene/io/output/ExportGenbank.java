package collagene.io.output;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import collagene.io.SequenceExporter;
import collagene.seq.AnnotatedSequence;
import collagene.seq.SeqAnnotation;

/**
 * Exporter of Genbank files
 * 
 * http://www.insdc.org/files/feature_table.html
 * 
 * @author Johan Henriksson
 *
 */
public class ExportGenbank implements SequenceExporter
	{

	@Override
	public boolean isType(File f)
		{
		return(f.getName().endsWith(".gb"));
		}

	@Override
	public void save(OutputStream is, List<AnnotatedSequence> list)
			throws IOException
		{
		PrintWriter pw=new PrintWriter(is);
		
		AnnotatedSequence seq=list.get(0);
		
		//Locus line
		pw.print("LOCUS       "+pad(seq.name,24)+" "+seq.getLength()+" bp    DNA   ");
		if(seq.isCircular)
			pw.print("circular");
		else
			pw.print("linear");
		pw.println();

		//Features
		pw.println("FEATURES             Location/Qualifiers");
		for(SeqAnnotation a:seq.annotations)
			{
			pw.println("     misc_feature    "+(a.getFrom()+1)+".."+(a.getTo()));
			pw.println("                     /label=\""+a.name+"\"");
			pw.println("                     /ApEinfo_revcolor=#"+a.color.getColorAsRGBstring());
			pw.println("                     /ApEinfo_fwdcolor=#"+a.color.getColorAsRGBstring());
			}
		
		//The sequence
		pw.println("ORIGIN");
		int colsize=10;
		int perline=60;
		for(int start=0;start<seq.getLength();start+=perline)
			{
			pw.print("          ");  //TODO position, formatted
			for(int j=0;j<perline;j+=colsize)
				{
				int s1=Math.min(seq.getLength(), start+j);
				int end=Math.min(seq.getLength(), start+j+colsize);
				pw.print(seq.getSequence().substring(s1,end));
				pw.print(" ");
				}
			pw.println();
			}
		pw.println("//");
		pw.flush();
		}

	
	private static String pad(String s, int n)
		{
		StringBuilder sb=new StringBuilder();
		sb.append(s);
		for(int i=s.length();i<n;i++)
			sb.append(" ");
		return sb.toString();
		}

	
	
	}
