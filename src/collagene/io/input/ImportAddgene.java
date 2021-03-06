package collagene.io.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import collagene.gui.colors.ColorSet;
import collagene.seq.AnnotatedSequence;
import collagene.seq.Orientation;
import collagene.seq.SeqAnnotation;
import collagene.seq.SeqColor;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;

/**
 * 
 * https://github.com/addgene/giraffe  for detecting stuff in sequence
 * 
 * http://www.addgene.org/browse/sequence_vdb/6108/   for commercial sequences
 * 
 * http://www.addgene.org/50946/   here is a main thing
 * 
 * http://www.addgene.org/50946/sequences/#depositor-full    the sequence
 * 
 * http://www.addgene.org/browse/sequence/76659/    the map
 * really points to
 * http://www.addgene.org/browse/sequence/76659/giraffe-analyze/
 * 
 * this file holds an analysis:
 *                                     
 * http://www.addgene.org/giraffe/blat/8242cbc9678aec18c4b70f5e74d861fc4d4798b6/default
 * 
 * 
 * their renderer is here
 * https://github.com/addgene/giraffe/blob/master/src/django/giraffe/analyze/static/js/draw.js
 * 
 * BEST: if one just gives it a URL to the sequence. 
 * 
 * 
 * 
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class ImportAddgene
	{
	
	
	
	//For a gene, find deposited sequence names. Is there only ever one full?
	
	//Some kind of search function here too
	
	
	
	/**
	 * Download a page
	 */
	private static String downloadAsString(String url2) throws IOException
		{
		InputStream is = null;
		BufferedReader br;
		String line;

		try
			{
			URL url=new URL(url2);
			is = url.openStream(); // throws an IOException
			br = new BufferedReader(new InputStreamReader(is));

			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine())!=null)
				{
				sb.append(line+"\n");
				}
			return sb.toString();
			}
		catch (IOException ioe)
			{
			ioe.printStackTrace();
			}
		finally
			{
			try
				{
				if (is!=null)
					is.close();
				}
			catch (IOException ioe)
				{
				}
			}
		throw new IOException();
		}
	
	
	/**
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static AnnotatedSequence get(String url) throws IOException
		{
		ColorSet colorset=ColorSet.colorset;
		int curcol=0;
		
		//Get the title of the sequence from the first page.
		//ex. <title>Addgene pBABE GFP - Analyze Sequence</title>
		String htmlContent=downloadAsString(url);
		htmlContent=htmlContent.substring(htmlContent.indexOf("<title>")+"<title>Addgene ".length());
		System.out.println(htmlContent);
		String seqName=htmlContent.substring(0, htmlContent.indexOf(" - Analyze Sequence"));
		System.out.println(seqName);
		
		//Construct URL to giraffe database for the features
		if(!url.endsWith("/"))
			url=url+"/";
		String giraffeID;
		
		if(url.contains("sequence_vdb"))
			giraffeID=downloadAsString("http://www.addgene.org/browse/sequence/"+getGiraffeInt(url)+"/giraffe-analyze_vdb/");
		else
			giraffeID=downloadAsString(url+"giraffe-analyze/");
			
		giraffeID=giraffeID.substring(giraffeID.indexOf("init_giraffe_analyze(")+"init_giraffe_analyze(".length());
		giraffeID=giraffeID.substring(0,giraffeID.indexOf(','));
		giraffeID=giraffeID.replace("\"", "").trim();
		System.out.println(giraffeID);

		String featuresurl="http://www.addgene.org/giraffe/blat/"+giraffeID+"/default?sequence=1";
		System.out.println(featuresurl);
		String content=downloadAsString(featuresurl);
		//System.out.println(content);
		
		
		try
			{
			JSONArray fileRoot = (JSONArray) JSONValue.parseWithException(content);
			
			AnnotatedSequence seq=new AnnotatedSequence();

			
			int size=(Integer)fileRoot.get(0);
			
			String theseq=(String)fileRoot.get(2);
			if(theseq.length()!=size)
				{
				System.out.println("wtf length "+theseq.length()+"  vs  "+size);
				StringBuilder sb=new StringBuilder();
				for(int i=0;i<size;i++)
					sb.append("N");
				seq.setSequence(sb.toString());
				}
			else
				seq.setSequence(theseq.toUpperCase());
			
			
			JSONArray arr=(JSONArray)fileRoot.get(1);
			
			for(int i=0;i<arr.size();i++)
				{
				JSONObject ob=(JSONObject)arr.get(i);
				int typeid=(Integer)ob.get("type_id");
				//3 is primer
				
				if(!ob.containsKey("cut") && typeid!=3)
					{
					SeqAnnotation annot=new SeqAnnotation();
					annot.name=(String)ob.get("feature");
					annot.range.from=(Integer)ob.get("start");
					annot.range.to=(Integer)ob.get("end");
					annot.orientation=(Boolean)ob.get("clockwise") ? Orientation.FORWARD : Orientation.REVERSE;

					curcol=(curcol+1)%colorset.size();
					annot.color=new SeqColor(colorset.get(curcol));
					
					seq.addAnnotation(annot);
					}
				}
			seq.name=seqName;
			seq.isCircular=true;

			return seq;
			}
		catch (ParseException e)
			{
			e.printStackTrace();
			throw new IOException(e.getMessage());
			}
		
		}

	public static void main(String[] args)
		{
		 try
			{
//			get("http://www.addgene.org/browse/sequence/76659/");
			get("http://www.addgene.org/browse/sequence_vdb/6108/");
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}
		
		}
	

	public static Integer getGiraffeInt(String t)
		{
		if(t.startsWith("http://www.addgene.org/browse/sequence/") || t.startsWith("https://www.addgene.org/browse/sequence/") ||
				t.startsWith("http://www.addgene.org/browse/sequence_vdb/6108/"))  // and a number"76659/"
			{
			String t2;
			if(t.contains("sequence_vdb"))
				t2=t.substring(t.indexOf("sequence_vdb/")+"sequence_vdb/".length());
			else
				t2=t.substring(t.indexOf("sequence/")+"sequence/".length());
			if(t2.length()>0)
				{
				if(t2.endsWith("/"))
					t2=t2.substring(0,t2.length()-1);
				return Integer.parseInt(t2);
				}
			}
		return null;
		}
	
	public static boolean isAddgeneUrl(String t)
		{
		try
			{
			return getGiraffeInt(t)!=null;
			}
		catch (Exception e)
			{
			return false;
			}
		/*
		if(t.startsWith("http://www.addgene.org/browse/sequence/") || t.startsWith("https://www.addgene.org/browse/sequence/") ||
				t.startsWith("http://www.addgene.org/browse/sequence_vdb/6108/"))  // and a number"76659/"
			{
			String t2;
			if(t.contains("sequence_vdb"))
				t2=t.substring(t.indexOf("sequence_vdb/")+"sequence_vdb/".length());
			else
				t2=t.substring(t.indexOf("sequence/")+"sequence/".length());
			if(t2.length()>0 && Character.isDigit(t2.charAt(0)))
				return true;
			}
		return false;
		*/
		}
	}
