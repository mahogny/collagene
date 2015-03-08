package io.madgene;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

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
		eSeq.setAttribute("su",seq.getSequence());
		eSeq.setAttribute("su",seq.getSequenceLower());
		eSeq.setAttribute("circular",""+seq.isCircular);
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

			if(a.orientation==Orientation.FORWARD)
				eAnnot.setAttribute("orientation","fwd");
			else if(a.orientation==Orientation.REVERSE)
				eAnnot.setAttribute("orientation","rev");
			else if(a.orientation==Orientation.NOTORIENTED)
				eAnnot.setAttribute("orientation","none");
			else
				throw new IOException("!");
			
			e.addContent(eAnnot);
			}

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
