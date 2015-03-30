package collagene.io.collagene;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import collagene.gui.CollageneProject;
import collagene.seq.AnnotatedSequence;

/**
 * 
 * The native XML format for storing a project
 * 
 * @author Johan Henriksson
 *
 */
public class CollageneXML
	{

	
	
	
	/**
	 * Save XML data
	 */
	public static void saveProject(File f, CollageneProject proj) throws IOException
		{
		Element e=new Element("madgene");
		Document doc=new Document(e);
	
		for(AnnotatedSequence s:proj.sequenceLinkedList)
			{
			Element ee=new Element("oneseq");
			e.addContent(ee);
			ExportOneXML.save(s,ee);
			}
		
		XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
    xmlOutputter.output(doc, new FileOutputStream(f));
		}
	
	public static CollageneProject loadProject(File f) throws IOException
		{
		CollageneProject proj;
		FileInputStream is=new FileInputStream(f);
		proj=loadProject(is);
		is.close();
		proj.currentProjectFile=f;
		return proj;
		}
	/**
	 * Load XML data
	 */
	public static CollageneProject loadProject(InputStream is) throws IOException
		{
		try
			{
			CollageneProject proj=new CollageneProject();

			SAXBuilder sax = new SAXBuilder();
			Document doc = sax.build(is);
			
			for(Element e:doc.getRootElement().getChildren())
				{
				if(e.getName().equals("oneseq"))
					{
					AnnotatedSequence seq=ImportOneXML.loadOne(e);
					proj.sequenceLinkedList.add(seq);
					}
				}
			return proj;
			}
		catch (Exception e)
			{
			throw new IOException(e.getMessage());
			}
		}
	}
