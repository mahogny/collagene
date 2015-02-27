package io.madgene;

import gui.MadgeneProject;

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

import seq.AnnotatedSequence;

/**
 * 
 * The native XML format for storing a project
 * 
 * @author Johan Henriksson
 *
 */
public class MadgeneXML
	{

	
	
	
	/**
	 * Save XML data
	 */
	public static void saveProject(File f, MadgeneProject proj) throws IOException
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
	
	public static MadgeneProject loadProject(File f) throws IOException
		{
		MadgeneProject proj;
		FileInputStream is=new FileInputStream(f);
		proj=loadProject(is);
		is.close();
		return proj;
		}
	/**
	 * Load XML data
	 */
	public static MadgeneProject loadProject(InputStream is) throws IOException
		{
		try
			{
			MadgeneProject proj=new MadgeneProject();

			SAXBuilder sax = new SAXBuilder();
			Document doc = sax.build(is);
			
			for(Element e:doc.getRootElement().getChildren())
				{
				if(e.getName().equals(""))
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
