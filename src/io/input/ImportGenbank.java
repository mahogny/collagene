package io.input;

import io.SequenceImporter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import seq.AnnotatedSequence;
import seq.SeqAnnotation;

/**
 * Importer from the genbank format
 * 
 * @author Johan Henriksson
 *
 */
public class ImportGenbank implements SequenceImporter
	{

	private static String unquote(String s)
		{
		return s.substring(1,s.length()-1);
		}
	

	
	/**
	 * Check if data is in genbank format
	 */
	public boolean isType(InputStream is) throws IOException
		{
		BufferedReader br=new BufferedReader(new InputStreamReader(is));
		String line=br.readLine();
		return line!=null && line.contains("LOCUS");
		}

	/**
	 * Load data
	 */
	public List<AnnotatedSequence> load(InputStream is) throws IOException
		{
		AnnotatedSequence seq=new AnnotatedSequence();
		BufferedReader br=new BufferedReader(new InputStreamReader(is));
		
		String line;
		line=br.readLine();
		upper: for(;;)
			{
//			System.out.println(line);
			if(line.contains("LOCUS"))
				{
				seq.name=line.substring("LOCUS".length(),"LOCUS".length()+31).trim();
				line=line.toUpperCase();
				seq.isCircular=line.contains("CIRCULAR");
				}
			else if(line.contains("FEATURES"))
				{
				line=br.readLine();
				for(;;)
					{
					if(line.startsWith("     "))
						{
						line=line.trim();
						String fname=line.substring(0,line.indexOf(' '));
						System.out.println(fname);
						line=line.substring(line.indexOf(' ')).trim();
						
						SeqAnnotation annot=new SeqAnnotation();
						seq.annotations.add(annot);
						annot.name=fname;

						int doti=line.indexOf('.');
						String sfrom=line.substring(0,doti);
						String sto=line.substring(doti+2);
						//System.out.println(sfrom+"  "+sto);
						annot.from=Integer.parseInt(sfrom);
						annot.to=Integer.parseInt(sto);
						
						
						line=br.readLine();
						for(;;)
							{
							if(line.startsWith("                     /"))
								{
								line=line.trim().substring(1);
								if(line.startsWith("label="))
									{
									annot.name=unquote(line.substring("label=".length()));
									}
								else if(line.startsWith("ApEinfo_revcolor="))
									{
									String c=line.substring("ApEinfo_revcolor=#".length());
									annot.colorR=Integer.parseInt(c.substring(0,2), 16)/255.0;
									annot.colorG=Integer.parseInt(c.substring(2,4), 16)/255.0;
									annot.colorB=Integer.parseInt(c.substring(4,6), 16)/255.0;
									}
								
								System.out.println("++++ "+line);
								line=br.readLine();
								}
							else
								break;
							}
						}
					else
						continue upper;
					}
				}
			else if(line.contains("ORIGIN"))
				{
				//Here is the sequence
				StringBuilder sb=new StringBuilder();
				line=br.readLine();
				for(;;)
					{
					if(line.startsWith(" "))
						{
						line=line.trim();
						line=line.substring(line.indexOf(' ')).trim();
						line=line.replace(" ", "");
						sb.append(line);
						}
					else
						{
						seq.setSequence(sb.toString());
						break upper;
						}
					line=br.readLine();
					}
				}
			else if(line.equals("//"))
				break;
			line=br.readLine();
			}
		

		
		
		return Arrays.asList(seq);
		}
	}
