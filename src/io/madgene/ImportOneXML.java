package io.madgene;

import io.SequenceImporter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import seq.AnnotatedSequence;
import seq.Orientation;
import seq.SeqAnnotation;

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
				annot.from=e.getAttribute("from").getIntValue();
				annot.to=e.getAttribute("to").getIntValue();

				annot.col.r=e.getAttribute("colr").getIntValue();
				annot.col.g=e.getAttribute("colg").getIntValue();
				annot.col.b=e.getAttribute("colb").getIntValue();
				
				String o=e.getAttributeValue("orientation");
				if(o.equals("fwd"))
					annot.orientation=Orientation.FORWARD;
				else if(o.equals("rev"))
					annot.orientation=Orientation.REVERSE;
				else if(o.equals("none"))
					annot.orientation=Orientation.NOTORIENTED;
				else
					throw new IOException("orientation!");
				}
			
			}
		return seq;
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
