package collagene.io.collagene;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import collagene.io.SequenceImporter;
import collagene.primer.Primer;
import collagene.seq.AnnotatedSequence;
import collagene.seq.Orientation;
import collagene.seq.SeqAnnotation;

/**
 * Load from native format
 * 
 * @author Johan Henriksson
 *
 */
public class ImportOneXML implements SequenceImporter
	{
	/**
	 * Check if this file format
	 */
	public boolean isType(InputStream is) throws IOException
		{
		try
			{
			SAXBuilder sax = new SAXBuilder();
			Document doc = sax.build(is);
			return doc.getRootElement().getName().equals("sequence");
			}
		catch (JDOMException e)
			{
			return false;
			}
		}

	

	public static AnnotatedSequence loadOne(Element root) throws IOException, DataConversionException
		{
	  AnnotatedSequence seq = new AnnotatedSequence();
		
		for(Element e:root.getChildren())
			{
			if(e.getName().equals("sequence"))
				{
				seq.name=e.getAttributeValue("name");
				seq.isCircular=Boolean.parseBoolean(e.getAttributeValue("circular"));
				seq.setSequence(
						e.getAttributeValue("su"),
						e.getAttributeValue("sl"));
				}
			else if(e.getName().equals("annotation"))
				{
				SeqAnnotation annot=new SeqAnnotation();
				seq.annotations.add(annot);
				
				annot.name=e.getAttributeValue("name");
				annot.range.from=e.getAttribute("from").getIntValue();
				annot.range.to=e.getAttribute("to").getIntValue();

				annot.color.r=e.getAttribute("colr").getIntValue();
				annot.color.g=e.getAttribute("colg").getIntValue();
				annot.color.b=e.getAttribute("colb").getIntValue();
				
				annot.orientation=stringToOrientation(e.getAttributeValue("orientation"));
				}
			else if(e.getName().equals("primer"))
				{
				Primer p=new Primer();
				seq.addPrimer(p);
				
				p.name=e.getAttributeValue("name");
				p.sequence=e.getAttributeValue("sequence");
				p.targetPosition=e.getAttribute("target").getIntValue();

				p.orientation=stringToOrientation(e.getAttributeValue("orientation"));
				}
			
			}
		return seq;
		}
	
	private static Orientation stringToOrientation(String o) throws IOException
		{
		if(o.equals("fwd"))
			return Orientation.FORWARD;
		else if(o.equals("rev"))
			return Orientation.REVERSE;
		else if(o.equals("none"))
			return Orientation.NOTORIENTED;
		else
			throw new IOException("orientation!");
		}
	
	/**
	 * Load XML data
	 */
	public List<AnnotatedSequence> load(InputStream is) throws IOException
		{
		try
			{
			SAXBuilder sax = new SAXBuilder();
			Document doc = sax.build(is);
			AnnotatedSequence seq=loadOne(doc.getRootElement());
		  return Arrays.asList(seq);
			}
		catch (Exception e)
			{
			throw new IOException(e.getMessage());
			}
		}

	}
