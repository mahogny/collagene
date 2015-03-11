package io.madgene;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import primer.Primer;
import seq.AnnotatedSequence;
import seq.Orientation;
import seq.SeqAnnotation;

/**
 * Save in native format - but maybe native should be genbank?
 * 
 * @author Johan Henriksson
 *
 */
public class ExportOneXML
	{
	public static void save(AnnotatedSequence seq, Element e) throws IOException
		{
		
		Element eSeq=new Element("sequence");
		eSeq.setAttribute("name",seq.name);
		eSeq.setAttribute("su",seq.getSequence());
		eSeq.setAttribute("sl",seq.getSequenceLower());
		eSeq.setAttribute("circular",""+seq.isCircular);
		eSeq.setAttribute("notes",""+seq.notes);
		e.addContent(eSeq);

		
		
		for(SeqAnnotation a:seq.annotations)
			{
			Element eAnnot=new Element("annotation");
			eAnnot.setAttribute("name",a.name);
			eAnnot.setAttribute("from",""+a.from);
			eAnnot.setAttribute("to",""+a.to);

			eAnnot.setAttribute("colr",""+a.color.r);
			eAnnot.setAttribute("colg",""+a.color.g);
			eAnnot.setAttribute("colb",""+a.color.b);

			eAnnot.setAttribute("orientation",orientationToString(a.orientation));
			
			e.addContent(eAnnot);
			}

		for(Primer p:seq.primers)
			{
			Element ePrimer=new Element("primer");
			ePrimer.setAttribute("name",p.name);
			ePrimer.setAttribute("target",""+p.targetPosition);
			ePrimer.setAttribute("sequence",""+p.sequence);
			ePrimer.setAttribute("orientation",orientationToString(p.orientation));

			e.addContent(ePrimer);
			}
		}
	
	private static String orientationToString(Orientation orientation) throws IOException
		{
		if(orientation==Orientation.FORWARD)
			return "fwd";
		else if(orientation==Orientation.REVERSE)
			return "rev";
		else if(orientation==Orientation.NOTORIENTED)
			return "none";
		else
			throw new IOException("!");
		}
	
	public static void save(File f, AnnotatedSequence seq) throws IOException
		{
		Element e=new Element("f");
		Document doc=new Document(e);

		save(seq,e);
		
		
		XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
    xmlOutputter.output(doc, new FileOutputStream(f));
		}
	}
