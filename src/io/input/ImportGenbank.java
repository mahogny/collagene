package io.input;

import gui.colors.ColorSet;
import io.SequenceImporter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import seq.AnnotatedSequence;
import seq.SeqAnnotation;
import seq.SeqColor;

/**
 * Importer from the genbank format
 * 
 * SerialCloner has extensions: http://serialbasics.free.fr/Serial_Cloner-Download_files/Features%20ReadMe.pdf
 * ApE has extensions
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
		try
			{
			load(is);
			return true;
			}
		catch (Exception e)
			{
/*		e.printStackTrace();*/
			return false;
			}
		/*
		BufferedReader br=new BufferedReader(new InputStreamReader(is));
		String line=br.readLine();
		return line!=null && line.contains("LOCUS");*/
		}

	/**
	 * Load data
	 */
	public List<AnnotatedSequence> load(InputStream is) throws IOException
		{
		AnnotatedSequence seq=new AnnotatedSequence();
		BufferedReader br=new BufferedReader(new InputStreamReader(is));

		ColorSet colorset=ColorSet.colorset;
		int curcol=0;
		String line;
		line=br.readLine();
		upper: for(;;)
			{
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
						annot.from=Integer.parseInt(sfrom)-1;
						annot.to=Integer.parseInt(sto);
						
						curcol=(curcol+1)%colorset.size();
						annot.color=new SeqColor(colorset.get(curcol));

						
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
									annot.color.r=Integer.parseInt(c.substring(0,2), 16);
									annot.color.g=Integer.parseInt(c.substring(2,4), 16);
									annot.color.b=Integer.parseInt(c.substring(4,6), 16);
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
//						line=line.trim();
//						line=line.substring(line.indexOf(' ')).trim();
//						line=line.replace(" ", "");
						for(char c:line.toCharArray())
							if(" 0123456789".indexOf(c)==-1)
								sb.append(c);
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
